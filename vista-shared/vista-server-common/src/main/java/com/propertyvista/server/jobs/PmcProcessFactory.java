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

public class PmcProcessFactory {

    public static PmcProcess createPmcProcess(PmcProcessType triggerType) {
        switch (triggerType) {
        case Test:
            return new TestPmcProcess();
        case Billing:
            return new BillingProcess();
        case PaymentsIssue:
            return new PaymentsIssueProcess();
        case PaymentsScheduledCreditCards:
            return new PaymentsScheduledProcess(PaymentType.CreditCard);
        case PaymentsScheduledEcheck:
            return new PaymentsScheduledProcess(PaymentType.Echeck);
        case PaymentsPadSend:
            return new PadSendProcess();
        case PaymentsPadReciveAcknowledgment:
            return new PadReciveAcknowledgmentProcess();
        case PaymentsPadReciveReconciliation:
            return new PadReciveReconciliationProcess();
        case Cleanup:
            return new CleanupPmcProcess();
        case UpdateArrears:
            return new UpdateArrearsProcess();
        case LeaseActivation:
            return new LeaseActivationProcess();
        case InitializeFutureBillingCycles:
            return new FutureBillingCycleInitializationProcess();
        case UpdatePaymentsSummary:
            // TODO: not sure if it should happen: return new PaymentsSummarySnapshotProcess();
        default:
            throw new IllegalArgumentException("Not implemented");
        }
    }
}
