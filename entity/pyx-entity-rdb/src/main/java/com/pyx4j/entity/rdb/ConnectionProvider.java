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
 * Created on 2010-07-07
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.cfg.Configuration;
import com.pyx4j.entity.rdb.cfg.Configuration.ConnectionPoolProvider;
import com.pyx4j.entity.rdb.cfg.ConnectionPoolType;
import com.pyx4j.entity.rdb.dialect.DerbyDialect;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.rdb.dialect.H2Dialect;
import com.pyx4j.entity.rdb.dialect.HSQLDialect;
import com.pyx4j.entity.rdb.dialect.MySQLDialect;
import com.pyx4j.entity.rdb.dialect.NamingConvention;
import com.pyx4j.entity.rdb.dialect.NamingConventionOracle;
import com.pyx4j.entity.rdb.dialect.OracleDialect;
import com.pyx4j.entity.rdb.dialect.PostgreSQLDialect;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.shared.DatastoreReadOnlyRuntimeException;

public class ConnectionProvider {

    private static final Logger log = LoggerFactory.getLogger(ConnectionProvider.class);

    private ConnectionPool connectionPool;

    private Dialect dialect;

    public static enum ConnectionReason {

        forDDL,

        forUpdate,

        forRead,

    }

    public ConnectionProvider(Configuration configuration) throws SQLException {
        setupDataSource(configuration);
    }

    private void setupDataSource(Configuration configuration) throws SQLException {
        synchronized (java.sql.DriverManager.class) {
            try {
                Class.forName(configuration.driverClass());
            } catch (ClassNotFoundException e) {
                throw new SQLException("JDBC driver " + configuration.driverClass() + " not found");
            }
        }

        NamingConvention namingConvention = configuration.namingConvention();
        if (namingConvention == null) {
            namingConvention = new NamingConventionOracle(64, null);
        }

        switch (configuration.databaseType()) {
        case HSQLDB:
            dialect = new HSQLDialect(namingConvention, configuration.getMultitenancyType(), configuration.sequencesBaseIdentity());
            break;
        case H2:
            dialect = new H2Dialect(namingConvention, configuration.getMultitenancyType(), configuration.sequencesBaseIdentity());
            break;
        case MySQL:
            dialect = new MySQLDialect(namingConvention, configuration.getMultitenancyType());
            break;
        case Oracle:
            dialect = new OracleDialect(namingConvention, configuration.getMultitenancyType());
            break;
        case PostgreSQL:
            dialect = new PostgreSQLDialect(namingConvention, configuration.getMultitenancyType());
            break;
        case Derby:
            dialect = new DerbyDialect(namingConvention, configuration.getMultitenancyType(), configuration.sequencesBaseIdentity());
            break;
        default:
            throw new Error("Unsupported driver Dialect " + configuration.driverClass());
        }

        // connection pool
        try {
            if (configuration.connectionPool() == ConnectionPoolProvider.dbcp) {
                connectionPool = new ConnectionPoolDBCP(configuration);
            } else if (configuration.connectionPool() == ConnectionPoolProvider.c3p0) {
                connectionPool = new ConnectionPoolC3P0(configuration, dialect);
            } else {
                throw new SQLException("Configuration does not specify proper connection pool " + configuration.connectionPool());
            }

            log.debug("Using connection pool {}", connectionPool);
        } catch (Exception e) {
            throw new SQLException("Failed to initialize connection pool: " + e.getMessage(), e);
        }

    }

    public void reconnect(Configuration configuration) throws SQLException {
        close();
        setupDataSource(configuration);
    }

    public void close() {
        try {
            connectionPool.close();
        } catch (Throwable e) {
            log.error("pool close error", e);
        } finally {
            connectionPool = null;
        }
    }

    public void dispose() {
        close();
    }

    public void deregister() {
        dispose();
        MemoryLeakReducer.deregisterDrivers();
    }

    public Dialect getDialect() {
        return dialect;
    }

    public Connection getConnection(ConnectionReason reason) {
        try {
            if ((reason == ConnectionReason.forUpdate) && ServerSideConfiguration.instance().datastoreReadOnly()) {
                throw new DatastoreReadOnlyRuntimeException(ServerSideConfiguration.instance().getApplicationMaintenanceMessage());
            }
            if (reason == ConnectionReason.forDDL) {
                return connectionPool.getDataSource(ConnectionPoolType.DDL).getConnection();
            } else {
                return connectionPool.getDataSource(ConnectionPoolType.Web).getConnection();
            }
        } catch (SQLException e) {
            log.error("SQL connection error", e);
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection(ConnectionTarget connectionTarget) {
        try {
            return connectionPool.getDataSource(ConnectionPoolType.translate(connectionTarget)).getConnection();
        } catch (SQLException e) {
            log.error("SQL connection error", e);
            throw new RuntimeException(e);
        }
    }

    public Connection getAdministrationConnection() {
        try {
            return connectionPool.getDataSource(ConnectionPoolType.DDL).getConnection();
        } catch (SQLException e) {
            log.error("SQL connection error", e);
            throw new RuntimeException(e);
        }
    }

    public void resetConnectionPool() {
        connectionPool.resetConnectionPool();
    }

}
