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

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.report.EntityReportFormatter;
import com.pyx4j.essentials.server.report.ReportTableFormatter;
import com.pyx4j.essentials.server.report.ReportTableXLSXFormatter;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailAttachment;
import com.pyx4j.server.mail.MailMessage;
import com.pyx4j.server.mail.SMTPMailServiceConfig;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.AuditRecordEventType;
import com.propertyvista.domain.security.CrmRole;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerCreditCheck;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureCertificate;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.operations.domain.security.AuditRecord;
import com.propertyvista.server.domain.security.CrmUserCredential;
import com.propertyvista.server.jobs.TaskRunner;

class VistaBusinessStatsReport {

    private final static Logger log = LoggerFactory.getLogger(VistaBusinessStatsReport.class);

    static ReportTableFormatter startStatsReport() {

        ReportTableFormatter formatter;
        try {
            String fileName = IOUtils.resourceFileName("BusinessStatisticsReport.xlsx", VistaBusinessStatsReport.class);
            InputStream fileIs = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            formatter = new ReportTableXLSXFormatter(fileIs, true);
        } catch (IOException e) {
            log.error("Error", e);
            throw new UserRuntimeException("The workbook could not be retrieved", e);
        }
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

        formatter.header("Date of the report");
        formatter.newRow();
        formatter.cell(new LogicalDate());
        formatter.newRow();

        formatter.newRow();
        // Header for PMC data
        EntityReportFormatter<VistaBusinessStatsPmcModel> er = new EntityReportFormatter<VistaBusinessStatsPmcModel>(VistaBusinessStatsPmcModel.class);
        er.createHeader(formatter);

        return formatter;
    }

    static void processStatsReportsPmc(ExecutionMonitor executionMonitor, ReportTableFormatter formatter) {
        EntityReportFormatter<VistaBusinessStatsPmcModel> er = new EntityReportFormatter<VistaBusinessStatsPmcModel>(VistaBusinessStatsPmcModel.class);
        VistaBusinessStatsPmcModel data = EntityFactory.create(VistaBusinessStatsPmcModel.class);

        Date reportSince = DateUtils.addDays(new Date(), -7);

        Date monthlyPeriod = DateUtils.addMonths(new Date(), -1);

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

        data.country().setValue(pmc.features().countryOfOperation().getValue().toString());

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
            EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
            criteria.eq(criteria.proto().registeredInPortal(), Boolean.TRUE);
            data.registeredTenantsCount().setValue(Persistence.service().count(criteria));
        }

        {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.eq(criteria.proto().billingAccount().payments().$().createdBy(), CustomerUser.class);
            data.payingTenants().setValue(Persistence.service().count(criteria));
        }

        {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.eq(criteria.proto().billingAccount().payments().$().createdBy(), CustomerUser.class);
            criteria.ge(criteria.proto().billingAccount().payments().$().createdDate(), reportSince);
            data.newPayingTenants().setValue(Persistence.service().count(criteria));
        }

        {
            EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
            criteria.eq(criteria.proto().paymentStatus(), PaymentStatus.Cleared);
            criteria.eq(criteria.proto().paymentMethod().type(), PaymentType.Echeck);
            criteria.ge(criteria.proto().finalizeDate(), monthlyPeriod);
            List<PaymentRecord> records = Persistence.service().query(criteria);
            BigDecimal amount = BigDecimal.ZERO;
            for (PaymentRecord record : records) {
                amount = amount.add(record.amount().getValue());
            }
            data.eChequeCount().setValue(records.size());
            data.eChequeValue().setValue(amount);

            criteria.eq(criteria.proto().createdBy(), CustomerUser.class);
            records = Persistence.service().query(criteria);
            amount = BigDecimal.ZERO;
            for (PaymentRecord record : records) {
                amount = amount.add(record.amount().getValue());
            }

            data.eChequeCountOneTime().setValue(records.size());
            data.eChequeValueOneTime().setValue(amount);
        }

        {
            EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
            criteria.eq(criteria.proto().paymentStatus(), PaymentStatus.Cleared);
            criteria.eq(criteria.proto().paymentMethod().type(), PaymentType.DirectBanking);
            criteria.ge(criteria.proto().finalizeDate(), monthlyPeriod);
            List<PaymentRecord> records = Persistence.service().query(criteria);
            BigDecimal amount = BigDecimal.ZERO;
            for (PaymentRecord record : records) {
                amount = amount.add(record.amount().getValue());
            }
            data.eftCount().setValue(records.size());
            data.eftValue().setValue(amount);
        }

