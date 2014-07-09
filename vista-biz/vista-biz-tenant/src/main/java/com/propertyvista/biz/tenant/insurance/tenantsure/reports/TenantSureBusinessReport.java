/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 9, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance.tenantsure.reports;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.report.EntityReportFormatter;
import com.pyx4j.essentials.server.report.ReportTableFormatter;
import com.pyx4j.essentials.server.report.ReportTableXLSXFormatter;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailAttachment;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy.TenantSureStatus;

public class TenantSureBusinessReport {

    public ReportTableFormatter start() {
        ReportTableFormatter formatter = new ReportTableXLSXFormatter(true);
        // Header for data
        EntityReportFormatter<TenantSureBusinessReportModel> er = new EntityReportFormatter<TenantSureBusinessReportModel>(TenantSureBusinessReportModel.class);
        er.createHeader(formatter);
        return formatter;
    }

    public void processReport(ExecutionMonitor executionMonitor, ReportTableFormatter formatter) {
        EntityReportFormatter<TenantSureBusinessReportModel> er = new EntityReportFormatter<TenantSureBusinessReportModel>(TenantSureBusinessReportModel.class);

        EntityQueryCriteria<TenantSureInsurancePolicy> criteria = EntityQueryCriteria.create(TenantSureInsurancePolicy.class);
        criteria.in(criteria.proto().status(), EnumSet.of(TenantSureStatus.Active, TenantSureStatus.PendingCancellation));
        ICursorIterator<TenantSureInsurancePolicy> iterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (iterator.hasNext()) {
                TenantSureInsurancePolicy policy = iterator.next();

                TenantSureBusinessReportModel model = EntityFactory.create(TenantSureBusinessReportModel.class);

                model.firstName().setValue(policy.client().tenant().customer().person().name().firstName().getValue());
                model.lastName().setValue(policy.client().tenant().customer().person().name().lastName().getValue());
                model.insuranceCertificateNumber().setValue(policy.certificate().insuranceCertificateNumber().getValue());
                model.status().setValue(policy.status().getValue());
                model.inceptionDate().setValue(policy.certificate().inceptionDate().getValue());
                model.expiryDate().setValue(policy.certificate().expiryDate().getValue());

                er.reportEntity(formatter, model);

                executionMonitor.addProcessedEvent("TenantSure MonthlyPayable", policy.totalMonthlyPayable().getValue());
            }
        } finally {
            iterator.close();
        }
    }

    public void completeReport(ReportTableFormatter formatter) {
        List<String> emails = new ArrayList<String>();
        if (VistaDeployment.isVistaProduction()) {
            // mail to domain will not be sent in test env
            emails.add("dgarland@highcourtpartners.com");
            emails.add("vista-operations-stats@propertyvista.com");
        } else {
            emails.add("test-emails@propertyvista.be");
        }

        MailMessage m = new MailMessage();
        m.setTo(emails);
        m.setSender(ServerSideConfiguration.instance().getApplicationEmailSender());
        m.setSubject("Vista TenantSure Report " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        String message = SimpleMessageFormat.format("TenantSure Report generated on {0,date,short}<p/> See attached Excel file", new Date());
        m.setHtmlBody(message);

        String fileName = "vistaTenantSure-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xlsx";
        m.addAttachment(new MailAttachment(fileName, formatter.getContentType(), formatter.getBinaryData()));

        Mail.queueUofW(m, null, null);
    }

}
