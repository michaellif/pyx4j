/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 5, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.dashboard.DashboardCalulationsFacade;
import com.propertyvista.biz.dashboard.DashboardCalulationsFacade.PaymentsSummarySnapshotHook;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentsSummary;
import com.propertyvista.domain.settings.PmcVistaFeatures;

public class PaymentsSummarySnapshotProcess implements PmcProcess {

    private static final Logger log = LoggerFactory.getLogger(PaymentsSummarySnapshotProcess.class);

    protected static final String EXECUTION_MONITOR_SECTION_NAME = "PaymentSummarySnapshot";

    @Override
    public boolean start(PmcProcessContext context) {
        log.info("Payment Summary Snapshot process started");
        return true;
    }

    @Override
    public boolean allowExecution(PmcVistaFeatures features) {
        return true;
    }

    @Override
    public void executePmcJob(final PmcProcessContext context) {
        ServerSideFactory.create(DashboardCalulationsFacade.class).takePaymentsSummarySnapshots(new LogicalDate(context.getForDate()),
                new PaymentsSummarySnapshotHook() {

                    @Override
                    public boolean onPaymentsSummarySnapshotTaken(PaymentsSummary summmary) {
                        context.getExecutionMonitor().addProcessedEvent(EXECUTION_MONITOR_SECTION_NAME);
                        return true;
                    }

                    @Override
                    public boolean onPaymentsSummarySnapshotFailed(Throwable t) {
                        context.getExecutionMonitor().addFailedEvent(EXECUTION_MONITOR_SECTION_NAME, t);
                        log.error("Failed to create payments summary snapshot", t);
                        return true;
                    }
                });
    }

    @Override
    public void complete(PmcProcessContext context) {
        log.info("Payment Summary Snapshot process finished");
    }

}
