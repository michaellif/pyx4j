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
 * Created on Feb 6, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.server;

import java.util.List;
import java.util.Vector;

import junit.framework.Assert;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Organization;
import com.pyx4j.entity.test.shared.domain.Task;

public abstract class SetPersistanceTestCase extends DatastoreTestBase {

    public void testUnownedSetMemebrIsNotSaved() {
        Department department = EntityFactory.create(Department.class);
        String deptName = "dept" + uniqueString();
        department.name().setValue(deptName);

        Employee employee = EntityFactory.create(Employee.class);
        String empName = "emp" + uniqueString();
        double origSalary = 50000.0;
        employee.salary().setValue(origSalary);
        employee.firstName().setValue(empName);
        department.employees().add(employee);

        boolean saved = false;
        try {
            srv.persist(department);
            saved = true;
        } catch (Error e) {
            // OK
        }
        if (saved) {
            fail("Should not save UnownedSetMemebr");
        }

        // See if it is not updated with cascade
        srv.persist(employee);
        employee.salary().setValue(100000.0);
        srv.persist(department);

        Employee employee2 = srv.retrieve(Employee.class, employee.getPrimaryKey());
        Assert.assertEquals("salary no update", origSalary, employee2.salary().getValue());
    }

    public void testUnownedSetMemebrInCascadeIsNotSaved() {
        Organization org = EntityFactory.create(Organization.class);
        org.name().setValue("org" + uniqueString());

        Department department = EntityFactory.create(Department.class);
        String deptName = "dept" + uniqueString();
        department.name().setValue(deptName);

        Employee employee1 = EntityFactory.create(Employee.class);
        String empName = "emp1." + uniqueString();
        double origSalary = 50000.0;
        employee1.salary().setValue(origSalary);
        employee1.firstName().setValue(empName);
        srv.persist(employee1);

        department.employees().add(employee1);
        org.departments().add(department);

        // test starts here
        srv.persist(org);

        Employee employee2 = EntityFactory.create(Employee.class);
        String empName2 = "emp2." + uniqueString();
        employee2.salary().setValue(60000.0);
        employee2.firstName().setValue(empName2);
        department.employees().add(employee2);

        boolean saved = false;
        try {
            srv.persist(org);
            saved = true;
        } catch (Error e) {
            // OK
        }
        if (saved) {
            fail("Should not save UnownedSetMember department.employees");
        }
        department.employees().remove(employee2);
        employee1.salary().setValue(120000.0);
        srv.persist(org);

        Organization org2 = srv.retrieve(Organization.class, org.getPrimaryKey());
        assertEquals("set size", 1, org2.departments().size());

        Employee employee1r = srv.retrieve(Employee.class, employee1.getPrimaryKey());
        Assert.assertEquals("salary no update", origSalary, employee1r.salary().getValue());
    }

    private enum TestCaseMethod {

        Persist,

        Merge,

        PersistCollection
    }

    public void testOwnedSetUpdate(TestCaseMethod testCaseMethod) {
        Employee emp = EntityFactory.create(Employee.class);
        emp.firstName().setValue("Bob" + uniqueString());

        Task task = EntityFactory.create(Task.class);
        task.description().setValue("Do Nothing");
        emp.tasks().add(task);

        srv.persist(emp);

        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertEquals("Retr. Set size", 1, emp2.tasks().size());
        Task task2 = emp2.tasks().iterator().next();
        Assert.assertEquals("Owned vlue Pk", task.getPrimaryKey(), task2.getPrimaryKey());

        String description = "Work " + uniqueString();
        task2.description().setValue(description);

        switch (testCaseMethod) {
        case Persist:
            srv.persist(emp2);
            break;
        case Merge:
            srv.merge(emp2);
            break;
        case PersistCollection:
            List<Employee> collection = new Vector<Employee>();
            collection.add(emp2);
            srv.persist(collection);
            break;
        }

        Employee emp3 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertEquals("Retr. Set size", 1, emp3.tasks().size());
        Task task3 = emp3.tasks().iterator().next();
        Assert.assertEquals("Owned vlue Pk", task.getPrimaryKey(), task3.getPrimaryKey());
        Assert.assertEquals("description update", description, task3.description().getValue());
        srv.delete(emp3);
    }

    public void testOwnedSetUpdate() {
        testOwnedSetUpdate(TestCaseMethod.Persist);
    }

    public void testOwnedSetMerge() {
        testOwnedSetUpdate(TestCaseMethod.Merge);
    }

    public void TODO_testOwnedSetUpdateIterable() {
        testOwnedSetUpdate(TestCaseMethod.PersistCollection);
    }

    public void testOwnedListUpdate(TestCaseMethod testCaseMethod) {
        Employee emp = EntityFactory.create(Employee.class);
        emp.firstName().setValue("Bob" + uniqueString());

        Task task = EntityFactory.create(Task.class);
        task.description().setValue("Do Nothing");
        emp.tasksSorted().add(task);

        srv.persist(emp);

        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertEquals("Retr. Set size", 1, emp2.tasksSorted().size());
        Task task2 = emp2.tasksSorted().iterator().next();
        Assert.assertEquals("Owned vlue Pk", task.getPrimaryKey(), task2.getPrimaryKey());

        String description = "Work " + uniqueString();
        task2.description().setValue(description);

        switch (testCaseMethod) {
        case Persist:
            srv.persist(emp2);
            break;
        case Merge:
            srv.merge(emp2);
            break;
        case PersistCollection:
            List<Employee> collection = new Vector<Employee>();
            collection.add(emp2);
            srv.persist(collection);
            break;
        }

        Employee emp3 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertEquals("Retr. Set size", 1, emp3.tasksSorted().size());
        Task task3 = emp3.tasksSorted().iterator().next();
        Assert.assertEquals("Owned vlue Pk", task.getPrimaryKey(), task3.getPrimaryKey());
        Assert.assertEquals("description update", description, task3.description().getValue());
        srv.delete(emp3);
    }

    public void testOwnedListUpdate() {
        testOwnedListUpdate(TestCaseMethod.Persist);
    }

    public void testOwnedListMerge() {
        testOwnedListUpdate(TestCaseMethod.Merge);
    }

    public void TODO_testOwnedListUpdateIterable() {
        testOwnedListUpdate(TestCaseMethod.PersistCollection);
    }
}
