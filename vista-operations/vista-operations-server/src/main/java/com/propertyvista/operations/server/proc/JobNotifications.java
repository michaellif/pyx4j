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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.jatl.Html;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailMessage;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.biz.system.VistaSystemFacade;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.operations.domain.scheduler.PmcProcessOptions;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.domain.scheduler.RunData;
import com.propertyvista.operations.domain.scheduler.RunDataStatus;
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

        List<OperationsUser> targetUsers = new ArrayList<>();

        for (TriggerNotification notificationCfg : trigger.notifications()) {
            if (events.contains(notificationCfg.event().getValue())) {
                if (!targetUsers.contains(notificationCfg.user())) {
                    targetUsers.add(notificationCfg.user());
                }
            }
        }
        if (targetUsers.size() > 0) {
            sendNotification(trigger, run, targetUsers);
        }

    }

    private static void sendNotification(Trigger trigger, Run run, List<OperationsUser> targetUsers) {
        MailMessage m = new MailMessage();
        for (OperationsUser user : targetUsers) {
            m.setTo(user.email().getValue());
        }
        m.setSender(ServerSideConfiguration.instance().getApplicationEmailSender());
        m.setSubject(SimpleMessageFormat.format("Trigger Run notification {0} {1}", trigger.name(), run.status()));

        StringWriter stringWriter = new StringWriter();
        Html html = new Html(stringWriter);

        // Distinguish test environments
        Integer enviromentId = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).enviromentId();
        if (enviromentId != null) {
            html.text(SimpleMessageFormat.format("Env{0}", enviromentId)).br();
        }

        html.text(SimpleMessageFormat.format("Execution of process {0} is {1},", trigger.name(), run.status())).br();
        if (trigger.triggerType().getValue().hasOption(PmcProcessOptions.RunForDay)) {
            html.text(SimpleMessageFormat.format("For Date: {0}", run.forDate())).br();
        }
        html.text(SimpleMessageFormat.format("Statistics: {0}", run.executionReport().getStringView())).br();
        if (!run.errorMessage().isNull()) {
            html.text(SimpleMessageFormat.format("Error Message: {0}", run.errorMessage())).br();
        }

        EntityQueryCriteria<RunData> criteria = EntityQueryCriteria.create(RunData.class);
        criteria.eq(criteria.proto().execution(), run);
        criteria.ne(criteria.proto().status(), RunDataStatus.Processed);
        List<RunData> failedPmcData = Persistence.service().query(criteria);
        if (failedPmcData.size() > 0) {
            html.br();
            html.table().style("border: 1px solid rgb(221, 221, 221); border-collapse: collapse");

            String headStyle = "color: #666666; background-color: rgb(248, 248, 248); padding: 0 2px; border: 1px solid rgb(221, 221, 221);";
            String rowStyle = "border: 1px solid rgb(221, 221, 221);";

            html.th().style(headStyle).align("left").text("Pmc").end();
            html.th().style(headStyle).align("left").text("Status").end();
            html.th().style(headStyle).align("left").text("Stats").end();
            html.th().style(headStyle).align("left").text("ErrorMessage").end();

            for (RunData data : failedPmcData) {
                html.tr();
                html.td().style(rowStyle).text(data.pmc().name().getStringView()).end();
                html.td().style(rowStyle).text(data.status().getStringView()).end();
                html.td().style(rowStyle).text(data.executionReport().getStringView()).end();
                html.td().style(rowStyle).text(data.errorMessage().getStringView()).end();
                html.end();
            }
            html.end();
        }

        html.br();

        html.raw(SimpleMessageFormat.format("Execution details <a style=\"color:#929733\" href=\"{0}\">here</a>", //
                AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaApplication.operations, true), true,
                        new OperationsSiteMap.Management.TriggerRun().formViewerPlace(run.getPrimaryKey()))));

        m.setHtmlBody(stringWriter.getBuffer().toString());

        boolean important = false;
        switch (run.status().getValue()) {
        case PartiallyCompleted:
        case Failed:
            important = true;
            break;
        default:
            break;
        }
        if ((run.executionReport().erred().getValue(0L) > 0) || (run.executionReport().detailsErred().getValue(0L) > 0)) {
            important = true;
        }

        if (important) {
            m.setHeader("Priority", "Urgent");
            m.setHeader("Importance", "high");
            m.setHeader("X-Priority", "1");
        }

        Mail.queueUofW(m, null, ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getOperationsAlertMailServiceConfiguration());
    }
}
