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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.utils.EntityBinder;
import com.pyx4j.entity.shared.utils.EntityComparatorFactory;
import com.pyx4j.essentials.server.services.reports.ReportExporter;
import com.pyx4j.essentials.server.services.reports.ReportProgressStatus;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

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
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.tenant.lease.Lease;

public class EftReportGenerator implements ReportExporter {

    private static final I18n i18n = I18n.get(EftReportGenerator.class);

    private volatile ReportProgressStatusHolderExectutionMonitorAdapter reportProgressStatusHolder;

    private final EntityBinder<PaymentRecord, EftReportRecordDTO> dtoBinder;

    private BuildingsCriteriaNormalizer buildingCriteriaNormalizer;

    public EftReportGenerator() {
        dtoBinder = new EntityBinder<PaymentRecord, EftReportRecordDTO>(PaymentRecord.class, EftReportRecordDTO.class) {

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
                EftReportRecordDTO eftReportRecordDto = super.createTO(paymentRecord);
                switch (paymentRecord.paymentMethod().type().getValue()) {
                case Echeck:
                    EcheckInfo echeck = paymentRecord.paymentMethod().details().duplicate(EcheckInfo.class);
                    eftReportRecordDto.bankId().setValue(echeck.bankId().getValue());
                    eftReportRecordDto.transitNumber().setValue(echeck.branchTransitNumber().getValue());
                    if (SecurityController.checkBehavior(VistaCrmBehavior.PropertyVistaSupport)) {
                        eftReportRecordDto.accountNumber().setValue(echeck.accountNo().number().getValue());
                    } else {
                        eftReportRecordDto.accountNumber().setValue(echeck.accountNo().obfuscatedNumber().getValue());
                    }
                    break;
                default:
                    break;
                }
                return eftReportRecordDto;
            }
        };

