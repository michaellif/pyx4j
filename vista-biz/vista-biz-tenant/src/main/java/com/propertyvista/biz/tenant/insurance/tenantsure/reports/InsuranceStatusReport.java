/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-26
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance.tenantsure.reports;

import java.util.Date;
import java.util.EnumSet;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.AndCriterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.report.EntityReportFormatter;
import com.pyx4j.essentials.server.report.ReportTableFormatter;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.tenant.insurance.TenantSureReportStatusData;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicyReport;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicyReport.ReportedStatus;
import com.propertyvista.domain.tenant.insurance.TenantSureInsuranceCertificate;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy.TenantSureStatus;

public class InsuranceStatusReport implements Report {

    @Override
    public void start(ReportTableFormatter formatter) {
        EntityReportFormatter<TenantSureReportStatusData> er = new EntityReportFormatter<TenantSureReportStatusData>(TenantSureReportStatusData.class);
        er.createHeader(formatter);
    }

    @Override
    public void processReport(ExecutionMonitor executionMonitor, Date date, ReportTableFormatter formatter) {
        EntityReportFormatter<TenantSureReportStatusData> er = new EntityReportFormatter<TenantSureReportStatusData>(TenantSureReportStatusData.class);

        EntityQueryCriteria<TenantSureInsurancePolicyReport> criteria = EntityQueryCriteria.create(TenantSureInsurancePolicyReport.class);
        criteria.or(//@formatter:off
                // active:
                PropertyCriterion.in(criteria.proto().insurance().status(), EnumSet.of(TenantSureStatus.Active, TenantSureStatus.PendingCancellation)),

                // cancelled but not reported insurance certificates:
                new AndCriterion(
                        PropertyCriterion.eq(criteria.proto().insurance().status(), TenantSureStatus.Cancelled),
                        PropertyCriterion.ne(criteria.proto().reportedStatus(), TenantSureStatus.Cancelled)
                )
         );//@formatter:on

        ICursorIterator<TenantSureInsurancePolicyReport> iterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (iterator.hasNext()) {
                TenantSureInsurancePolicyReport reportedStatusHolder = iterator.next();
                reportedStatusHolder = updateReportStatus(reportedStatusHolder);

                TenantSureReportStatusData data = EntityFactory.create(TenantSureReportStatusData.class);
                data.firstName().setValue(reportedStatusHolder.insurance().client().tenant().customer().person().name().firstName().getValue());
                data.lastName().setValue(reportedStatusHolder.insurance().client().tenant().customer().person().name().lastName().getValue());
                data.insuranceCertificateNumber().setValue(reportedStatusHolder.insurance().certificate().insuranceCertificateNumber().getValue());
                data.monthlyPayable().setValue(reportedStatusHolder.insurance().totalMonthlyPayable().getValue().toString());
                data.status().setValue(reportedStatusHolder.reportedStatus().getValue().name());
                data.statusFrom().setValue(SimpleMessageFormat.format("{0,date,short}", reportedStatusHolder.statusFrom().getValue()));

                String specialFlag = "";
                if (reportedStatusHolder.insurance().status().getValue() == TenantSureStatus.PendingCancellation) {
                    specialFlag = SimpleMessageFormat.format(//@formatter:off
                            "{0} due to {1} since {2,date,short}",
                            TenantSureStatus.PendingCancellation,
                            reportedStatusHolder.insurance().cancellation().getValue(),
                            reportedStatusHolder.insurance().cancellationDate().getValue()
                    );//@formatter:on
                }
                data.cancellation().setValue(specialFlag);

                er.reportEntity(formatter, data);

                executionMonitor.addProcessedEvent(//@formatter:off
                        "Report",
                        reportedStatusHolder.insurance().totalMonthlyPayable().getValue(),
                        SimpleMessageFormat.format("TenantSure report for {0} {1} was generated.",
                                reportedStatusHolder.insurance().client().tenant().customer().person().name().firstName().getValue(),
                                reportedStatusHolder.insurance().client().tenant().customer().person().name().lastName().getValue())
                );//@formatter:on
            }
        } finally {
            iterator.close();
        }
    }

    @Override
    public void complete(ReportFileCreator reportFileCreator, ReportTableFormatter formatter) {
        reportFileCreator.report(formatter.getBinaryData());
    }

    /**
     * @param reportedStatusHolder
     * @return updated reported status
     * @throws IllegalArgumentException
     *             if previously reported status and current insurance status cannot be updated
     */
    private static TenantSureInsurancePolicyReport updateReportStatus(final TenantSureInsurancePolicyReport reportedStatusHolder) {
        return new UnitOfWork().execute(new Executable<TenantSureInsurancePolicyReport, Error>() {

            @Override
            public TenantSureInsurancePolicyReport execute() throws Error {
                ReportedStatus reportClientStatus = null;
                LogicalDate statusFrom = null;
                boolean needsUpdate = false;
                switch (reportedStatusHolder.insurance().status().getValue()) {
                case Cancelled:
                    if (reportedStatusHolder.reportedStatus().getValue() != ReportedStatus.Cancelled) {
                        reportClientStatus = ReportedStatus.Cancelled;
                        statusFrom = reportedStatusHolder.insurance().certificate().expiryDate().getValue();
                    }
                    break;

                case Active:
                case PendingCancellation:
                    if (reportedStatusHolder.reportedStatus().getValue() == null) {
                        reportClientStatus = ReportedStatus.New;
                        statusFrom = reportedStatusHolder.insurance().certificate().inceptionDate().getValue();
                        needsUpdate = true;

                    } else if ((reportedStatusHolder.reportedStatus().getValue() == ReportedStatus.Active)
                            | (reportedStatusHolder.reportedStatus().getValue() == ReportedStatus.New)) {
                        reportClientStatus = ReportedStatus.Active;
                        needsUpdate = reportedStatusHolder.reportedStatus().getValue() == ReportedStatus.New;
                        statusFrom = reportedStatusHolder.statusFrom().getValue();
                    }
                    break;

                default:
                    reportClientStatus = null;
                }

                if (reportClientStatus == null) {
                    throw new IllegalArgumentException(SimpleMessageFormat.format("wrong insurance status for report: {0} id = {1},  status = {2}",
                            TenantSureInsuranceCertificate.class.getSimpleName(), reportedStatusHolder.insurance().getPrimaryKey(), reportedStatusHolder
                                    .insurance().status().getValue()));
                }

                if (needsUpdate) {
                    reportedStatusHolder.reportedStatus().setValue(reportClientStatus);
                    reportedStatusHolder.statusFrom().setValue(statusFrom);

                    Persistence.service().persist(reportedStatusHolder);
                }

                return reportedStatusHolder;
            }
        });

    }
}
