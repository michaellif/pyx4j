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

import java.util.List;

import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

public class NotificationFacadeMock implements NotificationFacade {

    @Override
    public void rejectPayment(PaymentRecord paymentRecord, boolean applyNSF) {
    }

    @Override
    public void autoPayReviewRequiredNotification(Lease leaseId) {
    }

    @Override
    public void autoPayCancelledByResidentNotification(Lease leaseId, List<AutopayAgreement> canceledAgreements) {
    }

    @Override
    public void autoPayCancelledBySystemNotification(Lease leaseId, List<AutopayAgreement> canceledAgreements) {
    }

    @Override
    public void aggregateNotificationsStart() {
    }

    @Override
    public void aggregatedNotificationsCancel() {
    }

    @Override
    public void aggregatedNotificationsSend() {
    }

    @Override
    public void yardiUnableToRejectPayment(PaymentRecord paymentRecord, boolean applyNSF, String yardiErrorMessage) {
    }

    @Override
    public void oneTimePaymentSubmitted(PaymentRecord paymentRecord) {
    }

    @Override
    public void paymentCleared(PaymentRecord paymentRecord) {
    }

    @Override
    public void autoPaySetupCompleted(AutopayAgreement autopayAgreement) {
    }

    @Override
    public void autoPayChanges(AutopayAgreement autopayAgreement) {
    }

    @Override
    public void autoPayCancellation(AutopayAgreement autopayAgreement) {
    }

    @Override
    public void yardiUnableToPostPaymentBatch(String errorMessage) {
    }

    @Override
    public void yardiConfigurationError(String errorMessage) {
    }

    @Override
    public void directDebitAccountChanged(LeaseTermTenant tenant) {
    }

    @Override
    public void billingAlertNotification(Lease leaseId, String alert) {
    }

}
