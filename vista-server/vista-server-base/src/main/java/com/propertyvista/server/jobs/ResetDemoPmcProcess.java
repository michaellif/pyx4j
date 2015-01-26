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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.DemoData.DemoPmc;
import com.propertyvista.domain.settings.PmcVistaFeatures;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.operations.domain.scheduler.TriggerPmc;
import com.propertyvista.server.TaskRunner;

public class ResetDemoPmcProcess implements PmcProcess {

    private static final Logger log = LoggerFactory.getLogger(ResetDemoPmcProcess.class);

    private static final String[] PMCS_TO_RESET = { DemoPmc.rockville.toString() };

//    private static final DemoPmc[] PMCsToReset = { DemoPmc.rockville };

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
    public void complete(PmcProcessContext context) {
        log.info("Reset demo PMC Job completed");
    }

    @Override
        public void executePmcJob(PmcProcessContext context) {
            log.info("Execution of Reset demo PMC Job began...");
    
            // Get PMCs
            Set<String> pmcsToReset = getPMCsToReset();
    
            pmcsToReset.addAll(Arrays.asList(PMCS_TO_RESET));
    
            // TODO Why is invoked twice from different namespaces?
            // TODO reset & preload PMCs based on TriggerPmcs by invoking PmcPreloaderFacade
            for (String pmcDnsName : pmcsToReset) {
    //            ServerSideFactory.create(PmcPreloaderFacade.class).resetAndPreloadPmcProcess(pmcName, context.getExecutionMonitor());
    
            }
    
        }

    private Set<String> getPMCsToReset() {
        Set<String> pmcsToReset = new HashSet<String>();

        // TODO Improve query
        List<TriggerPmc> triggersPmc = TaskRunner.runInOperationsNamespace(new Callable<List<TriggerPmc>>() {
            @Override
            public List<TriggerPmc> call() {
                EntityQueryCriteria<TriggerPmc> criteria = EntityQueryCriteria.create(TriggerPmc.class);
                criteria.eq(criteria.proto().trigger().triggerType(), PmcProcessType.resetDemoPMC);
                return Persistence.service().query(criteria);
            }
        });

        for (TriggerPmc triggerPmc : triggersPmc) {
            if (!triggerPmc.pmc().isNull()) {
                pmcsToReset.add(triggerPmc.pmc().dnsName().getValue());
            }
        }

        return pmcsToReset;
    }
}
