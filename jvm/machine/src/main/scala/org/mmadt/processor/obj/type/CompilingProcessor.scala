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

package org.mmadt.processor.obj.`type`

import org.mmadt.language.model.{Model, SimpleModel}
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.{Type, TypeChecker}
import org.mmadt.language.obj.value.Value
import org.mmadt.processor.obj.`type`.util.InstUtil
import org.mmadt.processor.{Processor, Traverser}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class CompilingProcessor[S <: Obj, E <: Obj](val model: Model = new SimpleModel) extends Processor[S, E] {
  override def apply(startObj: S, endType: E with Type[_]): Iterator[Traverser[E]] = {
    if (startObj.isInstanceOf[Value[_]]) throw new IllegalArgumentException("The compiling processor only accepts types: " + startObj)
    var mutatingType: E with Type[_] = endType
    var mutatingTraverser: Traverser[Obj] = new C1Traverser(startObj)
    /////
    while (mutatingType.insts() != Nil) {
      TypeChecker.checkType(mutatingTraverser.obj(), mutatingType)
      var length: scala.Int = 1
      mutatingTraverser = model.get(mutatingTraverser.obj().asInstanceOf[Type[_]].pure(), mutatingType) match {
        case Some(rewrite) =>
          length = mutatingType.insts().length
          mutatingTraverser.apply(rewrite.asInstanceOf[E with Type[_]])
        case None => mutatingTraverser.split(mutatingType.insts().head._2.apply(mutatingTraverser.obj(), InstUtil.valueArgs(mutatingTraverser, mutatingType.insts().head._2)))
      }
      for (_ <- 0 until length) mutatingType = mutatingType.pop()
      TypeChecker.checkType(mutatingTraverser.obj(), mutatingType)
    }
    Iterator(mutatingTraverser.asInstanceOf[Traverser[E]])
  }
}