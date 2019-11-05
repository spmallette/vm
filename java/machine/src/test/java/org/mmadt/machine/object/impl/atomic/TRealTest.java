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

package org.mmadt.machine.object.impl.atomic;

import org.junit.jupiter.api.Test;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.impl.util.TestHelper;
import org.mmadt.machine.object.model.atomic.Real;
import org.mmadt.util.IteratorUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mmadt.language.__.gt;
import static org.mmadt.language.__.start;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TRealTest {

    @Test
    void testInstanceReferenceType() {
        Real instance = TReal.of(23.4f);
        Real reference = TReal.of(1.46f,13.02f).plus(TReal.of(2.0f)).div(TReal.of(1.4f));
        Real type = TReal.some();
        TestHelper.validateKinds(instance, reference, type);
        //////
        instance = TReal.of(41.3f).q(2);
        reference = TReal.of(23.0f, 56.0f, 11.0f);
        type = TReal.of().q(1,45);
        TestHelper.validateKinds(instance, reference, type);
    }

    @Test
    void shouldStreamCorrectly() {
        assertEquals(TInst.ids(), TReal.of(1.0f).access());
        assertEquals(TReal.of(1.0f, 2.0f, 3.0f, 4.0f).q(4), TReal.of(1.0f, 2.0f, 3.0f, 4.0f));
        assertEquals(TLst.of(1.0f, 2.0f, 3.0f, 4.0f).<List<Real>>get(), IteratorUtils.list(TReal.of(1.0f, 2.0f, 3.0f, 4.0f).iterable().iterator()));
    }
}