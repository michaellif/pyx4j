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

import junit.framework.Assert;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.test.shared.domain.Employee;

public abstract class QueryTestBase extends DatastoreTestBase {

    public void testQueryByString() {
        Employee emp = EntityFactory.create(Employee.class);
        String empName = "Bob " + uniqueString();
        emp.firstName().setValue(empName);

        srv.persist(emp);

        EntityQueryCriteria<Employee> criteria1 = EntityQueryCriteria.create(Employee.class);
        criteria1.add(PropertyCriterion.eq(criteria1.meta().firstName(), empName));
        Employee emp1 = srv.retrieve(criteria1);
        Assert.assertNotNull("retrieve", emp1);
        Assert.assertEquals("PK Value", emp.getPrimaryKey(), emp1.getPrimaryKey());
        Assert.assertEquals("Search Value", empName, emp1.firstName().getValue());
    }

}
