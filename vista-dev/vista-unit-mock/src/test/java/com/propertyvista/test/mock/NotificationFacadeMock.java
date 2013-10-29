/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-20
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.test.mock;

import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.tenant.lease.Lease;

public class NotificationFacadeMock implements NotificationFacade {

    @Override
    public void rejectPayment(PaymentRecord paymentRecord, boolean applyNSF) {
    }

    @Override
    public void autoPayReviewRequiredNotification(Lease leaseId) {
    }

    @Override
    public void autoPayCancelledByResidentNotification(Lease leaseId) {
    }

    @Override
    public void autoPayCancelledBySystemNotification(Lease leaseId) {
    }

    @Override
    public void aggregateNotificationsStart() {
    }

    @Override
    public void aggregatedNotificationsSend() {
    }

    @Override
    public void yardiUnableToRejectPayment(PaymentRecord paymentRecord, boolean applyNSF, String yardiErrorMessage) {
    }

}
