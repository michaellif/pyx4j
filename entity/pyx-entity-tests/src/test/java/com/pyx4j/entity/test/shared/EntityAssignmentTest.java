/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2013-01-22
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.impl.ObjectHandler;
import com.pyx4j.entity.test.shared.domain.Address;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Organization;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToManyChild;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToManyParent;

public class EntityAssignmentTest extends InitializerTestBase {

    public void testOwnedObjectsAssignments() {
        Employee employee1 = EntityFactory.create(Employee.class);

        Address address = EntityFactory.create(Address.class);
        address.streetName().setValue("A Street");
        assertEquals("Address/", address.getPath().toString());

        // Entity do not change when assigned to different position in Graph
        employee1.homeAddress().set(address);

        assertTrue(address.getValue() == employee1.homeAddress().getValue());

        if (ObjectHandler.PROPER_POINTERS) {
            assertEquals("Address/", address.getPath().toString());
        }
        assertEquals("Employee/homeAddress/", employee1.homeAddress().getPath().toString());

        if (ObjectHandler.PROPER_POINTERS) {
            assertNull(address.getParent());
            assertNull(address.getOwner());
        } else {
            assertEquals(employee1, address.getParent());
            assertEquals(employee1, address.getOwner());
        }
        assertEquals(employee1, employee1.homeAddress().getParent());
        assertEquals(employee1, employee1.homeAddress().getOwner());

        // move Child to different owner
        Employee employee2 = EntityFactory.create(Employee.class);
        employee2.homeAddress().set(employee1.homeAddress());

        if (ObjectHandler.PROPER_POINTERS) {
            // TODO Maybe future
            //assertTrue(employee1.homeAddress().isNull());
        } else {
            assertTrue(employee2.homeAddress().getValue() == employee1.homeAddress().getValue());
        }

    }

    public void testOwnedObjectsAssignmentCollections() {
        BidirectionalOneToManyParent p1 = EntityFactory.create(BidirectionalOneToManyParent.class);
        p1.name().setValue("v1");

        BidirectionalOneToManyChild c1 = EntityFactory.create(BidirectionalOneToManyChild.class);
        c1.name().setValue("v2");
        p1.children().add(c1);

        assertEquals("parent value is the same", p1.getValue(), c1.parent().getValue());
        assertTrue("parent value is the same", p1.getValue() == c1.parent().getValue());

        // move Child to different owner
        BidirectionalOneToManyParent p2 = EntityFactory.create(BidirectionalOneToManyParent.class);
        p2.name().setValue("v2");
        p2.children().add(c1);
        assertTrue("parent value changed", p2.getValue() == c1.parent().getValue());

        // The data value stay shared
        String v3 = "v3";
        c1.name().setValue(v3);
        assertEquals(v3, p1.children().get(0).name().getValue());
        assertEquals(v3, p2.children().get(0).name().getValue());
    }

    public void testAssignmentToMultiplePlaces() {
        String v = "any";
        Employee emp = EntityFactory.create(Employee.class);
        emp.department().name().setValue(v);

        assertEquals(v, emp.department().name().getValue());

        Organization org = EntityFactory.create(Organization.class);
        org.departments().add(emp.department());

        if (ObjectHandler.PROPER_POINTERS) {
            assertEquals(v, emp.department().name().getValue());
        }
    }

}
