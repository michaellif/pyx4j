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

import java.util.List;

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

    public static enum EnvironmentType {
        LocalJVM, GAEDevelopment, GAESandbox
    }

    public static final ServerSideConfiguration instance() {
        // Fall back for Tests
        if (ServerSideConfiguration.instance == null) {
            instance = new ServerSideConfiguration();
        }
        return instance;
    }

    public static final void setInstance(ServerSideConfiguration instance) {
        if (ServerSideConfiguration.instance != null) {
            throw new Error("Can't redefine ServerSideConfiguration");
        }
        ServerSideConfiguration.instance = instance;
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

    public String getApplicationEmailSender() {
        return "\"Pyx Software Services Inc\" <skarzhevskyy@gmail.com>";
    }

    public IPersistenceConfiguration getPersistenceConfiguration() {
        return null;
    }

    public boolean datastoreReadOnly() {
        return false;
    }

    public EnvironmentType getEnvironmentType() {
        SecurityManager sm = System.getSecurityManager();
        if (sm == null) {
            return EnvironmentType.LocalJVM;
        } else if (sm.getClass().getName().startsWith("com.google.appengine.tools.development")) {
            return EnvironmentType.GAEDevelopment;
        } else if (sm.getClass().getName().startsWith("com.google.apphosting.")) {
            return EnvironmentType.GAESandbox;
        } else {
            return EnvironmentType.LocalJVM;
        }
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

}
