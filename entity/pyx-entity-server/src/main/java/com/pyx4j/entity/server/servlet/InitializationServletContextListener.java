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
 * @version $Id$
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

/**
 * System property "com.pyx4j.appConfig" defines Config suffix class to use.
 * 
 * contextName in <context-param> can redefine what configuration to use if
 * ServerSideConfiguration.selectInstanceByContextName is overriden
 * 
 */
public class InitializationServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            String configClass = sce.getServletContext().getInitParameter(ServerSideConfiguration.class.getName());
            if (CommonsStringUtils.isStringSet(configClass)) {
                try {
                    configClass += System.getProperty("com.pyx4j.appConfig", "");
                    ServletContext servletContext = sce.getServletContext();

                    ServerSideConfiguration defaultConfig = (ServerSideConfiguration) Class.forName(configClass).newInstance();
                    ServerSideConfiguration.setInstance(defaultConfig.selectInstanceByContextName(servletContext, getContextName(servletContext)));

                    Logger log = LoggerFactory.getLogger(InitializationServletContextListener.class);
                    log.debug("ServerInfo", servletContext.getServerInfo());
                    log.debug("Java Servlet API", servletContext.getMajorVersion(), servletContext.getMinorVersion());
                    log.debug("ServletContext", servletContext.getContextPath(), servletContext.getServletContextName());

                } catch (Throwable e) {
                    Logger log = LoggerFactory.getLogger(InitializationServletContextListener.class);
                    log.error("ServerSideConfiguration creation error", e);
                    throw new ServletException("ServerSideConfiguration not avalable");
                }
            } else {
                ServerSideConfiguration.setInstance(new ServerSideConfiguration());
            }

            EntityImplGenerator.generate(ServerSideConfiguration.instance().getEnvironmentType() == EnvironmentType.GAESandbox);
        } catch (Throwable e) {
            Logger log = LoggerFactory.getLogger(InitializationServletContextListener.class);
            log.error("initialization error", e);
        }
    }

    protected String getContextName(ServletContext servletContext) {
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

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        PersistenceServicesFactory.deregister();
        EntityImplGenerator.release();
    }

}
