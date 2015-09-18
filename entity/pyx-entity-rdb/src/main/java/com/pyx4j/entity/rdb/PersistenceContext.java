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
 * Created on Feb 27, 2012
 * @author vlads
 */
package com.pyx4j.entity.rdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.config.server.Trace;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.server.CompensationHandler;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.IntegrityConstraintUserRuntimeException;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.i18n.shared.I18n;

public class PersistenceContext {

    private static final Logger log = LoggerFactory.getLogger(PersistenceContext.class);

    private static final I18n i18n = I18n.get(PersistenceContext.class);

    private static Boolean assertTransactionManangementCallOriginEnabled;

    private final PersistenceContext suppressedPersistenceContext;

    private final ConnectionProvider connectionProvider;

    private final ConnectionTarget connectionTarget;

    private final TransactionType transactionType;

    private final String contextOpenFrom;

    private Connection connection = null;

    private Date timeNow;

    private Key currentUserKey;

    int savepoints = 0;

    private long transactionStart = -1;

    private long transactionUncommittedChangesStart = -1;

    private final Stack<TransactionContext> transactionContexts = new Stack<TransactionContext>();

    private static Object openSessionLock = new Object();

    private static long openSessionCount = 0;

    private static Set<PersistenceContext> openSessions = new HashSet<PersistenceContext>();

    private boolean isEnded = false;

    public static enum TransactionType {

        SingelAPICallAutoCommit,

        JDBCPersistence,

        Transaction,

        AutoCommit
    }

    private static int txCount = 0;

    private static class TransactionContextOptions {

        private boolean enableSavepointAsNestedTransactions = false;

        private String assertTransactionManangementCallOrigin;

        private String transactionManangementCallOriginSetFrom;

        private final int txId = txCount++;

        TransactionContextOptions() {
        }

        TransactionContextOptions(TransactionContextOptions options) {
            this.enableSavepointAsNestedTransactions = options.enableSavepointAsNestedTransactions;
            this.assertTransactionManangementCallOrigin = options.assertTransactionManangementCallOrigin;
            this.transactionManangementCallOriginSetFrom = options.transactionManangementCallOriginSetFrom;
        }

    }

    private final Stack<TransactionContextOptions> options = new Stack<TransactionContextOptions>();

    PersistenceContext(PersistenceContext suppressedPersistenceContext, ConnectionProvider connectionProvider, TransactionType transactionType,
            ConnectionTarget connectionTarget) {
        this.suppressedPersistenceContext = suppressedPersistenceContext;
        this.connectionProvider = connectionProvider;
        this.connectionTarget = connectionTarget;
        this.transactionType = transactionType;
        if (PersistenceTrace.traceOpenSession) {
            this.contextOpenFrom = Trace.getStackTrace(new Throwable());
        } else if (ServerSideConfiguration.isStartedUnderJvmDebugMode()) {
            this.contextOpenFrom = PersistenceTrace.getCallOrigin();
        } else {
            this.contextOpenFrom = "n/a";
        }
        transactionContexts.push(new TransactionContext(null, savepoints));
    }

    void startTransaction() {
        if (options.isEmpty()) {
            options.push(new TransactionContextOptions());
        } else {
            options.push(new TransactionContextOptions(options()));
        }
        if (isTransaction()) {
            options().enableSavepointAsNestedTransactions = true;
        }
    }

    boolean endTransaction() {
        if (isEnded) {
            throw new Error("Transaction already ended");
        }
        try {
            if (savepointActive()) {
                savepointRelease();
                return false;
            } else {
                isEnded = true;
                return true;
            }
        } finally {
            options.pop();
        }
    }

    void endCallContext() {
        timeNow = null;
    }

    TransactionContextOptions options() {
        return options.peek();
    }

    public String txId() {
        try {
            return "TX" + options().txId;
        } catch (EmptyStackException e) {
            return "NoTx";
        }
    }

    PersistenceContext getSuppressedPersistenceContext() {
        return suppressedPersistenceContext;
    }

    String getContextOpenFrom() {
        return contextOpenFrom;
    }

    public boolean isSingelAPICallTransaction() {
        return transactionType == TransactionType.SingelAPICallAutoCommit;
    }

    public boolean isTransaction() {
        return transactionType == TransactionType.Transaction;
    }

    public boolean isExplicitTransaction() {
        return transactionType == TransactionType.Transaction || transactionType == TransactionType.JDBCPersistence;
    }

    public ConnectionTarget getConnectionTarget() {
        return connectionTarget;
    }

    public Date getTimeNow() {
        if (timeNow == null) {
            timeNow = DateUtils.getDBRounded(SystemDateManager.getDate());
        }
        return timeNow;
    }

    public Key getCurrentUserKey() {
        return currentUserKey;
    }

    public void setCurrentUserKey(Key currentUserKey) {
        this.currentUserKey = currentUserKey;
    }

