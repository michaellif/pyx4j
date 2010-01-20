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
 * Created on Dec 23, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.test.shared.domain.Address;
import com.pyx4j.entity.test.shared.domain.Country;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;

public class FactoryTest extends InitializerTestCase {

    public void testObjectCreation() {
        Country country = EntityFactory.create(Country.class);
        assertNotNull("EntityFactory create", country);
        //System.out.println("Country ProxyClass:" + country.getClass().getName());

        country.name().setValue("Canada");

        assertEquals("name Value", "Canada", country.name().getValue());

        Address address = EntityFactory.create(Address.class);
        address.streetName().setValue("Street");
        address.country().set(country);

        //System.out.println(((IFullDebug) country).debugString());
        //System.out.println(((IFullDebug) address).debugString());
        //System.out.println(((IFullDebug) (address.country())).debugString());

        assertEquals("address.country Value", "Canada", address.country().name().getValue());
    }

    public void testSimpleManipulations() {
        Employee employee = EntityFactory.create(Employee.class);
        employee.firstName().setValue("Firstname");

        assertTrue("firstName is wrong", "Firstname".equals(employee.firstName().getValue()));

        Address address = EntityFactory.create(Address.class);
        employee.homeAddress().set(address);
        address = employee.homeAddress();
        address.streetName().setValue("Home Street");

        assertEquals("streetName is wrong", "Home Street", employee.homeAddress().streetName().getValue());
        assertTrue("path is wrong", "Employee/".equals(address.getParent().getPath().toString()));
        assertTrue("path is " + address.getPath(), "Employee/homeAddress/".equals(address.getPath().toString()));

    }

    public void testAutomaticMemberInstantiation() {
        Employee employee = EntityFactory.create(Employee.class);
        employee.homeAddress().streetName().setValue("Home Street");

        assertTrue("streetName is wrong", "Home Street".equals(employee.homeAddress().streetName().getValue()));
    }

    public void testElvisMemberAccess() {
        Employee employee = EntityFactory.create(Employee.class);
        assertNotNull("Memeber Descriptor", employee.homeAddress().streetName());
        assertNull("Memeber value", employee.homeAddress().streetName().getValue());
    }

    public void testEquals() {
        Employee employee1 = EntityFactory.create(Employee.class);
        employee1.firstName().setValue("Firstname1");
        employee1.setPrimaryKey("keyX");

        Employee employee2 = EntityFactory.create(Employee.class);
        employee2.firstName().setValue("Firstname2");
        employee2.setPrimaryKey("keyX");
        assertEquals("same key", employee1, employee2);
    }

    public void testSetManipulations() {
        Department department = EntityFactory.create(Department.class);
        Employee employee1 = EntityFactory.create(Employee.class);
        employee1.firstName().setValue("Firstname1");
        employee1.setPrimaryKey("key1");
        department.employees().add(employee1);

        Employee employee2 = EntityFactory.create(Employee.class);
        employee2.firstName().setValue("Firstname2");
        employee2.setPrimaryKey("key2");
        department.employees().add(employee2);

        Employee employee3 = EntityFactory.create(Employee.class);
        employee3.firstName().setValue("Firstname3");

        assertEquals("Set size", 2, department.employees().size());
        assertEquals("Set size", 2, department.employees().getValue().size());
        assertTrue("contains(emp1)", department.employees().contains(employee1));
        assertTrue("contains(emp2)", department.employees().contains(employee2));
        assertFalse("contains(emp3)", department.employees().contains(employee3));
        employee3.setPrimaryKey("key2");
        assertEquals("same key (emp2 and emp3)", employee2, employee3);
        assertTrue("contains(emp3(emp2))", department.employees().contains(employee3));

        employee2.setPrimaryKey("key2x");
        employee3.setPrimaryKey("key2x");
        assertTrue("contains mod Key(emp3(emp2))", department.employees().contains(employee3));
    }

    public void testPathCalculation() {
        Path path = EntityFactory.create(Employee.class).firstName().getPath();

        assertTrue("path is wrong", "Employee/firstName/".equals(path.toString()));

        path = EntityFactory.create(Employee.class).homeAddress().streetName().getPath();

        assertTrue("path is wrong", "Employee/homeAddress/streetName/".equals(path.toString()));

    }

    public void testGetByPath() {
        Employee employee = EntityFactory.create(Employee.class);
        employee.firstName().setValue("Firstname");

        Address address = EntityFactory.create(Address.class);
        employee.homeAddress().set(address);
        address = employee.homeAddress();
        address.streetName().setValue("Home Street");

        Path path = EntityFactory.create(Employee.class).homeAddress().streetName().getPath();

        IObject object = employee.getMember(path);

    }

}
