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
 */
package com.pyx4j.entity.rdb;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.C3P0Registry;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.cfg.C3P0Config;
import com.mchange.v2.c3p0.impl.C3P0ImplUtils;
import com.mchange.v2.c3p0.management.ActiveManagementCoordinator;
import com.mchange.v2.cfg.MultiPropertiesConfig;

import com.pyx4j.commons.Consts;
import com.pyx4j.entity.rdb.cfg.Configuration;
import com.pyx4j.entity.rdb.cfg.Configuration.ConnectionPoolConfiguration;
import com.pyx4j.entity.rdb.cfg.ConnectionCustomizer;
import com.pyx4j.entity.rdb.cfg.ConnectionPoolType;
import com.pyx4j.log4j.LoggerConfig;

public class ConnectionPoolC3P0 implements ConnectionPool {

    private static final Logger log = LoggerFactory.getLogger(ConnectionPoolC3P0.class);

    private static boolean singleInstanceCreated = false;

    private final Map<ConnectionPoolType, DataSource> dataSources = new HashMap<ConnectionPoolType, DataSource>();

    public ConnectionPoolC3P0(Configuration configuration) throws Exception {
        if (singleInstanceCreated) {
            throw new Error("Only single Instance of  ConnectionPoolC3P0 supported");
        }
        initC3P0Management();

        log.debug("initialize DB ConnectionPool {}", configuration);

        for (ConnectionPoolType connectionType : ConnectionPoolType.managedByPersistenceService()) {
            ConnectionPoolConfiguration cpc = configuration.connectionPoolConfiguration(connectionType);
            ComboPooledDataSource dataSource = createDataSource(configuration, connectionType, cpc);
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

    /**
     * Configure C3P0 application specific settings.
     */
    private static void initC3P0Management() {
        if (LoggerConfig.getContextName() != null) {
            Properties overrideProps = new Properties();
            overrideProps.put(ActiveManagementCoordinator.C3P0_REGISTRY_NAME_KEY, LoggerConfig.getContextName());
            MultiPropertiesConfig overrides = MultiPropertiesConfig.fromProperties(overrideProps);
            C3P0Config.refreshMainConfig(new MultiPropertiesConfig[] { overrides }, "Set webapp specific C3P0Registry name.");
        }
    }

    public static ComboPooledDataSource createDataSource(Configuration configuration, ConnectionPoolType connectionType,
            ConnectionPoolConfiguration cpConfiguration) throws Exception {

        Properties properties = new Properties();
        ConnectionCustomizer connectionCustomizer = configuration.connectionCustomizer();
        if (connectionCustomizer != null) {
            connectionCustomizer.initConnectionProperties(properties, connectionType);
        }

        ComboPooledDataSource dataSource = new ComboPooledDataSource(false);
        dataSource.setProperties(properties);
        dataSource.setDriverClass(configuration.driverClass()); // load the jdbc driver
        dataSource.setJdbcUrl(configuration.connectionUrl());
        dataSource.setUser(configuration.userName());
        dataSource.setPassword(configuration.password());

        // the settings below are optional -- c3p0 can work with defaults
        dataSource.setAcquireIncrement(1); // how many new connections it will try to acquire if pool is exhausted
        dataSource.setAcquireRetryAttempts(3); // Defines how many times c3p0 will try to acquire a new Connection from the database before giving up
        dataSource.setMaxIdleTime(20 * Consts.MIN2SEC); // Seconds a Connection can remain pooled but unused before being discarded.

        if (configuration.connectionAutomaticTestTable() != null) {
            dataSource.setAutomaticTestTable(configuration.connectionAutomaticTestTable()); // If provided, c3p0 will create an empty table of the specified name, and use queries against that table to test the Connection
        } else {
            dataSource.setPreferredTestQuery(configuration.connectionValidationQuery());
        }
        dataSource.setIdleConnectionTestPeriod(5 * Consts.MIN2SEC); //If this is a number greater than 0, c3p0 will test all idle, pooled but unchecked-out connections, every this number of seconds.

        dataSource.setDataSourceName(LoggerConfig.getContextName() + "_" + connectionType.name());

        dataSource.setInitialPoolSize(cpConfiguration.initialPoolSize());
        dataSource.setMinPoolSize(cpConfiguration.minPoolSize());
        dataSource.setMaxPoolSize(cpConfiguration.maxPoolSize());
        dataSource.setMaxStatements(cpConfiguration.maxPoolPreparedStatements());

        dataSource.setCheckoutTimeout(Consts.SEC2MILLISECONDS * cpConfiguration.getCheckoutTimeout());

        dataSource.setUnreturnedConnectionTimeout(cpConfiguration.unreturnedConnectionTimeout());
        dataSource.setDebugUnreturnedConnectionStackTraces(true);

        dataSource.setTestConnectionOnCheckout(cpConfiguration.testConnectionOnCheckout());
        dataSource.setTestConnectionOnCheckin(cpConfiguration.testConnectionOnCheckin());

        if (connectionCustomizer != null) {
            Map<String, Object> extensions = new HashMap<>();
            extensions.put(ConnectionPoolType.class.getName(), connectionType);
            extensions.put(ConnectionCustomizer.class.getName(), connectionCustomizer);
            dataSource.setExtensions(extensions);

            dataSource.setConnectionCustomizerClassName(C3P0ConnectionCustomizer.class.getName());
        }

        log.debug("{} Pool size is {} min and {} max", connectionType, dataSource.getMinPoolSize(), dataSource.getMaxPoolSize());

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
