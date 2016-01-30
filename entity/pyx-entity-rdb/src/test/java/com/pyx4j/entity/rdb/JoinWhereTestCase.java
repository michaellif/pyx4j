/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Jan 29, 2016
 * @author vlads
 */
package com.pyx4j.entity.rdb;

import java.util.List;

import org.junit.Assert;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.join.org5where.Department5;
import com.pyx4j.entity.test.shared.domain.join.org5where.Employee5;
import com.pyx4j.entity.test.shared.domain.join.org5where.Employee5.Employee5Type;
import com.pyx4j.entity.test.shared.domain.join.org6where.Department6;
import com.pyx4j.entity.test.shared.domain.join.org6where.Employee6;
import com.pyx4j.entity.test.shared.domain.join.org6where.Employee6.Employee6Type;

public abstract class JoinWhereTestCase extends DatastoreTestBase {

    public void testJoinWhereMultipeOneToOneNotOwned() {
        String testId = uniqueString();

        Department6 department1 = EntityFactory.create(Department6.class);
        String dep1name = "D1 " + uniqueString();
        department1.testId().setValue(testId);
        department1.name().setValue(dep1name);
        srv.persist(department1);

        Employee6 emp11 = EntityFactory.create(Employee6.class);
        String emp11Name = "manager 1.1 " + uniqueString();
        emp11.testId().setValue(testId);
        emp11.name().setValue(emp11Name);
        emp11.type().setValue(Employee6Type.manager);
        emp11.department().set(department1);
        srv.persist(emp11);

        Employee6 emp12 = EntityFactory.create(Employee6.class);
        String emp12Name = "director 1.2 " + uniqueString();
        emp12.testId().setValue(testId);
        emp12.name().setValue(emp12Name);
        emp12.type().setValue(Employee6Type.director);
        emp12.department().set(department1);
        srv.persist(emp12);

        // See if retrieval works
        {
            Department6 department1r = srv.retrieve(Department6.class, department1.getPrimaryKey());

            Assert.assertEquals("Member retrieved using JoinWhere", emp11, department1r.manager());
            Assert.assertEquals("Member retrieved using JoinWhere", emp12, department1r.director());
        }

        // See if query works
        {
            EntityQueryCriteria<Department6> criteria = EntityQueryCriteria.create(Department6.class);
            criteria.eq(criteria.proto().testId(), testId);
            criteria.in(criteria.proto().director().name(), emp12Name);

            List<Department6> retrived = srv.query(criteria);
            Assert.assertEquals("result set size", 1, retrived.size());
        }

        {
            EntityQueryCriteria<Department6> criteria = EntityQueryCriteria.create(Department6.class);
            criteria.eq(criteria.proto().testId(), testId);
            criteria.in(criteria.proto().employees().$().name(), emp12Name);

            List<Department6> retrived = srv.query(criteria);
            Assert.assertEquals("result set size", 0, retrived.size());
        }
    }

    public void testJoinWhereOneToManyNotOwned() {
        String testId = uniqueString();

        Department6 department1 = EntityFactory.create(Department6.class);
        String dep1name = "D1 " + uniqueString();
        department1.testId().setValue(testId);
        department1.name().setValue(dep1name);
        srv.persist(department1);

        Employee6 emp11 = EntityFactory.create(Employee6.class);
        String emp11Name = "manager 1.1 " + uniqueString();
        emp11.testId().setValue(testId);
        emp11.name().setValue(emp11Name);
        emp11.type().setValue(Employee6Type.manager);
        emp11.department().set(department1);
        srv.persist(emp11);

        Employee6 emp12 = EntityFactory.create(Employee6.class);
        String emp12Name = "employee 1.2 " + uniqueString();
        emp12.testId().setValue(testId);
        emp12.name().setValue(emp12Name);
        emp12.type().setValue(Employee6Type.employee);
        emp12.department().set(department1);
        srv.persist(emp12);

        Employee6 emp13 = EntityFactory.create(Employee6.class);
        String emp13Name = "employee 1.3 " + uniqueString();
        emp13.testId().setValue(testId);
        emp13.name().setValue(emp13Name);
        emp13.type().setValue(Employee6Type.employee);
        emp13.department().set(department1);
        srv.persist(emp13);

        // See if retrieval works
        {
            Department6 department1r = srv.retrieve(Department6.class, department1.getPrimaryKey());

            Assert.assertEquals("Member retrieved using JoinWhere", emp11, department1r.manager());
            Assert.assertTrue("Member retrieved using JoinWhere", department1r.director().isNull());

            Assert.assertEquals("Collection retrieved using JoinWhere", 2, department1r.employees().size());
        }

        // See if query works
        {
            EntityQueryCriteria<Department6> criteria = EntityQueryCriteria.create(Department6.class);
            criteria.eq(criteria.proto().testId(), testId);
            criteria.in(criteria.proto().director().name(), emp12Name);

            List<Department6> retrived = srv.query(criteria);
            Assert.assertEquals("result set size", 0, retrived.size());
        }

        {
            EntityQueryCriteria<Department6> criteria = EntityQueryCriteria.create(Department6.class);
            criteria.eq(criteria.proto().testId(), testId);
            criteria.in(criteria.proto().employees().$().name(), emp12Name);

            List<Department6> retrived = srv.query(criteria);
            Assert.assertEquals("result set size", 1, retrived.size());
        }
    }

