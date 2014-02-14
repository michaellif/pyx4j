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

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailMessage;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.biz.system.VistaSystemFacade;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.operations.domain.scheduler.PmcProcessOptions;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.domain.scheduler.Trigger;
import com.propertyvista.operations.domain.scheduler.TriggerNotification;
import com.propertyvista.operations.domain.scheduler.TriggerNotificationEvent;
import com.propertyvista.operations.domain.security.OperationsUser;
import com.propertyvista.operations.rpc.OperationsSiteMap;

public class JobNotifications {

    public static void notify(Trigger trigger, Run run) {
        if (ServerSideFactory.create(VistaSystemFacade.class).isCommunicationsDisabled()) {
            return;
        }

        List<TriggerNotificationEvent> events = new ArrayList<TriggerNotificationEvent>();
        events.add(TriggerNotificationEvent.All);

        switch (run.status().getValue()) {
        case Completed:
            events.add(TriggerNotificationEvent.Completed);
            break;
        case PartiallyCompleted:
        case Failed:
            events.add(TriggerNotificationEvent.Error);
            events.add(TriggerNotificationEvent.Failed);
            events.add(TriggerNotificationEvent.NonEmpty);
            break;
        default:
            return;
        }

        if (run.executionReport().total().getValue(0L) > 0) {
            events.add(TriggerNotificationEvent.NonEmpty);
        }
        if (run.executionReport().failed().getValue(0L) > 0) {
            events.add(TriggerNotificationEvent.Failed);
        }

        for (TriggerNotification notificationCfg : trigger.notifications()) {
            if (events.contains(notificationCfg.event().getValue())) {
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

        // Distinguish test environments
        Integer enviromentId = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).enviromentId();
        if (enviromentId != null) {
            message += SimpleMessageFormat.format("Env{0}<br/>\n", enviromentId);
        }

        message += SimpleMessageFormat.format("Execution of process {0} is {1},<br/>\n", trigger.name(), run.status());
        if (trigger.triggerType().getValue().hasOption(PmcProcessOptions.RunForDay)) {
            message += SimpleMessageFormat.format("For Date: {0}<br/>\n", run.forDate());
        }
        message += SimpleMessageFormat.format("Statistics: {0}<br/>\n", run.executionReport().getStringView());
        if (!run.errorMessage().isNull()) {
            message += SimpleMessageFormat.format("Error Message: {0}<br/>\n", run.errorMessage());
        }

        message += SimpleMessageFormat.format("Execution details <a style=\"color:#929733\" href=\"{0}\">here</a>", //
                AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaApplication.operations, true), true,
                        new OperationsSiteMap.Management.TriggerRun().formViewerPlace(run.getPrimaryKey())));

        message += "</body></html>";
        m.setHtmlBody(message);

        boolean important = false;
        switch (run.status().getValue()) {
        case PartiallyCompleted:
        case Failed:
            important = true;
            break;
        default:
            break;
        }
        if (run.executionReport().erred().getValue(0L) > 0) {
            important = true;
        }

        if (important) {
            m.setHeader("Priority", "Urgent");
            m.setHeader("Importance", "high");
            m.setHeader("X-Priority", "1");
        }

        Mail.queueUofW(m, null, null);
    }
}
