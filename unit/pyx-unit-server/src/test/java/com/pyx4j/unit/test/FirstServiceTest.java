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

import com.pyx4j.unit.server.MockServiceFactory;
import com.pyx4j.unit.test.rpc.FirstServices;

public class FirstServiceTest extends TestCase {

    public void testNothing() {

    }

    public void TODO_testMockServiceFactory() {
        FirstServices service = MockServiceFactory.create(FirstServices.class);
        Assert.assertNotNull("Service Not Created", service);
        service.doNow(new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {
                fail(caught.getMessage());
            }

            @Override
            public void onSuccess(Boolean result) {
                assertEquals("Service exeution results", Boolean.TRUE, result);
            }
        }, "Hello");
    }
}
