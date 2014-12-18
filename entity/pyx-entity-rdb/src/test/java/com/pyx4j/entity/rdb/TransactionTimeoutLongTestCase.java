/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Oct 13, 2014
 * @author vlads
 */
package com.pyx4j.entity.rdb;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import com.pyx4j.commons.Consts;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.CompensationHandler;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.Simple1;
import com.pyx4j.entity.test.shared.domain.Simple2;

public abstract class TransactionTimeoutLongTestCase extends DatastoreTestBase {

    @Override
    protected void setUp() throws Exception {
        TestsConnectionPoolConfiguration.overrideUnreturnedConnectionTimeout.set(30);
        super.setUp();
        // Initialize table before tests for postgresql to work, TODO fix this
        srv.count(EntityQueryCriteria.create(Simple1.class));
        srv.count(EntityQueryCriteria.create(Simple2.class));
    }

    @Override
    protected void tearDown() throws Exception {
        TestsConnectionPoolConfiguration.overrideUnreturnedConnectionTimeout.remove();
        super.tearDown();
    }

    public void testTransactionTimeoutOtherAutoCommit() throws InterruptedException {
        runTransactionTimeout(TransactionScopeOption.Suppress);
    }

    public void testTransactionTimeoutOtherNoAutoCommit() throws InterruptedException {
        runTransactionTimeout(TransactionScopeOption.RequiresNew);
    }

    public void runTransactionTimeout(TransactionScopeOption otherTransactionScopeOption) throws InterruptedException {
        final String setId = uniqueString();
        srv.endTransaction();

        srv.startTransaction(TransactionScopeOption.RequiresNew, ConnectionTarget.Web);
        try {
            srv.persist(createEntity(setId, "1.0"));
            Thread.sleep(Consts.MIN2MSEC * 2);

            // Timeout error will happen here
            try {
                srv.persist(createEntity(setId, "1.1"));

                Assert.fail("Should throw Exception");
            } catch (RuntimeException connectionDoesNotExistOk) {
            }
        } finally {
            try {
                srv.endTransaction();
            } catch (AssertionError uncommittedChangesOk) {
            }
        }
        makeManyCommits(otherTransactionScopeOption);

        assertNotExists(setId, "1.0");
        assertNotExists(setId, "1.1");
    }

    enum ErrorCondition {
        MainFlow, Nested, Commit,
    }

    public void testUnitOfWorkTimeoutOtherAutoCommitErrorInMainFlow() throws InterruptedException {
        runUnitOfWorkTimeout(TransactionScopeOption.Suppress, ErrorCondition.MainFlow);
    }

    public void testUnitOfWorkTimeoutOtherAutoCommitErrorInNested() throws InterruptedException {
        runUnitOfWorkTimeout(TransactionScopeOption.Suppress, ErrorCondition.Nested);
    }

    public void testUnitOfWorkTimeoutOtherAutoCommitErrorInCommit() throws InterruptedException {
        runUnitOfWorkTimeout(TransactionScopeOption.Suppress, ErrorCondition.Commit);
    }

    public void testUnitOfWorkTimeoutOtherNoAutoCommitErrorInMainFlow() throws InterruptedException {
        runUnitOfWorkTimeout(TransactionScopeOption.RequiresNew, ErrorCondition.MainFlow);
    }

    public void testUnitOfWorkTimeoutOtherNoAutoCommitErrorInNested() throws InterruptedException {
        runUnitOfWorkTimeout(TransactionScopeOption.RequiresNew, ErrorCondition.Nested);
    }

    public void testUnitOfWorkTimeoutOtherNoAutoCommitErrorInCommit() throws InterruptedException {
        runUnitOfWorkTimeout(TransactionScopeOption.RequiresNew, ErrorCondition.Commit);
    }

