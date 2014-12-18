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
 */
package com.propertyvista.crm.server.services.reports.generators;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.server.CrudEntityBinder;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityComparatorFactory;
import com.pyx4j.essentials.server.services.reports.ReportExporter;
import com.pyx4j.essentials.server.services.reports.ReportProgressStatus;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.shared.domain.reports.ReportTemplate;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.payment.PaymentReportFacade;
import com.propertyvista.biz.financial.payment.PreauthorizedPaymentsReportCriteria;
import com.propertyvista.crm.rpc.dto.reports.EftReportDataDTO;
import com.propertyvista.crm.rpc.dto.reports.EftReportRecordDTO;
import com.propertyvista.crm.server.services.reports.util.ReportProgressStatusHolderExectutionMonitorAdapter;
import com.propertyvista.crm.server.util.BuildingsCriteriaNormalizer;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.reports.EftReportMetadata;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.domain.tenant.lease.Lease;

public class EftReportGenerator implements ReportExporter {

    private static final I18n i18n = I18n.get(EftReportGenerator.class);

    private volatile ReportProgressStatusHolderExectutionMonitorAdapter reportProgressStatusHolder;

    private final CrudEntityBinder<PaymentRecord, EftReportRecordDTO> dtoBinder;

    private BuildingsCriteriaNormalizer buildingCriteriaNormalizer;

    public EftReportGenerator() {
        dtoBinder = new CrudEntityBinder<PaymentRecord, EftReportRecordDTO>(PaymentRecord.class, EftReportRecordDTO.class) {

            @Override
            protected void bind() {
                bind(toProto.targetDate(), boProto.targetDate());
                bind(toProto.notice(), boProto.notice());
                bind(toProto.billingCycleStartDate(), boProto.padBillingCycle().billingCycleStartDate());
                bind(toProto.leaseId(), boProto.preauthorizedPayment().tenant().lease().leaseId());
                bind(toProto.leaseId_(), boProto.preauthorizedPayment().tenant().lease());
                bind(toProto.leaseStatus(), boProto.preauthorizedPayment().tenant().lease().status());
                bind(toProto.leaseFrom(), boProto.preauthorizedPayment().tenant().lease().leaseFrom());
                bind(toProto.leaseTo(), boProto.preauthorizedPayment().tenant().lease().leaseTo());
                bind(toProto.expectedMoveOut(), boProto.preauthorizedPayment().tenant().lease().expectedMoveOut());

                bind(toProto.comments(), boProto.preauthorizedPayment().comments());

                bind(toProto.building(), boProto.preauthorizedPayment().tenant().lease().unit().building().propertyCode());
                bind(toProto.building_(), boProto.preauthorizedPayment().tenant().lease().unit().building());

                bind(toProto.unit(), boProto.preauthorizedPayment().tenant().lease().unit().info().number());
                bind(toProto.unit_(), boProto.preauthorizedPayment().tenant().lease().unit());
                bind(toProto.participantId(), boProto.preauthorizedPayment().tenant().participantId());
                bind(toProto.customer(), boProto.preauthorizedPayment().tenant().customer());
                bind(toProto.customer_(), boProto.preauthorizedPayment().tenant());
                bind(toProto.amount(), boProto.amount());
                bind(toProto.amount_().id(), boProto.id());
                bind(toProto.paymentType(), boProto.paymentMethod().type());
                bind(toProto.paymentStatus(), boProto.paymentStatus());
            }

            @Override
            public EftReportRecordDTO createTO(PaymentRecord paymentRecord) {
                EftReportRecordDTO to = super.createTO(paymentRecord);
                switch (paymentRecord.paymentMethod().type().getValue()) {
                case Echeck:
                    EcheckInfo echeck = paymentRecord.paymentMethod().details().duplicate(EcheckInfo.class);
                    to.bankId().setValue(echeck.bankId().getValue());
                    to.transitNumber().setValue(echeck.branchTransitNumber().getValue());
                    if (SecurityController.check(VistaBasicBehavior.PropertyVistaSupport)) {
                        to.accountNumber().setValue(echeck.accountNo().number().getValue());
                    } else {
                        to.accountNumber().setValue(echeck.accountNo().obfuscatedNumber().getValue());
                    }
                    break;
                default:
                    break;
                }
                return to;
            }
        };

        buildingCriteriaNormalizer = new BuildingsCriteriaNormalizer(EntityFactory.getEntityPrototype(PaymentRecord.class).billingAccount().lease().unit()
                .building());
    }

