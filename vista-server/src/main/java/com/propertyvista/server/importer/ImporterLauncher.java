/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 22, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.server.importer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;

import com.propertyvista.portal.server.generator.SharedData;
import com.propertyvista.portal.server.importer.Importer;
import com.propertyvista.server.config.VistaServerSideConfiguration;

public class ImporterLauncher {
    private static final Logger log = LoggerFactory.getLogger(ImporterLauncher.class);

    /**
     * This is optional, use only if you need this
     */
    public static void configureDb() {
        ServerSideConfiguration.setInstance(new VistaServerSideConfiguration());
    }

    public static void main(String[] args) {
        ImporterLauncher.configureDb();
        SharedData.init();
        log.info("Importing new Data...");
        try {
            Importer importer = new Importer();
            importer.start();
        } catch (Exception e) {
            log.error("Problem with importing XML", e);
        }
    }
}
