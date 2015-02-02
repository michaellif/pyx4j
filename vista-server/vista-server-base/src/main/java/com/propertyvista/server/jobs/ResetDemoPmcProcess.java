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

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.preloader.PmcPreloaderFacade;
import com.propertyvista.domain.DemoData.DemoPmc;
import com.propertyvista.domain.settings.PmcVistaFeatures;

public class ResetDemoPmcProcess implements PmcProcess {

    private static final Logger log = LoggerFactory.getLogger(ResetDemoPmcProcess.class);

    private static final DemoPmc[] PMCS_TO_RESET = { DemoPmc.vista, DemoPmc.redridge, DemoPmc.rockville, DemoPmc.star, DemoPmc.gondor, DemoPmc.metcap,
            DemoPmc.timbercreek };

    @Override
    public boolean start(PmcProcessContext context) {

        for (DemoPmc pmcToReset : PMCS_TO_RESET) {
            try {
                ServerSideFactory.create(PmcPreloaderFacade.class).resetAndPreloadPmcProcess(pmcToReset.toString());
                context.getExecutionMonitor().addProcessedEvent("PMC '" + pmcToReset.toString() + "' Reseted and Preloaded");
            } catch (Exception e) {
                context.getExecutionMonitor().addErredEvent("Error reseting and preload PMC '" + pmcToReset.toString() + "'", e);
            }
        }

        return true;
    }

    @Override
    public boolean allowExecution(PmcVistaFeatures features) {
        return false;
    }

    @Override
    public void complete(PmcProcessContext context) {

    }

    @Override
    public void executePmcJob(PmcProcessContext context) {

    }

}
