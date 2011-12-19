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
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;

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
}
