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

package org.mmadt.machine.obj.theory.obj.value.inst

import org.mmadt.machine.obj.theory.obj.`type`.Type
import org.mmadt.machine.obj.theory.obj.util.VorT
import org.mmadt.machine.obj.theory.obj.value.{RecValue, Value}
import org.mmadt.machine.obj.theory.obj.{Inst, Obj}
import org.mmadt.machine.obj.theory.traverser.Traverser

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait GetInst[A <: Obj, B <: Obj] extends Inst {
  type LEFT = Left[Value[_], Type[_]]
  type RIGHT = Right[Value[_], Type[_]]
  private lazy val wrappedArg = VorT.wrap[A, A](arg())


  override def apply(traverser: Traverser): Traverser = {
    wrappedArg match {
      case argV: LEFT => traverser.split(traverser.obj[RecValue[A, B]]().get(argV.value.asInstanceOf[A]))
      case argT: RIGHT => traverser.split(traverser.obj[RecValue[A,B]]().get(traverser.apply(argT.value.asInstanceOf[Type[_]]).obj()))
    }
  }
}