    public void testJoinWhereMultipeOneToOneOwned() {
        String testId = uniqueString();

        Employee5 emp11 = EntityFactory.create(Employee5.class);
        String emp11Name = "manager 1.1 " + uniqueString();
        emp11.testId().setValue(testId);
        emp11.name().setValue(emp11Name);
        emp11.type().setValue(Employee5Type.manager); // TODO remove

        Employee5 emp12 = EntityFactory.create(Employee5.class);
        String emp12Name = "director 1.2 " + uniqueString();
        emp12.testId().setValue(testId);
        emp12.name().setValue(emp12Name);
        emp12.type().setValue(Employee5Type.director); // TODO remove

        Department5 department1 = EntityFactory.create(Department5.class);
        String dep1name = "D1 " + uniqueString();
        department1.testId().setValue(testId);
        department1.name().setValue(dep1name);
        department1.manager().set(emp11);
        department1.director().set(emp12);
        srv.persist(department1);

        // See if retrieval works
        {
            Department5 department1r = srv.retrieve(Department5.class, department1.getPrimaryKey());

            Assert.assertEquals("Member retrieved using JoinWhere", emp11, department1r.manager());
            Assert.assertEquals("Member retrieved using JoinWhere", Employee5Type.manager, department1r.manager().type().getValue());
            Assert.assertEquals("Member retrieved using JoinWhere", emp12, department1r.director());
            Assert.assertEquals("Member retrieved using JoinWhere", Employee5Type.director, department1r.director().type().getValue());
        }

        // See if query works
        {
            EntityQueryCriteria<Department5> criteria = EntityQueryCriteria.create(Department5.class);
            criteria.eq(criteria.proto().testId(), testId);
            criteria.in(criteria.proto().director().name(), emp12Name);

            List<Department5> retrived = srv.query(criteria);
            Assert.assertEquals("result set size", 1, retrived.size());
        }

        {
            EntityQueryCriteria<Department5> criteria = EntityQueryCriteria.create(Department5.class);
            criteria.eq(criteria.proto().testId(), testId);
            criteria.in(criteria.proto().employees().$().name(), emp12Name);

            List<Department5> retrived = srv.query(criteria);
            Assert.assertEquals("result set size", 0, retrived.size());
        }
    }

    public void testJoinWhereOneToManyOwned() {
        String testId = uniqueString();

        Employee5 emp11 = EntityFactory.create(Employee5.class);
        String emp11Name = "manager 1.1 " + uniqueString();
        emp11.testId().setValue(testId);
        emp11.name().setValue(emp11Name);
        emp11.type().setValue(Employee5Type.manager);// TODO remove

        Employee5 emp12 = EntityFactory.create(Employee5.class);
        String emp12Name = "employee 1.2 " + uniqueString();
        emp12.testId().setValue(testId);
        emp12.name().setValue(emp12Name);
        emp12.type().setValue(Employee5Type.employee);// TODO remove

        Employee5 emp13 = EntityFactory.create(Employee5.class);
        String emp13Name = "employee 1.3 " + uniqueString();
        emp13.testId().setValue(testId);
        emp13.name().setValue(emp13Name);
        emp13.type().setValue(Employee5Type.employee);// TODO remove

        Department5 department1 = EntityFactory.create(Department5.class);
        String dep1name = "D1 " + uniqueString();
        department1.testId().setValue(testId);
        department1.name().setValue(dep1name);
        department1.manager().set(emp11);
        department1.employees().add(emp12);
        department1.employees().add(emp13);
        srv.persist(department1);

        // See if retrieval works
        {
            Department5 department1r = srv.retrieve(Department5.class, department1.getPrimaryKey());

            Assert.assertEquals("Member retrieved using JoinWhere", emp11, department1r.manager());
            Assert.assertTrue("Member retrieved using JoinWhere", department1r.director().isNull());

            Assert.assertEquals("Collection retrieved using JoinWhere", 2, department1r.employees().size());
        }

        // See if query works
        {
            EntityQueryCriteria<Department5> criteria = EntityQueryCriteria.create(Department5.class);
            criteria.eq(criteria.proto().testId(), testId);
            criteria.in(criteria.proto().director().name(), emp12Name);

            List<Department5> retrived = srv.query(criteria);
            Assert.assertEquals("result set size", 0, retrived.size());
        }

        {
            EntityQueryCriteria<Department5> criteria = EntityQueryCriteria.create(Department5.class);
            criteria.eq(criteria.proto().testId(), testId);
            criteria.in(criteria.proto().employees().$().name(), emp12Name);

            List<Department5> retrived = srv.query(criteria);
            Assert.assertEquals("result set size", 1, retrived.size());
        }
    }

}
