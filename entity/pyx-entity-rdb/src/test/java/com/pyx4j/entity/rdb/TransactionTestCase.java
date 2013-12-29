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
 * Created on 2013-02-22
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import java.rmi.server.ServerNotActiveException;
import java.util.List;

import junit.framework.Assert;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.CompensationHandler;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Simple1;
import com.pyx4j.entity.test.shared.domain.Simple2;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToOneInversedChild;
import com.pyx4j.entity.test.shared.domain.ownership.managed.BidirectionalOneToOneInversedParent;

public abstract class TransactionTestCase extends DatastoreTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Initialize table before tests for postgresql to work
        srv.count(EntityQueryCriteria.create(Simple1.class));
        srv.count(EntityQueryCriteria.create(Simple2.class));
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

    private void assertNotExists2(String setId, String id) {
        EntityQueryCriteria<Simple2> criteria = EntityQueryCriteria.create(Simple2.class);
        criteria.eq(criteria.proto().testId(), setId);
        criteria.eq(criteria.proto().name(), id);
        List<Simple2> emps = srv.query(criteria);
        Assert.assertEquals(id + " Exists, result set size", 0, emps.size());
    }

    private void assertTransactionNotPresent() {
        Assert.assertTrue("There should be no transaction context", srv.getTransactionScopeOption() == null);

    }

    public void testNestedTransactionStack() {
        final String setId = uniqueString();
        srv.persist(createEntity(setId, "0.0"));
        srv.endTransaction();

        assertTransactionNotPresent();
        srv.startTransaction(TransactionScopeOption.Suppress, ConnectionTarget.Web);
        {
            srv.persist(createEntity(setId, "1.0"));

            srv.startTransaction(TransactionScopeOption.RequiresNew, ConnectionTarget.Web);
            {
                // This will lock DB
                //srv.persist(createEntity(setId, "2.0"));
                srv.count(EntityQueryCriteria.create(Employee.class));

                srv.startTransaction(TransactionScopeOption.Suppress, ConnectionTarget.Web);
                {
                    srv.persist(createEntity(setId, "3.0"));
                }
                srv.endTransaction();

                srv.commit();
            }
            srv.endTransaction();

        }
        srv.endTransaction();
        assertTransactionNotPresent();
    }

    public void testNestedTransactionRollback() {
        String setId = uniqueString();
        Simple1 emp1 = createEntity(setId, "1.0");

        // Tx1
        srv.startTransaction(TransactionScopeOption.Nested, ConnectionTarget.Web);
        {
            srv.persist(emp1);

            srv.persist(createEntity(setId, "1.1"));

            // Tx2
            srv.startTransaction(TransactionScopeOption.Nested, ConnectionTarget.Web);
            {
                srv.persist(createEntity(setId, "2.0"));
                srv.commit();

                srv.persist(createEntity(setId, "2.1"));
                srv.rollback();

                srv.persist(createEntity(setId, "2.2"));
                srv.commit();
            }
            srv.endTransaction();

            srv.persist(createEntity(setId, "1.2"));
            srv.rollback();

            srv.persist(createEntity(setId, "1.3"));
            srv.commit();
        }
        srv.endTransaction();

        assertNotExists(setId, "1.0");
        assertNotExists(setId, "1.1");
        assertNotExists(setId, "2.0");
        assertNotExists(setId, "2.1");
        assertNotExists(setId, "2.2");
        assertNotExists(setId, "1.2");
        assertExists(setId, "1.3");
    }

    public void testNestedTransactionsFragmentL1() {
        String setId = uniqueString();
        Simple1 emp1 = createEntity(setId, "1.0");

        // Tx1
        srv.startTransaction(TransactionScopeOption.Nested, ConnectionTarget.Web);
        {
            srv.persist(emp1);
            srv.commit();

            srv.persist(createEntity(setId, "1.1"));
            srv.rollback();

            // Tx2
            srv.startTransaction(TransactionScopeOption.Nested, ConnectionTarget.Web);
            {
                srv.persist(createEntity(setId, "2.0"));
                srv.commit();

                srv.persist(createEntity(setId, "2.1"));
                srv.rollback();

                srv.persist(createEntity(setId, "2.2"));
                srv.commit();
            }
            srv.endTransaction();

            srv.persist(createEntity(setId, "1.2"));
            srv.commit();
        }
        srv.endTransaction();

        assertExists(setId, "1.0");
        assertNotExists(setId, "1.1");
        assertExists(setId, "2.0");
        assertNotExists(setId, "2.1");
        assertExists(setId, "2.2");
        assertExists(setId, "1.2");
    }

    public void testNestedTransactionsFragmentL2() {
        String setId = uniqueString();
        Simple1 emp1 = createEntity(setId, "1.0");

        // Tx1
        srv.startTransaction(TransactionScopeOption.Nested, ConnectionTarget.Web);
        {
            srv.persist(emp1);

            // Tx2
            srv.startTransaction(TransactionScopeOption.Nested, ConnectionTarget.Web);
            {
                srv.persist(createEntity(setId, "2.0"));
                srv.commit();

                srv.persist(createEntity(setId, "2.1"));
                srv.rollback();

                srv.persist(createEntity(setId, "2.2"));
                srv.commit();
            }
            srv.endTransaction();

            srv.persist(createEntity(setId, "1.2"));
            srv.commit();
        }
        srv.endTransaction();

        assertExists(setId, "1.0");
        assertExists(setId, "2.0");
        assertNotExists(setId, "2.1");
        assertExists(setId, "2.2");
        assertExists(setId, "1.2");
    }

    protected boolean isDerbyLockBug() {
        return false;
    }

    public void testSuppress() {
        final String setId = uniqueString();
        srv.endTransaction();

        srv.startBackgroundProcessTransaction();

        srv.persist(createEntity(setId, "1.1"));
        srv.commit();
        srv.persist(createEntity(setId, "1.2"));

        srv.startTransaction(TransactionScopeOption.Suppress, ConnectionTarget.Web);
        {
            srv.persist(createEntity2(setId, "2.0"));
            // TODO verify Lock in next version
            if (!isDerbyLockBug()) {
                assertNotExists(setId, "1.2");
            }
        }
        srv.endTransaction();

        srv.commit();
        srv.endTransaction();

        assertExists(setId, "1.1");
        assertExists(setId, "1.2");
        assertExists2(setId, "2.0");
    }

    public void testTransactionsCompensationHandler() {
        final String setId = uniqueString();
        srv.endTransaction();

        srv.startTransaction();

        srv.persist(createEntity(setId, "1.0"));

        srv.addTransactionCompensationHandler(new CompensationHandler() {
            @Override
            public Void execute() throws RuntimeException {
                srv.persist(createEntity(setId, "1.0CH"));
                return null;
            }
        });

        srv.rollback();

        srv.persist(createEntity(setId, "1.1"));

        srv.commit();

        srv.endTransaction();

        assertNotExists(setId, "1.0");
        assertExists(setId, "1.0CH");
        assertExists(setId, "1.1");
    }

    public void testTransactionsCompensationHandlerMultipeCommit() {
        final String setId = uniqueString();
        srv.endTransaction();

        srv.startTransaction();

        srv.persist(createEntity(setId, "1.0"));

        srv.addTransactionCompensationHandler(new CompensationHandler() {
            @Override
            public Void execute() throws RuntimeException {
                srv.persist(createEntity(setId, "1.0CH"));
                return null;
            }
        });

        srv.commit();
        srv.commit();
        srv.commit();

        srv.endTransaction();

        assertExists(setId, "1.0");
        assertNotExists(setId, "1.0CH");
    }

    public void testUnitOfWorkNestedTransactions() {
        final String setId = uniqueString();

        final Executable<Void, ServerNotActiveException> exec2 = new Executable<Void, ServerNotActiveException>() {

            @Override
            public Void execute() throws ServerNotActiveException {
                srv.persist(createEntity(setId, "2.0"));
                throw new ServerNotActiveException();
            }

        };

        Executable<Void, RuntimeException> exec1 = new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() throws RuntimeException {

                srv.persist(createEntity(setId, "1.0"));

                try {
                    new UnitOfWork().execute(exec2);
                    Assert.fail("Should throw exception");
                } catch (ServerNotActiveException ok) {
                }

                srv.persist(createEntity(setId, "1.1"));

                return null;
            }

        };

        new UnitOfWork().execute(exec1);

        assertExists(setId, "1.0");
        assertNotExists(setId, "2.0");
        assertExists(setId, "1.1");
    }

    public void testUnitOfWorkExternalTransactionIntegration() {
        srv.endTransaction();
        final String setId = uniqueString();

        srv.startTransaction(TransactionScopeOption.RequiresNew, ConnectionTarget.Web);

        srv.persist(createEntity(setId, "1.0"));

        new UnitOfWork().execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() {
                srv.persist(createEntity(setId, "2.0"));
                return null;
            }
        });

        srv.persist(createEntity(setId, "1.1"));

        assertExists(setId, "1.0");
        assertExists(setId, "2.0");
        assertExists(setId, "1.1");

    }

    public void testUnitOfWorkManangementCallOrigin() {
        final String setId = uniqueString();

        try {
            new UnitOfWork().execute(new Executable<Void, RuntimeException>() {

                @Override
                public Void execute() {
                    srv.persist(createEntity(setId, "1.0"));
                    // The call to commit should happen in the context of UnitOfWork
                    srv.commit();
                    return null;
                }

            });
            Assert.fail("call to commit() Should throw IllegalAccessError");
        } catch (IllegalAccessError ok) {
        }

        assertNotExists(setId, "1.0");

    }

    public void testUnitOfWorkCompensationHandlerL1() {
        final String setId = uniqueString();

        try {
            new UnitOfWork().execute(new Executable<Void, ServerNotActiveException>() {

                @Override
                public Void execute() throws ServerNotActiveException {
                    srv.persist(createEntity(setId, "1.0"));

                    UnitOfWork.addTransactionCompensationHandler(new CompensationHandler() {
                        @Override
                        public Void execute() throws RuntimeException {
                            srv.persist(createEntity(setId, "1.0CH"));
                            return null;
                        }
                    });

                    throw new ServerNotActiveException();
                }

            });
            Assert.fail("Should throw Exception");
        } catch (ServerNotActiveException ok) {
        }

        assertNotExists(setId, "1.0");
        assertExists(setId, "1.0CH");

    }

    public void testForeignKeysLock() {

        final String setId = uniqueString();

        final BidirectionalOneToOneInversedParent p1 = EntityFactory.create(BidirectionalOneToOneInversedParent.class);

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() {
                p1.testId().setValue(setId);
                srv.persist(p1);
                return null;
            }

        });

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() {
                BidirectionalOneToOneInversedChild c1 = EntityFactory.create(BidirectionalOneToOneInversedChild.class);
                c1.testId().setValue(setId);
                c1.parent().set(p1);
                srv.persist(c1);

                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                    @Override
                    public Void execute() {
                        p1.name().setValue("u1");
                        srv.persist(p1);
                        return null;
                    }

                });

                return null;
            }

        });
    }
}
