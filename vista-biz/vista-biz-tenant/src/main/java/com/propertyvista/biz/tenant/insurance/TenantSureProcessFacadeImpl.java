/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-27
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance;

import java.util.EnumSet;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.AndCriterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.report.ReportTableFormater;

import com.propertyvista.admin.domain.scheduler.RunStats;
import com.propertyvista.admin.domain.tenantsure.TenantSureHQUpdateFile;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureReport;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureReport.ReportedStatus;
import com.propertyvista.server.jobs.StatisticsUtils;

public class TenantSureProcessFacadeImpl implements TenantSureProcessFacade {

    @Override
    public void processCancellations(RunStats runStats, LogicalDate dueDate) {
        EntityQueryCriteria<InsuranceTenantSure> criteria = EntityQueryCriteria.create(InsuranceTenantSure.class);
        criteria.le(criteria.proto().expiryDate(), dueDate);
        criteria.eq(criteria.proto().status(), InsuranceTenantSure.Status.PendingCancellation);
        ICursorIterator<InsuranceTenantSure> iterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (iterator.hasNext()) {
                InsuranceTenantSure ts = iterator.next();
            }
        } finally {
            iterator.completeRetrieval();
        }

    }

    @Override
    public void processPayments(RunStats runStats, LogicalDate dueDate) {
        TenantSurePayments.processPayments(runStats, dueDate);
    }

    @Override
    public void processReports(RunStats runStats, LogicalDate dueDate, ReportTableFormater formater) {
        formater.header("First Name");
        formater.header("Last Name");
        formater.header("Insurance Certificate Number");
        formater.header("Monthly Payable");
        formater.header("Status");
        formater.header("Status From");
        formater.newRow();

        EntityQueryCriteria<InsuranceTenantSureReport> criteria = EntityQueryCriteria.create(InsuranceTenantSureReport.class);
        criteria.or(//@formatter:off
                // active:
                PropertyCriterion.in(criteria.proto().insurance().status(), EnumSet.of(InsuranceTenantSure.Status.Active, InsuranceTenantSure.Status.PendingCancellation)),

                // cancelled but not reported insurance certificates:
                new AndCriterion(
                        PropertyCriterion.eq(criteria.proto().insurance().status(), InsuranceTenantSure.Status.Cancelled),
                        PropertyCriterion.ne(criteria.proto().reportedStatus(), InsuranceTenantSure.Status.Cancelled)
                )
         );//@formatter:off

        ICursorIterator<InsuranceTenantSureReport> iterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (iterator.hasNext()) {
                InsuranceTenantSureReport reportedStatusHolder = iterator.next();

                formater.cell(reportedStatusHolder.insurance().client().tenant().customer().person().name().firstName().getValue());
                formater.cell(reportedStatusHolder.insurance().client().tenant().customer().person().name().lastName().getValue());
                formater.cell(reportedStatusHolder.insurance().insuranceCertificate().insuranceCertificateNumber().getStringView());
                formater.cell(reportedStatusHolder.insurance().monthlyPayable().getValue().toString());
                formater.cell(updateReportStatus(reportedStatusHolder));

                formater.newRow();

                StatisticsUtils.addProcessed(runStats, 1, reportedStatusHolder.insurance().monthlyPayable().getValue());
            }
        } finally {
            iterator.completeRetrieval();
        }

        Persistence.service().commit();
    }

    @Override
    public TenantSureHQUpdateFile reciveHQUpdatesFile() {
        return HQUpdate.reciveHQUpdatesFile();
    }

    @Override
    public void processHQUpdate(RunStats runStats, TenantSureHQUpdateFile fileId) {
        HQUpdate.processHQUpdate(runStats, fileId);
    }

    /**
     * @param reportedStatusHolder
     * @return updated reported status
     * @throws IllegalArgumentException if previously reported status and current insurance status cannot be updated
     */
    private InsuranceTenantSureReport.ReportedStatus updateReportStatus(InsuranceTenantSureReport reportedStatusHolder) {
        ReportedStatus reportClientStatus = null;
        LogicalDate statusFrom = null;
        
        boolean needsUpdate = false;        
        switch (reportedStatusHolder.insurance().status().getValue()) {
        case Cancelled:
            if (reportedStatusHolder.reportedStatus().getValue() != ReportedStatus.Cancelled ) {
                reportClientStatus = ReportedStatus.Cancelled;
                statusFrom = reportedStatusHolder.insurance().expiryDate().getValue();
            }
            break;

        case Active:
        case PendingCancellation:
            if (reportedStatusHolder.reportedStatus().getValue() != ReportedStatus.Active) {
                reportClientStatus = ReportedStatus.New;
                statusFrom = reportedStatusHolder.insurance().inceptionDate().getValue();
                needsUpdate = true;
                
            } else if ((reportedStatusHolder.reportedStatus().getValue() == ReportedStatus.Active)  | (reportedStatusHolder.reportedStatus().getValue() == ReportedStatus.New)) {
                reportClientStatus = ReportedStatus.Active;                
                needsUpdate = reportedStatusHolder.reportedStatus().getValue() == ReportedStatus.New;
            }
            break;

        default:
            reportClientStatus = null;
        }

        if (reportClientStatus == null) {
            throw new IllegalArgumentException(SimpleMessageFormat.format("wrong insurance status for report: {0} id = {1},  status = {2}",
                    InsuranceTenantSure.class.getSimpleName(), reportedStatusHolder.insurance().getPrimaryKey(), reportedStatusHolder.insurance().status().getValue()));
        }

        if (needsUpdate) {
            reportedStatusHolder.reportedStatus().setValue(reportClientStatus);
            reportedStatusHolder.statusFrom().setValue(statusFrom);
            
            Persistence.service().persist(reportedStatusHolder);
        }

        return reportedStatusHolder.reportedStatus().getValue();
    }
}
