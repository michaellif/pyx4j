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
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.dashboard.gadgets.payments.PaymentsSummary;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.server.common.util.PaymentsSummaryHelper;
import com.propertyvista.server.common.util.PaymentsSummaryHelper.PaymentsSummarySnapshotHook;

public class PaymentsSummarySnapshotProcess implements PmcProcess {

    private static final Logger log = LoggerFactory.getLogger(PaymentsSummarySnapshotProcess.class);

    @Override
    public boolean start(PmcProcessContext context) {
        log.info("Payment Summary Snapshot process started");
        return true;
    }

    @Override
    public void executePmcJob(final PmcProcessContext context) {
        long maxProgress = Persistence.service().count(EntityQueryCriteria.create(MerchantAccount.class)) * (long) PaymentRecord.PaymentStatus.values().length;
        context.getRunStats().total().setValue(maxProgress);

        PaymentsSummaryHelper summaryHelper = new PaymentsSummaryHelper(new PaymentsSummarySnapshotHook() {

            long processed = 0L;

            long failed = 0L;

            @Override
            public boolean onPaymentsSummarySnapshotTaken(PaymentsSummary summmary) {
                Persistence.service().commit();
                context.getRunStats().processed().setValue(++processed);
                return true;
            }

            @Override
            public boolean onPaymentsSummarySnapshotFailed(Throwable caught) {
                Persistence.service().rollback();
                context.getRunStats().failed().setValue(++failed);
                log.error("Failed to create payments summary snapshot", caught);
                return true;
            }
        });

        // TODO this should be yesterday, or something like that
        LogicalDate today = new LogicalDate(Persistence.service().getTransactionSystemTime());
        summaryHelper.takePaymentsSummarySnapshots(today);
    }

    @Override
    public void complete(PmcProcessContext context) {
        log.info("Payment Summary Snapshot process finished");
    }

}
