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
 */
package com.pyx4j.entity.test.server;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.config.shared.ApplicationBackend;
import com.pyx4j.config.shared.ApplicationBackend.ApplicationBackendType;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.test.env.ConfigureTestsEnv;
import com.pyx4j.entity.test.shared.InitializerTestBase;
import com.pyx4j.gwt.server.DateUtils;

/**
 * This is the base for abstract Server side tests. RDBMS or GAE test would have their own
 * PersistenceEnvironment implementation.
 */
public abstract class DatastoreTestBase extends TestCase {

    private static final Logger log = LoggerFactory.getLogger(DatastoreTestBase.class);

    protected IEntityPersistenceService srv;

    private static int uniqueCount = 0;

    protected enum TestCaseMethod {

        Persist,

        Merge,

        PersistCollection
    }

    protected abstract PersistenceEnvironment getPersistenceEnvironment();

    private PersistenceEnvironment persistenceEnvironment;

    protected Random random = new Random(256);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ConfigureTestsEnv.configure();
        persistenceEnvironment = getPersistenceEnvironment();
        if (persistenceEnvironment != null) {
            srv = persistenceEnvironment.setupDatastore();
            if (ApplicationBackend.getBackendType() != ApplicationBackendType.GAE) {
                PersistenceServicesFactory.setPersistenceService(srv);
            }
        }
        if (srv == null) {
            srv = PersistenceServicesFactory.getPersistenceService();
        }
        srv.startTransaction(TransactionScopeOption.Suppress, ConnectionTarget.Web);
        log.debug("start test {}.{}", this.getClass().getName(), this.getName());
    }

    protected ApplicationBackendType getBackendType() {
        return persistenceEnvironment.getBackendType();
    }

    public static void requireJavaAssertEnabled() {
        if (!InitializerTestBase.isJavaAssertEnabled()) {
            throw new Error("This test is expected to run with assertion on; add -ea to java start arguments");
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (persistenceEnvironment != null) {
            // Want to see data in tables even if test fails.
            try {
                srv.commit();
            } catch (Throwable ignore) {
            }
            try {
                try {
                    if (srv.getTransactionScopeOption() != null) {
                        srv.endTransaction();
                    }
                } finally {
                    srv.removeThreadLocale();
                }
            } finally {
                persistenceEnvironment.teardownDatastore(srv);
            }
        }
        log.debug("ended test {}.{}", this.getClass().getName(), this.getName());
    }

    /**
     * Emulate Database current time
     */
    protected void setDBTime(String dateStr) {
        SystemDateManager.setDate(DateUtils.detectDateformat(dateStr));
    }

    static public void assertValueEquals(String message, Object expected, Object actual) {
        InitializerTestBase.assertValueEquals(message, expected, actual);
    }

    protected void assertFullyEqual(String message, IEntity ent1, IEntity ent2) {
        assertTrue(message + "\n" + EntityGraph.getChangedDataPath(ent1, ent2) + "\n" + ent1.toString() + "\n!=\n" + ent2.toString(),
                EntityGraph.fullyEqual(ent1, ent2));
    }

    public synchronized String uniqueString() {
        return Integer.toHexString(++uniqueCount) + "_" + Long.toHexString(System.currentTimeMillis()) + " " + this.getName();
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

    protected <T extends IEntity> void srvSave(T ent, TestCaseMethod testCaseMethod) {
        switch (testCaseMethod) {
        case Persist:
            srv.persist(ent);
            break;
        case Merge:
            srv.merge(ent);
            break;
        case PersistCollection:
            List<T> collection = new Vector<T>();
            collection.add(ent);
            srv.persist(collection);
            break;
        }
    }
}
