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

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.{IntType, Type}
import org.mmadt.language.obj.op.map.IdOp
import org.mmadt.language.obj.{DomainInst, Inst, InstList, Int, IntQ, Obj, base}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TInt(name:String,quantifier:IntQ,_insts:DomainInst[Int]) extends AbstractTObj(name,quantifier,Nil) with IntType {
  def this() = this(Tokens.int,qOne,base())
  def this(name:String) = this(name,qOne,base())
  override def q(quantifier:IntQ):this.type = new TInt(name,quantifier,_insts).asInstanceOf[this.type]

  override      val insts:InstList                = if (null == _insts._1) Nil else _insts._1.insts ++ (Nil :+ (_insts._1,_insts._2))
  override lazy val via  :(Type[Obj],Inst[_,Int]) = if (null == _insts._1) (this,IdOp[Int]()) else _insts.asInstanceOf[(Type[Obj],Inst[_,Int])]
}
