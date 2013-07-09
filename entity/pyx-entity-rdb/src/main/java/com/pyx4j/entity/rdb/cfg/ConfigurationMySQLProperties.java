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

import java.util.Map;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.PropertiesConfiguration;

public class ConfigurationMySQLProperties extends ConfigurationMySQL {

    protected ConfigurationProperties properties;

    protected String db;

    protected boolean autoReconnect;

    public ConfigurationMySQLProperties() {
        properties = new ConfigurationProperties();
        properties.port = 3306;
        properties.host = "localhost";
    }

    @Override
    public String connectionUrl() {
        if (CommonsStringUtils.isStringSet(properties.jdbcConnectionUrl)) {
            return properties.jdbcConnectionUrl;
        } else {
            return super.connectionUrl();
        }
    }

    @Override
    public String dbHost() {
        return properties.host;
    }

    @Override
    public int dbPort() {
        return properties.port;
    }

    @Override
    public String dbName() {
        return db;
    }

    @Override
    public String userName() {
        return properties.user;
    }

    @Override
    public String password() {
        return properties.password;
    }

    @Override
    public String dbAdministrationUserName() {
        return properties.dbAdministrationUserName();
    }

    @Override
    public String dbAdministrationPassword() {
        return properties.dbAdministrationPassword();
    }

    @Override
    public MultitenancyType getMultitenancyType() {
        return properties.multitenant;
    }

    @Override
    public boolean createForeignKeys() {
        return properties.createForeignKeys;
    }

    @Override
    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    @Override
    public ConnectionPoolConfiguration connectionPoolConfiguration(ConnectionPoolType connectionType) {
        return properties.connectionPoolConfiguration(connectionType);
    }

    @Override
    public int tablesIdentityOffset() {
        return properties.tablesIdentityOffset;
    }

    @Override
    public Ddl ddl() {
        return properties.ddl;
    }

    @Override
    public boolean forceQualifiedNames() {
        return properties.forceQualifiedNames;
    }

    @Override
    public String tablesSchema() {
        if (properties.tablesSchema == null) {
            return userName();
        } else {
            return properties.tablesSchema;
        }
    }

    @Override
    public String tablesCreateOption() {
        return properties.tablesCreateOption;
    }

    public void readProperties(String prefix, Map<String, String> properties) {
        PropertiesConfiguration c = new PropertiesConfiguration(prefix, properties);

        this.db = c.getValue("db", this.db);
        this.autoReconnect = c.getBooleanValue("autoReconnect", this.autoReconnect);

        this.properties.readProperties(c);
    }

}
