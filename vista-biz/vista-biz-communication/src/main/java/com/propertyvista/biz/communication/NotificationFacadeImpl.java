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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.communication.notifications.AbstractNotification;
import com.propertyvista.biz.communication.notifications.AutoPayCancelledByResidentNotification;
import com.propertyvista.biz.communication.notifications.AutoPayCancelledBySystemNotification;
import com.propertyvista.biz.communication.notifications.AutoPayReviewRequiredNotification;
import com.propertyvista.biz.communication.notifications.NotificationsAggregator;
import com.propertyvista.biz.communication.notifications.PostToYardiFailedNotification;
import com.propertyvista.biz.communication.notifications.RejectPaymentNotification;
import com.propertyvista.biz.system.OperationsAlertFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.tenant.lease.Lease;

public class NotificationFacadeImpl implements NotificationFacade {

    private final static Logger log = LoggerFactory.getLogger(NotificationFacadeImpl.class);

    private static final ThreadLocal<NotificationsAggregator> aggregatorThreadLocal = new ThreadLocal<NotificationsAggregator>();

    private void aggregateOrSend(AbstractNotification notification) {
        try {
            NotificationsAggregator aggregator = aggregatorThreadLocal.get();
            if (aggregator != null) {
                aggregator.aggregate(notification);
            } else {
                notification.send();
            }
        } catch (Throwable e) {
            log.error("unable to send notification", e);
            ServerSideFactory.create(OperationsAlertFacade.class).record(null, "Notification {0} failed", notification.getClass().getSimpleName(), e);
        }
    }

    @Override
    public void aggregateNotificationsStart() {
        NotificationsAggregator aggregator = aggregatorThreadLocal.get();
        if (aggregator == null) {
            aggregatorThreadLocal.set(new NotificationsAggregator());
        }
    }

    @Override
    public void aggregatedNotificationsSend() {
        NotificationsAggregator aggregator = aggregatorThreadLocal.get();
        if (aggregator != null) {
            aggregator.send();
        }
        aggregatorThreadLocal.remove();
    }

    @Override
    public void rejectPayment(PaymentRecord paymentRecord, boolean applyNSF) {
        aggregateOrSend(new RejectPaymentNotification(paymentRecord, applyNSF));
    }

    @Override
    public void yardiUnableToRejectPayment(PaymentRecord paymentRecord, boolean applyNSF, String yardiErrorMessage) {
        aggregateOrSend(new PostToYardiFailedNotification(paymentRecord, applyNSF, yardiErrorMessage));
    }

    @Override
    public void autoPayCancelledByResidentNotification(Lease leaseId, List<AutopayAgreement> canceledAgreements) {
        aggregateOrSend(new AutoPayCancelledByResidentNotification(leaseId, canceledAgreements));
    }

    @Override
    public void autoPayReviewRequiredNotification(Lease leaseId) {
        aggregateOrSend(new AutoPayReviewRequiredNotification(leaseId));
    }

    @Override
    public void autoPayCancelledBySystemNotification(Lease leaseId, List<AutopayAgreement> canceledAgreements) {
        aggregateOrSend(new AutoPayCancelledBySystemNotification(leaseId, canceledAgreements));
    }

}
