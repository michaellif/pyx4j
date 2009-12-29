/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 23, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.test.shared.domain.Address;
import com.pyx4j.entity.test.shared.domain.Country;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;

public class FactoryTest extends InitializerTestCase {

    public void testObjectCreation() {
        Country country = EntityFactory.create(Country.class);
        country.name().setValue("Canada");

        assertEquals("name Value", "Canada", country.name().getValue());

        Address address = EntityFactory.create(Address.class);
        address.country().set(country);

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

        assertTrue("streetName is wrong", "Home Street".equals(employee.homeAddress().streetName().getValue()));
        assertTrue("path is wrong", "Employee/".equals(address.getParent().getPath().toString()));
        assertTrue("path is " + address.getPath(), "Employee/homeAddress/".equals(address.getPath().toString()));

    }

    public void testAutomaticMemberInstantiation() {
        Employee employee = EntityFactory.create(Employee.class);
        employee.homeAddress().streetName().setValue("Home Street");

        assertTrue("streetName is wrong", "Home Street".equals(employee.homeAddress().streetName().getValue()));
    }

    public void testSetManipulations() {
        Department department = EntityFactory.create(Department.class);
        Employee employee = EntityFactory.create(Employee.class);
        employee.firstName().setValue("Firstname");
        department.employees().add(employee);
        employee = EntityFactory.create(Employee.class);
        employee.firstName().setValue("Firstname2");
        department.employees().add(employee);

        assertTrue("Set size is wrong", department.employees().getValue().size() == 2);
        assertTrue("contains() failed", department.employees().contains(employee));

    }

    public void testPathCalculation() {
        Path path = EntityFactory.create(Employee.class).firstName().getPath();

        assertTrue("path is wrong", "Employee/firstName/".equals(path.toString()));

        path = EntityFactory.create(Employee.class).homeAddress().streetName().getPath();

        assertTrue("path is wrong", "Employee/homeAddress/streetName/".equals(path.toString()));

    }
}
