/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Aug 2, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.essentials.server.services.reports;

import java.io.Serializable;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.services.reports.ReportExporter.ExportedReport;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;
import com.pyx4j.gwt.server.deferred.IDeferredProcess;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.rpc.reports.IReportsService;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

public class AbstractReportsService implements IReportsService {

    public final class ExportReportDeferredProcess implements IDeferredProcess {

        public static final long serialVersionUID = 1L;

        private final ReportGenerator reportGenerator;

        private final ReportMetadata reportMetadata;

        private volatile boolean cancelled;

        private volatile ExportedReport exported;

        private volatile boolean isReady;

        private final ReportProgressStatusHolder reportProgressStatusHolder;

        public ExportReportDeferredProcess(ReportGenerator reportGenerator, ReportMetadata reportMetadata) {
            this.reportGenerator = reportGenerator;
            this.reportMetadata = reportMetadata;
            this.exported = null;
            this.isReady = false;
            this.reportProgressStatusHolder = new ReportProgressStatusHolder();
        }

        @Override
        public DeferredProcessProgressResponse status() {
            if (isReady) {
                DeferredReportProcessProgressResponse r = new DeferredReportProcessProgressResponse();
                r.setCompleted();
                r.setDownloadLink(System.currentTimeMillis() + "/" + exported.fileName);
                return r;
            } else {
                DeferredProcessProgressResponse r = new DeferredProcessProgressResponse();
                ReportProgressStatus status = reportProgressStatusHolder.get();
                if (status != null) {
                    r.setMessage(status.stage);
                    r.setProgress(status.stageProgress);
                    r.setProgressMaximum(status.stageProgressMax);
                }
                if (cancelled) {
                    r.setCanceled();
                }
                return r;
            }
        }

        @Override
        public void execute() {
            if (!cancelled) {
                new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.TransactionProcessing).execute(new Executable<Void, RuntimeException>() {

                    @Override
                    public Void execute() {
                        Serializable reportData = reportGenerator.generateReport(reportMetadata, reportProgressStatusHolder);
                        exported = ((ReportExporter) reportGenerator).export(reportData, reportProgressStatusHolder);
                        return null;
                    }

                });
                new Downloadable(exported.data, exported.contentType).save(exported.fileName);
                isReady = true;
            }
        }

        @Override
        public void cancel() {
            this.cancelled = true;
        }
    }

    private final Map<Class<? extends ReportMetadata>, ReportGenerator> reportGenerators;

    public AbstractReportsService(Map<Class<? extends ReportMetadata>, ReportGenerator> reportGenerators) {
        this.reportGenerators = reportGenerators;
    }

    @Override
    public void generateReport(AsyncCallback<Serializable> callback, ReportMetadata reportMetadata) {

        ReportGenerator reportGenerator = reportGenerators.get(reportMetadata.getInstanceValueClass());
        if (reportGenerator != null) {
            callback.onSuccess(reportGenerator.generateReport(reportMetadata, new ReportProgressStatusHolder()));
        } else {
            throw new Error("report generation failed: report generator for report type '" + reportMetadata.getInstanceValueClass().getName()
                    + "' was not found");
        }

    }

    @Override
    public void export(AsyncCallback<String> callback, ReportMetadata reportMetadata) {
        ReportGenerator reportGenerator = reportGenerators.get(reportMetadata.getInstanceValueClass());
        if (reportGenerator != null && reportGenerator instanceof ReportExporter) {
            callback.onSuccess(DeferredProcessRegistry.fork(new ExportReportDeferredProcess(reportGenerator, reportMetadata),
                    DeferredProcessRegistry.THREAD_POOL_DOWNLOADS));
        } else {
            throw new Error("report generation failed: report generator for report type '" + reportMetadata.getInstanceValueClass().getName()
                    + "' was not found or doesn't support export");
        }
    }

    @Override
    public void cancelExport(AsyncCallback<VoidSerializable> callback, String downloadUrl) {
        String fileName = Downloadable.getDownloadableFileName(downloadUrl);
        if (fileName != null) {
            Downloadable.cancel(fileName);
        }
        callback.onSuccess(null);
    }
}
