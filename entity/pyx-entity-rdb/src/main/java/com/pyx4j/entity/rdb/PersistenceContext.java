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
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.gwt.server.DateUtils;

public class PersistenceContext {

    private static final Logger log = LoggerFactory.getLogger(PersistenceContext.class);

    private final ConnectionProvider connectionProvider;

    private final boolean explicitTransaction;

    private Connection connection = null;

    private boolean uncommittedChanges;

    private Date timeNow;

    private int savepoints = 0;

    PersistenceContext(ConnectionProvider connectionProvider, boolean explicitTransaction) {
        this.connectionProvider = connectionProvider;
        this.explicitTransaction = explicitTransaction;
    }

    public boolean isExplicitTransaction() {
        return explicitTransaction;
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

    public Connection getConnection() {
        if (connection == null) {
            connection = connectionProvider.getConnection();
            if (explicitTransaction) {
                try {
                    connection.setAutoCommit(false);
                } catch (SQLException e) {
                    SQLUtils.closeQuietly(connection);
                    connection = null;
                    throw new RuntimeException(e);
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
    }

    public Dialect getDialect() {
        return connectionProvider.getDialect();
    }

    void release() {
        if (!explicitTransaction) {
            close();
        }
    }

    void savepointCreate() {
        savepoints++;
        //TODO
    }

    void savepointRelease() {
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
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    void rollback() {
        if (connection != null) {
            try {
                connection.rollback();
                uncommittedChanges = false;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    void close() {
        if (connection != null) {
            if (explicitTransaction && uncommittedChanges) {
                log.error("There are uncommitted changes in Database");
            }
            SQLUtils.closeQuietly(connection);
            connection = null;
            if (explicitTransaction && uncommittedChanges) {
                throw new Error("There are uncommitted changes in Database");
            }
        }
    }

    public void terminate() {
        if (connection != null) {
            SQLUtils.closeQuietly(connection);
            connection = null;
        }
    }

}
