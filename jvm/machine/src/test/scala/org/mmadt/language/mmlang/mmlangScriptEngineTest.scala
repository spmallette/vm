/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 *  mm-ADT is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU Affero General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 *  License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 *  You can be released from the requirements of the license by purchasing a
 *  commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language.mmlang

import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.model.Model
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Obj, Str}
import org.mmadt.language.{LanguageFactory, Tokens}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmlangScriptEngineTest extends FunSuite {

  lazy val engine:mmADTScriptEngine = LanguageFactory.getLanguage("mmlang").getEngine.get()

  test("empty space parsing"){

    assert(!engine.eval("").alive())
    assert(!engine.eval("    ").alive())
    assert(!engine.eval("  \n  ").alive())
    assert(!engine.eval("\t  \n  ").alive())
  }

  test("canonical type parsing"){
    assertResult(bool)(engine.eval("bool"))
    assertResult(int)(engine.eval("int"))
    assertResult(real)(engine.eval("real"))
    assertResult(str)(engine.eval("str"))
    assertResult(rec)(engine.eval("rec[]"))
    assertResult(rec)(engine.eval("rec"))
  }

  test("quantified canonical type parsing"){
    assertResult(bool.q(int(2)))(engine.eval("bool{2}"))
    assertResult(int.q(int(0),int(1)))(engine.eval("int{?}"))
    assertResult(real.q(int(0),int(0)))(engine.eval("real{0}"))
    assertResult(str)(engine.eval("str{1}"))
    assertResult(rec.q(int(5),int(10)))(engine.eval("rec[]{5,10}"))
    assertResult(rec.q(int(5),int(10)))(engine.eval("rec{5,10}"))
  }

  test("atomic value parsing"){
    assertResult(btrue)(engine.eval("true"))
    assertResult(bfalse)(engine.eval("false"))
    assertResult(int(5))(engine.eval("5"))
    assertResult(int(-51))(engine.eval("-51"))
    assertResult(real(5.0))(engine.eval("5.0"))
    assertResult(real(0.0))(engine.eval("0.0"))
    assertResult(real(1.0))(engine.eval("1.0"))
    assertResult(real(-51.2))(engine.eval("-51.2"))
    assertResult(real(0.21))(engine.eval("0.21"))
    assertResult(str("marko"))(engine.eval("'marko'"))
    assertResult(str("marko comp3 45AHA\"\"\\'-%^&"))(engine.eval("'marko comp3 45AHA\"\"\\'-%^&'"))
  }

  test("atomic named value parsing"){
    assertResult(vbool(name = "keep",value = true))(engine.eval("keep:true"))
    assertResult(vint(name = "nat",value = 5))(engine.eval("nat:5"))
    assertResult(vint(name = "score",value = -51))(engine.eval("score:-51"))
    assertResult(vstr(name = "fname",value = "marko"))(engine.eval("fname:'marko'"))
    assertResult(vstr(name = "garbage",value = "marko comp3 45AHA\"\"\\'-%^&"))(engine.eval("garbage:'marko comp3 45AHA\"\"\\'-%^&'"))
  }

  test("rec value parsing"){
    assertResult(rec(str("name") -> str("marko")))(engine.eval("['name'->'marko']"))
    assertResult(rec(str("name") -> str("marko"),str("age") -> int(29)))(engine.eval("['name'->'marko','age'->29]"))
    assertResult(rec(str("name") -> str("marko"),str("age") -> int(29)))(engine.eval("['name'->  'marko' , 'age' ->29]"))
  }

  test("rec type parsing"){
    assertResult(trec(str("name") -> str,str("age") -> int))(engine.eval("rec[   'name'   ->str ,   'age' ->int]"))
    assertResult(trec(str("name") -> str,str("age") -> int))(engine.eval("rec['name'->str,'age'->int]"))
    assertResult(trec(str("name") -> str,str("age") -> int).q(30))(engine.eval("rec['name'->str,'age'->int]{30}"))
    assertResult(bool.q(30) <= trec(str("name") -> str,str("age") -> int).q(30).get("age",int).gt(30))(engine.eval("rec['name'->str,'age'->int]{30}[get,'age'][gt,30]"))
    assertResult(bool.q(30) <= trec(str("name") -> str,str("age") -> int).q(30).get("age",int).gt(30))(engine.eval("bool{30}<=rec['name'->str,'age'->int]{30}[get,'age'][gt,30]"))
    assertResult(bool.q(*) <= trec(str("name") -> str,str("age") -> int).q(*).get("age",int).gt(30))(engine.eval("bool{*}<=rec['name'->str,'age'->int]{*}[get,'age'][gt,30]"))
  }

  test("rec named value parsing"){
    assertResult(vrec(name = "person",Map(str("name") -> str("marko"),str("age") -> int(29))))(engine.eval("person:[   'name'   ->'marko' ,   'age' ->29]"))
    assertResult(vrec(name = "person",Map(str("name") -> str("marko"),str("age") -> int(29))))(engine.eval("person:['name'->'marko','age'->29]"))
  }

  test("composite type get/put"){
    val person:RecType[StrValue,ObjType] = trec(str("name") -> str,str("age") -> int)
    assertResult(str <= person.get(str("name")))(engine.eval("str<=rec['name'->str,'age'->int][get,'name']"))
    assertResult(int <= person.get(str("age")))(engine.eval("int<=rec['name'->str,'age'->int][get,'age']"))
    assertResult(int <= rec.put(str("age"),int).get(str("age")))(engine.eval("rec[][put,'age',int][get,'age']"))
    assertResult(int <= rec.put(str("age"),int).get(str("age")).plus(int(10)))(engine.eval("rec[][put,'age',int][get,'age'][plus,10]"))
    assertResult(int <= rec.put(str("age"),int).get(str("age")).plus(int(10)))(engine.eval("int<=rec[][put,'age',int][get,'age'][plus,10]"))
    assertResult(int(20))(engine.eval("['name'->'marko'] rec[][put,'age',10][get,'age'][plus,10]"))
    assertResult(int(20))(engine.eval("['name'->'marko'] int<=rec[][put,'age',10][get,'age'][plus,10]"))
  }

  test("quantified value parsing"){
    assertResult(btrue.q(int(2)))(engine.eval("true{2}"))
    assertResult(bfalse)(engine.eval("false{1}"))
    assertResult(int(5).q(+))(engine.eval("5{+}"))
    assertResult(int(6).q(qZero))(engine.eval("6{0}"))
    assertResult(real(7.563).q(qZero))(engine.eval("7.563{0,0}"))
    assertResult(str("marko").q(int(10),int(100)))(engine.eval("'marko'{10,100}"))
    assertResult(int(20).q(int(10)))(engine.eval("13{10}[plus,7]"))
    assertResult(int(13).q(int(10)))(engine.eval("13{10}[is>5]"))
  }

  test("anonymous type"){
    assertResult(__.plus(1).mult(2))(engine.eval("[plus,1][mult,2]"))
    assertResult(__.plus(1).mult(__.plus(10)))(engine.eval("[plus,1][mult,[plus,10]]"))
    assertResult(__.plus(1.2).mult(__.plus(10.1)))(engine.eval("[plus,1.2][mult,[plus,10.1]]"))
    assertResult(int(75))(engine.eval("4[plus,1][mult,[plus,10]]"))
  }

  test("quantifier inst parsing"){
    assertResult(int.id().q(2).id().q(4))(engine.eval("int[id]{2}[id]{4}"))
    assertResult("int{8}<=int[id]{2}[id]{4}")(engine.eval("int[id]{2}[id]{4}").toString)
    assertResult(int(10).q(8))(engine.eval("10 int[id]{2}[id]{4}"))
    assertResult(int(10).q(8))(engine.eval("10[id]{2}[id]{4}"))
    assertResult("int{8}<=int[plus,10]{2}[id]{4}")(engine.eval("int[plus,10]{2}[id]{4}").toString)
    assertResult(int(15).q(8))(engine.eval("5[plus,10]{2}[id]{4}"))
    assertResult(int(17).q(8))(engine.eval("5[plus,10]{2}[id]{4}[plus,2]"))
    assertResult(int(17).q(16))(engine.eval("5{2}[plus,10]{2}[id]{4}[plus,2]"))
    assertResult(int.q(0,24))(engine.eval("int[plus,10]{2}[id]{4}[is,[gt,2]]{3}").asInstanceOf[IntType].range)
    assertResult(int(15).q(48))(engine.eval("5{2}[plus,10]{2}[id]{4}[is,[gt,2]]{3}"))
    // assertResult(true)(engine.eval("[plus,2]{2}[mult,3]{32}[plus,4]")()) // TODO: support instruction quantification (requires a full refactor of the inst obj model)
  }

  test("refinement type parsing"){
    assertResult(int.q(?) <= int.is(int.gt(int(10))))(engine.eval("int[is,int[gt,10]]"))
    //    assertResult(int <= int.is(int.gt(int(10))))(engine.eval("int<=int[is,int[gt,10]]")) //TODO: when a range is specified by the user, use that during compilation
    assertResult(int.q(?) <= int.is(int.gt(int(10))))(engine.eval("int[is>10]"))
  }

  test("as instruction parsing"){
    assertResult(int(1))(engine.eval("1[as,int]"))
    assertResult(str("1"))(engine.eval("1[as,str]"))
    assertResult(int(14))(engine.eval("'1'[plus,'4'][as,int]"))
    assertResult(int(16))(engine.eval("'1'[plus,'4'][as,int[plus,2]]"))
    assertResult(int(16))(engine.eval("'1'[plus,'4'][as,int][plus,2]"))
    assertResult(str("14"))(engine.eval("5[plus,2][mult,2][as,str]"))
    assertResult(str("14hello"))(engine.eval("5 int[plus,2][mult,2]str[plus,'hello']"))
    assertResult(str("14hello"))(engine.eval("5[plus,2][mult,2]str[plus,'hello']"))
    assertResult(vrec(str("x") -> int(7)))(engine.eval("5 int[plus,2][as,rec['x'->int]]"))
    assertResult(vrec(str("x") -> int(7),str("y") -> int(10)))(engine.eval("5 int[plus 2]<x>[plus 3]<y>[as,rec['x'-><x>,'y'-><y>]]"))
    assertResult(vrec(str("x") -> int(7),str("y") -> int(10)))(engine.eval("5 int[plus 2]<x>[plus 3]<y>[as,rec['x'->int<x>,'y'->int<y>]]"))
    assertResult(vrec(str("x") -> int(7),str("y") -> int(10),str("z") -> vrec(str("a") -> int(17))))(engine.eval("5 int[plus 2]<x>[plus 3]<y>[as,rec['x'->int<x>,'y'->int<y>,'z'->[as,rec['a'-><x> + <y>]]]]"))
  }

  test("a instruction parsing"){
    assertResult(btrue)(engine.eval("1[a,int]"))
    assertResult(btrue)(engine.eval("1.2[a,real]"))
    assertResult(bfalse)(engine.eval("'1'[a,int]"))
    assertResult(int(1))(engine.eval("1[is?int]"))
    assertResult(int(1))(engine.eval("1 is?int"))
    assertResult(int(1))(engine.eval("1is?int"))
  }

  test("endomorphic type parsing"){
    assertResult(int.plus(int.mult(int(6))))(engine.eval("int[plus,int[mult,6]]"))
  }
  test("explain instruction parsing"){
    assert(engine.eval("int[plus,int[mult,6]][explain]").toString.contains("instruction"))
    assert(engine.eval("int[plus,[plus,2][mult,7]]<x>[mult,[plus,5]<y>[mult,[plus,<y>]]][is,[gt,<x>]<z>[id]][plus,5][explain]").toString.contains("bool<z>"))
  }

  test("choose instruction parsing"){
    List(
      int.plus(int(2)).choose[IntType,ObjType](int.is(int.gt(int(10))) -> int.gt(int(20)),int -> int.plus(int(10))),
      int.plus(int(2)).choose(trec[IntType,ObjType](int.is(int.gt(int(10))) -> int.gt(int(20)),int -> int.plus(int(10)))),
      int.plus(int(2)).choose(trec[IntType,ObjType](name = Tokens.rec,value = Map[IntType,ObjType](int.is(int.gt(int(10))) -> int.gt(int(20)),int -> int.plus(int(10)))))).
      foreach(chooseInst => {
        assertResult(chooseInst)(engine.eval("int[plus,2][choose,rec[int[is,int[gt,10]]->int[gt,20] | int->int[plus,10]]]"))
        assertResult(chooseInst)(engine.eval("int[plus,2][int[is,int[gt,10]]->int[gt,20] | int->int[plus,10]]"))
        assertResult(chooseInst)(engine.eval("int[plus,2][int[is,[gt,10]]->[gt,20] | int->[plus,10]]"))
        assertResult(chooseInst)(engine.eval("int[plus,2][int[is,[gt,10]]->int[gt,20] | int->int[plus,10]]"))
        assertResult(chooseInst)(engine.eval("int[plus,2][[is,[gt,10]]->[gt,20] | int->[plus,10]]"))
        assertResult(chooseInst)(engine.eval(
          """
            | int[plus,2]
            |    [int[is,int[gt,10]] -> int[gt,20]
            |    |int                -> int[plus,10]]""".stripMargin))
      })
  }

  test("choose with mixed end types"){
    assertResult(btrue)(engine.eval("  5 [plus,2][[is>5]->true|[is==1]->[plus 2]|int->20]"))
    assertResult(int(3))(engine.eval("-1 [plus,2][[is>5]->true|[is==1]->[plus2]|int->20]"))
    assertResult(int(20))(engine.eval("1 [plus,2][[is>5]->true|[is==1]->[plus 2]|int->20]"))
    assertResult(obj)(engine.eval("int[plus,2][int[is>5]->true|[is==1]->[plus2]|int->20]").asInstanceOf[Type[Obj]].range)
    //
    assertResult(btrue.q(int(3)))(engine.eval("             5{3} [plus,2][[is>5]->true|[is==1]->[plus,2]|int->20]"))
    assertResult(int(3).q(5))(engine.eval("           -1{5} [plus,2][[is>5]->true|[is==1]->[plus,2]|int->20]"))
    assertResult(int(20).q(int(8),int(10)))(engine.eval("1{8,10} [plus,2][[is>5]->true|[is==1]->[plus,2]|int->20]"))
    assertResult(obj.q(+))(engine.eval("int{+}[plus,2][[is>5]->true|[is==1]->[plus,2]|int->20]").asInstanceOf[Type[Obj]].range)
  }

  test("traverser read/write state parsing"){
    assertResult(real(45.5))(engine.eval("45.0<x>[mult,0.0][plus,<x>][plus,0.5]"))
    assertResult(int.to("a").plus(int(10)).to("b").mult(int(20)))(engine.eval("int<a>[plus,10]<b>[mult,20]"))
    assertResult(int.to("a").plus(int(10)).to("b").mult(int.from[IntType]("a")))(engine.eval("int<a>[plus,10]<b>[mult,<a>]"))
    assertResult(int.to("a").plus(int(10)).to("b").mult(int.from[IntType]("a")))(engine.eval("int<a>[plus,10]<b>[mult,int<a>]"))
    assertResult(int.to("a").plus(int(10)).to("b").mult(int.from[IntType]("a")))(engine.eval("int<a>[plus,10]int<b>[mult,int<a>]"))
    assertResult(int(21))(engine.eval("5[plus,2]<x>[mult,2][plus,<x>]"))
    assertResult(int(21))(engine.eval("5[plus,2]<x>[mult,2][plus,int<x>]"))
    assertResult("int[plus,2]<x>[mult,2]<y>[plus,int<x>[plus,int<y>]]")(engine.eval("int[plus,2]<x>[mult,2]<y>[plus,<x>[plus,<y>]]").toString)
    assertResult(int(35))(engine.eval("5[plus,2]<x>[mult,2]<y>[plus,int<x>[plus,<y>]]"))
  }

  test("infix operator instruction parsing"){
    assertResult(real.plus(6.0))(real.plus(6.0))
    assertResult(int.plus(int(6)))(engine.eval("int+6"))
    assertResult(real.plus(real(6.0)))(engine.eval("real + 6.0"))
    assertResult(int.plus(int(6)).gt(int(10)))(engine.eval("int+6>10"))
    assertResult(int.plus(int(6)).lt(int(10)))(engine.eval("int+6<10"))
    assertResult(int.plus(int(6)).lte(int(10)))(engine.eval("int+6=<10"))
    assertResult(int.plus(int(6)).gte(int(10)))(engine.eval("int+6>=10"))
    assertResult(int.plus(int(1)).mult(int(2)).gt(int(10)))(engine.eval("int+1*2>10"))
    assertResult(str.plus(str("hello")))(engine.eval("str+'hello'"))
    assertResult(int.is(int.gt(int(5))))(engine.eval("int[is,int[gt,5]]"))
    assertResult(int.is(int.gt(int(5))))(engine.eval("int[is>5]"))
    assertResult(int.is(int.gt(int(5))))(engine.eval("int[is > 5]"))
    assertResult(int.is(int.lt(int(5))))(engine.eval("int[is < 5]"))
    assertResult(int.is(int.lte(int(5))))(engine.eval("int[is =< 5]"))
    assertResult(int.is(int.gte(int(5))))(engine.eval("int[is >= 5]"))
  }

  test("get dot-notation parsing"){
    assertResult(__.get(str("a")))(engine.eval(".a"))
    assertResult(__.get(str("a")).get(str("b")))(engine.eval(".a.b"))
    assertResult(__.get(str("a")).get(str("b")).get(str("c")))(engine.eval(".a.b.c"))
    assertResult(int(4))(engine.eval(
      """
        |['a':
        |  ['aa':1,
        |   'ab':2],
        | 'b':
        |   ['ba':3,
        |    'bb':
        |      ['bba':4]]].b.bb.bba""".stripMargin))
    assertResult(int(0))(engine.eval("['a':['b':['c':['d':0]]]].a.b.c.d"))
  }

  test("bool strm input parsing"){
    assertResult(Set(btrue))(engine.eval("true,false bool{*}[is,[id]]").toSet)
    assertResult(Set(btrue))(engine.eval("true,false[is,[id]]").toSet)
  }

  test("int strm input parsing"){
    assertResult(Set(int(-1),int(0)))(engine.eval("0,1 int{+}[plus,-1]").toSet)
    assertResult(Set(int(1),int(2),int(3)))(engine.eval("0,1,2[plus,1]").toSet)
    assertResult(Set(int(30),int(40)))(engine.eval("0,1,2,3 int{2,5}[plus,1][is,int[gt,2]][mult,10]").toSet)
    assertResult(Set(int(300),int(40)))(engine.eval("0,1,2,3[plus,1][is,int[gt,2]][int[is,int[gt,3]] -> int[mult,10] | int -> int[mult,100]]").toSet)
    // assertResult(Set(int(30),int(40)))(engine.eval("0,1,2,3 ==> (int{3}=>int[plus,1][is,int[gt,2]][mult,10])")).toSet)
  }

  test("real strm input parsing"){
    assertResult(Set(real(-1.2),real(0.0)))(engine.eval("0.0,1.2 real{+}[plus,-1.2]").toSet)
  }

  test("str strm input parsing"){
    assertResult(str("marko"))(engine.eval("""'m','a','r','k','o' str{*}[fold,'seed','',str[plus,str<seed>]]"""))
    assertResult(int(5))(engine.eval("""'m','a','r','k','o'[count]"""))
  }

  test("rec strm input parsing"){
    assertResult(Set(vrec(str("a") -> int(1),str("b") -> int(0)),vrec(str("a") -> int(2),str("b") -> int(0))))(engine.eval("""['a'->1],['a'->2][plus,['b'->0]]""").toSet)
  }

  test("anonymous expression parsing"){
    assertResult(int.is(int.gt(int.id())))(engine.eval("int[is,[gt,[id]]]"))
    assertResult(real.is(real.gt(real.id())))(engine.eval("real[is,[gt,[id]]]"))
    assertResult(int.plus(int(1)).plus(int.plus(int(5))))(engine.eval("int[plus,1][plus,[plus,5]]"))
    assertResult(int.plus(int(1)).is(int.gt(int(5))))(engine.eval("int[plus,1][is,[gt,5]]"))
    assertResult(int.q(?) <= int.is(int.gt(int(5))))(engine.eval("int[is,[gt,5]]"))
    assertResult(int.q(?) <= int.is(int.gt(int.mult(int.plus(int(5))))))(engine.eval("int[is,[gt,[mult,[plus,5]]]]"))
    assertResult(int.q(?) <= int.is(int.gt(int.mult(int.plus(int(5))))))(engine.eval("int[is,int[gt,int[mult,int[plus,5]]]]"))
    assertResult(engine.eval("int[is,int[gt,int[mult,int[plus,5]]]]"))(engine.eval("int[is,[gt,[mult,[plus,5]]]]"))
    assertResult(int.choose(int.is(int.gt(int(5))) -> int(1),int -> int(2)))(engine.eval(" int[[is>5] -> 1 | int -> 2]"))
    assertResult(int.plus(int(10)).choose(trec[Obj,Obj](int.is(int.gt(int(10))) -> int.gt(int(20)),int -> int.plus(int(10)))))(engine.eval(" int[plus,10][[is,[gt,10]]->[gt,20] | int->[plus,10]]"))
    assertResult(int.plus(int(10)).choose(int.is(int.gt(int(5))) -> int(1),int -> int(2)))(engine.eval(" int[plus,10][[is>5] -> 1 | int -> 2]"))
    assertResult(Set(int(302),int(42)))(engine.eval(
      """ 0,1,2,3
        | [plus,1][is>2]
        |   [ is>3 -> [mult,10]
        |   | int  -> [mult,100]][plus,2]""".stripMargin).toSet)
    assertResult(bfalse)(engine.eval("4[plus,1][[is>5] -> true | int -> false]"))
    assertResult(btrue)(engine.eval("5[plus,1][[is>5] -> true | int -> false]"))
    assertResult(btrue)(engine.eval("true[bool -> bool | int -> int]"))
    assertResult(int(10))(engine.eval("10[bool -> bool | int -> int]"))
    assertResult(int(10))(engine.eval("10[bool -> true | int -> int]"))
    assertResult(int(11))(engine.eval("10[bool -> true | int -> int[plus,1]]"))
  }

  test("expression parsing"){
    assertResult(btrue)(engine.eval("true bool[is,bool]"))
    assertResult(int(7))(engine.eval("5 int[plus,2]"))
    assertResult(int(70))(engine.eval("10 int[plus,int[mult,6]]"))
    assertResult(int(55))(engine.eval("5 int[plus,int[mult,int[plus,5]]]"))
    assertResult(bfalse)(engine.eval("0 int+1*2>10"))
    assertResult(str("marko rodriguez"))(engine.eval("'marko' str[plus,' '][plus,'rodriguez']"))
    assertResult(int(10))(engine.eval("10 int[is,bool<=int[gt,5]]"))
    assertResult(int.q(?) <= int.plus(int(10)).is(int.gt(int(5))))(engine.eval(" int[plus,10][is,bool<=int[gt,5]]"))
    assertResult(int.q(?) <= int.plus(int(10)).is(int.gt(int(5))))(engine.eval("int[plus,10][is,int[gt,5]]"))
    assertResult(int.q(0,3) <= int.q(3).plus(int(10)).is(int.q(3).gt(int(5))))(engine.eval("int{3}[plus,10][is,int[gt,5]]"))
  }

  test("reducing expressions"){
    assertResult(int(7))(engine.eval("5{7} int{7}[plus,2][count]"))
    assertResult(int(7))(engine.eval("5{7} [plus,2][count]"))
    assertResult(int(5))(engine.eval("1,3,7,2,1 int{3,100}[plus,2][count]"))
    assertResult(int(6))(engine.eval("1,3,7,2,1,10 [plus,2][count]"))
    assertResult(int(2))(engine.eval("1,3,7,2,1,10 +2[is>5][count]"))
    //assertResult(real(2.0))(engine.eval("1.0,3.1,7.2,2.5,1.1,10.1 +2.0[is>5.0][count]"))
    ///
    assertResult(int(7))(engine.eval("1,2,3 int{1,7}[fold,'seed',1,[plus,int<seed>]]"))
  }

  test("logical expressions"){
    assertResult(btrue)(engine.eval("true[and,true]"))
    assertResult(btrue.q(3))(engine.eval("true{3}[and,true][or,false]"))
    assertResult(btrue.q(3))(engine.eval("true{3}[and,true][or,[and,bool]]"))
    assertResult(bfalse.q(3,30))(engine.eval("true{3,30}[and,false][or,[and,bool]]"))
  }

  test("composite expression parsing"){
    assertResult(trec(str("age") -> int).id())(engine.eval("rec['age'->int][id]"))
    assertResult(vrec(str("age") -> int(29)))(engine.eval("['age'->29] rec['age'->int][id]"))
    assertResult(trec(str("age") -> int,str("name") -> str) <= trec[Str,Obj](str("age") -> int).put(str("name"),str))(engine.eval("rec['age'->int][put,'name',str]"))
    assertResult(rec(str("age") -> int(29),str("name") -> str("marko")))(engine.eval(
      """
        |['age'->29] rec['age'->int][choose,rec[
        |  rec['age'->int][is,rec['age'->int][get,'age'][gt,30]] -> rec['age'->int][put,'name','bill'] |
        |  rec['age'->int]                                       -> rec['age'->int][put,'name','marko']]]""".stripMargin))
    assertResult(rec(str("age") -> int(29),str("name") -> str("marko")))(engine.eval(
      """
        |['age'->29] rec['age'->int][
        |  is.age>30  -> [put,'name','bill'] |
        |  rec        -> [put,'name','marko']]""".stripMargin))
  }

  test("global model"){
    println(engine.eval("model"))
  }

  test("model inst parsing"){
    assertResult("int[plus,1][model,rec[int[is,bool<=int[gt,0]]:nat]]")(engine.eval("int[plus,1][model,rec[int<=int[is,[gt,0]]:nat]]").toString)
    assertResult("nat:11")(engine.eval("10[plus,1][model,rec[int[is,[gt,0]]:nat]]").toString)
    assertResult("-10")(engine.eval("-11[plus,1][model,rec[int[is,[gt,0]]:nat]]").toString)
    engine.put("model",Model.simple().put(int <= int.is(int.gt(0)),tobj("nat")))
    assertResult("nat:12")(engine.eval("11[plus,1][as,nat]").toString)
    assertResult(btrue)(engine.eval("11[plus,10][a,nat]"))
    assertResult(bfalse)(engine.eval("-100[plus,10][a,nat]"))
    assertThrows[AssertionError]{engine.eval("-11[plus,1][as,nat]")}
    println(engine.eval("int[plus,10][a,nat]"))
    println(engine.eval("int[plus,1][as,nat][mult,19]").toString)
//TODO    println(engine.eval("int{3}[[is,[a,nat]]->[as,nat][plus,20] | obj->[is,false]]").toString)
    assertResult("nat:32")(engine.eval("32").toString)
//TODO    assertResult(List(int(32).named("nat"),int(44).named("nat")))(engine.eval("12,-4,24[is[a,nat]->[as,nat][plus,20] | int->[is,false]]").toList)
  }

  /*test("model parsing"){
    val engine2                          = LanguageFactory.getLanguage("mmlang").getEngine.get()
    val person:RecType[StrValue,ObjType] = trec(str("name") -> str,str("age") -> int)
    // model creation
    assertResult(trec(tobj("nat") -> (int <= int.is(int.gt(0)))))(engine2.eval("rec[nat -> int<=int[is>0]]"))
    assertResult(trec[ObjType,ObjType](
      tobj("nat") -> (int <= int.is(int.gt(0))),
      tobj("person") -> trec(
        str("name") -> str,
        str("age") -> tobj("nat"))))(engine2.eval("rec[nat -> int<=int[is>0] | person -> rec['name'->str,'age'->nat]]"))
    val model:Model = Model.from(engine2.eval("rec[nat -> int<=int[is>0] | person -> rec['name'->str,'age'->nat]]").asInstanceOf[RecType[Type[Obj],Type[Obj]]])
    engine2.put("model",model)
    assertResult(model)(engine2.get("model"))
    // model compilations
    assertResult(int <= int.is(int.gt(0)))(engine2.eval("nat"))
    assertResult(int(4))(engine2.eval("2 nat[plus,2]"))
    assertResult(int <= int.is(int.gt(0)).plus(1))(engine2.eval("nat+1"))
    assertResult(trec(value = Map(str("name") -> str,str("age") -> tobj("nat"))))(engine2.eval("person"))
    assertResult(str <= person.get(str("name"),str))(engine2.eval("person[get,'name']"))
    assertResult(person.get(str("age"),int.named("nat")).plus(1))(engine2.eval("person[get,'age'][plus,1]"))

    // model type checking
    /*  assertThrows[AssertionError]{ // TODO: requires no type erasure and model checking
    engine.eval("-2 nat[plus,2]")()
     }*/
    /*assertThrows[AssertionError]{ // TODO: requires that a specified range can't be overridden by abstract interpretation
      println(engine.eval("nat<=nat[plus,-3]")())
      engine.eval("2 nat<=nat[plus,-3]")()
    }*/
    //assertResult(int(30))(engine.eval("['name'->'marko','age'->29]person[get,'age',nat][plus,1]")) // TODO: need to type check the values against the domain of the type (we currently only type check types against types)
    //assertResult(false)(engine.eval("['name'->'ryan','age'->10],['name'->'marko','age'->29] person[get,'age'][plus,1]")) // TODO: need to type check the values against the domain of the type (we currently only type check types against types)
  }*/
}