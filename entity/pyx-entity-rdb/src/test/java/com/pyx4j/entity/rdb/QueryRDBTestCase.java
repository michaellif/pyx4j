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
 * Created on Nov 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import java.util.List;
import java.util.Vector;

import junit.framework.Assert;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.Employee;

public abstract class QueryRDBTestCase extends DatastoreTestBase {

    public void testSortById() {
        String setId = uniqueString();
        Employee emp1 = EntityFactory.create(Employee.class);
        String emp1Name = "Bob " + uniqueString();
        emp1.firstName().setValue(emp1Name);
        emp1.workAddress().streetName().setValue(setId);
        srv.persist(emp1);

        Employee emp2 = EntityFactory.create(Employee.class);
        String emp2Name = "Anna " + uniqueString();
        emp2.firstName().setValue(emp2Name);
        emp2.workAddress().streetName().setValue(setId);
        srv.persist(emp2);

        {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().workAddress().streetName(), setId));
            criteria.asc(criteria.proto().id());

            List<Employee> empsSortedAsc = srv.query(criteria);
            Assert.assertEquals("result set size", 2, empsSortedAsc.size());
            Assert.assertEquals("PK Value", empsSortedAsc.get(0).getPrimaryKey(), emp1.getPrimaryKey());
        }

        {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().workAddress().streetName(), setId));
            criteria.desc(criteria.proto().id());

            List<Employee> empsSortedAsc = srv.query(criteria);
            Assert.assertEquals("result set size", 2, empsSortedAsc.size());
            Assert.assertEquals("PK Value", empsSortedAsc.get(0).getPrimaryKey(), emp2.getPrimaryKey());
        }
    }

    public void testCriterionIN() {
        String setId = uniqueString();
        Vector<Employee> emps = new Vector<Employee>();
        final int dataSize = 3;
        for (int i = 0; i < dataSize; i++) {
            Employee emp = EntityFactory.create(Employee.class);
            emp.firstName().setValue(uniqueString());
            emp.workAddress().streetName().setValue(setId);
            srv.persist(emp);
            emps.add(emp);
        }

        {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            criteria.add(PropertyCriterion.in(criteria.proto().id(), emps));

            List<Employee> empsRetrived = srv.query(criteria);
            Assert.assertEquals("result set size", dataSize, empsRetrived.size());
        }
    }
}
