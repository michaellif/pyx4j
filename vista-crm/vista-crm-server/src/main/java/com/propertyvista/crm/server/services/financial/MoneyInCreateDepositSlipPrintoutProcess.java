/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-20
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.financial;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.report.JasperFileFormat;
import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.report.JasperReportProcessor;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.DepositSlipCheckDetailsRecordDTO;
import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;

public class MoneyInCreateDepositSlipPrintoutProcess extends AbstractDeferredProcess {

    private static final Logger log = LoggerFactory.getLogger(MoneyInBatchCreateDeferredProcess.class);

    private static final long serialVersionUID = 1L;

    private final AtomicInteger progress;

    private final int progressMax;

    private final MoneyInBatchDTO batch;

    private String fileName;

    private volatile Throwable error;

    public MoneyInCreateDepositSlipPrintoutProcess(MoneyInBatchDTO batch) {
        this.progress = new AtomicInteger();
        this.progress.set(0);
        this.progressMax = 1;
        this.batch = batch;
    }

    @Override
    public void execute() {
        try {
            ByteArrayOutputStream depositSlipOutputStream = new ByteArrayOutputStream();
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("depositSlipNumber", batch.depositSlipNumber().getValue());
            params.put("depositDate", batch.depositDate().getValue());
            params.put("accountName", batch.bankAccountName().getValue());
            params.put("bankId", batch.bankId().getValue());
            params.put("transitNumber", batch.bankTransitNumber().getValue());
            params.put("accountNumber", batch.bankAccountNumber().getValue());

            params.put("totalAmount", batch.totalReceivedAmount().getValue());
            params.put("numberOfChecks", batch.numberOfReceipts().getValue());

            List<DepositSlipCheckDetailsRecordDTO> printablePayments = filterPrintablePayments(batch.payments());

            JasperReportProcessor.createReport(new JasperReportModel(MoneyInCreateDepositSlipPrintoutProcess.class.getPackage().getName() + ".BankDepositSlip",
                    printablePayments, params), JasperFileFormat.PDF, depositSlipOutputStream);

            depositSlipOutputStream.flush();
            Downloadable d = new Downloadable(depositSlipOutputStream.toByteArray(), MimeMap.getContentType(DownloadFormat.PDF));
            fileName = "deposit-slip.pdf";
            d.save(fileName);
        } catch (Throwable e) {
            log.error("deposit slip generation failed", e);
            error = e;
        } finally {
            completed = true;
        }

    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredReportProcessProgressResponse r = new DeferredReportProcessProgressResponse();
        r.setProgress(progress.get());
        r.setProgressMaximum(progressMax);
        if (completed) {
            r.setCompleted();
            r.setDownloadLink(System.currentTimeMillis() + "/" + fileName);
        }
        if (error != null) {
            r.setError();
            r.setErrorStatusMessage("failed to create deposit slip printout due to " + error.getMessage());
        }
        return r;
    }

    private List<DepositSlipCheckDetailsRecordDTO> filterPrintablePayments(List<DepositSlipCheckDetailsRecordDTO> payments) {
        List<DepositSlipCheckDetailsRecordDTO> printablePayments = new ArrayList<>();
        for (DepositSlipCheckDetailsRecordDTO payment : payments) {
            if (payment.status().getValue() != PaymentStatus.Canceled) {
                printablePayments.add(payment);
            }
        }
        return printablePayments;
    }

}
