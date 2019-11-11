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

package org.mmadt.machine.object.impl.composite.inst.branch;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.inst.BranchInstruction;
import org.mmadt.machine.object.model.type.PList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class BranchInst<S extends Obj, E extends Obj> extends TInst implements BranchInstruction<S, E> {

    private Map<Inst, List<Inst>> branches;

    private BranchInst(final Map<Inst, List<Inst>> branches, final Object... args) {
        super(PList.of(args));
        this.<PList<Obj>>get().add(0, TStr.of(Tokens.BRANCH));
        this.branches = branches;
    }

    @Override
    public Map<Inst, List<Inst>> getBranches() {
        return this.branches;
    }

    public static <S extends Obj, E extends Obj> BranchInst<S, E> create(final Object... a) {
        final List<Inst> args = Stream.of(a).map(x -> (Inst) x).collect(Collectors.toList());
        final Map<Inst, List<Inst>> branches = new LinkedHashMap<>();
        for (int i = 0; i < args.size(); i = i + 2) {
            final Inst predicate = args.get(i).isZero() ? null : args.get(i);
            final Inst branch = args.get(i + 1);
            branches.computeIfAbsent(predicate, p -> new ArrayList<>()).add(branch);
        }
        return new BranchInst<>(branches, args.toArray(new Object[]{}));
    }
}