    public Connection getConnection() {
        if (connection == null) {
            connection = connectionProvider.getConnection(getConnectionTarget());
            transactionUncommittedChangesStart = -1;
            transactionStart = System.currentTimeMillis();
            if (isExplicitTransaction()) {
                try {
                    connection.setAutoCommit(false);
                    connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                } catch (SQLException e) {
                    SQLUtils.closeQuietly(connection);
                    connection = null;
                    throw new RuntimeException(e);
                }
            }
            if (PersistenceTrace.traceOpenSession) {
                log.info("*** connection open  {} {} {}", Integer.toHexString(System.identityHashCode(this)), transactionType, getConnectionTarget());
                synchronized (openSessionLock) {
                    openSessionCount++;
                    openSessions.add(this);
                }
            }
        }
        return connection;
    }

    public int getConnectionTimeout() {
        return connectionProvider.getConnectionTimeout(getConnectionTarget());
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        stmt.setQueryTimeout(getConnectionTimeout());
        return stmt;
    }

    private boolean assertTransactionManangementCallOriginEnabled() {
        if (assertTransactionManangementCallOriginEnabled == null) {
            if (Boolean.valueOf(System.getProperty("com.pyx4j.performanceTestEnviroment"))) {
                assertTransactionManangementCallOriginEnabled = false;
            } else {
                assertTransactionManangementCallOriginEnabled = ServerSideConfiguration.isStartedUnderUnitTest()
                        || ServerSideConfiguration.isStartedUnderJvmDebugMode() || ServerSideConfiguration.isStartedUnderEclipse();
            }
        }
        return assertTransactionManangementCallOriginEnabled;
    }

    public void setAssertTransactionManangementCallOrigin() {
        if (assertTransactionManangementCallOriginEnabled()) {
            assertTransactionManangementCallOrigin();
            options().assertTransactionManangementCallOrigin = Trace.getCallOriginMethod(EntityPersistenceServiceRDB.class);
            if (options().assertTransactionManangementCallOrigin.startsWith(UnitOfWork.class.getName())) {
                options().transactionManangementCallOriginSetFrom = Trace.getCallOrigin(UnitOfWork.class);
            }
            // Ignore call origin when called from ProxyInstance
            if (options().assertTransactionManangementCallOrigin.startsWith("sun.reflect.")) {
                options().assertTransactionManangementCallOrigin = null;
            }
        }
    }

    private void assertTransactionManangementCallOrigin() {
        if ((options().assertTransactionManangementCallOrigin != null)
                && (!options().assertTransactionManangementCallOrigin.equals(Trace.getCallOriginMethod(EntityPersistenceServiceRDB.class)))) {
            log.error("CallOrigin {} != {}", Trace.getCallOriginMethod(EntityPersistenceServiceRDB.class), options().assertTransactionManangementCallOrigin);
            throw new IllegalAccessError("Transaction Management of this thread can only performed from " + options().assertTransactionManangementCallOrigin //
                    + ((options().transactionManangementCallOriginSetFrom != null)
                            ? (", created from \n" + options().transactionManangementCallOriginSetFrom + "\n") : ""));
        }
    }

    public boolean isUncommittedChanges() {
        for (TransactionContext tc : transactionContexts) {
            if (tc.isUncommittedChanges()) {
                return true;
            }
        }
        return false;
    }

    private String getUncommittedChangesFrom() {
        ListIterator<TransactionContext> li = transactionContexts.listIterator(transactionContexts.size());
        while (li.hasPrevious()) {
            TransactionContext tc = li.previous();
            if (tc.isUncommittedChanges()) {
                return tc.getUncommittedChangesFrom();
            }
        }
        return null;
    }

    public void setUncommittedChanges() {
        if (this.transactionUncommittedChangesStart <= 0) {
            this.transactionUncommittedChangesStart = System.currentTimeMillis();
        }
        transactionContexts.peek().setUncommittedChanges();
    }

    public Dialect getDialect() {
        return connectionProvider.getDialect();
    }

    void release() {
        if (!isExplicitTransaction()) {
            close();
        }
    }

    void starNestedContext(boolean enableSavepointAsNestedTransactions) {
        assertTransactionManangementCallOrigin();
        savepoints++;
        if (isExplicitTransaction()) {
            options().enableSavepointAsNestedTransactions = enableSavepointAsNestedTransactions;

            if (options().enableSavepointAsNestedTransactions) {
                transactionContexts.push(new TransactionContext(getConnection(), savepoints));
            }
        }
    }

    private void savepointRelease() {
        assert(savepoints > 0) : " Inconsistent Transaction end";
        savepoints--;
        if (options().enableSavepointAsNestedTransactions) {
            TransactionContext tc = transactionContexts.pop();
            tc.merge(transactionContexts.peek());
            if (PersistenceTrace.traceTransaction) {
                log.info("{} releaseSavepoint {}", txId(), tc.savepointName);
            }
            tc.releaseSavepoint(connection, getDialect());
        }
    }

