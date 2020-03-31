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

package org.mmadt.storage.obj.`type`

import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.op.map.IdOp
import org.mmadt.language.obj.{Inst, InstList, IntQ, Obj}
import org.mmadt.storage.obj.OObj

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
abstract class AbstractTObj(name:String,quantifier:IntQ,_insts:InstList) extends OObj(name,quantifier) with Type[Obj] {
  override lazy val via  :(Type[Obj],Inst[_,Obj]) = if (_insts.isEmpty) (this,IdOp()) else (_insts.last._1,_insts.last._2.asInstanceOf[Inst[Obj,Obj]])
  override      val insts:InstList                  = if (_insts.isEmpty) Nil else _insts.last._1.insts ++ (Nil :+ (_insts.last._1,_insts.last._2))
}