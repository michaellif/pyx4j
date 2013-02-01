/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-07
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import com.propertyvista.admin.domain.scheduler.PmcProcessType;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.server.jobs.insurance.PaymentsTenantSureProcess;

public class PmcProcessFactory {

    public static PmcProcess createPmcProcess(PmcProcessType triggerType) {
        switch (triggerType) {
        case test:
            return new TestPmcProcess();

// Billing-related:
        case billing:
            return new BillingProcess();
        case paymentsIssue:
            return new PaymentsIssueProcess();
        case paymentsScheduledCreditCards:
            return new PaymentsScheduledProcess(PaymentType.CreditCard);
        case paymentsScheduledEcheck:
            return new PaymentsScheduledProcess(PaymentType.Echeck);
        case paymentsPadSend:
            return new PadSendProcess();
        case paymentsPadReciveAcknowledgment:
            return new PadReciveAcknowledgmentProcess();
        case paymentsPadReciveReconciliation:
            return new PadReciveReconciliationProcess();
        case paymentsTenantSure:
            return new PaymentsTenantSureProcess();
        case initializeFutureBillingCycles:
            return new FutureBillingCycleInitializationProcess();

// Lease-related:
        case leaseActivation:
            return new LeaseActivationProcess();
        case leaseCompletion:
            return new LeaseCompletionProcess();
        case leaseRenewal:
            return new LeaseRenewalProcess();

// Global:
        case equifaxRetention:
            return new EquifaxRetentionProcess();

        case yardiImportProcess:
            return new YardiImportProcess();

        case yardiBatchProcess:
            return new YardiBatchProcess();

// Misc:
        case cleanup:
            return new CleanupPmcProcess();
        case updateArrears:
            return new UpdateArrearsProcess();
        case updatePaymentsSummary:
            // TODO: not sure if it should happen: return new PaymentsSummarySnapshotProcess();

        default:
            throw new IllegalArgumentException("Not implemented");
        }
    }
}
