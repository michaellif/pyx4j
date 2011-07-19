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
 * Created on 2011-04-19
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.cfg;

import com.pyx4j.entity.rdb.dialect.NamingConvention;
import com.pyx4j.entity.rdb.dialect.NamingConventionOracle;

public abstract class ConfigurationOracle implements Configuration {

    @Override
    public DatabaseType databaseType() {
        return DatabaseType.Oracle;
    }

    @Override
    public String driverClass() {
        return "oracle.jdbc.driver.OracleDriver";
    }

    public int dbPort() {
        return 1521;
    }

    @Override
    public String connectionUrl() {
        return "jdbc:oracle:thin:@" + dbHost() + ":" + dbPort() + ":" + dbName();
    }

    @Override
    public boolean isMultitenant() {
        return false;
    }

    @Override
    public String connectionValidationQuery() {
        return "SELECT 1 FROM DUAL";
    }

    @Override
    public boolean readOnly() {
        return false;
    }

    @Override
    public ConnectionPoolProvider connectionPool() {
        return ConnectionPoolProvider.c3p0;
    }

    @Override
    public int minPoolSize() {
        return 3;
    }

    @Override
    public int maxPoolSize() {
        return 15;
    }

    @Override
    public int maxPoolPreparedStatements() {
        return 1000;
    }

    @Override
    public NamingConvention namingConvention() {
        return new NamingConventionOracle(32, null);
    }

}
