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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.report.ReportTableFormatter;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure.CancellationType;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure.TenantSureStatus;
import com.propertyvista.domain.tenant.insurance.TenantSureConstants;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.operations.domain.scheduler.RunStats;
import com.propertyvista.operations.domain.tenantsure.TenantSureHQUpdateFile;

public class TenantSureProcessFacadeImpl implements TenantSureProcessFacade {

    private static final Logger log = LoggerFactory.getLogger(TenantSureProcessFacadeImpl.class);

    private static class TenantSureCancellator implements Callable<VoidSerializable> {

        private final InsuranceTenantSure ts;

        public TenantSureCancellator(InsuranceTenantSure ts) {
            this.ts = ts;
        }

        @Override
        public VoidSerializable call() throws Exception {
            ts.status().setValue(TenantSureStatus.Cancelled);
            log.info("cancelling TenantSure for certifcate: (#{}, expiry date {})}", ts.insuranceCertificateNumber().getValue(), ts.expiryDate().getValue());
            Persistence.service().persist(ts);
            return null;
        }

    }

    private static class TenantSureSkippedPaymentCancellator implements Callable<VoidSerializable> {

        private final InsuranceTenantSure ts;

        public TenantSureSkippedPaymentCancellator(InsuranceTenantSure ts) {
            assert ts.cancellation().getValue() == CancellationType.SkipPayment;
            this.ts = ts;
        }

        @Override
        public VoidSerializable call() throws Exception {
            ServerSideFactory.create(TenantSureFacade.class).cancelDueToSkippedPayment(ts.tenant().<Tenant> createIdentityStub());
            return null;
        }

    }

    @Override
    public void processCancellations(RunStats runStats, LogicalDate dueDate) {

        long cancelledCounter = 0l;
        long failedCounter = 0l;

        log.info("processing TenantSure cancellations requested by tenant");

        EntityQueryCriteria<InsuranceTenantSure> byTenantCancellationsCriteria = EntityQueryCriteria.create(InsuranceTenantSure.class);
        byTenantCancellationsCriteria.le(byTenantCancellationsCriteria.proto().expiryDate(), dueDate);
        byTenantCancellationsCriteria.eq(byTenantCancellationsCriteria.proto().status(), InsuranceTenantSure.TenantSureStatus.PendingCancellation);
        byTenantCancellationsCriteria.eq(byTenantCancellationsCriteria.proto().cancellation(), InsuranceTenantSure.CancellationType.CancelledByTenant);
        ICursorIterator<InsuranceTenantSure> iterator = Persistence.service().query(null, byTenantCancellationsCriteria, AttachLevel.Attached);
        try {
            while (iterator.hasNext()) {
                InsuranceTenantSure ts = iterator.next();
                String certificateNumber = ts.insuranceCertificateNumber().getValue();
                try {
                    UnitOfWork.execute(new TenantSureCancellator(ts));
                    ++cancelledCounter;
                } catch (Throwable cancellationError) {
                    ++failedCounter;
                    log.error("failed to cancel TenatSure insurance certificate: (#{})", certificateNumber);
                    log.error("failure: ", cancellationError);
                }
            }
        } catch (Throwable error) {
            log.error("failed to process cancellations due to: ", error);
        } finally {
            iterator.completeRetrieval();
        }

        log.info("processing TenantSure cancellation due to skipped payment");
        EntityQueryCriteria<InsuranceTenantSure> skippedPaymentCancellationsCriteria = EntityQueryCriteria.create(InsuranceTenantSure.class);
        skippedPaymentCancellationsCriteria.eq(skippedPaymentCancellationsCriteria.proto().status(), InsuranceTenantSure.TenantSureStatus.PendingCancellation);
        skippedPaymentCancellationsCriteria.eq(skippedPaymentCancellationsCriteria.proto().cancellation(), InsuranceTenantSure.CancellationType.SkipPayment);
        ICursorIterator<InsuranceTenantSure> skippedIterator = Persistence.service().query(null, byTenantCancellationsCriteria, AttachLevel.Attached);
        try {
            LogicalDate today = new LogicalDate(Persistence.service().getTransactionSystemTime());
            while (iterator.hasNext()) {
                InsuranceTenantSure ts = iterator.next();
                if (gracePeriodEnd(ts).compareTo(today) < 0) {
                    String certificateNumber = ts.insuranceCertificateNumber().getValue();
                    try {
                        UnitOfWork.execute(new TenantSureSkippedPaymentCancellator(ts));
                        ++cancelledCounter;
                    } catch (Throwable cancellationError) {
                        ++failedCounter;
                        log.error("failed to cancel (due to skipped payment) TenatSure insurance certificate: (#{})", certificateNumber);
                        log.error("failure", cancellationError);
                    }
                }
            }
        } finally {
            skippedIterator.completeRetrieval();
        }

        runStats.processed().setValue(cancelledCounter);
        runStats.failed().setValue(failedCounter);

    }

    @Override
    public void processPayments(RunStats runStats, LogicalDate dueDate) {
        TenantSurePayments.processPayments(runStats, dueDate);
    }

    @Override
    public TenantSureHQUpdateFile reciveHQUpdatesFile() {
        return HQUpdate.reciveHQUpdatesFile();
    }

    @Override
    public void processHQUpdate(RunStats runStats, TenantSureHQUpdateFile fileId) {
        HQUpdate.processHQUpdate(runStats, fileId);
    }

    @Override
    public ReportTableFormatter startReport() {
        return TenantSureReports.startReport();
    }

    @Override
    public void processReportPmc(RunStats runStats, Date date, ReportTableFormatter formater) {
        TenantSureReports.processReportPmc(runStats, date, formater);
    }

    @Override
    public void completeReport(ReportTableFormatter formater, Date date) {
        TenantSureReports.completeReport(formater, date);
    }

    @Override
    public void processTransactionsReport(RunStats runtStats, Date date, ReportTableFormatter formatter) {
        TenantSureReports.processTransactionsReport(runtStats, date, formatter);
    }

    @Override
    public ReportTableFormatter startTransactionsReport() {
        return TenantSureReports.startTransactionsReport();
    }

    @Override
    public void completeTransactionsReport(ReportTableFormatter formatter, Date date) {
        TenantSureReports.completeTransactionsReport(formatter, date);
    }

    private LogicalDate gracePeriodEnd(InsuranceTenantSure insuranceTenantSure) {
        GregorianCalendar gracePeriodEnd = new GregorianCalendar();
        gracePeriodEnd.setTime(TenantSurePayments.getNextPaymentDate(insuranceTenantSure));
        gracePeriodEnd.add(GregorianCalendar.DATE, TenantSureConstants.TENANTSURE_SKIPPED_PAYMENT_GRACE_PERIOD_DAYS);
        return new LogicalDate(gracePeriodEnd.getTime());
    }

}
