/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 29, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import java.util.Date;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Status;
import com.pyx4j.entity.test.shared.domain.Employee.EmploymentStatus;

public class PrimitiveTest extends InitializerTestCase {

    public void testString() {
        Employee emp = EntityFactory.create(Employee.class);
        assertNull("Initial value", emp.firstName().getValue());
        emp.firstName().setValue("Bob");
        assertEquals("Value", "Bob", emp.firstName().getValue());
    }

    public void testDate() {
        Employee emp = EntityFactory.create(Employee.class);
        assertNull("Initial value", emp.hiredate().getValue());
        Date today = new Date();
        emp.hiredate().setValue(today);
        assertEquals("Value", today, emp.hiredate().getValue());
    }

    public void testBoolean() {
        Employee emp = EntityFactory.create(Employee.class);
        assertNull("Initial value", emp.reliable().getValue());
        emp.reliable().setValue(Boolean.TRUE);
        assertEquals("Value", Boolean.TRUE, emp.reliable().getValue());
    }

    public void testInteger() {
        Employee emp = EntityFactory.create(Employee.class);
        assertNull("Initial value", emp.rating().getValue());
        emp.rating().setValue(5);
        assertEquals("Value", Integer.valueOf(5), emp.rating().getValue());
    }

    public void testDouble() {
        Employee emp = EntityFactory.create(Employee.class);
        assertNull("Initial value", emp.salary().getValue());
        emp.salary().setValue(77.8);
        assertEquals("Value", 77.8, emp.salary().getValue());
    }

    public void testEnumExternal() {
        Employee emp = EntityFactory.create(Employee.class);
        assertNull("Initial value", emp.accessStatus().getValue());
        emp.accessStatus().setValue(Status.SUSPENDED);
        assertEquals("Value", Status.SUSPENDED, emp.accessStatus().getValue());
    }

    public void testEnumEmbeded() {
        Employee emp = EntityFactory.create(Employee.class);
        assertNull("Initial value", emp.employmentStatus().getValue());
        emp.employmentStatus().setValue(EmploymentStatus.FULL_TIME);
        assertEquals("Value", EmploymentStatus.FULL_TIME, emp.employmentStatus().getValue());
    }
}
