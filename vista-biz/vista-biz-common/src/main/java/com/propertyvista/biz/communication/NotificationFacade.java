/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-19
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import java.util.List;

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.tenant.lease.Lease;

public interface NotificationFacade {

    public void rejectPayment(PaymentRecord paymentRecord, boolean applyNSF);

    public void yardiUnableToRejectPayment(PaymentRecord paymentRecord, boolean applyNSF, String yardiErrorMessage);

    public void autoPayReviewRequiredNotification(Lease leaseId);

    public void autoPayCancelledBySystemNotification(Lease leaseId, List<AutopayAgreement> canceledAgreements);

    public void autoPayCancelledByResidentNotification(Lease leaseId, List<AutopayAgreement> canceledAgreements);

    public void aggregateNotificationsStart();

    /**
     * Do not send Notifications, Call in rollback
     */
    public void aggregatedNotificationsCancel();

    public void aggregatedNotificationsSend();

}