    private void runUnitOfWorkTimeout(TransactionScopeOption otherTransactionScopeOption, final ErrorCondition errorCondition) throws InterruptedException {
        final String setId = uniqueString();
        srv.endTransaction();

        final int maxNested = 4;
        try {
            new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.Web).execute(new Executable<Void, RuntimeException>() {

                @Override
                public Void execute() throws RuntimeException {
                    srv.persist(createEntity(setId, "1.0"));

                    UnitOfWork.addTransactionCompensationHandler(new CompensationHandler() {

                        @Override
                        public Void execute() {
                            srv.persist(createEntity2(setId, "Compensation-1.0"));
                            return null;
                        }
                    });

                    for (int i = 0; i < maxNested; i++) {
                        final int nextedId = i;
                        new UnitOfWork(TransactionScopeOption.Nested).execute(new Executable<Void, RuntimeException>() {

                            @Override
                            public Void execute() {
                                srv.persist(createEntity(setId, "2." + nextedId));

                                UnitOfWork.addTransactionCompensationHandler(new CompensationHandler() {

                                    @Override
                                    public Void execute() {
                                        srv.persist(createEntity2(setId, "Compensation-2." + nextedId));
                                        return null;
                                    }
                                });

                                // Timeout error will happen here
                                if ((errorCondition == ErrorCondition.Nested) && (nextedId == maxNested - 1)) {
                                    try {
                                        Thread.sleep(Consts.MIN2MSEC * 1);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }

                                    srv.persist(createEntity(setId, "2.x"));
                                    Assert.fail("Should throw Exception in Nested transaction");
                                }
                                return null;
                            }
                        });
                    }

                    try {
                        Thread.sleep(Consts.MIN2MSEC * 1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    // Timeout error will happen here
                    if (errorCondition == ErrorCondition.MainFlow) {
                        srv.persist(createEntity(setId, "1.1"));

                        Assert.fail("Should throw Exception in MainFlow");
                    }

                    return null;
                }
            });
        } catch (RuntimeException connectionDoesNotExistOk) {
        }

        makeManyCommits(otherTransactionScopeOption);

        // Verify CompensationHandler fired
        assertExists2(setId, "Compensation-1.0");

        for (int i = 0; i < maxNested; i++) {
            assertNotExists(setId, "2." + i);
            assertExists2(setId, "Compensation-2." + i);
        }
        assertNotExists(setId, "1.0");
        assertNotExists(setId, "1.1");

    }

    // *******  Test helpers  **********

    private void makeManyCommits(final TransactionScopeOption transactionScopeOption) throws InterruptedException {
        // Open more then 5 connections (saturate connection pool) and commit them all
        List<Thread> treads = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Thread tread = new Thread() {
                @Override
                public void run() {

                    new UnitOfWork(transactionScopeOption).execute(new Executable<Void, RuntimeException>() {

                        @Override
                        public Void execute() throws RuntimeException {
                            createEntity2("a", "b");
                            try {
                                Thread.sleep(Consts.SEC2MSEC * 10);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                            return null;
                        }
                    });

                }
            };
            tread.start();
            treads.add(tread);
        }

        for (Thread tread : treads) {
            tread.join();
        }
    }

    private Simple1 createEntity(String setId, String id) {
        Simple1 emp = EntityFactory.create(Simple1.class);
        emp.testId().setValue(setId);
        emp.name().setValue(id);
        return emp;
    }

    private Simple2 createEntity2(String setId, String id) {
        Simple2 emp = EntityFactory.create(Simple2.class);
        emp.testId().setValue(setId);
        emp.name().setValue(id);
        return emp;
    }

    private void assertExists(String setId, String id) {
        EntityQueryCriteria<Simple1> criteria = EntityQueryCriteria.create(Simple1.class);
        criteria.eq(criteria.proto().testId(), setId);
        criteria.eq(criteria.proto().name(), id);
        List<Simple1> emps = srv.query(criteria);
        Assert.assertEquals(id + " NotExists, result set size", 1, emps.size());
    }

    private void assertExists2(String setId, String id) {
        EntityQueryCriteria<Simple2> criteria = EntityQueryCriteria.create(Simple2.class);
        criteria.eq(criteria.proto().testId(), setId);
        criteria.eq(criteria.proto().name(), id);
        List<Simple2> emps = srv.query(criteria);
        Assert.assertEquals(id + " NotExists, result set size", 1, emps.size());
    }

    private void assertNotExists(String setId, String id) {
        EntityQueryCriteria<Simple1> criteria = EntityQueryCriteria.create(Simple1.class);
        criteria.eq(criteria.proto().testId(), setId);
        criteria.eq(criteria.proto().name(), id);
        List<Simple1> emps = srv.query(criteria);
        Assert.assertEquals(id + " Exists, result set size", 0, emps.size());
    }
}
