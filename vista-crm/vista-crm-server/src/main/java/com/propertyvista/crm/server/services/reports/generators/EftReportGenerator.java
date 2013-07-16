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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.services.reports.ReportExporter;
import com.pyx4j.essentials.server.services.reports.ReportProgressStatus;
import com.pyx4j.essentials.server.services.reports.ReportProgressStatusHolder;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

import com.propertyvista.biz.financial.payment.PaymentReportFacade;
import com.propertyvista.biz.financial.payment.PreauthorizedPaymentsReportCriteria;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.reports.EftReportMetadata;
import com.propertyvista.domain.tenant.lease.Lease;

public class EftReportGenerator implements ReportExporter {

    private static final I18n i18n = I18n.get(EftReportGenerator.class);

    private final ReportProgressStatusHolder reportProgressStatusHolder;

    private volatile boolean aborted;

    public EftReportGenerator() {
        aborted = false;
        reportProgressStatusHolder = new ReportProgressStatusHolder();
    }

    @Override
    public Serializable generateReport(ReportMetadata metadata) {
        reportProgressStatusHolder.set(new ReportProgressStatus(i18n.tr("Gathering Data"), 1, 2, 0, 100));
        EftReportMetadata reportMetadata = (EftReportMetadata) metadata;

        if (reportMetadata.forthcomingEft().isBooleanTrue()) {
            // Create forthcoming payment records here
            Vector<PaymentRecord> paymentRecords = new Vector<PaymentRecord>();

            // Find PadGenerationDate for each BillingCycle in system, they may be different
            Set<LogicalDate> padGenerationDays = new HashSet<LogicalDate>();
            EntityQueryCriteria<BillingCycle> criteria = EntityQueryCriteria.create(BillingCycle.class);
            criteria.eq(criteria.proto().billingCycleStartDate(), reportMetadata.billingCycleStartDate().getValue());
            criteria.isNull(criteria.proto().actualPadGenerationDate());
            for (BillingCycle cycle : Persistence.secureQuery(criteria)) {
                padGenerationDays.add(cycle.targetPadGenerationDate().getValue());
            }

            List<Building> selectedBuildings = null;
            if (reportMetadata.filterByBuildings().getValue(false) && (!reportMetadata.selectedBuildings().isEmpty())) {
                selectedBuildings = reportMetadata.selectedBuildings();
            }

            for (LogicalDate padGenerationDate : padGenerationDays) {
                PreauthorizedPaymentsReportCriteria reportCriteria = new PreauthorizedPaymentsReportCriteria(padGenerationDate, selectedBuildings);
                if (reportMetadata.filterByExpectedMoveOut().isBooleanTrue()) {
                    reportCriteria.setExpectedMoveOutCriteris(reportMetadata.minimum().getValue(), reportMetadata.maximum().getValue());
                }
                reportCriteria.setLeasesOnNoticeOnly(reportMetadata.leasesOnNoticeOnly().isBooleanTrue());
                paymentRecords.addAll(ServerSideFactory.create(PaymentReportFacade.class).reportPreauthorisedPayments(reportCriteria));
            }

            for (PaymentRecord paymentRecord : paymentRecords) {
                enhancePaymentRecord(paymentRecord);
            }
            return paymentRecords;

        } else {
            EntityQueryCriteria<PaymentRecord> criteria = makeCriteria(reportMetadata);
            int count = Persistence.service().count(criteria);
            int progress = 0;

            ICursorIterator<PaymentRecord> paymentRecordsIter = Persistence.secureQuery(null, criteria, AttachLevel.Attached);
            Vector<PaymentRecord> paymentRecords = new Vector<PaymentRecord>(count);
            try {
                while (paymentRecordsIter.hasNext() & !aborted) {
                    if (progress % 10 == 0) {
                        reportProgressStatusHolder.set(new ReportProgressStatus(i18n.tr("Gathering Data"), 1, 2, progress, count));
                    }
                    PaymentRecord paymentRecord = paymentRecordsIter.next();
                    enhancePaymentRecord(paymentRecord);
                    paymentRecords.add(paymentRecord);
                }
            } finally {
                IOUtils.closeQuietly(paymentRecordsIter);
            }

            return paymentRecords;
        }

    }

    @Override
    public void abort() {
        this.aborted = true;
    }

    @Override
    public synchronized ReportProgressStatus getProgressStatus() {
        return reportProgressStatusHolder.get();
    }

    @Override
    public ExportedReport export(Serializable report) {
        @SuppressWarnings("unchecked")
        Vector<PaymentRecord> paymentRecords = (Vector<PaymentRecord>) report;
        return new EftReportExport().createReport(paymentRecords, reportProgressStatusHolder);
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

        if (reportMetadata.leasesOnNoticeOnly().isBooleanTrue()) {
            criteria.eq(criteria.proto().billingAccount().lease().completion(), Lease.CompletionType.Notice);
        }
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
        if (reportMetadata.filterByExpectedMoveOut().isBooleanTrue()) {
            criteria.ge(criteria.proto().billingAccount().lease().expectedMoveOut(), reportMetadata.minimum());
            criteria.le(criteria.proto().billingAccount().lease().expectedMoveOut(), reportMetadata.maximum());
        }

        return criteria;
    }

    private void enhancePaymentRecord(PaymentRecord paymentRecord) {
        Persistence.service().retrieve(paymentRecord.preauthorizedPayment().tenant());

        Lease lease = Persistence.service().retrieve(Lease.class, paymentRecord.preauthorizedPayment().tenant().lease().getPrimaryKey());
        Persistence.service().retrieve(lease.unit());
        Persistence.service().retrieve(lease.unit().building());

        paymentRecord.preauthorizedPayment().tenant().lease().set(lease.duplicate());
        // Clear unused values
        paymentRecord.preauthorizedPayment().tenant().lease().billingAccount().setValueDetached();
        paymentRecord.preauthorizedPayment().tenant().lease().currentTerm().setValueDetached();
        paymentRecord.preauthorizedPayment().tenant().lease().previousTerm().setValueDetached();
        paymentRecord.preauthorizedPayment().tenant().lease().nextTerm().setValueDetached();

        paymentRecord.preauthorizedPayment().tenant().lease().unit().set(null);
        paymentRecord.preauthorizedPayment().tenant().lease().unit().setPrimaryKey(lease.unit().getPrimaryKey());
        paymentRecord.preauthorizedPayment().tenant().lease().unit().info().number().setValue(lease.unit().info().number().getValue());

        paymentRecord.preauthorizedPayment().tenant().lease().unit().building().set(null);
        paymentRecord.preauthorizedPayment().tenant().lease().unit().building().setPrimaryKey(lease.unit().building().getPrimaryKey());
        paymentRecord.preauthorizedPayment().tenant().lease().unit().building().propertyCode().setValue(lease.unit().building().propertyCode().getValue());
    }

    /** this is for testing progress UI */
    private void makeMockupProgress() {
        int dummyMax = 1000;
        for (int i = 0; i < dummyMax; ++i) {
            if (aborted) {
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            reportProgressStatusHolder.set(new ReportProgressStatus(i18n.tr("Gathering Data"), 1, 2, i, dummyMax));

        }
    }

}
