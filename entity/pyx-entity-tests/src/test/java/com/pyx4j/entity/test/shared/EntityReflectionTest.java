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
 * Created on Dec 30, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Address;
import com.pyx4j.entity.test.shared.domain.Employee;

public class EntityReflectionTest extends InitializerTestCase {

    public void testAddressMemberList() {
        Address address = EntityFactory.create(Address.class);
        List<String> names = address.getEntityMeta().getMemberNames();

        assertTrue("Address has streetName", names.contains("streetName"));
        assertTrue("Address has country", names.contains("country"));
        assertEquals("Address Memebers count", Address.TEST_DECLARED_MEMEBERS, names.size());
    }

    public void testEmployeeMemberList() {
        Employee emp = EntityFactory.create(Employee.class);
        List<String> names = emp.getEntityMeta().getMemberNames();
        assertEquals("Employee Memebers count", Employee.DECLARED_MEMEBERS, names.size());
        //Test declared order
        List<String> namesDeclared = Arrays.asList(Employee.MEMEBERS_ORDER);
        if (!EqualsHelper.equals(names, namesDeclared)) {
            fail("Memeber Order is not preserved " + names + " != " + namesDeclared);
        }
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
