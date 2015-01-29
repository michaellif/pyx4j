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

public class N4BatchGenerationDeferredProcess extends AbstractDeferredProcess {

    private static final Logger log = LoggerFactory.getLogger(N4BatchGenerationDeferredProcess.class);

    private static final long serialVersionUID = 1L;

    private final ExecutionMonitor monitor;

    private final N4Batch[] batches;

    private Exception error;

    private String fileName;

    public N4BatchGenerationDeferredProcess(N4Batch... batches) {
        this.batches = batches;
        monitor = new ExecutionMonitor();
        monitor.setExpectedTotal(N4ManagementFacade.N4_REPORT_SECTION, 1); // preset to avoid NPE or math errors
    }

    @Override
    public void execute() {
        for (N4Batch batch : batches) {
            try {
                ServerSideFactory.create(N4ManagementFacade.class).issueN4(batch, monitor);
            } catch (Exception e) {
                error = e;
                String msg = "N4 generation failed for batch: " + batch.name().getStringView();
                log.error(msg, e);
                monitor.addFailedEvent(N4ManagementFacade.N4_REPORT_SECTION, msg, e);
            } finally {
                completed = true;
            }
        }

        if (monitor.getErred() > 0) {
            String report = monitor.getTextMessages(CompletionType.erred, CompletionType.failed);
            if (report != null && !report.isEmpty()) {
                Downloadable errorDownload = new Downloadable(report.getBytes(), MimeMap.getContentType(DownloadFormat.TXT));
                errorDownload.save(fileName = "failed-n4s-report-" + System.currentTimeMillis() + "." + DownloadFormat.TXT.getExtension());
            }
        }
    }

    @Override
    protected DeferredProcessProgressResponse createProgressResponse() {
        return new DeferredReportProcessProgressResponse();
    }

    @Override
    protected DeferredProcessProgressResponse updateProgressResponse(DeferredProcessProgressResponse r) {
        DeferredReportProcessProgressResponse rRep = (DeferredReportProcessProgressResponse) super.updateProgressResponse(r);
        if (!rRep.isCompleted() && !rRep.isCanceled()) {
            rRep.setProgress((int) (100 * monitor.getTotalCounter(N4ManagementFacade.N4_REPORT_SECTION) / monitor
                    .getExpectedTotal(N4ManagementFacade.N4_REPORT_SECTION)));
            rRep.setProgressMaximum(100);
        } else if (monitor.getErred() > 0) {
            rRep.setErrorStatusMessage(monitor.getTextMessages(CompletionType.erred, CompletionType.failed));
        } else if (error != null) {
            rRep.setMessage(error.getMessage());
        }
        if (fileName != null) {
            rRep.setDownloadLink(System.currentTimeMillis() + "/" + fileName);
        }
        return rRep;
    }
}
