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

import java.util.Map;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.PropertiesConfiguration;

public class ConfigurationPostgreSQLProperties extends ConfigurationPostgreSQL {

    protected ConfigurationProperties properties;

    protected String db;

    public ConfigurationPostgreSQLProperties() {
        properties = new ConfigurationProperties();
        properties.port = 5432;
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
    public ConnectionPoolConfiguration connectionPoolConfiguration(ConnectionPoolType connectionType) {
        return properties.connectionPoolConfiguration(connectionType);
    }

    @Override
    public int tablesIdentityOffset() {
        return properties.tablesIdentityOffset;
    }

    @Override
    public String tableCreateOption(String entityShortName) {
        return properties.tableCreateOptions.get(entityShortName);
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

    @Override
    public Integer tableIdentityOffset(String entityShortName) {
        return properties.tableIdentityOffset.get(entityShortName);
    }

    @Override
    public String sharedSequencesSchema() {
        return properties.sharedSequencesSchema;
    }

    @Override
    public boolean createForeignKeys() {
        return properties.createForeignKeys;
    }

    @Override
    public boolean allowForeignKeyDeferrable() {
        return properties.allowForeignKeyDeferrable;
    }

    public void readProperties(String prefix, Map<String, String> properties) {
        PropertiesConfiguration c = new PropertiesConfiguration(prefix, properties);

        this.db = c.getValue("db", this.db);

        this.properties.readProperties(c);
    }

}
