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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Notification;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.tenant.lease.Lease;

public class NotificationFacadeImpl implements NotificationFacade {

    @Override
    public void rejectPayment(PaymentRecord paymentRecord, boolean applyNSF) {
        if (applyNSF) {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.eq(criteria.proto().billingAccount(), paymentRecord.billingAccount());
            Lease leaseId = Persistence.service().retrieve(criteria, AttachLevel.IdOnly);
            List<Employee> employees = NotificationsUtils.getNotificationTraget(leaseId, Notification.NotificationType.ElectronicPaymentRejectedNsf);
            if (!employees.isEmpty()) {
                ServerSideFactory.create(CommunicationFacade.class).sendPaymentReversalWithNsfNotification(NotificationsUtils.toEmails(employees),
                        paymentRecord);
            }
        }
    }

    @Override
    public void papSuspension(Lease leaseId) {
        List<Employee> employees = NotificationsUtils.getNotificationTraget(leaseId, Notification.NotificationType.PreauthorizedPaymentSuspension);
        if (!employees.isEmpty()) {
            ServerSideFactory.create(CommunicationFacade.class).sendPapSuspensionNotification(NotificationsUtils.toEmails(employees), leaseId);
        }
    }
}
