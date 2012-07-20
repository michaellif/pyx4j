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

import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.entity.rdb.dialect.NamingConvention;

public interface Configuration extends IPersistenceConfiguration {

    public enum Ddl {
        auto, validate, disabled
    };

    public enum ConnectionPoolProvider {
        dbcp, c3p0
    };

    public enum DatabaseType {
        Oracle, MySQL, PostgreSQL, HSQLDB, Other
    };

    public enum MultitenancyType {

        SingleTenant,

        SeparateSchemas,

        SharedSchema

    }

    public String driverClass();

    public DatabaseType databaseType();

    public String dbHost();

    public String dbName();

    public String connectionUrl();

    public String userName();

    public String password();

    public String dbAdministrationUserName();

    public String dbAdministrationPassword();

    public Ddl ddl();

    public MultitenancyType getMultitenancyType();

    /**
     * Applicable for MultitenancyType.SeparateSchemas
     */
    public String sharedSequencesSchema();

    public boolean createForeignKeys();

    /**
     * Used in development to create sparse identity values for every table, to ensure pseudo-unique value of every key.
     * 
     * @return 0 if all id starts with the same value 0
     */
    public int tablesItentityOffset();

    public String connectionValidationQuery();

    public String connectionAutomaticTestTable();

    public boolean readOnly();

    public ConnectionPoolProvider connectionPool();

    public int initialPoolSize();

    public int minPoolSize();

    public int maxPoolSize();

    public int initialBackgroundProcessPoolSize();

    public int minBackgroundProcessPoolSize();

    public int maxBackgroundProcessPoolSize();

    public int maxPoolPreparedStatements();

    public int unreturnedConnectionTimeout();

    public int unreturnedConnectionBackgroundProcessTimeout();

    /**
     * 
     * @return return null to use framework default
     */
    public NamingConvention namingConvention();
}
