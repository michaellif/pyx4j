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
            fail("Should not save UnownedSetMember");
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
        Assert.assertEquals("Owned value Pk", task.getPrimaryKey(), task2.getPrimaryKey());
        Assert.assertFalse("Values retrived", task2.isValuesDetached());

        String description = "Work " + uniqueString();
        task2.description().setValue(description);

        srvSave(emp2, testCaseMethod);

        Employee emp3 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertEquals("Retr. Set size", 1, emp3.tasks().size());
        Task task3 = emp3.tasks().iterator().next();
        Assert.assertEquals("Owned value Pk", task.getPrimaryKey(), task3.getPrimaryKey());
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
        Assert.assertEquals("Owned value Pk", task.getPrimaryKey(), task2.getPrimaryKey());
        Assert.assertFalse("Values retrived", task2.isValuesDetached());

        String description = "Work1 " + uniqueString();
        task2.description().setValue(description);

        srvSave(emp2, testCaseMethod);

        Employee emp3 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertEquals("Retr. Set size", 1, emp3.tasksSorted().size());
        Task task3 = emp3.tasksSorted().iterator().next();
        Assert.assertEquals("Owned value Pk", task.getPrimaryKey(), task3.getPrimaryKey());
        Assert.assertEquals("description update", description, task3.description().getValue());

        //--- Sort Order

        Task task22 = emp3.tasksSorted().$();
        String description22 = "Work2 " + uniqueString();
        task22.description().setValue(description22);
        emp3.tasksSorted().add(0, task22);
        Assert.assertEquals("Owned value 0", task22, emp3.tasksSorted().get(0));

        srvSave(emp3, testCaseMethod);

        Employee emp4 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertEquals("Retr. Set size", 2, emp4.tasksSorted().size());

        Assert.assertEquals("Owned value 0", task22, emp4.tasksSorted().get(0));
        Assert.assertEquals("Owned value 1", task3, emp4.tasksSorted().get(1));

        //--- Sort Order Additions

        Task task44 = emp4.tasksSorted().$();
        String description44 = "Work2 " + uniqueString();
        task44.description().setValue(description44);
        emp4.tasksSorted().add(task44);

        srvSave(emp4, testCaseMethod);

        Employee emp5 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertEquals("Retr. Set size", 3, emp5.tasksSorted().size());

        Assert.assertEquals("Added as last", task44, emp5.tasksSorted().get(2));

        // Just cleanup
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
