/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on May 2, 2015
 * @author vlads
 */
package com.pyx4j.quartz;

import java.sql.Connection;
import java.sql.SQLException;

import org.quartz.utils.ConnectionProvider;

import com.mchange.v2.c3p0.C3P0Registry;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.impl.C3P0ImplUtils;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.ConnectionPoolC3P0;
import com.pyx4j.entity.rdb.cfg.Configuration;
import com.pyx4j.entity.rdb.cfg.Configuration.ConnectionPoolConfiguration;
import com.pyx4j.entity.rdb.cfg.ConnectionPoolType;

public class QuartzPoolingConnectionProvider implements ConnectionProvider {

    private ComboPooledDataSource dataSource;

    public QuartzPoolingConnectionProvider() throws Exception {
        Configuration rdbConfiguration = (Configuration) ServerSideConfiguration.instance().getPersistenceConfiguration();
        ConnectionPoolType connectionType = ConnectionPoolType.Scheduler;
        ConnectionPoolConfiguration cpc = rdbConfiguration.connectionPoolConfiguration(connectionType);
        dataSource = ConnectionPoolC3P0.createDataSource(rdbConfiguration, connectionType, cpc);
        dataSource.setIdentityToken(C3P0ImplUtils.allocateIdentityToken(dataSource));
        C3P0Registry.reregister(dataSource);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void shutdown() throws SQLException {
        dataSource.close();

    }

    @Override
    public void initialize() throws SQLException {
    }

}
