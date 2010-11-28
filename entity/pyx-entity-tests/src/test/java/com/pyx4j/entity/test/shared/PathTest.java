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
 * Created on Feb 19, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.test.shared.domain.Address;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Task;

public class PathTest extends InitializerTestCase {

    public void testPathCalculation() {
        Employee emp = EntityFactory.create(Employee.class);
        assertEquals("primitive path", "Employee/firstName/", emp.firstName().getPath().toString());
        assertEquals("member path", "Employee/homeAddress/streetName/", emp.homeAddress().streetName().getPath().toString());

        assertEquals("set path", "Employee/tasks/", emp.tasks().getPath().toString());

        assertEquals("set path", "Employee/tasks/[]/", emp.tasks().$().getPath().toString());
        assertEquals("set path", "Employee/tasks/[]/deadLine/", emp.tasks().$().deadLine().getPath().toString());

        emp.tasks().add(EntityFactory.create(Task.class));
        assertEquals("set path", "Employee/tasks/[]/", emp.tasks().iterator().next().getPath().toString());

    }

    public void testPathParsing() {
        Path p = new Path("Employee/tasks/");
        assertEquals("path Root", "Employee", p.getRootObjectClassName());
        assertEquals("path Root", "tasks", p.getPathMembers().get(0));

        Path p2 = new Path("Employee/tasks/[]/deadLine/");
        assertEquals("path Root", "Employee", p2.getRootObjectClassName());
        assertEquals("path Root", "tasks", p2.getPathMembers().get(0));
        assertEquals("path Root", "[]", p2.getPathMembers().get(1));
        assertEquals("path Root", "deadLine", p2.getPathMembers().get(2));
    }

    public void testGetByPath() {
        Employee employee = EntityFactory.create(Employee.class);
        employee.firstName().setValue("Firstname");

        Address address = EntityFactory.create(Address.class);
        employee.homeAddress().set(address);
        address = employee.homeAddress();
        final String streetNameValue = "Home Street";
        address.streetName().setValue(streetNameValue);

        Path path = EntityFactory.create(Employee.class).homeAddress().streetName().getPath();

        //TODO  this was removed with SharedEntityHandler.assertPath
        //        try {
        //            address.getMember(path);
        //            fail("Allow invalid access to path");
        //        } catch (IllegalArgumentException ok) {
        //        }

        IObject<?> object = employee.getMember(path);
        assertEquals("member by Path FieldName", "streetName", object.getFieldName());
        assertEquals("member by Path Value", streetNameValue, employee.getValue(path));
        assertNull("member by Path null", employee.getValue(EntityFactory.create(Employee.class).department().name().getPath()));
    }

    public void testSetByPathLevel1() {
        Employee emp = EntityFactory.create(Employee.class);
        Employee empMeta = EntityFactory.create(Employee.class);

        final String nameValue = "Bob 21";
        emp.setValue(empMeta.firstName().getPath(), nameValue);
        assertEquals("member Value", nameValue, emp.firstName().getValue());
    }

    public void testSetByPathLevel2() {
        Employee emp = EntityFactory.create(Employee.class);
        Employee empMeta = EntityFactory.create(Employee.class);

        final String streetNameValue = "Home Street";
        emp.setValue(empMeta.homeAddress().streetName().getPath(), streetNameValue);
        assertEquals("member Value", streetNameValue, emp.homeAddress().streetName().getValue());
    }
}
