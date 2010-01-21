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
 * Created on Jan 20, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.gae;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Status;
import com.pyx4j.entity.test.shared.domain.Employee.EmploymentStatus;

public class PrimitivePersistanceTest extends LocalDatastoreTest {

    private IEntityPersistenceService srv;

    @Before
    public void setupPersistenceService() {
        srv = PersistenceServicesFactory.getPersistenceService();
    }

    @Test
    public void testString() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.firstName().getValue());
        Assert.assertEquals("Class of Value", String.class, emp.firstName().getValueClass());
        emp.firstName().setValue("Bob");

        srv.persist(emp);
        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertEquals("Class of Value", String.class, emp2.firstName().getValue().getClass());
        Assert.assertEquals("Value", "Bob", emp2.firstName().getValue());
    }

    @Test
    public void testDate() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.hiredate().getValue());
        Assert.assertEquals("Class of Value", Date.class, emp.hiredate().getValueClass());
        Date today = new Date();
        emp.hiredate().setValue(today);

        srv.persist(emp);
        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertEquals("Class of Value", Date.class, emp2.hiredate().getValue().getClass());
        Assert.assertEquals("Value", today, emp2.hiredate().getValue());
    }

    @Test
    public void testBoolean() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.reliable().getValue());
        Assert.assertEquals("Class of Value", Boolean.class, emp.reliable().getValueClass());
        emp.reliable().setValue(Boolean.TRUE);

        srv.persist(emp);
        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertEquals("Class of Value", Boolean.class, emp2.reliable().getValue().getClass());
        Assert.assertEquals("Value", Boolean.TRUE, emp2.reliable().getValue());
    }

    @Test
    public void testLong() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.holidays().getValue());
        Assert.assertEquals("Class of Value", Long.class, emp.holidays().getValueClass());
        emp.holidays().setValue(7L);

        srv.persist(emp);
        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertEquals("Class of Value", Long.class, emp2.holidays().getValue().getClass());
        Assert.assertEquals("Value", Long.valueOf(7), emp2.holidays().getValue());
    }

    @Test
    public void testInteger() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.rating().getValue());
        Assert.assertEquals("Class of Value", Integer.class, emp.rating().getValueClass());
        emp.rating().setValue(5);

        srv.persist(emp);
        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertEquals("Class of Value", Integer.class, emp2.rating().getValue().getClass());
        Assert.assertEquals("Value", Integer.valueOf(5), emp2.rating().getValue());
    }

    @Test
    public void testDouble() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.salary().getValue());
        Assert.assertEquals("Class of Value", Double.class, emp.salary().getValueClass());
        emp.salary().setValue(77.8);

        srv.persist(emp);
        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertEquals("Class of Value", Double.class, emp2.salary().getValue().getClass());
        Assert.assertEquals("Value", 77.8, emp2.salary().getValue());
    }

    @Test
    public void testEnumExternal() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.accessStatus().getValue());
        Assert.assertEquals("Class of Value", Status.class, emp.accessStatus().getValueClass());
        emp.accessStatus().setValue(Status.SUSPENDED);

        srv.persist(emp);
        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertEquals("Class of Value", Status.class, emp2.accessStatus().getValue().getClass());
        Assert.assertEquals("Value", Status.SUSPENDED, emp2.accessStatus().getValue());
    }

    @Test
    public void testEnumEmbeded() {
        Employee emp = EntityFactory.create(Employee.class);
        Assert.assertNull("Initial value", emp.employmentStatus().getValue());
        Assert.assertEquals("Class of Value", EmploymentStatus.class, emp.employmentStatus().getValueClass());
        emp.employmentStatus().setValue(EmploymentStatus.FULL_TIME);

        srv.persist(emp);
        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertEquals("Class of Value", EmploymentStatus.class, emp2.employmentStatus().getValue().getClass());
        Assert.assertEquals("Value", EmploymentStatus.FULL_TIME, emp2.employmentStatus().getValue());
    }

}
