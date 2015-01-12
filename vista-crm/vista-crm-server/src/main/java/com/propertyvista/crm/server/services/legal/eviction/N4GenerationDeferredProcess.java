/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 9, 2015
 * @author stanp
 */
package com.propertyvista.crm.server.services.legal.eviction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Pair;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.legal.eviction.N4ManagementFacade;
import com.propertyvista.domain.legal.n4.N4Batch;
import com.propertyvista.operations.domain.scheduler.CompletionType;

public class N4GenerationDeferredProcess extends AbstractDeferredProcess {

    private static final Logger log = LoggerFactory.getLogger(N4GenerationDeferredProcess.class);

    private static final long serialVersionUID = 1L;

    private final ExecutionMonitor monitor;

    private final N4Batch batch;

    private Exception error;

    private String fileName;

    public N4GenerationDeferredProcess(N4Batch batch) {
        this.batch = batch;
        monitor = new ExecutionMonitor();
    }

    @Override
    public void execute() {
        try {
            ServerSideFactory.create(N4ManagementFacade.class).issueN4(batch, monitor);

            if (monitor.getErred() > 0) {
                Pair<byte[], DownloadFormat> report = makeErredReport(monitor);
                Downloadable errorReport = new Downloadable(report.getA(), MimeMap.getContentType(report.getB()));
                errorReport.save(fileName = "failed-n4s-report-" + System.currentTimeMillis() + "." + report.getB().getExtension());
            }
        } catch (Exception e) {
            error = e;
            log.error("N4 generation failed", e);
        } finally {
            completed = true;
        }
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredProcessProgressResponse status = super.status();
        DeferredReportProcessProgressResponse r = new DeferredReportProcessProgressResponse();
        if (!status.isCompleted() && !status.isCanceled()) {
            r.setProgress((int) (monitor.getTotalCounter(N4ManagementFacade.N4_REPORT_SECTION) / monitor.getExpectedTotal(N4ManagementFacade.N4_REPORT_SECTION)));
            r.setProgressMaximum(100);
            if (fileName != null) {
                r.setDownloadLink(System.currentTimeMillis() + "/" + fileName);
            }
        } else if (monitor.getErred() > 0) {
            r.setErrorStatusMessage(monitor.getTextMessages(CompletionType.erred, CompletionType.failed));
        } else if (error != null) {
            r.setMessage(error.getMessage());
        }
        return r;
    }

    private Pair<byte[], DownloadFormat> makeErredReport(ExecutionMonitor monitor) {
        return new Pair<byte[], DownloadFormat>(monitor.getTextMessages(CompletionType.erred, CompletionType.failed).getBytes(), DownloadFormat.TXT);
    }
}
