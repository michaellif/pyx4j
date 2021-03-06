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
 */
package com.pyx4j.entity.test.shared;

import java.util.Date;

import org.junit.Assert;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.shared.utils.EntityGraph.EntityGraphEqualOptions;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Status;
import com.pyx4j.entity.test.shared.domain.Task;
import com.pyx4j.entity.test.shared.domain.bidir.Master;
import com.pyx4j.entity.test.shared.domain.equal.WithPersonalIdentity;
import com.pyx4j.entity.test.shared.domain.inherit.Base1Entity;
import com.pyx4j.entity.test.shared.domain.inherit.Base2Entity;
import com.pyx4j.entity.test.shared.domain.inherit.Concrete1Entity;
import com.pyx4j.entity.test.shared.domain.inherit.Concrete2Entity;
import com.pyx4j.entity.test.shared.domain.inherit.ReferenceEntity;

public class EntityEqualsTest extends InitializerTestBase {

    public void testNewEntity() {

        Task task1 = EntityFactory.create(Task.class);

        Task task2 = EntityFactory.create(Task.class);
        Assert.assertNotSame("new Items should have different hashCode", task1.hashCode(), task2.hashCode());

        long nullEntityHashCode = task1.hashCode();
        task1.status().setValue(Status.ACTIVE);
        assertEquals("hashCode should not change on non PK values", nullEntityHashCode, task1.hashCode());

        task2.status().setValue(Status.ACTIVE);

        assertFalse("new Items are different", task1.equals(task2));
        Assert.assertNotSame("new Items should have different hashCode", task1.hashCode(), task2.hashCode());
    }

    public void testSameValue() {

        Task task1 = EntityFactory.create(Task.class);
        task1.status().setValue(Status.ACTIVE);

        Task task2 = EntityFactory.create(Task.class);
        task2.set(task1);

        assertTrue("copied Items have same value", task1.equals(task2));
        assertEquals("copied Items have same hashCode", task1.hashCode(), task2.hashCode());
    }

    public void testTemplateEntityEqual() {
        assertFalse("compare Prototypes", EntityFactory.getEntityPrototype(Base1Entity.class).equals(EntityFactory.getEntityPrototype(Base2Entity.class)));
    }

