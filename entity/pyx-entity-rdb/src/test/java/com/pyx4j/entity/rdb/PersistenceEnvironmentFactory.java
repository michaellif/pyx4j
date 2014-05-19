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
 * Created on 2010-07-08
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.rdb.derby.TestConfigurationDerby;
import com.pyx4j.entity.rdb.h2.TestConfigurationH2;
import com.pyx4j.entity.rdb.hsql.TestConfigurationHSQL;
import com.pyx4j.entity.rdb.mysql.TestConfigurationMySQL;
import com.pyx4j.entity.rdb.oracle.TestConfigurationOracle;
import com.pyx4j.entity.rdb.postgresql.TestConfigurationPostgreSQL;
import com.pyx4j.entity.test.server.PersistenceEnvironment;

public class PersistenceEnvironmentFactory {

    public static PersistenceEnvironment getMySQLPersistenceEnvironment() {
        return new RDBDatastorePersistenceEnvironment(new TestConfigurationMySQL());
    }

    public static PersistenceEnvironment getHSQLPersistenceEnvironment() {
        return new RDBDatastorePersistenceEnvironment(new TestConfigurationHSQL());
    }

    public static PersistenceEnvironment getH2PersistenceEnvironment() {
        return new RDBDatastorePersistenceEnvironment(new TestConfigurationH2());
    }

    public static PersistenceEnvironment getOraclePersistenceEnvironment() {
        return new RDBDatastorePersistenceEnvironment(new TestConfigurationOracle());
    }

    public static PersistenceEnvironment getPostgreSQLPersistenceEnvironment() {
        return new RDBDatastorePersistenceEnvironment(new TestConfigurationPostgreSQL());
    }

    public static PersistenceEnvironment getDerbyPersistenceEnvironment() {
        return new RDBDatastorePersistenceEnvironment(new TestConfigurationDerby());
    }

    public static PersistenceEnvironment getPersistenceEnvironment(DatabaseType databaseType) {
        switch (databaseType) {
        case Oracle:
            return getOraclePersistenceEnvironment();
        case MySQL:
            return getMySQLPersistenceEnvironment();
        case PostgreSQL:
            return getPostgreSQLPersistenceEnvironment();
        case HSQLDB:
            return getHSQLPersistenceEnvironment();
        case Derby:
            return getDerbyPersistenceEnvironment();
        default:
            throw new IllegalArgumentException();
        }
    }
}
