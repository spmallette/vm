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

package org.mmadt.storage.obj

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Lst.LstTuple
import org.mmadt.language.obj.Obj.{IntQ, ViaTuple, rootVia}
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{LstType, Type}
import org.mmadt.storage.StorageFactory.qOne
import org.mmadt.storage.obj.`type`.TLst
import org.mmadt.storage.obj.value.VLst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
abstract class OLst[A <: Obj](val name: String = Tokens.lst, val g: LstTuple[A] = (Tokens.`,`, List.empty[A]), val q: IntQ = qOne, val via: ViaTuple = rootVia) extends Lst[A] {
  override def clone(name: String = this.name,
                     g: Any = this.g,
                     q: IntQ = this.q,
                     via: ViaTuple = this.via): this.type = OLst.makeLst(name = name, g = g.asInstanceOf[LstTuple[A]], q = q, via = via).asInstanceOf[this.type]

}
object OLst {
  def makeLst[A <: Obj](name: String = Tokens.lst, g: LstTuple[A] = (Tokens.`,`, List.empty[A]), q: IntQ = qOne, via: ViaTuple = rootVia): Lst[A] = {
    val list: List[A] = g._1 match {
      case _ if g._2 == null => null
      case Tokens.`,` => Type.mergeObjs(g._2).asInstanceOf[List[A]]
      case Tokens.`;` => g._2
      case Tokens.`|` => g._2.filter(_.alive)
    }
    if (null != list && (list.isEmpty || !list.filter(_.alive).exists(x => x.isInstanceOf[Type[_]]))) new VLst[A](name, g = (g._1, list), q, via)
    else new TLst[A](name, g = (g._1, list), q, via)
  }
  def emptyType[A <: Obj]: LstType[A] = new TLst[A](g = (Tokens.`,`, null))
}

