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

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.impl.GenericObjectPool;

import com.pyx4j.commons.Consts;
import com.pyx4j.entity.rdb.cfg.Configuration;
import com.pyx4j.entity.rdb.cfg.Configuration.ConnectionPoolConfiguration;
import com.pyx4j.entity.rdb.cfg.ConnectionPoolType;
import com.pyx4j.log4j.LoggerConfig;

public class ConnectionPoolDBCP implements ConnectionPool {

    private final Map<ConnectionPoolType, DataSource> dataSources = new HashMap<ConnectionPoolType, DataSource>();

    private final Map<ConnectionPoolType, GenericObjectPool<PoolableConnection>> connectionPools = new HashMap<>();

    private ObjectName jndiName(String type) {
        try {
            return new ObjectName("org.apache.commons.dbcp2." + LoggerConfig.getContextName() + ":type=dbpool");
        } catch (MalformedObjectNameException e) {
            throw new Error(e);
        }
    }

    public ConnectionPoolDBCP(Configuration cfg) {

        for (ConnectionPoolType connectionType : ConnectionPoolType.poolable()) {
            ConnectionPoolConfiguration cpc = cfg.connectionPoolConfiguration(connectionType);

            ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(cfg.connectionUrl(), cfg.userName(), cfg.password());

            PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, jndiName(connectionType.name().toString()));
            poolableConnectionFactory.setValidationQueryTimeout(2);
            poolableConnectionFactory.setValidationQuery(cfg.connectionValidationQuery());

            if (cpc.maxPoolPreparedStatements() > 0) {
                poolableConnectionFactory.setPoolStatements(true);
                poolableConnectionFactory.setMaxOpenPrepatedStatements(cpc.maxPoolPreparedStatements());
            }

            poolableConnectionFactory.setMaxConnLifetimeMillis(Consts.SEC2MILLISECONDS * cpc.unreturnedConnectionTimeout());

            GenericObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory);
            connectionPool.setTestWhileIdle(true);

            //connectionPool.setMinIdle(cpc.minPoolSize());
            connectionPool.setMaxTotal(cpc.maxPoolSize());

            poolableConnectionFactory.setPool(connectionPool);
            DataSource dataSource = new PoolingDataSource<PoolableConnection>(connectionPool);

            dataSources.put(connectionType, dataSource);
            connectionPools.put(connectionType, connectionPool);
        }

        {
            ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(cfg.connectionUrl(), cfg.dbAdministrationUserName(),
                    cfg.dbAdministrationPassword());

            PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, jndiName("DDL"));
            poolableConnectionFactory.setValidationQueryTimeout(1);
            poolableConnectionFactory.setValidationQuery(cfg.connectionValidationQuery());

            GenericObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory);
            connectionPool.setTestWhileIdle(true);

            DataSource dataSourceAministration = new PoolingDataSource<PoolableConnection>(connectionPool);

            dataSources.put(ConnectionPoolType.DDL, dataSourceAministration);
            connectionPools.put(ConnectionPoolType.DDL, connectionPool);
        }
    }

    @Override
    public DataSource getDataSource(ConnectionPoolType connectionType) {
        return dataSources.get(connectionType);
    }

    @Override
    public void resetConnectionPool() {
        // TODO implement
    }

    @Override
    public void close() throws Throwable {
        Throwable closeError = null;
        for (GenericObjectPool<PoolableConnection> connectionPool : connectionPools.values()) {
            try {
                connectionPool.close();
            } catch (Exception e) {
                closeError = e;
            }
        }
        if (closeError != null) {
            throw closeError;
        }
    }

    @Override
    public String toString() {
        return "DBCP";
    }

}
