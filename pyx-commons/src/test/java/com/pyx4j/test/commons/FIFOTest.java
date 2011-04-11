/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2011-04-11
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.test.commons;

import java.util.Iterator;

import junit.framework.TestCase;

import com.pyx4j.commons.FIFO;

public class FIFOTest extends TestCase {

    private static final int SIZE = 5;

    private final FIFO<String> fifo = new FIFO<String>(SIZE);

    @Override
    protected void setUp() throws Exception {
    }

    public void testFillFIFO() {
        fifo.push("Object1");
        fifo.push("Object2");
        fifo.push("Object3");
        assertEquals(3, fifo.size());
        fifo.push("Object4");
        fifo.push("Object5");
        assertEquals(5, fifo.size());
        fifo.push("Object6");
        assertEquals(5, fifo.size());
        assertFalse(fifo.contains("Object1"));
    }

    public void testFIFOIterator() {
        fifo.push("Object1");
        fifo.push("Object2");
        fifo.push("Object3");
        fifo.push("Object4");
        fifo.push("Object5");
        fifo.push("Object6");
        Iterator<String> iter = fifo.iterator();
        assertEquals("Object2", iter.next());
        assertEquals("Object3", iter.next());
        assertEquals("Object4", iter.next());
        assertEquals("Object5", iter.next());
        assertEquals("Object6", iter.next());
        assertFalse(iter.hasNext());
        fifo.push("Object7");
        fifo.push("Object8");
        iter = fifo.iterator();

        assertEquals("Object4", iter.next());
        assertEquals("Object5", iter.next());
        assertEquals("Object6", iter.next());
        assertEquals("Object7", iter.next());
        assertEquals("Object8", iter.next());
        assertFalse(iter.hasNext());
    }

}
