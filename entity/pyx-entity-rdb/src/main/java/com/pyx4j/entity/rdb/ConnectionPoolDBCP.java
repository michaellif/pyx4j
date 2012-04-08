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

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import com.pyx4j.entity.rdb.cfg.Configuration;

public class ConnectionPoolDBCP implements ConnectionPool {

    private final GenericObjectPool connectionPool;

    private final DataSource dataSource;

    private final GenericObjectPool connectionPoolBackgroundProcess;

    private final DataSource dataSourceBackgroundProcess;

    private final GenericObjectPool connectionPoolAministration;

    private final DataSource dataSourceAministration;

    public ConnectionPoolDBCP(Configuration cfg) {
        {
            connectionPool = new GenericObjectPool(null);
            connectionPool.setTestWhileIdle(true);

            ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(cfg.connectionUrl(), cfg.userName(), cfg.password());

            PoolableConnectionFactory poolable = new PoolableConnectionFactory(connectionFactory, connectionPool, null, cfg.connectionValidationQuery(),
                    cfg.readOnly(), true);
            poolable.setValidationQueryTimeout(1);

            dataSource = new PoolingDataSource(connectionPool);
        }

        {
            connectionPoolBackgroundProcess = new GenericObjectPool(null);
            connectionPoolBackgroundProcess.setTestWhileIdle(true);

            ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(cfg.connectionUrl(), cfg.userName(), cfg.password());

            PoolableConnectionFactory poolable = new PoolableConnectionFactory(connectionFactory, connectionPoolBackgroundProcess, null,
                    cfg.connectionValidationQuery(), cfg.readOnly(), true);
            poolable.setValidationQueryTimeout(1);

            dataSourceBackgroundProcess = new PoolingDataSource(connectionPoolBackgroundProcess);
        }

        {
            connectionPoolAministration = new GenericObjectPool(null);
            connectionPoolAministration.setTestWhileIdle(true);

            ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(cfg.connectionUrl(), cfg.dbAdministrationUserName(),
                    cfg.dbAdministrationPassword());

            PoolableConnectionFactory poolable = new PoolableConnectionFactory(connectionFactory, connectionPoolAministration, null,
                    cfg.connectionValidationQuery(), cfg.readOnly(), true);
            poolable.setValidationQueryTimeout(1);

            dataSourceAministration = new PoolingDataSource(connectionPoolAministration);
        }
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public DataSource getBackgroundProcessDataSource() {
        return dataSourceBackgroundProcess;
    }

    @Override
    public DataSource getAministrationDataSource() {
        return dataSourceAministration;
    }

    @Override
    public void close() throws Exception {
        try {
            connectionPool.close();
        } finally {
            connectionPoolBackgroundProcess.close();
        }
    }

    @Override
    public String toString() {
        return "DBCP";
    }

}
