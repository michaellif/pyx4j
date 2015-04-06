/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2012-10-20
 * @author vlads
 */
package com.pyx4j.tester.server;

import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.rdb.cfg.ConfigurationHSQL;
import com.pyx4j.entity.rdb.cfg.ConfigurationMySQL;
import com.pyx4j.entity.rdb.cfg.ConfigurationPostgreSQL;
import com.pyx4j.essentials.server.EssentialsServerSideConfiguration;

public class TestServerSideConfiguration extends EssentialsServerSideConfiguration {

    private final DatabaseType databaseType;

    public TestServerSideConfiguration(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    @Override
    public IPersistenceConfiguration getPersistenceConfiguration() {
        if (databaseType == DatabaseType.MySQL) {
            return new ConfigurationMySQL() {

                @Override
                public String dbHost() {
                    return "localhost";
                }

                @Override
                public String dbName() {
                    return "tst_entity";
                }

                @Override
                public String userName() {
                    return "tst_entity";
                }

                @Override
                public String password() {
                    return "tst_entity";
                }
            };
        } else if (databaseType == DatabaseType.PostgreSQL) {
            return new ConfigurationPostgreSQL() {

                @Override
                public String dbHost() {
                    return "localhost";
                }

                @Override
                public String dbName() {
                    return "tst_entity";
                }

                @Override
                public String userName() {
                    return "tst_entity";
                }

                @Override
                public String password() {
                    return "tst_entity";
                }
            };
        } else {
            return new ConfigurationHSQL() {

                @Override
                public String dbName() {
                    return "test";
                }
            };
        }
    }

}
