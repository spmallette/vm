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

package org.mmadt.language.obj.op

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.obj.qOne
import org.mmadt.storage.obj.value.{VInst, VStr}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ToOp[O <: Type[O]] {
  def to(other: String): O = this.to(new VStr(other)) //
  def to(label: StrValue): O
}

object ToOp {
  def apply[O <: ToOp[O] with Type[O]](other: StrValue): Inst = new VInst((Tokens.to, List(other)), qOne, ((a: O, b: List[Obj]) => a.to(other)).asInstanceOf[(Obj, List[Obj]) => Obj]) //
}