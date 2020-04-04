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
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.RealType
import org.mmadt.storage.StorageFactory.qOne

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TReal(name:String,quantifier:IntQ,via:DomainInst[Real]) extends AbstractTObj[Real](name,quantifier,via) with RealType {
  def this() = this(Tokens.real,qOne,base())
  def this(name:String) = this(name,qOne,base())
  override def hardQ(quantifier:IntQ):this.type = new TReal(name,quantifier,via).asInstanceOf[this.type]
  override def q(quantifier:IntQ):this.type = new TReal(name,multQ(via._1,quantifier),(via._1,via._2.q(quantifier))).asInstanceOf[this.type]

}
