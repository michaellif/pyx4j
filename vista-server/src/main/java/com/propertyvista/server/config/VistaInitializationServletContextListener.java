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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.quartz.SchedulerHelper;
import com.pyx4j.server.contexts.DevSession;
import com.pyx4j.server.contexts.Lifecycle;

import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.security.AuditRecordEventType;
import com.propertyvista.operations.server.proc.PmcProcessMonitor;
import com.propertyvista.sshd.InterfaceSSHDServer;

public class VistaInitializationServletContextListener extends com.pyx4j.entity.server.servlet.InitializationServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        super.contextInitialized(sce);
        try {
            Persistence.service();
            ServerSideFactory.create(AuditFacade.class).record(AuditRecordEventType.System, null, "System Start");

            ServerSideFactory.create(PasswordEncryptorFacade.class).activateDecryption();
            SchedulerHelper.init();
            SchedulerHelper.setActive(!VistaDeployment.isVistaStaging());
            InterfaceSSHDServer.init();
        } catch (Throwable e) {
            Logger log = LoggerFactory.getLogger(VistaInitializationServletContextListener.class);
            log.error("VistaServer initialization error", e);
            throw new Error("VistaServer initialization error", e);
        } finally {
            Lifecycle.endContext();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServerSideFactory.create(AuditFacade.class).record(AuditRecordEventType.System, null, "System Shutdown");
        try {
            InterfaceSSHDServer.shutdown();
            PmcProcessMonitor.shutdown();
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
