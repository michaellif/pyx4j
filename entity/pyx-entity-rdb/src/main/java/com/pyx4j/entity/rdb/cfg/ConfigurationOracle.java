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

import java.util.List;

import com.pyx4j.commons.CommonsStringUtils;
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

    /**
     * This will override sid;
     */
    public String serviceName() {
        return null;
    }

    /**
     * This will override host and sid|serviceName; connection would be established using TNSNames
     */
    public String tnsName() {
        return null;
    }

    @Override
    public String connectionUrl() {
        if (CommonsStringUtils.isStringSet(tnsName())) {
            return "jdbc:oracle:thin:@" + tnsName();
        } else if (CommonsStringUtils.isStringSet(serviceName())) {
            return "jdbc:oracle:thin:@//" + dbHost() + ":" + dbPort() + "/" + serviceName();
        } else {
            return "jdbc:oracle:thin:@" + dbHost() + ":" + dbPort() + ":" + dbName();
        }
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
    public final boolean sequencesBaseIdentity() {
        return true;
    }

    @Override
    public List<String> dbInitializationSqls() {
        return null;
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
        return true;
    }

    @Override
    public String tablesSchema() {
        return userName();
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
    public boolean allowForeignKeyDeferrable() {
        return false;
    }

    @Override
    public String connectionValidationQuery() {
        return "SELECT 1 FROM DUAL";
    }

    @Override
    public String connectionAutomaticTestTable() {
        return "C3P0_CONNECTION_TEST";
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
    public int tablesIdentityOffset() {
        return 0;
    }

    @Override
    public NamingConvention namingConvention() {
        return new NamingConventionOracle(32, null);
    }

    @Override
    public String toString() {
        return ConfigurationToString.toString(this);
    }

}
