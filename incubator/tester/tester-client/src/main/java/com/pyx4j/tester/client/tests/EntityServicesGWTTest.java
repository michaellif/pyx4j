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
 * Created on Jan 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.client.tests;

import java.util.Vector;

import junit.framework.TestCase;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.test.shared.domain.Country;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.unit.client.GUnitTester;

public class EntityServicesGWTTest extends TestCase {

    static final int TIME_OUT = 10 * 1000;

    @Override
    protected void setUp() throws Exception {
        GUnitTester.delayTestFinish(this, TIME_OUT);
    }

    public void testSave() {
        final Country country = EntityFactory.create(Country.class);
        String countryName = "Canada_cs_" + System.currentTimeMillis();
        country.name().setValue(countryName);

        final AsyncCallback<IEntity> callback = new AsyncCallback<IEntity>() {

            @Override
            public void onFailure(Throwable t) {
                fail(t.getClass().getName() + "[" + t.getMessage() + "]");
            }

            @Override
            public void onSuccess(IEntity result) {
                assertEquals("Result Class Value", country.getClass(), result.getClass());
                assertEquals("Result Value", country.name().getValue(), ((Country) result).name().getValue());
                assertNotNull("Result PK Value", ((Country) result).getPrimaryKey());
                GUnitTester.finishTest(EntityServicesGWTTest.this);
            }
        };

        RPCManager.execute(EntityServices.Save.class, country, callback);
    }

    public void testEmptyQuery() {
        EntityQueryCriteria<Country> cc = new EntityQueryCriteria<Country>(Country.class);

        final Country countryMeta = EntityFactory.create(Country.class);
        cc.add(PropertyCriterion.eq(countryMeta.name(), "Neverland"));

        final AsyncCallback callback = new AsyncCallback<Vector<Country>>() {

            @Override
            public void onFailure(Throwable t) {
                fail(t.getClass().getName() + "[" + t.getMessage() + "]");
            }

            @Override
            public void onSuccess(Vector<Country> result) {
                assertEquals("Result Class Value", 0, result.size());
                GUnitTester.finishTest(EntityServicesGWTTest.this);
            }
        };

        RPCManager.execute(EntityServices.Query.class, cc, callback);
    }

    public void testQuery() {
        Country country = EntityFactory.create(Country.class);
        final String countryName = "Canada_cq_" + System.currentTimeMillis();
        country.name().setValue(countryName);

        final EntityQueryCriteria<Country> cc = new EntityQueryCriteria<Country>(Country.class);
        Country countryMeta = EntityFactory.create(Country.class);
        cc.add(PropertyCriterion.eq(countryMeta.name(), countryName));

        final AsyncCallback callback = new AsyncCallback<Vector<Country>>() {

            @Override
            public void onFailure(Throwable t) {
                fail(t.getClass().getName() + "[" + t.getMessage() + "]");
            }

            @Override
            public void onSuccess(Vector<Country> result) {
                assertEquals("Result Class Value", 1, result.size());
                assertEquals("Result Value", countryName, result.get(0).name().getValue());
                GUnitTester.finishTest(EntityServicesGWTTest.this);
            }
        };

        final AsyncCallback<IEntity> setUpCallback = new AsyncCallback<IEntity>() {

            @Override
            public void onFailure(Throwable t) {
                fail(t.getClass().getName() + "[" + t.getMessage() + "]");
            }

            @Override
            public void onSuccess(IEntity result) {
                RPCManager.execute(EntityServices.Query.class, cc, callback);
            }
        };

        RPCManager.execute(EntityServices.Save.class, country, setUpCallback);
    }
}
