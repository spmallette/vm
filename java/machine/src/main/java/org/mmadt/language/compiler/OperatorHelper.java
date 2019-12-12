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

package org.mmadt.language.compiler;

import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.impl.composite.inst.map.AndInst;
import org.mmadt.machine.object.impl.composite.inst.map.DivInst;
import org.mmadt.machine.object.impl.composite.inst.map.EqInst;
import org.mmadt.machine.object.impl.composite.inst.map.GtInst;
import org.mmadt.machine.object.impl.composite.inst.map.GteInst;
import org.mmadt.machine.object.impl.composite.inst.map.LtInst;
import org.mmadt.machine.object.impl.composite.inst.map.LteInst;
import org.mmadt.machine.object.impl.composite.inst.map.MapInst;
import org.mmadt.machine.object.impl.composite.inst.map.MultInst;
import org.mmadt.machine.object.impl.composite.inst.map.NegInst;
import org.mmadt.machine.object.impl.composite.inst.map.OrInst;
import org.mmadt.machine.object.impl.composite.inst.map.PlusInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.Sym;
import org.mmadt.machine.object.model.ext.algebra.WithAnd;
import org.mmadt.machine.object.model.ext.algebra.WithDiv;
import org.mmadt.machine.object.model.ext.algebra.WithMinus;
import org.mmadt.machine.object.model.ext.algebra.WithMult;
import org.mmadt.machine.object.model.ext.algebra.WithOr;
import org.mmadt.machine.object.model.ext.algebra.WithOrder;
import org.mmadt.machine.object.model.ext.algebra.WithPlus;

import static org.mmadt.machine.object.impl.__.as;
import static org.mmadt.machine.object.impl.__.mult;
import static org.mmadt.machine.object.impl.__.plus;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class OperatorHelper {

    private OperatorHelper() {
        // static helper class
    }

    public static <A extends Obj> A applyBinary(final String operator, final A lhs, final Object rhs) {
        // System.out.println(lhs + " " + operator + " " + rhs);
        switch (operator) {
            case (Tokens.ASTERIX):
                return lhs instanceof Sym ? (A) as(lhs).mult(mult(rhs)) : (A) ((WithMult) lhs).mult(rhs);
            case (Tokens.CROSS):
                return lhs instanceof Sym ? (A) as(lhs).mult(plus(rhs)) : (A) ((WithPlus) lhs).plus(rhs);
            case (Tokens.BACKSLASH):
                return (A) ((WithDiv) lhs).div(rhs);
            case (Tokens.DASH):
                return (A) ((WithMinus) lhs).minus(rhs);
            case (Tokens.AMPERSAND):
                return (A) ((WithAnd) lhs).and(rhs);
            case (Tokens.BAR):
                return (A) ((WithOr) lhs).or((A) rhs);
            case (Tokens.RANGLE):
                return (A) ((WithOrder) lhs).gt(rhs);
            case (Tokens.LANGLE):
                return (A) ((WithOrder) lhs).lt(rhs);
            case (Tokens.REQUALS):
                return (A) ((WithOrder) lhs).gte(rhs);
            case (Tokens.LEQUALS):
                return (A) ((WithOrder) lhs).lte(rhs);
            case (Tokens.DEQUALS):
                return (A) lhs.eq(rhs);
            case (Tokens.MAPSTO):
                return lhs.mapTo((A) rhs);
            case (Tokens.MAPSFROM):
                return lhs.mapFrom((A) rhs);
            case (Tokens.LPACK):
                return (A) TRec.of(rhs, lhs);
            case Tokens.RPACK:
                return (A) TRec.of(lhs, rhs);
            default:
                throw new RuntimeException("Unknown operator: " + operator);
        }
    }

    public static Obj applyUnary(final String operator, final Obj rhs) {
        switch (operator) {
            case (Tokens.ASTERIX):
                return mult(rhs);
            case (Tokens.CROSS):
                return plus(rhs);
            case (Tokens.DASH):
                return rhs instanceof WithMinus ? ((WithMinus) rhs).neg() : MapInst.create(rhs).mult(NegInst.create());
            case (Tokens.BACKSLASH):
                return DivInst.create(rhs);
            case (Tokens.AMPERSAND):
                return AndInst.create(rhs);
            case (Tokens.BAR):
                return OrInst.create(rhs);
            case (Tokens.RANGLE):
                return GtInst.create(rhs);
            case (Tokens.LANGLE):
                return LtInst.create(rhs);
            case (Tokens.REQUALS):
                return GteInst.create(rhs);
            case (Tokens.LEQUALS):
                return LteInst.create(rhs);
            case (Tokens.DEQUALS):
                return EqInst.create(rhs);
            default:
                throw new RuntimeException("Unknown operator: " + operator);
        }
    }
}
