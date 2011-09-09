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
 * Created on Jan 18, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Address;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Organization;

public class EntityCloneTest extends InitializerTestCase {

    public void testFirstLevelClone() {
        Employee employee = EntityFactory.create(Employee.class);
        employee.firstName().setValue("First Name");

        Employee employee2 = (Employee) employee.cloneEntity();

        assertEquals("Firstname", employee.firstName().getValue(), employee2.firstName().getValue());

        employee2.firstName().setValue("First name Mod");
        assertEquals("Firstname Mod", "First name Mod", employee2.firstName().getValue());
        assertEquals("Firstname Ori", "First Name", employee.firstName().getValue());
    }

    public void testDeepClone() {
        Employee employee = EntityFactory.create(Employee.class);
        employee.firstName().setValue("First Name");

        Address address = EntityFactory.create(Address.class);
        employee.homeAddress().set(address);
        address = employee.homeAddress();
        address.streetName().setValue("Home Street");
        assertEquals("Level 2 value Orig", "Home Street", employee.homeAddress().streetName().getValue());

        Employee employee2 = (Employee) employee.cloneEntity();
        assertEquals("Level 2 value Cloned", "Home Street", employee2.homeAddress().streetName().getValue());

        assertEquals("Level 2 value", employee.homeAddress().streetName().getValue(), employee2.homeAddress().streetName().getValue());
        employee2.homeAddress().streetName().setValue("Home Mod");
        assertEquals("Level 2 value Mod", "Home Mod", employee2.homeAddress().streetName().getValue());
        assertEquals("Level 2 value Orig", "Home Street", employee.homeAddress().streetName().getValue());
    }

    public void testLateAssignment() {
        Employee employee = EntityFactory.create(Employee.class);
        Address address = EntityFactory.create(Address.class);
        //System.out.println("assign address");
        employee.homeAddress().set(address);
        //System.out.println("set address name");
        address.streetName().setValue("Home Street");
        assertEquals("Level 2 value Not Updated", "Home Street", employee.homeAddress().streetName().getValue());
    }

    public void testCircularReferences() {
        Organization org = EntityFactory.create(Organization.class);
        org.name().setValue("org");

        Department department = EntityFactory.create(Department.class);
        department.name().setValue("dept1");

        org.departments().add(department);

        Organization orgClone = org.cloneEntity();

        assertEquals("Level 1 name", org.name().getValue(), orgClone.name().getValue());
        assertEquals("Level 2 name", org.departments().iterator().next().name().getValue(), orgClone.departments().iterator().next().name().getValue());
    }
}
