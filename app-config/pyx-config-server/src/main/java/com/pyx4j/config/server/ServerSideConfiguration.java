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
 */
package com.pyx4j.config.server;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.rpc.IServiceFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.security.server.AclRevalidator;
import com.pyx4j.security.server.ThrottleConfig;
import com.pyx4j.security.shared.AclCreator;

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

    private Integer overrideSessionMaxInactiveInterval;

    private static Boolean isStartedUnderEclipse;

    private static Boolean jvmDebugMode;

    private static Boolean isUnitTest;

    private static final long startTime = System.currentTimeMillis();

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

    /**
     * Convenience method to access custom ServerSideConfiguration
     */
    @SuppressWarnings("unchecked")
    public static <E extends ServerSideConfiguration> E instance(Class<E> serverSideConfigurationClass) {
        ServerSideConfiguration i = instance();
        if (!serverSideConfigurationClass.isAssignableFrom(i.getClass())) {
            throw new Error(i.getClass().getName() + " cannot be cast to " + serverSideConfigurationClass.getName(), instanceDefinedFrom);
        }
        return (E) i;
    }

    public static final void setInstance(ServerSideConfiguration instance) {
        if (ServerSideConfiguration.instance != null) {
            if (ServerSideConfiguration.instance == instance) {
                return;
            }
            if (!ServerSideConfiguration.isStartedUnderUnitTest()) {
                throw new Error("Can't redefine ServerSideConfiguration", instanceDefinedFrom);
            }
        }
        ServerSideConfiguration.instance = instance;
        // Initialize the UnitTest detection
        ServerSideConfiguration.isStartedUnderUnitTest();
        instanceDefinedFrom = new Throwable("ServerSideConfiguration initialized from");
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    public static final <E extends ServerSideConfiguration> E initialize(ServletContext servletContext, Class<E> serverSideConfigurationClass) {
        ServerSideConfiguration defaultApplicationConfig;
        String configClassSuffix = System.getProperty("com.pyx4j.appConfig");

        try {
            if (configClassSuffix == null) {
                defaultApplicationConfig = serverSideConfigurationClass.newInstance();
            } else {
                String configClass = serverSideConfigurationClass.getName() + configClassSuffix;
                defaultApplicationConfig = (ServerSideConfiguration) Class.forName(configClass).newInstance();
            }
            ServerSideConfiguration selectedConfig = defaultApplicationConfig.selectInstanceByContextName(servletContext, getContextName(servletContext));
            ServerSideConfiguration.setInstance(selectedConfig);
        } catch (Throwable e) {
            Logger log = LoggerFactory.getLogger(ServerSideConfiguration.class);
            log.error("ServerSideConfiguration creation error", e);
            throw new Error("ServerSideConfiguration not available");
        }

        return instance(serverSideConfigurationClass);
    }

    public static String getContextName(ServletContext servletContext) {
        // Can define this in web.xml
        String configContextName = servletContext.getInitParameter("contextName");
        if (CommonsStringUtils.isStringSet(configContextName)) {
            return configContextName;
        }
        // Version 2.5
        configContextName = servletContext.getContextPath();
        if (CommonsStringUtils.isStringSet(configContextName)) {
            int idx = configContextName.lastIndexOf('/');
            if (idx != -1) {
                return configContextName.substring(idx + 1);
            } else {
                System.err.println("WARN unexpected context path [" + configContextName + "]");
            }
        }
        return null;
    }

    public static long getStartTime() {
        return startTime;
    }

    public ServerSideConfiguration selectInstanceByContextName(ServletContext servletContext, String contextName) {
        return this;
    }

    public void configurationInstanceSelected(ServletContext servletContext) {

    }

    public boolean isDevelopmentBehavior() {
        return true;
    }

    public boolean isDemoBehavior() {
        return false;
    }

    public boolean isQaBehavior() {
        return false;
    }

    public boolean isProductionBackend() {
        return false;
    }

    public boolean allowDataDump() {
        return isDevelopmentBehavior();
    }

    /**
     * Enable temporary to allow hosted mode on production environment.
     */
    public boolean allowToBypassRpcServiceManifest() {
        return isStartedUnderUnitTest();
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

    public boolean strictDataModelPermissions() {
        return false;
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
        return new EmptyNamespaceDataResolver();
    }

    public LocaleResolver getLocaleResolver() {
        return null;
    }

    public ThrottleConfig getThrottleConfig() {
        return new ThrottleConfig();
    }

    public boolean useAppengineGoogleAccounts() {
        return false;
    }

    public String getApplicationName() {
        return null;
    }

    public String getEnviromentName() {
        return null;
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

    public Collection<LifecycleListener> getLifecycleListeners() {
        return Collections.emptyList();
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
        if (isStartedUnderEclipse == null) {
            if (Boolean.valueOf(System.getProperty("com.pyx4j.EclipseDeveloperEnviroment"))) {
                isStartedUnderEclipse = true;
            } else {
                StackTraceElement[] ste = new Throwable().getStackTrace();
                String firstRunnableClass = (ste[ste.length - 1]).getClassName();
                isStartedUnderEclipse = firstRunnableClass.startsWith("org.eclipse.jdt") || firstRunnableClass.startsWith("org.eclipse.jetty")
                        || firstRunnableClass.contains(".server.jetty.");
            }
        }
        return isStartedUnderEclipse;
    }

    public static boolean isStartedUnderUnitTest() {
        if (isUnitTest == null) {
            StackTraceElement[] ste = new Throwable().getStackTrace();
            for (int i = ste.length - 1; i > 2; i--) {
                if (ste[i].getClassName().startsWith("org.junit.") || ste[i].getClassName().startsWith("junit.")) {
                    isUnitTest = true;
                    break;
                }
            }
            if (isUnitTest == null) {
                isUnitTest = false;
            }
        }
        return isUnitTest;
    }

    /**
     * Running in Continuous Integration on Build server
     */
    public static boolean isRunningInCI() {
        return (System.getProperty("bamboo.buildNumber") != null);
    }

    public static boolean isStartedUnderJvmDebugMode() {
        if (jvmDebugMode == null) {
            try {
                jvmDebugMode = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
            } catch (Throwable e) {
                // AccessControlException: "java.lang.management.ManagementPermission" "monitor"
                jvmDebugMode = false;
            }
        }
        return jvmDebugMode;
    }

    /**
     * @return true when Web Application started in Eclipse development environment using our JettyLaunch
     */
    public static boolean isRunningInDeveloperEnviroment() {
        return ApplicationMode.isDevelopment() && Boolean.valueOf(System.getProperty("com.pyx4j.DeveloperEnviroment"));
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

    /**
     *
     * Specifies the time, in seconds, between client requests before the servlet container will invalidate UI session.
     *
     * An <tt>interval</tt> value of zero or less indicates that the session should never timeout.
     *
     * null indicates the default web.xml settings are taken
     */
    public Integer getOverrideSessionMaxInactiveInterval() {
        return overrideSessionMaxInactiveInterval;
    }

    public void setOverrideSessionMaxInactiveInterval(Integer overrideSessionMaxInactiveInterval) {
        this.overrideSessionMaxInactiveInterval = overrideSessionMaxInactiveInterval;
    }

    public String getDevelopmentSessionCookieName() {
        return "pyx_dev_access";
    }

    public String getDevelopmentSessionCookieDomain() {
        return null;
    }

    public PropertiesConfiguration getConfigProperties() {
        return new PropertiesConfiguration(Collections.<String, String> emptyMap());
    }

    public boolean isNetworkSimulationAvailable() {
        return getConfigProperties().getBooleanValue("networkSimulation.available", false);
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
