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

import com.pyx4j.config.server.PropertiesConfiguration;

public class ConfigurationOracleProperties extends ConfigurationOracle {

    protected String host;

    protected int port = 1521;

    protected String sid;

    protected String user;

    protected String password;

    protected int minPoolSize = 3;

    protected int maxPoolSize = 15;

    protected int maxPoolPreparedStatement = 1000;

    protected int unreturnedConnectionTimeout = 60;

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
    public String userName() {
        return user;
    }

    @Override
    public String password() {
        return password;
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
    public int maxPoolPreparedStatements() {
        return maxPoolPreparedStatement;
    }

    @Override
    public int unreturnedConnectionTimeout() {
        return unreturnedConnectionTimeout;
    }

    public void readProperties(String prefix, Map<String, String> properties) {
        PropertiesConfiguration c = new PropertiesConfiguration(prefix, properties);
        this.host = c.getValue("host", this.host);
        this.port = c.getIntegerValue("port", this.port);
        this.sid = c.getValue("sid", this.sid);

        this.user = c.getValue("user", this.user);
        this.password = c.getValue("password", this.password);

        this.minPoolSize = c.getIntegerValue("minPoolSize", this.minPoolSize);
        this.maxPoolSize = c.getIntegerValue("maxPoolSize", this.maxPoolSize);
        this.maxPoolPreparedStatement = c.getIntegerValue("maxPoolPreparedStatement", this.maxPoolPreparedStatement);
        this.unreturnedConnectionTimeout = c.getIntegerValue("unreturnedConnectionTimeout", this.unreturnedConnectionTimeout);
    }

}
