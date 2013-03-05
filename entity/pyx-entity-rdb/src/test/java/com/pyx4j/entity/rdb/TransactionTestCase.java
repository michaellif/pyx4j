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
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import com.pyx4j.entity.server.CompensationHandler;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.Employee;

public abstract class TransactionTestCase extends DatastoreTestBase {

    private Employee createEntity(String setId, String id) {
        Employee emp = EntityFactory.create(Employee.class);
        emp.workAddress().streetName().setValue(setId);
        emp.firstName().setValue(id);
        return emp;
    }

    private void assertExists(String setId, String id) {
        EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
        criteria.eq(criteria.proto().workAddress().streetName(), setId);
        criteria.eq(criteria.proto().firstName(), id);
        List<Employee> emps = srv.query(criteria);
        Assert.assertEquals(id + " NotExists, result set size", 1, emps.size());
    }

    private void assertNotExists(String setId, String id) {
        EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
        criteria.eq(criteria.proto().workAddress().streetName(), setId);
        criteria.eq(criteria.proto().firstName(), id);
        List<Employee> emps = srv.query(criteria);
        Assert.assertEquals(id + " Exists, result set size", 0, emps.size());
    }

    private void assertTransactionNotPresent() {
        try {
            srv.getTransactionSystemTime();
        } catch (Throwable ok) {
            return;
        }
        Assert.fail("Should throw exception since there should be no transaction context");

    }

