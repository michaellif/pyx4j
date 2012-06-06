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

    billing,

    @Translate("Issue PreAuthorized Payments")
    paymentsIssue,

    @Translate("Process Scheduled CreditCards Payments")
    paymentsScheduledCreditCards,

    @Translate("Process Scheduled eCheque Payments")
    paymentsScheduledEcheck,

    paymentsBmoRecive,

    @Translate("Send eCheque Payments to Caledon")
    paymentsPadSend,

    paymentsPadReciveAcknowledgment,

    paymentsPadReciveReconciliation,

    leaseActivation,

    cleanup,

    updateArrears,

    updatePaymentsSummary,

    // Used for scheduler testing
    test;

    public String getDescription() {
        return I18nEnum.toString(this);
    }
}
