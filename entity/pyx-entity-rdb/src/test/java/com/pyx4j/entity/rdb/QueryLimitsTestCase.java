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
 * Created on Jul 3, 2011
 * @author vlads
 */
package com.pyx4j.entity.rdb;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.Employee;

public abstract class QueryLimitsTestCase extends DatastoreTestBase {

    private static final Logger log = LoggerFactory.getLogger(QueryLimitsTestCase.class);

    public void testPages() {
        Employee mgr = EntityFactory.create(Employee.class);
        String mgrName = "Manager " + uniqueString();
        mgr.firstName().setValue(mgrName);
        srv.persist(mgr);

        for (int i = 0; i <= 25; i++) {
            Employee emp = EntityFactory.create(Employee.class);
            emp.firstName().setValue(String.valueOf(i) + "_" + uniqueString());
            emp.manager().set(mgr);
            srv.persist(emp);
        }

        EntityListCriteria<Employee> criteria = EntityListCriteria.create(Employee.class);
        criteria.setPageSize(10);
        criteria.add(PropertyCriterion.eq(criteria.proto().manager(), mgr));

        ICursorIterator<Employee> emps0 = srv.query(null, criteria, AttachLevel.Attached);
        try {
            int cnt = 0;
            while (cnt < criteria.getPageSize()) {
                emps0.next();
                cnt++;
            }
            Assert.assertTrue("has OneMore for pagination", emps0.hasNext());
            emps0.next();
            Assert.assertFalse("has no More", emps0.hasNext());
        } finally {
            emps0.close();
        }

        criteria.setPageNumber(2);
        ICursorIterator<Employee> emps2 = srv.query(null, criteria, AttachLevel.Attached);
        try {
            int cnt = 0;
            while (cnt < 5) {
                Employee emp = emps2.next();
                log.debug("item: {} {}", cnt, emp.firstName().getValue());
                cnt++;
            }
            Assert.assertFalse("has no More", emps0.hasNext());
        } finally {
            emps2.close();
        }

    }
}
