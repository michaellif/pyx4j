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

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.tenant.lease.Lease;

public interface NotificationFacade {

    public void rejectPayment(PaymentRecord paymentRecord, boolean applyNSF);

    public void yardiUnableToRejectPayment(PaymentRecord paymentRecord, boolean applyNSF);

    public void autoPayReviewRequiredNotification(Lease leaseId);

    public void autoPayCancelledBySystemNotification(Lease leaseId);

    public void autoPayCancelledByResidentNotification(Lease leaseId);

    public void aggregateNotificationsStart();

    public void aggregatedNotificationsSend();

}
