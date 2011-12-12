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
import com.pyx4j.entity.shared.criterion.OrCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.Address;
import com.pyx4j.entity.test.shared.domain.City;
import com.pyx4j.entity.test.shared.domain.Employee;
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
            criteria.add(PropertyCriterion.in(criteria.proto().id(), emps));

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

            List<Employee> empsRetrived = srv.query(criteria);
            Assert.assertEquals("result set size", 2, empsRetrived.size());
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
}
