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

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import com.pyx4j.entity.rdb.cfg.Configuration;
import com.pyx4j.entity.rdb.cfg.ConnectionPoolType;

public class ConnectionPoolDBCP implements ConnectionPool {

    private final Map<ConnectionPoolType, DataSource> dataSources = new HashMap<ConnectionPoolType, DataSource>();

    private final Map<ConnectionPoolType, GenericObjectPool> connectionPools = new HashMap<ConnectionPoolType, GenericObjectPool>();

    public ConnectionPoolDBCP(Configuration cfg) {

        for (ConnectionPoolType connectionType : ConnectionPoolType.poolable()) {
            GenericObjectPool connectionPool = new GenericObjectPool(null);
            connectionPool.setTestWhileIdle(true);

            ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(cfg.connectionUrl(), cfg.userName(), cfg.password());

            PoolableConnectionFactory poolable = new PoolableConnectionFactory(connectionFactory, connectionPool, null, cfg.connectionValidationQuery(),
                    cfg.readOnly(), true);
            poolable.setValidationQueryTimeout(1);

            DataSource dataSource = new PoolingDataSource(connectionPool);

            dataSources.put(connectionType, dataSource);
            connectionPools.put(connectionType, connectionPool);
        }

        {
            GenericObjectPool connectionPool = new GenericObjectPool(null);
            connectionPool.setTestWhileIdle(true);

            ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(cfg.connectionUrl(), cfg.dbAdministrationUserName(),
                    cfg.dbAdministrationPassword());

            PoolableConnectionFactory poolable = new PoolableConnectionFactory(connectionFactory, connectionPool, null, cfg.connectionValidationQuery(),
                    cfg.readOnly(), true);
            poolable.setValidationQueryTimeout(1);

            DataSource dataSourceAministration = new PoolingDataSource(connectionPool);

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
        for (GenericObjectPool connectionPool : connectionPools.values()) {
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
