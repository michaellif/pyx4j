/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Dec 18, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import java.util.List;

import junit.framework.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.Trace;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Organization;
import com.pyx4j.entity.test.shared.domain.Task;
import com.pyx4j.entity.test.shared.domain.detached.MainHolderEnity;
import com.pyx4j.entity.test.shared.domain.join.AccPrincipal;
import com.pyx4j.entity.test.shared.domain.join.AccSubject;
import com.pyx4j.entity.test.shared.domain.join.AccSubjectPrincipal;
import com.pyx4j.entity.test.shared.domain.join.BRefReadChild;
import com.pyx4j.entity.test.shared.domain.join.BRefReadOwner;
import com.pyx4j.entity.test.shared.domain.join.OneToOneReadChild;
import com.pyx4j.entity.test.shared.domain.join.OneToOneReadOwner;

public abstract class QueryJoinRDBTestCase extends DatastoreTestBase {

    private static final Logger log = LoggerFactory.getLogger(QueryJoinRDBTestCase.class);

    public void testLeftJoin() {
        String setId = uniqueString();
        String searchBy = uniqueString();
        Department department1 = EntityFactory.create(Department.class);
        department1.name().setValue("A" + uniqueString());
        srv.persist(department1);

        Employee emp1 = EntityFactory.create(Employee.class);
        String emp1Name = "Bob " + uniqueString();
        emp1.firstName().setValue(emp1Name);
        emp1.homeAddress().streetName().setValue(searchBy);
        emp1.workAddress().streetName().setValue(setId);
        emp1.department().set(department1);
        srv.persist(emp1);

        Department department2 = EntityFactory.create(Department.class);
        department2.name().setValue("B" + uniqueString());
        srv.persist(department2);

        Employee emp2 = EntityFactory.create(Employee.class);
        String emp2Name = "Anna " + uniqueString();
        emp2.firstName().setValue(emp2Name);
        emp2.workAddress().streetName().setValue(setId);
        emp2.department().set(department2);
        srv.persist(emp2);

        if (PersistenceTrace.traceSql) {
            log.debug(Trace.id() + " setId = {} ", setId);
        }

        {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().workAddress().streetName(), setId));
            criteria.asc(criteria.proto().homeAddress().streetName());

