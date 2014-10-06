/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 3, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.communication.notifications;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Notification;
import com.propertyvista.domain.tenant.lease.Lease;

public class BillingAlertNotification extends AbstractGroupPerBuildingNotification {

    private final Map<Lease, List<String>> billingAlerts = new HashMap<>();

    public BillingAlertNotification(Lease leaseId, String alert) {
        super(Notification.NotificationType.BillingAlert, leaseId);
        billingAlerts.put(leaseId, Arrays.asList(alert));
    }

    @Override
    public boolean aggregate(AbstractNotification other) {
        if (super.aggregate(other)) {
            for (Map.Entry<Lease, List<String>> me : ((BillingAlertNotification) other).billingAlerts.entrySet()) {
                if (billingAlerts.containsKey(me.getKey())) {
                    List<String> list = billingAlerts.get(me.getKey());
                    list.addAll(me.getValue());
                } else {
                    billingAlerts.put(me.getKey(), me.getValue());
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void send() {
        List<Employee> employees = NotificationsUtils.getNotificationTraget(getBuildingId(), Notification.NotificationType.BillingAlert);
        if (!employees.isEmpty()) {
            ServerSideFactory.create(CommunicationFacade.class).sendBillingAlertNotification(NotificationsUtils.toEmails(employees), getLeaseIds(), billingAlerts);
        }
    }
}
