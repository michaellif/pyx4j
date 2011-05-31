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
 * Created on Jan 1, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.server;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Status;

public abstract class QueryTestCase extends DatastoreTestBase {

    protected Employee metaEmp;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        metaEmp = EntityFactory.create(Employee.class);
    }

    public void testQueryByString() {
        Employee emp = EntityFactory.create(Employee.class);
        String empName = "Bob " + uniqueString();
        emp.firstName().setValue(empName);

        srv.persist(emp);

        EntityQueryCriteria<Employee> criteria1 = EntityQueryCriteria.create(Employee.class);
        criteria1.add(PropertyCriterion.eq(criteria1.proto().firstName(), empName));
        Employee emp1 = srv.retrieve(criteria1);
        Assert.assertNotNull("retrieve", emp1);
        Assert.assertEquals("PK Value", emp.getPrimaryKey(), emp1.getPrimaryKey());
        Assert.assertEquals("Search Value", empName, emp1.firstName().getValue());

        List<Employee> emps = srv.query(criteria1);
        Assert.assertEquals("result set size", 1, emps.size());
        emp1 = emps.get(0);
        Assert.assertEquals("PK Value", emp.getPrimaryKey(), emp1.getPrimaryKey());
        Assert.assertEquals("Search Value", empName, emp1.firstName().getValue());
    }

    protected void execTestQuery(IObject<?> member, Serializable value) {
        execTestQuery(member, value, false);
    }

    protected void execTestQuery(IObject<?> member, Serializable value, boolean addAndName) {
        Employee emp = EntityFactory.create(Employee.class);
        String empName = "Bob " + uniqueString();
        emp.firstName().setValue(empName);

        Assert.assertEquals("Search Value Class", value.getClass(), member.getValueClass());
        emp.setMemberValue(member.getFieldName(), value);

        srv.persist(emp);

        EntityQueryCriteria<Employee> criteria1 = EntityQueryCriteria.create(Employee.class);
        criteria1.add(PropertyCriterion.eq(member, value));
        if (addAndName) {
            criteria1.add(PropertyCriterion.eq(criteria1.proto().firstName(), empName));
        }
        Employee emp1 = srv.retrieve(criteria1);
        Assert.assertNotNull("retrieve by " + member.getFieldName() + " " + member.getValueClass(), emp1);
        Assert.assertEquals("PK Value", emp.getPrimaryKey(), emp1.getPrimaryKey());
        Assert.assertEquals("Search Value Class", member.getValueClass(), emp1.getMemberValue(member.getFieldName()).getClass());
        Assert.assertEquals("Search Value", value, emp1.getMemberValue(member.getFieldName()));
        Assert.assertEquals("Verify Value", empName, emp1.firstName().getValue());

        List<Employee> emps = srv.query(criteria1);
        Assert.assertEquals("result set size", 1, emps.size());
        emp1 = emps.get(0);
        Assert.assertEquals("PK Value", emp.getPrimaryKey(), emp1.getPrimaryKey());
        Assert.assertEquals("Search Value", value, emp1.getMemberValue(member.getFieldName()));
        Assert.assertEquals("Verify Value", empName, emp1.firstName().getValue());
    }

    public void testQueryByDate() {
        execTestQuery(metaEmp.from(), new Date(1000 * (new Date().getTime() / 1000)));
    }

    public void testQueryByLong() {
        execTestQuery(metaEmp.holidays(), Long.valueOf(new Date().getTime()));
    }

    public void testQueryByInteger() {
        Integer i = (int) (new Date().getTime() % 100000);
        execTestQuery(metaEmp.rating(), i);
    }

    public void testQueryByDouble() {
        Double d = (double) new Date().getTime();
        execTestQuery(metaEmp.salary(), d);
    }

    public void testQueryByEnum() {
        execTestQuery(metaEmp.accessStatus(), Status.DEACTIVATED, true);
    }

    public void testQueryByBoolean() {
        execTestQuery(metaEmp.reliable(), Boolean.FALSE, true);
    }

    public void testQueryByEntity() {
        Employee mrg = EntityFactory.create(Employee.class);
        String mgrName = "Manager " + uniqueString();
        mrg.firstName().setValue(mgrName);
        srv.persist(mrg);

        Employee emp = EntityFactory.create(Employee.class);
        String empName = "Bob " + uniqueString();
        emp.firstName().setValue(empName);

        emp.manager().set(mrg);

        srv.persist(emp);

        EntityQueryCriteria<Employee> criteria1 = EntityQueryCriteria.create(Employee.class);
        criteria1.add(PropertyCriterion.eq(criteria1.proto().manager(), mrg));

        Employee emp1 = srv.retrieve(criteria1);
        Assert.assertNotNull("retrieve", emp1);
        Assert.assertEquals("PK Value", emp.getPrimaryKey(), emp1.getPrimaryKey());
        Assert.assertEquals("Search Value", mrg, emp1.manager());
        Assert.assertEquals("Verify Value", empName, emp1.firstName().getValue());

        List<Employee> emps = srv.query(criteria1);
        Assert.assertEquals("result set size", 1, emps.size());
        emp1 = emps.get(0);
        Assert.assertEquals("PK Value", emp.getPrimaryKey(), emp1.getPrimaryKey());
        Assert.assertEquals("Search Value", mrg, emp1.manager());
        Assert.assertEquals("Verify Value", empName, emp1.firstName().getValue());
    }

    public void testQueryBySetEntityMember() {
        // Setup data
        Employee employee1 = EntityFactory.create(Employee.class);
        employee1.firstName().setValue("emp1" + uniqueString());
        srv.persist(employee1);

        Department department = EntityFactory.create(Department.class);
        String deptName = "Dept " + uniqueString();
        department.name().setValue(deptName);
        department.employees().add(employee1);
        srv.persist(department);

        // test starts here
        EntityQueryCriteria<Department> criteria = EntityQueryCriteria.create(Department.class);
        criteria.add(PropertyCriterion.eq(department.employees(), employee1));
        List<Department> departments2 = srv.query(criteria);
        Assert.assertEquals("Retr All List size", 1, departments2.size());
        Assert.assertEquals("Retr All department.name", deptName, departments2.get(0).name().getValue());

        List<Key> departmentsIds = srv.queryKeys(criteria);
        Assert.assertEquals("Retr Keys List size", 1, departmentsIds.size());
        Assert.assertEquals("Retr All department.id", department.getPrimaryKey(), departmentsIds.get(0));

    }
}
