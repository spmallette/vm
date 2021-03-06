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

package org.mmadt.language.obj

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Lst.LstTuple
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.branch.CombineOp
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory._

trait Lst[+A <: Obj] extends Poly[A]
  with CombineOp[Obj]
  with GetOp[Int, Obj]
  with PutOp[Int, Obj]
  with PlusOp[Lst[Obj]]
  with MultOp[Lst[Obj]]
  with ZeroOp[Lst[Obj]] {
  def g: LstTuple[A]
  def gsep: String = g._1
  lazy val glist: List[A] = if (null == g._2) List.empty[A] else g._2.map(x => x.update(this.model))
  override def ctype: Boolean = null == g._2 // type token

  override def equals(other: Any): Boolean = other match {
    case alst: Lst[_] => Poly.sameSep(this, alst) &&
      this.name.equals(alst.name) &&
      eqQ(this, alst) &&
      // this.glist.size == alst.glist.size &&
      this.glist.zip(alst.glist).forall(b => b._1.equals(b._2))
    case _ => true // MAIN EQUALS IS IN TYPE
  }
  def clone(f: List[A] => List[_]): this.type = this.clone(g = (this.gsep, f(this.glist)))
  final override def `,`(next: Obj): Lst[next.type] = this.lstMaker(Tokens.`,`, next)
  final override def `;`(next: Obj): Lst[next.type] = this.lstMaker(Tokens.`;`, next)
  final override def `|`(next: Obj): Lst[next.type] = this.lstMaker(Tokens.`|`, next)

  private final def lstMaker(sep: String, obj: Obj): Lst[obj.type] = {
    obj match {
      case blst: Lst[Obj] => lst(g = (sep, List[obj.type](this.asInstanceOf[obj.type], blst.asInstanceOf[obj.type])))
      case _ if sep != this.gsep => lst(g = (sep, List(this, obj).asInstanceOf[List[obj.type]]))
      case _ => this.clone(g = (sep, this.g._2 :+ obj)).asInstanceOf[Lst[obj.type]]
    }
  }
}

object Lst {
  type LstTuple[+A <: Obj] = (String, List[A])

  def test[A <: Obj](alst: Lst[A], blst: Lst[A]): Boolean =
    Poly.sameSep(alst, blst) &&
      withinQ(alst, blst) &&
      (blst.ctype || {
        if (blst.isChoice) alst.g._2.exists(x => x.alive)
        else alst.size == blst.size
      }) &&
      alst.glist.zip(blst.glist).forall(pair => if (blst.isChoice && pair._1.alive && pair._2.alive && pair._1 == pair._2) true else pair._1.test(pair._2))

  def moduleMult[A <: Obj, B <: Obj](start: A, alst: Lst[A]): Lst[A] = {
    alst.gsep match {
      /////////// ,-lst
      case Tokens.`,` => alst.clone(x => Type.mergeObjs(Type.mergeObjs(x).map(v => start ~~> v)))
      /////////// ;-lst
      case Tokens.`;` =>
        var running = start
        alst.clone(_.map(v => {
          running = if (running.isInstanceOf[Strm[_]]) strm(running.toStrm.values.map(r => r ~~> v))
          else Obj.resolveArg(running, v) match {
            case x: Value[_] if v.isInstanceOf[Value[_]] => x.hardQ(q => multQ(running.q, q)).asInstanceOf[A]
            case x => x
          }
          running
        }))
      /////////// |-lst
      case Tokens.`|` =>
        //var taken: Boolean = false
        alst.clone(_.map(v => start ~~> v).filter(_.alive))
      /*.filter(v =>
        if (taken) false
        else if (zeroable(v.q)) true
        else {
          taken = true;
          true
        }))*/
    }
  }

  def keepFirst[A <: Obj](apoly: Lst[A]): Lst[A] = {
    val first: scala.Int = apoly.glist.indexWhere(x => x.alive)
    apoly.clone(_.zipWithIndex.map(a => if (a._2 == first) a._1 else zeroObj.asInstanceOf[A]))
  }

  def cmult[A <: Obj](apoly: Lst[A], bpoly: Lst[A]): Lst[A] = {
    var clist: List[A] = Nil
    apoly.glist.foreach(a => bpoly.glist.foreach(b => {
      clist = clist :+ (a `=>` b)
    }))
    lst(g = (Tokens.`,`, clist))
  }
}