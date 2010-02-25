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

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.test.shared.domain.Address;
import com.pyx4j.entity.test.shared.domain.City;
import com.pyx4j.entity.test.shared.domain.Country;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Province;
import com.pyx4j.entity.test.shared.domain.Status;
import com.pyx4j.entity.test.shared.domain.Task;

public abstract class RetrievalTestCase extends DatastoreTestBase {

    public void testRetrieveByPk() {
        Employee emp = EntityFactory.create(Employee.class);
        String empName = "Bob " + uniqueString();
        emp.firstName().setValue(empName);

        srv.persist(emp);

        EntityQueryCriteria<Employee> criteria1 = EntityQueryCriteria.create(Employee.class);
        criteria1.add(PropertyCriterion.eq(IEntity.PRIMARY_KEY, emp.getPrimaryKey()));
        Employee emp1 = srv.retrieve(criteria1);
        Assert.assertNotNull("retrieve", emp1);
        Assert.assertEquals("Value", empName, emp1.firstName().getValue());
    }

    public void testOwnedSet() {
        Employee emp = EntityFactory.create(Employee.class);
        emp.firstName().setValue("Bob");

        Task task = EntityFactory.create(Task.class);
        Date today = new Date();
        task.deadLine().setValue(today);
        task.status().setValue(Status.DEACTIVATED);

        emp.tasks().add(task);

        srv.persist(emp);
        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertEquals("Value", "Bob", emp2.firstName().getValue());

        Assert.assertEquals("Retr. Set size", 1, emp2.tasks().size());
        Assert.assertTrue("Retr. contains", emp2.tasks().contains(task));

        Task task2 = emp2.tasks().iterator().next();

        Assert.assertEquals("deadLine", today, task2.deadLine().getValue());
        Assert.assertEquals("Status", Status.DEACTIVATED, task2.status().getValue());
    }

    public void testUnownedSetRetrieveOwner() {
        // Setup data
        Department department = EntityFactory.create(Department.class);
        String deptName = "Dept " + uniqueString();
        department.name().setValue(deptName);
        srv.persist(department);

        Employee employee1 = EntityFactory.create(Employee.class);
        employee1.firstName().setValue("Firstname1" + uniqueString());
        srv.persist(employee1);
        department.employees().add(employee1);

        Employee employee2 = EntityFactory.create(Employee.class);
        employee2.firstName().setValue("Firstname2" + uniqueString());
        srv.persist(employee2);
        department.employees().add(employee2);

        srv.persist(department);

        {
            EntityQueryCriteria<Department> criteria1 = EntityQueryCriteria.create(Department.class);
            criteria1.add(PropertyCriterion.eq(department.name(), deptName));
            List<Department> departments1 = srv.query(criteria1);
            Assert.assertEquals("Retr 1. List size", 1, departments1.size());
            Assert.assertEquals("Retr 1. department.name", deptName, departments1.get(0).name().getValue());
        }

        {
            EntityQueryCriteria<Department> criteria2 = EntityQueryCriteria.create(Department.class);
            criteria2.add(PropertyCriterion.eq(department.employees(), employee2));
            List<Department> departments2 = srv.query(criteria2);
            Assert.assertEquals("Retr 2. List size", 1, departments2.size());
            Assert.assertEquals("Retr 2. department.name", deptName, departments2.get(0).name().getValue());
        }

        // TODO Variation of passing only Entity Key, Not supported for now
        //        {
        //            EntityCriteria<Department> criteria3 = EntityCriteria.create(Department.class);
        //            criteria3.add(PropertyCriterion.eq(department.employees(), employee2.getPrimaryKey()));
        //            List<Department> departments3 = srv.query(criteria3);
        //            Assert.assertEquals("Retr 3. List size", 1, departments3.size());
        //            Assert.assertEquals("Retr 3. department.name", deptName, departments3.get(0).name().getValue());
        //        }
    }

