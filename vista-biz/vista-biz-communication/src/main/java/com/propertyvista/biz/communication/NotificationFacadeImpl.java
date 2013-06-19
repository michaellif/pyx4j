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

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Notification;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.tenant.lease.Lease;

public class NotificationFacadeImpl implements NotificationFacade {

    @Override
    public void rejectPayment(PaymentRecord paymentRecord, boolean applyNSF) {
        // TODO Auto-generated method stub
    }

    @Override
    public void papSuspension(Lease leaseId) {
        List<Employee> employees = NotificationsManager.getNotificationTraget(leaseId, Notification.NotificationType.PreauthorizedPaymentSuspension);
        if (employees.isEmpty()) {
            ServerSideFactory.create(CommunicationFacade.class).sendPapSuspensionNotification(NotificationsManager.toEmails(employees), leaseId);
        }
    }

    @Override
    public void maintenanceRequest(MaintenanceRequest request, boolean isNewRequest) {
        for (Employee employee : NotificationsManager.getNotificationTraget(request.building(), Notification.NotificationType.MaintenanceRequest)) {
            ServerSideFactory.create(CommunicationFacade.class).sendMaintenanceRequestEmail(employee.email().getValue(), employee.name().getStringView(),
                    request, isNewRequest, true);
        }
    }
}
