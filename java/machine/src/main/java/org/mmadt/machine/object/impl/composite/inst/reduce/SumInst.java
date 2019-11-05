/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.machine.object.impl.composite.inst.reduce;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.inst.ReduceInstruction;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.algebra.WithOrderedRing;
import org.mmadt.machine.object.model.util.ObjectHelper;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class SumInst<S extends WithOrderedRing<S>> extends TInst implements ReduceInstruction<S, S> {

    public SumInst() {
        super(PList.of(Tokens.SUM));
    }

    @Override
    public S apply(final S current, final S obj) {
        return current.plus(obj).mult(obj.<S>q().peek());
    }

    @Override
    public S merge(final S valueA, final S valueB) {
        return valueA.plus(valueB);
    }

    @Override
    public S getInitialValue() {
        return (S) this.q().peek().zero();
    }

    public static <S extends Obj> S create(final S source) {
        return ObjectHelper.allInstances(source) ?
                (S) source.q().peek() :
                source.set(null).q(source.q().one()).access(source.access().mult(new SumInst<>()));
    }
}