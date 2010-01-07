/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 6, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.impl.EntityImplGenerator;
import com.pyx4j.entity.shared.EntityFactory;

public class InitializationServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            EntityFactory.setImplementation(new ServerEntityFactory());
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
