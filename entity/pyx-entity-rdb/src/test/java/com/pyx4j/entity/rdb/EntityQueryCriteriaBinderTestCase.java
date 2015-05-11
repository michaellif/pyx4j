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
 * Created on May 5, 2015
 * @author vlads
 */
package com.pyx4j.entity.rdb;

import java.util.List;

import org.junit.Assert;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.CrudEntityBinder;
import com.pyx4j.entity.shared.utils.EntityBinder;
import com.pyx4j.entity.shared.utils.EntityQueryCriteriaBinder;
import com.pyx4j.entity.shared.utils.EntityQueryCriteriaBinder.CriteriaEnhancer;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Employee.EmploymentStatus;
import com.pyx4j.entity.test.shared.domain.EmployeeTO;
import com.pyx4j.entity.test.shared.domain.EmployeeTO.EmploymentBusinessStatus;

public abstract class EntityQueryCriteriaBinderTestCase extends DatastoreTestBase {

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
        emp1.employmentStatus().setValue(EmploymentStatus.DISMISSED);
        srv.persist(emp1);

        emp2 = EntityFactory.create(Employee.class);
        String emp2Name = "Harry " + uniqueString();
        emp2.firstName().setValue(emp2Name);
        emp2.workAddress().streetName().setValue(setId);
        emp2.employmentStatus().setValue(EmploymentStatus.CONTRACT);
        srv.persist(emp2);
    }

    public void testQueryCustomization() {
        EntityBinder<Employee, EmployeeTO> binder = new CrudEntityBinder<Employee, EmployeeTO>(Employee.class, EmployeeTO.class) {

            @Override
            protected void bind() {
                bindCompleteObject();
            }

        };

        EntityQueryCriteriaBinder<Employee, EmployeeTO> criteriaBinder = EntityQueryCriteriaBinder.create(binder);

        criteriaBinder.addCriteriaEnhancer(criteriaBinder.proto().employmentBusinessStatus(), new CriteriaEnhancer<Employee>() {

            @Override
            public void enhanceCriteria(PropertyCriterion toCriterion, EntityQueryCriteria<Employee> criteria) {
                EmploymentBusinessStatus employmentBusinessStatus = (EmploymentBusinessStatus) toCriterion.getValue();
                switch (employmentBusinessStatus) {
                case Current:
                    criteria.in(criteria.proto().employmentStatus(), EmploymentStatus.CONTRACT, EmploymentStatus.FULL_TIME, EmploymentStatus.PART_TIME);
                    break;
                case Past:
                    criteria.eq(criteria.proto().employmentStatus(), EmploymentStatus.DISMISSED);
                    break;
                }

            }
        });

        // Query
        {
            EntityQueryCriteria<EmployeeTO> toCriteria = EntityQueryCriteria.create(EmployeeTO.class);
            toCriteria.eq(toCriteria.proto().workAddress().streetName(), setId);
            toCriteria.eq(toCriteria.proto().employmentBusinessStatus(), EmploymentBusinessStatus.Past);

            List<Employee> emps = srv.query(criteriaBinder.convertQueryCriteria(toCriteria));
            Assert.assertEquals("result set size", 1, emps.size());
            Assert.assertEquals("right selection ", emp1, emps.get(0));
        }

        {
            EntityQueryCriteria<EmployeeTO> toCriteria = EntityQueryCriteria.create(EmployeeTO.class);
            toCriteria.eq(toCriteria.proto().workAddress().streetName(), setId);
            toCriteria.eq(toCriteria.proto().employmentBusinessStatus(), EmploymentBusinessStatus.Current);

            List<Employee> emps = srv.query(criteriaBinder.convertQueryCriteria(toCriteria));
            Assert.assertEquals("result set size", 1, emps.size());
            Assert.assertEquals("right selection ", emp2, emps.get(0));
        }

    }
}
