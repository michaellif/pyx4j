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

    @Translate("P 0A - Receive Direct Banking Payments from BMO")
    paymentsBmoReceive(PmcProcessOptions.GlobalOnly),

    @Translate("P 0B - Process Direct Banking, create payment records and post to yardi (auto triggered by PaymentsBmoReceive)")
    paymentsDbpProcess,

    @Translate("P 1 - Issue PreAuthorized Payments")
    paymentsIssue(PmcProcessOptions.RunForDay),

    @Translate("P 3 - Process Scheduled eCheque Payments")
    paymentsScheduledEcheck(PmcProcessOptions.RunForDay),

    @Translate("P 4 - Process Scheduled Cards Payments")
    // TODO rename enum to remove redit 
    paymentsScheduledCreditCards(PmcProcessOptions.RunForDay),

    @Translate("P 5A - Send eCheque(PAD) Funds Transfer to Caledon")
    paymentsPadSend,

    @Translate("P 5B - Send Direct Banking (BMO) Funds Transfer to Caledon")
    paymentsDbpSend,

    @Translate("P 6A - Payments Receive Funds Transfer Acknowledgment from Caledon (PAD,DBP)")
    paymentsReceiveAcknowledgment(PmcProcessOptions.GlobalOnly),

    @Translate("P 6B - Payments Pad Process Acknowledgment (auto triggered by ReceiveAcknowledgment)")
    paymentsPadProcessAcknowledgment,

    @Translate("P 6C - Payments Direct Banking Process Acknowledgment (auto triggered by ReceiveAcknowledgment)")
    paymentsDbpProcessAcknowledgment,

    @Translate("P 7A - Payments Receive Funds Transfer Reconciliation from Caledon (PAD,DBP)")
    paymentsReceiveReconciliation(PmcProcessOptions.GlobalOnly),

    @Translate("P 7B - Payments Pad Process Reconciliation (auto triggered by ReceiveReconciliation)")
    paymentsPadProcessReconciliation,

    @Translate("P 7C - Payments Direct Banking Process Reconciliation (auto triggered by ReceiveReconciliation)")
    paymentsDbpProcessReconciliation,

    paymentsTenantSure(PmcProcessOptions.RunForDay),

    // TODO rename to : paymentsUpdatePreauthorizedPaymentAgreements
    paymentsLastMonthSuspend(PmcProcessOptions.RunForDay),

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
