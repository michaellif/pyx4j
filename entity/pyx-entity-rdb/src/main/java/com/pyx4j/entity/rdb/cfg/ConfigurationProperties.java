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
 * Created on 2013-01-03
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.cfg;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.PropertiesConfiguration;
import com.pyx4j.entity.rdb.cfg.Configuration.Ddl;
import com.pyx4j.entity.rdb.cfg.Configuration.MultitenancyType;

public class ConfigurationProperties {

    public String jdbcConnectionUrl;

    public String host;

    public int port;

    public String user;

    public String password;

    private String dbAdministrationUserName;

    private String dbAdministrationPassword;

    public int initialPoolSize = 1;

    public int minPoolSize = 3;

    public int maxPoolSize = 15;

    public int initialBackgroundProcessPoolSize = 1;

    public int minBackgroundProcessPoolSize = 2;

    public int maxBackgroundProcessPoolSize = 40;

    public int maxPoolPreparedStatements = 1000;

    public int unreturnedConnectionTimeout = 60;

    public int unreturnedConnectionBackgroundProcessTimeout = 60 * 60;

    public int tablesIdentityOffset;

    public boolean createForeignKeys = true;

    public boolean allowForeignKeyDeferrable = false;

    public MultitenancyType multitenant = MultitenancyType.SingleTenant;

    public String sharedSequencesSchema = null;

    public boolean forceQualifiedNames = false;

    public String tablesSchema = null;

    public Ddl ddl = Ddl.auto;

    public void readProperties(PropertiesConfiguration c) {

        this.jdbcConnectionUrl = c.getValue("jdbcConnectionUrl", this.jdbcConnectionUrl);

        this.host = c.getValue("host", this.host);
        this.port = c.getIntegerValue("port", this.port);

        this.user = c.getValue("user", this.user);
        this.password = c.getValue("password", this.password);

        this.dbAdministrationUserName = c.getValue("dbAdministrationUserName", this.user);
        this.dbAdministrationPassword = c.getValue("dbAdministrationPassword", this.password);

        this.multitenant = c.getEnumValue("multitenant", MultitenancyType.class, this.multitenant);
        this.sharedSequencesSchema = c.getValue("sharedSequencesSchema", this.sharedSequencesSchema);

        this.createForeignKeys = c.getBooleanValue("createForeignKeys", this.createForeignKeys);
        this.allowForeignKeyDeferrable = c.getBooleanValue("allowForeignKeyDeferrable", this.allowForeignKeyDeferrable);
        this.forceQualifiedNames = c.getBooleanValue("forceQualifiedNames", this.forceQualifiedNames);
        this.tablesSchema = c.getValue("tablesSchema", this.tablesSchema);

        this.initialPoolSize = c.getIntegerValue("initialPoolSize", this.initialPoolSize);
        this.minPoolSize = c.getIntegerValue("minPoolSize", this.minPoolSize);
        this.maxPoolSize = c.getIntegerValue("maxPoolSize", this.maxPoolSize);
        this.maxPoolPreparedStatements = c.getIntegerValue("maxPoolPreparedStatements", this.maxPoolPreparedStatements);
        this.unreturnedConnectionTimeout = c.getIntegerValue("unreturnedConnectionTimeout", this.unreturnedConnectionTimeout);

        this.initialBackgroundProcessPoolSize = c.getIntegerValue("initialBackgroundProcessPoolSize", this.initialBackgroundProcessPoolSize);
        this.minBackgroundProcessPoolSize = c.getIntegerValue("minBackgroundProcessPoolSize", this.minBackgroundProcessPoolSize);
        this.maxBackgroundProcessPoolSize = c.getIntegerValue("maxBackgroundProcessPoolSize", this.maxBackgroundProcessPoolSize);
        this.unreturnedConnectionBackgroundProcessTimeout = c.getIntegerValue("unreturnedConnectionBackgroundProcessTimeout",
                this.unreturnedConnectionBackgroundProcessTimeout);

        this.tablesIdentityOffset = c.getIntegerValue("tablesIdentityOffset", this.tablesIdentityOffset);
        this.ddl = c.getEnumValue("ddl", Ddl.class, ddl);
    }

    public String dbAdministrationUserName() {
        if (CommonsStringUtils.isStringSet(dbAdministrationUserName)) {
            return dbAdministrationUserName;
        } else {
            return user;
        }
    }

    public String dbAdministrationPassword() {
        if (CommonsStringUtils.isStringSet(dbAdministrationPassword)) {
            return dbAdministrationPassword;
        } else {
            return password;
        }
    }
}
