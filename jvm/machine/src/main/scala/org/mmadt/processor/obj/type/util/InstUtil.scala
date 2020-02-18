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

package org.mmadt.processor.obj.`type`.util

import org.mmadt.language.obj._
import org.mmadt.processor.Traverser
import org.mmadt.storage.obj.int

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object InstUtil {

  private def valueArgs[S <: Obj,E <: Obj](traverser:Traverser[S],inst:Inst):List[Obj] ={
    inst.args().map{
      case valueArg:OValue => valueArg
      case typeArg:OType => traverser.split(traverser.obj() match {
        case atype:OType => atype.range()
        case avalue:OValue => avalue
      }).apply(typeArg).obj()
    }
  }

  /**
   * Before an instruction is applied, its arguments are computing by a split of the incoming traverser
   */
  def instEval[S <: Obj,E <: Obj](traverser:Traverser[S],inst:Inst):Traverser[E] =
    traverser.split(inst.apply(traverser.obj(),InstUtil.valueArgs(traverser,inst)).asInstanceOf[E])

  @scala.annotation.tailrec
  def createInstList(list:List[(OType,Inst)],atype:OType):List[(OType,Inst)] ={
    if (atype.insts().isEmpty) list else createInstList(List((atype.range(),atype.insts().last._2)) ++ list,atype.insts().last._1)
  }

  def nextInst(atype:OType):Option[Inst] = atype.insts() match {
    case Nil => None
    case x => Some(x.head._2)
  }

  def updateQ[O <: Obj](obj:Obj,atype:OType):O = atype.q() match {
    case _ if ==(int(1),int(1)) => obj.asInstanceOf[O]
    case tq:TQ => obj.q(obj.q()._1 * tq._1,obj.q()._2 * tq._2).asInstanceOf[O]
  }
}
