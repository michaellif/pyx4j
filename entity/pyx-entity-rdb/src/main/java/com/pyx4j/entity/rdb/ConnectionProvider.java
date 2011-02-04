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
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.rdb.cfg.Configuration;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.rdb.dialect.HSQLDialect;
import com.pyx4j.entity.rdb.dialect.MySQLDialect;

public class ConnectionProvider {

    private static final Logger log = LoggerFactory.getLogger(ConnectionProvider.class);

    private DataSource dataSource;

    private GenericObjectPool connectionPool;

    private Dialect dialect;

    public ConnectionProvider(Configuration cfg) throws SQLException {
        setupDataSource(cfg);
    }

    private synchronized void setupDataSource(Configuration cfg) throws SQLException {
        try {
            Class.forName(cfg.driverClass());
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC driver " + cfg.driverClass() + " not found");
        }

        connectionPool = new GenericObjectPool(null);
        connectionPool.setTestWhileIdle(true);

        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(cfg.connectionUrl(), cfg.userName(), cfg.password());

        PoolableConnectionFactory poolable = new PoolableConnectionFactory(connectionFactory, connectionPool, null, cfg.connectionValidationQuery(),
                cfg.readOnly(), true);
        poolable.setValidationQueryTimeout(1);

        dataSource = new PoolingDataSource(connectionPool);

        if (cfg.driverClass().contains("mysql")) {
            dialect = new MySQLDialect();
        } else if (cfg.driverClass().contains("hsqldb")) {
            dialect = new HSQLDialect();
        } else {
            throw new Error("Unsupported driver Dialect " + cfg.driverClass());
        }
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
                log.debug("do not deregister", d.getClass().getName());
                continue;
            }
            try {
                DriverManager.deregisterDriver(d);
                log.info("deregistered driver {}", d.getClass().getName());
            } catch (Throwable e) {
                log.error("deregister error", e);
            }
        }
    }

    public Dialect getDialect() {
        return dialect;
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
