/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on 2010-05-07
 * @author vlads
 */
package com.pyx4j.site.client;

import java.io.Serializable;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.shared.ApplicationBackend;
import com.pyx4j.config.shared.ApplicationBackend.ApplicationBackendType;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.DocCreationRequest;
import com.pyx4j.entity.rpc.DocCreationService;
import com.pyx4j.essentials.client.DownloadFrame;
import com.pyx4j.essentials.rpc.download.DownloadableService;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.rpc.report.ReportService;
import com.pyx4j.gwt.client.deferred.DeferredProcessDialog;
import com.pyx4j.gwt.commons.BrowserType;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.BlockingAsyncCallback;
import com.pyx4j.rpc.shared.DeferredCorrelationId;

//TODO find name for that - it is used not only for reports
public class ReportDialog extends DeferredProcessDialog {

    private final static Logger log = LoggerFactory.getLogger(ReportDialog.class);

    private static final I18n i18n = I18n.get(ReportDialog.class);

    private DownloadableService reportService;

    private String downloadServletPath;

    private String downloadUrl;

    public void start(ReportService<?> reportService, EntityQueryCriteria<?> criteria) {
        start(reportService, criteria, null);
    }

    public void start(ReportService<?> reportService, EntityQueryCriteria<?> criteria, HashMap<String, Serializable> parameters) {
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setTimezoneOffset(TimeUtils.getTimezoneOffset());
        reportRequest.setCriteria(criteria);
        reportRequest.setParameters(parameters);
        start(reportService, reportRequest);
    }

    public void start(ReportService<?> reportService, ReportRequest reportRequest) {
        this.reportService = reportService;
        show();

        AsyncCallback<String> callback = new BlockingAsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                hide();
                throw new UnrecoverableClientError(caught);
            }

            @Override
            public void onSuccess(String deferredCorrelationID) {
                startProgress(deferredCorrelationID);
            }

        };

        reportService.createDownload(callback, reportRequest);

    }

    public void start(DocCreationService reportService, DocCreationRequest docCreationRequest) {
        if (reportService instanceof DownloadableService) {
            this.reportService = (DownloadableService) reportService;
        } else if (this.reportService == null) {
            this.reportService = GWT.<DownloadableService> create(DownloadableService.class);
        }

        show();
        AsyncCallback<DeferredCorrelationId> callback = new BlockingAsyncCallback<DeferredCorrelationId>() {

            @Override
            public void onFailure(Throwable caught) {
                hide();
                throw new UnrecoverableClientError(caught);
            }

            @Override
            public void onSuccess(DeferredCorrelationId deferredCorrelationID) {
                startProgress(deferredCorrelationID.getDeferredCorrelationId());
            }

        };
        reportService.startDocCreation(callback, docCreationRequest);
    }

    public ReportDialog(String title, String initialMessage) {
        super(title, initialMessage, ApplicationBackend.getBackendType() == ApplicationBackendType.GAE);
        downloadServletPath = AppSite.getDeploymentBaseURL() + "download/";
    }

    protected boolean useDownloadFrame() {
        return !BrowserType.isIE(); /* && !ClientState.isSeleniumMode(); */
    }

    public void setDownloadServletPath(String path) {
        downloadServletPath = path;
    }

    @Override
    public void onDeferredSuccess(DeferredProcessProgressResponse result) {
        if (result.isCompletedSuccess()) {
            downloadUrl = ((DeferredReportProcessProgressResponse) result).getDownloadLink();
            log.debug("DownloadLink link {} {}", downloadServletPath, downloadUrl);
            if (useDownloadFrame()) {
                new DownloadFrame(downloadServletPath + downloadUrl);
                hide();
            } else {
                VerticalPanel vp = new VerticalPanel();
                this.setBody(vp);
                vp.add(new HTML(i18n.tr("Report creation completed")));
                Anchor downloadLink = new Anchor(i18n.tr("Download"), downloadServletPath + downloadUrl, "_blank");
                downloadLink.ensureDebugId("reportDownloadLink");
                downloadLink.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        hide();
                    }
                });
                vp.add(downloadLink);
            }
        }

        onDeferredCompleate();
    }

    @Override
    public boolean onClickClose() {
        if (downloadUrl != null) {
            reportService.cancelDownload(null, downloadUrl);
        }
        return super.onClickClose();
    }
}
