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

package org.mmadt.language.obj.op.initial

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.{IntType, __}
import org.mmadt.language.obj.op.InitialInstruction
import org.mmadt.language.obj.{Inst, Int, Obj}
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait IntOp {
  this:__ =>
  def int():IntType = this.compose(StorageFactory.int,IntOp()).hardQ(qOne)
}

object IntOp {
  def apply():Inst[Obj,Int] = new IntInst()

  class IntInst() extends VInst[Obj,Int]((Tokens.int,Nil)) with InitialInstruction {
    override def apply(trav:Traverser[Obj]):Traverser[Int] = trav.split(int)
  }

}