package org.mmadt.storage.obj

import org.mmadt.language.mmlang.mmlangScriptEngineFactory
import org.mmadt.language.obj.Obj._
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Int, Obj, Poly, Str}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2, TableFor4}

class OPolyTest extends FunSuite with TableDrivenPropertyChecks {

  test("basic poly") {
    assertResult(str("a"))(("a" | "b" | "c").head())
    assertResult("b" | "c")(("a" | "b" | "c").tail())

    assertResult(str("a"))(("a" / "b" / "c").head())
    assertResult("b" / "c")(("a" / "b" / "c").tail())
  }

  test("parallel expressions") {
    val starts: TableFor2[Obj, Obj] =
      new TableFor2[Obj, Obj](("expr", "result"),
        (int(1).-<(int / int), int(1) / int(1)),
        (int(1).-<(int / int.plus(2)), int(1) / int(3)),
        (int(1).-<(int / int.plus(2).q(10)), int(1) / int(3).q(10)),
        (int(1).q(5).-<(int / int.plus(2).q(10)), int(1).q(5) / int(3).q(50)),
        (int(1).q(5).-<(int / int.plus(2).q(10)) >-, int(int(1).q(5), int(3).q(50))),
        // (int(int(1), int(100)).-<(|(int, int)) >-, int(int(1), int(1), int(100), int(100))),
        //(int(int(1).q(5), int(100)).-<(|(int, int.plus(2).q(10))) >-, int(int(1).q(5), int(3).q(50), int(100), int(102).q(10))),
        //(int(int(1), int(2)).-<(|(int, int -< (|(int, int)))), |(strm(List(int(1), int(2))), strm(List(|(int(1), int(1)), |(int(2), int(2)))))),
        //(int(1) -< |(str, int), |[Obj](obj.q(0), int(1))),
        // (strm(List(int(1), str("a"))) -< `|`(str, int), strm(List(`|`[Obj](obj.q(0), int(1)), `|`[Obj](str("a"), obj.q(0))))),
      )
    forEvery(starts) { (query, result) => {
      println(s"${query}")
      assertResult(result)(new mmlangScriptEngineFactory().getScriptEngine.eval(s"${query}"))
      assertResult(result)(query)
    }
    }
  }


  test("parallel [tail][head] values") {
    val starts: TableFor2[Poly[Obj], List[Value[Obj]]] =
      new TableFor2[Poly[Obj], List[Value[Obj]]](("parallel", "projections"),
        (|, List.empty),
        ("a" |, List(str("a"))),
        ("a" | "b", List(str("a"), str("b"))),
        ("a" | "b" | "c", List(str("a"), str("b"), str("c"))),
        ("a" | ("b" | "d") | "c", List(str("a"), "b" | "d", str("c"))),
      )
    forEvery(starts) { (alst, blist) => {
      assertResult(alst.groundList)(blist)
      if (blist.nonEmpty) {
        assertResult(alst.head())(blist.head)
        assertResult(alst.ground._2.head)(blist.head)
        assertResult(alst.tail().ground._2)(blist.tail)
        assertResult(alst.ground._2.tail)(blist.tail)
      }
    }
    }
  }

  test("scala type constructor") {
    assertResult("['a'|'b']")(("a" | "b").toString())
  }

  /*test("parallel keys") {
    assertResult("[name->'marko'|age->29]")(("name" -> str("marko") | "age" -> int(29)).toString)
    assertResult(str("marko"))(("name" -> str("marko") | "age" -> int(29)).get("name"))
  }

  test("parallel [put] values") {
    assertResult(("name" -> str("marko")) | ("age" -> int(29)) | ("year" -> int(2020)))(("name" -> str("marko") | "age" -> int(29)).put("year", 2020))
    assertResult(("name" -> str("marko")) | ("age" -> int(40)))(("name" -> str("marko") | "age" -> int(29)).put("age", 40))
    assertResult(("name" -> str("marko")) | ("age" -> int(41)))(("name" -> str("marko") | "age" -> int(29)).put("age", 40).put("age", 41))

    //assertResult("[1->true]")(("1" -> btrue)|().toString)
    assertResult("[1->true|2->false]")((("1" -> btrue) | ("2" -> bfalse)).toString)
    assertResult("[1->true|2->false|3->false]")((("1" -> btrue) | ("2" -> bfalse)).put("3", bfalse).toString)
    //assertResult("[1->true|2->false]")((("1" -> btrue)|).put("2",bfalse).toString)
    //assertResult("[1->true|2->false]")((("1" -> btrue)|).plus(("2" -> bfalse)|).toString)
    //assertResult(bfalse)("1" -> btrue ==> poly("|").plus("2" -> bfalse).get("2", bool))
    /*assertResult(rec(int(1) -> btrue, int(2) -> bfalse))(rec(int(1) -> btrue) ==> rec.plus(rec(int(2) -> bfalse)))
    assertResult(btrue)(rec(int(1) -> btrue, int(2) -> bfalse).get(int(1)))
    assertResult(bfalse)(rec(int(1) -> btrue, int(2) -> bfalse).get(int(2)))
    intercept[NoSuchElementException] {
      rec(int(1) -> btrue, int(2) -> bfalse).get(int(3))
    }*/

