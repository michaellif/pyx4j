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
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Status;
import com.pyx4j.entity.test.shared.domain.Task;

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

    protected void execTestQuery(IObject<?> member, Serializable value, Restriction restriction, Serializable queryValue) {
        execTestQuery(member, value, restriction, queryValue, true);
    }

    protected void execTestQuery(IObject<?> member, Serializable value, boolean addAndName) {
        execTestQuery(member, value, Restriction.EQUAL, value, addAndName);
    }

    protected void execTestQuery(IObject<?> member, Serializable value, Restriction restriction, Serializable queryValue, boolean addAndName) {
        Employee emp = EntityFactory.create(Employee.class);
        String empName = "Bob " + uniqueString();
        emp.firstName().setValue(empName);

        Assert.assertEquals("Search Value Class", value.getClass(), member.getValueClass());
        emp.setMemberValue(member.getFieldName(), value);

        srv.persist(emp);
        Assert.assertNotNull("PK assigned", emp.getPrimaryKey());
        Assert.assertNotNull("retrieved  by PK", srv.retrieve(Employee.class, emp.getPrimaryKey()));

        EntityQueryCriteria<Employee> criteria1 = EntityQueryCriteria.create(Employee.class);
        criteria1.add(new PropertyCriterion(member, restriction, queryValue));
        if (addAndName) {
            criteria1.add(PropertyCriterion.eq(criteria1.proto().firstName(), empName));
        }
        Employee emp1 = srv.retrieve(criteria1);
        Assert.assertNotNull("retrieved by " + member.getFieldName() + " " + member.getValueClass(), emp1);
        Assert.assertEquals("PK Value", emp.getPrimaryKey(), emp1.getPrimaryKey());
        Assert.assertEquals("Search Value Class", member.getValueClass(), emp1.getMemberValue(member.getFieldName()).getClass());
        assertValueEquals("Search Value", value, emp1.getMemberValue(member.getFieldName()));

        Assert.assertEquals("Verify Value", empName, emp1.firstName().getValue());

        List<Employee> emps = srv.query(criteria1);
        Assert.assertEquals("result set size", 1, emps.size());
        emp1 = emps.get(0);
        Assert.assertEquals("PK Value", emp.getPrimaryKey(), emp1.getPrimaryKey());
        assertValueEquals("Search Value", value, emp1.getMemberValue(member.getFieldName()));
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
        execTestQuery(metaEmp.flagDouble(), d);
    }

    //TODO Make it work on GAE
    public void testQueryByBigDecimal() {
        {
            BigDecimal d = new BigDecimal(new Date().getTime());
            execTestQuery(metaEmp.salary(), d);
        }

        {
            execTestQuery(metaEmp.salary(), new BigDecimal("10.00"), Restriction.EQUAL, new BigDecimal("10.000"));
            execTestQuery(metaEmp.salary(), new BigDecimal("10.00"), Restriction.NOT_EQUAL, new BigDecimal("0.00"));
            execTestQuery(metaEmp.salary(), new BigDecimal("10.00"), Restriction.GREATER_THAN, new BigDecimal("5.0"));
            execTestQuery(metaEmp.salary(), new BigDecimal("10.00"), Restriction.LESS_THAN, new BigDecimal("15.0"));
        }
    }

    //TODO Make it work on GAE
    public void testQueryByEnum() {
        execTestQuery(metaEmp.accessStatus(), Status.DEACTIVATED, true);
    }

    //TODO Make it work on GAE
    public void testQueryByBoolean() {
        execTestQuery(metaEmp.reliable(), Boolean.FALSE, true);
    }

    public void testQueryByEntity() {
        Employee mgr = EntityFactory.create(Employee.class);
        String mgrName = "Manager " + uniqueString();
        mgr.firstName().setValue(mgrName);
        srv.persist(mgr);

        Employee emp = EntityFactory.create(Employee.class);
        String empName = "Bob " + uniqueString();
        emp.firstName().setValue(empName);

        emp.manager().set(mgr);

        srv.persist(emp);

        EntityQueryCriteria<Employee> criteria1 = EntityQueryCriteria.create(Employee.class);
        criteria1.add(PropertyCriterion.eq(criteria1.proto().manager(), mgr));

        Employee emp1 = srv.retrieve(criteria1);
        Assert.assertNotNull("retrieve", emp1);
        Assert.assertEquals("PK Value", emp.getPrimaryKey(), emp1.getPrimaryKey());
        Assert.assertEquals("Search Value", mgr, emp1.manager());
        Assert.assertEquals("Verify Value", empName, emp1.firstName().getValue());

        List<Employee> emps = srv.query(criteria1);
        Assert.assertEquals("result set size", 1, emps.size());
        emp1 = emps.get(0);
        Assert.assertEquals("PK Value", emp.getPrimaryKey(), emp1.getPrimaryKey());
        Assert.assertEquals("Search Value", mgr, emp1.manager());
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

    public void testQueryByPrimitiveSet() {
        String testId = uniqueString();

        Task task1 = EntityFactory.create(Task.class);
        task1.description().setValue(testId);
        task1.notes().add("Note1");
        task1.notes().add("Note2");
        srv.persist(task1);

        Task task2 = EntityFactory.create(Task.class);
        task2.description().setValue(testId);
        task2.notes().add("Note2");
        task2.notes().add("Note3");
        srv.persist(task2);

        {
            EntityQueryCriteria<Task> criteria = EntityQueryCriteria.create(Task.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().description(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().notes(), "Note1"));
            List<Task> list1 = srv.query(criteria);
            Assert.assertEquals("Retr All List size", 1, list1.size());
        }

        {
            EntityQueryCriteria<Task> criteria = EntityQueryCriteria.create(Task.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().description(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().notes(), "Note2"));
            List<Task> list1 = srv.query(criteria);
            Assert.assertEquals("Retr All List size", 2, list1.size());
        }
    }
}
