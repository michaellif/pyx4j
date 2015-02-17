/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 14, 2014
 * @author stanp
 */
package com.propertyvista.biz.communication.notifications;

import java.util.List;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Notification;

public class YardiConfigurationNotification extends AbstractNotification {

    private final String message;

    public YardiConfigurationNotification(String message) {
        this.message = message;
    }

    @Override
    public boolean aggregate(AbstractNotification other) {
        return false;
    }

    @Override
    public void send() {
        List<Employee> employees = NotificationsUtils.getNotificationTraget(Notification.AlertType.YardiSynchronization);
        List<String> emails = NotificationsUtils.toEmails(employees);
        if (emails.isEmpty()) {
            emails.add("leonard@propertyvista.com");
            emails.add("support@propertyvista.com");
        }
        ServerSideFactory.create(CommunicationFacade.class).sendYardiConfigurationNotification(emails, message);
    }
}