        {
            EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
            criteria.eq(criteria.proto().paymentStatus(), PaymentStatus.Cleared);
            criteria.eq(criteria.proto().paymentMethod().type(), PaymentType.Interac);
            criteria.ge(criteria.proto().finalizeDate(), monthlyPeriod);
            List<PaymentRecord> records = Persistence.service().query(criteria);
            BigDecimal amount = BigDecimal.ZERO;
            for (PaymentRecord record : records) {
                amount = amount.add(record.amount().getValue());
            }
            data.interacCount().setValue(records.size());
            data.interacValue().setValue(amount);
        }

        // TODO All cards are currently listed as Visa. When we can query by polymorphic entities the credit cards should be split in types.

        {
            EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
            criteria.eq(criteria.proto().paymentStatus(), PaymentStatus.Cleared);
            criteria.eq(criteria.proto().paymentMethod().type(), PaymentType.CreditCard);
            criteria.ge(criteria.proto().finalizeDate(), monthlyPeriod);
            List<PaymentRecord> records = Persistence.service().query(criteria);
            BigDecimal amount = BigDecimal.ZERO;
            for (PaymentRecord record : records) {
                amount = amount.add(record.amount().getValue());
            }
            data.creditVisaCount().setValue(records.size());
            data.creditVisaValue().setValue(amount);

            criteria.eq(criteria.proto().createdBy(), CustomerUser.class);
            records = Persistence.service().query(criteria);
            amount = BigDecimal.ZERO;
            for (PaymentRecord record : records) {
                amount = amount.add(record.amount().getValue());
            }

            data.creditVisaCountOneTime().setValue(records.size());
            data.creditVisaValueOneTime().setValue(amount);
        }

        {
            EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
            criteria.eq(criteria.proto().paymentStatus(), PaymentStatus.Cleared);
            criteria.eq(criteria.proto().paymentMethod().type(), PaymentType.Interac);
            criteria.ge(criteria.proto().finalizeDate(), monthlyPeriod);
            List<PaymentRecord> records = Persistence.service().query(criteria);
            BigDecimal amount = BigDecimal.ZERO;
            for (PaymentRecord record : records) {
                amount = amount.add(record.amount().getValue());
            }
            data.interacCount().setValue(records.size());
            data.interacValue().setValue(amount);
        }

        {
            EntityQueryCriteria<InsuranceTenantSureCertificate> criteria = EntityQueryCriteria.create(InsuranceTenantSureCertificate.class);
            criteria.eq(criteria.proto().status(), InsuranceTenantSureCertificate.TenantSureStatus.Active);
            data.insuranceCount().setValue(Persistence.service().count(criteria));
        }

        {
            EntityQueryCriteria<CustomerCreditCheck> criteria = EntityQueryCriteria.create(CustomerCreditCheck.class);
            criteria.ge(criteria.proto().creditCheckDate(), monthlyPeriod);
            data.processedReports().setValue(Persistence.service().count(criteria));
        }

        {

            List<CrmUserCredential> users = Persistence.service().query(EntityQueryCriteria.create(CrmUserCredential.class));
            CrmUserCredential crmUser = EntityFactory.create(CrmUserCredential.class);
            userLoop: for (CrmUserCredential user : users) {
                for (CrmRole role : user.roles()) {
                    for (VistaCrmBehavior behaviour : role.behaviors()) {
                        if (behaviour.equals(VistaCrmBehavior.PropertyVistaAccountOwner)) {
                            Persistence.service().retrieve(user.user());
                            crmUser = user;
                            break userLoop;
                        }
                    }
                }
            }

            data.contactName().setValue(crmUser.user().name() != null ? crmUser.user().name().getValue() : null);
            data.contactEmail().setValue(crmUser.user().email() != null ? crmUser.user().email().getValue() : null);
        }

        er.reportEntity(formatter, data);

    }

    static void completeStatsReport(ReportTableFormatter formater) {
        List<String> emails = new ArrayList<String>();
        SMTPMailServiceConfig mailConfig = (SMTPMailServiceConfig) ServerSideConfiguration.instance().getMailServiceConfigConfiguration();
        if (CommonsStringUtils.isStringSet(mailConfig.getForwardAllTo())) {
            emails.add(mailConfig.getForwardAllTo());
        } else if (VistaDeployment.isVistaProduction()) {
            emails.add("vista-operations-stats@propertyvista.com");
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
