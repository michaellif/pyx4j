/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 22, 2012
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import java.math.BigDecimal;
import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.system.YardiARFacade;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.settings.PmcVistaFeatures;
import com.propertyvista.operations.domain.scheduler.CompletionType;
import com.propertyvista.shared.config.VistaFeatures;

public class YardiImportProcess implements PmcProcess {

    private static final Logger log = LoggerFactory.getLogger(YardiImportProcess.class);

    private BigDecimal yardiRequestsTimeTotal = BigDecimal.ZERO;

    @Override
    public boolean start(PmcProcessContext context) {
        log.info("Yardi Import batch job started");
        return true;
    }

    @Override
    public boolean allowExecution(PmcVistaFeatures features) {
        return features.yardiIntegration().getValue(false);
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        ExecutionMonitor executionMonitor = context.getExecutionMonitor();
        if (VistaFeatures.instance().yardiIntegration()) {
            try {
                ServerSideFactory.create(YardiARFacade.class).doAllImport(executionMonitor);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            } catch (YardiServiceException e) {
                throw new RuntimeException(e);
            } finally {
                BigDecimal yardiTime = executionMonitor.getValue("yardiTime", CompletionType.processed);
                if (yardiTime != null) {
                    yardiRequestsTimeTotal = yardiRequestsTimeTotal.add(yardiTime);
                }
            }
        }
    }

    @Override
    public void complete(PmcProcessContext context) {
        log.info("Yardi Import batch job finished");
        context.getExecutionMonitor().addInfoEvent("yardiTime", TimeUtils.durationFormat(yardiRequestsTimeTotal.longValue()), yardiRequestsTimeTotal);
    }
}
