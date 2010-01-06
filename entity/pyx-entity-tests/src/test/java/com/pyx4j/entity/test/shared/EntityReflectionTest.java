/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 30, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import java.util.Set;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Address;
import com.pyx4j.entity.test.shared.domain.Employee;

public class EntityReflectionTest extends InitializerTestCase {

    public void testAddressMemberList() {
        Address address = EntityFactory.create(Address.class);
        Set<String> names = address.getMemberNames();

        assertTrue("Address has streetName", names.contains("streetName"));
        assertTrue("Address has country", names.contains("country"));
        assertEquals("Address Memebers count", 2, names.size());
    }

    public void testEmployeeMemberList() {
        Employee emp = EntityFactory.create(Employee.class);
        Set<String> names = emp.getMemberNames();
        assertEquals("Employee Memebers count", 11, names.size());
    }

    public void testAddressMemberAccess() {
        Address address = EntityFactory.create(Address.class);
        address.streetName().setValue("Home Street");
        assertNotNull("Memebr by name", address.getMember("streetName"));
        assertEquals("streetName is wrong", "Home Street", address.getMember("streetName").getValue());
        assertEquals("streetName is wrong", "Home Street", address.getMemberValue("streetName"));
        address.setMemberValue("streetName", "Work  Street");
        assertEquals("streetName is wrong", "Work  Street", address.getMember("streetName").getValue());
    }

}
