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

import com.pyx4j.config.server.PropertiesConfiguration;

public class ConfigurationMySQLProperties extends ConfigurationMySQL {

    protected String host = "localhost";

    protected int port = 3306;

    protected String db;

    protected String user;

    protected String password;

    protected String dbAdministrationUserName;

    protected String dbAdministrationPassword;

    protected MultitenancyType multitenant = MultitenancyType.SingleTenant;

    private boolean createForeignKeys = true;

    protected boolean autoReconnect;

    protected int initialPoolSize = 1;

    protected int minPoolSize = 3;

    protected int maxPoolSize = 15;

    protected int initialBackgroundProcessPoolSize = 1;

    protected int minBackgroundProcessPoolSize = 2;

    protected int maxBackgroundProcessPoolSize = 40;

    protected int maxPoolPreparedStatements = 1000;

    protected int unreturnedConnectionTimeout = 60;

    protected int unreturnedConnectionBackgroundProcessTimeout = 60 * 60;

    private int tablesItentityOffset;

    private Ddl ddl = Ddl.auto;

    @Override
    public String dbHost() {
        return host;
    }

    @Override
    public int dbPort() {
        return port;
    }

    @Override
    public String dbName() {
        return db;
    }

    @Override
    public String userName() {
        return user;
    }

    @Override
    public String password() {
        return password;
    }

    @Override
    public String dbAdministrationUserName() {
        return dbAdministrationUserName;
    }

    @Override
    public String dbAdministrationPassword() {
        return dbAdministrationPassword;
    }

    @Override
    public MultitenancyType getMultitenancyType() {
        return multitenant;
    }

    @Override
    public boolean createForeignKeys() {
        return createForeignKeys;
    }

    @Override
    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    @Override
    public int initialPoolSize() {
        return initialPoolSize;
    }

    @Override
    public int minPoolSize() {
        return minPoolSize;
    }

    @Override
    public int maxPoolSize() {
        return maxPoolSize;
    }

    @Override
    public int initialBackgroundProcessPoolSize() {
        return initialBackgroundProcessPoolSize;
    }

    @Override
    public int minBackgroundProcessPoolSize() {
        return minBackgroundProcessPoolSize;
    }

    @Override
    public int maxBackgroundProcessPoolSize() {
        return maxBackgroundProcessPoolSize;
    }

    @Override
    public int maxPoolPreparedStatements() {
        return maxPoolPreparedStatements;
    }

    @Override
    public int unreturnedConnectionTimeout() {
        return unreturnedConnectionTimeout;
    }

    @Override
    public int unreturnedConnectionBackgroundProcessTimeout() {
        return unreturnedConnectionBackgroundProcessTimeout;
    }

    @Override
    public int tablesItentityOffset() {
        return tablesItentityOffset;
    }

    @Override
    public Ddl ddl() {
        return ddl;
    }

    public void readProperties(String prefix, Map<String, String> properties) {
        PropertiesConfiguration c = new PropertiesConfiguration(prefix, properties);
        this.host = c.getValue("host", this.host);
        this.port = c.getIntegerValue("port", this.port);
        this.db = c.getValue("db", this.db);

        this.user = c.getValue("user", this.user);
        this.password = c.getValue("password", this.password);

        this.dbAdministrationUserName = c.getValue("dbAdministrationUserName", this.user);
        this.dbAdministrationPassword = c.getValue("dbAdministrationPassword", this.password);

        this.multitenant = c.getEnumValue("multitenant", MultitenancyType.class, this.multitenant);
        this.createForeignKeys = c.getBooleanValue("createForeignKeys", this.createForeignKeys);
        this.autoReconnect = c.getBooleanValue("autoReconnect", this.autoReconnect);

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

        this.tablesItentityOffset = c.getIntegerValue("tablesItentityOffset", this.tablesItentityOffset);
        this.ddl = c.getEnumValue("ddl", Ddl.class, ddl);
    }

}
