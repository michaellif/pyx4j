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
package com.propertyvista.biz.operations.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang.time.DateUtils;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.report.EntityReportFormatter;
import com.pyx4j.essentials.server.report.ReportTableFormatter;
import com.pyx4j.essentials.server.report.ReportTableXLSXFormatter;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailAttachment;
import com.pyx4j.server.mail.MailMessage;
import com.pyx4j.server.mail.SMTPMailServiceConfig;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.AuditRecordEventType;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure;
import com.propertyvista.operations.domain.scheduler.RunStats;
import com.propertyvista.operations.domain.security.AuditRecord;
import com.propertyvista.server.jobs.TaskRunner;

class VistaBusinessStatsReport {

    static ReportTableFormatter startStatsReport() {
        ReportTableFormatter formatter = new ReportTableXLSXFormatter();
        {
            formatter.header("PMC Total");
            formatter.newRow();

            EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
            formatter.cell(Persistence.service().count(criteria));
            formatter.newRow();
        }

        Date reportSince = DateUtils.addDays(new Date(), -7);
        {
            formatter.header("PMC Created in last week");
            formatter.newRow();

            EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
            criteria.ge(criteria.proto().created(), reportSince);
            formatter.cell(Persistence.service().count(criteria));
            formatter.newRow();
        }
        {
            formatter.header("Total Login to CRM in last week");
            formatter.newRow();

            EntityQueryCriteria<AuditRecord> criteria = EntityQueryCriteria.create(AuditRecord.class);
            criteria.ge(criteria.proto().created(), reportSince);
            formatter.cell(Persistence.service().count(criteria));
            formatter.newRow();
        }

        formatter.newRow();
        // Header for PMC data
        EntityReportFormatter<VistaBusinessStatsPmcData> er = new EntityReportFormatter<VistaBusinessStatsPmcData>(VistaBusinessStatsPmcData.class);
        er.createHeader(formatter);

        return formatter;
    }

    static void processStatsReportsPmc(RunStats runStats, ReportTableFormatter formatter) {
        EntityReportFormatter<VistaBusinessStatsPmcData> er = new EntityReportFormatter<VistaBusinessStatsPmcData>(VistaBusinessStatsPmcData.class);
        VistaBusinessStatsPmcData data = EntityFactory.create(VistaBusinessStatsPmcData.class);

        Date reportSince = DateUtils.addDays(new Date(), -7);

        final Pmc pmc = VistaDeployment.getCurrentPmc();
        data.name().setValue(pmc.name().getStringView());

        AuditRecord auditRecord = TaskRunner.runInOperationsNamespace(new Callable<AuditRecord>() {
            @Override
            public AuditRecord call() {
                EntityQueryCriteria<AuditRecord> criteria = EntityQueryCriteria.create(AuditRecord.class);
                criteria.eq(criteria.proto().event(), AuditRecordEventType.Login);
                criteria.eq(criteria.proto().namespace(), pmc.namespace());
                criteria.desc(criteria.proto().created());
                return Persistence.service().retrieve(criteria);
            }
        });
        if (auditRecord != null) {
            data.lastLogin().setValue(auditRecord.created().getValue());
            data.active().setValue(data.lastLogin().getValue().after(reportSince));
        }

        {
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            data.buildingCount().setValue(Persistence.service().count(criteria));
        }
        {
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            criteria.ge(criteria.proto().created(), reportSince);
            data.newBuildingCount().setValue(Persistence.service().count(criteria));
        }
        {
            EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
            data.unitsCount().setValue(Persistence.service().count(criteria));
        }
        {
            EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
            data.tenantsCount().setValue(Persistence.service().count(criteria));
        }
        {
            EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
            criteria.ge(criteria.proto().created(), reportSince);
            data.newTenantsCount().setValue(Persistence.service().count(criteria));
        }
        {
            EntityQueryCriteria<InsuranceTenantSure> criteria = EntityQueryCriteria.create(InsuranceTenantSure.class);
            criteria.eq(criteria.proto().status(), InsuranceTenantSure.TenantSureStatus.Active);
            data.tenantInsurance().setValue(Persistence.service().count(criteria));
        }
        er.reportEntity(formatter, data);
    }

    static void completeStatsReport(ReportTableFormatter formater) {
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

        String message = SimpleMessageFormat.format("Statistics Report generated on {0,date,short}<p/> See attached Excel file", new Date());
        m.setHtmlBody(message);

        String fileName = "OperationsStatistics-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xlsx";
        m.addAttachment(new MailAttachment(fileName, formater.getContentType(), formater.getBinaryData()));

        Mail.send(m);
    }
}