    @Override
    public Serializable generateReport(ReportTemplate metadata) {
        reportProgressStatusHolder = new ReportProgressStatusHolderExectutionMonitorAdapter();

        EftReportMetadata reportMetadata = (EftReportMetadata) metadata;
        EftReportDataDTO reportData = EntityFactory.create(EftReportDataDTO.class);

        if (reportMetadata.forthcomingEft().getValue(false)) {
            reportProgressStatusHolder.setExecutionMonitor(new ExecutionMonitor());

            Set<LogicalDate> padGenerationDays = new HashSet<LogicalDate>();
            Vector<PaymentRecord> paymentRecords = new Vector<PaymentRecord>();

            if (!reportProgressStatusHolder.isTerminationRequested()) {
                // Find PadGenerationDate for each BillingCycle in system, they may be different
                EntityQueryCriteria<BillingCycle> criteria = EntityQueryCriteria.create(BillingCycle.class);
                criteria.eq(criteria.proto().billingCycleStartDate(), reportMetadata.billingCycleStartDate().getValue());
                criteria.isNull(criteria.proto().actualAutopayExecutionDate());
                for (BillingCycle cycle : Persistence.secureQuery(criteria)) {
                    if (reportProgressStatusHolder.isTerminationRequested()) {
                        break;
                    }
                    padGenerationDays.add(cycle.targetAutopayExecutionDate().getValue());
                }
            }

            if (!reportProgressStatusHolder.isTerminationRequested()) {
                List<Building> selectedBuildings = buildingCriteriaNormalizer.normalize( //
                        reportMetadata.filterByPortfolio().getValue(false) ? reportMetadata.selectedPortfolios() : null, //
                        reportMetadata.filterByBuildings().getValue(false) ? reportMetadata.selectedBuildings() : null);

                int progress = 0;
                int count = padGenerationDays.size();
                for (LogicalDate padGenerationDate : padGenerationDays) {
                    if (reportProgressStatusHolder.isTerminationRequested()) {
                        break;
                    }
                    PreauthorizedPaymentsReportCriteria reportCriteria = new PreauthorizedPaymentsReportCriteria(padGenerationDate, selectedBuildings);
                    if (reportMetadata.filterByExpectedMoveOut().getValue(false)) {
                        reportCriteria.setExpectedMoveOutCriteris(reportMetadata.minimum().getValue(), reportMetadata.maximum().getValue());
                    }
                    reportCriteria.setLeasesOnNoticeOnly(reportMetadata.leasesOnNoticeOnly().getValue(false));
                    reportCriteria.setTrace(reportMetadata.trace().getValue(false));
                    paymentRecords.addAll(ServerSideFactory.create(PaymentReportFacade.class).reportPreauthorisedPayments(reportCriteria,
                            reportProgressStatusHolder.getExecutionMonitor()));
                    reportProgressStatusHolder.set(new ReportProgressStatus(i18n.tr("Gathering Data"), 1, 2, progress++, count));
                }
            }

            if (!reportProgressStatusHolder.isTerminationRequested()) {
                int progress = 0;
                int count = padGenerationDays.size();

                if (!reportMetadata.orderBy().isNull()) {
                    Path path = dtoBinder.getBoundBOMemberPath(new Path(reportMetadata.orderBy().memberPath().getValue()));
                    for (PaymentRecord paymentRecord : paymentRecords) {
                        enhancePaymentRecord(paymentRecord);
                    }
                    if (reportMetadata.orderBy().isDesc().getValue(false)) {
                        Collections.sort(paymentRecords, Collections.reverseOrder(EntityComparatorFactory.createMemberComparator(path)));
                    } else {
                        Collections.sort(paymentRecords, EntityComparatorFactory.createMemberComparator(path));
                    }
                }

                for (PaymentRecord paymentRecord : paymentRecords) {
                    if (reportProgressStatusHolder.isTerminationRequested()) {
                        break;
                    }
                    enhancePaymentRecord(paymentRecord);
                    reportData.eftReportRecords().add(dtoBinder.createTO(paymentRecord));
                    reportProgressStatusHolder.set(new ReportProgressStatus(i18n.tr("Gathering Data"), 2, 2, progress++, count));
                }
            }

        } else if (!reportProgressStatusHolder.isTerminationRequested()) {
            EntityQueryCriteria<PaymentRecord> criteria = makeCriteria(reportMetadata);

            int progress = 0;
            int count = Persistence.service().count(criteria);
            ICursorIterator<PaymentRecord> paymentRecordsIter = Persistence.secureQuery(null, criteria, AttachLevel.Attached);
            try {
                while (paymentRecordsIter.hasNext() && !reportProgressStatusHolder.isTerminationRequested()) {
                    PaymentRecord paymentRecord = paymentRecordsIter.next();
                    enhancePaymentRecord(paymentRecord);
                    reportData.eftReportRecords().add(dtoBinder.createTO(paymentRecord));
                    if (progress % 100 == 0) {
                        reportProgressStatusHolder.set(new ReportProgressStatus(i18n.tr("Gathering Data"), 1, 1, progress, count));
                    }
                    ++progress;
                }
            } finally {
                IOUtils.closeQuietly(paymentRecordsIter);
            }
        }

        if (!reportProgressStatusHolder.isTerminationRequested()) {
            reportData.agregateByBuildings().setValue(
                    reportMetadata.orderBy().isEmpty()
                            || reportMetadata.orderBy().memberPath().getValue()
                                    .equals(EntityFactory.getEntityPrototype(EftReportRecordDTO.class).building().getPath().toString()));

        }
        return reportData;
    }

