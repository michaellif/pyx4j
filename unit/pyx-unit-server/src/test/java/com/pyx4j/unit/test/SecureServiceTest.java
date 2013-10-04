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
 * Created on Feb 15, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.test;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.rpc.server.LocalService;
import com.pyx4j.security.shared.AclCreator;
import com.pyx4j.security.shared.CoreBehavior;
import com.pyx4j.security.shared.SecurityViolationException;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.unit.server.mock.TestLifecycle;
import com.pyx4j.unit.test.rpc.FirstServices;
import com.pyx4j.unit.test.rpc.SecureNonGrantedService;
import com.pyx4j.unit.test.rpc.SecureService;

public class SecureServiceTest extends TestCase {

    private static ServerSideConfiguration initOnce;

    @Override
    protected void setUp() throws Exception {
        if (initOnce == null) {
            initOnce = new ServerSideConfiguration() {
                @Override
                public AclCreator getAclCreator() {
                    return new SecureServiceTestAccessControlList();
                }
            };
            ServerSideConfiguration.setInstance(initOnce);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        TestLifecycle.tearDown();
    }

    public void testNonGrantedServiceSecurity() {
        TestLifecycle.testSession(new UserVisit(), CoreBehavior.USER);
        TestLifecycle.beginRequest();

        SecureNonGrantedService service = LocalService.create(SecureNonGrantedService.class);

        service.doEcho(new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                if (!(caught instanceof SecurityViolationException)) {
                    fail(caught.getClass() + "" + caught.getMessage());
                }
            }

            @Override
            public void onSuccess(String result) {
                fail("Service called onSuccess, SecurityViolationException expected");
            }
        }, null);

    }

    public void testGrantedServiceSecurity() {
        TestLifecycle.testSession(new UserVisit(), CoreBehavior.USER);
        TestLifecycle.beginRequest();

        SecureService service = LocalService.create(SecureService.class);

        service.doEcho(new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                if (!(caught instanceof SecurityViolationException)) {
                    fail(caught.getClass() + "" + caught.getMessage());
                }
            }

            @Override
            public void onSuccess(String result) {
                fail("Service called onSuccess, SecurityViolationException expected");
            }
        }, null);

        TestLifecycle.testSession(new UserVisit(), CoreBehavior.DEVELOPER);
        TestLifecycle.beginRequest();

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
        TestLifecycle.testSession(new UserVisit(), CoreBehavior.DEVELOPER);
        TestLifecycle.beginRequest();
        SecureService service = LocalService.create(SecureService.class);

        // Verify if service works at all and onSuccess is called once
        final AtomicBoolean onSuccessCalled = new AtomicBoolean();
        service.doException(new AsyncCallback<Boolean>() {

            @Override
            public void onFailure(Throwable caught) {
                fail(caught.getMessage());
            }

            @Override
            public void onSuccess(Boolean result) {
                assertFalse("onSuccess called more then one time", onSuccessCalled.get());
                onSuccessCalled.set(true);
                assertEquals("Result not expected", Boolean.FALSE, result);
            }
        }, Boolean.FALSE);

        //Test regular serializable Exception
        final AtomicBoolean onFailureCalled1 = new AtomicBoolean();
        service.doException(new AsyncCallback<Boolean>() {

            @Override
            public void onFailure(Throwable caught) {
                assertFalse("onFailure called more then one time", onFailureCalled1.get());
                onFailureCalled1.set(true);
                Assert.assertTrue("Throwable Class " + caught.getClass().getName(), caught instanceof UserRuntimeException);
                assertEquals("Error message expected", FirstServices.EXCEPTION_MESSAGE, caught.getMessage());
            }

            @Override
            public void onSuccess(Boolean result) {
                fail("Exception should be thrown");
            }
        }, Boolean.TRUE);

        //Test Exception in Callback
        final AtomicBoolean onFailureCalled2 = new AtomicBoolean();
        try {
            service.doException(new AsyncCallback<Boolean>() {

                @Override
                public void onFailure(Throwable caught) {
                    assertFalse("onFailure called more then one time", onFailureCalled2.get());
                    onFailureCalled2.set(true);
                    throw new RejectedExecutionException("Ok");
                }

                @Override
                public void onSuccess(Boolean result) {
                    fail("Exception should be thrown");
                }
            }, Boolean.TRUE);

            fail("Exception should be thrown from onFailure");

        } catch (RejectedExecutionException ok) {
            assertEquals("Error message expected", "Ok", ok.getMessage());
        }
    }
}
