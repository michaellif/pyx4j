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
package com.pyx4j.entity.rdb.cfg;

import com.pyx4j.entity.rdb.dialect.NamingConvention;

public abstract class ConfigurationPostgreSQL implements Configuration {

    @Override
    public DatabaseType databaseType() {
        return DatabaseType.PostgreSQL;
    }

    @Override
    public String driverClass() {
        return "org.postgresql.Driver";
    }

    public int dbPort() {
        return 5432;
    }

    @Override
    public String connectionUrl() {
        StringBuilder b = new StringBuilder();
        b.append("jdbc:postgresql://").append(dbHost()).append(":").append(dbPort()).append("/").append(dbName());
        return b.toString();
    }

    @Override
    public String dbAdministrationUserName() {
        return userName();
    }

    @Override
    public String dbAdministrationPassword() {
        return password();
    }

    @Override
    public Ddl ddl() {
        return Ddl.auto;
    }

    @Override
    public MultitenancyType getMultitenancyType() {
        return MultitenancyType.SingleTenant;
    }

    @Override
    public String sharedSequencesSchema() {
        return null;
    }

    @Override
    public boolean createForeignKeys() {
        return true;
    }

    @Override
    public String connectionValidationQuery() {
        return "SELECT 1";
    }

    @Override
    public String connectionAutomaticTestTable() {
        return "_c3p0_connection_test";
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
    public int initialPoolSize() {
        return 1;
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
    public int initialBackgroundProcessPoolSize() {
        return 1;
    }

    @Override
    public int minBackgroundProcessPoolSize() {
        return 2;
    }

    @Override
    public int maxBackgroundProcessPoolSize() {
        return 40;
    }

    @Override
    public int maxPoolPreparedStatements() {
        return 1000;
    }

    @Override
    public int unreturnedConnectionTimeout() {
        return 60;
    }

    @Override
    public int unreturnedConnectionBackgroundProcessTimeout() {
        return 60 * 60;
    }

    @Override
    public NamingConvention namingConvention() {
        return null;
    }

    @Override
    public int tablesItentityOffset() {
        return 0;
    }

    @Override
    public String toString() {
        return ConfigurationToString.toString(this);
    }
}
