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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.report.EntityReportFormatter;
import com.pyx4j.essentials.server.report.ReportTableFormatter;
import com.pyx4j.essentials.server.report.ReportTableXLSXFormatter;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailAttachment;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.util.DomainUtil;

class CaledonReport {

    private final static Logger log = LoggerFactory.getLogger(CaledonReport.class);

    static ReportTableFormatter startStatsReport() {
        ReportTableFormatter formatter = new ReportTableXLSXFormatter(true);

        // Header for data
        EntityReportFormatter<CaledonReportModel> er = new EntityReportFormatter<CaledonReportModel>(CaledonReportModel.class);
        er.createHeader(formatter);

        return formatter;
    }

    static void processStatsReportsPmc(ExecutionMonitor executionMonitor, ReportTableFormatter formatter) {
        ICursorIterator<MerchantAccount> accountIterator;
        { //TODO->Closure
            EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
            criteria.isNotNull(criteria.proto()._buildings());
            accountIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        }
        try {
            while (accountIterator.hasNext()) {
                createAccountStats(formatter, accountIterator.next());
                if (executionMonitor.isTerminationRequested()) {
                    break;
                }
            }
        } finally {
            accountIterator.close();
        }
    }

    private static void createAccountStats(ReportTableFormatter formatter, MerchantAccount merchantAccount) {
        EntityReportFormatter<CaledonReportModel> er = new EntityReportFormatter<CaledonReportModel>(CaledonReportModel.class);
        CaledonReportModel model = EntityFactory.create(CaledonReportModel.class);

        model.MID().setValue(merchantAccount.getStringView());

        {
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            criteria.eq(criteria.proto().merchantAccounts().$().merchantAccount(), merchantAccount);
            model.buildings().setValue(Persistence.service().count(criteria));
        }
        {
            EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
            criteria.eq(criteria.proto().building().merchantAccounts().$().merchantAccount(), merchantAccount);
            model.units().setValue(Persistence.service().count(criteria));
        }

        // Calculate Average Rent,  Average EFT as total,    Max Lease charges
        model.leaseCount().setValue(0);
        model.eftCount().setValue(0);
        model.averageEFT().setValue(BigDecimal.ZERO);
        model.averageRent().setValue(BigDecimal.ZERO);
        model.maxLeaseCharges().setValue(BigDecimal.ZERO);

        {
            ICursorIterator<Lease> leaseIterator;
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.eq(criteria.proto().unit().building().merchantAccounts().$().merchantAccount(), merchantAccount);
            criteria.eq(criteria.proto().status(), Lease.Status.Active);
            leaseIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
            try {
                while (leaseIterator.hasNext()) {
                    calulateLeaseStats(leaseIterator.next(), model);
                }
            } finally {
                leaseIterator.close();
            }
        }

        if (model.eftCount().getValue() != 0) {
            model.averageEFT().setValue(model.averageEFT().getValue().divide(new BigDecimal(model.eftCount().getValue()), 2, RoundingMode.FLOOR));
        }
        if (model.leaseCount().getValue() != 0) {
            model.averageRent().setValue(model.averageRent().getValue().divide(new BigDecimal(model.leaseCount().getValue()), 2, RoundingMode.FLOOR));
        }

        model.pmcName().setValue(VistaDeployment.getCurrentPmc().name().getStringView());
        er.reportEntity(formatter, model);
    }

    private static void calulateLeaseStats(Lease lease, CaledonReportModel model) {
        model.leaseCount().setValue(model.leaseCount().getValue() + 1);
        model.averageRent().setValue(model.averageRent().getValue().add(lease.currentTerm().version().leaseProducts().serviceItem().agreedPrice().getValue()));

        // Calculate Max Lease charges
        BigDecimal leaseCharges = BigDecimal.ZERO;

        LogicalDate date = new LogicalDate(new Date());
        BillingCycle cycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(lease, date);
        List<InvoiceProductCharge> charges = ServerSideFactory.create(ARFacade.class).estimateLeaseCharges(cycle, lease);

        for (InvoiceProductCharge charge : charges) {
            leaseCharges = leaseCharges.add(charge.amount().getValue());
        }

        model.maxLeaseCharges().setValue(DomainUtil.max(model.maxLeaseCharges().getValue(), leaseCharges));

        // Calculate total EFT
        BillingCycle nextBillingCycle = ServerSideFactory.create(PaymentMethodFacade.class).getNextScheduledPreauthorizedPaymentBillingCycle(lease);
        for (PaymentRecord paymentRecord : ServerSideFactory.create(PaymentMethodFacade.class).calulatePreauthorizedPayment(nextBillingCycle,
                lease.billingAccount())) {
            model.eftCount().setValue(model.eftCount().getValue() + 1);
            model.averageEFT().setValue(model.averageEFT().getValue().add(paymentRecord.amount().getValue()));
        }
    }

    static void completeStatsReport(ReportTableFormatter formater) {
        List<String> emails = new ArrayList<String>();
        // mail to domain will not be sent in test env
        emails.add("kevin.bullock@caledoncard.com");
        if (VistaDeployment.isVistaProduction()) {
            emails.add("vista-operations-stats@propertyvista.com");
        }

        MailMessage m = new MailMessage();
        m.setTo(emails);
        m.setSender(ServerSideConfiguration.instance().getApplicationEmailSender());
        m.setSubject("Vista Rent Statistics Report " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        String message = SimpleMessageFormat.format("Statistics Report generated on {0,date,short}<p/> See attached Excel file", new Date());
        m.setHtmlBody(message);

        String fileName = "rentStatistics-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xlsx";
        m.addAttachment(new MailAttachment(fileName, formater.getContentType(), formater.getBinaryData()));

        Mail.send(m);
    }
}
