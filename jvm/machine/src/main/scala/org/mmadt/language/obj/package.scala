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

package org.mmadt.language

import org.mmadt.language.obj.Obj.IntQ
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
package object obj {
  // less typing
  type OType[+O <: Obj] = Type[O] with O
  type OValue[+O <: Obj] = Value[O] with O
  type OStrm[+O <: Obj] = O with Strm[O]
  // quantifier utilities
  def minZero(quantifier: IntQ): IntQ = (int(0), quantifier._2)
  def maxZero(quantifier: IntQ): IntQ = (quantifier._2, quantifier._2)
  def multQ(qA: IntQ, qB: IntQ): IntQ = qB match {
    case x: IntQ if qOne.equals(x) => qA
    case _ => (qA._1.g * qB._1.g, qA._2.g * qB._2.g)
  }
  def zeroable(quantifier: IntQ): Boolean = quantifier._1.g <= 0 && quantifier._2.g >= 0

  def plusQ(qA: IntQ, qB: IntQ): IntQ = qB match {
    case _ if equals(qZero) => qA
    case _: IntQ => (qA._1.g + qB._1.g, qA._2.g + qB._2.g)
  }
  def minusQ(qA: IntQ, qB: IntQ): IntQ = qB match {
    case _ if equals(qZero) => qA
    case _: IntQ => (qA._1.g - qB._1.g, qA._2.g - qB._2.g)
  }
  def divQ(qA: IntQ, qB: IntQ): IntQ = qB match {
    case _ if equals(qZero) => qA
    case _: IntQ if (qB._1.g == 0) => (0, qA._2.g / qB._2.g)
    case _: IntQ => (qA._1.g / qB._1.g, qA._2.g / qB._2.g)
  }
  def withinQ(objA: Obj, objB: Obj): Boolean = {
    objA.q._1.g >= objB.q._1.g &&
      objA.q._2.g <= objB.q._2.g
  }
  def eqQ(objA: Obj, objB: Obj): Boolean = {
    (objA.q, objB.q) match {
      case (null, null) => true
      case (null, y) if y._1.g == 1 && y._2.g == 1 => true
      case (x, null) if x._1.g == 1 && x._2.g == 1 => true
      case (x, y) if x._1.g == y._1.g && x._2.g == y._2.g => true
      case _ => false
    }
  }
  def sameBase(objA: Obj, objB: Obj): Boolean = baseName(objA).equals(baseName(objB))
}


