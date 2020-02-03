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

package org.mmadt.machine.obj.theory.obj.`type`

import org.mmadt.language.{Stringer, Tokens}
import org.mmadt.machine.obj.TQ
import org.mmadt.machine.obj.impl.obj.value.VInt.int0
import org.mmadt.machine.obj.theory.obj.value.BoolValue
import org.mmadt.machine.obj.theory.obj.{Inst, Obj}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Type[T <: Type[T]] extends Obj {

  def insts(): List[(Type[_], Inst)] //
  def push(inst: Inst, q: TQ): T //
  def pop(): T //

  def is(bool: BoolValue): T = if (bool.value()) this.asInstanceOf[T] else this.asInstanceOf[T] //.q(int(0), int(0))
  def is(bool: BoolType): T = this.push(inst(Tokens.is, bool), (int0, q()._2))

  override def toString: String = Stringer.typeString(this)


}