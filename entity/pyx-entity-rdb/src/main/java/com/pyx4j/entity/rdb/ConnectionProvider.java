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

import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.cfg.Configuration;
import com.pyx4j.entity.rdb.cfg.Configuration.ConnectionPoolProvider;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.rdb.dialect.HSQLDialect;
import com.pyx4j.entity.rdb.dialect.MySQLDialect;
import com.pyx4j.entity.rdb.dialect.NamingConvention;
import com.pyx4j.entity.rdb.dialect.NamingConventionOracle;
import com.pyx4j.entity.rdb.dialect.OracleDialect;
import com.pyx4j.rpc.shared.UserRuntimeException;

public class ConnectionProvider {

    private static final Logger log = LoggerFactory.getLogger(ConnectionProvider.class);

    private ConnectionPool connectionPool;

    private DataSource dataSource;

    private Dialect dialect;

    public static enum ConnectionTarget {

        forUpdate,

        forRead,

    }

    public ConnectionProvider(Configuration cfg) throws SQLException {
        setupDataSource(cfg);
    }

    private void setupDataSource(Configuration cfg) throws SQLException {
        synchronized (java.sql.DriverManager.class) {
            try {
                Class.forName(cfg.driverClass());
            } catch (ClassNotFoundException e) {
                throw new SQLException("JDBC driver " + cfg.driverClass() + " not found");
            }
        }

        // connection pool
        try {
            if (cfg.connectionPool() == ConnectionPoolProvider.dbcp) {
                connectionPool = new ConnectionPoolDBCP(cfg);
            } else if (cfg.connectionPool() == ConnectionPoolProvider.c3p0) {
                connectionPool = new ConnectionPoolC3P0(cfg);
            } else {
                throw new SQLException("Configuration does not specify proper connection pool " + cfg.connectionPool());
            }

            log.debug("Using connection pool {}", connectionPool);

            dataSource = connectionPool.getDataSource();
        } catch (Exception e) {
            throw new SQLException("Failed to initialize connection pool: " + e.getMessage());
        }

        NamingConvention namingConvention = cfg.namingConvention();
        if (namingConvention == null) {
            namingConvention = new NamingConventionOracle(64, null);
        }

        switch (cfg.databaseType()) {
        case HSQLDB:
            dialect = new HSQLDialect(namingConvention, cfg.isMultitenant());
            break;
        case MySQL:
            dialect = new MySQLDialect(namingConvention, cfg.isMultitenant());
            break;
        case Oracle:
            dialect = new OracleDialect(namingConvention, cfg.isMultitenant());
            break;
        default:
            throw new Error("Unsupported driver Dialect " + cfg.driverClass());
        }
        // Development support configuration
        dialect.setTablesItentityOffset(cfg.tablesItentityOffset());
    }

    public void dispose() {
        try {
            connectionPool.close();
        } catch (Throwable e) {
            log.error("pool close error", e);
        }
    }

    public void deregister() {
        dispose();

        // Unregister drivers during shutdown.
        // Fix Memory leak that the singleton java.sql.DriverManager can result in.
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        List<Driver> drvCopy = new Vector<Driver>();
        while (drivers.hasMoreElements()) {
            drvCopy.add(drivers.nextElement());
        }
        for (Driver d : drvCopy) {
            if (d.getClass().getClassLoader() != ConnectionProvider.class.getClassLoader()) {
                log.debug("do not deregister {}", d.getClass().getName());
                continue;
            }
            try {
                DriverManager.deregisterDriver(d);
                log.info("deregistered driver {}", d.getClass().getName());
                if ("oracle.jdbc.OracleDriver".equals(d.getClass().getName())) {
                    deregisterOracleDiagnosabilityMBean();
                }
            } catch (Throwable e) {
                log.error("deregister error", e);
            }
        }
    }

    public void deregisterOracleDiagnosabilityMBean() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            Hashtable<String, String> keys = new Hashtable<String, String>();
            keys.put("type", "diagnosability");
            keys.put("name", cl.getClass().getName() + "@" + Integer.toHexString(cl.hashCode()).toLowerCase());
            mbs.unregisterMBean(new ObjectName("com.oracle.jdbc", keys));
            log.info("deregistered OracleDiagnosabilityMBean");
        } catch (javax.management.InstanceNotFoundException nf) {
            log.debug("Oracle OracleDiagnosabilityMBean not found ", nf);
        } catch (Throwable e) {
            log.error("Oracle  JMX unregister error", e);
        }
    }

    public Dialect getDialect() {
        return dialect;
    }

    public Connection getConnection(ConnectionTarget reason) {
        try {
            if ((reason == ConnectionTarget.forUpdate) && ServerSideConfiguration.instance().datastoreReadOnly()) {
                throw new UserRuntimeException(ServerSideConfiguration.instance().getApplicationMaintenanceMessage());
            }
            return dataSource.getConnection();
        } catch (SQLException e) {
            log.error("SQL connection error", e);
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            log.error("SQL connection error", e);
            throw new RuntimeException(e);
        }
    }

}
