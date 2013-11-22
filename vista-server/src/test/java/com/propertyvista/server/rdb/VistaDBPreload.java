/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.rdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;
import com.pyx4j.server.mail.Mail;

import com.propertyvista.biz.system.VistaSystemFacade;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.server.config.VistaServerSideConfiguration;

public class VistaDBPreload {

    private static final Logger log = LoggerFactory.getLogger(VistaDBPreload.class);

    public static void main(String[] args) {
        log.info("Generating new Data...");
        long start = System.currentTimeMillis();
        VistaServerSideConfiguration conf = new VistaServerSideConfiguration();
        ServerSideConfiguration.setInstance(conf);
        NamespaceManager.setNamespace(VistaNamespace.demoNamespace);
        try {
            Lifecycle.startElevatedUserContext();
            Mail.getMailService().setDisabled(true);
            ServerSideFactory.create(VistaSystemFacade.class).setCommunicationsDisabled(true);
            log.info(conf.getDataPreloaders().preloadAll());
        } finally {
            ServerSideFactory.create(VistaSystemFacade.class).setCommunicationsDisabled(false);
            Mail.getMailService().setDisabled(false);
            Lifecycle.endElevatedUserContext();
        }
        log.info("Total time: " + TimeUtils.secSince(start));
    }
}
