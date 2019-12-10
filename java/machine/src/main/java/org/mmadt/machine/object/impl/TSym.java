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

package org.mmadt.machine.object.impl;

import org.mmadt.machine.object.model.Obj;

import java.util.Objects;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TSym extends TObj {

    public static TSym of(final String symbol) {
        return new TSym(symbol);
    }

    private TSym(final String symbol) {
        super(null);
        this.type = TType.of(symbol).label(symbol);
    }

    @Override
    public boolean test(final Obj obj) {
        return (null != obj.symbol() && obj.symbol().equals(this.symbol()));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.type.symbol());
    }

    @Override
    public boolean equals(final Object object) {
        return object instanceof TSym && Objects.equals(this.symbol(), ((TSym) object).symbol());
    }

    @Override
    public String toString() {
        return this.symbol();
    }
}
