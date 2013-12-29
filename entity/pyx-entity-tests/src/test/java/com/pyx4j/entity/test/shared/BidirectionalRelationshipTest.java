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
 * Created on Feb 21, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import junit.framework.Assert;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.impl.ObjectHandler;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Organization;
import com.pyx4j.entity.test.shared.domain.bidir.Child;
import com.pyx4j.entity.test.shared.domain.bidir.Master;
import com.pyx4j.entity.test.shared.domain.ownership.RootEnity;
import com.pyx4j.entity.test.shared.domain.ownership.creation.ForceCreationOneToOneParentDTO;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToManyChild;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToManyParentDTO;

public class BidirectionalRelationshipTest extends InitializerTestBase {

    public void testOwnedEntityValue() {
        Master m = EntityFactory.create(Master.class);
        m.name().setValue("m1");

        Child c1 = EntityFactory.create(Child.class);
        String c1Name = "c1";
        c1.name().setValue(c1Name);

        assertTrue("Not owned Yet", c1.master().isNull());
        m.child().name().setValue("!" + c1Name);

        m.child().set(c1);

        assertFalse("Owned now", c1.master().isNull());
        assertEquals("Owned properly", m, c1.master());
        assertEquals("Same value", c1Name, m.child().name().getValue());

        assertNull("Owner Entity FieldName", m.getFieldName());
        assertNotNull("Owner Field Entity FieldName", c1.master().getFieldName());

        assertEquals("Owner the same value", m.getValue(), c1.getMemberValue(c1.master().getFieldName()));
        assertTrue("Owner refferes to the same value", c1.getMemberValue(c1.master().getFieldName()) == m.getValue());

        m.setPrimaryKey(new Key(76));
        assertEquals("Owner ID Update", m.getPrimaryKey(), c1.master().getPrimaryKey());

        String c2Name = "c2";
        c1.name().setValue(c2Name);
        assertEquals("Value updated", c2Name, m.child().name().getValue());
    }

    public void testOwnerValueInitialization() {
        Master m = EntityFactory.create(Master.class);
        m.name().setValue("m1");
        m.child().name().setValue("c1");
        assertEquals("Owner value is assigned automatically", m.name().getValue(), m.child().master().name().getValue());
    }

    public void testOwnedEntityNonNull() {
        Master m = EntityFactory.create(Master.class);
        Child c = EntityFactory.create(Child.class);
        Assert.assertTrue("Master is not null", m.isNull());
        Assert.assertTrue("Child is not null", c.isNull());

        m.child().set(c);
        Assert.assertFalse("Master is null", m.isNull());
        Assert.assertFalse("Child is null", c.isNull());
    }

    public void testOwnedUnidirectionalValueInitialization() {
        RootEnity m = EntityFactory.create(RootEnity.class);
        m.name().setValue("m1");

        Assert.assertTrue("Child is null", m.ownedLeaf().isNull());

        m.ownedLeaf().name().setValue(null);
        Assert.assertTrue("Child is still null", m.ownedLeaf().isNull());
    }

    // This function exhibit difference in behavior of Bidirectional references
    //  The value or @Owner Member Entity Object in child is initialized when you try to assign any value(even null) to child object!
    public void testOwnedBidirectionalValueInitialization() {
        Master m = EntityFactory.create(Master.class);
        m.name().setValue("m1");

        Assert.assertTrue("Child is null", m.child().isNull());

        m.child().name().setValue(null);
        Assert.assertFalse("Child is NOT null", m.child().isNull());
    }

    public void testOwnedSetValue() {
        Organization org = EntityFactory.create(Organization.class);
        org.name().setValue("org");

        Department department = EntityFactory.create(Department.class);
        String deptName = "dept1";
        department.name().setValue(deptName);

        assertTrue("Not owned Yet", department.organization().isNull());

        org.departments().add(department);
        assertEquals("set size", 1, org.departments().size());

        assertFalse("Owned now", department.organization().isNull());
        org.setPrimaryKey(new Key(77));
        assertEquals("Owner ID Update", org.getPrimaryKey(), department.organization().getPrimaryKey());

        Department orgDepartment = org.departments().iterator().next();
        assertNotNull("Direct value access", orgDepartment.getMemberValue("organization"));
        assertFalse("Owned now", orgDepartment.organization().isNull());
        assertEquals("Owned properly", orgDepartment.organization(), org);
        assertNotNull("Direct value access", orgDepartment.getMemberValue("organization"));

        // Test Recursive print
        System.out.println(orgDepartment.toString());
    }

    public void testOwnerChanges() {
        Employee emp = EntityFactory.create(Employee.class);
        emp.firstName().setValue("any");
        Assert.assertTrue("Owner is not null", emp.department().organization().isNull());
        Department department = EntityFactory.create(Department.class);
        //department has @Owner  but it is not Employee!
        emp.department().set(department);
        Assert.assertTrue("Owner is not null after assignment", emp.department().organization().isNull());
    }

    public void testOneToOneOwnerPreservationInClear() {
        ForceCreationOneToOneParentDTO o = EntityFactory.create(ForceCreationOneToOneParentDTO.class);
        o.name().setValue("v1");
        o.child().name().setValue("v2");
        o.otherEntity().description().setValue("v3");

        assertEquals("parent value is the same", o.getValue(), o.child().parent().getValue());
        assertTrue("parent value is the same", o.getValue() == o.child().parent().getValue());

        o.child().clearValues();

        assertEquals("parent value is the same", o.getValue(), o.child().parent().getValue());
        assertTrue("parent value is the same", o.getValue() == o.child().parent().getValue());
    }

    public void testOneToManyOwnerPreservationInClear() {
        BidirectionalOneToManyParentDTO o = EntityFactory.create(BidirectionalOneToManyParentDTO.class);
        o.name().setValue("v1");
        BidirectionalOneToManyChild c = EntityFactory.create(BidirectionalOneToManyChild.class);
        c.name().setValue("v2");
        o.children().add(c);

        assertEquals("parent value is the same", o.getValue(), c.parent().getValue());
        assertTrue("parent value is the same", o.getValue() == c.parent().getValue());

        if (ObjectHandler.PROPER_POINTERS) {
            BidirectionalOneToManyChild cActual = o.children().get(0);
            cActual.clearValues();
            assertEquals("parent value is the same", o.getValue(), cActual.parent().getValue());
            assertTrue("parent value is the same", o.getValue() == cActual.parent().getValue());
        } else {
            c.clearValues();

            assertEquals("parent value is the same", o.getValue(), c.parent().getValue());
            assertTrue("parent value is the same", o.getValue() == c.parent().getValue());
        }
    }
}
