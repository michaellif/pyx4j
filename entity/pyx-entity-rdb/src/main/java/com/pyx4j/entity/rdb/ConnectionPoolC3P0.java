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
        dataSource.setAcquireIncrement(3); // how many new connections it will try to acquire if pool is exhausted
        dataSource.setMaxPoolSize(cfg.maxPoolSize()); // default is 15, we may need more for the server

        log.info("Pool size is {} min and {} max", dataSource.getMinPoolSize(), dataSource.getMaxPoolSize());
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
