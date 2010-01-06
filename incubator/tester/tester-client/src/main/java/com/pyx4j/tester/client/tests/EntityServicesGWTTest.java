/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.client.tests;

import junit.framework.TestCase;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.test.shared.domain.Country;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.unit.client.GUnitTester;

public class EntityServicesGWTTest extends TestCase {

    static final int TIME_OUT = 10 * 1000;

    public void testSave() {

        GUnitTester.delayTestFinish(this, TIME_OUT);

        final Country country = EntityFactory.create(Country.class);
        String countryName = "Canada_c_" + System.currentTimeMillis();
        country.name().setValue(countryName);

        final AsyncCallback<IEntity<?>> callback = new AsyncCallback<IEntity<?>>() {

            public void onFailure(Throwable t) {
                t.printStackTrace();
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
}
