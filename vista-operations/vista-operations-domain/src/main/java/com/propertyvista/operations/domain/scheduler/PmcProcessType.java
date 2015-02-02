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
 */
package com.propertyvista.operations.domain.scheduler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

@I18n
public enum PmcProcessType implements Serializable {

    vistaBusinessReport,

    vistaCaleonReport,

    billing(PmcProcessOptions.RunForDay, PmcProcessOptions.RequiredDaily),

    depositRefund,

    depositInterestAdjustment,

    @Translate("P 0A - Receive Direct Banking Payments from BMO")
    paymentsBmoReceive(PmcProcessOptions.GlobalOnly),

    @Translate("P 0B - Process Direct Banking, create payment records and post to yardi (auto triggered by PaymentsBmoReceive)")
    paymentsDbpProcess,

    @Translate("P 1 - Issue PreAuthorized Payments")
    paymentsIssue(PmcProcessOptions.RunForDay, PmcProcessOptions.RequiredDaily),

    @Translate("P 3 - Post Scheduled eCheque Payments")
    paymentsScheduledEcheck(PmcProcessOptions.RunForDay, PmcProcessOptions.RequiredDaily),

    @Translate("P 4 - Post Scheduled Cards Payments")
    paymentsScheduledCards(PmcProcessOptions.RunForDay, PmcProcessOptions.RequiredDaily),

    @Translate("P 5A - Send eCheque(PAD) Funds Transfer to Caledon")
    paymentsPadSend,

    @Translate("P 5B - Send Direct Banking (BMO) Funds Transfer to Caledon")
    paymentsDbpSend,

    @Translate("P 5C - Send Cards Payments to Caledon")
    paymentsCardsSend,

    @Translate("P 5D - Post rejected Cards Payments")
    paymentsCardsPostRejected,

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

    @Translate("P 8A - Payments Receive Cards Reconciliation from Caledon")
    paymentsReceiveCardsReconciliation(PmcProcessOptions.GlobalOnly),

    @Translate("P 8B - Payments Process Cards Reconciliation (auto triggered by paymentsReceiveCardsReconciliation)")
    paymentsProcessCardsReconciliation,

    paymentsTenantSure(PmcProcessOptions.RunForDay, PmcProcessOptions.RequiredDaily),

    // TODO rename to : paymentsUpdatePreauthorizedPaymentAgreements
    paymentsLastMonthSuspend(PmcProcessOptions.RunForDay),

    initializeFutureBillingCycles(PmcProcessOptions.RunForDay),

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

    tenantSureRenewal(PmcProcessOptions.RunForDay), // Vlad

    tenantSureReports,

    tenantSureTransactionReports(PmcProcessOptions.RunForDay),

    tenantSureBusinessReport,

    @Translate("ILS Vendor Update")
    ilsUpdate,

    @Translate("ILS Email Feed")
    ilsEmailFeed,

    vistaHeathMonitor(PmcProcessOptions.RunForDay),

    @Translate("Reset data for preselected Demo PMCs")
    resetDemoPMC(PmcProcessOptions.GlobalOnly),

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

    public List<PmcProcessOptions> getOptions() {
        return options;
    }

    public static Collection<PmcProcessType> requiredDaily() {
        Collection<PmcProcessType> c = new ArrayList<>();
        for (PmcProcessType t : EnumSet.allOf(PmcProcessType.class)) {
            if (t.hasOption(PmcProcessOptions.RequiredDaily)) {
                c.add(t);
            }
        }
        return c;
    }

    public String getDescription() {
        return I18nEnum.toString(this);
    }
}
