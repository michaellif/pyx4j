/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 18, 2014
 * @author vlads
 */
package com.propertyvista.server.jobs;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.system.yardi.YardiConfigurationFacade;
import com.propertyvista.config.VistaDeployment;

public abstract class AbstractYardiAwareProcess implements PmcProcess {

    private long yardiRequestsTimeTotal = 0;

    public abstract void executeYardiAwarePmcJob(PmcProcessContext context);

    @Override
    public final void executePmcJob(PmcProcessContext context) {
        boolean yardiIntegration = VistaDeployment.getCurrentPmc().features().yardiIntegration().getValue(false);
        try {
            if (yardiIntegration) {
                ServerSideFactory.create(YardiConfigurationFacade.class).initYardiCredentialCache();
                ServerSideFactory.create(YardiConfigurationFacade.class).startYardiTimer();
            }

            executeYardiAwarePmcJob(context);
        } finally {
            if (yardiIntegration) {
                ServerSideFactory.create(YardiConfigurationFacade.class).clearYardiCredentialCache();
                AtomicReference<Long> yardiTime = new AtomicReference<>(Long.valueOf(0));
                AtomicReference<Long> maxRequestTime = new AtomicReference<>(Long.valueOf(0));
                ServerSideFactory.create(YardiConfigurationFacade.class).stopYardiTimer(yardiTime, maxRequestTime);
                context.getExecutionMonitor().addInfoEvent("yardiTime", new BigDecimal(yardiTime.get()), TimeUtils.durationFormat(yardiTime.get()));
                context.getExecutionMonitor().addInfoEvent("yardiMaxRequestTime", new BigDecimal(maxRequestTime.get()),
                        TimeUtils.durationFormat(maxRequestTime.get()));
                yardiRequestsTimeTotal += yardiTime.get();
            }
        }

    }

    @Override
    public void complete(PmcProcessContext context) {
        if (yardiRequestsTimeTotal != 0) {
            context.getExecutionMonitor().addInfoEvent("yardiTime", new BigDecimal(yardiRequestsTimeTotal), TimeUtils.durationFormat(yardiRequestsTimeTotal));
        }
    }

}
