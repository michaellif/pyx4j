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

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.messaging.saaj.util.ByteOutputStream;

import com.pyx4j.entity.report.JasperFileFormat;
import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.report.JasperReportProcessor;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.crm.rpc.dto.financial.moneyin.batch.MoneyInBatchDTO;

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
            ByteOutputStream depositSlipOutputStream = new ByteOutputStream();
            JasperReportProcessor.createReport(new JasperReportModel(MoneyInCreateDepositSlipPrintoutProcess.class.getPackage().getName() + ".BankDepositSlip",
                    batch.payments(), new HashMap<String, Object>()), JasperFileFormat.PDF, depositSlipOutputStream);

            depositSlipOutputStream.flush();
            Downloadable d = new Downloadable(depositSlipOutputStream.getBytes(), MimeMap.getContentType(DownloadFormat.PDF));
            fileName = "deposit-slip-stub.pdf";
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

}
