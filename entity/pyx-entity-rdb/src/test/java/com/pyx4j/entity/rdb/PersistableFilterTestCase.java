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

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.filter.QueryFilterBinder;
import com.pyx4j.entity.core.filter.QueryFilterStorage;
import com.pyx4j.entity.server.filter.ColumnStorage;
import com.pyx4j.entity.server.filter.PersistableFilterManager;
import com.pyx4j.entity.server.filter.QueryCriteriaBinderBuilder;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.EmployeeQueryCriteria;
import com.pyx4j.entity.test.shared.domain.TestsQueryCriteriaColumnStorage;

public abstract class PersistableFilterTestCase extends DatastoreTestBase {

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

    //TODO need a global factory for this binders
    private QueryFilterBinder<Employee, EmployeeQueryCriteria> binder() {
        QueryCriteriaBinderBuilder<Employee, EmployeeQueryCriteria> b = new QueryCriteriaBinderBuilder<>(EmployeeQueryCriteria.class);
        b.map(b.proto().firstName(), b.criteriaProto().firstName());
        return b.build();
    }

    public void testQuery() {
        QueryFilterBinder<Employee, EmployeeQueryCriteria> binder = binder();

        // Query
        {
            EmployeeQueryCriteria query = EntityFactory.create(EmployeeQueryCriteria.class);
            query.firstName().value().setValue("Bob");

            EntityQueryCriteria<Employee> criteria = PersistableFilterManager.convertQueryCriteria(query, binder);
            List<Employee> emps = srv.query(criteria);
            Assert.assertEquals("result set size", 1, emps.size());
            Assert.assertEquals("right selection ", emp1, emps.get(0));
        }
        {
            EmployeeQueryCriteria query = EntityFactory.create(EmployeeQueryCriteria.class);
            query.firstName().value().setValue("Harry");

            EntityQueryCriteria<Employee> criteria = PersistableFilterManager.convertQueryCriteria(query, binder);
            List<Employee> emps = srv.query(criteria);
            Assert.assertEquals("result set size", 1, emps.size());
            Assert.assertEquals("right selection ", emp2, emps.get(0));
        }
    }

    public void testQueryPersistence() {
        ColumnStorage.instance().initialize(TestsQueryCriteriaColumnStorage.class);
        QueryFilterStorage storeHere = EntityFactory.create(QueryFilterStorage.class);

        {
            EmployeeQueryCriteria query = EntityFactory.create(EmployeeQueryCriteria.class);
            query.firstName().value().setValue("Bob");
            PersistableFilterManager.persistCriteria(query, storeHere);
        }

        // Query
        {
            QueryFilterStorage storeHereId = storeHere.<QueryFilterStorage> createIdentityStub();

            EmployeeQueryCriteria query = PersistableFilterManager.retriveCriteria(EmployeeQueryCriteria.class, storeHereId);

            Assert.assertEquals("stored value", "Bob", query.firstName().value().getValue());

            // Use stored  Query
            EntityQueryCriteria<Employee> criteria = PersistableFilterManager.convertQueryCriteria(query, binder());
            List<Employee> emps = srv.query(criteria);
            Assert.assertEquals("result set size", 1, emps.size());
            Assert.assertEquals("right selection ", emp1, emps.get(0));
        }
    }

}
