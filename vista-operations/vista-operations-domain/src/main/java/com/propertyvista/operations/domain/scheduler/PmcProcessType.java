/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 4, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.domain.scheduler;

import java.io.Serializable;

import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

@I18n
public enum PmcProcessType implements Serializable {

    vistaBusinessReport,

    billing(true),

    @Translate("PAD 1 - Issue PreAuthorized Payments")
    paymentsIssue(true),

    @Translate("PAD 2 - Update PreAuthorized Payments")
    paymentsUpdate(true),

    @Translate("PAD 3 - Process Scheduled eCheque Payments")
    paymentsScheduledEcheck,

    @Translate("PAD 4 - Process Scheduled CreditCards Payments")
    paymentsScheduledCreditCards,

    @Translate("PAD 5 - Send eCheque Payments to Caledon")
    paymentsPadSend,

    @Translate("PAD 6A - Payments Pad Receive Acknowledgment from Caledon")
    paymentsPadReceiveAcknowledgment,

    @Translate("PAD 6B - Payments Pad Process Acknowledgment (auto triggered by ReceiveAcknowledgment)")
    paymentsPadProcesAcknowledgment,

    @Translate("PAD 7A - Payments Pad Receive Reconciliation from Caledon")
    paymentsPadReceiveReconciliation,

    @Translate("PAD 7B - Receive Payments from BMO")
    paymentsBmoReceive,

    @Translate("PAD 7C - Payments Pad Process Reconciliation (auto triggered by ReceiveReconciliation)")
    paymentsPadProcesReconciliation,

    paymentsTenantSure(true),

    initializeFutureBillingCycles,

    leaseActivation,

    leaseCompletion,

    leaseRenewal,

    cleanup,

    updateArrears,

    updatePaymentsSummary,

    equifaxRetention,

    yardiImportProcess,

    tenantSureHQUpdate, //

    tenantSureCancellation, // Vlad & Artyom

    tenantSureReports,

    tenantSureTransactionReports(true),

    // Used for scheduler testing
    test;

    private final boolean dailyExecutions;

    PmcProcessType() {
        dailyExecutions = false;
    }

    PmcProcessType(boolean dailyExecutions) {
        this.dailyExecutions = dailyExecutions;
    }

    //TODO
    PmcProcessType(PmcProcessOptions... options) {
        boolean runForDay = false;
        for (PmcProcessOptions otion : options) {
            if (otion == PmcProcessOptions.RunForDay) {
                runForDay = true;
            }
        }
        this.dailyExecutions = runForDay;
    }

    public boolean isDailyExecutions() {
        return dailyExecutions;
    }

    public String getDescription() {
        return I18nEnum.toString(this);
    }
}
