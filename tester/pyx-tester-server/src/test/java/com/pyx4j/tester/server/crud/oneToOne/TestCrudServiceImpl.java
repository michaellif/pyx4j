/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2012-10-20
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.server.crud.oneToOne;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.test.shared.domain.join.OneToOneReadOwner;
import com.pyx4j.tester.server.crud.DBTestsSetup;
import com.pyx4j.tester.shared.crud.oneToOne.OneToOneCrudService;
import com.pyx4j.unit.server.AsyncCallbackAssertion;
import com.pyx4j.unit.server.TestServiceFactory;
import com.pyx4j.unit.server.mock.TestLifecycle;

public class TestCrudServiceImpl extends TestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        DBTestsSetup.defaultInit();
        //        TestLifecycle.beginRequest();
    }

    @Override
    protected void tearDown() throws Exception {
        TestLifecycle.tearDown();
    }

    public void testPersit() {
        String testId = UUID.randomUUID().toString();

        final OneToOneReadOwner o = EntityFactory.create(OneToOneReadOwner.class);
        o.name().setValue(UUID.randomUUID().toString());
        o.testId().setValue(testId);

        OneToOneCrudService service = TestServiceFactory.create(OneToOneCrudService.class);

        final AtomicReference<Key> entityPk = new AtomicReference<Key>(null);

        service.create(new AsyncCallbackAssertion<Key>() {

            @Override
            public void onSuccess(Key result) {
                assertNotNull("Service execution results", result);
                entityPk.set(result);
                o.setPrimaryKey(result);
            }
        }, o);

        //Test table initialization
        {
            OneToOneReadOwner o1r = Persistence.service().retrieve(OneToOneReadOwner.class, entityPk.get());
            Assert.assertNull("Got child object", o1r.child().getPrimaryKey());
            // There are no data e.g. Value is null, consider it Attached
            Assert.assertEquals("Got child object Attached", AttachLevel.Attached, o1r.child().getAttachLevel());
        }

        // --- Retrieve
        final AtomicReference<OneToOneReadOwner> entityR1 = new AtomicReference<OneToOneReadOwner>(null);

        service.retrieve(new AsyncCallbackAssertion<OneToOneReadOwner>() {

            @Override
            public void onSuccess(OneToOneReadOwner result) {
                entityR1.set(result);
            }
        }, entityPk.get(), RetrieveTarget.Edit);

        assertTrue("Not Same data", EntityGraph.fullyEqual(entityR1.get(), o));

        // --- Update

        entityR1.get().name().setValue(UUID.randomUUID().toString());

        service.save(new AsyncCallbackAssertion<Key>() {

            @Override
            public void onSuccess(Key result) {
                Assert.assertEquals("", entityPk.get(), result);
            }
        }, entityR1.get());

        final AtomicReference<OneToOneReadOwner> entityR2 = new AtomicReference<OneToOneReadOwner>(null);

        service.retrieve(new AsyncCallbackAssertion<OneToOneReadOwner>() {

            @Override
            public void onSuccess(OneToOneReadOwner result) {
                entityR2.set(result);
            }
        }, entityPk.get(), RetrieveTarget.View);

        assertTrue("Not Same data", EntityGraph.fullyEqual(entityR2.get(), entityR1.get()));
    }
}
