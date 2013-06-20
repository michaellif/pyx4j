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

import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.AndCriterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.OrCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.Address;
import com.pyx4j.entity.test.shared.domain.City;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Task;
import com.pyx4j.entity.test.shared.domain.join.org1.Department1;
import com.pyx4j.entity.test.shared.domain.join.org1.Employee1;
import com.pyx4j.entity.test.shared.domain.join.org1.Organization1;
import com.pyx4j.entity.test.shared.domain.join.org2.Department2;
import com.pyx4j.entity.test.shared.domain.join.org2.Employee2;
import com.pyx4j.entity.test.shared.domain.join.org2.Organization2;
import com.pyx4j.entity.test.shared.domain.sort.SortBy;
import com.pyx4j.entity.test.shared.domain.sort.SortSortable;
import com.pyx4j.geo.GeoBox;
import com.pyx4j.geo.GeoCircle;
import com.pyx4j.geo.GeoPoint;

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
            criteria.in(criteria.proto().id(), emps);

            List<Employee> empsRetrived = srv.query(criteria);
            Assert.assertEquals("result set size", dataSize, empsRetrived.size());
        }
    }

    public void testCriterionINJoin() {
        String setId = uniqueString();
        Vector<Employee> emps = new Vector<Employee>();
        Vector<Task> tasks = new Vector<Task>();
        final int dataSize = 3;
        for (int i = 0; i < dataSize; i++) {
            Employee emp = EntityFactory.create(Employee.class);
            emp.firstName().setValue(uniqueString());
            emp.workAddress().streetName().setValue(setId);

            Task task = EntityFactory.create(Task.class);
            task.description().setValue(setId);
            emp.tasks().add(task);

            srv.persist(emp);
            emps.add(emp);
            tasks.add(task);
        }

        {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            criteria.in(criteria.proto().tasks(), tasks);

            List<Employee> empsRetrived = srv.query(criteria);
            Assert.assertEquals("result set size", dataSize, empsRetrived.size());
        }
    }

    public void testCriterionOr() {
        String setId = uniqueString();
        Employee emp1 = EntityFactory.create(Employee.class);
        emp1.firstName().setValue(uniqueString());
        emp1.workAddress().streetName().setValue(setId);
        srv.persist(emp1);

        Employee emp2 = EntityFactory.create(Employee.class);
        emp2.firstName().setValue(uniqueString());
        emp2.workAddress().streetName().setValue(setId);
        srv.persist(emp2);

        {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            criteria.or(PropertyCriterion.eq(criteria.proto().id(), emp1.id().getValue()), PropertyCriterion.eq(criteria.proto().id(), emp2.id().getValue()));

            boolean usageExample = false;
            if (usageExample) {
                {
                    OrCriterion or = new OrCriterion();
                    or.left(PropertyCriterion.eq(criteria.proto().firstName(), emp1.firstName().getValue()));
                    or.left(PropertyCriterion.eq(criteria.proto().workAddress(), setId));
                    or.right(PropertyCriterion.eq(criteria.proto().workAddress(), setId));
                    criteria.add(or);
                }

                {
                    criteria.or().left(PropertyCriterion.eq(criteria.proto().id(), emp1.id().getValue()))
                            .left(PropertyCriterion.eq(criteria.proto().workAddress(), setId))
                            .right(PropertyCriterion.eq(criteria.proto().id(), emp2.id().getValue()));
                }
            }

            List<Employee> retrived = srv.query(criteria);
            Assert.assertEquals("result set size", 2, retrived.size());
        }
    }

    public void testCriterionOrWithJoin0() {
        String setId = uniqueString();
        Department department1 = EntityFactory.create(Department.class);
        department1.testId().setValue(setId);
        String dep1name = "D1 " + uniqueString();
        department1.name().setValue(dep1name);
        srv.persist(department1);

        Employee emp11 = EntityFactory.create(Employee.class);
        String emp1Name = "E1.1 " + uniqueString();
        emp11.firstName().setValue(emp1Name);
        emp11.workAddress().streetName().setValue(setId);
        emp11.department().set(department1);
        srv.persist(emp11);

        department1.employees().setAttachLevel(AttachLevel.Attached);
        department1.employees().add(emp11);
        srv.persist(department1);

        Department department2 = EntityFactory.create(Department.class);
        department2.testId().setValue(setId);
        String dep2name = "D2 " + uniqueString();
        department2.name().setValue(dep2name);
        srv.persist(department2);

        Employee emp21 = EntityFactory.create(Employee.class);
        String emp21Name = "E2.1 " + uniqueString();
        emp21.firstName().setValue(emp21Name);
        emp21.workAddress().streetName().setValue(setId);
        emp21.department().set(department2);
        srv.persist(emp21);

        Employee emp22 = EntityFactory.create(Employee.class);
        String emp22Name = "E2.2 " + uniqueString();
        emp22.firstName().setValue(emp22Name);
        emp22.workAddress().streetName().setValue(setId);
        emp22.manager().set(emp21);
        srv.persist(emp22);

        //Test direct query
        {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            criteria.eq(criteria.proto().workAddress().streetName(), setId);

            OrCriterion or = criteria.or();
            or.left().eq(criteria.proto().manager().firstName(), emp21Name);
            or.right().eq(criteria.proto().department().name(), dep1name);

            List<Employee> retrived = srv.query(criteria);
            Assert.assertEquals("result set size", 2, retrived.size());
            Assert.assertTrue(retrived.contains(emp11));
            Assert.assertTrue(retrived.contains(emp22));
        }

        // Back reference query
        {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            criteria.eq(criteria.proto().workAddress().streetName(), setId);

            OrCriterion or = criteria.or();
            or.left().eq(criteria.proto().employees().$().firstName(), emp22Name);
            or.right().eq(criteria.proto().department().name(), dep1name);

            List<Employee> retrived = srv.query(criteria);
            Assert.assertEquals("result set size", 2, retrived.size());
            Assert.assertTrue(retrived.contains(emp11));
            Assert.assertTrue(retrived.contains(emp21));
        }

        {
            EntityQueryCriteria<Department> criteria = EntityQueryCriteria.create(Department.class);
            criteria.eq(criteria.proto().testId(), setId);

            OrCriterion or = criteria.or();
            or.left().eq(criteria.proto().employees().$().firstName(), emp1Name);
            or.right().eq(criteria.proto().name(), dep2name);

            List<Department> retrived = srv.query(criteria);
            Assert.assertEquals("result set size", 2, retrived.size());
            Assert.assertTrue(retrived.contains(department1));
            Assert.assertTrue(retrived.contains(department2));
        }
    }

    public void testCriterionOrWithJoin01() {
        String setId = uniqueString();
        Department department1 = EntityFactory.create(Department.class);
        department1.testId().setValue(setId);
        String dep1name = "D1 " + uniqueString();
        department1.name().setValue(dep1name);
        srv.persist(department1);

        Employee emp11 = EntityFactory.create(Employee.class);
        String emp11Name = "E1.1 " + uniqueString();
        emp11.firstName().setValue(emp11Name);
        emp11.workAddress().streetName().setValue(setId);
        emp11.department().set(department1);
        srv.persist(emp11);

        department1.employees().setAttachLevel(AttachLevel.Attached);
        department1.employees().add(emp11);
        srv.persist(department1);

        Department department2 = EntityFactory.create(Department.class);
        department2.testId().setValue(setId);
        String dep2name = "D2 " + uniqueString();
        department2.name().setValue(dep2name);
        srv.persist(department2);

        Employee emp21 = EntityFactory.create(Employee.class);
        String emp21Name = "E2.1 " + uniqueString();
        emp21.firstName().setValue(emp21Name);
        emp21.workAddress().streetName().setValue(setId);
        emp21.department().set(department2);
        srv.persist(emp21);

        Employee emp22 = EntityFactory.create(Employee.class);
        String emp22Name = "E2.2 " + uniqueString();
        emp22.firstName().setValue(emp22Name);
        emp22.workAddress().streetName().setValue(setId);
        emp22.manager().set(emp21);
        emp22.department().set(department2);
        srv.persist(emp22);

        department2.employees().setAttachLevel(AttachLevel.Attached);
        department2.employees().add(emp21);
        department2.employees().add(emp22);
        srv.persist(department2);

        // See if it retrieve Department where there are Employee without manager  (LEFT JOIN  created)
        {
            EntityQueryCriteria<Department> criteria = EntityQueryCriteria.create(Department.class);
            criteria.eq(criteria.proto().testId(), setId);

            OrCriterion or = criteria.or();
            or.left().eq(criteria.proto().employees().$().department().name(), dep1name);
            or.right().eq(criteria.proto().employees().$().manager().firstName(), emp21Name);

            List<Department> retrived = srv.query(criteria);
            Assert.assertEquals("result set size", 2, retrived.size());
            Assert.assertTrue(retrived.contains(department1));
            Assert.assertTrue(retrived.contains(department2));
        }

    }

    public void testCriterionOrWithJoin1() {
        String setId = uniqueString();

        Department1 department1 = EntityFactory.create(Department1.class);
        String dep1name = "D1 " + uniqueString();
        department1.testId().setValue(setId);
        department1.name().setValue(dep1name);
        srv.persist(department1);

        Employee1 emp11 = EntityFactory.create(Employee1.class);
        String emp11Name = "E1.1 " + uniqueString();
        emp11.testId().setValue(setId);
        emp11.name().setValue(emp11Name);
        emp11.department().set(department1);
        srv.persist(emp11);

        Department1 department2 = EntityFactory.create(Department1.class);
        String dep2name = "D2 " + uniqueString();
        department2.testId().setValue(setId);
        department2.name().setValue(dep2name);
        srv.persist(department2);

        Employee1 emp21 = EntityFactory.create(Employee1.class);
        String emp21Name = "E2.1 " + uniqueString();
        emp21.name().setValue(emp21Name);
        emp21.testId().setValue(setId);
        emp21.department().set(department2);
        srv.persist(emp21);

        // This Employee is not in department
        Employee1 emp22 = EntityFactory.create(Employee1.class);
        String emp22Name = "E2.2 " + uniqueString();
        emp22.name().setValue(emp22Name);
        emp22.testId().setValue(setId);
        emp22.manager().set(emp21);
        srv.persist(emp22);

        Department1 department3 = EntityFactory.create(Department1.class);
        String dep3name = "D3 " + uniqueString();
        department3.testId().setValue(setId);
        department3.name().setValue(dep3name);
        srv.persist(department3);

        //Test direct query, See if it retrieve emp22 without department
        {
            EntityQueryCriteria<Employee1> criteria = EntityQueryCriteria.create(Employee1.class);
            criteria.eq(criteria.proto().testId(), setId);

            OrCriterion or = criteria.or();
            or.left().eq(criteria.proto().manager().name(), emp21Name);
            or.right().eq(criteria.proto().department().name(), dep1name);

            List<Employee1> retrived = srv.query(criteria);
            Assert.assertEquals("result set size", 2, retrived.size());
            Assert.assertTrue(retrived.contains(emp11));
            Assert.assertTrue(retrived.contains(emp22));
        }

        // Back reference query
        {
            EntityQueryCriteria<Employee1> criteria = EntityQueryCriteria.create(Employee1.class);
            criteria.eq(criteria.proto().testId(), setId);

            OrCriterion or = criteria.or();
            or.left().eq(criteria.proto().employees().$().name(), emp22Name);
            or.right().eq(criteria.proto().department().name(), dep1name);

            List<Employee1> retrived = srv.query(criteria);
            Assert.assertEquals("result set size", 2, retrived.size());
            Assert.assertTrue(retrived.contains(emp11));
            Assert.assertTrue(retrived.contains(emp21));
        }

        //  See if it retrieve department without Employee
        {
            EntityQueryCriteria<Department1> criteria = EntityQueryCriteria.create(Department1.class);
            criteria.eq(criteria.proto().testId(), setId);

            OrCriterion or = criteria.or();
            or.left().eq(criteria.proto().employees().$().name(), emp11Name);
            or.right().eq(criteria.proto().name(), dep3name);

            List<Department1> retrived = srv.query(criteria);
            Assert.assertEquals("result set size", 2, retrived.size());
            Assert.assertTrue(retrived.contains(department1));
            Assert.assertTrue(retrived.contains(department3));
        }
    }

    public void testCriterionOrWithJoin11() {
        String setId = uniqueString();

        Organization1 organization1 = EntityFactory.create(Organization1.class);
        organization1.testId().setValue(setId);
        organization1.name().setValue("O1");
        srv.persist(organization1);

        Department1 department1 = EntityFactory.create(Department1.class);
        String dep1name = "D1 " + uniqueString();
        department1.testId().setValue(setId);
        department1.name().setValue(dep1name);
        department1.organization().set(organization1);
        srv.persist(department1);

        Employee1 emp11 = EntityFactory.create(Employee1.class);
        String emp11Name = "E1.1 " + uniqueString();
        emp11.testId().setValue(setId);
        emp11.name().setValue(emp11Name);
        emp11.department().set(department1);
        srv.persist(emp11);

        Organization1 organization2 = EntityFactory.create(Organization1.class);
        organization2.testId().setValue(setId);
        organization2.name().setValue("O2");
        srv.persist(organization2);

        Department1 department2 = EntityFactory.create(Department1.class);
        String dep2name = "D2 " + uniqueString();
        department2.testId().setValue(setId);
        department2.name().setValue(dep2name);
        department2.organization().set(organization2);
        srv.persist(department2);

        Employee1 emp21 = EntityFactory.create(Employee1.class);
        String emp21Name = "E2.1 " + uniqueString();
        emp21.name().setValue(emp21Name);
        emp21.testId().setValue(setId);
        emp21.department().set(department2);
        srv.persist(emp21);

        Employee1 emp22 = EntityFactory.create(Employee1.class);
        String emp22Name = "E2.2 " + uniqueString();
        emp22.name().setValue(emp22Name);
        emp22.testId().setValue(setId);
        emp22.manager().set(emp21);
        emp22.department().set(department2);
        srv.persist(emp22);

        Department1 department3 = EntityFactory.create(Department1.class);
        String dep3name = "D3 " + uniqueString();
        department3.testId().setValue(setId);
        department3.name().setValue(dep3name);
        srv.persist(department3);

        // See if it retrieve Department where there are Employee without manager  (LEFT JOIN  created)
        {
            EntityQueryCriteria<Department1> criteria = EntityQueryCriteria.create(Department1.class);
            criteria.eq(criteria.proto().testId(), setId);

            OrCriterion or = criteria.or();
            or.left().eq(criteria.proto().employees().$().department().name(), dep1name);
            or.right().eq(criteria.proto().employees().$().manager().name(), emp21Name);

            List<Department1> retrived = srv.query(criteria);
            Assert.assertEquals("result set size", 2, retrived.size());
            Assert.assertTrue(retrived.contains(department1));
            Assert.assertTrue(retrived.contains(department2));
        }

        // The same as above only query root is different
        {
            EntityQueryCriteria<Organization1> criteria = EntityQueryCriteria.create(Organization1.class);
            criteria.eq(criteria.proto().testId(), setId);

            OrCriterion or = criteria.or();
            or.left().eq(criteria.proto().departments().$().employees().$().department().name(), dep1name);
            or.right().eq(criteria.proto().departments().$().employees().$().manager().name(), emp21Name);

            List<Organization1> retrived = srv.query(criteria);
            Assert.assertEquals("result set size", 2, retrived.size());
            Assert.assertTrue(retrived.contains(organization1));
            Assert.assertTrue(retrived.contains(organization2));
        }

    }

    //TODO
    public void X_testCriterionOrWithJoin21() {
        String setId = uniqueString();

        Department2 department1 = EntityFactory.create(Department2.class);
        String dep1name = "D1 " + uniqueString();
        department1.testId().setValue(setId);
        department1.name().setValue(dep1name);
        srv.persist(department1);

        Organization2 organization1 = EntityFactory.create(Organization2.class);
        organization1.testId().setValue(setId);
        organization1.name().setValue("O1");
        organization1.departments().add(department1);
        srv.persist(organization1);

        Employee2 emp11 = EntityFactory.create(Employee2.class);
        String emp11Name = "E1.1 " + uniqueString();
        emp11.testId().setValue(setId);
        emp11.name().setValue(emp11Name);
        emp11.department().set(department1);
        srv.persist(emp11);

        Department2 department2 = EntityFactory.create(Department2.class);
        String dep2name = "D2 " + uniqueString();
        department2.testId().setValue(setId);
        department2.name().setValue(dep2name);
        srv.persist(department2);

        Organization2 organization2 = EntityFactory.create(Organization2.class);
        organization2.testId().setValue(setId);
        organization2.name().setValue("O2");
        organization2.departments().add(department2);
        srv.persist(organization2);

        Employee2 emp21 = EntityFactory.create(Employee2.class);
        String emp21Name = "E2.1 " + uniqueString();
        emp21.name().setValue(emp21Name);
        emp21.testId().setValue(setId);
        emp21.department().set(department2);
        srv.persist(emp21);

        Employee2 emp22 = EntityFactory.create(Employee2.class);
        String emp22Name = "E2.2 " + uniqueString();
        emp22.name().setValue(emp22Name);
        emp22.testId().setValue(setId);
        emp22.manager().set(emp21);
        emp22.department().set(department2);
        srv.persist(emp22);

        Department2 department3 = EntityFactory.create(Department2.class);
        String dep3name = "D3 " + uniqueString();
        department3.testId().setValue(setId);
        department3.name().setValue(dep3name);
        srv.persist(department3);

        // See if it retrieve Department where there are Employee without manager  (LEFT JOIN  created)
        {
            EntityQueryCriteria<Department2> criteria = EntityQueryCriteria.create(Department2.class);
            criteria.eq(criteria.proto().testId(), setId);

            OrCriterion or = criteria.or();
            or.left().eq(criteria.proto().employees().$().department().name(), dep1name);
            or.right().eq(criteria.proto().employees().$().manager().name(), emp21Name);

            List<Department2> retrived = srv.query(criteria);
            Assert.assertEquals("result set size", 2, retrived.size());
            Assert.assertTrue(retrived.contains(department1));
            Assert.assertTrue(retrived.contains(department2));
        }

        // The same as above only query root is different
        {
            EntityQueryCriteria<Organization2> criteria = EntityQueryCriteria.create(Organization2.class);
            criteria.eq(criteria.proto().testId(), setId);

            OrCriterion or = criteria.or();
            or.left().eq(criteria.proto().departments().$().employees().$().department().name(), dep1name);
            or.right().eq(criteria.proto().departments().$().employees().$().manager().name(), emp21Name);

            List<Organization2> retrived = srv.query(criteria);
            Assert.assertEquals("result set size", 2, retrived.size());
            Assert.assertTrue(retrived.contains(organization1));
            Assert.assertTrue(retrived.contains(organization2));
        }

    }

    public void testCriterionAnd() {
        String setId = uniqueString();
        Employee emp1 = EntityFactory.create(Employee.class);
        emp1.firstName().setValue(uniqueString());
        emp1.workAddress().streetName().setValue(setId);
        srv.persist(emp1);

        Employee emp2 = EntityFactory.create(Employee.class);
        emp2.firstName().setValue(uniqueString());
        emp2.workAddress().streetName().setValue(setId);
        srv.persist(emp2);

        {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);

            boolean thisIsEquvalents = true;
            if (thisIsEquvalents) {
                AndCriterion and = new AndCriterion();
                and.eq(criteria.proto().id(), emp1.id());
                and.eq(criteria.proto().firstName(), emp1.firstName());

                criteria.add(and);
            } else {
                criteria.eq(criteria.proto().id(), emp1.id());
                criteria.eq(criteria.proto().firstName(), emp1.firstName());
            }

            List<Employee> empsRetrived = srv.query(criteria);
            Assert.assertEquals("result set size", 1, empsRetrived.size());
        }
    }

    public void testSimpleGeoSerch() {
        City city = EntityFactory.create(City.class);
        city.name().setValue(uniqueString());

        Address address1 = EntityFactory.create(Address.class);
        address1.city().set(city);
        address1.streetName().setValue(uniqueString());
        address1.location().setValue(new GeoPoint(43.72316, -79.33030));
        srv.persist(address1);

        Address address2 = EntityFactory.create(Address.class);
        address2.city().set(city);
        address2.streetName().setValue(uniqueString());
        address2.location().setValue(new GeoPoint(43.80269, -79.10929));
        srv.persist(address2);

        int searchRadiusKm = 10;
        GeoPoint centerPoint = new GeoPoint(43.65232, -79.38386);
        GeoCircle geoCircle = new GeoCircle(centerPoint, searchRadiusKm);
        GeoBox geoBox = geoCircle.getMinBox();

        EntityQueryCriteria<Address> criteria = EntityQueryCriteria.create(Address.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().city().name(), city.name().getValue()));
        criteria.add(PropertyCriterion.le(criteria.proto().location(), geoBox.getNorthEast()));
        criteria.add(PropertyCriterion.ge(criteria.proto().location(), geoBox.getSouthWest()));

        List<Address> near = srv.query(criteria);
        Assert.assertEquals("result set size", 1, near.size());

        //  test with bigger radius
        searchRadiusKm = 30;
        geoCircle = new GeoCircle(centerPoint, searchRadiusKm);
        geoBox = geoCircle.getMinBox();

        criteria.resetCriteria();
        criteria.add(PropertyCriterion.eq(criteria.proto().city().name(), city.name().getValue()));
        criteria.add(PropertyCriterion.le(criteria.proto().location(), geoBox.getNorthEast()));
        criteria.add(PropertyCriterion.ge(criteria.proto().location(), geoBox.getSouthWest()));

        List<Address> far = srv.query(criteria);
        Assert.assertEquals("result set size", 2, far.size());
    }

    public void testSortByToString() {
        String testId = uniqueString();
        {
            SortSortable item = EntityFactory.create(SortSortable.class);
            item.testId().setValue(testId);
            item.sortByOwned().name().setValue("B");
            item.sortByOwned().amount().setValue("1");
            srv.persist(item);
        }
        {
            SortSortable item = EntityFactory.create(SortSortable.class);
            item.testId().setValue(testId);
            item.sortByOwned().name().setValue("A");
            item.sortByOwned().amount().setValue("2");
            srv.persist(item);
        }
        {
            SortSortable item = EntityFactory.create(SortSortable.class);
            item.testId().setValue(testId);
            item.sortByOwned().name().setValue("A");
            item.sortByOwned().amount().setValue("1");
            srv.persist(item);
        }

        // Explicit sort
        {
            EntityQueryCriteria<SortSortable> criteria = EntityQueryCriteria.create(SortSortable.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.asc(criteria.proto().sortByOwned().name());
            criteria.asc(criteria.proto().sortByOwned().amount());

            List<SortSortable> r = srv.query(criteria);
            Assert.assertEquals("result set size", 3, r.size());
            Assert.assertEquals("sort Ok", "A", r.get(0).sortByOwned().name().getValue());
            Assert.assertEquals("sort Ok", "2", r.get(1).sortByOwned().amount().getValue());
        }

        // Created sort by ToString members
        {
            EntityQueryCriteria<SortSortable> criteria = EntityQueryCriteria.create(SortSortable.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.asc(criteria.proto().sortByOwned());

            List<SortSortable> r = srv.query(criteria);
            Assert.assertEquals("result set size", 3, r.size());

            Assert.assertEquals("sort Ok", "A", r.get(0).sortByOwned().name().getValue());
            Assert.assertEquals("sort Ok", "1", r.get(0).sortByOwned().amount().getValue());

            Assert.assertEquals("sort Ok", "A", r.get(1).sortByOwned().name().getValue());
            Assert.assertEquals("sort Ok", "2", r.get(1).sortByOwned().amount().getValue());

            Assert.assertEquals("sort Ok", "B", r.get(2).sortByOwned().name().getValue());
        }
    }

    public void testSortByEmbeddedToString() {
        String testId = uniqueString();
        {
            SortSortable item = EntityFactory.create(SortSortable.class);
            item.testId().setValue(testId);
            item.sortByEmbedded().name().setValue("B");
            item.sortByEmbedded().amount().setValue("1");
            srv.persist(item);
        }
        {
            SortSortable item = EntityFactory.create(SortSortable.class);
            item.testId().setValue(testId);
            item.sortByEmbedded().name().setValue("A");
            item.sortByEmbedded().amount().setValue("2");
            srv.persist(item);
        }
        {
            SortSortable item = EntityFactory.create(SortSortable.class);
            item.testId().setValue(testId);
            item.sortByEmbedded().name().setValue("A");
            item.sortByEmbedded().amount().setValue("1");
            srv.persist(item);
        }

        // Explicit sort
        {
            EntityQueryCriteria<SortSortable> criteria = EntityQueryCriteria.create(SortSortable.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.asc(criteria.proto().sortByEmbedded().name());
            criteria.asc(criteria.proto().sortByEmbedded().amount());

            List<SortSortable> r = srv.query(criteria);
            Assert.assertEquals("result set size", 3, r.size());

            Assert.assertEquals("sort Ok", "A", r.get(0).sortByEmbedded().name().getValue());
            Assert.assertEquals("sort Ok", "1", r.get(0).sortByEmbedded().amount().getValue());

            Assert.assertEquals("sort Ok", "A", r.get(1).sortByEmbedded().name().getValue());
            Assert.assertEquals("sort Ok", "2", r.get(1).sortByEmbedded().amount().getValue());

            Assert.assertEquals("sort Ok", "B", r.get(2).sortByEmbedded().name().getValue());
            Assert.assertEquals("sort Ok", "1", r.get(2).sortByEmbedded().amount().getValue());
        }

        // Created sort by ToString members
        {
            EntityQueryCriteria<SortSortable> criteria = EntityQueryCriteria.create(SortSortable.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.asc(criteria.proto().sortByEmbedded());

            List<SortSortable> r = srv.query(criteria);
            Assert.assertEquals("result set size", 3, r.size());

            Assert.assertEquals("sort Ok", "A", r.get(0).sortByEmbedded().name().getValue());
            Assert.assertEquals("sort Ok", "1", r.get(0).sortByEmbedded().amount().getValue());

            Assert.assertEquals("sort Ok", "A", r.get(1).sortByEmbedded().name().getValue());
            Assert.assertEquals("sort Ok", "2", r.get(1).sortByEmbedded().amount().getValue());

            Assert.assertEquals("sort Ok", "B", r.get(2).sortByEmbedded().name().getValue());
            Assert.assertEquals("sort Ok", "1", r.get(2).sortByEmbedded().amount().getValue());
        }
    }

    public void testSortByListValueToString() {
        String testId = uniqueString();
        {
            SortSortable item = EntityFactory.create(SortSortable.class);
            item.testId().setValue(testId);
            item.name().setValue("B3");
            SortBy member = item.sortByListMember().$();
            member.name().setValue("B");
            member.amount().setValue("1");
            item.sortByListMember().add(member);
            srv.persist(item);
        }
        {
            SortSortable item = EntityFactory.create(SortSortable.class);
            item.testId().setValue(testId);
            item.name().setValue("B2");
            SortBy member = item.sortByListMember().$();
            member.name().setValue("A");
            member.amount().setValue("2");
            item.sortByListMember().add(member);
            srv.persist(item);
        }
        {
            SortSortable item = EntityFactory.create(SortSortable.class);
            item.testId().setValue(testId);
            item.name().setValue("B1");
            {
                SortBy member = item.sortByListMember().$();
                member.name().setValue("A");
                member.amount().setValue("1");
                item.sortByListMember().add(member);
            }
            {
                SortBy member = item.sortByListMember().$();
                member.name().setValue("A");
                member.amount().setValue("1");
                item.sortByListMember().add(member);
            }
            srv.persist(item);
        }

        // Explicit sort
        {
            EntityQueryCriteria<SortSortable> criteria = EntityQueryCriteria.create(SortSortable.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.asc(criteria.proto().sortByListMember().$().name());
            criteria.asc(criteria.proto().sortByListMember().$().amount());

            try {
                List<SortSortable> r = srv.query(criteria);
                Assert.fail("Sort by collections is unsupported");

                // for future
                {
                    Assert.assertEquals("result set size", 3, r.size());
                    Assert.assertEquals("sort Ok", "A", r.get(0).sortByListMember().iterator().next().name().getValue());
                    Assert.assertEquals("sort Ok", "2", r.get(1).sortByListMember().iterator().next().amount().getValue());
                }
            } catch (Error expected) {
                // Ok
            }
        }

        // Created sort by ToString members
        {
            EntityQueryCriteria<SortSortable> criteria = EntityQueryCriteria.create(SortSortable.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.asc(criteria.proto().sortByListMember().$());

            try {
                List<SortSortable> r = srv.query(criteria);

                Assert.fail("Sort by collections is unsupported");

                // for future
                {
                    Assert.assertEquals("result set size", 3, r.size());

                    Assert.assertEquals("sort Ok", "A", r.get(0).sortByListMember().iterator().next().name().getValue());
                    Assert.assertEquals("sort Ok", "1", r.get(0).sortByListMember().iterator().next().amount().getValue());

                    Assert.assertEquals("sort Ok", "A", r.get(1).sortByListMember().iterator().next().name().getValue());
                    Assert.assertEquals("sort Ok", "2", r.get(1).sortByListMember().iterator().next().amount().getValue());

                    Assert.assertEquals("sort Ok", "B", r.get(2).sortByListMember().iterator().next().name().getValue());
                }
            } catch (Error expected) {
                // Ok
            }
        }
    }

    public void testSotrByAlphanum() {
        String testId = uniqueString();
        {
            SortSortable item = EntityFactory.create(SortSortable.class);
            item.testId().setValue(testId);
            item.alphanum().setValue("A2");
            srv.persist(item);
        }
        {
            SortSortable item = EntityFactory.create(SortSortable.class);
            item.testId().setValue(testId);
            item.alphanum().setValue("A10");
            srv.persist(item);
        }

        // Created sort by alphanum member
        {
            EntityQueryCriteria<SortSortable> criteria = EntityQueryCriteria.create(SortSortable.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.asc(criteria.proto().alphanum());

            List<SortSortable> r = srv.query(criteria);
            Assert.assertEquals("result set size", 2, r.size());

            Assert.assertEquals("sort Ok", "A2", r.get(0).alphanum().getValue());
            Assert.assertEquals("sort Ok", "A10", r.get(1).alphanum().getValue());
        }
    }

    public void testSotrByEmbeddedAlphanum() {
        String testId = uniqueString();
        {
            SortSortable item = EntityFactory.create(SortSortable.class);
            item.testId().setValue(testId);
            item.sortByEmbedded().alphanum().setValue("A2");
            srv.persist(item);
        }
        {
            SortSortable item = EntityFactory.create(SortSortable.class);
            item.testId().setValue(testId);
            item.sortByEmbedded().alphanum().setValue("A10");
            srv.persist(item);
        }

        // Created sort by alphanum member
        {
            EntityQueryCriteria<SortSortable> criteria = EntityQueryCriteria.create(SortSortable.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.asc(criteria.proto().sortByEmbedded().alphanum());

            List<SortSortable> r = srv.query(criteria);
            Assert.assertEquals("result set size", 2, r.size());

            Assert.assertEquals("sort Ok", "A2", r.get(0).sortByEmbedded().alphanum().getValue());
            Assert.assertEquals("sort Ok", "A10", r.get(1).sortByEmbedded().alphanum().getValue());
        }
    }

    public void testCriterionWithPathReference() {
        String testId = uniqueString();
        String value = uniqueString();
        {
            SortSortable item = EntityFactory.create(SortSortable.class);
            item.testId().setValue(testId);
            item.name().setValue("A");
            item.sortByOwned().name().setValue(value);
            srv.persist(item);
        }
        {
            SortSortable item = EntityFactory.create(SortSortable.class);
            item.testId().setValue(testId);
            item.name().setValue(value);
            item.sortByOwned().name().setValue(value);
            srv.persist(item);
        }

        {
            EntityQueryCriteria<SortSortable> criteria = EntityQueryCriteria.create(SortSortable.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().sortByOwned().name(), criteria.proto().name()));

            List<SortSortable> r = srv.query(criteria);
            Assert.assertEquals("result set size", 1, r.size());

            Assert.assertEquals("sort Ok", value, r.get(0).name().getValue());
        }
    }
}
