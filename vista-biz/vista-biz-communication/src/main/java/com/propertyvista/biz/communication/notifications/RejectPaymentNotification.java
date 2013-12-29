/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 28, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.communication.notifications;

import java.util.List;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Notification;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.tenant.lease.Lease;

public class RejectPaymentNotification extends AbstractNotification {

    private final PaymentRecord paymentRecord;

    private final boolean applyNSF;

    public RejectPaymentNotification(PaymentRecord paymentRecord, boolean applyNSF) {
        this.paymentRecord = paymentRecord;
        this.applyNSF = applyNSF;
    }

    @Override
    public boolean aggregate(AbstractNotification other) {
        return false;
    }

    @Override
    public void send() {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.eq(criteria.proto().billingAccount(), paymentRecord.billingAccount());
        Lease leaseId = Persistence.service().retrieve(criteria, AttachLevel.IdOnly);
        List<Employee> employees = NotificationsUtils.getNotificationTraget(leaseId, Notification.NotificationType.ElectronicPaymentRejectedNsf);
        if (!employees.isEmpty()) {
            ServerSideFactory.create(CommunicationFacade.class)
                    .sendPaymentRejectedNotification(NotificationsUtils.toEmails(employees), paymentRecord, applyNSF);
        }
    }

}
