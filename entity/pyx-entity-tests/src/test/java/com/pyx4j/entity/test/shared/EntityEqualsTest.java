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
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Status;
import com.pyx4j.entity.test.shared.domain.Task;

public class EntityEqualsTest extends InitializerTestCase {

    static public void assertNotEquals(String message, long expected, long actual) {
        if (expected != actual) {
            return;
        } else {
            Assert.failNotEquals(message, expected, actual);
        }
    }

    public void testNewEntity() {

        Task task1 = EntityFactory.create(Task.class);

        Task task2 = EntityFactory.create(Task.class);
        assertNotEquals("new Items should have diferent hashCode", task1.hashCode(), task2.hashCode());

        long nullEntityHashCode = task1.hashCode();
        task1.status().setValue(Status.ACTIVE);
        assertEquals("hashCode should not change on non PK values", nullEntityHashCode, task1.hashCode());

        task2.status().setValue(Status.ACTIVE);

        assertFalse("new Items are diferent", task1.equals(task2));
        assertNotEquals("new Items should have diferent hashCode", task1.hashCode(), task2.hashCode());
    }

    public void testSameValue() {

        Task task1 = EntityFactory.create(Task.class);
        task1.status().setValue(Status.ACTIVE);

        Task task2 = EntityFactory.create(Task.class);
        task2.set(task1);

        assertTrue("copied Items have same value", task1.equals(task2));
        assertEquals("copied Items have same hashCode", task1.hashCode(), task2.hashCode());
    }

    public void testEqualsWithSet() {

        Department department1 = EntityFactory.create(Department.class);
        Employee employee1 = EntityFactory.create(Employee.class);
        employee1.setPrimaryKey(new Key(1));
        department1.employees().add(employee1);
        department1.setPrimaryKey(new Key(11));

        Department department2 = EntityFactory.create(Department.class);
        Employee employee2 = EntityFactory.create(Employee.class);
        employee2.setPrimaryKey(new Key(2));
        department2.employees().add(employee2);
        department2.setPrimaryKey(new Key(11));

        assertEquals("same key", department1, department2);
        assertEquals("same key same hashCode", department1.hashCode(), department2.hashCode());
    }

    public void testFullyEqual() {
        Task t1 = EntityFactory.create(Task.class);
        t1.setPrimaryKey(new Key(22));
        t1.description().setValue("Task1");
        t1.notes().add("Note 1");
        t1.notes().add("Note 2");
        t1.oldStatus().add(Status.SUSPENDED);

        Task t2 = EntityFactory.create(Task.class);
        t2.setPrimaryKey(new Key(22));
        t2.description().setValue("Task1");
        t2.notes().add("Note 1");
        t2.notes().add("Note 2");
        t2.oldStatus().add(Status.SUSPENDED);

        assertTrue("Not Same data\n" + t1.toString() + "\n!=\n" + t2.toString(), EntityGraph.fullyEqual(t1, t2));

        t2.status().setValue(Status.ACTIVE);
        assertFalse("Same data\n" + t1.toString() + "\n!=\n" + t2.toString(), EntityGraph.fullyEqual(t1, t2));

        t1.status().setValue(Status.ACTIVE);

        t2.notes().clear();
        t2.notes().add("Note 2");
        t2.notes().add("Note 1");
        assertTrue("Not Same data Set Order\n" + t1.toString() + "\n!=\n" + t2.toString(), EntityGraph.fullyEqual(t1, t2));

        t2.notes().clear();
        t2.notes().add("Note X");
        assertFalse("Data should be different\n" + t1.toString() + "\n!=\n" + t2.toString(), EntityGraph.fullyEqual(t1, t2));
    }

    public void testBusinessEqual() {
        Task t1 = EntityFactory.create(Task.class);
        t1.setPrimaryKey(new Key(22));
        t1.description().setValue("Task1");

        Task t2 = EntityFactory.create(Task.class);
        t2.description().setValue("Task1");
        assertTrue("Not Same business data\n" + t1.toString() + "\n!=\n" + t2.toString(), t1.businessEquals(t2));

        t2.deadLine().setValue(new Date());
        assertFalse("Same business data\n" + t1.toString() + "\n!=\n" + t2.toString(), t1.businessEquals(t2));
    }
}
