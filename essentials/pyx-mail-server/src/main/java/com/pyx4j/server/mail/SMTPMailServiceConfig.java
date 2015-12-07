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
 */
package com.pyx4j.server.mail;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.server.IMailServiceConfigConfiguration;
import com.pyx4j.config.server.PropertiesConfiguration;
import com.pyx4j.entity.shared.AbstractOutgoingMailQueue;

public abstract class SMTPMailServiceConfig implements IMailServiceConfigConfiguration {

    protected String host;

    protected int port = 465;

    protected String sender;

    protected String user;

    protected String password;

    protected boolean starttls;

    protected boolean debug;

    protected int maxDeliveryAttempts = 40;

    protected int queuePriority = 0;

    protected String allowSendToEmailSufix;

    protected String blockedMailForwardTo;

    protected String forwardAllTo;

    //Socket connection timeout value in milliseconds. This timeout is implemented by java.net.Socket. -1 is infinite timeout.
    protected int connectionTimeout = 90 * Consts.SEC2MSEC;

    //Socket read timeout value in milliseconds. This timeout is implemented by java.net.Socket. -1  is infinite timeout.
    protected int timeout = 90 * Consts.SEC2MSEC;

    //see https://javamail.java.net/nonav/docs/api/com/sun/mail/smtp/package-summary.html
    // This is overridden by predefined properties we have in this config
    protected Map<String, String> properties;

    protected Map<String, String> headers;

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

    public String getSender() {
        return sender;
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

    public Class<? extends AbstractOutgoingMailQueue> persistableQueueEntityClass() {
        return null;
    }

    @Override
    public int maxDeliveryAttempts() {
        return maxDeliveryAttempts;
    }

    @Override
    public int queuePriority() {
        return queuePriority;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public Set<Map.Entry<String, String>> getProperties() {
        if (properties == null) {
            return Collections.emptySet();
        }
        return properties.entrySet();
    }

    public boolean isDebug() {
        return debug;
    }

    public String getAllowSendToEmailSufix() {
        return allowSendToEmailSufix;
    }

    public String getForwardAllTo() {
        return forwardAllTo;
    }

    // Added to retrieve default value when getForwardAllTo is overridden
    public final String getDefaultForwardAllTo() {
        return forwardAllTo;
    }

    public String getBlockedMailForwardTo() {
        return blockedMailForwardTo;
    }

    public void setBlockedMailForwardTo(String blockedMailForwardTo) {
        this.blockedMailForwardTo = blockedMailForwardTo;
    }

    public Set<Map.Entry<String, String>> getHeaders() {
        if (headers == null) {
            return Collections.emptySet();
        }
        return headers.entrySet();
    }

    // Simplified solution callback to enable different email providers for different email types.
    public SMTPMailServiceConfig selectConfigurationInstance(MailMessage mailMessage) {
        return this;
    }

    public void readProperties(String prefix, Map<String, String> properties) {
        PropertiesConfiguration c = new PropertiesConfiguration(prefix, properties);
        this.host = c.getValue("host", this.host);
        this.port = c.getIntegerValue("port", this.port);
        this.starttls = c.getBooleanValue("starttls", this.starttls);

        this.sender = c.getValue("sender", this.sender);

        this.headers = c.getValues("headers");

        this.maxDeliveryAttempts = c.getIntegerValue("maxDeliveryAttempts", this.maxDeliveryAttempts);
        this.queuePriority = c.getIntegerValue("queuePriority", this.queuePriority);

        this.allowSendToEmailSufix = c.getValue("allowSendToEmailSufix", this.allowSendToEmailSufix);
        this.blockedMailForwardTo = c.getValue("blockedMailForwardTo", this.blockedMailForwardTo);
        this.forwardAllTo = c.getValue("forwardAllTo", this.forwardAllTo);

        this.user = c.getValue("user", this.user);
        this.password = c.getValue("password", this.password);

        this.debug = c.getBooleanValue("debug", this.debug);

        this.connectionTimeout = c.getIntegerValue("smtp.connectiontimeout", this.connectionTimeout);
        this.timeout = c.getIntegerValue("smtp.timeout", this.timeout);
        this.properties = c.getValues("properties");
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("configurationClass                                : ").append(this.getClass().getName()).append("\n");
        b.append("configurationId                                   : ").append(configurationId()).append("\n");
        b.append("host                                              : ").append(getHost()).append("\n");
        b.append("sender                                            : ").append(getSender()).append("\n");
        b.append("port                                              : ").append(getPort()).append("\n");
        b.append("starttls                                          : ").append(isStarttls()).append("\n");
        b.append("user                                              : ").append(getUser()).append("\n");
        b.append("allowSendToEmailSufix                             : ").append(getAllowSendToEmailSufix()).append("\n");
        b.append("blockedMailForwardTo                              : ").append(getBlockedMailForwardTo()).append("\n");
        b.append("forwardAllTo                                      : ").append(this.forwardAllTo).append("\n");
        b.append("forwardAllTo (active)                             : ").append(getForwardAllTo()).append("\n");
        b.append("maxDeliveryAttempts                               : ").append(maxDeliveryAttempts()).append("\n");
        b.append("queuePriority                                     : ").append(queuePriority()).append("\n");
        b.append("debug                                             : ").append(isDebug()).append("\n");
        b.append("smtp.connectionTimeout                            : ").append(getConnectionTimeout()).append("\n");
        b.append("smtp.timeout                                      : ").append(getTimeout()).append("\n");
        b.append("headers                                           : ").append(getHeaders()).append("\n");
        b.append("properties                                        : ").append(getProperties()).append("\n");

        return b.toString();
    }
}
