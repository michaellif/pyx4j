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

import com.pyx4j.entity.server.impl.EntityImplGenerator;

public class InitializationServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        EntityImplGenerator.generate();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