        buildingCriteriaNormalizer = new BuildingsCriteriaNormalizer(EntityFactory.getEntityPrototype(PaymentRecord.class).billingAccount().lease()
                .unit().building());
    }

    @Override
    public Serializable generateReport(ReportMetadata metadata) {
        reportProgressStatusHolder = new ReportProgressStatusHolderExectutionMonitorAdapter();
        reportProgressStatusHolder.set(new ReportProgressStatus(i18n.tr("Generating Report"), 1, 2, 0, 100));

        EftReportMetadata reportMetadata = (EftReportMetadata) metadata;

        EftReportDataDTO reportData = EntityFactory.create(EftReportDataDTO.class);

        if (reportMetadata.forthcomingEft().isBooleanTrue()) {
            reportProgressStatusHolder.setExecutionMonitor(new ExecutionMonitor());
            // Create forthcoming payment records here
            Vector<PaymentRecord> paymentRecords = new Vector<PaymentRecord>();

            // Find PadGenerationDate for each BillingCycle in system, they may be different
            Set<LogicalDate> padGenerationDays = new HashSet<LogicalDate>();
            EntityQueryCriteria<BillingCycle> criteria = EntityQueryCriteria.create(BillingCycle.class);
            criteria.eq(criteria.proto().billingCycleStartDate(), reportMetadata.billingCycleStartDate().getValue());
            criteria.isNull(criteria.proto().actualAutopayExecutionDate());
            for (BillingCycle cycle : Persistence.secureQuery(criteria)) {
                padGenerationDays.add(cycle.targetAutopayExecutionDate().getValue());
            }

            List<Building> selectedBuildings = buildingCriteriaNormalizer.normalize(//@formatter:off
                    reportMetadata.filterByPortfolio().isBooleanTrue() ? reportMetadata.selectedPortfolios() : null,
                    reportMetadata.filterByBuildings().isBooleanTrue() ? reportMetadata.selectedBuildings() : null
            );//formatter:on

            for (LogicalDate padGenerationDate : padGenerationDays) {
                PreauthorizedPaymentsReportCriteria reportCriteria = new PreauthorizedPaymentsReportCriteria(padGenerationDate, selectedBuildings);
                if (reportMetadata.filterByExpectedMoveOut().isBooleanTrue()) {
                    reportCriteria.setExpectedMoveOutCriteris(reportMetadata.minimum().getValue(), reportMetadata.maximum().getValue());
                }
                reportCriteria.setLeasesOnNoticeOnly(reportMetadata.leasesOnNoticeOnly().isBooleanTrue());
                paymentRecords.addAll(ServerSideFactory.create(PaymentReportFacade.class).reportPreauthorisedPayments(reportCriteria,
                        reportProgressStatusHolder.getExecutionMonitor()));
            }

            if (!reportMetadata.orderBy().isNull()) {
                Collections.sort(
                        paymentRecords,
                        EntityComparatorFactory.createMemberComparator(dtoBinder.getBoundDboMemberPath(new Path(reportMetadata.orderBy().memberPath()
                                .getValue()))));
            }

            for (PaymentRecord paymentRecord : paymentRecords) {
                enhancePaymentRecord(paymentRecord);
                reportData.eftReportRecords().add(dtoBinder.createTO(paymentRecord));
            }

        } else {
            EntityQueryCriteria<PaymentRecord> criteria = makeCriteria(reportMetadata);
            int count = Persistence.service().count(criteria);
            int progress = 0;

            ICursorIterator<PaymentRecord> paymentRecordsIter = Persistence.secureQuery(null, criteria, AttachLevel.Attached);
            try {
                while (paymentRecordsIter.hasNext() & !reportProgressStatusHolder.isTerminationRequested()) {
                    PaymentRecord paymentRecord = paymentRecordsIter.next();
                    enhancePaymentRecord(paymentRecord);
                    reportData.eftReportRecords().add(dtoBinder.createTO(paymentRecord));
                    if (progress % 100 == 0) {
                        reportProgressStatusHolder.set(new ReportProgressStatus(i18n.tr("Gathering Data"), 1, 2, progress, count));
                    }
                    ++progress;
                }
            } finally {
                IOUtils.closeQuietly(paymentRecordsIter);
            }

        }

        reportData.agregateByBuildings().setValue(
                reportMetadata.orderBy().isEmpty()
                        || reportMetadata.orderBy().memberPath().getValue()
                                .equals(EntityFactory.getEntityPrototype(EftReportRecordDTO.class).building().getPath().toString()));

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
        @SuppressWarnings("unchecked")
        EftReportDataDTO reportData = (EftReportDataDTO) report;
        return new EftReportExport().createReport(reportData, reportProgressStatusHolder);
    }

    private EntityQueryCriteria<PaymentRecord> makeCriteria(EftReportMetadata reportMetadata) {
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.desc(criteria.proto().padBillingCycle().billingType());
        criteria.desc(criteria.proto().padBillingCycle().billingCycleStartDate());
        if (reportMetadata.orderBy().isNull()) {
            criteria.asc(criteria.proto().billingAccount().lease().unit().building().propertyCode());
            criteria.asc(criteria.proto().billingAccount().lease().unit().info().number());
            criteria.asc(criteria.proto().billingAccount().lease().leaseId());
            criteria.asc(criteria.proto().preauthorizedPayment().tenant().participantId());
            criteria.asc(criteria.proto().amount());
        } else {
            Sort orderBy = new Sort(criteria.proto().getMember(dtoBinder.getBoundDboMemberPath(new Path(reportMetadata.orderBy().memberPath().getValue()))),
                    reportMetadata.orderBy().isDesc().isBooleanTrue());
            criteria.sort(orderBy);
        }

        criteria.isNotNull(criteria.proto().padBillingCycle());

        criteria.eq(criteria.proto().billingAccount().lease().status(), Lease.Status.Active);
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

        buildingCriteriaNormalizer.addBuildingCriterion(//@formatter:off
                criteria,
                reportMetadata.filterByPortfolio().isBooleanTrue() ? reportMetadata.selectedPortfolios() : null,
                reportMetadata.filterByBuildings().isBooleanTrue() ? reportMetadata.selectedBuildings() : null
        );//@formatter:on

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

    private void normalizeBuildingsFilter(EftReportMetadata reportMetadata) {
        List<Building> selectedBuildings = buildingCriteriaNormalizer.normalize(//@formatter:off
                reportMetadata.filterByPortfolio().isBooleanTrue() ? reportMetadata.selectedPortfolios() : null,
                reportMetadata.filterByBuildings().isBooleanTrue() ? reportMetadata.selectedBuildings() : null
        );//@formatter:on
        reportMetadata.filterByBuildings().setValue(!selectedBuildings.isEmpty());
        reportMetadata.selectedBuildings().clear();
        reportMetadata.selectedBuildings().addAll(selectedBuildings);
    }

}
