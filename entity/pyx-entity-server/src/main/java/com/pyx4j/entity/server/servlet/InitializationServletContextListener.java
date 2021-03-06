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
 * Created on Jan 6, 2010
 * @author vlads
 */
package com.pyx4j.entity.server.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration.EnvironmentType;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.impl.EntityImplGenerator;
import com.pyx4j.log4j.LoggerConfig;

/**
 * System property "com.pyx4j.appConfig" defines Config suffix for class to use.
 *
 * contextName in <context-param> of web.xml can redefine what configuration to use if
 * ServerSideConfiguration.selectInstanceByContextName is overridden
 *
 */
public class InitializationServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            ServletContext servletContext = sce.getServletContext();
            // Old web.xml not used in modern applications
            if (!ServerSideConfiguration.isInitialized()) {
                LoggerConfig.setContextName(ServerSideConfiguration.getContextName(servletContext));
                String configClassName = sce.getServletContext().getInitParameter(ServerSideConfiguration.class.getName());
                if (CommonsStringUtils.isStringSet(configClassName)) {
                    Class<ServerSideConfiguration> serverSideConfigurationClass;
                    try {
                        @SuppressWarnings("unchecked")
                        Class<ServerSideConfiguration> configClass = (Class<ServerSideConfiguration>) Class.forName(configClassName);
                        serverSideConfigurationClass = configClass;
                    } catch (Throwable e) {
                        Logger log = LoggerFactory.getLogger(InitializationServletContextListener.class);
                        log.error("ServerSideConfiguration creation error", e);
                        throw new ServletException("ServerSideConfiguration not available");
                    }
                    ServerSideConfiguration.initialize(servletContext, serverSideConfigurationClass);
                } else {
                    ServerSideConfiguration.setInstance(new ServerSideConfiguration());
                }
            }

            ServerSideConfiguration.instance().configurationInstanceSelected(servletContext);

            Logger log = LoggerFactory.getLogger(InitializationServletContextListener.class);
            log.debug("ServerInfo {}", servletContext.getServerInfo());
            log.debug("Java Servlet API {} {}", servletContext.getMajorVersion(), servletContext.getMinorVersion());
            log.debug("ServletContext {} {}", servletContext.getContextPath(), servletContext.getServletContextName());
            log.debug("ServerSideConfiguration {}", ServerSideConfiguration.instance().getClass());
            ServerSideConfiguration.logSystemProperties();

            EntityImplGenerator.generate(ServerSideConfiguration.instance().getEnvironmentType() == EnvironmentType.GAESandbox);
        } catch (Throwable e) {
            Logger log = LoggerFactory.getLogger(InitializationServletContextListener.class);
            log.error("initialization error", e);
            throw new Error("Application initialization error", e);
        }
    }

    public static String getContextName(ServletContext servletContext) {
        return ServerSideConfiguration.getContextName(servletContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        PersistenceServicesFactory.deregister();
        EntityImplGenerator.release();
        Logger log = LoggerFactory.getLogger(InitializationServletContextListener.class);
        log.info("contextDestroyed");
        LoggerConfig.shutdown();
    }
}
