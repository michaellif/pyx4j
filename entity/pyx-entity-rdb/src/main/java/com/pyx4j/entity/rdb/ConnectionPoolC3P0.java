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

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.C3P0Registry;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.impl.C3P0ImplUtils;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.cfg.Configuration;
import com.pyx4j.entity.rdb.cfg.Configuration.ConnectionPoolConfiguration;
import com.pyx4j.entity.rdb.cfg.ConnectionPoolType;

public class ConnectionPoolC3P0 implements ConnectionPool {

    private static final Logger log = LoggerFactory.getLogger(ConnectionPoolC3P0.class);

    private static boolean singleInstanceCreated = false;

    private final Map<ConnectionPoolType, DataSource> dataSources = new HashMap<ConnectionPoolType, DataSource>();

    public ConnectionPoolC3P0(Configuration configuration) throws Exception {
        if (singleInstanceCreated) {
            throw new Error("Only single Instance of  ConnectionPoolC3P0 supported");
        }
        log.debug("initialize DB ConnectionPool {}", configuration);

        for (ConnectionPoolType connectionType : ConnectionPoolType.poolable()) {
            ConnectionPoolConfiguration cpc = configuration.connectionPoolConfiguration(connectionType);

            ComboPooledDataSource dataSource = createDataSource(configuration);
            dataSource.setDataSourceName(connectionType.name());

            dataSource.setInitialPoolSize(cpc.initialPoolSize());
            dataSource.setMinPoolSize(cpc.minPoolSize());
            dataSource.setMaxPoolSize(cpc.maxPoolSize());
            dataSource.setMaxStatements(cpc.maxPoolPreparedStatements());

            dataSource.setCheckoutTimeout(Consts.SEC2MILLISECONDS * cpc.getCheckoutTimeout());

            dataSource.setUnreturnedConnectionTimeout(cpc.unreturnedConnectionTimeout());
            dataSource.setDebugUnreturnedConnectionStackTraces(true);

            if (ServerSideConfiguration.isRunningInDeveloperEnviroment() || (connectionType != ConnectionPoolType.Web)) {
                dataSource.setTestConnectionOnCheckout(true);
            }

            log.debug("{} Pool size is {} min and {} max", connectionType, dataSource.getMinPoolSize(), dataSource.getMaxPoolSize());
            dataSource.setIdentityToken(C3P0ImplUtils.allocateIdentityToken(dataSource));
            C3P0Registry.reregister(dataSource);

            dataSources.put(connectionType, dataSource);
        }

        {
            dataSources.put(
                    ConnectionPoolType.DDL,
                    DataSources.unpooledDataSource(configuration.connectionUrl(), configuration.dbAdministrationUserName(),
                            configuration.dbAdministrationPassword()));
        }
        singleInstanceCreated = true;
    }

    private ComboPooledDataSource createDataSource(Configuration cfg) throws Exception {
        ComboPooledDataSource dataSource = new ComboPooledDataSource(false);
        dataSource.setDriverClass(cfg.driverClass()); // load the jdbc driver
        dataSource.setJdbcUrl(cfg.connectionUrl());
        dataSource.setUser(cfg.userName());
        dataSource.setPassword(cfg.password());

        // the settings below are optional -- c3p0 can work with defaults
        dataSource.setAcquireIncrement(1); // how many new connections it will try to acquire if pool is exhausted
        dataSource.setAcquireRetryAttempts(3); // Defines how many times c3p0 will try to acquire a new Connection from the database before giving up
        dataSource.setMaxIdleTime(20 * Consts.MIN2SEC); // Seconds a Connection can remain pooled but unused before being discarded.

        if (cfg.connectionAutomaticTestTable() != null) {
            dataSource.setAutomaticTestTable(cfg.connectionAutomaticTestTable()); // If provided, c3p0 will create an empty table of the specified name, and use queries against that table to test the Connection
        } else {
            dataSource.setPreferredTestQuery(cfg.connectionValidationQuery());
        }
        dataSource.setIdleConnectionTestPeriod(5 * Consts.MIN2SEC); //If this is a number greater than 0, c3p0 will test all idle, pooled but unchecked-out connections, every this number of seconds.
        return dataSource;
    }

    @Override
    public DataSource getDataSource(ConnectionPoolType connectionType) {
        return dataSources.get(connectionType);
    }

    @Override
    public void resetConnectionPool() {
        for (DataSource dataSource : dataSources.values()) {
            if (dataSource instanceof ComboPooledDataSource) {
                ((ComboPooledDataSource) dataSource).resetPoolManager(false);
            }
        }
    }

    @Override
    public void close() throws Throwable {
        singleInstanceCreated = false;
        Throwable closeError = null;
        for (DataSource dataSource : dataSources.values()) {
            if (dataSource instanceof ComboPooledDataSource) {
                try {
                    ((ComboPooledDataSource) dataSource).close();
                } catch (Throwable e) {
                    closeError = e;
                }
            }
        }
        if (closeError != null) {
            throw closeError;
        }
    }

    @Override
    public String toString() {
        return "C3P0";
    }

}
