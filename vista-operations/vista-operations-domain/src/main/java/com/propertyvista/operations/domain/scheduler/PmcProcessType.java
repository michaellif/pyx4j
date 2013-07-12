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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

@I18n
public enum PmcProcessType implements Serializable {

    vistaBusinessReport,

    vistaCaleonReport,

    billing(PmcProcessOptions.RunForDay),

    depositRefund,

    depositInterestAdjustment,

    @Translate("PAD 1 - Issue PreAuthorized Payments")
    paymentsIssue(PmcProcessOptions.RunForDay),

    @Translate("PAD 2 - Update PreAuthorized Payments")
    paymentsUpdate(PmcProcessOptions.RunForDay),

    @Translate("PAD 3 - Process Scheduled eCheque Payments")
    paymentsScheduledEcheck(PmcProcessOptions.RunForDay),

    @Translate("PAD 4 - Process Scheduled CreditCards Payments")
    paymentsScheduledCreditCards(PmcProcessOptions.RunForDay),

    @Translate("PAD 5 - Send eCheque Payments to Caledon")
    paymentsPadSend,

    @Translate("PAD 6A - Payments Pad Receive Acknowledgment from Caledon")
    paymentsPadReceiveAcknowledgment(PmcProcessOptions.GlobalOnly),

    @Translate("PAD 6B - Payments Pad Process Acknowledgment (auto triggered by ReceiveAcknowledgment)")
    paymentsPadProcesAcknowledgment,

    @Translate("PAD 7A - Payments Pad Receive Reconciliation from Caledon")
    paymentsPadReceiveReconciliation(PmcProcessOptions.GlobalOnly),

    @Translate("PAD 7B - Receive Payments from BMO")
    paymentsBmoReceive(PmcProcessOptions.GlobalOnly),

    @Translate("PAD 7C - Payments Pad Process Reconciliation (auto triggered by ReceiveReconciliation)")
    paymentsPadProcesReconciliation,

    paymentsTenantSure(PmcProcessOptions.RunForDay),

    paymentsLastMonthSuspend,

    initializeFutureBillingCycles,

    leaseActivation,

    leaseCompletion,

    leaseRenewal,

    cleanup,

    updateArrears,

    updatePaymentsSummary,

    equifaxRetention(PmcProcessOptions.GlobalOnly),

    yardiARDateVerification(PmcProcessOptions.RunForDay),

    yardiImportProcess,

    tenantSureHQUpdate, //

    tenantSureCancellation, // Vlad & Artyom

    tenantSureReports,

    tenantSureTransactionReports(PmcProcessOptions.RunForDay),

    // Used for scheduler testing
    test;

    private final List<PmcProcessOptions> options;

    PmcProcessType() {
        this.options = Collections.emptyList();
    }

    PmcProcessType(PmcProcessOptions... options) {
        this.options = Arrays.asList(options);
    }

    public boolean hasOption(PmcProcessOptions option) {
        return options.contains(option);
    }

    public String getDescription() {
        return I18nEnum.toString(this);
    }
}
