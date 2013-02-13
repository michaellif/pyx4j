/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.proc;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.domain.security.OperationsUser;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.domain.scheduler.Trigger;
import com.propertyvista.operations.domain.scheduler.TriggerNotification;
import com.propertyvista.operations.domain.scheduler.TriggerNotificationEvent;

public class JobNotifications {

    public static void notify(Trigger trigger, Run run) {

        TriggerNotificationEvent event;
        switch (run.status().getValue()) {
        case Completed:
            event = TriggerNotificationEvent.Completed;
            break;
        case PartiallyCompleted:
        case Failed:
            event = TriggerNotificationEvent.Error;
            break;
        default:
            return;
        }

        for (TriggerNotification notificationCfg : trigger.notifications()) {
            if ((notificationCfg.event().getValue() == TriggerNotificationEvent.All) || (notificationCfg.event().getValue() == event)) {
                sendNotification(trigger, run, notificationCfg.user());
            }
        }

    }

    private static void sendNotification(Trigger trigger, Run run, OperationsUser user) {
        MailMessage m = new MailMessage();
        m.setTo(user.email().getValue());
        m.setSender(ServerSideConfiguration.instance().getApplicationEmailSender());
        m.setSubject(SimpleMessageFormat.format("Trigger Run notification {0} {1}", trigger.name(), run.status()));

        String message = "<html><body>";
        message += SimpleMessageFormat.format("Execution of process {0} is {1},<br/>\n", trigger.name(), run.status());
        message += SimpleMessageFormat.format("For Date: {0}<br/>\n", run.forDate());
        message += SimpleMessageFormat.format("Statistics: {0}<br/>\n", run.stats().getStringView());
        if (!run.errorMessage().isNull()) {
            message += SimpleMessageFormat.format("Error Message: {0}<br/>\n", run.errorMessage());
        }
        message += "</body></html>";
        m.setHtmlBody(message);
        Mail.send(m);
    }
}
