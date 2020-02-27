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

package org.mmadt.language.obj.op.traverser

import org.mmadt.language.Tokens
import org.mmadt.language.obj.op.TraverserInstruction
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.obj._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait FromOp {
  this:Obj =>

  def from[O <: Obj](label:String):O = this.from(str(label))
  def from[O <: Obj](label:String,default:Obj):O = this.from(str(label),default)
  def from[O <: Obj](label:StrValue):O = label.asInstanceOf[O] // TODO NO IMPL -- INST
  def from[O <: Obj](label:StrValue,default:Obj):O = default.asInstanceOf[O]
}

object FromOp {
  def apply(label:StrValue):Inst = new VInst((Tokens.from,List(label)),qOne,(a:Obj,b:List[Obj]) => a.from[Obj](label)) with TraverserInstruction
  def apply[O <: Obj](label:StrValue,default:Obj):Inst = new VInst((Tokens.from,List(label,default)),qOne,(a:Obj,b:List[Obj]) => a.from[Obj](label,default)) with TraverserInstruction
}
