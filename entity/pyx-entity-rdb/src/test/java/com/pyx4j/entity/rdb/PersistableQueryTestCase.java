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
 * Created on Apr 21, 2015
 * @author vlads
 */
package com.pyx4j.entity.rdb;

import java.util.List;

import org.junit.Assert;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.query.QueryBinder;
import com.pyx4j.entity.core.query.QueryStorage;
import com.pyx4j.entity.server.query.PersistableQueryFacade;
import com.pyx4j.entity.server.query.QueryBinderBuilder;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Employee.EmploymentStatus;
import com.pyx4j.entity.test.shared.domain.EmployeeQuery;
import com.pyx4j.entity.test.shared.domain.TestsQueryCriteriaColumnStorage;

public abstract class PersistableQueryTestCase extends DatastoreTestBase {

    private String setId;

    private Employee emp1;

    private Employee emp2;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Setup Data
        setId = uniqueString();
        emp1 = EntityFactory.create(Employee.class);
        String emp1Name = "Bob " + uniqueString();
        emp1.firstName().setValue(emp1Name);
        emp1.workAddress().streetName().setValue(setId);
        srv.persist(emp1);

        emp2 = EntityFactory.create(Employee.class);
        String emp2Name = "Harry " + uniqueString();
        emp2.firstName().setValue(emp2Name);
        emp2.workAddress().streetName().setValue(setId);
        srv.persist(emp2);
    }

    @Override
    protected void tearDown() throws Exception {
        EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
        criteria.eq(criteria.proto().workAddress().streetName(), setId);
        srv.delete(criteria);
        super.tearDown();
    }

    private QueryBinder<Employee, EmployeeQuery> createEmployeeQueryBinder() {
        QueryBinderBuilder<Employee, EmployeeQuery> b = new QueryBinderBuilder<>(EmployeeQuery.class);
        b.map(b.proto().firstName(), b.criteriaProto().firstName());
        b.map(b.proto().department(), b.criteriaProto().department());
        b.map(b.proto().employmentStatus(), b.criteriaProto().employmentStatus());
        return b.build();
    }

    public void testQuery() {
        QueryBinder<Employee, EmployeeQuery> binder = createEmployeeQueryBinder();

        // Query
        {
            EmployeeQuery query = EntityFactory.create(EmployeeQuery.class);
            query.firstName().stringValue().setValue("Bob");

            EntityQueryCriteria<Employee> criteria = ServerSideFactory.create(PersistableQueryFacade.class).convertToCriteria(query, binder);
            criteria.eq(criteria.proto().workAddress().streetName(), setId);
            List<Employee> emps = srv.query(criteria);
            Assert.assertEquals("result set size", 1, emps.size());
            Assert.assertEquals("right selection ", emp1, emps.get(0));
        }
        {
            EmployeeQuery query = EntityFactory.create(EmployeeQuery.class);
            query.firstName().stringValue().setValue("Harry");

            EntityQueryCriteria<Employee> criteria = ServerSideFactory.create(PersistableQueryFacade.class).convertToCriteria(query, binder);
            criteria.eq(criteria.proto().workAddress().streetName(), setId);
            List<Employee> emps = srv.query(criteria);
            Assert.assertEquals("result set size", 1, emps.size());
            Assert.assertEquals("right selection ", emp2, emps.get(0));
        }
    }

    public void testQueryPersistence() {
        ServerSideFactory.create(PersistableQueryFacade.class).registerColumnStorageClass(TestsQueryCriteriaColumnStorage.class);
        ServerSideFactory.create(PersistableQueryFacade.class).preloadColumnStorage();

        QueryStorage storeHere = EntityFactory.create(QueryStorage.class);

        {
            EmployeeQuery query = EntityFactory.create(EmployeeQuery.class);
            query.firstName().stringValue().setValue("Bob");
            ServerSideFactory.create(PersistableQueryFacade.class).persistQuery(query, storeHere);
        }

        // Query
        {
            QueryStorage storeHereId = storeHere.<QueryStorage> createIdentityStub();

            EmployeeQuery query = ServerSideFactory.create(PersistableQueryFacade.class).retriveQuery(EmployeeQuery.class, storeHereId);

            Assert.assertEquals("stored value", "Bob", query.firstName().stringValue().getValue());

            // Use stored  Query
            EntityQueryCriteria<Employee> criteria = ServerSideFactory.create(PersistableQueryFacade.class).convertToCriteria(query,
                    createEmployeeQueryBinder());
            criteria.eq(criteria.proto().workAddress().streetName(), setId);
            List<Employee> emps = srv.query(criteria);
            Assert.assertEquals("result set size", 1, emps.size());
            Assert.assertEquals("right selection ", emp1, emps.get(0));
        }

        // Modify
        {
            EmployeeQuery query = ServerSideFactory.create(PersistableQueryFacade.class).retriveQuery(EmployeeQuery.class, storeHere);
            query.firstName().stringValue().setValue("Other");
            ServerSideFactory.create(PersistableQueryFacade.class).persistQuery(query, storeHere);
        }

        // Use after modifications
        {
            EmployeeQuery query = ServerSideFactory.create(PersistableQueryFacade.class).retriveQuery(EmployeeQuery.class, storeHere);
            EntityQueryCriteria<Employee> criteria = ServerSideFactory.create(PersistableQueryFacade.class).convertToCriteria(query,
                    createEmployeeQueryBinder());
            criteria.eq(criteria.proto().workAddress().streetName(), setId);
            List<Employee> emps = srv.query(criteria);
            Assert.assertEquals("result set size", 0, emps.size());
        }
    }

    public void testQueryEntityConditionPersistence() {
        Department department1 = EntityFactory.create(Department.class);
        department1.name().setValue("A" + uniqueString());
        srv.persist(department1);
        emp1.department().set(department1);
        srv.persist(emp1);

        ServerSideFactory.create(PersistableQueryFacade.class).registerColumnStorageClass(TestsQueryCriteriaColumnStorage.class);
        ServerSideFactory.create(PersistableQueryFacade.class).preloadColumnStorage();
        ServerSideFactory.create(PersistableQueryFacade.class).registerBinder(EmployeeQuery.class, createEmployeeQueryBinder());

        QueryStorage storeHere = EntityFactory.create(QueryStorage.class);

        {
            EmployeeQuery query = EntityFactory.create(EmployeeQuery.class);
            query.department().references().add(department1);
            ServerSideFactory.create(PersistableQueryFacade.class).persistQuery(query, storeHere);
        }

        // Query
        {
            QueryStorage storeHereId = storeHere.<QueryStorage> createIdentityStub();

            EmployeeQuery query = ServerSideFactory.create(PersistableQueryFacade.class).retriveQuery(EmployeeQuery.class, storeHereId);

            Assert.assertTrue("stored value", query.department().keys().contains(department1.getPrimaryKey()));

            // Use stored  Query
            EntityQueryCriteria<Employee> criteria = ServerSideFactory.create(PersistableQueryFacade.class).convertToCriteria(query);
            criteria.eq(criteria.proto().workAddress().streetName(), setId);
            List<Employee> emps = srv.query(criteria);
            Assert.assertEquals("result set size", 1, emps.size());
            Assert.assertEquals("right selection ", emp1, emps.get(0));
        }
    }

    public void testQueryEnumConditionPersistence() {
        emp1.employmentStatus().setValue(EmploymentStatus.PART_TIME);
        srv.persist(emp1);

        ServerSideFactory.create(PersistableQueryFacade.class).registerColumnStorageClass(TestsQueryCriteriaColumnStorage.class);
        ServerSideFactory.create(PersistableQueryFacade.class).preloadColumnStorage();
        ServerSideFactory.create(PersistableQueryFacade.class).registerBinder(EmployeeQuery.class, createEmployeeQueryBinder());

        QueryStorage storeHere = EntityFactory.create(QueryStorage.class);

        {
            EmployeeQuery query = EntityFactory.create(EmployeeQuery.class);
            query.employmentStatus().values().add(EmploymentStatus.PART_TIME);
            ServerSideFactory.create(PersistableQueryFacade.class).persistQuery(query, storeHere);
        }

        // Query
        {
            QueryStorage storeHereId = storeHere.<QueryStorage> createIdentityStub();

            EmployeeQuery query = ServerSideFactory.create(PersistableQueryFacade.class).retriveQuery(EmployeeQuery.class, storeHereId);

            Assert.assertTrue("stored value", query.employmentStatus().values().contains(EmploymentStatus.PART_TIME));

            // Use stored  Query
            EntityQueryCriteria<Employee> criteria = ServerSideFactory.create(PersistableQueryFacade.class).convertToCriteria(query);
            criteria.eq(criteria.proto().workAddress().streetName(), setId);
            List<Employee> emps = srv.query(criteria);
            Assert.assertEquals("result set size", 1, emps.size());
            Assert.assertEquals("right selection ", emp1, emps.get(0));
        }

        // Modify
        {
            EmployeeQuery query = ServerSideFactory.create(PersistableQueryFacade.class).retriveQuery(EmployeeQuery.class, storeHere);
            query.employmentStatus().values().clear();
            query.employmentStatus().values().add(EmploymentStatus.FULL_TIME);
            ServerSideFactory.create(PersistableQueryFacade.class).persistQuery(query, storeHere);
        }

        // Use after modifications
        {
            EmployeeQuery query = ServerSideFactory.create(PersistableQueryFacade.class).retriveQuery(EmployeeQuery.class, storeHere);
            EntityQueryCriteria<Employee> criteria = ServerSideFactory.create(PersistableQueryFacade.class).convertToCriteria(query,
                    createEmployeeQueryBinder());
            criteria.eq(criteria.proto().workAddress().streetName(), setId);
            List<Employee> emps = srv.query(criteria);
            Assert.assertEquals("result set size", 0, emps.size());
        }
    }

}