    public void testOwnedList() {
        Employee emp = EntityFactory.create(Employee.class);
        emp.firstName().setValue("Bob");

        Task task = EntityFactory.create(Task.class);
        Date today = new Date();
        task.deadLine().setValue(today);
        task.status().setValue(Status.DEACTIVATED);

        emp.tasksSorted().add(task);

        srv.persist(emp);
        Employee emp2 = srv.retrieve(Employee.class, emp.getPrimaryKey());
        Assert.assertEquals("Value", "Bob", emp2.firstName().getValue());

        Assert.assertEquals("Retr. Set size", 1, emp2.tasksSorted().size());
        Assert.assertTrue("Retr. contains", emp2.tasksSorted().contains(task));

        Task task2 = emp2.tasksSorted().iterator().next();

        Assert.assertEquals("deadLine", today, task2.deadLine().getValue());
        Assert.assertEquals("Status", Status.DEACTIVATED, task2.status().getValue());
    }

    public void testEmbeddedEntity() {

        Address address = EntityFactory.create(Address.class);

        City city = EntityFactory.create(City.class);
        String cityName = "Toronto" + uniqueString();
        city.name().setValue(cityName);

        address.city().set(city);

        srv.persist(address);

        Long primaryKey = address.getPrimaryKey();
        Address address2 = srv.retrieve(Address.class, primaryKey);
        Assert.assertNotNull("retrieve", address2);

        Assert.assertEquals("address.city Value", cityName, address2.city().name().getValue());
    }

    public void testEmbeddedEntitySet() {

        Province prov = EntityFactory.create(Province.class);
        prov.name().setValue("Ontario" + uniqueString());

        City city1 = EntityFactory.create(City.class);
        city1.name().setValue("Ottawa" + uniqueString());

        City city2 = EntityFactory.create(City.class);
        city2.name().setValue("Toronto" + uniqueString());

        prov.cities().add(city1);
        prov.cities().add(city2);

        srv.persist(prov);
        Province prov2 = srv.retrieve(Province.class, prov.getPrimaryKey());

        Assert.assertEquals("Retr. Set size", 2, prov2.cities().size());

        Iterator<City> it = prov2.cities().iterator();
        City city1r = it.next();
        City city2r = it.next();

        if (city1r.name().equals(city1.name())) {
            Assert.assertEquals("Retr. Value", city2.name(), city2r.name());
        } else {
            Assert.assertEquals("Retr. Value", city1.name(), city2r.name());
            Assert.assertEquals("Retr. Value", city2.name(), city1r.name());
        }

        {
            EntityQueryCriteria<Province> criteria1 = EntityQueryCriteria.create(Province.class);
            criteria1.add(PropertyCriterion.eq(prov.cities(), city1.name().getValue()));
            List<Province> provs = srv.query(criteria1);
            Assert.assertEquals("Retr 1. List size", 1, provs.size());
            Assert.assertEquals("Retr 1. prov.name", prov.name().getValue(), provs.get(0).name().getValue());
        }
    }

    public void testEmbeddedEntityLevel2() {

        Employee emp = EntityFactory.create(Employee.class);
        emp.firstName().setValue("Bob" + uniqueString());

        Address address = EntityFactory.create(Address.class);
        String streetName = "Bloor" + uniqueString();
        address.streetName().setValue(streetName);

        City city = EntityFactory.create(City.class);
        String cityName = "Toronto" + uniqueString();
        city.name().setValue(cityName);

        Country country = EntityFactory.create(Country.class);
        String countryName = "Canada" + uniqueString();
        country.name().setValue(countryName);
        srv.persist(country);
        address.country().set(country);

        address.city().set(city);

        emp.workAddress().set(address);

        srv.persist(emp);

        Long primaryKey = emp.getPrimaryKey();
        Employee emp2 = srv.retrieve(Employee.class, primaryKey);
        Assert.assertNotNull("retrieve", emp2);

        Assert.assertEquals("address.streetName Value", streetName, emp2.workAddress().streetName().getValue());
        //System.out.println(((IFullDebug) emp2).debugString());

        Assert.assertEquals("address.country Value", countryName, emp2.workAddress().country().name().getValue());
        Assert.assertEquals("address.city Value", cityName, emp2.workAddress().city().name().getValue());
    }
}
