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

package org.mmadt.machine.object.impl.composite.inst.sideeffect;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.inst.SideEffectInstruction;
import org.mmadt.machine.object.model.type.PList;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class ExplainInst<S extends Obj> extends TInst implements SideEffectInstruction<S> {

    private ExplainInst(final Object key) {
        super(PList.of(Tokens.EXPLAIN, key));
    }

    @Override
    public void accept(final S obj) {
        System.out.println("EXPLAIN: " + this.args().get(0) + "\n");
        printInst(0,this.args().get(0).access());

    }

    private final void printInst(int indent, final Inst inst) {
        for (final Inst i : inst.iterable()) {
            System.out.println(pad("",indent) + pad(i + ":", 50-indent) + pad(i.domain(), 30) + " -> " + i.range());
            for (final Obj x : i.args()) {
                if (x instanceof Inst) {
                    printInst((1+indent)*2,(Inst) x);
                }
            }
        }
    }

    private final String pad(final Object object, final int padding) {
        String result = object.toString();
        final int length = result.length();
        for (int i = 0; i < padding - length; i++) {
            result = result + " ";
        }
        return result;
    }

    public static <S extends Obj> ExplainInst<S> create(final Object arg) {
        return new ExplainInst<>(arg);
    }


}