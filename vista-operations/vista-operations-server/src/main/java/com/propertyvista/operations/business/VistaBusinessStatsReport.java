/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-10
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.essentials.server.report.ReportTableFormater;
import com.pyx4j.essentials.server.report.ReportTableXLSXFormater;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailMessage;
import com.pyx4j.server.mail.SMTPMailServiceConfig;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.operations.domain.scheduler.RunStats;

class VistaBusinessStatsReport {

    static ReportTableFormater startStatsReport() {
        ReportTableFormater formatter = new ReportTableXLSXFormater();
        formatter.header("test");
        return formatter;
    }

    static void processStatsReportsPmc(RunStats runStats, ReportTableFormater formater) {
        // TODO Auto-generated method stub
    }

    static void completeStatsReport(ReportTableFormater formater) {
        List<String> emails = new ArrayList<String>();
        SMTPMailServiceConfig mailConfig = (SMTPMailServiceConfig) ServerSideConfiguration.instance().getMailServiceConfigConfiguration();
        if (CommonsStringUtils.isStringSet(mailConfig.getForwardAllTo())) {
            emails.add(mailConfig.getForwardAllTo());
        } else if (VistaDeployment.isVistaProduction()) {
            emails.add("leonard@propertyvista.com");
            emails.add("vladsd@propertyvista.com");
        } else {
            emails.add("vista-operations-stats@pyx4j.com");
        }

        MailMessage m = new MailMessage();
        m.setTo(emails);
        m.setSender(ServerSideConfiguration.instance().getApplicationEmailSender());
        m.setSubject("Vista Business Operations Statistics Report " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        String message = "See attachments";
        m.setHtmlBody(message);

        Mail.send(m);
    }
}
