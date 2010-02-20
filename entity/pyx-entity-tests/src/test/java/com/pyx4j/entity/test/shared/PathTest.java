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

        assertEquals("set path", "Employee/tasks/[]", emp.tasks().$().getPath().toString());
        assertEquals("set path", "Employee/tasks/[]deadLine/", emp.tasks().$().deadLine().getPath().toString());

        emp.tasks().add(EntityFactory.create(Task.class));
        assertEquals("set path", "Employee/tasks/[]", emp.tasks().iterator().next().getPath().toString());
    }

    public void testGetByPath() {
        Employee employee = EntityFactory.create(Employee.class);
        employee.firstName().setValue("Firstname");

        Address address = EntityFactory.create(Address.class);
        employee.homeAddress().set(address);
        address = employee.homeAddress();
        address.streetName().setValue("Home Street");

        Path path = EntityFactory.create(Employee.class).homeAddress().streetName().getPath();

        IObject<?> object = employee.getMember(path);

    }
}