            List<Employee> empsSortedAsc = srv.query(criteria);
            Assert.assertEquals("result set size", 2, empsSortedAsc.size());
            Assert.assertEquals("PK Value", empsSortedAsc.get(0).getPrimaryKey(), emp2.getPrimaryKey());
        }

        // Add second Join
        {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            criteria.or(PropertyCriterion.eq(criteria.proto().department().name(), department1.name()),
                    PropertyCriterion.eq(criteria.proto().department().name(), department2.name()));
            criteria.add(PropertyCriterion.eq(criteria.proto().workAddress().streetName(), setId));
            criteria.asc(criteria.proto().homeAddress().streetName());

            List<Employee> empsSortedAsc = srv.query(criteria);
            Assert.assertEquals("result set size", 2, empsSortedAsc.size());
            Assert.assertEquals("PK Value", empsSortedAsc.get(0).getPrimaryKey(), emp2.getPrimaryKey());
        }

        // Add second Join with sort
        {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            criteria.or(PropertyCriterion.eq(criteria.proto().department().name(), department1.name()),
                    PropertyCriterion.eq(criteria.proto().department().name(), department2.name()));
            criteria.add(PropertyCriterion.eq(criteria.proto().workAddress().streetName(), setId));
            criteria.asc(criteria.proto().homeAddress().streetName());
            criteria.asc(criteria.proto().department().name());

            List<Employee> empsSortedAsc = srv.query(criteria);
            Assert.assertEquals("result set size", 2, empsSortedAsc.size());
            Assert.assertEquals("PK Value", empsSortedAsc.get(0).getPrimaryKey(), emp2.getPrimaryKey());
        }

        // Add second LEFT Join
        {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().workAddress().streetName(), setId));
            criteria.asc(criteria.proto().department().name());
            criteria.asc(criteria.proto().homeAddress().streetName());

            List<Employee> empsSortedAsc = srv.query(criteria);
            Assert.assertEquals("result set size", 2, empsSortedAsc.size());
            Assert.assertEquals("PK Value", empsSortedAsc.get(0).getPrimaryKey(), emp1.getPrimaryKey());
        }
    }

    public void testRecurciveQueryCriteria() {
        String setId = uniqueString();
        String searchBy = uniqueString();

        Organization org1 = EntityFactory.create(Organization.class);
        org1.name().setValue("A" + uniqueString());

        Department department1 = EntityFactory.create(Department.class);
        department1.name().setValue("A" + uniqueString());
        department1.organization().set(org1);
        org1.departments().add(department1);
        srv.persist(org1);
        srv.persist(department1);

        Employee emp1 = EntityFactory.create(Employee.class);
        String emp1Name = "Bob " + uniqueString();
        emp1.firstName().setValue(emp1Name);
        emp1.homeAddress().streetName().setValue(searchBy);
        emp1.workAddress().streetName().setValue(setId);
        emp1.department().set(department1);
        srv.persist(emp1);

        Organization org2 = EntityFactory.create(Organization.class);
        org2.name().setValue("B" + uniqueString());

        Department department2 = EntityFactory.create(Department.class);
        department2.name().setValue("B" + uniqueString());
        department2.organization().set(org2);
        org2.departments().add(department2);
        srv.persist(org2);
        srv.persist(department2);

        Employee emp2 = EntityFactory.create(Employee.class);
        String emp2Name = "Anna " + uniqueString();
        emp2.firstName().setValue(emp2Name);
        emp2.workAddress().streetName().setValue(setId);
        emp2.department().set(department2);
        srv.persist(emp2);

        {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().workAddress().streetName(), setId));
            criteria.add(PropertyCriterion.eq(criteria.proto().department().organization(), org1));

            List<Employee> empsSortedAsc = srv.query(criteria);
            Assert.assertEquals("result set size", 1, empsSortedAsc.size());
            Assert.assertEquals("PK Value", empsSortedAsc.get(0).getPrimaryKey(), emp1.getPrimaryKey());
        }

        { // Verify join using auto-generated table
            EntityQueryCriteria<Department> criteria = EntityQueryCriteria.create(Department.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().organization().name(), org2.name()));

            List<Department> list = srv.query(criteria);
            Assert.assertEquals("result set size", 1, list.size());
            Assert.assertEquals("PK Value", list.get(0).getPrimaryKey(), department2.getPrimaryKey());
        }

        { // Verify join using auto-generated table on second level
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().workAddress().streetName(), setId));
            criteria.add(PropertyCriterion.eq(criteria.proto().department().organization().name(), org2.name()));

            List<Employee> empsSortedAsc = srv.query(criteria);
            Assert.assertEquals("result set size", 1, empsSortedAsc.size());
            Assert.assertEquals("PK Value", empsSortedAsc.get(0).getPrimaryKey(), emp2.getPrimaryKey());
        }

        {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().workAddress().streetName(), setId));
            criteria.desc(criteria.proto().department().organization().name());

            List<Employee> empsSortedAsc = srv.query(criteria);
            Assert.assertEquals("result set size", 2, empsSortedAsc.size());
            Assert.assertEquals("PK Value", empsSortedAsc.get(0).getPrimaryKey(), emp2.getPrimaryKey());
        }

        {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().workAddress().streetName(), setId));
            criteria.asc(criteria.proto().department().name());
            criteria.desc(criteria.proto().department().organization().name());

            List<Employee> empsSortedAsc = srv.query(criteria);
            Assert.assertEquals("result set size", 2, empsSortedAsc.size());
            Assert.assertEquals("PK Value", empsSortedAsc.get(0).getPrimaryKey(), emp1.getPrimaryKey());
        }
    }

    public void testOneToManyQueryCriteria() {
        String setId = uniqueString();
        String searchBy = uniqueString();

        Employee emp = EntityFactory.create(Employee.class);
        emp.firstName().setValue(uniqueString());
        emp.workAddress().streetName().setValue(setId);

        Task task = EntityFactory.create(Task.class);
        task.description().setValue(searchBy);
        emp.tasks().add(task);

        srv.persist(emp);

        {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().tasks().$().description(), searchBy));

            List<Employee> emps = srv.query(criteria);
            Assert.assertEquals("result set size", 1, emps.size());
            Assert.assertEquals("PK Value", emps.get(0).getPrimaryKey(), emp.getPrimaryKey());
        }
    }

    public void testOneToManyQueryCriteriaDistinct() {
        String setId = uniqueString();
        String searchBy = uniqueString();

        Employee emp = EntityFactory.create(Employee.class);
        emp.firstName().setValue(uniqueString());
        emp.workAddress().streetName().setValue(setId);

        Task task1 = EntityFactory.create(Task.class);
        task1.description().setValue(searchBy);
        emp.tasks().add(task1);

        Task task2 = EntityFactory.create(Task.class);
        task2.description().setValue(searchBy);
        emp.tasks().add(task2);

        srv.persist(emp);

        {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().tasks().$().description(), searchBy));

            List<Employee> emps = srv.query(criteria);
            Assert.assertEquals("result set size", 1, emps.size());
            Assert.assertEquals("PK Value", emps.get(0).getPrimaryKey(), emp.getPrimaryKey());
        }
    }

    //TODO Fix Me on Postgress
    public void testOneToManyQueryCriteriaDistinctAndSort() {
        String setId = uniqueString();
        String searchBy = uniqueString();

        Employee emp = EntityFactory.create(Employee.class);
        emp.firstName().setValue(uniqueString());
        emp.workAddress().streetName().setValue(setId);

        emp.department().name().setValue(uniqueString());
        srv.persist(emp.department());

        Task task1 = EntityFactory.create(Task.class);
        task1.description().setValue(searchBy);
        emp.tasks().add(task1);

        Task task2 = EntityFactory.create(Task.class);
        task2.description().setValue(searchBy);
        emp.tasks().add(task2);

        srv.persist(emp);

        {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().tasks().$().description(), searchBy));
            criteria.asc(criteria.proto().department().name());

            List<Employee> emps = srv.query(criteria);
            Assert.assertEquals("result set size", 1, emps.size());
            Assert.assertEquals("PK Value", emps.get(0).getPrimaryKey(), emp.getPrimaryKey());
        }
    }

    public void testOneToManyQueryNotExists() {
        String testId = uniqueString();
        String searchBy = uniqueString();
        String searchBy2 = uniqueString();

        Employee emp1 = EntityFactory.create(Employee.class);
        emp1.workAddress().streetName().setValue(testId);
        emp1.firstName().setValue("T1" + uniqueString());

        Task task1 = EntityFactory.create(Task.class);
        task1.description().setValue(searchBy);
        emp1.tasks().add(task1);
        srv.persist(emp1);

        Employee emp2 = EntityFactory.create(Employee.class);
        emp2.workAddress().streetName().setValue(testId);
        emp2.firstName().setValue("T0" + uniqueString());
        srv.persist(emp2);

        {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().workAddress().streetName(), testId));
            criteria.notExists(criteria.proto().tasks());

            List<Employee> emps = srv.query(criteria);
            Assert.assertEquals("result set size", 1, emps.size());
        }

        {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().workAddress().streetName(), testId));
            criteria.or(PropertyCriterion.notExists(criteria.proto().tasks()), PropertyCriterion.eq(criteria.proto().tasks().$().description(), searchBy));

            List<Employee> emps = srv.query(criteria);
            Assert.assertEquals("result set size", 2, emps.size());
        }

        Task task2 = EntityFactory.create(Task.class);
        task2.description().setValue(searchBy2);
        emp2.tasks().add(task2);
        srv.persist(emp2);

        //Test notExists as sub query
        {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().workAddress().streetName(), testId));
            criteria.notExists(criteria.proto().tasks(), PropertyCriterion.eq(criteria.proto().tasks().$().description(), searchBy));

            List<Employee> emps = srv.query(criteria);
            Assert.assertEquals("result set size", 1, emps.size());
        }

    }

    public void testOneToManyQueryJoinTableNotExists() {
        // Setup data
        String testId = uniqueString();
        String searchBy = uniqueString();

        BRefReadOwner owner1 = EntityFactory.create(BRefReadOwner.class);
        owner1.name().setValue("C1" + uniqueString());
        owner1.testId().setValue(testId);
        srv.persist(owner1);

        BRefReadChild c1 = EntityFactory.create(BRefReadChild.class);
        c1.name().setValue(searchBy);
        c1.testId().setValue(testId);
        c1.sortColumn().setValue(2);
        c1.bRefOwner().set(owner1);
        srv.persist(c1);

        BRefReadOwner owner2 = EntityFactory.create(BRefReadOwner.class);
        owner2.name().setValue("C0" + uniqueString());
        owner2.testId().setValue(testId);
        srv.persist(owner2);

        {
            EntityQueryCriteria<BRefReadOwner> criteria = EntityQueryCriteria.create(BRefReadOwner.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.or(PropertyCriterion.notExists(criteria.proto().children()), PropertyCriterion.eq(criteria.proto().children().$().name(), searchBy));

            List<BRefReadOwner> emps = srv.query(criteria);
            Assert.assertEquals("result set size", 2, emps.size());

        }

        {
            EntityQueryCriteria<BRefReadOwner> criteria = EntityQueryCriteria.create(BRefReadOwner.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.notExists(criteria.proto().children(), PropertyCriterion.eq(criteria.proto().children().$().name(), searchBy));

            List<BRefReadOwner> emps = srv.query(criteria);
            Assert.assertEquals("result set size", 1, emps.size());

        }

    }

    public void testJoinTable() {
        String testId = uniqueString();

        AccPrincipal principal1 = EntityFactory.create(AccPrincipal.class);
        principal1.name().setValue(uniqueString());
        principal1.testId().setValue(testId);
        srv.persist(principal1);

        AccPrincipal principal2 = EntityFactory.create(AccPrincipal.class);
        principal2.name().setValue(uniqueString());
        principal2.testId().setValue(testId);
        srv.persist(principal2);

        AccSubject subject1 = EntityFactory.create(AccSubject.class);
        subject1.name().setValue(uniqueString());
        subject1.testId().setValue(testId);
        srv.persist(subject1);

        AccSubject subject2 = EntityFactory.create(AccSubject.class);
        subject2.name().setValue(uniqueString());
        subject2.testId().setValue(testId);
        srv.persist(subject2);

        AccSubjectPrincipal join11 = EntityFactory.create(AccSubjectPrincipal.class);
        join11.principal().set(principal1);
        join11.subject().set(subject1);
        srv.persist(join11);

        AccSubjectPrincipal join22 = EntityFactory.create(AccSubjectPrincipal.class);
        join22.principal().set(principal2);
        join22.subject().set(subject2);
        srv.persist(join22);

        {
            EntityQueryCriteria<AccSubject> criteria = EntityQueryCriteria.create(AccSubject.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().access(), principal1));

            List<AccSubject> data = srv.query(criteria);
            // Test join
            Assert.assertEquals("result set size", 1, data.size());
            // Test data retrieval with JoinTable
            Assert.assertEquals("Data retrieved using JoinTable", AttachLevel.Detached, data.get(0).access().getAttachLevel());
            boolean ok = true;
            try {
                data.get(0).access().size();
                ok = false;
            } catch (Throwable e) {
            }
            Assert.assertEquals("Data retrieved using JoinTable", true, ok);
        }

        // Verify data retrival for second table
        {
            EntityQueryCriteria<AccPrincipal> criteria = EntityQueryCriteria.create(AccPrincipal.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().subjects(), subject1));
            AccPrincipal principal1r1 = srv.retrieve(criteria);
            Assert.assertEquals("Data retrieved using JoinTable", 1, principal1r1.subjects().size());
            Assert.assertTrue("Inserted value present", principal1r1.subjects().contains(subject1));
        }
        {
            AccPrincipal principal1r2 = srv.retrieve(AccPrincipal.class, principal1.getPrimaryKey());
            Assert.assertEquals("Data retrieved using JoinTable", 1, principal1r2.subjects().size());
            Assert.assertTrue("Inserted value present", principal1r2.subjects().contains(subject1));
        }
    }

    public void testOneToOneJTQueryNotExists() {
        String testId = uniqueString();
        String searchBy = uniqueString();

        OneToOneReadOwner o1 = EntityFactory.create(OneToOneReadOwner.class);
        o1.name().setValue("C1;" + uniqueString());
        o1.testId().setValue(testId);
        srv.persist(o1);

        OneToOneReadChild c1 = EntityFactory.create(OneToOneReadChild.class);
        c1.name().setValue(searchBy);
        c1.testId().setValue(testId);
        c1.o2oOwner().set(o1);
        srv.persist(c1);

        OneToOneReadOwner o2 = EntityFactory.create(OneToOneReadOwner.class);
        o2.name().setValue("C0;" + uniqueString());
        o2.testId().setValue(testId);
        srv.persist(o2);

        // Test implementation of NOT EXISTS, creation of second join
        {
            EntityQueryCriteria<OneToOneReadOwner> criteria = EntityQueryCriteria.create(OneToOneReadOwner.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            //TODO OR implementation for joins
            //criteria.or(PropertyCriterion.eq(criteria.proto().child().name(), c1.name()), PropertyCriterion.isNull(criteria.proto().child()));
            criteria.or(PropertyCriterion.notExists(criteria.proto().child()), PropertyCriterion.eq(criteria.proto().child().name(), searchBy));

            List<OneToOneReadOwner> data = srv.query(criteria);
            Assert.assertEquals("Found using JoinTable", 2, data.size());
        }

        //Test notExists as sub query
        {
            EntityQueryCriteria<OneToOneReadOwner> criteria = EntityQueryCriteria.create(OneToOneReadOwner.class);
            criteria.eq(criteria.proto().testId(), testId);
            criteria.notExists(criteria.proto().child(), PropertyCriterion.eq(criteria.proto().child().name(), searchBy));

            List<OneToOneReadOwner> data = srv.query(criteria);
            Assert.assertEquals("Found using JoinTable", 1, data.size());
            Assert.assertEquals("Found correct object", o2, data.get(0));
        }

    }

    public void testOneToOneUnidirectionalQueryNotExists() {
        String testId = uniqueString();
        String searchBy = uniqueString();

        MainHolderEnity o1 = EntityFactory.create(MainHolderEnity.class);
        o1.name().setValue(testId);
        o1.ownedEntity().name().setValue(searchBy);
        srv.persist(o1);

        MainHolderEnity o2 = EntityFactory.create(MainHolderEnity.class);
        o2.name().setValue(testId);
        srv.persist(o2);

        //Test notExists as sub query
        {
            EntityQueryCriteria<MainHolderEnity> criteria = EntityQueryCriteria.create(MainHolderEnity.class);
            criteria.eq(criteria.proto().name(), testId);
            criteria.notExists(criteria.proto().ownedEntity(), PropertyCriterion.eq(criteria.proto().ownedEntity().name(), searchBy));

            List<MainHolderEnity> data = srv.query(criteria);
            Assert.assertEquals("Found using reference", 1, data.size());
            Assert.assertEquals("Found correct object", o2, data.get(0));
        }

        o2.ownedEntity().name().setValue(searchBy);
        srv.persist(o2);
        {
            EntityQueryCriteria<MainHolderEnity> criteria = EntityQueryCriteria.create(MainHolderEnity.class);
            criteria.eq(criteria.proto().name(), testId);
            criteria.notExists(criteria.proto().ownedEntity(), PropertyCriterion.eq(criteria.proto().ownedEntity().name(), searchBy));

            List<MainHolderEnity> data = srv.query(criteria);
            Assert.assertEquals("Found using reference", 0, data.size());
        }
    }

    public void testOneToOneUnidirectional2XQueryNotExists() {
        String testId = uniqueString();
        String searchBy = uniqueString();

        MainHolderEnity o1 = EntityFactory.create(MainHolderEnity.class);
        o1.name().setValue(testId);
        o1.ownedEntity().detachedEntity().name().setValue(searchBy);
        srv.persist(o1);

        MainHolderEnity o2 = EntityFactory.create(MainHolderEnity.class);
        o2.name().setValue(testId);
        srv.persist(o2);

        //Test notExists as sub query
        {
            EntityQueryCriteria<MainHolderEnity> criteria = EntityQueryCriteria.create(MainHolderEnity.class);
            criteria.eq(criteria.proto().name(), testId);
            criteria.notExists(criteria.proto().ownedEntity(), PropertyCriterion.eq(criteria.proto().ownedEntity().detachedEntity().name(), searchBy));

            List<MainHolderEnity> data = srv.query(criteria);
            Assert.assertEquals("Found using reference", 1, data.size());
            Assert.assertEquals("Found correct object", o2, data.get(0));
        }

        {
            EntityQueryCriteria<MainHolderEnity> criteria = EntityQueryCriteria.create(MainHolderEnity.class);
            criteria.eq(criteria.proto().name(), testId);
            criteria.notExists(criteria.proto().ownedEntity().detachedEntity(),
                    PropertyCriterion.eq(criteria.proto().ownedEntity().detachedEntity().name(), searchBy));

            List<MainHolderEnity> data = srv.query(criteria);
            Assert.assertEquals("Found using reference", 1, data.size());
            Assert.assertEquals("Found correct object", o2, data.get(0));
        }

        o2.ownedEntity().detachedEntity().name().setValue(searchBy);
        srv.persist(o2);
        {
            EntityQueryCriteria<MainHolderEnity> criteria = EntityQueryCriteria.create(MainHolderEnity.class);
            criteria.eq(criteria.proto().name(), testId);
            criteria.notExists(criteria.proto().ownedEntity(), PropertyCriterion.eq(criteria.proto().ownedEntity().detachedEntity().name(), searchBy));

            List<MainHolderEnity> data = srv.query(criteria);
            Assert.assertEquals("Found using reference", 0, data.size());
        }

        {
            EntityQueryCriteria<MainHolderEnity> criteria = EntityQueryCriteria.create(MainHolderEnity.class);
            criteria.eq(criteria.proto().name(), testId);
            criteria.notExists(criteria.proto().ownedEntity().detachedEntity(),
                    PropertyCriterion.eq(criteria.proto().ownedEntity().detachedEntity().name(), searchBy));

            List<MainHolderEnity> data = srv.query(criteria);
            Assert.assertEquals("Found using reference", 0, data.size());
        }
    }
}
