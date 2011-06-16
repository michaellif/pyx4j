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
 * Created on Apr 10, 2011
 * @author dmitry
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import com.pyx4j.commons.Consts;
import com.pyx4j.entity.rdb.cfg.Configuration;

public class ConnectionPoolC3P0 implements ConnectionPool {

    private static final Logger log = LoggerFactory.getLogger(ConnectionPoolC3P0.class);

    private final ComboPooledDataSource dataSource;

    public ConnectionPoolC3P0(Configuration cfg) throws Exception {
        dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(cfg.driverClass()); // load the jdbc driver            
        dataSource.setJdbcUrl(cfg.connectionUrl());
        dataSource.setUser(cfg.userName());
        dataSource.setPassword(cfg.password());

        // the settings below are optional -- c3p0 can work with defaults
        dataSource.setMinPoolSize(cfg.minPoolSize()); // default is 3
        dataSource.setMaxPoolSize(cfg.maxPoolSize()); // default is 15, we may need more for the server
        dataSource.setAcquireIncrement(1); // how many new connections it will try to acquire if pool is exhausted
        dataSource.setAcquireRetryAttempts(3); // Defines how many times c3p0 will try to acquire a new Connection from the database before giving up
        dataSource.setMaxIdleTime(20 * Consts.MIN2SEC); // Seconds a Connection can remain pooled but unused before being discarded.

        dataSource.setAutomaticTestTable("_c3p0_connection_test"); // If provided, c3p0 will create an empty table of the specified name, and use queries against that table to test the Connection
        dataSource.setIdleConnectionTestPeriod(5 * Consts.MIN2SEC); //If this is a number greater than 0, c3p0 will test all idle, pooled but unchecked-out connections, every this number of seconds.

        dataSource.setUnreturnedConnectionTimeout(60);
        dataSource.setDebugUnreturnedConnectionStackTraces(true);

        log.debug("Pool size is {} min and {} max", dataSource.getMinPoolSize(), dataSource.getMaxPoolSize());
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void close() throws Exception {
        dataSource.close();
    }

    @Override
    public String toString() {
        return "C3P0";
    }
}
