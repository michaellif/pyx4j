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
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.Trace;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.gwt.server.DateUtils;

public class PersistenceContext {

    private static final Logger log = LoggerFactory.getLogger(PersistenceContext.class);

    private final ConnectionProvider connectionProvider;

    private final TransactionType transactionType;

    private final String contextOpenFrom;

    private Connection connection = null;

    private String connectionNamespace;

    private boolean uncommittedChanges;

    private String uncommittedChangesFrom;

    private Date timeNow;

    private Key currentUserKey;

    private int savepoints = 0;

    private long transactionStart = -1;

    public static final boolean traceOpenSession = false;

    private static Object openSessionLock = new Object();

    private static long openSessionCount = 0;

    private static Set<PersistenceContext> openSessions = new HashSet<PersistenceContext>();

    public static enum TransactionType {

        BackgroundProcess,

        ExplicitTransaction,

        AutoCommit
    }

    PersistenceContext(ConnectionProvider connectionProvider, TransactionType transactionType) {
        this.connectionProvider = connectionProvider;
        this.transactionType = transactionType;
        if (traceOpenSession) {
            this.contextOpenFrom = Trace.getStackTrace(new Throwable());
        } else if (ServerSideConfiguration.isStartedUnderJvmDebugMode()) {
            this.contextOpenFrom = Trace.getCallOrigin(EntityPersistenceServiceRDB.class);
        } else {
            this.contextOpenFrom = "n/a";
        }
    }

    String getContextOpenFrom() {
        return contextOpenFrom;
    }

    public boolean isExplicitTransaction() {
        return transactionType != TransactionType.AutoCommit;
    }

    public boolean isBackgroundProcessTransaction() {
        return transactionType == TransactionType.BackgroundProcess;
    }

    void setTimeNow(Date date) {
        timeNow = date;
    }

    public Date getTimeNow() {
        if (timeNow == null) {
            timeNow = DateUtils.getRoundedNow();
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
            if (transactionType == TransactionType.BackgroundProcess) {
                connection = connectionProvider.getBackgroundProcessConnection();
            } else {
                connection = connectionProvider.getConnection();
            }
            uncommittedChanges = false;
            transactionStart = -1;
            if (isExplicitTransaction()) {
                try {
                    connection.setAutoCommit(false);
                } catch (SQLException e) {
                    SQLUtils.closeQuietly(connection);
                    connection = null;
                    throw new RuntimeException(e);
                }
            }

            if (traceOpenSession) {
                log.info("*** connection open  {} {}", Integer.toHexString(System.identityHashCode(this)), transactionType);
                synchronized (openSessionLock) {
                    openSessionCount++;
                    openSessions.add(this);
                }
            }
        }
        return connection;
    }

    public boolean isUncommittedChanges() {
        return this.uncommittedChanges;
    }

    public void setUncommittedChanges() {
        this.uncommittedChanges = true;
        if (ServerSideConfiguration.isStartedUnderJvmDebugMode()) {
            this.uncommittedChangesFrom = Trace.getCallOrigin(EntityPersistenceServiceRDB.class);
        } else {
            this.uncommittedChangesFrom = "n/a";
        }
        this.transactionStart = System.currentTimeMillis();
    }

    public Dialect getDialect() {
        return connectionProvider.getDialect();
    }

    void release() {
        if (!isExplicitTransaction()) {
            close();
        }
    }

    void savepointCreate() {
        savepoints++;
        //TODO
    }

    void savepointRelease() {
        assert (savepoints > 0) : " Inconsistent Transaction end";
        savepoints--;
        // TODO
    }

    public boolean savepointActive() {
        return (savepoints > 0);
    }

    void commit() {
        if (connection != null) {
            try {
                connection.commit();
                uncommittedChanges = false;
                transactionStart = -1;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    void rollback() {
        if (connection != null) {
            try {
                log.warn("rollback transaction changes since {}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date(transactionStart)));
                connection.rollback();
                uncommittedChanges = false;
                transactionStart = -1;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    void close() {
        if (connection != null) {
            if (isExplicitTransaction() && uncommittedChanges) {
                log.error("There are uncommitted changes in Database. {}", uncommittedChangesFrom);
            }
            SQLUtils.closeQuietly(connection);
            if (traceOpenSession) {
                log.info("*** connection close {} {}", Integer.toHexString(System.identityHashCode(this)), transactionType);
                synchronized (openSessionLock) {
                    openSessionCount--;
                    openSessions.remove(this);
                }
            }
            connection = null;
            if (isExplicitTransaction() && uncommittedChanges) {
                throw new Error("There are uncommitted changes in Database");
            }
        }
    }

    public void terminate() {
        if (connection != null) {
            SQLUtils.closeQuietly(connection);
            if (traceOpenSession) {
                log.info("*** connection close {} {}", Integer.toHexString(System.identityHashCode(this)), transactionType);
                synchronized (openSessionLock) {
                    openSessionCount--;
                    openSessions.remove(this);
                }
            }
            connection = null;
        }
    }

    public static void debugOpenSessions() {
        if (traceOpenSession) {
            log.info("*** OpenSessions {}", openSessionCount);
            for (PersistenceContext persistenceContext : openSessions) {
                log.info("*** {} {}, context open from {}", new Object[] { Integer.toHexString(System.identityHashCode(persistenceContext)),
                        persistenceContext.transactionType, persistenceContext.getContextOpenFrom() });
            }
        } else {
            log.info("*** traceOpenSession compiled out");
        }
    }

}
