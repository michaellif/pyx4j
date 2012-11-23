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

    protected String jdbcConnectionUrl;

    protected String tnsName;

    protected String host;

    protected int port = 1521;

    protected String serviceName;

    protected String sid;

    protected String user;

    protected String password;

    protected String dbAdministrationUserName;

    protected String dbAdministrationPassword;

    protected int initialPoolSize = 1;

    protected int minPoolSize = 3;

    protected int maxPoolSize = 15;

    protected int initialBackgroundProcessPoolSize = 1;

    protected int minBackgroundProcessPoolSize = 2;

    protected int maxBackgroundProcessPoolSize = 40;

    protected int maxPoolPreparedStatements = 1000;

    protected int unreturnedConnectionTimeout = 60;

    protected int unreturnedConnectionBackgroundProcessTimeout = 60 * 60;

    private int tablesIdentityOffset;

    private boolean createForeignKeys = true;

    private Ddl ddl = Ddl.auto;

    @Override
    public String connectionUrl() {
        if (CommonsStringUtils.isStringSet(jdbcConnectionUrl)) {
            return jdbcConnectionUrl;
        } else {
            return super.connectionUrl();
        }
    }

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
    public boolean createForeignKeys() {
        return createForeignKeys;
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
    public int tablesIdentityOffset() {
        return tablesIdentityOffset;
    }

    @Override
    public Ddl ddl() {
        return ddl;
    }

    public void readProperties(String prefix, Map<String, String> properties) {
        PropertiesConfiguration c = new PropertiesConfiguration(prefix, properties);

        this.jdbcConnectionUrl = c.getValue("jdbcConnectionUrl", this.jdbcConnectionUrl);

        this.host = c.getValue("host", this.host);
        this.port = c.getIntegerValue("port", this.port);
        this.sid = c.getValue("sid", this.sid);
        this.tnsName = c.getValue("tnsName", this.tnsName);
        this.serviceName = c.getValue("serviceName", this.serviceName);

        this.user = c.getValue("user", this.user);
        this.password = c.getValue("password", this.password);

        this.dbAdministrationUserName = c.getValue("dbAdministrationUserName", this.user);
        this.dbAdministrationPassword = c.getValue("dbAdministrationPassword", this.password);

        this.createForeignKeys = c.getBooleanValue("createForeignKeys", this.createForeignKeys);

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

}
