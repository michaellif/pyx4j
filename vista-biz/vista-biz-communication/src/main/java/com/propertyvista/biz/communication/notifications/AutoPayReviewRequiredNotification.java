/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 16, 2013
 * @author vlads
 */
package com.propertyvista.biz.communication.notifications;

import java.util.List;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Notification;
import com.propertyvista.domain.tenant.lease.Lease;

public class AutoPayReviewRequiredNotification extends AbstractGroupPerBuildingNotification {

    public AutoPayReviewRequiredNotification(Lease leaseId) {
        super(Notification.AlertType.AutoPayReviewRequired, leaseId);
    }

    @Override
    public void send() {
        List<Employee> employees = NotificationsUtils.getNotificationTraget(getBuildingId(), Notification.AlertType.AutoPayReviewRequired);
        if (!employees.isEmpty()) {
            ServerSideFactory.create(CommunicationFacade.class).sendAutoPayReviewRequiredNotification(NotificationsUtils.toEmails(employees), getLeaseIds());
        }
    }

}
