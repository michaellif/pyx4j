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
 * Created on 2011-03-25
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.test;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.unit.server.MockServiceFactory;
import com.pyx4j.unit.test.rpc.FirstServices;

public class FirstServiceTest extends TestCase {

    public void testMockServiceFactory() {
        FirstServices service = MockServiceFactory.create(FirstServices.class);
        Assert.assertNotNull("Service Not Created", service);
        Assert.assertTrue("Service Class", service instanceof FirstServices);

        service.doNow(new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {
                fail(caught.getMessage());
            }

            @Override
            public void onSuccess(Boolean result) {
                assertEquals("Service execution results", Boolean.TRUE, result);
            }
        }, "Hello");
    }

    public void testMockServiceEcho() {
        FirstServices service = MockServiceFactory.create(FirstServices.class);

        final String data = String.valueOf(System.currentTimeMillis());

        service.doEcho(new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                fail(caught.getMessage());
            }

            @Override
            public void onSuccess(String result) {
                assertEquals("Service execution results", data, result);
            }
        }, data);

    }

    public void testException() {
        FirstServices service = MockServiceFactory.create(FirstServices.class);

        // Verify if service works at all
        service.doException(new AsyncCallback<Boolean>() {

            @Override
            public void onFailure(Throwable caught) {
                fail(caught.getMessage());
            }

            @Override
            public void onSuccess(Boolean result) {
                assertEquals("Error not expected", Boolean.FALSE, result);
            }
        }, Boolean.FALSE);

        //Test regular serializable Exception
        service.doException(new AsyncCallback<Boolean>() {

            @Override
            public void onFailure(Throwable caught) {
                assertEquals("Error message expected", FirstServices.EXCEPTION_MESSAGE, caught.getMessage());
                Assert.assertTrue("Throwable Class", caught instanceof UserRuntimeException);
            }

            @Override
            public void onSuccess(Boolean result) {
                fail("Exception should be thrown");
            }
        }, Boolean.TRUE);

        // Test non serializable wrapper
        service.doException(new AsyncCallback<Boolean>() {

            @Override
            public void onFailure(Throwable caught) {
                Assert.assertTrue("Throwable Class", !(caught instanceof NullPointerException));
                Assert.assertTrue("Throwable Class serializable", caught instanceof UnRecoverableRuntimeException);
            }

            @Override
            public void onSuccess(Boolean result) {
                fail("Exception should be thrown");
            }
        }, null);
    }
}
