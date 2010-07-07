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

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import com.pyx4j.entity.rdb.cfg.Configuration;
import com.pyx4j.entity.rdb.cfg.ConfigurationMySQL;

public class ConnectionProvider {

    private DataSource dataSource;

    private synchronized void setupDataSource() throws SQLException {
        if (dataSource != null) {
            return;
        }

        Configuration cfg = new ConfigurationMySQL();
        try {
            Class.forName(cfg.driverClass());
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC driver " + cfg.driverClass() + " not found");
        }

        GenericObjectPool connectionPool = new GenericObjectPool(null);

        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(cfg.connectionUrl(), cfg.userName(), cfg.password());

        new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);

        dataSource = new PoolingDataSource(connectionPool);
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            setupDataSource();
        }
        return dataSource.getConnection();
    }

}
