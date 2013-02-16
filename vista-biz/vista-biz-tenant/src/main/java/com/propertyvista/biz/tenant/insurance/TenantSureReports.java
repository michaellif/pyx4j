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
import java.util.GregorianCalendar;

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
import com.pyx4j.entity.shared.criterion.AndCriterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.report.EntityReportFormatter;
import com.pyx4j.essentials.server.report.ReportTableCSVFormatter;
import com.pyx4j.essentials.server.report.ReportTableFormatter;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.biz.financial.payment.CreditCardFacade;
import com.propertyvista.biz.financial.payment.CreditCardFacade.ReferenceNumberPrefix;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure.TenantSureStatus;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureReport;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureReport.ReportedStatus;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureTransaction;
import com.propertyvista.operations.domain.scheduler.RunStats;
import com.propertyvista.operations.rpc.TenantSureCcTaransactionsReportLineDTO;
import com.propertyvista.server.jobs.StatisticsUtils;

class TenantSureReports {

    private static final Logger log = LoggerFactory.getLogger(TenantSureReports.class);

    static ReportTableFormatter startReport() {
        ReportTableFormatter formatter = new ReportTableCSVFormatter();

        // Header for data
        EntityReportFormatter<TenantSureReportStatusData> er = new EntityReportFormatter<TenantSureReportStatusData>(TenantSureReportStatusData.class);
        er.createHeader(formatter);

        return formatter;
    }

    static void processReportPmc(RunStats runStats, Date date, ReportTableFormatter formatter) {
        EntityReportFormatter<TenantSureReportStatusData> er = new EntityReportFormatter<TenantSureReportStatusData>(TenantSureReportStatusData.class);

        EntityQueryCriteria<InsuranceTenantSureReport> criteria = EntityQueryCriteria.create(InsuranceTenantSureReport.class);
        criteria.or(//@formatter:off
                // active:
                PropertyCriterion.in(criteria.proto().insurance().status(), EnumSet.of(InsuranceTenantSure.TenantSureStatus.Active, InsuranceTenantSure.TenantSureStatus.PendingCancellation)),

                // cancelled but not reported insurance certificates:
                new AndCriterion(
                        PropertyCriterion.eq(criteria.proto().insurance().status(), InsuranceTenantSure.TenantSureStatus.Cancelled),
                        PropertyCriterion.ne(criteria.proto().reportedStatus(), InsuranceTenantSure.TenantSureStatus.Cancelled)
                )
         );//@formatter:on

        ICursorIterator<InsuranceTenantSureReport> iterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (iterator.hasNext()) {
                InsuranceTenantSureReport reportedStatusHolder = iterator.next();
                reportedStatusHolder = updateReportStatus(reportedStatusHolder);

                TenantSureReportStatusData data = EntityFactory.create(TenantSureReportStatusData.class);

                data.firstName().setValue(reportedStatusHolder.insurance().client().tenant().customer().person().name().firstName().getValue());
                data.lastName().setValue(reportedStatusHolder.insurance().client().tenant().customer().person().name().lastName().getValue());
                data.insuranceCertificateNumber().setValue(reportedStatusHolder.insurance().insuranceCertificateNumber().getStringView());
                data.monthlyPayable().setValue(reportedStatusHolder.insurance().monthlyPayable().getValue().toString());
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

                StatisticsUtils.addProcessed(runStats, 1, reportedStatusHolder.insurance().monthlyPayable().getValue());
            }
        } finally {
            iterator.completeRetrieval();
        }