    val X: Poly[Str] = "1" -> str("a")
    val Y: Poly[Str] = "2" -> str("b")
    val Z: Poly[Str] = "3" -> str("c")
    // forwards keys
    assertResult(List(str("a"), str("b")))(X.plus(Y).groundList)
    assertResult(List(str("a"), str("b"), str("c")))(X.plus(Y).plus(Z).groundList)
    /*assertResult(ListMap(X, Y))(rec(X).plus(rec(Y)).ground)
    assertResult(ListMap(X, Y, Z))(rec(X, Y, Z).ground)
    assertResult(ListMap(X, Y, Z))(rec(X).plus(rec(Y, Z)).ground)
    assertResult(ListMap(X, Y, Z))(rec(X, Y).plus(rec(Z)).ground)
    // backwards keys
    assertResult(ListMap(Y, X))(rec(Y, X).ground)
    assertResult(ListMap(Y, X))(rec(Y).plus(rec(X)).ground)
    assertResult(ListMap(Z, Y, X))(rec(Z, Y, X).ground)
    assertResult(ListMap(Z, Y, X))(rec(Z).plus(rec(Y, X)).ground)
    assertResult(ListMap(Z, Y, X))(rec(Z, Y).plus(rec(X)).ground)
    // overwrite orderings
    assertResult(ListMap(X, Y, Z))(rec(X, Y).plus(rec(X, Z)).ground) // TODO: determine overwrite order*/

  }*/


  test("parallel [get] values") {
    assertResult(str("a"))((str("a") |).get(0))
    assertResult(str("b"))((str("a") | "b").get(1))
    assertResult(str("b"))((str("a") | "b" | "c").get(1))
    assertResult("b" | "d")(("a" | ("b" | "d") | "c").get(1))
  }

  test("parallel [get] types") {
    assertResult(str)((str.plus("a") | str).get(0, str).range)
  }

  test("parallel structure") {
    val poly: Poly[Obj] = int.mult(8).split(__.id() | __.plus(2) | 3)
    assertResult("lst[int[id]|int[plus,2]|3]<=int[mult,8]-<[int[id]|int[plus,2]|3]")(poly.toString)
    assertResult(int.id())(poly.groundList.head)
    assertResult(int.plus(2))(poly.groundList(1))
    assertResult(int(3))(poly.groundList(2))
    assertResult(int)(poly.groundList.head.via._1)
    assertResult(int)(poly.groundList(1).via._1)
    assert(poly.groundList(2).root)
    assertResult(int.id() | int.plus(2) | int(3))(poly.range)
  }

  test("parallel quantifier") {
    val poly: Poly[Obj] = int.q(2).mult(8).split(__.id() | __.plus(2) | 3)
    assertResult("lst[int{2}[id]|int{2}[plus,2]|3]<=int{2}[mult,8]-<[int{2}[id]|int{2}[plus,2]|3]")(poly.toString)
    assertResult(int.q(2).id())(poly.groundList.head)
    assertResult(int.q(2).plus(2))(poly.groundList(1))
    assertResult(int(3))(poly.groundList(2))
    assertResult(int.q(2))(poly.groundList.head.via._1)
    assertResult(int.q(2))(poly.groundList(1).via._1)
    assert(poly.groundList(2).root)
    assertResult(int.q(2).id() | int.q(2).plus(2) | int(3))(poly.range)
  }

  test("parallel [split] quantification") {
    assertResult(int.q(3))(int.mult(8).split(__.id() | __.plus(8).mult(2) | int(56)).merge[Int].id().isolate)
    assertResult(int.q(13, 23))(int.mult(8).split(__.id().q(10, 20) | __.plus(8).mult(2).q(2) | int(56)).merge[Int].id().isolate)
    assertResult(int.q(25, 45))(int.q(2).mult(8).q(1).split(__.id().q(10, 20) | __.plus(8).mult(2).q(2) | int(56)).merge[Int].id().isolate)
    //assertResult(__)(int.q(2).mult(8).q(0).split(__.id().q(10, 20) / __.plus(8).mult(2).q(2) / int(56)).merge[Obj].id().isolate)
  }

  test("serial value/type checking") {
    val starts: TableFor2[Poly[Obj], Boolean] =
      new TableFor2[Poly[Obj], Boolean](("serial", "isValue"),
        (/, true),
        ("a" / "b", true),
        ("a" / "b" / "c" / "d", true),
        (str / "b", false),
      )
    forEvery(starts) { (serial, bool) => {
      assertResult(bool)(serial.isValue)
    }
    }
  }

  test("serial [put]") {
    val starts: TableFor4[Poly[Obj], Int, Obj, Poly[Obj]] =
      new TableFor4[Poly[Obj], Int, Obj, Poly[Obj]](("serial", "key", "value", "newProd"),
        (/, 0, "a", "a" /),
        ("b" /, 0, "a", "a" / "b"),
        ("a" / "c", 1, "b", "a" / "b" / "c"),
        ("a" / "b", 2, "c", "a" / "b" / "c"),
        //(str("a")/"b", 2, str("c")/ "d", str("a")/ "b"/ (str("c")/ "d")),
        //
        //(`/x`, 0, str, (str /).via(/, PutOp[Int, Str](0, str))),
        //(/, int.is(int.gt(0)), "a", /[Obj].via(/, PutOp[Int, Str](int.is(int.gt(0)), "a"))),
      )
    forEvery(starts) { (serial, key, value, newProduct) => {
      assertResult(newProduct)(serial.put(key, value))
      assertResult(newProduct)(PutOp(key, value).exec(serial))
    }
    }
  }

}
