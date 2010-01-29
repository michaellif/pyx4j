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
 * Created on Dec 29, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import java.util.Date;

import com.pyx4j.commons.Pair;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Status;
import com.pyx4j.entity.test.shared.domain.Employee.EmploymentStatus;
import com.pyx4j.entity.test.shared.rpc.ComplexPrimitive;

public class PrimitiveTest extends InitializerTestCase {

    public void testString() {
        Employee emp = EntityFactory.create(Employee.class);
        assertNull("Initial value", emp.firstName().getValue());
        assertEquals("Class of Value", String.class, emp.firstName().getValueClass());
        emp.firstName().setValue("Bob");
        assertEquals("Value", "Bob", emp.firstName().getValue());
    }

    public void testDate() {
        Employee emp = EntityFactory.create(Employee.class);
        assertNull("Initial value", emp.hiredate().getValue());
        assertEquals("Class of Value", Date.class, emp.hiredate().getValueClass());
        Date today = new Date();
        emp.hiredate().setValue(today);
        assertEquals("Value", today, emp.hiredate().getValue());
    }

    public void testBoolean() {
        Employee emp = EntityFactory.create(Employee.class);
        assertNull("Initial value", emp.reliable().getValue());
        assertEquals("Class of Value", Boolean.class, emp.reliable().getValueClass());
        emp.reliable().setValue(Boolean.TRUE);
        assertEquals("Value", Boolean.TRUE, emp.reliable().getValue());
    }

    public void testLong() {
        Employee emp = EntityFactory.create(Employee.class);
        assertNull("Initial value", emp.holidays().getValue());
        assertEquals("Class of Value", Long.class, emp.holidays().getValueClass());
        emp.holidays().setValue(5L);
        assertEquals("Value", Long.valueOf(5), emp.holidays().getValue());
    }

    public void testInteger() {
        Employee emp = EntityFactory.create(Employee.class);
        assertNull("Initial value", emp.rating().getValue());
        assertEquals("Class of Value", Integer.class, emp.rating().getValueClass());
        emp.rating().setValue(5);
        assertEquals("Value", Integer.valueOf(5), emp.rating().getValue());
    }

    public void testDouble() {
        Employee emp = EntityFactory.create(Employee.class);
        assertNull("Initial value", emp.salary().getValue());
        assertEquals("Class of Value", Double.class, emp.salary().getValueClass());
        emp.salary().setValue(77.8);
        assertEquals("Value", 77.8, emp.salary().getValue());
    }

    public void testEnumExternal() {
        Employee emp = EntityFactory.create(Employee.class);
        assertNull("Initial value", emp.accessStatus().getValue());
        assertEquals("Class of Value", Status.class, emp.accessStatus().getValueClass());
        emp.accessStatus().setValue(Status.SUSPENDED);
        assertEquals("Value", Status.SUSPENDED, emp.accessStatus().getValue());
    }

    public void testEnumEmbeded() {
        Employee emp = EntityFactory.create(Employee.class);
        assertNull("Initial value", emp.employmentStatus().getValue());
        assertEquals("Class of Value", EmploymentStatus.class, emp.employmentStatus().getValueClass());
        emp.employmentStatus().setValue(EmploymentStatus.FULL_TIME);
        assertEquals("Value", EmploymentStatus.FULL_TIME, emp.employmentStatus().getValue());
    }

    public void testComplexPrimitive() {
        ComplexPrimitive cp = EntityFactory.create(ComplexPrimitive.class);
        Pair<String, String> pair = new Pair<String, String>("left", "right");
        Pair<String, String> pair2 = new Pair<String, String>("left", "right");
        cp.stringPair().setValue(pair);
        assertEquals("Class of Value", Pair.class, cp.stringPair().getValueClass());
        assertEquals("Value", pair2, cp.stringPair().getValue());
    }
}
