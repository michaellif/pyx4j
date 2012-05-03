/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-18
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import javax.servlet.ServletContextEvent;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.dev.DevSession;
import com.pyx4j.quartz.SchedulerHelper;
import com.pyx4j.server.contexts.Lifecycle;

public class VistaInitializationServletContextListener extends com.pyx4j.entity.server.servlet.InitializationServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            super.contextInitialized(sce);
            Persistence.service();
            SchedulerHelper.init();
        } finally {
            Lifecycle.endContext();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            SchedulerHelper.shutdown();
            DevSession.cleanup();
        } finally {
            Lifecycle.endContext();
        }

        try {
            // Avoid Tomcat redeploy warnings
            Thread.sleep(1000);
        } catch (InterruptedException ignore) {
        }
        super.contextDestroyed(sce);
    }

}
