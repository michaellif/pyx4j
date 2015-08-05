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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.UserRuntimeException;
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
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.contexts.ServerContext;
import com.pyx4j.site.rpc.reports.IReportsService;
import com.pyx4j.site.shared.domain.reports.ReportTemplate;

public abstract class AbstractReportsService<R extends ReportTemplate> implements IReportsService<R> {

    private static final String REPORT_SESSION_STORAGE_KEY = "REPORT_SESSION_STORAGE_KEY";

    private static final I18n i18n = I18n.get(AbstractReportsService.class);

    public static final class GenerateReportDeferredProcess implements IDeferredProcess {

        private static final Logger log = LoggerFactory.getLogger(GenerateReportDeferredProcess.class);

        private static final long serialVersionUID = 8173598149198655557L;

        private volatile boolean cancelled;

        private volatile boolean isReady;

        private final ReportGenerator reportGenerator;

        private final ReportTemplate reportMetadata;

        private volatile Throwable error;

        public GenerateReportDeferredProcess(ReportGenerator reportGenerator, ReportTemplate reportMetadata) {
            this.cancelled = false;
            this.isReady = false;
            this.reportGenerator = reportGenerator;
            this.reportMetadata = reportMetadata;
        }

        @Override
        public void started() {
            log.error("TODO", new Error("migrate to AbstractDeferredProcess"));
        }

        @Override
        public DeferredProcessProgressResponse status() {
            if (isReady) {
                DeferredProcessProgressResponse r = new DeferredProcessProgressResponse();
                r.setCompleted();
                return r;
            } else {
                DeferredProcessProgressResponse r = new DeferredProcessProgressResponse();
                ReportProgressStatus status = reportGenerator.getProgressStatus();
                if (error != null) {
                    if (error instanceof UserRuntimeException) {
                        r.setErrorStatusMessage(error.getMessage());
                    } else {
                        r.setErrorStatusMessage(i18n.tr("A server side error occured during report generation."));
                    }
                } else if (status != null) {
                    r.setMessage(status.stage);
                    r.setProgress((int) status.stageProgress);
                    r.setProgressMaximum((int) status.stageProgressMax);
                }
                if (cancelled) {
                    r.setCanceled();
                }
                return r;
            }
        }

        @Override
        public void execute() {
            Serializable reportData = null;
            if (!cancelled) {
                try {
                    reportData = new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.TransactionProcessing)
                            .execute(new Executable<Serializable, RuntimeException>() {
                                @Override
                                public Serializable execute() {
                                    return reportGenerator.generateReport(reportMetadata);
                                }
                            });
                    ServerContext.getVisit().setAttribute(REPORT_SESSION_STORAGE_KEY, reportData);
                    isReady = true;
                } catch (Throwable error) {
                    this.error = error;
                    log.error("Error during report generation:", error);
                }
            }
        }

        @Override
        public void cancel() {
            reportGenerator.abort();
            cancelled = true;
        }

    }

    public static final class ExportReportDeferredProcess implements IDeferredProcess {

        private static final Logger log = LoggerFactory.getLogger(ExportReportDeferredProcess.class);

        public static final long serialVersionUID = 1L;

        private final ReportGenerator reportGenerator;

        private final ReportTemplate reportMetadata;

        private volatile boolean cancelled;

        private volatile ExportedReport exported;

        private volatile boolean isReady;

        private volatile Throwable error;

        public ExportReportDeferredProcess(ReportGenerator reportGenerator, ReportTemplate reportMetadata) {
            this.reportGenerator = reportGenerator;
            this.reportMetadata = reportMetadata;
            this.exported = null;
            this.isReady = false;
        }

        @Override
        public void started() {
            log.error("TODO", new Error("migrate to AbstractDeferredProcess"));
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
                ReportProgressStatus status = reportGenerator.getProgressStatus();
                if (status != null) {
                    if (error != null) {
                        if (error instanceof UserRuntimeException) {
                            r.setErrorStatusMessage(error.getMessage());
                        } else {
                            r.setErrorStatusMessage(i18n.tr("A server side error occured during report export."));
                        }
                    } else if (status != null) {
                        r.setMessage(status.stage);
                        r.setProgress((int) status.stageProgress);
                        r.setProgressMaximum((int) status.stageProgressMax);
                    }
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
                try {
                    new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.TransactionProcessing)
                            .execute(new Executable<Void, RuntimeException>() {
                                @Override
                                public Void execute() {
                                    Serializable reportData = reportGenerator.generateReport(reportMetadata);
                                    exported = ((ReportExporter) reportGenerator).export(reportData);
                                    return null;
                                }

                            });
                    new Downloadable(exported.data, exported.contentType).save(exported.fileName);
                    isReady = true;
                } catch (Exception e) {
                    this.error = e;
                    log.error("Error during report export:", e);
                }
            }
        }

        @Override
        public void cancel() {
            reportGenerator.abort();
            cancelled = true;
        }

    }

    private final ReportGeneratorFactory reportGeneratorFactory;

    public AbstractReportsService(ReportGeneratorFactory reportGeneratorFactory) {
        this.reportGeneratorFactory = reportGeneratorFactory;
    }

    @Override
    public void generateReport(AsyncCallback<Serializable> callback, R reportMetadata) {
        @SuppressWarnings("unchecked")
        Class<? extends ReportTemplate> reportMetadataClass = (Class<? extends ReportTemplate>) reportMetadata.getInstanceValueClass();
        ReportGenerator reportGenerator = reportGeneratorFactory.getReportGenerator(reportMetadataClass);
        callback.onSuccess(reportGenerator.generateReport(reportMetadata));

    }

    @Override
    public void generateReportAsync(AsyncCallback<String> callback, R reportMetadata) {
        @SuppressWarnings("unchecked")
        Class<? extends ReportTemplate> reportMetadataClass = (Class<? extends ReportTemplate>) reportMetadata.getInstanceValueClass();
        ReportGenerator reportGenerator = reportGeneratorFactory.getReportGenerator(reportMetadataClass);
        callback.onSuccess(DeferredProcessRegistry.fork(new GenerateReportDeferredProcess(reportGenerator, reportMetadata),
                DeferredProcessRegistry.THREAD_POOL_DOWNLOADS));
    }

    @Override
    public void getReport(AsyncCallback<Serializable> callback) {
        Serializable report = ServerContext.getVisit().getAttribute(REPORT_SESSION_STORAGE_KEY);
        ServerContext.getVisit().removeAttribute(REPORT_SESSION_STORAGE_KEY);
        callback.onSuccess(report);
    }

    @Override
    public void export(AsyncCallback<String> callback, R reportMetadata) {
        @SuppressWarnings("unchecked")
        Class<? extends ReportTemplate> reportMetadataClass = (Class<? extends ReportTemplate>) reportMetadata.getInstanceValueClass();
        ReportGenerator reportGenerator = reportGeneratorFactory.getReportGenerator(reportMetadataClass);
        callback.onSuccess(DeferredProcessRegistry.fork(new ExportReportDeferredProcess(reportGenerator, reportMetadata),
                DeferredProcessRegistry.THREAD_POOL_DOWNLOADS));
    }

    @Override
    public void cancelExportedReport(AsyncCallback<VoidSerializable> callback, String downloadUrl) {
        String fileName = Downloadable.getDownloadableFileName(downloadUrl);
        if (fileName != null) {
            Downloadable.cancel(fileName);
        }
        callback.onSuccess(null);
    }

}
