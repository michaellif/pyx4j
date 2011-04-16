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
 * Created on Jan 23, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.server;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import junit.framework.TestCase;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.test.env.ConfigureTestsEnv;

/**
 * This is the base for abstract Server side tests. RDBMS or GAE test would have their own
 * PersistenceEnvironment implementation.
 */
public abstract class DatastoreTestBase extends TestCase {

    protected IEntityPersistenceService srv;

    private static int uniqueCount = 0;

    protected abstract PersistenceEnvironment getPersistenceEnvironment();

    private PersistenceEnvironment persistenceEnvironment;

    protected Random random = new Random(256);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ConfigureTestsEnv.configure();
        persistenceEnvironment = getPersistenceEnvironment();
        if (persistenceEnvironment != null) {
            persistenceEnvironment.setupDatastore();
        }
        srv = PersistenceServicesFactory.getPersistenceService();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (persistenceEnvironment != null) {
            persistenceEnvironment.teardownDatastore();
        }
    }

    protected void assertFullyEqual(String message, IEntity ent1, IEntity ent2) {
        assertTrue(message + "\n" + ent1.toString() + "\n!=\n" + ent2.toString(), EntityGraph.fullyEqual(ent1, ent2));
    }

    public synchronized String uniqueString() {
        return Integer.toHexString(++uniqueCount) + "_" + Long.toHexString(System.currentTimeMillis()) + " " + this.getName();
    }

    public static Date getRoundedNow() {
        Calendar c = new GregorianCalendar();
        // DB does not store Milliseconds
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    protected Date randomDate() {
        Calendar c = new GregorianCalendar();
        c.add(Calendar.MONTH, -random.nextInt(1024));
        // DB does not store Milliseconds
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    protected java.sql.Date randomSqlDate() {
        return new java.sql.Date(TimeUtils.dayStart(randomDate()).getTime());
    }
}
