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

package org.mmadt.language.obj.value.strm

import org.mmadt.language.LanguageFactory
import org.mmadt.language.obj.Obj.{IntQ, ViaTuple, rootVia}
import org.mmadt.language.obj._
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.strm.util.MultiSet

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Strm[+O <: Obj] extends Value[O] {
  def values: Seq[O]

  def apply[P <: Obj](f: O => P): P = strm[P](this.values.map(x => f(x)))
  override def via(obj: Obj, inst: Inst[_ <: Obj, _ <: Obj]): this.type = strm(this.values.map(x => inst.asInstanceOf[Inst[Obj, Obj]].exec(x)).filter(_.alive)).asInstanceOf[this.type]
  override def q(q: IntQ): this.type = strm(this.values.map(x => x.q(q)).filter(_.alive)).asInstanceOf[this.type]
  override val q: IntQ = this.values.foldLeft(qZero)((a, b) => plusQ(a, b.q))
  // utility methods
  override def toStrm: Strm[this.type] = this.asInstanceOf[Strm[this.type]]
  override def clone(name: String = this.name, g: Any = null, q: IntQ = this.q, via: ViaTuple = rootVia): this.type = strm(this.values).asInstanceOf[this.type]

  // standard Java implementations
  override def toString: String = LanguageFactory.printStrm(this)
  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.values.hashCode()
  override def test(other: Obj): Boolean = withinQ(this, other) && this.values.head.test(other)
  override def equals(other: Any): Boolean = other match {
    case obj: Obj if !this.alive => !obj.alive
    case avalue: Value[O] => MultiSet.equals(this, avalue.toStrm)
    case _ => false
  }
}