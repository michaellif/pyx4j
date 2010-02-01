/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Feb 1, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import java.util.Iterator;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.entity.test.shared.domain.Task;

public class PrimitiveSetTest extends InitializerTestCase {

    public void testManipulations() {
        Task task = EntityFactory.create(Task.class);
        assertEquals("Class of Value", String.class, task.notes().getValueClass());
        assertEquals("Class of Type", IPrimitiveSet.class, task.notes().getObjectClass());
        assertTrue("Initial value isNull", task.notes().isNull());
        assertTrue("Initial value Empty", task.notes().isEmpty());
        assertFalse("Initial iterator null safe", task.notes().iterator().hasNext());

        task.notes().add("Note1");
        assertFalse("Value isNull", task.notes().isNull());
        assertFalse("Value Empty", task.notes().isEmpty());
        assertTrue("Iterator", task.notes().iterator().hasNext());
        assertEquals("Set size", 1, task.notes().size());

        task.notes().add("Note2");
        assertEquals("Set size", 2, task.notes().size());
        assertEquals("Set size", 2, task.notes().getValue().size());

        assertTrue("contains(1)", task.notes().contains("Note1"));
        assertTrue("contains(2)", task.notes().contains("Note2"));

        Iterator<String> it = task.notes().iterator();
        assertEquals("iterator.hasNext() first", true, it.hasNext());
        String el1 = it.next();
        assertEquals("iterator.hasNext() second", true, it.hasNext());
        String el2 = it.next();
        assertEquals("iterator.hasNext()", false, it.hasNext());
        if (el1.equals("Note1")) {
            assertEquals("iterator. second()", "Note2", el2);
        } else {
            assertEquals("iterator. first()", "Note2", el1);
            assertEquals("iterator. second()", "Note1", el2);
        }

    }
}
