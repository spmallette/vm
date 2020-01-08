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

package org.mmadt.machine.object.impl.composite.inst.map;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.inst.MapInstruction;
import org.mmadt.machine.object.model.composite.util.PList;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TypeInst<S extends Obj> extends TInst<S, S> implements MapInstruction<S, S> {

    private TypeInst() {
        super(PList.of(Tokens.TYPE));
    }

    public S apply(final S obj) {
        return this.quantifyRange(obj.type());
    }

    public static <S extends Obj> TypeInst<S> create() {
        return new TypeInst<>();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static <S extends Obj> S compute(final S obj) {
        return !obj.isReference() ?
                obj.type().copy(obj) :
                TypeInst.<S>create().attach(obj, obj.type().copy(obj));
    }
}