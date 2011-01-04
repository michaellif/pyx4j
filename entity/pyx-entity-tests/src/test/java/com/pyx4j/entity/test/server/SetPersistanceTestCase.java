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
        String empName = "emp" + uniqueString();
        double origSalary = 50000.0;
        employee1.salary().setValue(origSalary);
        employee1.firstName().setValue(empName);
        srv.persist(employee1);

        department.employees().add(employee1);
        org.departments().add(department);

        // test starts here
        srv.persist(org);

        Employee employee2 = EntityFactory.create(Employee.class);
        String empName2 = "emp" + uniqueString();
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
            fail("Should not save UnownedSetMemebr");
        }
        department.employees().remove(employee2);
        employee1.salary().setValue(120000.0);
        srv.persist(org);

        Organization org2 = srv.retrieve(Organization.class, org.getPrimaryKey());
        assertEquals("set size", 1, org2.departments().size());

        Employee employee1r = srv.retrieve(Employee.class, employee1.getPrimaryKey());
        Assert.assertEquals("salary no update", origSalary, employee1r.salary().getValue());
    }
}
