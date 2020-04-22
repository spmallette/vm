package org.mmadt.processor.obj.branch

import org.mmadt.language.obj.branch.{Brch, Prod}
import org.mmadt.language.obj.op.map.MultBOp
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.{Int, Obj, Str}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2, TableFor4}

class ProdTest extends FunSuite with TableDrivenPropertyChecks {

  test("product value/type checking") {
    val starts: TableFor2[Brch[Obj], Boolean] =
      new TableFor2[Brch[Obj], Boolean](("prod", "isValue"),
        (prod(), true),
        (prod("a", "b"), true),
        (prod("a", "b", "c", "d"), true),
        (prod[Obj]("a", "b").mult(prod("c", "d")), true),
        (MultBOp[Obj](prod("c", "d")).exec(prod("a", "b")), true),
        (prod(str, "b"), false),
      )
    forEvery(starts) { (prod, bool) => {
      assertResult(bool)(prod.isValue)
    }
    }
  }

  test("product [put]") {
    val starts: TableFor4[Prod[Obj], Int, Obj, Prod[Obj]] =
      new TableFor4[Prod[Obj], Int, Obj, Prod[Obj]](("prod", "key", "value", "newProd"),
        (prod(), 0, "a", prod("a")),
        (prod("b"), 0, "a", prod("a", "b")),
        (prod("a", "c"), 1, "b", prod("a", "b", "c")),
        (prod("a", "b"), 2, "c", prod("a", "b", "c")),
        (prod("a", "b"), 2, prod[Str]("c", "d"), prod("a", "b", prod[Str]("c", "d"))),
        //
        (prod(), 0, str, prod[Obj](str).via(prod(),PutOp[Int,Str](0, str))),
        (prod(), int.is(int.gt(0)), "a", prod().via(prod(), PutOp[Int, Str](int.is(int.gt(0)), "a"))),
      )
    forEvery(starts) { (product, key, value, newProduct) => {
      assertResult(newProduct)(product.put(key, value))
      assertResult(newProduct)(PutOp(key, value).exec(product))
    }
    }
  }

}