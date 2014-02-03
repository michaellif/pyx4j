/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Feb 3, 2014
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import junit.framework.Assert;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Simple1;

public abstract class UpdateQueryRDBTestCase extends DatastoreTestBase {

    public void testSingelTableUpdate() {
        String testId = uniqueString();
        Simple1 ent1;
        Simple1 ent2;
        {
            Simple1 ent = EntityFactory.create(Simple1.class);
            ent.testId().setValue(testId);
            ent.name().setValue("A");
            srv.persist(ent);
            ent1 = ent;
        }
        {
            Simple1 ent = EntityFactory.create(Simple1.class);
            ent.testId().setValue(testId);
            ent.name().setValue("B");
            srv.persist(ent);
            ent2 = ent;
        }

        // test

        EntityQueryCriteria<Simple1> criteria = EntityQueryCriteria.create(Simple1.class);
        criteria.eq(criteria.proto().testId(), testId);

        Simple1 entityTemplate = EntityFactory.create(Simple1.class);
        entityTemplate.name().setValue("C");

        srv.update(criteria, entityTemplate);

        {
            Simple1 ent = srv.retrieve(Simple1.class, ent1.getPrimaryKey());
            Assert.assertEquals("Name updated", entityTemplate.name().getValue(), ent.name().getValue());
        }
        {
            Simple1 ent = srv.retrieve(Simple1.class, ent2.getPrimaryKey());
            Assert.assertEquals("Name updated", entityTemplate.name().getValue(), ent.name().getValue());
        }

    }

    public void testEmbeddedUpdate() {
        String testId = uniqueString();
        Employee ent1;
        Employee ent2;
        {
            Employee ent = EntityFactory.create(Employee.class);
            ent.firstName().setValue(testId);
            ent.workAddress().streetName().setValue("A");
            srv.persist(ent);
            ent1 = ent;
        }
        {
            Employee ent = EntityFactory.create(Employee.class);
            ent.firstName().setValue(testId);
            ent.workAddress().streetName().setValue("B");
            srv.persist(ent);
            ent2 = ent;
        }

        // test

        EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
        criteria.eq(criteria.proto().firstName(), testId);

        Employee entityTemplate = EntityFactory.create(Employee.class);
        entityTemplate.workAddress().streetName().setValue("C");

        srv.update(criteria, entityTemplate);

        {
            Employee ent = srv.retrieve(Employee.class, ent1.getPrimaryKey());
            Assert.assertEquals("Name updated", entityTemplate.workAddress().streetName().getValue(), ent.workAddress().streetName().getValue());
        }
        {
            Employee ent = srv.retrieve(Employee.class, ent2.getPrimaryKey());
            Assert.assertEquals("Name updated", entityTemplate.workAddress().streetName().getValue(), ent.workAddress().streetName().getValue());
        }
    }

    public void testRefferenceUpdate() {
        String testId = uniqueString();

        Department department1 = EntityFactory.create(Department.class);
        department1.name().setValue("A" + testId);
        srv.persist(department1);

        Department department2 = EntityFactory.create(Department.class);
        department2.name().setValue("B" + testId);
        srv.persist(department2);

        Employee ent1;
        Employee ent2;
        {
            Employee ent = EntityFactory.create(Employee.class);
            ent.firstName().setValue(testId);
            ent.workAddress().streetName().setValue("A");
            ent.department().set(department1);
            srv.persist(ent);
            ent1 = ent;
        }
        {
            Employee ent = EntityFactory.create(Employee.class);
            ent.firstName().setValue(testId);
            ent.workAddress().streetName().setValue("B");
            ent.department().set(department1);
            srv.persist(ent);
            ent2 = ent;
        }

        // test

        EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
        criteria.eq(criteria.proto().firstName(), testId);

        Employee entityTemplate = EntityFactory.create(Employee.class);
        entityTemplate.department().set(department2);

        srv.update(criteria, entityTemplate);

        {
            Employee ent = srv.retrieve(Employee.class, ent1.getPrimaryKey());
            Assert.assertEquals("Reference updated", department2, ent.department());
            Assert.assertEquals("Name not updated", ent1.workAddress().streetName().getValue(), ent.workAddress().streetName().getValue());
        }
        {
            Employee ent = srv.retrieve(Employee.class, ent2.getPrimaryKey());
            Assert.assertEquals("Reference updated", department2, ent.department());
            Assert.assertEquals("Name not updated", ent2.workAddress().streetName().getValue(), ent.workAddress().streetName().getValue());
        }
    }

    //TODO
    public void TODO_testJonConditionUpdate() {
        String testId = uniqueString();

        Department department1 = EntityFactory.create(Department.class);
        department1.name().setValue("A" + testId);
        srv.persist(department1);

        Employee ent1;
        Employee ent2;
        {
            Employee ent = EntityFactory.create(Employee.class);
            ent.firstName().setValue(testId);
            ent.department().set(department1);
            ent.rating().setValue(1);
            ent.workAddress().streetName().setValue("A");
            srv.persist(ent);
            ent1 = ent;
        }
        {
            Employee ent = EntityFactory.create(Employee.class);
            ent.firstName().setValue(testId);
            ent.department().set(department1);
            ent.rating().setValue(2);
            ent.workAddress().streetName().setValue("B");
            srv.persist(ent);
            ent2 = ent;
        }

        // test

        EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
        criteria.eq(criteria.proto().department().name(), department1.name());

        Employee entityTemplate = EntityFactory.create(Employee.class);
        entityTemplate.workAddress().streetName().setValue("C");

        srv.update(criteria, entityTemplate);

        {
            Employee ent = srv.retrieve(Employee.class, ent1.getPrimaryKey());
            Assert.assertEquals("Not updated", ent1.rating().getValue(), ent.rating().getValue());
            Assert.assertEquals("Reference Not updated", department1, ent.department());
            Assert.assertEquals("Name updated", entityTemplate.workAddress().streetName().getValue(), ent.workAddress().streetName().getValue());
        }
        {
            Employee ent = srv.retrieve(Employee.class, ent2.getPrimaryKey());
            Assert.assertEquals("Not updated", ent2.rating().getValue(), ent.rating().getValue());
            Assert.assertEquals("Reference Not updated", department1, ent.department());
            Assert.assertEquals("Name updated", entityTemplate.workAddress().streetName().getValue(), ent.workAddress().streetName().getValue());
        }
    }

}
