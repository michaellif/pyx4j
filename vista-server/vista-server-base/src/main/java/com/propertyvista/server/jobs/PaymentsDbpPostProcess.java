/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 30, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import java.math.BigDecimal;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.payment.PaymentProcessFacade;
import com.propertyvista.biz.system.yardi.YardiConfigurationFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.settings.PmcVistaFeatures;

public class PaymentsDbpPostProcess implements PmcProcess {

    @Override
    public boolean start(PmcProcessContext context) {
        return true;
    }

    @Override
    public boolean allowExecution(PmcVistaFeatures features) {
        return true;
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        boolean yardiIntegration = VistaDeployment.getCurrentPmc().features().yardiIntegration().getValue(false);
        try {
            if (yardiIntegration) {
                ServerSideFactory.create(YardiConfigurationFacade.class).initYardiCredentialCache();
                ServerSideFactory.create(YardiConfigurationFacade.class).startYardiTimer();
            }

            ServerSideFactory.create(PaymentProcessFacade.class).processDirectDebitRecords(context.getExecutionMonitor());

        } finally {
            if (yardiIntegration) {
                ServerSideFactory.create(YardiConfigurationFacade.class).clearYardiCredentialCache();
                long yardiTime = ServerSideFactory.create(YardiConfigurationFacade.class).stopYardiTimer();
                context.getExecutionMonitor().addInfoEvent("yardiTime", TimeUtils.durationFormat(yardiTime), new BigDecimal(yardiTime));
            }
        }
    }

    @Override
    public void complete(PmcProcessContext context) {
    }

}
