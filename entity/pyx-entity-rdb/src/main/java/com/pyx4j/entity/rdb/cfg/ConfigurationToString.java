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
 * Created on Sep 17, 2011
 * @author vlads
 */
package com.pyx4j.entity.rdb.cfg;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.rdb.cfg.Configuration.ConnectionPoolConfiguration;

public class ConfigurationToString {

    public static String toString(Configuration conf) {
        StringBuilder b = new StringBuilder();
        b.append("configurationClass                                : ").append(conf.getClass().getName()).append("\n");
        b.append("driverClass                                       : ").append(conf.driverClass()).append("\n");
        b.append("connectionUrl                                     : ").append(conf.connectionUrl()).append("\n");
        b.append("dbHost                                            : ").append(conf.dbHost()).append("\n");
        b.append("dbName                                            : ").append(conf.dbName()).append("\n");
        b.append("userName                                          : ").append(conf.userName()).append("\n");
        b.append("Multitenant                                       : ").append(conf.getMultitenancyType()).append("\n");
        b.append("Ddl                                               : ").append(conf.ddl()).append("\n");

        for (ConnectionPoolType ct : ConnectionPoolType.poolable()) {
            ConnectionPoolConfiguration cpc = conf.connectionPoolConfiguration(ct);
            b.append(CommonsStringUtils.paddingRight(ct.name() + ".initialPoolSize", 50, ' ')).append(": ").append(cpc.initialPoolSize()).append("\n");
            b.append(CommonsStringUtils.paddingRight(ct.name() + ".minPoolSize", 50, ' ')).append(": ").append(cpc.minPoolSize()).append("\n");
            b.append(CommonsStringUtils.paddingRight(ct.name() + ".maxPoolSize", 50, ' ')).append(": ").append(cpc.maxPoolSize()).append("\n");
            b.append(CommonsStringUtils.paddingRight(ct.name() + ".checkoutTimeout", 50, ' ')).append(": ")
                    .append(TimeUtils.durationFormatSeconds(cpc.getCheckoutTimeout())).append("\n");

            b.append(CommonsStringUtils.paddingRight(ct.name() + ".unreturnedConnectionTimeout", 50, ' ')).append(": ")
                    .append(TimeUtils.durationFormatSeconds(cpc.unreturnedConnectionTimeout())).append("\n");
            b.append(CommonsStringUtils.paddingRight(ct.name() + ".numHelperThreads", 50, ' ')).append(": ").append(cpc.numHelperThreads()).append("\n");

            b.append(CommonsStringUtils.paddingRight(ct.name() + ".maxPoolPreparedStatements", 50, ' ')).append(": ").append(cpc.maxPoolPreparedStatements())
                    .append("\n");
            b.append(CommonsStringUtils.paddingRight(ct.name() + ".maxStatementsPerConnection", 50, ' ')).append(": ").append(cpc.maxStatementsPerConnection())
                    .append("\n");
            b.append(CommonsStringUtils.paddingRight(ct.name() + ".statementCacheNumDeferredCloseThreads", 50, ' ')).append(": ")
                    .append(cpc.statementCacheNumDeferredCloseThreads()).append("\n");

            b.append(CommonsStringUtils.paddingRight(ct.name() + ".testConnectionOnCheckout", 50, ' ')).append(": ").append(cpc.testConnectionOnCheckout())
                    .append("\n");
            b.append(CommonsStringUtils.paddingRight(ct.name() + ".testConnectionOnCheckin", 50, ' ')).append(": ").append(cpc.testConnectionOnCheckin())
                    .append("\n");
        }

        b.append("tablesIdentityOffset                              : ").append(conf.tablesIdentityOffset()).append("\n");
        b.append("createForeignKeys                                 : ").append(conf.createForeignKeys()).append("\n");
        b.append("allowForeignKeyDeferrable                         : ").append(conf.allowForeignKeyDeferrable()).append("\n");

        return b.toString();
    }
}
