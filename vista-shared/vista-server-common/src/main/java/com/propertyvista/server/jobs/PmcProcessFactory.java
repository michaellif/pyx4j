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

import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.server.jobs.insurance.PaymentsTenantSureProcess;
import com.propertyvista.server.jobs.insurance.TenantSureCancellationProcess;
import com.propertyvista.server.jobs.insurance.TenantSureReportsProcess;
import com.propertyvista.server.jobs.insurance.TenantSureTransactionsReportProcess;

public class PmcProcessFactory {

    public static PmcProcess createPmcProcess(PmcProcessType triggerType) {
        switch (triggerType) {
        case test:
            return new TestPmcProcess();

// Billing-related:
        case billing:
            return new BillingProcess();
        case initializeFutureBillingCycles:
            return new FutureBillingCycleInitializationProcess();

        case depositRefund:
            return new DepositRefundProcess();
        case depositInterestAdjustment:
            return new DepositInterestAdjustmentProcess();

        case paymentsIssue:
            return new PaymentsIssueProcess();
        case paymentsUpdate:
            return new PaymentsUpdateProcess();
        case paymentsScheduledCreditCards:
            return new PaymentsScheduledProcess(PaymentType.CreditCard);
        case paymentsScheduledEcheck:
            return new PaymentsScheduledProcess(PaymentType.Echeck);

        case paymentsLastMonthSuspend:
            return new PaymentsLastMonthSuspendProcess();

// Caledon            
        case paymentsPadSend:
            return new PadSendProcess();
        case paymentsPadReceiveAcknowledgment:
            return new PadReceiveAcknowledgmentProcess();
        case paymentsPadProcesAcknowledgment:
            return new PadProcessAcknowledgmentProcess();
        case paymentsPadReceiveReconciliation:
            return new PadReceiveReconciliationProcess();
        case paymentsPadProcesReconciliation:
            return new PadProcessReconciliationProcess();

        case paymentsTenantSure:
            return new PaymentsTenantSureProcess();

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

        case yardiARDateVerification:
            return new YardiARDateVerificationProcess();

// TenantSure:
        case tenantSureReports:
            return new TenantSureReportsProcess();
        case tenantSureTransactionReports:
            return new TenantSureTransactionsReportProcess();
        case tenantSureCancellation:
            return new TenantSureCancellationProcess();

// Misc:
        case vistaBusinessReport:
            return new VistaBusinessStatsReportProcess();
        case vistaCaleonReport:
            return new VistaBusinessCaledonReportProcess();
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
