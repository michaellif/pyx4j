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
 */
package com.pyx4j.entity.rdb.cfg;

import java.util.List;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.dialect.NamingConvention;

public interface Configuration extends IPersistenceConfiguration {

    public enum Ddl {
        auto, validate, disabled
    };

    public enum ConnectionPoolProvider {
        dbcp, c3p0
    };

    public class ConnectionPoolConfiguration {

        public int initialPoolSize = 1;

        public int minPoolSize = 3;

        public int maxPoolSize = 15;

        public int checkoutTimeout = Consts.MIN2SEC;

        public int unreturnedConnectionTimeout = Consts.MIN2SEC;

        public int maxPoolPreparedStatements = 1000;

        public int maxStatementsPerConnection = 0;

        public int statementCacheNumDeferredCloseThreads = 0;

        public boolean testConnectionOnCheckout;

        public boolean testConnectionOnCheckin = false;

        public int numHelperThreads = 3;

        /**
         * Set the default values for all DB connection types
         */
        public ConnectionPoolConfiguration(ConnectionPoolType connectionType) {
            assert connectionType != ConnectionPoolType.DDL;

            if (connectionType != null) {
                switch (connectionType) {
                case BackgroundProcess:
                    maxPoolSize = 20;
                    unreturnedConnectionTimeout = 1 * Consts.HOURS2SEC;
                    checkoutTimeout = 20 * Consts.MIN2SEC;
                    testConnectionOnCheckout = true;
                    break;
                case TransactionProcessing:
                    maxPoolSize = 40;
                    unreturnedConnectionTimeout = 10 * Consts.MIN2SEC;
                    checkoutTimeout = 2 * Consts.MIN2SEC;
                    testConnectionOnCheckout = true;
                    break;
                case Scheduler:
                    maxPoolSize = 4;
                    unreturnedConnectionTimeout = 10 * Consts.MIN2SEC;
                    testConnectionOnCheckout = true;
                    break;
                default:
                    if (ServerSideConfiguration.isRunningInDeveloperEnviroment()) {
                        testConnectionOnCheckout = true;
                    } else {
                        testConnectionOnCheckout = false;
                    }
                    break;
                }
            }

            if (ServerSideConfiguration.isStartedUnderJvmDebugMode()) {
                unreturnedConnectionTimeout = 0;
            }
        }

        public int initialPoolSize() {
            return initialPoolSize;
        }

        public int minPoolSize() {
            return minPoolSize;
        }

        public int maxPoolSize() {
            return maxPoolSize;
        }

        /**
         * The number of seconds a client calling getConnection() will wait for a Connection to be checked-in or acquired when the pool is exhausted. Zero
         * means wait indefinitely. Setting any positive value will cause the getConnection() call to time-out and break with an SQLException after the
         * specified number of seconds.
         *
         */
        public int getCheckoutTimeout() {
            return checkoutTimeout;
        }

        /**
         * Defines a limit (in seconds) to how long a Connection may remain checked out.
         * If set to a nozero value, unreturned, checked-out Connections that exceed this limit will be summarily destroyed, and then replaced in the pool.
         */
        public int unreturnedConnectionTimeout() {
            return unreturnedConnectionTimeout;
        }

        public int maxPoolPreparedStatements() {
            return maxPoolPreparedStatements;
        }

        public int maxStatementsPerConnection() {
            return maxStatementsPerConnection;
        }

        public int statementCacheNumDeferredCloseThreads() {
            return statementCacheNumDeferredCloseThreads;
        }

        public boolean testConnectionOnCheckout() {
            return testConnectionOnCheckout;
        }

        public boolean testConnectionOnCheckin() {
            return testConnectionOnCheckin;
        }

        public int numHelperThreads() {
            return numHelperThreads;
        }

    }

    public enum DatabaseType {
        Oracle, MySQL, PostgreSQL, HSQLDB, H2, Derby, Other
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

    public boolean forceQualifiedNames();

    public String tablesSchema();

    /**
     * Allow to define globally for application TABLESPACE, STORAGE, ENGINE
     */
    public String tablesCreateOption();

    public String tableCreateOption(String entityShortName);

    public String tableQueryHint(String tableName);

    public boolean sequencesBaseIdentity();

    /**
     * Applicable for MultitenancyType.SeparateSchemas
     */
    public String sharedSequencesSchema();

    public boolean createForeignKeys();

    public boolean allowForeignKeyDeferrable();

    /**
     * Used in development to create sparse identity values for every table, to ensure pseudo-unique value of every key.
     *
     * @return 0 if all id starts with the same value 0
     */
    public int tablesIdentityOffset();

    /**
     * Allow to change Sequence START WITH for specific Entity/Table
     *
     * @param entityShortName
     * @return initial Sequence value
     */
    public Integer tableIdentityOffset(String entityShortName);

    public List<String> dbInitializationSqls();

    /**
     * This takes priority over connectionAutomaticTestTable
     */
    public String connectionValidationQuery();

    public String connectionAutomaticTestTable();

    public boolean readOnly();

    public ConnectionPoolProvider connectionPool();

    public ConnectionPoolConfiguration connectionPoolConfiguration(ConnectionPoolType connectionType);

    public ConnectionCustomizer connectionCustomizer();

    /**
     *
     * @return return null to use framework default
     */
    public NamingConvention namingConvention();
}