    public void testPolymorphicEqual() {
        Base1Entity ent1b = EntityFactory.create(Base1Entity.class);
        Concrete1Entity ent1 = EntityFactory.create(Concrete1Entity.class);
        ent1.setPrimaryKey(new Key(1));
        ent1b.set(ent1);

        Base1Entity ent2b = EntityFactory.create(Base1Entity.class);
        Concrete2Entity ent2 = EntityFactory.create(Concrete2Entity.class);
        ent2.setPrimaryKey(new Key(1));
        ent2b.set(ent2);

        assertFalse("Items of different type are different", ent1b.equals(ent2b));

        // N.B. Value comparison result is undefined in general case!
        assertFalse("Items of different type are different", ent1b.getValue().equals(ent2b.getValue()));
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

    public void testFullyEqualEmptyStrings() {
        Task t1 = EntityFactory.create(Task.class);
        t1.setPrimaryKey(new Key(22));
        t1.description().setValue("");

        Task t2 = EntityFactory.create(Task.class);
        t2.setPrimaryKey(new Key(22));
        t2.description().setValue(null);

        assertTrue("Not Same data\n" + t1.toString() + "\n!=\n" + t2.toString(), EntityGraph.fullyEqual(t1, t2));
    }

    public void testFullyEqualWithTransientMember() {
        Department d1 = EntityFactory.create(Department.class);
        d1.setPrimaryKey(new Key(11));

        Department d2 = EntityFactory.create(Department.class);
        d2.setPrimaryKey(new Key(11));

        assertTrue("Same data\n" + d1.toString() + "\n!=\n" + d2.toString(), EntityGraph.fullyEqual(d1, d2));

        d1.transientStuff().setValue("V1");
        d2.transientStuff().setValue("V2");

        assertTrue("Same data\n" + d1.toString() + "\n!=\n" + d2.toString(), EntityGraph.fullyEqual(d1, d2));
    }

    public void testFullyEqualWithPolymorphicEntity() {
        Concrete2Entity ent1 = EntityFactory.create(Concrete2Entity.class);
        ent1.setPrimaryKey(new Key(1));

        Concrete1Entity ent11 = EntityFactory.create(Concrete1Entity.class);
        ent1.reference().set(ent11);
        ent11.setPrimaryKey(new Key(11));
        ent11.nameB1().setValue("B1.1");
        ent11.nameC1().setValue("C1.1");

        Concrete2Entity ent2 = EntityFactory.create(Concrete2Entity.class);
        ent2.setPrimaryKey(new Key(1));

        Concrete1Entity ent21 = EntityFactory.create(Concrete1Entity.class);
        ent2.reference().set(ent21);
        ent21.setPrimaryKey(new Key(11));
        ent21.nameB1().setValue("B1.1");
        ent21.nameC1().setValue("C1.1");

        assertTrue("Same data\n" + ent1.toString() + "\n!=\n" + ent2.toString(), EntityGraph.fullyEqual(ent1, ent2));

        ent21.nameC1().setValue("C1.1x");
        assertFalse("Data should be different\n" + ent1.toString() + "\n!=\n" + ent2.toString(), EntityGraph.fullyEqualValues(ent1, ent2));

        assertTrue("Owned Data should be same\n" + ent1.toString() + "\n==\n" + ent2.toString(), EntityGraph.fullyEqual(ent1, ent2));
    }

    public void testFullyEqualIsDirtyPersonalIdentity() {
        // CForm isDirty()
        EntityGraphEqualOptions options = new EntityGraphEqualOptions(false);
        options.ignoreTransient = false;
        options.ignoreRpcTransient = true;
        options.trace = true;

        WithPersonalIdentity ent1 = EntityFactory.create(WithPersonalIdentity.class);

        WithPersonalIdentity ent2 = EntityFactory.create(WithPersonalIdentity.class);
        ent2.identity().newNumber().setValue("1234");

        assertFalse("Data should be different\n" + ent1.toString() + "\n!=\n" + ent2.toString(), EntityGraph.fullyEqual(ent1, ent2, options));
    }

    public void testFullyEqualIsDirtyWithOwner() {
        EntityGraphEqualOptions options = new EntityGraphEqualOptions(false);
        options.ignoreTransient = false;
        options.ignoreRpcTransient = true;
        options.trace = true;

        Master ent1 = EntityFactory.create(Master.class);
        ent1.name().setValue("V1");

        Master ent2 = EntityFactory.create(Master.class);
        ent2.name().setValue("V1");
        // init owner
        ent2.child().master();

        assertTrue("Data should be same\n" + ent1.toString() + "\n!=\n" + ent2.toString(), EntityGraph.fullyEqual(ent1, ent2, options));

        ent2.name().setValue("V2");
        assertFalse("Data should be different\n" + ent1.toString() + "\n!=\n" + ent2.toString(), EntityGraph.fullyEqual(ent1, ent2, options));

        ent2.name().setValue("V1");
        ent2.child().name().setValue("C1");
        assertFalse("Data should be different\n" + ent1.toString() + "\n!=\n" + ent2.toString(), EntityGraph.fullyEqual(ent1, ent2, options));
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

    public void testIsEmpty() {
        Task t1 = EntityFactory.create(Task.class);
        assertTrue("Initially Empty", t1.isEmpty());

        t1.setPrimaryKey(new Key(22));
        assertTrue("still Empty when just PK is set", t1.isEmpty());

        t1.description().setValue(null);
        assertTrue("still Empty when value set but it is null", t1.isEmpty());
    }

    public void testIsEmptyAbstractSetMember() {
        ReferenceEntity ent = EntityFactory.create(ReferenceEntity.class);
        Concrete2Entity ent1 = EntityFactory.create(Concrete2Entity.class);
        ent.references().add(ent1);

        assertTrue("should be Empty", ent1.isEmpty());
        assertFalse("Collection -> should NOT be Empty", ent.isEmpty());

        Concrete2Entity ent2 = EntityFactory.create(Concrete2Entity.class);
        ent2.setPrimaryKey(new Key(22));
        ent1.reference().set(ent2);
        assertTrue("should be still Empty", ent1.isEmpty());
    }

    public void testEqualAbstractSetMember() {
        ReferenceEntity ent = EntityFactory.create(ReferenceEntity.class);
        Concrete2Entity ent1 = EntityFactory.create(Concrete2Entity.class);
        ent1.setPrimaryKey(new Key(11));
        ent.references().add(ent1);

        Concrete2Entity ent1dup = EntityFactory.create(Concrete2Entity.class);
        ent1dup.setPrimaryKey(new Key(11));

        assertTrue("should be Equals", ent1dup.equals(ent.references().get(0)));
    }

}
