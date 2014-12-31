/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 30, 2014
 * @author ernestog
 */
package com.propertyvista.server.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.shared.ApplicationMode;

import com.propertyvista.biz.preloader.PmcPreloaderFacade;
import com.propertyvista.domain.settings.PmcVistaFeatures;

public class ResetDemoPmcProcess implements PmcProcess {

    private static final Logger log = LoggerFactory.getLogger(ResetDemoPmcProcess.class);

    @Override
    public boolean start(PmcProcessContext context) {
        log.info("Reset demo PMC Job started");
        return true;
    }

    @Override
    public boolean allowExecution(PmcVistaFeatures features) {
        if (!ApplicationMode.isDemo()) {
            log.info("No DEMO environment. PMCs reset job is not allowed.");
        }

        return ApplicationMode.isDemo();
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        PmcPreloaderFacade pmcPreloader = null; //new PmcPreloaderFacadeFactory().getFacade();
        log.info("Execution of Reset demo PMC Job began...");

        // TODO reset & preload PMCs based on TriggerPmcs by invoking PmcPreloaderFacade
//            pmcPreloader.resetPmcTables(pmc.name());
//            pmcPreloader.resetAndPreload(pmc.name());

    }

    @Override
    public void complete(PmcProcessContext context) {
        log.info("Reset demo PMC Job completed");
    }

}