    @Override
    public void abort() {
        reportProgressStatusHolder.requestTermination();
    }

    @Override
    public synchronized ReportProgressStatus getProgressStatus() {
        return reportProgressStatusHolder.get();
    }

    @Override
    public ExportedReport export(Serializable report) {
        EftReportDataDTO reportData = (EftReportDataDTO) report;
        return new EftReportExport().createReport(reportData, reportProgressStatusHolder);
    }

    private EntityQueryCriteria<PaymentRecord> makeCriteria(EftReportMetadata reportMetadata) {
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        if (reportMetadata.orderBy().isNull()) {
            criteria.desc(criteria.proto().padBillingCycle().billingCycleStartDate());
            criteria.asc(criteria.proto().billingAccount().lease().unit().building().propertyCode());
            criteria.asc(criteria.proto().billingAccount().lease().unit().info().number());
            criteria.asc(criteria.proto().billingAccount().lease().leaseId());
            criteria.asc(criteria.proto().preauthorizedPayment().tenant().participantId());
            criteria.asc(criteria.proto().amount());
        } else {
            Sort orderBy = new Sort(criteria.proto().getMember(dtoBinder.getBoundBOMemberPath(new Path(reportMetadata.orderBy().memberPath().getValue()))),
                    reportMetadata.orderBy().isDesc().getValue(false));
            criteria.sort(orderBy);
            criteria.desc(criteria.proto().padBillingCycle().billingCycleStartDate());
        }

        criteria.isNotNull(criteria.proto().padBillingCycle());

        criteria.eq(criteria.proto().billingAccount().lease().status(), Lease.Status.Active);
        if (reportMetadata.leasesOnNoticeOnly().getValue(false)) {
            criteria.eq(criteria.proto().billingAccount().lease().completion(), Lease.CompletionType.Notice);
        }
        if (!reportMetadata.paymentStatus().isNull()) {
            criteria.eq(criteria.proto().paymentStatus(), reportMetadata.paymentStatus().getValue());
        }
        if (reportMetadata.onlyWithNotice().getValue(false)) {
            criteria.isNotNull(criteria.proto().notice());
        }
        if (reportMetadata.filterByBillingCycle().getValue(false)) {
            criteria.eq(criteria.proto().padBillingCycle().billingType().billingPeriod(), reportMetadata.billingPeriod());
            criteria.eq(criteria.proto().padBillingCycle().billingCycleStartDate(), reportMetadata.billingCycleStartDate());
        }

        buildingCriteriaNormalizer.addBuildingCriterion(//@formatter:off
                criteria,
                reportMetadata.filterByPortfolio().getValue(false) ? reportMetadata.selectedPortfolios() : null,
                reportMetadata.filterByBuildings().getValue(false) ? reportMetadata.selectedBuildings() : null
        );//@formatter:on

        if (reportMetadata.filterByExpectedMoveOut().getValue(false)) {
            criteria.ge(criteria.proto().billingAccount().lease().expectedMoveOut(), reportMetadata.minimum());
            criteria.le(criteria.proto().billingAccount().lease().expectedMoveOut(), reportMetadata.maximum());
        }

        return criteria;
    }

    private void enhancePaymentRecord(PaymentRecord paymentRecord) {
        Persistence.ensureRetrieve(paymentRecord.preauthorizedPayment().tenant().lease().unit().building(), AttachLevel.Attached);
        Lease lease = paymentRecord.preauthorizedPayment().tenant().lease().duplicate();

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

}
