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
 * Created on Jan 19, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.config.server;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.rpc.IServiceFactory;
import com.pyx4j.security.server.ThrottleConfig;
import com.pyx4j.security.shared.AclCreator;
import com.pyx4j.security.shared.AclRevalidator;

/**
 * All methods can return null to use default implementation in framework.
 * 
 * This is the main configuration class you should override in application.
 * 
 * Example web.xml
 * 
 * <pre>
 * <web-app>
 *   ....
 *  <context-param>
 *      <param-name>com.pyx4j.config.server.ServerSideConfiguration</param-name>
 *      <param-value>com.mycorp.server.MyServerSideConfiguration</param-value>
 *  </context-param>
 *   ....
 * </pre>
 */
public class ServerSideConfiguration {

    private static ServerSideConfiguration instance;

    private static Throwable instanceDefinedFrom;

    private EnvironmentType environmentType;

    public static enum EnvironmentType {
        LocalJVM, GAEDevelopment, GAESandbox
    }

    public static final ServerSideConfiguration instance() {
        // Fall back for Tests
        if (ServerSideConfiguration.instance == null) {
            instance = new ServerSideConfiguration();
            instanceDefinedFrom = new Throwable("ServerSideConfiguration initialized from");
        }
        return instance;
    }

    public static final void setInstance(ServerSideConfiguration instance) {
        if (ServerSideConfiguration.instance != null) {
            if (ServerSideConfiguration.instance != instance) {
                return;
            }
            throw new Error("Can't redefine ServerSideConfiguration", instanceDefinedFrom);
        }
        ServerSideConfiguration.instance = instance;
        instanceDefinedFrom = new Throwable("ServerSideConfiguration initialized from");
    }

    public ServerSideConfiguration selectInstanceByContextName(ServletContext servletContext, String contextName) {
        return this;
    }

    public boolean isDevelopmentBehavior() {
        return true;
    }

    public IServiceFactory getRPCServiceFactory() {
        return null;
    }

    public IServiceFactory getJ2SEServiceFactory() {
        return null;
    }

    /**
     * @return empty List to avoid Entity Implementations creation.
     */
    public List<String> findEntityClasses() {
        return null;
    }

    /**
     * Allow to share GAE DB between applications. The same as @Table(prefix = "app1")
     * affects all Entity saved by application.
     * 
     * @return non null value to prefix all table names in DB
     */
    public String persistenceNamePrefix() {
        return null;
    }

    /**
     * Default is Allow All Access Control List
     */
    public AclCreator getAclCreator() {
        return null;
    }

    public AclRevalidator getAclRevalidator() {
        return null;
    }

    public NamespaceResolver getNamespaceResolver() {
        return new EmptyNamespaceResolver();
    }

    public ThrottleConfig getThrottleConfig() {
        return new ThrottleConfig();
    }

    public boolean useAppengineGoogleAccounts() {
        return false;
    }

    /**
     * May use appengine-web.xml to define system-properties or override this method
     */
    public String getMainApplicationURL() {
        return System.getProperty("com.pyx4j.appUrl");
    }

    /**
     * When HTTPD site is proxing requests by removing context.
     */
    public boolean isContextLessDeployment() {
        return false;
    }

    public String getApplicationEmailSender() {
        return "\"Pyx Software Services Inc\" <skarzhevskyy@gmail.com>";
    }

    public IPersistenceConfiguration getPersistenceConfiguration() {
        return null;
    }

    /**
     * Null assumes GAE e-mail delivery
     */
    public IMailServiceConfigConfiguration getMailServiceConfigConfiguration() {
        return null;
    }

    public boolean datastoreReadOnly() {
        return false;
    }

    public String getApplicationMaintenanceMessage() {
        return "Application is in read-only due to maintenance.";
    }

    public EnvironmentType getEnvironmentType() {
        if (environmentType != null) {
            return environmentType;
        }
        SecurityManager sm = System.getSecurityManager();
        if (sm == null) {
            environmentType = EnvironmentType.LocalJVM;
        } else if (sm.getClass().getName().startsWith("com.google.appengine.tools.development")) {
            environmentType = EnvironmentType.GAEDevelopment;
        } else if (sm.getClass().getName().startsWith("com.google.apphosting.")) {
            environmentType = EnvironmentType.GAESandbox;
        } else {
            environmentType = EnvironmentType.LocalJVM;
        }
        return environmentType;
    }

    /**
     * @return true when running in Eclipse development env. (Not from maven)
     */
    public static boolean isStartedUnderEclipse() {
        StackTraceElement[] ste = new Throwable().getStackTrace();
        String firstRunnableClass = (ste[ste.length - 1]).getClassName();
        return firstRunnableClass.startsWith("org.eclipse.jdt") || firstRunnableClass.startsWith("org.eclipse.jetty");
    }

    public String getSessionCookieName() {
        switch (getEnvironmentType()) {
        default:
        case LocalJVM:
            return System.getProperty("org.apache.catalina.SESSION_COOKIE_NAME", "JSESSIONID");
        case GAEDevelopment:
        case GAESandbox:
            return "JSESSIONID";
        }
    }

    public static String getSystemProperties() {
        StringBuffer sysProperties = new StringBuffer();
        Properties properties = System.getProperties();
        // Sort the list
        List<String> list2sort = new Vector<String>();
        int max_key = 0;
        for (Object keyObj : properties.keySet()) {
            String key = keyObj.toString();
            list2sort.add(key);
            int len = key.length();
            if (len > max_key) {
                max_key = len;
            }
        }
        Collections.sort(list2sort);
        if (max_key > 41) {
            max_key = 41;
        }

        for (String key : list2sort) {
            StringBuffer key_p = new StringBuffer(key);
            while (key_p.length() < max_key) {
                key_p.append(" ");
            }
            String value = (String) properties.get(key);
            if (value == null) {
                value = "{null}";
            }
            StringBuffer value_p = new StringBuffer(value);
            value_p.append("]");
            while (value_p.length() < 60) {
                value_p.append(" ");
            }
            sysProperties.append("         " + key_p.toString() + " = [" + value_p.toString() + "\n");
        }
        return sysProperties.toString();
    }

    public static void logSystemProperties() {
        LoggerFactory.getLogger(ServerSideConfiguration.class).debug("System Properties:\n" + getSystemProperties());
    }

}
