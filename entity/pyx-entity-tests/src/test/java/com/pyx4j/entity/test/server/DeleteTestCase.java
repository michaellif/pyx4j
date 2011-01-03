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
 * Created on Aug 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.server;

import java.util.List;

import junit.framework.Assert;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Organization;

public abstract class DeleteTestCase extends DatastoreTestBase {

    public void testSingleDelete() {
        Employee emp = EntityFactory.create(Employee.class);
        String empName = "Bob " + uniqueString();
        emp.firstName().setValue(empName);

        srv.persist(emp);

        EntityQueryCriteria<Employee> criteria1 = EntityQueryCriteria.create(Employee.class);
        criteria1.add(PropertyCriterion.eq(criteria1.meta().firstName(), empName));
        Employee emp1 = srv.retrieve(criteria1);
        Assert.assertNotNull("verify retrieve", emp1);
        Assert.assertEquals("PK Value", emp.getPrimaryKey(), emp1.getPrimaryKey());
        Assert.assertEquals("Search Value", empName, emp1.firstName().getValue());

        srv.delete(emp);

        List<Employee> emps = srv.query(criteria1);
        Assert.assertEquals("result set size", 0, emps.size());

        EntityQueryCriteria<Employee> criteria2 = EntityQueryCriteria.create(Employee.class);
        criteria2.add(PropertyCriterion.eq(IEntity.PRIMARY_KEY, emp.getPrimaryKey()));
        Employee emp2 = srv.retrieve(criteria1);
        Assert.assertNull("retrieve by PK", emp2);
    }

    public void testOwnedSetCascadeDelete() {
        Organization org = EntityFactory.create(Organization.class);
        org.name().setValue("org" + uniqueString());

        Department department = EntityFactory.create(Department.class);
        String deptName = "dept" + uniqueString();
        department.name().setValue(deptName);

        org.departments().add(department);

        srv.persist(org);
        Department department1 = srv.retrieve(Department.class, department.getPrimaryKey());
        assertNotNull("found by pk", department1);

        // test starts here
        srv.delete(org);

        // Department is removed as well.
        Department department2 = srv.retrieve(Department.class, department.getPrimaryKey());
        assertNull("found by pk", department2);
    }
}
