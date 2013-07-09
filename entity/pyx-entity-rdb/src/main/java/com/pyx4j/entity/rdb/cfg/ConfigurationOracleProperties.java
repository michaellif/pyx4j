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
 * Created on 2011-06-08
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.cfg;

import java.util.Map;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.PropertiesConfiguration;

public class ConfigurationOracleProperties extends ConfigurationOracle {

    protected ConfigurationProperties properties;

    protected String tnsName;

    protected String serviceName;

    protected String sid;

    public ConfigurationOracleProperties() {
        properties = new ConfigurationProperties();
        properties.port = 1521;
        properties.forceQualifiedNames = true;
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
        return sid;
    }

    @Override
    public String serviceName() {
        return serviceName;
    }

    @Override
    public String tnsName() {
        return tnsName;
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

        this.sid = c.getValue("sid", this.sid);
        this.tnsName = c.getValue("tnsName", this.tnsName);
        this.serviceName = c.getValue("serviceName", this.serviceName);

        this.properties.readProperties(c);
    }

}
