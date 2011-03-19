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
 * Created on Mar 19, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.server;

import junit.framework.Assert;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.EmployeeData;
import com.pyx4j.entity.test.shared.domain.Task;

public abstract class AssignedPrimaryKeyTestCase extends DatastoreTestBase {

    public void testPKRequired() {
        EmployeeData empData = EntityFactory.create(EmployeeData.class);
        boolean saved = false;
        try {
            srv.persist(empData);
            saved = true;
        } catch (Error e) {
            // OK
        }
        if (saved) {
            fail("Should not save entity without ASSIGNED PK");
        }
    }

    public void testPKAssigment() {
        Employee emp = EntityFactory.create(Employee.class);
        String empName = "Bob " + uniqueString();
        emp.firstName().setValue(empName);
        srv.persist(emp);

        EmployeeData empData = EntityFactory.create(EmployeeData.class);
        empData.setPrimaryKey(emp.getPrimaryKey());
        String comment = "aComment " + uniqueString();
        empData.comment().setValue(comment);
        srv.persist(empData);
        Assert.assertEquals("Assigned PrimaryKey", emp.getPrimaryKey(), empData.getPrimaryKey());

        Employee emp1 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertNotNull("retrieve", emp1);
        Assert.assertEquals("Value", empName, emp1.firstName().getValue());

        EmployeeData empData1 = srv.retrieve(EmployeeData.class, emp.getPrimaryKey());
        Assert.assertEquals("dataValue", comment, empData1.comment().getValue());
        Assert.assertEquals("Assigned PrimaryKey", emp.getPrimaryKey(), empData1.getPrimaryKey());
    }

    public void testOwnedListUpdate() {
        Employee emp = EntityFactory.create(Employee.class);
        emp.firstName().setValue(uniqueString());
        srv.persist(emp);

        EmployeeData empData = EntityFactory.create(EmployeeData.class);
        empData.setPrimaryKey(emp.getPrimaryKey());
        empData.comment().setValue(uniqueString());

        Task task1 = EntityFactory.create(Task.class);
        task1.description().setValue(uniqueString());
        empData.tasksArchive().add(task1);

        Task task2 = EntityFactory.create(Task.class);
        task2.description().setValue(uniqueString());
        empData.tasksArchive().add(task2);

        srv.persist(empData);

        EmployeeData empData1 = srv.retrieve(EmployeeData.class, emp.getPrimaryKey());
        Assert.assertEquals("Retr. Set size", 2, empData1.tasksArchive().size());

    }
}
