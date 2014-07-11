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

import java.io.File;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.essentials.server.report.ReportTableCSVFormatter;
import com.pyx4j.essentials.server.report.ReportTableFormatter;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.tenant.insurance.tenantsure.reports.InsuranceStatusReport;
import com.propertyvista.biz.tenant.insurance.tenantsure.reports.ReportFileCreatorImpl;
import com.propertyvista.biz.tenant.insurance.tenantsure.reports.TenantSureBusinessReport;
import com.propertyvista.biz.tenant.insurance.tenantsure.reports.TransactionsReport;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.domain.tenant.insurance.TenantSureConstants;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy.CancellationType;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy.TenantSureStatus;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.operations.domain.tenantsure.TenantSureHQUpdateFile;

public class TenantSureProcessFacadeImpl implements TenantSureProcessFacade {

    private static final Logger log = LoggerFactory.getLogger(TenantSureProcessFacadeImpl.class);

    private static final String EXECUTION_MONITOR_SECTION_NAME = "TenantSureCancellation";

    private static class TenantSureCancellator implements Executable<Void, RuntimeException> {

        private final TenantSureInsurancePolicy ts;

        public TenantSureCancellator(TenantSureInsurancePolicy ts) {
            this.ts = ts;
        }

        @Override
        public Void execute() {
            ts.status().setValue(TenantSureStatus.Cancelled);
            log.info("canceling TenantSure for certificate: (#{}, expiry date {})}", ts.certificate().insuranceCertificateNumber().getValue(), ts.certificate()
                    .expiryDate().getValue());
            Persistence.service().persist(ts);
            return null;
        }

    }

    private static class TenantSureSkippedPaymentCancellator implements Executable<Void, RuntimeException> {

        private final TenantSureInsurancePolicy ts;

        public TenantSureSkippedPaymentCancellator(TenantSureInsurancePolicy ts) {
            assert ts.cancellation().getValue() == CancellationType.SkipPayment;
            this.ts = ts;
        }

        @Override
        public Void execute() {
            ServerSideFactory.create(TenantSureFacade.class).cancelDueToSkippedPayment(ts.tenant().<Tenant> createIdentityStub());
            return null;
        }

    }

    @Override
    public void processCancellations(ExecutionMonitor executionMonitor, LogicalDate dueDate) {

        log.info("processing TenantSure cancellations requested by tenant");

        {
            EntityQueryCriteria<TenantSureInsurancePolicy> byTenantCancellationsCriteria = EntityQueryCriteria.create(TenantSureInsurancePolicy.class);
            byTenantCancellationsCriteria.le(byTenantCancellationsCriteria.proto().certificate().expiryDate(), dueDate);
            byTenantCancellationsCriteria.eq(byTenantCancellationsCriteria.proto().status(), TenantSureStatus.PendingCancellation);
            byTenantCancellationsCriteria.eq(byTenantCancellationsCriteria.proto().cancellation(), CancellationType.CancelledByTenant);
            ICursorIterator<TenantSureInsurancePolicy> iterator = Persistence.service().query(null, byTenantCancellationsCriteria, AttachLevel.Attached);
            try {
                while (iterator.hasNext()) {
                    TenantSureInsurancePolicy ts = iterator.next();
                    String certificateNumber = ts.certificate().insuranceCertificateNumber().getValue();

                    try {
                        new UnitOfWork().execute(new TenantSureCancellator(ts));
                        executionMonitor.addProcessedEvent(EXECUTION_MONITOR_SECTION_NAME, ("By Tenant: Policy ID = " + ts.id().getValue()
                                + ", Cert. Number = " + certificateNumber));
                    } catch (Throwable cancellationError) {
                        executionMonitor.addErredEvent(EXECUTION_MONITOR_SECTION_NAME,
                                ("Policy ID = " + ts.id().getValue() + ", Cert. Number = " + certificateNumber), cancellationError);
                        log.error("failed to cancel TenatSure insurance certificate: (#{})", certificateNumber);
                        log.error("failure: ", cancellationError);
                    }
                }
            } catch (Throwable error) {
                log.error("failed to process cancellations due to: ", error);
            } finally {
                iterator.close();
            }
        }

        {
            log.info("processing TenantSure cancellation due to skipped payment");
            EntityQueryCriteria<TenantSureInsurancePolicy> skippedPaymentCancellationsCriteria = EntityQueryCriteria.create(TenantSureInsurancePolicy.class);
            skippedPaymentCancellationsCriteria.eq(skippedPaymentCancellationsCriteria.proto().status(), TenantSureStatus.PendingCancellation);
            skippedPaymentCancellationsCriteria.eq(skippedPaymentCancellationsCriteria.proto().cancellation(), CancellationType.SkipPayment);
            ICursorIterator<TenantSureInsurancePolicy> skippedIterator = Persistence.service().query(null, skippedPaymentCancellationsCriteria,
                    AttachLevel.Attached);
            try {
                LogicalDate today = SystemDateManager.getLogicalDate();
                while (skippedIterator.hasNext()) {
                    TenantSureInsurancePolicy ts = skippedIterator.next();
                    Persistence.ensureRetrieve(ts.tenant(), AttachLevel.IdOnly);
                    if (gracePeriodEnd(ts).compareTo(today) < 0) {
                        String certificateNumber = ts.certificate().insuranceCertificateNumber().getValue();
                        try {
                            new UnitOfWork().execute(new TenantSureSkippedPaymentCancellator(ts));
                            executionMonitor.addProcessedEvent(EXECUTION_MONITOR_SECTION_NAME, "Skipped Payment: "
                                    + ("Policy ID = " + ts.id().getValue() + ", Cert. Number = " + certificateNumber));
                        } catch (Throwable cancellationError) {
                            executionMonitor.addErredEvent(//@formatter:off
                                EXECUTION_MONITOR_SECTION_NAME,                                        
                                SimpleMessageFormat.format("Failed to cancel (due to skipped payment) TenatSure insurance certificate: #{0}", certificateNumber)
                            );//@formatter:on
                            log.error("failed to cancel (due to skipped payment) TenatSure insurance certificate: (#{})", certificateNumber);
                            log.error("failure", cancellationError);
                        }
                    }
                }
            } catch (Throwable error) {
                log.error("failed to process skipped payment cancellations due to: ", error);
            } finally {
                skippedIterator.close();
            }
        }
    }

