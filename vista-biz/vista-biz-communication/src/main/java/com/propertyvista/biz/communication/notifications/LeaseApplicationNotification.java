/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 26, 2015
 * @author stanp
 */
package com.propertyvista.biz.communication.notifications;

import java.util.List;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Notification;
import com.propertyvista.domain.tenant.lease.Lease;

public class LeaseApplicationNotification extends AbstractNotification {

    private final Lease lease;

    private final Notification.AlertType alertType;

    public LeaseApplicationNotification(Lease lease, Notification.AlertType alertType) {
        this.lease = lease;
        this.alertType = alertType;
    }

    @Override
    public boolean aggregate(AbstractNotification other) {
        return false;
    }

    @Override
    public void send() {
        List<Employee> employees = NotificationsUtils.getNotificationTraget(lease, alertType);
        if (!employees.isEmpty()) {
            ServerSideFactory.create(CommunicationFacade.class).sendLeaseApplicationNotification(NotificationsUtils.toEmails(employees), lease, alertType);
        }
    }

}
