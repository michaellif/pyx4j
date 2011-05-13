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
 * Created on 2011-05-13
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.client.tests;

import java.util.Date;
import java.util.Stack;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Address;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.test.client.TestServices;
import com.pyx4j.unit.client.GUnitTester;

public class DateSerializationGWTTest extends TestCase {

    static final int TIME_OUT = 10 * 1000;

    private static final Logger log = LoggerFactory.getLogger(DateSerializationGWTTest.class);

    private static class ScenarioData {

        Address addr = EntityFactory.create(Address.class);

        String message;

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void validateDateServerResponce(final ScenarioData scenario, final Runnable nextScenario) {
        final AsyncCallback callback = new AsyncCallback<Address>() {

            @Override
            public void onFailure(Throwable t) {
                fail(t.getClass().getName() + "[" + t.getMessage() + "]");
            }

            @Override
            public void onSuccess(Address result) {
                Assert.assertTrue("Address class expected", (result instanceof Address));
                Address addr2 = result;
                log.debug("got effectiveFrom {}", addr2.effectiveFrom());
                log.debug("got effectiveTo {}", addr2.effectiveTo());

                if (!addr2.effectiveTo().isNull()) {
                    Assert.assertEquals("java.util.Date Class of Value", Date.class, addr2.effectiveTo().getValue().getClass());
                }
                Assert.assertEquals("java.util.Date Value", scenario.addr.effectiveTo(), addr2.effectiveTo());

                if (!addr2.effectiveFrom().isNull()) {
                    Assert.assertEquals("java.sql.Date Class of Value", java.sql.Date.class, addr2.effectiveFrom().getValue().getClass());
                }
                Assert.assertEquals("java.sql.Date Value", scenario.addr.effectiveFrom(), addr2.effectiveFrom());

                nextScenario.run();
            }
        };
        log.debug("sending effectiveFrom {}", scenario.addr.effectiveFrom());
        log.debug("sending effectiveTo {}", scenario.addr.effectiveTo());
        RPCManager.execute(TestServices.EchoSerializable.class, scenario.addr, callback);
    }

    @SuppressWarnings("deprecation")
    public static java.sql.Date createSqlDate(int year, int month, int day) {
        return new java.sql.Date(new Date(year - 1900, month - 1, day).getTime());
    }

    @SuppressWarnings("deprecation")
    public static Date createDate(int year, int month, int day, int hh, int mm) {
        return new Date(year - 1900, month - 1, day, hh, mm);
    }

    public void testDateSerialization() {
        GUnitTester.delayTestFinish(this, TIME_OUT);

        // 04/04/1999 - saved as 04/03/1999
        // 03/12/1962 - saved as 03/11/1962
        // ..all in between with problems
        // 03/30/1962 - saved as 03/29/1962

        final Stack<ScenarioData> scenarios = new Stack<ScenarioData>();
        scenarios.push(new ScenarioData() {
            {
                addr.effectiveFrom().setValue(createSqlDate(1999, 04, 04));
            }
        });

        scenarios.push(new ScenarioData() {
            {
                addr.effectiveFrom().setValue(createSqlDate(1962, 03, 12));
            }
        });

        scenarios.push(new ScenarioData() {
            {
                addr.effectiveFrom().setValue(createSqlDate(1962, 03, 29));
            }
        });

        boolean testRandomizedData = true;
        if (testRandomizedData) {
            for (int i = 1; i < 100; i++) {
                scenarios.push(new ScenarioData() {
                    {
                        addr.effectiveFrom().setValue(createSqlDate(1900 + Random.nextInt(200), 1 + Random.nextInt(12), Random.nextInt(28)));

                        addr.effectiveTo().setValue(
                                createDate(1900 + Random.nextInt(200), 1 + Random.nextInt(12), Random.nextInt(28), Random.nextInt(24), Random.nextInt(60)));
                    }
                });
            }
        }

        Runnable exec = new Runnable() {
            @Override
            public void run() {
                if (scenarios.isEmpty()) {
                    GUnitTester.finishTest(DateSerializationGWTTest.this);
                } else {
                    validateDateServerResponce(scenarios.pop(), this);
                }
            }
        };

        exec.run();
    }
}
