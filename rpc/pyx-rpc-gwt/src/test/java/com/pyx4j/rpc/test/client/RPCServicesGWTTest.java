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
 * Created on Dec 29, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.test.client;

import junit.framework.TestCase;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.unit.client.GUnitTester;

public class RPCServicesGWTTest extends TestCase {

    static final int TIME_OUT = 10 * 1000;

    public void testServiceEcho() {

        GUnitTester.delayTestFinish(this, TIME_OUT);

        final AsyncCallback<String> callback = new AsyncCallback<String>() {

            public void onFailure(Throwable t) {
                fail(t.getClass().getName() + "[" + t.getMessage() + "]");
            }

            public void onSuccess(String result) {
                assertEquals("Result Value", "Test22", result);
                GUnitTester.finishTest(RPCServicesGWTTest.this);
            }
        };

        RPCManager.execute(TestServices.Echo.class, new String("Test22"), callback);
    }

    public void testServiceThrowException() {

        GUnitTester.delayTestFinish(this, TIME_OUT);

        final AsyncCallback<String> callbackFailure = new AsyncCallback<String>() {

            public void onFailure(Throwable t) {
                //assertTrue("Throwable class", t instanceof ServiceCallException);
                assertEquals("Failure Value", "Test22", t.getMessage());
                GUnitTester.finishTest(RPCServicesGWTTest.this);
            }

            public void onSuccess(String result) {
                fail("Should throw exception");
            }
        };

        final AsyncCallback<String> callbackSuccess = new AsyncCallback<String>() {

            public void onFailure(Throwable t) {
                fail("Should not throw exception [" + t.getClass().getName() + "]");
                GUnitTester.finishTest(RPCServicesGWTTest.this);
            }

            public void onSuccess(String result) {
                RPCManager.execute(TestServices.ThrowException.class, new String("Test22"), callbackFailure);
            }
        };

        RPCManager.execute(TestServices.ThrowException.class, new String(), callbackSuccess);

    }
}
