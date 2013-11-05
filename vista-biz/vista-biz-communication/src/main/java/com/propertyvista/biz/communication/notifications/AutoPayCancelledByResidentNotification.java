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

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Notification;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.tenant.lease.Lease;

public class AutoPayCancelledByResidentNotification extends AbstractNotification {

    private final Lease leaseId;

    private final List<AutopayAgreement> canceledAgreements;

    public AutoPayCancelledByResidentNotification(Lease leaseId, List<AutopayAgreement> canceledAgreements) {
        this.leaseId = leaseId;
        this.canceledAgreements = new ArrayList<AutopayAgreement>(canceledAgreements);
    }

    @Override
    public boolean aggregate(AbstractNotification other) {
        return false;
    }

    @Override
    public void send() {
        List<Employee> employees = NotificationsUtils.getNotificationTraget(leaseId, Notification.NotificationType.AutoPayCanceledByResident);
        if (!employees.isEmpty()) {
            ServerSideFactory.create(CommunicationFacade.class).sendAutoPayCancelledByResidentNotification(NotificationsUtils.toEmails(employees), leaseId,
                    canceledAgreements);
        }
    }

}