    public void testNestedTransactionStack() {
        final String setId = uniqueString();
        srv.persist(createEntity(setId, "0.0"));
        srv.endTransaction();

        assertTransactionNotPresent();
        srv.startTransaction(TransactionScopeOption.Suppress, false);
        {
            srv.persist(createEntity(setId, "1.0"));

            srv.startTransaction(TransactionScopeOption.RequiresNew, false);
            {
                // This will lock DB
                //srv.persist(createEntity(setId, "2.0"));
                srv.count(EntityQueryCriteria.create(Employee.class));

                srv.startTransaction(TransactionScopeOption.Suppress, false);
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
        Employee emp1 = createEntity(setId, "1.0");

        // Tx1
        srv.startTransaction();
        srv.enableSavepointAsNestedTransactions();
        {
            srv.persist(emp1);

            srv.persist(createEntity(setId, "1.1"));

            // Tx2
            srv.startTransaction();
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
        Employee emp1 = createEntity(setId, "1.0");

        // Tx1
        srv.startTransaction();
        srv.enableSavepointAsNestedTransactions();
        {
            srv.persist(emp1);
            srv.commit();

            srv.persist(createEntity(setId, "1.1"));
            srv.rollback();

            // Tx2
            srv.startTransaction();
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
        Employee emp1 = createEntity(setId, "1.0");

        // Tx1
        srv.startTransaction();
        srv.enableSavepointAsNestedTransactions();
        {
            srv.persist(emp1);

            // Tx2
            srv.startTransaction();
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

    public void testUnitOfWorkCompensationHandlerL2_NoErrors1() throws ServerNotActiveException {
        final String setId = uniqueString();
        final List<String> compensationHandlerOrder = new ArrayList<String>();

        executeUnitOfWork(setId, compensationHandlerOrder, TransactionScopeOption.Required, TransactionScopeOption.Required, TransactionScopeOption.Required,
                "");

        assertExists(setId, "1.0");
        assertNotExists(setId, "1.0CH");
        assertExists(setId, "1.1");
        assertNotExists(setId, "1.1CH");
        assertExists(setId, "1.2");
        assertNotExists(setId, "1.2CH");
        assertExists(setId, "2.0");
        assertNotExists(setId, "2.0CH");
        assertExists(setId, "3.0");
        assertNotExists(setId, "3.0CH");

        assertEquals(0, compensationHandlerOrder.size());
    }

    public void _testUnitOfWorkCompensationHandlerL2_NoErrors2() throws ServerNotActiveException {
        final String setId = uniqueString();
        final List<String> compensationHandlerOrder = new ArrayList<String>();

        executeUnitOfWork(setId, compensationHandlerOrder, TransactionScopeOption.Required, TransactionScopeOption.Required,
                TransactionScopeOption.RequiresNew, "");

        assertExists(setId, "1.0");
        assertNotExists(setId, "1.0CH");
        assertExists(setId, "1.1");
        assertNotExists(setId, "1.1CH");
        assertExists(setId, "1.2");
        assertNotExists(setId, "1.2CH");
        assertExists(setId, "2.0");
        assertNotExists(setId, "2.0CH");
        assertExists(setId, "3.0");
        assertNotExists(setId, "3.0CH");

        assertEquals(0, compensationHandlerOrder.size());
    }

    public void testUnitOfWorkCompensationHandlerL2_1() {
        final String setId = uniqueString();
        final List<String> compensationHandlerOrder = new ArrayList<String>();

        try {
            executeUnitOfWork(setId, compensationHandlerOrder, TransactionScopeOption.Required, TransactionScopeOption.Required,
                    TransactionScopeOption.Required, "1.g");
            Assert.fail("Should throw Exception");
        } catch (ServerNotActiveException ok) {
        }

        assertNotExists(setId, "1.0");
        assertExists(setId, "1.0CH");
        assertNotExists(setId, "1.1");
        assertExists(setId, "1.1CH");
        assertNotExists(setId, "1.2");
        assertExists(setId, "1.2CH");
        assertNotExists(setId, "2.0");
        assertExists(setId, "2.0CH");
        assertNotExists(setId, "3.0");

        assertEquals(3, compensationHandlerOrder.indexOf("1.0CH"));
        assertEquals(2, compensationHandlerOrder.indexOf("2.0CH"));
        assertEquals(1, compensationHandlerOrder.indexOf("1.1CH"));
        assertEquals(0, compensationHandlerOrder.indexOf("1.2CH"));
    }

    public void _testUnitOfWorkCompensationHandlerL2_2() {
        final String setId = uniqueString();
        final List<String> compensationHandlerOrder = new ArrayList<String>();

        try {
            executeUnitOfWork(setId, compensationHandlerOrder, TransactionScopeOption.Required, TransactionScopeOption.Required,
                    TransactionScopeOption.RequiresNew, "1.g");
            Assert.fail("Should throw Exception");
        } catch (ServerNotActiveException ok) {
        }

    }

    public void testUnitOfWorkCompensationHandlerL2_3() {
        final String setId = uniqueString();
        final List<String> compensationHandlerOrder = new ArrayList<String>();

        try {
            executeUnitOfWork(setId, compensationHandlerOrder, TransactionScopeOption.Required, TransactionScopeOption.Required,
                    TransactionScopeOption.Required, "2.a", "1.c");
            Assert.fail("Should throw Exception");
        } catch (ServerNotActiveException ok) {
        }

        assertNotExists(setId, "1.0");
        assertExists(setId, "1.0CH");
        assertNotExists(setId, "1.1");
        assertNotExists(setId, "1.1CH");
        assertNotExists(setId, "1.2");
        assertNotExists(setId, "1.2CH");
        assertNotExists(setId, "2.0");
        assertNotExists(setId, "2.0CH");
        assertNotExists(setId, "3.0");
        assertNotExists(setId, "3.0CH");

        assertEquals(0, compensationHandlerOrder.indexOf("1.0CH"));
    }

    public void _testUnitOfWorkCompensationHandlerL2_4() {
        final String setId = uniqueString();
        final List<String> compensationHandlerOrder = new ArrayList<String>();

        try {
            executeUnitOfWork(setId, compensationHandlerOrder, TransactionScopeOption.Required, TransactionScopeOption.Required,
                    TransactionScopeOption.Required, "2.b");
            Assert.fail("Should throw Exception");
        } catch (ServerNotActiveException ok) {
        }

    }

    public void _testUnitOfWorkCompensationHandlerL2_5() {
        final String setId = uniqueString();
        final List<String> compensationHandlerOrder = new ArrayList<String>();

        try {
            executeUnitOfWork(setId, compensationHandlerOrder, TransactionScopeOption.Required, TransactionScopeOption.Required,
                    TransactionScopeOption.Required, "2.b");
            Assert.fail("Should throw Exception");
        } catch (ServerNotActiveException ok) {
        }

    }

    //**************************** Tester **************************//

    private void executeUnitOfWork(final String setId, final List<String> compensationHandlerOrder, TransactionScopeOption extTransactionScopeOption,
            final TransactionScopeOption int1TransactionScopeOption, final TransactionScopeOption int2TransactionScopeOption, final String... exceptionPoints)
            throws ServerNotActiveException {

        final Executable<Void, ServerNotActiveException> exec2 = new Executable<Void, ServerNotActiveException>() {

            @Override
            public Void execute() throws ServerNotActiveException {
                throwException("2.a", exceptionPoints);
                srv.persist(createEntity(setId, "2.0"));
                addTransactionCompensationHandler(setId, "2.0CH", compensationHandlerOrder);
                throwException("2.b", exceptionPoints);
                return null;
            }

        };

        final Executable<Void, ServerNotActiveException> exec3 = new Executable<Void, ServerNotActiveException>() {

            @Override
            public Void execute() throws ServerNotActiveException {
                throwException("3.a", exceptionPoints);
                srv.persist(createEntity(setId, "3.0"));
                throwException("3.b", exceptionPoints);
                return null;
            }

        };

        Executable<Void, ServerNotActiveException> exec1 = new Executable<Void, ServerNotActiveException>() {

            @Override
            public Void execute() throws ServerNotActiveException {
                throwException("1.a", exceptionPoints);
                {
                    srv.persist(createEntity(setId, "1.0"));
                    addTransactionCompensationHandler(setId, "1.0CH", compensationHandlerOrder);
                }

                throwException("1.b", exceptionPoints);

                try {
                    new UnitOfWork(int1TransactionScopeOption).execute(exec2);
                } catch (Exception e) {
                    throwException("1.c", exceptionPoints);
                }

                throwException("1.d", exceptionPoints);

                {
                    srv.persist(createEntity(setId, "1.1"));
                    addTransactionCompensationHandler(setId, "1.1CH", compensationHandlerOrder);
                }

                throwException("1.e", exceptionPoints);

                try {
                    new UnitOfWork(int2TransactionScopeOption).execute(exec3);
                } catch (Exception e) {
                    throwException("1.f", exceptionPoints);
                }

                throwException("1.f", exceptionPoints);

                {
                    srv.persist(createEntity(setId, "1.2"));
                    addTransactionCompensationHandler(setId, "1.2CH", compensationHandlerOrder);
                }

                throwException("1.g", exceptionPoints);

                return null;
            }

        };

        new UnitOfWork(extTransactionScopeOption).execute(exec1);

    }

    private void throwException(String id, String... exceptionPoints) throws ServerNotActiveException {
        for (String exceptionPoint : exceptionPoints) {
            if (id.equals(exceptionPoint)) {
                throw new ServerNotActiveException();
            }
        }

    }

    private void addTransactionCompensationHandler(final String setId, final String compensatorId, final List<String> compensationHandlerOrder) {
        UnitOfWork.addTransactionCompensationHandler(new CompensationHandler() {
            @Override
            public Void execute() throws RuntimeException {
                srv.persist(createEntity(setId, compensatorId));
                compensationHandlerOrder.add(compensatorId);
                return null;
            }
        });
    }
}
