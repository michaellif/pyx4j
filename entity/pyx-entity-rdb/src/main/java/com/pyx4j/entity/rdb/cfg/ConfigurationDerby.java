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

import java.util.List;

import com.pyx4j.entity.rdb.dialect.NamingConvention;

public abstract class ConfigurationDerby implements Configuration {

    @Override
    public DatabaseType databaseType() {
        return DatabaseType.Derby;
    }

    @Override
    public String driverClass() {
        return "org.apache.derby.jdbc.EmbeddedDriver";
    }

    @Override
    public String connectionUrl() {
        return "jdbc:derby:memory:" + dbName() + ";create=true";
    }

    @Override
    public String dbHost() {
        return null;
    }

    @Override
    public String userName() {
        return "root";
    }

    @Override
    public String password() {
        return "root";
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
    public boolean sequencesBaseIdentity() {
        return false;
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
    public boolean forceQualifiedNames() {
        return false;
    }

    @Override
    public String tablesSchema() {
        return null;
    }

    @Override
    public String sharedSequencesSchema() {
        return null;
    }

    @Override
    public boolean createForeignKeys() {
        //TODO fix this
        return false;
    }

    @Override
    public boolean allowForeignKeyDeferrable() {
        return false;
    }

    @Override
    public String connectionValidationQuery() {
        return null;
    }

    @Override
    public boolean readOnly() {
        return false;
    }

    @Override
    public List<String> dbInitializationSqls() {
        return null;
        //return Arrays.asList(new String[] { "SET DATABASE TRANSACTION CONTROL MVCC" });
    }

    @Override
    public ConnectionPoolProvider connectionPool() {
        return ConnectionPoolProvider.c3p0;
    }

    @Override
    public ConnectionPoolConfiguration connectionPoolConfiguration(ConnectionPoolType connectionType) {
        return new ConnectionPoolConfiguration(connectionType);
    }

    @Override
    public String connectionAutomaticTestTable() {
        return "_c3p0_connection_test";
    }

    @Override
    public int tablesIdentityOffset() {
        return 0;
    }

    @Override
    public NamingConvention namingConvention() {
        return null;
    }

    @Override
    public String toString() {
        return ConfigurationToString.toString(this);
    }
}
