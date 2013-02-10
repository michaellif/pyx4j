/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-09
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.AndCriterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.report.ReportTableCSVFormater;
import com.pyx4j.essentials.server.report.ReportTableFormater;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure.TenantSureStatus;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureReport;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureReport.ReportedStatus;
import com.propertyvista.operations.domain.scheduler.RunStats;
import com.propertyvista.server.jobs.StatisticsUtils;

class TenantSureReports {

    private static final Logger log = LoggerFactory.getLogger(TenantSureReports.class);

    static ReportTableFormater startReport() {
        ReportTableFormater formater = new ReportTableCSVFormater();

        formater.header("First Name");
        formater.header("Last Name");
        formater.header("Insurance Certificate Number");
        formater.header("Monthly Payable");
        formater.header("Status");
        formater.header("Status From");
        formater.header("Cancellation");
        formater.newRow();

        return formater;
    }

    static void completeReport(ReportTableFormater formater, Date date) {
        // create the file actually
        File sftpDir = ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getTenantSureInterfaceSftpDirectory();
        File dirReports = new File(sftpDir, "reports");
        if (!dirReports.exists()) {
            if (!dirReports.mkdirs()) {
                log.error("Unable to create directory {}", dirReports.getAbsolutePath());
                throw new Error(MessageFormat.format("Unable to create directory {0}", dirReports.getAbsolutePath()));
            }
        }

        String reportName = "subscribers-" + new SimpleDateFormat("yyyyMMdd").format(date) + ".csv";
        File file = new File(sftpDir, reportName);
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(formater.getBinaryData());
        } catch (Throwable e) {
            log.error("Unable write to file {}", file.getAbsolutePath(), e);
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    static void processReportPmc(RunStats runStats, Date date, ReportTableFormater formater) {
        EntityQueryCriteria<InsuranceTenantSureReport> criteria = EntityQueryCriteria.create(InsuranceTenantSureReport.class);
        criteria.or(//@formatter:off
                // active:
                PropertyCriterion.in(criteria.proto().insurance().status(), EnumSet.of(InsuranceTenantSure.TenantSureStatus.Active, InsuranceTenantSure.TenantSureStatus.PendingCancellation)),

                // cancelled but not reported insurance certificates:
                new AndCriterion(
                        PropertyCriterion.eq(criteria.proto().insurance().status(), InsuranceTenantSure.TenantSureStatus.Cancelled),
                        PropertyCriterion.ne(criteria.proto().reportedStatus(), InsuranceTenantSure.TenantSureStatus.Cancelled)
                )
         );//@formatter:off

        ICursorIterator<InsuranceTenantSureReport> iterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (iterator.hasNext()) {
                InsuranceTenantSureReport reportedStatusHolder = iterator.next();
                reportedStatusHolder = updateReportStatus(reportedStatusHolder);

                formater.cell(reportedStatusHolder.insurance().client().tenant().customer().person().name().firstName().getValue());
                formater.cell(reportedStatusHolder.insurance().client().tenant().customer().person().name().lastName().getValue());
                formater.cell(reportedStatusHolder.insurance().insuranceCertificateNumber().getStringView());
                formater.cell(reportedStatusHolder.insurance().monthlyPayable().getValue().toString());
                formater.cell(reportedStatusHolder.reportedStatus().getValue());
                formater.cell(SimpleMessageFormat.format("{0,date,short}", reportedStatusHolder.statusFrom().getValue()));

                String specialFlag = "";
                if (reportedStatusHolder.insurance().status().getValue() == TenantSureStatus.PendingCancellation) {
                    specialFlag = SimpleMessageFormat.format(//@formatter:off
                            "{0} due to {1} since {2,date,short}",
                            TenantSureStatus.PendingCancellation,
                            reportedStatusHolder.insurance().cancellation().getValue(),
                            reportedStatusHolder.insurance().cancellationDate().getValue()
                    );//@formatter:on
                }
                formater.cell(specialFlag);

                formater.newRow();

                StatisticsUtils.addProcessed(runStats, 1, reportedStatusHolder.insurance().monthlyPayable().getValue());
            }
        } finally {
            iterator.completeRetrieval();
        }

        Persistence.service().commit();
    }

    /**
     * @param reportedStatusHolder
     * @return updated reported status
     * @throws IllegalArgumentException
     *             if previously reported status and current insurance status cannot be updated
     */
    private static InsuranceTenantSureReport updateReportStatus(InsuranceTenantSureReport reportedStatusHolder) {
        ReportedStatus reportClientStatus = null;
        LogicalDate statusFrom = null;

        boolean needsUpdate = false;
        switch (reportedStatusHolder.insurance().status().getValue()) {
        case Cancelled:
            if (reportedStatusHolder.reportedStatus().getValue() != ReportedStatus.Cancelled) {
                reportClientStatus = ReportedStatus.Cancelled;
                statusFrom = reportedStatusHolder.insurance().expiryDate().getValue();
            }
            break;

        case Active:
        case PendingCancellation:
            if (reportedStatusHolder.reportedStatus().getValue() == null) {
                reportClientStatus = ReportedStatus.New;
                statusFrom = reportedStatusHolder.insurance().inceptionDate().getValue();
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
                    InsuranceTenantSure.class.getSimpleName(), reportedStatusHolder.insurance().getPrimaryKey(), reportedStatusHolder.insurance().status()
                            .getValue()));
        }

        if (needsUpdate) {
            reportedStatusHolder.reportedStatus().setValue(reportClientStatus);
            reportedStatusHolder.statusFrom().setValue(statusFrom);

            Persistence.service().persist(reportedStatusHolder);
        }

        return reportedStatusHolder;
    }

}
