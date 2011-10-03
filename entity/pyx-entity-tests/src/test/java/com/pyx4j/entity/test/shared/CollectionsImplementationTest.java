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
 * Created on Jan 24, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import java.util.Date;

import junit.framework.Assert;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Status;
import com.pyx4j.entity.test.shared.domain.Task;
import com.pyx4j.entity.test.shared.domain.inherit.Base1Entity;
import com.pyx4j.entity.test.shared.domain.inherit.Concrete1Entity;
import com.pyx4j.entity.test.shared.domain.inherit.Concrete2Entity;
import com.pyx4j.entity.test.shared.domain.inherit.RefferenceEntity;

public class CollectionsImplementationTest extends InitializerTestCase {

    public void testSetCreation() {

        Employee emp = EntityFactory.create(Employee.class);
        emp.firstName().setValue("Bob");

        Task task1 = EntityFactory.create(Task.class);
        Date today = new Date();
        task1.deadLine().setValue(today);
        task1.status().setValue(Status.DEACTIVATED);

        assertTrue("added 1", emp.tasks().add(task1));

        Task task2 = EntityFactory.create(Task.class);
        task2.status().setValue(Status.ACTIVE);

        assertFalse("contains 2", emp.tasks().contains(task2));
        assertTrue("added 2", emp.tasks().add(task2));

        assertEquals("Set size", 2, emp.tasks().size());
        assertTrue("contains 1", emp.tasks().contains(task1));
        assertTrue("contains 2", emp.tasks().contains(task2));
    }

    public void testListCreation() {

        Employee emp = EntityFactory.create(Employee.class);
        emp.firstName().setValue("Bob");

        Task task1 = EntityFactory.create(Task.class);
        Date today = new Date();
        task1.deadLine().setValue(today);
        task1.status().setValue(Status.DEACTIVATED);

        assertTrue("added 1", emp.tasksSorted().add(task1));

        Task task2 = EntityFactory.create(Task.class);
        task2.status().setValue(Status.ACTIVE);

        assertFalse("contains 2", emp.tasksSorted().contains(task2));
        assertTrue("added 2", emp.tasksSorted().add(task2));

        assertEquals("Set size", 2, emp.tasksSorted().size());
        assertTrue("contains 1", emp.tasksSorted().contains(task1));
        assertTrue("contains 2", emp.tasksSorted().contains(task2));

        assertEquals("get(0)", task1, emp.tasksSorted().get(0));
        assertEquals("get(1)", task2, emp.tasksSorted().get(1));

        // List can contain the same element more then one time.
        assertTrue("added 1 again", emp.tasksSorted().add(task1));
        assertEquals("Set size", 3, emp.tasksSorted().size());
        assertTrue("contains 3", emp.tasksSorted().contains(task1));
    }

    public void testSetMemberRefference() {
        Employee emp = EntityFactory.create(Employee.class);

        Task task1 = EntityFactory.create(Task.class);
        emp.tasks().add(task1);
        Task task1ref = emp.tasks().iterator().next();

        Date today = new Date();
        task1.deadLine().setValue(today);
        task1.status().setValue(Status.DEACTIVATED);

        assertEquals("deadLine", task1.deadLine(), task1ref.deadLine());
        assertEquals("status", task1.status(), task1ref.status());
    }

    public void testAbstractSetMember() {
        Concrete2Entity ent1 = EntityFactory.create(Concrete2Entity.class);

        RefferenceEntity ent2 = EntityFactory.create(RefferenceEntity.class);

        ent2.refferences().add(ent1);

        assertEquals("collection size", 1, ent2.refferences().size());
        Base1Entity item = ent2.refferences().iterator().next();

        assertTrue("item data type " + item.getClass(), item instanceof Concrete2Entity);

        ent2.refferences().remove(ent1);
        assertEquals("empty collection size", 0, ent2.refferences().size());
    }

    public void testPolymorphicRemoval() {

        Concrete1Entity ent1 = EntityFactory.create(Concrete1Entity.class);
        ent1.setPrimaryKey(new Key(1));

        // The same ID of different type
        Concrete2Entity ent2 = EntityFactory.create(Concrete2Entity.class);
        ent2.setPrimaryKey(new Key(1));

        RefferenceEntity ent = EntityFactory.create(RefferenceEntity.class);
        ent.refferences().add(ent1);
        ent.refferences().add(ent2);
        Assert.assertEquals("Item was added", 2, ent.refferences().size());

        Concrete1Entity ent1x = EntityFactory.create(Concrete1Entity.class);
        ent1x.setPrimaryKey(new Key(1));

        ent.refferences().remove(ent1x);
        Assert.assertEquals("Item was removed", 1, ent.refferences().size());

        Assert.assertEquals("Proper item reamins", ent2, ent.refferences().get(0));

    }
}
