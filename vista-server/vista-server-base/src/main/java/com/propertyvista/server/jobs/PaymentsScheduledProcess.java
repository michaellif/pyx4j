/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.payment.PaymentProcessFacade;
import com.propertyvista.biz.system.yardi.YardiConfigurationFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.settings.PmcVistaFeatures;

public class PaymentsScheduledProcess implements PmcProcess {

    private final PaymentType paymentType;

    private long yardiRequestsTimeTotal = 0;

    public PaymentsScheduledProcess(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

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

            ServerSideFactory.create(PaymentProcessFacade.class).processPmcScheduledPayments(context.getExecutionMonitor(), paymentType,
                    new LogicalDate(context.getForDate()));
        } finally {
            if (yardiIntegration) {
                ServerSideFactory.create(YardiConfigurationFacade.class).clearYardiCredentialCache();
                long yardiTime = ServerSideFactory.create(YardiConfigurationFacade.class).stopYardiTimer();
                context.getExecutionMonitor().addInfoEvent("yardiTime", TimeUtils.durationFormat(yardiTime), new BigDecimal(yardiTime));
                yardiRequestsTimeTotal += yardiTime;
            }
        }
    }

    @Override
    public void complete(PmcProcessContext context) {
        if (yardiRequestsTimeTotal != 0) {
            context.getExecutionMonitor().addInfoEvent("yardiTime", TimeUtils.durationFormat(yardiRequestsTimeTotal), new BigDecimal(yardiRequestsTimeTotal));
        }
    }
}
