/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-21
 * @author matheszabi
 * @version $Id$
 */
package com.propertyvista.server.rdb;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.portal.server.preloader.CommunicationDevPreloader;
import com.propertyvista.server.config.VistaServerSideConfiguration;
import com.propertyvista.server.config.VistaServerSideConfigurationDev;
import com.propertyvista.server.config.VistaServerSideConfigurationDevPostgreSQL;

public class VistaDBCommunicationPreloader {

    private static final Logger log = LoggerFactory.getLogger(VistaDBCommunicationPreloader.class);

    public static void main(String[] args) {
        List<String> arguments = Arrays.asList(args);

        VistaServerSideConfiguration conf;
        if (arguments.contains("--postgre")) {
            log.info("Use PostgreSQL");
            conf = new VistaServerSideConfigurationDevPostgreSQL();
        } else {
            log.info("Use MySQL");
            conf = new VistaServerSideConfigurationDev();
        }
        ServerSideConfiguration.setInstance(conf);
        NamespaceManager.setNamespace(VistaNamespace.demoNamespace);

        Persistence.service().startTransaction();

        boolean success = false;
        String txt = null;
        try {
            CommunicationDevPreloader preloader = new CommunicationDevPreloader();
            txt = preloader.create();
            Persistence.service().commit();
            success = true;
        } catch (Exception ex) {
            log.error("", ex);
        } finally {
            if (!success) {
                try {
                    Persistence.service().rollback();
                } catch (Throwable ignore) {
                    log.error("error in rollback", ignore);
                }
            }
            log.debug("txt: " + txt);
            Persistence.service().endTransaction();
        }
        //clear

    }//main

}
