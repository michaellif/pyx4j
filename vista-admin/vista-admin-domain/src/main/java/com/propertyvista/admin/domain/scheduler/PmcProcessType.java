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
package com.propertyvista.admin.domain.scheduler;

import java.io.Serializable;

import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

@I18n
public enum PmcProcessType implements Serializable {

    Billing(true),

    @Translate("Issue PreAuthorized Payments")
    PaymentsIssue(true),

    @Translate("Process Scheduled CreditCards Payments")
    PaymentsScheduledCreditCards,

    @Translate("Process Scheduled eCheque Payments")
    PaymentsScheduledEcheck,

    PaymentsBmoRecive,

    @Translate("Send eCheque Payments to Caledon")
    PaymentsPadSend,

    PaymentsPadReciveAcknowledgment,

    PaymentsPadReciveReconciliation,

    LeaseActivation,

    InitializeFutureBillingCycles,

    Cleanup,

    UpdateArrears,

    UpdatePaymentsSummary,

    // Used for scheduler testing
    Test;

    private final boolean dailyExecutions;

    PmcProcessType() {
        dailyExecutions = false;
    }

    PmcProcessType(boolean dailyExecutions) {
        this.dailyExecutions = dailyExecutions;
    }

    public boolean isDailyExecutions() {
        return dailyExecutions;
    }

    public String getDescription() {
        return I18nEnum.toString(this);
    }
}
