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
 * Created on Mar 14, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.client.tests;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.test.shared.domain.version.ItemA;
import com.pyx4j.tester.rpc.TesterEntitySerializationService;
import com.pyx4j.unit.client.GUnitTester;

public class VersionedEntitySerializationGWTTest extends TestCase {

    static final int TIME_OUT = 10 * 1000;

    public void testEchoEmptyEntitySerialization() {
        GUnitTester.delayTestFinish(this, TIME_OUT);
        final ItemA request = EntityFactory.create(ItemA.class);

        final AsyncCallback<IEntity> callback = new AsyncCallback<IEntity>() {

            @Override
            public void onFailure(Throwable t) {
                fail(t.getClass().getName() + "[" + t.getMessage() + "]");
            }

            @Override
            public void onSuccess(IEntity result) {
                Assert.assertTrue("Employee class expected", (result instanceof ItemA));
                ItemA item2 = (ItemA) result;
                Assert.assertTrue("isNull", item2.isNull());
                GUnitTester.finishTest(VersionedEntitySerializationGWTTest.this);
            }
        };

        GWT.<TesterEntitySerializationService> create(TesterEntitySerializationService.class).echo(callback, request);
    }

    public void testEchoVersionValuesSerialization() {
        GUnitTester.delayTestFinish(this, TIME_OUT);
        final ItemA request = EntityFactory.create(ItemA.class);
        request.setPrimaryKey(new Key(10).asDraftKey());
        request.name().setValue("data1");
        request.version().name().setValue("data2");

        final AsyncCallback<IEntity> callback = new AsyncCallback<IEntity>() {

            @Override
            public void onFailure(Throwable t) {
                fail(t.getClass().getName() + "[" + t.getMessage() + "]");
            }

            @Override
            public void onSuccess(IEntity result) {
                Assert.assertTrue("Employee class expected", (result instanceof ItemA));
                ItemA item2 = (ItemA) result;
                Assert.assertFalse("isNull", item2.isNull());

                Assert.assertEquals("Key", request.getPrimaryKey(), item2.getPrimaryKey());
                Assert.assertEquals("Key.getVersion", request.getPrimaryKey().getVersion(), item2.getPrimaryKey().getVersion());

                Assert.assertEquals("Value", request.name().getValue(), item2.name().getValue());
                Assert.assertEquals("version Value", request.version().name().getValue(), item2.version().name().getValue());

                GUnitTester.finishTest(VersionedEntitySerializationGWTTest.this);
            }
        };

        GWT.<TesterEntitySerializationService> create(TesterEntitySerializationService.class).echo(callback, request);
    }
}
