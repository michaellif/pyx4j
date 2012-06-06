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
package com.propertyvista.admin.server.proc;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.admin.domain.scheduler.Run;
import com.propertyvista.admin.domain.scheduler.Trigger;
import com.propertyvista.admin.domain.scheduler.TriggerNotification;
import com.propertyvista.admin.domain.scheduler.TriggerNotificationEvent;
import com.propertyvista.domain.security.AdminUser;

public class JobNotifications {

    private static I18n i18n = I18n.get(JobNotifications.class);

    public static void notify(Trigger trigger, Run run) {

        TriggerNotificationEvent event;
        switch (run.status().getValue()) {
        case Completed:
            event = TriggerNotificationEvent.Completed;
            break;
        case Failed:
            event = TriggerNotificationEvent.Error;
            break;
        default:
            return;
        }

        for (TriggerNotification notificationCfg : trigger.notifications()) {
            if (notificationCfg.event().equals(TriggerNotificationEvent.All) || notificationCfg.event().equals(event)) {
                sendNotification(trigger, run, notificationCfg.user());
            }
        }

    }

    private static void sendNotification(Trigger trigger, Run run, AdminUser user) {
        MailMessage m = new MailMessage();
        m.setTo(user.email().getValue());
        m.setSender(ServerSideConfiguration.instance().getApplicationEmailSender());
        m.setSubject(i18n.tr("Trigger Run notification {0} {1}", trigger.name().getValue(), run.status().getValue()));

        String message = i18n.tr("Execution of process {0} is {1},<br/>\n{2}\n", trigger.name().getValue(), run.status().getValue(), run.stats()
                .getStringView());

        m.setHtmlBody(message);
        Mail.send(m);
    }
}
