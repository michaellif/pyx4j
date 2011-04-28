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
 * Created on 2011-04-28
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.server;

import java.util.Date;

import junit.framework.Assert;

import com.pyx4j.commons.Consts;
import com.pyx4j.entity.shared.ConcurrentUpdateException;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.temporal.WithTimestamp;
import com.pyx4j.security.shared.SecurityViolationException;

public abstract class TimestampTestCase extends DatastoreTestBase {

    public void testCreatedTimestamp() {
        WithTimestamp ent = EntityFactory.create(WithTimestamp.class);
        ent.name().setValue(uniqueString());

        srv.persist(ent);

        WithTimestamp ent2 = srv.retrieve(WithTimestamp.class, ent.getPrimaryKey());
        Assert.assertNotNull("retrieve", ent2);
        Assert.assertEquals("name Value", ent.name().getValue(), ent2.name().getValue());
        Assert.assertNotNull("Timestamp Created", ent2.created().getValue());
        Assert.assertNotNull("Timestamp Updated", ent2.updated().getValue());
    }

    public void testCreatedTimestampChange() {
        WithTimestamp ent = EntityFactory.create(WithTimestamp.class);
        ent.name().setValue(uniqueString());

        srv.persist(ent);
        Date origCreationDate = ent.created().getValue();

        Date otherDate = randomDate();
        ent.created().setValue(otherDate);
        ent.name().setValue(uniqueString());
        boolean saved = false;
        try {
            srv.merge(ent);
            saved = true;
        } catch (SecurityViolationException e) {
            // OK
        }
        if (saved) {
            fail("Should not allow to change created Timestamp");
        }

        WithTimestamp ent2 = srv.retrieve(WithTimestamp.class, ent.getPrimaryKey());
        Assert.assertEquals("created Value", origCreationDate, ent2.created().getValue());
    }

    public void testUpdatedTimestamp() throws InterruptedException {
        WithTimestamp ent = EntityFactory.create(WithTimestamp.class);
        ent.name().setValue(uniqueString());

        srv.persist(ent);

        Date otherDate = randomDate();
        ent.updated().setValue(otherDate);

        srv.persist(ent);

        WithTimestamp ent2 = srv.retrieve(WithTimestamp.class, ent.getPrimaryKey());
        assertFalse("date ignored", otherDate.equals(ent2.updated().getValue()));

        Thread.sleep(1 * Consts.SEC2MSEC + 200);

        srv.persist(ent);

        WithTimestamp ent3 = srv.retrieve(WithTimestamp.class, ent.getPrimaryKey());
        assertFalse("date updated", ent3.updated().getValue().equals(ent2.updated().getValue()));

    }

    public void testConcurrentUpdate() {
        WithTimestamp ent = EntityFactory.create(WithTimestamp.class);
        ent.name().setValue(uniqueString());

        srv.persist(ent);

        // Test if Update works
        ent.name().setValue(uniqueString());
        srv.merge(ent);

        Date otherDate = randomDate();
        ent.updated().setValue(otherDate);

        boolean saved = false;
        try {
            srv.merge(ent);
            saved = true;
        } catch (ConcurrentUpdateException e) {
            // OK
        }
        if (saved) {
            fail("Should not allow to change updated Timestamp");
        }
    }
}
