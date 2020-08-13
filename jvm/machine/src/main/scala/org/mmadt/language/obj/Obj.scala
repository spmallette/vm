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

import org.mmadt.language.obj.Obj.{IntQ, ViaTuple}
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.branch._
import org.mmadt.language.obj.op.filter.IsOp
import org.mmadt.language.obj.op.initial.StartOp
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.reduce.{CountOp, FoldOp, SumOp}
import org.mmadt.language.obj.op.sideeffect.{ErrorOp, LoadOp}
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.language.obj.op.trace._
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{strm => _, _}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.processor.Processor
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Obj
  extends AOp
    with AsOp
    with AndOp
    with BranchOp
    with OrOp
    with CountOp
    with SumOp
    with DefineOp
    with DefsOp
    with IdOp
    with IsOp
    with FoldOp
    with MapOp
    with ModelOp
    with NotOp
    with JuxtOp
    with FromOp
    with LoadOp
    with QOp
    with ErrorOp
    with EvalOp
    with EqsOp
    with ToOp
    with PathOp
    with StartOp
    with SplitOp
    with RewriteOp
    with RepeatOp[Obj] {

  //////////////////////////////////////////////////////////////
  // data associated with every obj
  val name: String // the obj type name
  val q: IntQ // the obj quantifier
  val via: ViaTuple // the obj's incoming edge in the obj-graph
  //////////////////////////////////////////////////////////////

  // type methods
  def named(name: String): this.type = {
    LanguageException.checkAnonymousTypeName(this, name)
    this.clone(name = name)
  }
  def <=[D <: Obj](domainType: D): this.type = {
    if (domainType.range.equals(this)) domainType.asInstanceOf[this.type]
    else if (domainType.root) this.clone(via = (domainType, NoOp()))
    else this.clone(via = (domainType.rinvert, domainType.via._2))
  }
  // obj path methods
  def model: Model = Option(this.domainObj.via._1).getOrElse(ModelOp.EMPTY).asInstanceOf[Model]
  lazy val rangeObj: this.type = this.clone(q = this.q, via = this.domainObj.via)
  lazy val domainObj: Obj = if (this.root) this else this.via._1.domainObj
  lazy val trace: List[(Obj, Inst[Obj, Obj])] = if (this.root) Nil else this.via._1.trace :+ this.via.asInstanceOf[(Obj, Inst[Obj, Obj])]
  def root: Boolean = null == this.via || null == this.via._2
  def range: Type[Obj] = asType(this.rangeObj)
  def domain: Type[Obj] = asType(this.domainObj)
  def via(obj: Obj, inst: Inst[_ <: Obj, _ <: Obj]): this.type = this.clone(q = if (this.alive) obj.q.mult(inst.q) else qZero, via = (obj, inst))
  def rinvert[R <: Obj]: R = if (this.root) throw LanguageException.zeroLengthPath(this) else this.via._1.asInstanceOf[R]
  def linvert: this.type = {
    if (this.root) throw LanguageException.zeroLengthPath(this)
    this.trace.tail match {
      case Nil => this.rangeObj
      case incidentRoot => incidentRoot.foldLeft[Obj](incidentRoot.head._1.rangeObj)((btype, inst) => inst._2.exec(btype)).asInstanceOf[this.type]
    }
  }

  // quantifier methods
  def q(f: IntQ => IntQ): this.type = this.q(f.apply(this.q))
  def q(single: IntValue): this.type = this.q(single.q(qOne), single.q(qOne))
  def q(q: IntQ): this.type = if (q.equals(qZero)) this.rangeObj.clone(q = qZero) else this.clone(
    q = if (this.root) q else this.q.mult(q),
    via = if (this.root) this.via else (this.via._1, this.via._2.q(q)))
  def hardQ(f: IntQ => IntQ): this.type = this.hardQ(f.apply(this.q))
  def hardQ(q: IntQ): this.type = this.clone(q = q)
  def hardQ(single: IntValue): this.type = this.hardQ(single.q(qOne), single.q(qOne))
  def pureQ: IntQ = divQ(this.q, this.domainObj.q)
  lazy val alive: Boolean = this.q != qZero


  // utility methods
  def test(other: Obj): Boolean
  def clone(name: String = this.name, g: Any = null, q: IntQ = this.q, via: ViaTuple = this.via): this.type
  def toStrm: Strm[this.type] = strm[this.type](Seq[this.type](this)).asInstanceOf[Strm[this.type]]

  def compute[E <: Obj](rangeType: E): E = AsOp.autoAsType(this, x => Obj.internal(x, rangeType), rangeType)

  def ==>[E <: Obj](target: E): E = {
    if (!target.alive) return zeroObj.asInstanceOf[E]
    target match {
      case _: Value[_] => target.hardQ(q => multQ(this.q, q))
      case rangeType: Type[E] =>
        LanguageException.testTypeCheck(this, target.domain)
        this match {
          case _: Value[_] => AsOp.autoAsType(this.update(target.model), x => Processor.iterator(x, rangeType), rangeType)
          case _: Type[_] => AsOp.autoAsType(this.update(target.model), x => Processor.compiler(x, rangeType), rangeType)
        }
    }
  }

  // lst fluent methods
  final def `|`: Lst[this.type] = lst(Tokens.|, this).asInstanceOf[Lst[this.type]]
  def |(obj: Obj): Lst[obj.type] = lst(g = (Tokens.`|`, List(this.asInstanceOf[obj.type], obj)))
  final def `;`: Lst[this.type] = lst(Tokens.`;`, this).asInstanceOf[Lst[this.type]]
  def `;`(obj: Obj): Lst[obj.type] = lst(g = (Tokens.`;`, List(this.asInstanceOf[obj.type], obj)))
  final def `,`: Lst[this.type] = lst(Tokens.`,`, this).asInstanceOf[Lst[this.type]]
  def `,`(obj: Obj): Lst[obj.type] = lst(g = (Tokens.`,`, List(this.asInstanceOf[obj.type], obj)))

}

