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

package org.mmadt.machine.obj.impl.traverser

import org.mmadt.machine.obj.theory.obj.Inst
import org.mmadt.machine.obj.theory.obj.`type`.Type
import org.mmadt.machine.obj.theory.obj.value._
import org.mmadt.machine.obj.theory.obj.value.inst.PlusInst
import org.mmadt.machine.obj.theory.traverser.Traverser

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TTraverser[V <: Value[V]](obj: V) extends Traverser[V] {

  override def obj(): V = obj

  override def apply[T <: Type[T]](t: Type[T]): Traverser[V] = {
    if (t.insts().isEmpty)
      return this
    t.insts().head._2 match {
      case inst: PlusInst[V] => new TTraverser[V](inst.apply(this.obj)).apply(t.pop())
      case inst: Inst => new TTraverser[V](this.obj.asInstanceOf[IntValue].mult(inst.arg().asInstanceOf[IntValue]).asInstanceOf[V]).apply(t.pop())
      case _ => throw new RuntimeException("Unknown instruction: " + t);
    }
  }
}