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
 * Created on 2011-06-14
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import java.util.List;

import junit.framework.Assert;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.Country;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.server.contexts.NamespaceManager;

public abstract class MultitenantTestCase extends DatastoreTestBase {

    public void testRetrieveByPk() {
        NamespaceManager.setNamespace("1");

        Country country = EntityFactory.create(Country.class);
        String countryName = "Canada" + uniqueString();
        country.name().setValue(countryName);
        srv.persist(country);

        Key primaryKey = country.getPrimaryKey();

        NamespaceManager.setNamespace("2");
        Assert.assertNull("retrieve", srv.retrieve(Country.class, primaryKey));

        NamespaceManager.setNamespace("1");
        Assert.assertNotNull("retrieve", srv.retrieve(Country.class, primaryKey));
    }

    public void testQuery() {
        NamespaceManager.setNamespace("1");
        Employee emp = EntityFactory.create(Employee.class);
        String empName = "Bob " + uniqueString();
        emp.firstName().setValue(empName);

        srv.persist(emp);

        NamespaceManager.setNamespace("2");
        EntityQueryCriteria<Employee> criteria1 = EntityQueryCriteria.create(Employee.class);
        criteria1.add(PropertyCriterion.eq(criteria1.proto().firstName(), empName));
        Employee emp1 = srv.retrieve(criteria1);
        Assert.assertNull("retrieve", emp1);

        List<Employee> emps = srv.query(criteria1);
        Assert.assertEquals("result set size", 0, emps.size());
    }

    public void testUpdate() {
        NamespaceManager.setNamespace("1");
        Employee emp = EntityFactory.create(Employee.class);
        String empName = "Bob " + uniqueString();
        emp.firstName().setValue(empName);

        srv.persist(emp);

        NamespaceManager.setNamespace("2");

        Employee emp2 = EntityFactory.create(Employee.class);
        emp2.firstName().setValue("Bob2 " + uniqueString());
        emp2.setPrimaryKey(emp.getPrimaryKey());

        //TODO test error
        //srv.persist(emp2);

        NamespaceManager.setNamespace("1");
        Employee emp1 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertNotNull("retrieve", emp1);
        Assert.assertEquals("orig Not Changed", empName, emp1.firstName().getValue());
    }
}
