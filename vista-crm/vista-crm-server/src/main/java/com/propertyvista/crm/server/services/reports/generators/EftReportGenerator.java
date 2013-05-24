/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.generators;

import java.io.Serializable;
import java.util.Vector;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.services.reports.ReportExporter;
import com.pyx4j.essentials.server.services.reports.ReportGenerator;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

import com.propertyvista.biz.financial.payment.PaymentProcessFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.reports.EftReportMetadata;
import com.propertyvista.domain.tenant.lease.Lease;

public class EftReportGenerator implements ReportGenerator, ReportExporter {

    @Override
    public Serializable generateReport(ReportMetadata metadata) {
        EftReportMetadata reportMetadata = (EftReportMetadata) metadata;

        if (reportMetadata.forthcomingEft().isBooleanTrue()) {
            // Create forthcoming payment records here
            Vector<PaymentRecord> paymentRecords = new Vector<PaymentRecord>();

            paymentRecords.addAll(ServerSideFactory.create(PaymentProcessFacade.class).reportPreauthorisedPayments(
                    reportMetadata.billingCycleStartDate().getValue()));

            for (PaymentRecord paymentRecord : paymentRecords) {
                enahancePaymentRecord(paymentRecord);
            }

            return paymentRecords;
        } else {

            EntityQueryCriteria<PaymentRecord> criteria = makeCriteria(reportMetadata);
            Vector<PaymentRecord> paymentRecords = new Vector<PaymentRecord>(Persistence.service().query(criteria));
            for (PaymentRecord paymentRecord : paymentRecords) {
                enahancePaymentRecord(paymentRecord);
            }
            return paymentRecords;
        }

    }

    private void enahancePaymentRecord(PaymentRecord paymentRecord) {
        Persistence.service().retrieve(paymentRecord.preauthorizedPayment().tenant());

        Lease lease = Persistence.service().retrieve(Lease.class, paymentRecord.preauthorizedPayment().tenant().lease().getPrimaryKey());
        Persistence.service().retrieve(lease.unit());
        Persistence.service().retrieve(lease.unit().building());

        paymentRecord.preauthorizedPayment().tenant().lease().set(null); // set to null to disable the 'detached' state
        paymentRecord.preauthorizedPayment().tenant().lease().setPrimaryKey(lease.getPrimaryKey());
        paymentRecord.preauthorizedPayment().tenant().lease().leaseId().setValue(lease.leaseId().getValue());
        paymentRecord.preauthorizedPayment().tenant().lease().setValuePopulated();

        paymentRecord.preauthorizedPayment().tenant().lease().unit().set(null);
        paymentRecord.preauthorizedPayment().tenant().lease().unit().setPrimaryKey(lease.unit().getPrimaryKey());
        paymentRecord.preauthorizedPayment().tenant().lease().unit().info().number().setValue(lease.unit().info().number().getValue());

        paymentRecord.preauthorizedPayment().tenant().lease().unit().building().set(null);
        paymentRecord.preauthorizedPayment().tenant().lease().unit().building().setPrimaryKey(lease.unit().building().getPrimaryKey());
        paymentRecord.preauthorizedPayment().tenant().lease().unit().building().propertyCode().setValue(lease.unit().building().propertyCode().getValue());

    }

    @Override
    public ExportedReport export(Serializable report) {
        Vector<PaymentRecord> paymentRecords = (Vector<PaymentRecord>) report;
        // TODO convert to excel sheet here:
        return new ExportedReport("blablabla.txt", "text/html", "blablabla".getBytes());
    }

    private EntityQueryCriteria<PaymentRecord> makeCriteria(EftReportMetadata reportMetadata) {
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.desc(criteria.proto().padBillingCycle().billingType());
        criteria.desc(criteria.proto().padBillingCycle().billingCycleStartDate());
        criteria.asc(criteria.proto().billingAccount().lease().unit().building().propertyCode());
        criteria.asc(criteria.proto().billingAccount().lease().unit().info().number());
        criteria.asc(criteria.proto().billingAccount().lease().leaseId());
        criteria.asc(criteria.proto().preauthorizedPayment().tenant().participantId());
        criteria.asc(criteria.proto().amount());

        criteria.isNotNull(criteria.proto().padBillingCycle());

        if (!reportMetadata.paymentStatus().isNull()) {
            criteria.eq(criteria.proto().paymentStatus(), reportMetadata.paymentStatus().getValue());
        }
        if (reportMetadata.onlyWithNotice().isBooleanTrue()) {
            criteria.isNotNull(criteria.proto().notice());
        }
        if (reportMetadata.filterByBillingCycle().isBooleanTrue()) {
            criteria.eq(criteria.proto().padBillingCycle().billingType().billingPeriod(), reportMetadata.billingPeriod());
            criteria.eq(criteria.proto().padBillingCycle().billingCycleStartDate(), reportMetadata.billingCycleStartDate());
        }
        if (reportMetadata.filterByBuildings().isBooleanTrue()) {
            if (!reportMetadata.selectedBuildings().isEmpty()) {
                criteria.in(criteria.proto().billingAccount().lease().unit().building(), reportMetadata.selectedBuildings());
            } else {
                criteria.isNull(criteria.proto().billingAccount().lease().unit().building());
            }
        }

        return criteria;
    }

}
