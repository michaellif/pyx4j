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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.Trace;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.server.CompensationHandler;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

class TransactionContext {

    String savepointName;

    private Savepoint savepoint;

    private boolean uncommittedChanges = false;

    private String uncommittedChangesFrom;

    private List<CompensationHandler> compensationHandlers;

    private List<Executable<Void, RuntimeException>> completionHandlers;

    private List<CompensationHandler> commitedCompensationHandlers;

    TransactionContext(Connection connection, int id) {
        savepointName = "SP" + id;
        if (id != 0) {
            try {
                savepoint = connection.setSavepoint(savepointName);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            savepoint = null;
        }
    }

    void addTransactionCompensationHandler(CompensationHandler handler) {
        if (compensationHandlers == null) {
            compensationHandlers = new ArrayList<CompensationHandler>();
        }
        compensationHandlers.add(handler);
    }

    void addTransactionCompletionHandler(Executable<Void, RuntimeException> handler) {
        if (completionHandlers == null) {
            completionHandlers = new ArrayList<Executable<Void, RuntimeException>>();
        }
        completionHandlers.add(handler);
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
    }

    public String getUncommittedChangesFrom() {
        return uncommittedChangesFrom;
    }

    public void merge(TransactionContext tc) {
        if (commitedCompensationHandlers != null) {
            for (CompensationHandler handler : commitedCompensationHandlers) {
                tc.addTransactionCompensationHandler(handler);
            }
        }
        if (compensationHandlers != null) {
            for (CompensationHandler handler : compensationHandlers) {
                tc.addTransactionCompensationHandler(handler);
            }
        }
        if (completionHandlers != null) {
            for (Executable<Void, RuntimeException> handler : completionHandlers) {
                tc.addTransactionCompletionHandler(handler);
            }
        }
        if (this.uncommittedChanges) {
            tc.uncommittedChanges = this.uncommittedChanges;
            tc.uncommittedChangesFrom = this.uncommittedChangesFrom;
        }
    }

    void releaseSavepoint(Connection connection, Dialect dialect) {
        if (savepoint != null) {
            if (dialect.databaseType() != DatabaseType.Oracle) {
                try {
                    connection.releaseSavepoint(savepoint);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            savepoint = null;
        }
    }

    public void rollback(Connection connection) {
        if (savepoint != null) {
            try {
                connection.rollback(savepoint);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                savepoint = null;
            }
            try {
                savepointName += "r";
                savepoint = connection.setSavepoint(savepointName);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        uncommittedChanges = false;
    }

    void fireCompensationHandlers() {
        if (compensationHandlers != null) {

            ListIterator<CompensationHandler> li = compensationHandlers.listIterator(compensationHandlers.size());
            while (li.hasPrevious()) {
                CompensationHandler handler = li.previous();
                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(handler);
            }

            compensationHandlers.clear();
        }
    }

    void fireCompletionHandlers() {
        if (completionHandlers != null) {

            ListIterator<Executable<Void, RuntimeException>> li = completionHandlers.listIterator(completionHandlers.size());
            while (li.hasPrevious()) {
                Executable<Void, RuntimeException> handler = li.previous();
                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(handler);
            }

            completionHandlers.clear();
        }
    }

    public void commit(Connection connection, Dialect dialect) {
        // Move Savepoint
        if (savepoint != null) {
            if (dialect.databaseType() != DatabaseType.Oracle) {
                try {
                    connection.releaseSavepoint(savepoint);
                } catch (SQLException e) {
                    //throw new RuntimeException(e);
                } finally {
                    savepoint = null;
                }
            }
            try {
                savepointName += "c";
                savepoint = connection.setSavepoint(savepointName);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        uncommittedChanges = false;
        if (commitedCompensationHandlers == null) {
            commitedCompensationHandlers = compensationHandlers;
            compensationHandlers = null;
        } else if (compensationHandlers != null) {
            commitedCompensationHandlers.addAll(compensationHandlers);
            compensationHandlers.clear();
        }
    }

}
