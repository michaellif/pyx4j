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
 * Created on Jan 23, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.server;

import junit.framework.Assert;

import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Address;
import com.pyx4j.entity.test.shared.domain.Country;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;

public abstract class EntityPersistenceServiceTestCase extends DatastoreTestBase {

    public void testPersist() {
        Assert.assertNotNull("getPersistenceService", srv);

        Country country = EntityFactory.create(Country.class);
        String countryName = "Canada" + uniqueString();
        country.name().setValue(countryName);

        srv.persist(country);

        String primaryKey = country.getPrimaryKey();

        Country country2 = srv.retrieve(Country.class, primaryKey);
        Assert.assertNotNull("retrieve", country2);
        Assert.assertEquals("name Value", countryName, country2.name().getValue());
        Assert.assertEquals("primaryKey Value", primaryKey, country2.getPrimaryKey());
    }

    public void testUnownedOneToOnePersist() {
        IEntityPersistenceService srv = PersistenceServicesFactory.getPersistenceService();
        Country country = EntityFactory.create(Country.class);
        String countryName = "Canada" + uniqueString();
        country.name().setValue(countryName);
        srv.persist(country);

        Address address = EntityFactory.create(Address.class);
        address.country().set(country);
        srv.persist(address);

        String primaryKey = address.getPrimaryKey();
        Address address2 = srv.retrieve(Address.class, primaryKey);
        Assert.assertNotNull("retrieve", address2);

        Assert.assertEquals("address.country Value", countryName, address2.country().name().getValue());

    }

    public void testOwnedOneToOnePersist() {
        Employee employee = EntityFactory.create(Employee.class);
        employee.firstName().setValue("Firstname");

        String addressStreet = "Home Street " + uniqueString();

        Address address = EntityFactory.create(Address.class);
        address.streetName().setValue(addressStreet);
        employee.homeAddress().set(address);

        srv.persist(employee);
        String primaryKey = employee.getPrimaryKey();
        Employee employee2 = srv.retrieve(Employee.class, primaryKey);
        Assert.assertNotNull("retrieve", employee2);

        Assert.assertNotNull("retrieve owned", employee2.homeAddress());
        Assert.assertNotNull("retrieve owned member", employee2.homeAddress().streetName());

        Assert.assertEquals("streetName is wrong", addressStreet, employee2.homeAddress().streetName().getValue());
    }

    public void testUnownedSetPersist() {
        Department department = EntityFactory.create(Department.class);
        String deptName = "Dept " + uniqueString();
        department.name().setValue(deptName);
        srv.persist(department);

        Employee employee1 = EntityFactory.create(Employee.class);
        employee1.firstName().setValue("Firstname1");
        srv.persist(employee1);
        department.employees().add(employee1);

        Employee employee2 = EntityFactory.create(Employee.class);
        employee2.firstName().setValue("Firstname2");
        srv.persist(employee2);
        department.employees().add(employee2);

        srv.persist(department);
        //System.out.println(((IFullDebug) department).debugString());

        Assert.assertEquals("Set size", 2, department.employees().getValue().size());
        Assert.assertTrue("contains(emp1)", department.employees().contains(employee1));
        Assert.assertTrue("contains(emp2)", department.employees().contains(employee2));

        Department departmentR = srv.retrieve(Department.class, department.getPrimaryKey());
        //System.out.println(((IFullDebug) departmentR).debugString());

        Assert.assertEquals("Retr. department.name", deptName, departmentR.name().getValue());
        Assert.assertEquals("Retr. Set size", 2, departmentR.employees().getValue().size());
        Assert.assertTrue("Retr. contains(emp1)", departmentR.employees().contains(employee1));
        Assert.assertTrue("Retr. contains(emp2)", departmentR.employees().contains(employee2));
        //TODO
        //System.out.println(departmentR.employees().getValue().getClass());

    }

}
