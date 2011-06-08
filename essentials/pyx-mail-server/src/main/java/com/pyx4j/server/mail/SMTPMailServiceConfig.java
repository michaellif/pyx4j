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
 * Created on 2011-02-20
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.server.mail;

import java.util.Map;

import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.config.server.PropertiesConfiguration;

public class SMTPMailServiceConfig implements IMailServiceConfigConfiguration {

    protected String host;

    protected int port = 465;

    protected String user;

    protected String password;

    protected boolean starttls;

    protected boolean debug;

    protected String allowSendToEmailSufix;

    public SMTPMailServiceConfig() {
    }

    public SMTPMailServiceConfig(String host, int port, String user, String password) {
        super();
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public boolean isStarttls() {
        return starttls;
    }

    public boolean isDebug() {
        return debug;
    }

    public String getAllowSendToEmailSufix() {
        return allowSendToEmailSufix;
    }

    public void readProperties(String prefix, Map<String, String> properties) {
        PropertiesConfiguration c = new PropertiesConfiguration(prefix, properties);
        this.host = c.getValue("host", this.host);
        this.port = c.getIntegerValue("port", this.port);
        this.starttls = c.getBooleanValue("starttls", this.starttls);
        this.allowSendToEmailSufix = c.getValue("allowSendToEmailSufix", this.allowSendToEmailSufix);

        this.user = c.getValue("user", this.user);
        this.password = c.getValue("password", this.password);
    }
}
