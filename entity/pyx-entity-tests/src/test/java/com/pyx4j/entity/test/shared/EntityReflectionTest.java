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
import com.pyx4j.entity.test.shared.domain.inherit.Base1Entity;
import com.pyx4j.entity.test.shared.domain.inherit.Base2Entity;
import com.pyx4j.entity.test.shared.domain.inherit.ConcreteEntity;

public class EntityReflectionTest extends InitializerTestCase {

    public void testAddressMemberList() {
        Address address = EntityFactory.create(Address.class);
        List<String> names = address.getEntityMeta().getMemberNames();

        assertTrue("Address has streetName", names.contains("streetName"));
        assertTrue("Address has country", names.contains("country"));
        assertEquals("Address Members count", Address.TEST_DECLARED_MEMBERS, names.size());
    }

    public void testEmployeeMemberList() {
        Employee emp = EntityFactory.create(Employee.class);
        List<String> names = emp.getEntityMeta().getMemberNames();
        assertEquals("Employee Members count", Employee.DECLARED_MEMBERS, names.size());
        //Test declared order
        List<String> namesDeclared = Arrays.asList(Employee.MEMBERS_ORDER);
        if (!EqualsHelper.equals(names, namesDeclared)) {
            fail("Member Order is not preserved " + names + " != " + namesDeclared);
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

    public void testIsAssignableFrom() {
        Base2Entity base2Entity = EntityFactory.create(Base2Entity.class);
        assertTrue("Base2Entity instanceOf Base2Entity", base2Entity.isInstanceOf(Base2Entity.class));
        assertTrue("Base2Entity isAssignableFrom Base2Entity", base2Entity.isAssignableFrom(Base2Entity.class));

        assertTrue("Base2Entity instanceOf Base1Entity", base2Entity.isInstanceOf(Base1Entity.class));
        assertFalse("Base2Entity isAssignableFrom Base1Entity", base2Entity.isAssignableFrom(Base1Entity.class));
        assertFalse("Base2Entity instanceOf ConcreteEntity", base2Entity.isInstanceOf(ConcreteEntity.class));
        assertTrue("Base2Entity isAssignableFrom ConcreteEntity", base2Entity.isAssignableFrom(ConcreteEntity.class));

        ConcreteEntity concreteEntity = EntityFactory.create(ConcreteEntity.class);
        assertTrue("ConcreteEntity instanceOf ConcreteEntity", concreteEntity.isInstanceOf(ConcreteEntity.class));
        assertTrue("ConcreteEntity isAssignableFrom ConcreteEntity", concreteEntity.isAssignableFrom(ConcreteEntity.class));

        assertTrue("ConcreteEntity instanceOf Base1Entity", concreteEntity.isInstanceOf(Base1Entity.class));
        assertFalse("ConcreteEntity isAssignableFrom Base1Entity", concreteEntity.isAssignableFrom(Base1Entity.class));
        assertTrue("ConcreteEntity instanceOf Base2Entity", concreteEntity.isInstanceOf(Base2Entity.class));
        assertFalse("ConcreteEntity isAssignableFrom Base2Entity", concreteEntity.isAssignableFrom(Base2Entity.class));
        assertFalse("ConcreteEntity instanceOf Address", concreteEntity.isInstanceOf(Address.class));
        assertFalse("ConcreteEntity isAssignableFrom Address", concreteEntity.isAssignableFrom(Address.class));
    }
}
