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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.impl.EntityImplGenerator;
import com.pyx4j.entity.shared.EntityFactory;

public class InitializationServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            EntityFactory.setImplementation(new ServerEntityFactory());

            String configClass = sce.getServletContext().getInitParameter(ServerSideConfiguration.class.getName());
            if (CommonsStringUtils.isStringSet(configClass)) {
                try {
                    ServerSideConfiguration.setInstance((ServerSideConfiguration) Class.forName(configClass).newInstance());
                } catch (Throwable e) {
                    Logger log = LoggerFactory.getLogger(InitializationServletContextListener.class);
                    log.error("ServerSideConfiguration creation error", e);
                    throw new ServletException("ServerSideConfiguration not avalable");
                }
            } else {
                ServerSideConfiguration.setInstance(new ServerSideConfiguration());
            }

            EntityImplGenerator.generate();
        } catch (Throwable e) {
            Logger log = LoggerFactory.getLogger(InitializationServletContextListener.class);
            log.error("initialization error", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