object Obj {
  type IntQ = (IntValue, IntValue)
  type ViaTuple = (Obj, Inst[_ <: Obj, _ <: Obj])
  val rootVia: ViaTuple = (null, null)

  private def internal[E <: Obj](domainObj: Obj, rangeType: E): E = {
    rangeType match {
      case _: Value[E] => rangeType.q(multQ(domainObj.q, rangeType.q))
      case _: Type[E] =>
        rangeType.trace
          .headOption
          .map(x => x._2.exec(domainObj))
          .map(x => Obj.internal(x, rangeType.linvert))
          .getOrElse(domainObj.asInstanceOf[E])
    }
  }

  @inline implicit def booleanToBool(ground: Boolean): BoolValue = bool(ground)
  @inline implicit def longToInt(ground: Long): IntValue = int(ground)
  @inline implicit def intToInt(ground: scala.Int): IntValue = int(ground.longValue())
  @inline implicit def doubleToReal(ground: scala.Double): RealValue = real(ground)
  @inline implicit def floatToReal(ground: scala.Float): RealValue = real(ground)
  @inline implicit def stringToStr(ground: String): StrValue = str(ground)
  @inline implicit def tupleToRichTuple[A <: Obj, B <: Obj](ground: Tuple2[A, B]): Rec[A, B] = rec(g = (Tokens.`,`, Map(ground))) //new RichTuple2[A, B](ground)
  @inline implicit def richTupleToRec[A <: Obj, B <: Obj](ground: RichTuple2[A, B]): Rec[A, B] = rec(g = (Tokens.`,`, Map(ground.tuple)))
  class RichTuple2[A <: Obj, B <: Obj](val tuple: Tuple2[A, B]) {
    final def `_,`(next: Tuple2[A, _]): Rec[A, B] = this.`,`(next)
    final def `_;`(next: Tuple2[A, B]): Rec[A, B] = this.`;`(next)
    final def `_|`(next: Tuple2[A, B]): Rec[A, B] = this.`|`(next)
    final def `,`: Rec[A, B] = rec(g = (Tokens.`,`, Map(this.tuple._1 -> this.tuple._2)))
    final def `,`(next: Tuple2[A, _]): Rec[A, B] = this.recMaker(Tokens.`,`, next)
    final def `;`(next: Tuple2[A, B]): Rec[A, B] = this.recMaker(Tokens.`;`, next)
    final def `|`(next: Tuple2[A, B]): Rec[A, B] = this.recMaker(Tokens.`|`, next)
    private final def recMaker(sep: String, tuple: Tuple2[A, _]): Rec[A, B] = rec(g = (sep, Map(this.tuple, tuple.asInstanceOf[Tuple2[A, B]])))
  }
}