    @Override
    public void processRenewal(ExecutionMonitor executionMonitor, LogicalDate dueDate) {
        new TenantSureRenewal().processRenewal(executionMonitor, dueDate);
    }

    @Override
    public void processPayments(ExecutionMonitor executionMonitor, LogicalDate dueDate) {
        TenantSurePayments.processPayments(executionMonitor, dueDate);
    }

    @Override
    public TenantSureHQUpdateFile receiveHQUpdatesFile() {
        return HQUpdate.receiveHQUpdatesFile();
    }

    @Override
    public void processHQUpdate(ExecutionMonitor executionMonitor, TenantSureHQUpdateFile fileId) {
        HQUpdate.processHQUpdate(executionMonitor, fileId);
    }

    @Override
    public ReportTableFormatter startInsuranceStatusReport() {
        ReportTableFormatter tableFormatter = new ReportTableCSVFormatter();
        new InsuranceStatusReport().start(tableFormatter);
        return tableFormatter;
    }

    @Override
    public void processInsuranceStatusReportPmc(ExecutionMonitor executionMonitor, Date date, ReportTableFormatter formater) {
        new InsuranceStatusReport().processReport(executionMonitor, date, formater);
    }

    @Override
    public void completeInsuranceStatusReport(ReportTableFormatter formatter, Date date) {
        File sftpDir = ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getTenantSureInterfaceSftpDirectory();
        File dirReports = new File(sftpDir, "reports");
        new InsuranceStatusReport().complete(new ReportFileCreatorImpl(SimpleMessageFormat.format("subscribers-{0,date,yyyyMMdd}.csv", date), dirReports),
                formatter);
    }

    @Override
    public void processTransactionsReport(ExecutionMonitor executionMonitor, Date date, ReportTableFormatter formatter) {
        new TransactionsReport().processReport(executionMonitor, date, formatter);
    }

    @Override
    public ReportTableFormatter startTransactionsReport() {
        ReportTableFormatter f = new ReportTableCSVFormatter();
        new TransactionsReport().start(f);
        return f;
    }

    @Override
    public void completeTransactionsReport(ReportTableFormatter formatter, Date date) {
        File sftpDir = ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getTenantSureInterfaceSftpDirectory();
        File dirReports = new File(sftpDir, "reports");
        new TransactionsReport().complete(new ReportFileCreatorImpl(SimpleMessageFormat.format("transactions-{0,date,yyyyMMdd}.csv", date), dirReports) {
            @Override
            protected File makeFileName(File dirReports, String baseFileName, String extension) {
                return new File(dirReports, baseFileName + extension);
            }
        }, formatter);
    }

    private LogicalDate gracePeriodEnd(TenantSureInsurancePolicy insuranceTenantSure) {
        GregorianCalendar gracePeriodEnd = new GregorianCalendar();
        gracePeriodEnd.setTime(TenantSurePayments.getNextPaymentDate(insuranceTenantSure));
        gracePeriodEnd.add(GregorianCalendar.DATE, TenantSureConstants.TENANTSURE_SKIPPED_PAYMENT_GRACE_PERIOD_DAYS);
        return new LogicalDate(gracePeriodEnd.getTime());
    }

    @Override
    public ReportTableFormatter startTenantSureBusinessReport() {
        return new TenantSureBusinessReport().start();
    }

    @Override
    public void processTenantSureBusinessReportPmc(ExecutionMonitor executionMonitor, ReportTableFormatter formatter) {
        new TenantSureBusinessReport().processReport(executionMonitor, formatter);
    }

    @Override
    public void completeTenantSureBusinessReport(ReportTableFormatter formatter) {
        new TenantSureBusinessReport().completeReport(formatter);
    }

}
