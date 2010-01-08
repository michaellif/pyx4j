/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Jan 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.client.tests;

import java.util.Vector;

import junit.framework.TestCase;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.client.ClientEntityFactory;
import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.shared.EntityCriteria;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.test.shared.domain.Country;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.unit.client.GUnitTester;

public class EntityServicesGWTTest extends TestCase {

    static final int TIME_OUT = 10 * 1000;

    @Override
    protected void setUp() throws Exception {
        GUnitTester.delayTestFinish(this, TIME_OUT);
        EntityFactory.setImplementation(new ClientEntityFactory());
    }

    public void testSave() {
        final Country country = EntityFactory.create(Country.class);
        String countryName = "Canada_cs_" + System.currentTimeMillis();
        country.name().setValue(countryName);

        final AsyncCallback<IEntity<?>> callback = new AsyncCallback<IEntity<?>>() {

            public void onFailure(Throwable t) {
                fail(t.getMessage());
            }

            public void onSuccess(IEntity<?> result) {
                assertEquals("Result Class Value", country.getClass(), result.getClass());
                assertEquals("Result Value", country.name().getValue(), ((Country) result).name().getValue());
                assertNotNull("Result PK Value", ((Country) result).getPrimaryKey());
                GUnitTester.finishTest(EntityServicesGWTTest.this);
            }
        };

        RPCManager.execute(EntityServices.Save.class, country, callback);
    }

    public void testEmptyQuery() {
        EntityCriteria<Country> cc = new EntityCriteria<Country>(Country.class);

        final Country countryMeta = EntityFactory.create(Country.class);
        cc.add(PropertyCriterion.eq(countryMeta.name(), "Neverland"));

        final AsyncCallback callback = new AsyncCallback<Vector<Country>>() {

            public void onFailure(Throwable t) {
                fail(t.getMessage());
            }

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

        final EntityCriteria<Country> cc = new EntityCriteria<Country>(Country.class);
        Country countryMeta = EntityFactory.create(Country.class);
        cc.add(PropertyCriterion.eq(countryMeta.name(), countryName));

        final AsyncCallback callback = new AsyncCallback<Vector<Country>>() {

            public void onFailure(Throwable t) {
                fail(t.getMessage());
            }

            public void onSuccess(Vector<Country> result) {
                assertEquals("Result Class Value", 1, result.size());
                assertEquals("Result Value", countryName, result.get(0).name().getValue());
                GUnitTester.finishTest(EntityServicesGWTTest.this);
            }
        };

        final AsyncCallback<IEntity<?>> setUpCallback = new AsyncCallback<IEntity<?>>() {

            public void onFailure(Throwable t) {
                fail(t.getMessage());
            }

            public void onSuccess(IEntity<?> result) {
                RPCManager.execute(EntityServices.Query.class, cc, callback);
            }
        };

        RPCManager.execute(EntityServices.Save.class, country, setUpCallback);
    }
}
