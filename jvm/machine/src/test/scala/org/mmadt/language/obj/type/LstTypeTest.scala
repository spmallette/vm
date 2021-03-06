/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language.obj.`type`

import org.mmadt.TestUtil
import org.mmadt.language.Tokens
import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.mmlang.mmlangScriptEngineFactory
import org.mmadt.language.obj.Obj.intToInt
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.{Int, Lst, Obj, Poly}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

class LstTypeTest extends FunSuite with TableDrivenPropertyChecks {

  private val engine: mmADTScriptEngine = new mmlangScriptEngineFactory().getScriptEngine

  test("lst type token") {
    assertResult("lst")(lst.toString)
    assert(lst.isInstanceOf[LstType[_]])
    assert(lst.test(lst))
    assert(!lst.test(rec))
    assert(!lst.test(int))
    assert((str("a") `,` "b").test(lst))
    assert((str("a") `|` "b").test(lst))
    assert((str("a") `;` "b").test(lst))
    assert(!(str("a") `;` "b").test(lst.q(20)))
    assert((str("a") `;` "b").test(lst.q(0, 20)))
  }

  test("lst type basics") {
    assert((int(1) `;` 2).test(int `;` int))
    //assert((int(1) `,` 2).test(int.q(2)))
  }

  test("lst type [split]/[merge]") {
    val clst: Lst[IntType] = lst(g = (Tokens.`,`, List(int.plus(1), int.plus(2), int.plus(3))))
    val plst: Lst[IntType] = lst(g = (Tokens.`|`, List(int.plus(1), int.plus(2), int.plus(3))))
    val slst: Lst[IntType] = lst(g = (Tokens.`;`, List(int.plus(1), int.plus(2), int.plus(3))))

    assertResult(int.q(3))(clst.merge.range)
    assertResult(int.q(1))(plst.merge.range)
    assertResult(int.q(1))(slst.merge.range)

    assertResult(int(11, 12, 13))(int(10).split(clst).merge)
    assertResult(int(11))(int(10).split(plst).merge)
    assertResult(int(16))(int(10).split(slst).merge)
  }

  test("parallel expressions") {
    val starts: TableFor3[Obj, Lst[Obj], Obj] =
      new TableFor3[Obj, Lst[Obj], Obj](("lhs", "rhs", "result"),
        (int(1), int `,` int, int(1).q(2)),
        (int(1), int `,` int.plus(2), int(1, 3)),
        (int(1), int `,` int.plus(2).q(10), int(1, int(3).q(10))),
        (int(1).q(5), int `,` int.plus(2).q(10), int(int(1).q(5), int(3).q(50))),
        (int(1, 100), int | int, int(int(1), int(100))),
        (int(int(1), int(100)), int `,` int, int(1, 1, 100, 100)),
        (int(int(1), int(100)), int `,` int, int(int(1).q(2), int(100).q(2))),
        (int(int(1).q(5), int(100)), int `,` int.plus(2).q(10), int(int(1).q(5), int(3).q(50), int(100), int(102).q(10))),
        (int(int(1).q(5), int(100)), int | int.plus(2).q(10), int(int(1).q(5), int(100))),
        (int(1, 2), int | (int | int), int(1, 2)),
        (int(1, 2), (int | int) | int, int(1, 2)),
        (int(1, 2), (int | int) | obj, int(1, 2)),
        (int(1, 2), (str | str) | str, zeroObj),
        ((1 `;` 2), ((int `;` int) | str), (1 `;` 2)),
        (1, str | int, 1),
      )
    forEvery(starts) { (lhs, rhs, result) => TestUtil.evaluate(lhs, __.split(rhs).merge[Obj], result, compile = false) }
  }

  test("parallel [get] types") {
    assertResult(str)((str.plus("a") | str).get(0, str).range)
  }

  test("parallel structure") {
    val poly: Poly[Int] = int.mult(8).split(id | plus(2) | 3)
    assertResult("(int[id]|int[plus,2]|3)<=int[mult,8]-<(int[id]|int[plus,2]|3)")(poly.toString)
    assertResult(int.id)(poly.glist.head)
    assertResult(int.plus(2))(poly.glist(1))
    assertResult(int(3))(poly.glist(2))
    assertResult(int)(poly.glist.head.via._1)
    assertResult(int)(poly.glist(1).via._1)
    assert(poly.glist(2).root)
    assertResult(int.id | int.plus(2) | int(3))(poly.range)
  }

  test("parallel quantifier") {
    val poly: Poly[Int] = int.q(2).mult(8).split(id | plus(2) | 3)
    assertResult("(int[id]|int[plus,2]|3){2}<=int{2}[mult,8]-<(int[id]|int[plus,2]|3)")(poly.toString)
    /*    assertResult(int.q(2).id)(poly.glist.head)
        assertResult(int.q(2).plus(2))(poly.glist(1))
        assertResult(int(3))(poly.glist(2))
        assertResult(int.q(2))(poly.glist.head.via._1)
        assertResult(int.q(2))(poly.glist(1).via._1)
        assert(poly.glist(2).root)
        assertResult(int.q(2).id | int.q(2).plus(2) | int(3))(poly.range)*/
  }

  test("parallel [split] quantification") {
    assertResult(int)(int.mult(8).split(id | plus(8).mult(2) | int(56)).merge.id.rangeObj)
    assertResult(int.q(1, 20))(int.mult(8).split(id.q(10, 20) | plus(8).mult(2).q(2) | int(56)).merge.id.rangeObj)
    assertResult(int.q(2, 40))(int.q(2).mult(8).q(1).split(id.q(10, 20) | plus(8).mult(2).q(2) | int(56)).merge.id.rangeObj)
    assertResult(zeroObj)(int.q(2).mult(8).q(0).split(id.q(10, 20) | plus(8).mult(2).q(2) | int(56)).merge.id.rangeObj)
  }

  test("[mult] w/ lst types") {
    val check: TableFor3[Lst[Obj], Lst[Obj], Lst[Obj]] =
      new TableFor3[Lst[Obj], Lst[Obj], Lst[Obj]](("alst", "blst", "clst"),
        ((int.plus(1) `,` int.mult(2)), (int.plus(3) `,` int.mult(4)), (int.plus(1).plus(3) `,` int.plus(1).mult(4) `,` int.mult(2).plus(3) `,` int.mult(2).mult(4))),
      )
    forEvery(check) { (left, right, result) =>
      assertResult(result)(Lst.cmult(left, right))
    }
  }
}