    public boolean savepointActive() {
        return (savepoints > 0);
    }

    private boolean isDirectTransactionControl() {
        return !options().enableSavepointAsNestedTransactions || !savepointActive();
    }

    void addTransactionCompensationHandler(CompensationHandler handler) {
        transactionContexts.peek().addTransactionCompensationHandler(handler);
        if (PersistenceTrace.traceTransaction) {
            log.info("{} add CompensationHandler {}", txId(), handler.getClass().getName());
        }
    }

    void addTransactionCompletionHandler(Executable<Void, RuntimeException> handler) {
        transactionContexts.peek().addTransactionCompletionHandler(handler);
        if (PersistenceTrace.traceTransaction) {
            log.info("{} add CompletionHandler {}", txId(), handler.getClass().getName());
        }
    }

    void commit() {
        if (PersistenceTrace.traceTransaction) {
            log.info("{} commit\n\tfrom:{}\t", txId(), PersistenceTrace.getCallOrigin());
        }
        assertTransactionManangementCallOrigin();
        transactionContexts.peek().commit(connection, getDialect());
        if (isDirectTransactionControl()) {
            if (connection != null) {
                boolean ok = false;
                try {
                    connection.commit();
                    transactionStart = -1;
                    transactionUncommittedChangesStart = -1;
                    ok = true;
                } catch (SQLException e) {
                    if (connectionProvider.getDialect().isIntegrityConstraintException(e)) {
                        throw new IntegrityConstraintUserRuntimeException(i18n.tr("Unable to update/delete record referenced by another records."), null);
                    } else {
                        throw new RuntimeException(e);
                    }
                } finally {
                    if (!ok) {
                        log.error("commit failed");
                    }
                }
            }
            transactionContexts.peek().fireCompletionHandlers();
        }
        transactionContexts.peek().commitWasSuccessful();
        timeNow = null;
    }

    void rollback() {
        if (PersistenceTrace.traceTransaction) {
            log.info("{} rollback\n\tfrom:{}\t", txId(), PersistenceTrace.getCallOrigin());
        }
        assertTransactionManangementCallOrigin();
        try {
            transactionContexts.peek().rollback(connection);
            if (isDirectTransactionControl() && connection != null) {
                boolean ok = false;
                try {
                    log.warn("rollback transaction since {}; changes since {}", //
                            PersistenceTrace.traceTime(transactionStart), PersistenceTrace.traceTime(transactionUncommittedChangesStart));
                    connection.rollback();
                    transactionStart = -1;
                    transactionUncommittedChangesStart = -1;
                    ok = true;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (!ok) {
                        log.error("rollback failed");
                    }
                }
            }
        } finally {
            transactionContexts.peek().fireCompensationHandlers();
        }
    }

    void close() {
        if (connection != null) {
            boolean uncommittedChanges = isUncommittedChanges();
            if (isExplicitTransaction() && uncommittedChanges) {
                log.error("There are uncommitted changes in Database. {}", getUncommittedChangesFrom());
            }
            SQLUtils.closeQuietly(connection);
            if (PersistenceTrace.traceOpenSession) {
                log.info("*** connection close {} {}", Integer.toHexString(System.identityHashCode(this)), transactionType);
                synchronized (openSessionLock) {
                    openSessionCount--;
                    openSessions.remove(this);
                }
            }
            connection = null;
            if (isExplicitTransaction() && uncommittedChanges) {
                throw new AssertionError("There are uncommitted changes in Database");
            }
        }
        timeNow = null;
    }

    public void terminate() {
        if (connection != null) {
            SQLUtils.closeQuietly(connection);
            if (PersistenceTrace.traceOpenSession) {
                log.info("*** connection close {} {}", Integer.toHexString(System.identityHashCode(this)), transactionType);
                synchronized (openSessionLock) {
                    openSessionCount--;
                    openSessions.remove(this);
                }
            }
            connection = null;
        }
        if (!isEnded) {
            if (PersistenceTrace.traceTransaction) {
                log.info("{} terminate", txId());
            }
            throw new Error("Transaction was not ended");
        }
        timeNow = null;
    }

    public static void debugOpenSessions() {
        if (PersistenceTrace.traceOpenSession) {
            log.info("*** OpenSessions {}", openSessionCount);
            for (PersistenceContext persistenceContext : openSessions) {
                log.info("*** {} {}, context open from {}", new Object[] { Integer.toHexString(System.identityHashCode(persistenceContext)),
                        persistenceContext.transactionType, persistenceContext.getContextOpenFrom() });
            }
        } else {
            log.info("*** traceOpenSession compiled out");
        }
    }

    @Override
    public String toString() {
        return transactionType + " " + txId() + " " + super.toString();
    }

}