        Persistence.service().commit();
    }

    static void completeReport(ReportTableFormatter formatter, Date date) {
        // create the file actually
        File sftpDir = ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getTenantSureInterfaceSftpDirectory();
        File dirReports = new File(sftpDir, "reports");
        if (!dirReports.exists()) {
            if (!dirReports.mkdirs()) {
                log.error("Unable to create directory {}", dirReports.getAbsolutePath());
                throw new Error(MessageFormat.format("Unable to create directory {0}", dirReports.getAbsolutePath()));
            }
        }

        String baseFileName = "subscribers-" + new SimpleDateFormat("yyyyMMdd").format(date);
        File file = createUniqueFile(dirReports, baseFileName, ".csv");
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(formatter.getBinaryData());
        } catch (Throwable e) {
            log.error("Unable write to file {}", file.getAbsolutePath(), e);
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    static ReportTableFormatter startTransactionsReport() {
        ReportTableFormatter formatter = new ReportTableCSVFormatter();
        EntityReportFormatter<TenantSureCcTaransactionsReportLineDTO> er = new EntityReportFormatter<TenantSureCcTaransactionsReportLineDTO>(
                TenantSureCcTaransactionsReportLineDTO.class);
        er.createHeader(formatter);

        return formatter;
    }

    static void processTransactionsReport(RunStats runStats, Date date, ReportTableFormatter formatter) {
        EntityQueryCriteria<InsuranceTenantSureTransaction> criteria = EntityQueryCriteria.create(InsuranceTenantSureTransaction.class);

        GregorianCalendar lowerBound = new GregorianCalendar();
        lowerBound.setTime(date);
        lowerBound.set(GregorianCalendar.HOUR_OF_DAY, lowerBound.getActualMinimum(GregorianCalendar.HOUR_OF_DAY));
        lowerBound.set(GregorianCalendar.MINUTE, lowerBound.getActualMinimum(GregorianCalendar.MINUTE));
        lowerBound.set(GregorianCalendar.SECOND, lowerBound.getActualMinimum(GregorianCalendar.SECOND));
        lowerBound.set(GregorianCalendar.MILLISECOND, lowerBound.getActualMinimum(GregorianCalendar.MILLISECOND));

        GregorianCalendar upperBound = new GregorianCalendar();
        upperBound.setTime(date);
        upperBound.set(GregorianCalendar.HOUR_OF_DAY, upperBound.getActualMaximum(GregorianCalendar.HOUR_OF_DAY));
        upperBound.set(GregorianCalendar.MINUTE, upperBound.getActualMaximum(GregorianCalendar.MINUTE));
        upperBound.set(GregorianCalendar.SECOND, upperBound.getActualMaximum(GregorianCalendar.SECOND));
        upperBound.set(GregorianCalendar.MILLISECOND, upperBound.getActualMaximum(GregorianCalendar.MILLISECOND));

        criteria.ge(criteria.proto().transactionDate(), lowerBound.getTime());
        criteria.le(criteria.proto().transactionDate(), upperBound.getTime());
        criteria.le(criteria.proto().status(), InsuranceTenantSureTransaction.TransactionStatus.Cleared);

        ICursorIterator<InsuranceTenantSureTransaction> transactions = Persistence.service().query(null, criteria, AttachLevel.Attached);

        EntityReportFormatter<TenantSureCcTaransactionsReportLineDTO> reportGenerator = new EntityReportFormatter<TenantSureCcTaransactionsReportLineDTO>(
                TenantSureCcTaransactionsReportLineDTO.class);
        long numOfTransactions = 0l;
        long numOfWrongPaymentMethods = 0l;
        try {

            while (transactions.hasNext()) {
                InsuranceTenantSureTransaction transaction = transactions.next();

                TenantSureCcTaransactionsReportLineDTO transactionReportLine = EntityFactory.create(TenantSureCcTaransactionsReportLineDTO.class);
                transactionReportLine.date().setValue(transaction.transactionDate().getValue());
                transactionReportLine.tenant().setValue(transaction.insurance().tenant().customer().person().name().getStringView());
                transactionReportLine.insuranceCertificateNumber().setValue(transaction.insurance().insuranceCertificateNumber().getValue());
                if (transaction.paymentMethod().details().isInstanceOf(CreditCardInfo.class)) {
                    CreditCardInfo ccInfo = transaction.paymentMethod().details().duplicate(CreditCardInfo.class);
                    transactionReportLine.creditCardType().setValue(ccInfo.cardType().getValue().toString());
                    transactionReportLine.creditCardNumber().setValue(ccInfo.card().obfuscatedNumber().getValue());
                } else {
                    log.error("Unknown payment method details {} for TenantSure transaction {}", transaction.paymentMethod().details().getInstanceValueClass()
                            .getSimpleName(), transaction.getPrimaryKey());
                    ++numOfWrongPaymentMethods;
                    transactionReportLine.creditCardType().setValue("N / A");
                    transactionReportLine.creditCardNumber().setValue("N / A");
                }
                transactionReportLine.amount().setValue(transaction.amount().getValue());
                transactionReportLine.transactionReferenceNumber().setValue(
                        ServerSideFactory.create(CreditCardFacade.class).getTransactionreferenceNumber(ReferenceNumberPrefix.TenantSure,
                                transaction.id().getStringView()));

                reportGenerator.reportEntity(formatter, transactionReportLine);
                ++numOfTransactions;
            }

        } finally {
            transactions.completeRetrieval();
        }
        runStats.processed().setValue(numOfTransactions);
        runStats.erred().setValue(numOfWrongPaymentMethods);

    }

    static void completeTransactionsReport(ReportTableFormatter formatter, Date date) {
        // create the file actually
        File sftpDir = ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getTenantSureInterfaceSftpDirectory();
        File dirReports = new File(sftpDir, "reports");
        if (!dirReports.exists()) {
            if (!dirReports.mkdirs()) {
                log.error("Unable to create directory {}", dirReports.getAbsolutePath());
                throw new Error(MessageFormat.format("Unable to create directory {0}", dirReports.getAbsolutePath()));
            }
        }

        String reportFileName = "transactions-" + new SimpleDateFormat("yyyyMMdd").format(date) + ".csv";
        File file = new File(dirReports, reportFileName);
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(formatter.getBinaryData());
        } catch (Throwable e) {
            log.error("Unable write to file {}", file.getAbsolutePath(), e);
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(out);
        }
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

    static private File createUniqueFile(File dir, String baseFileName, String extension) {
        File dst = new File(dir, baseFileName + extension);
        int attemptCount = 0;
        while (dst.exists()) {
            attemptCount++;
            if (attemptCount > 1000) {
                log.warn("File {} already exists", dst.getAbsolutePath());
                return new File(dir, baseFileName + "." + System.currentTimeMillis() + extension);
            }
            dst = new File(dir, baseFileName + "." + attemptCount + extension);
        }
        return dst;
    }

}
