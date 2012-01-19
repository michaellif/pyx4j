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
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Organization;
import com.pyx4j.entity.test.shared.domain.join.AccPrincipal;
import com.pyx4j.entity.test.shared.domain.join.AccSubject;
import com.pyx4j.entity.test.shared.domain.join.AccSubjectPrincipal;

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

        if (EntityPersistenceServiceRDB.traceSql) {
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
        srv.persist(org1);

        Department department1 = EntityFactory.create(Department.class);
        department1.name().setValue("A" + uniqueString());
        department1.organization().set(org1);
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
        srv.persist(org2);

        Department department2 = EntityFactory.create(Department.class);
        department2.name().setValue("B" + uniqueString());
        department2.organization().set(org2);
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
            AccPrincipal principal1r2 = srv.retrieve(AccPrincipal.class, subject1.getPrimaryKey());
            Assert.assertEquals("Data retrieved using JoinTable", 1, principal1r2.subjects().size());
            Assert.assertTrue("Inserted value present", principal1r2.subjects().contains(subject1));
        }
    }
}
