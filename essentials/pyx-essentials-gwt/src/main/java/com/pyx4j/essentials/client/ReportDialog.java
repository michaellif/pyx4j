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
 * @version $Id$
 */
package com.pyx4j.essentials.client;

import java.io.Serializable;
import java.util.HashMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.rpc.report.ReportService;
import com.pyx4j.gwt.commons.BrowserType;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.client.BlockingAsyncCallback;

public class ReportDialog extends DeferredProcessDialog {

    private ReportService<?> reportService;

    private String downloadUrl;

    public static void start(ReportService<?> reportService, EntityQueryCriteria<?> criteria) {
        start(reportService, criteria, null);
    }

    public static void start(ReportService<?> reportService, EntityQueryCriteria<?> criteria, HashMap<String, Serializable> parameters) {
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setTimezoneOffset(TimeUtils.getTimezoneOffset());
        reportRequest.setCriteria(criteria);
        reportRequest.setParameters(parameters);
        start(reportService, reportRequest);
    }

    public static void start(ReportService<?> reportService, ReportRequest reportRequest) {

        final ReportDialog rd = new ReportDialog("Report", "Creating report...");
        rd.reportService = reportService;
        rd.show();

        AsyncCallback<String> callback = new BlockingAsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                rd.hide();
                throw new UnrecoverableClientError(caught);
            }

            @Override
            public void onSuccess(String deferredCorrelationID) {
                rd.setDeferredCorrelationID(deferredCorrelationID);
            }

        };

        reportService.createDownload(callback, reportRequest);
    }

    public ReportDialog(String title, String initialMessage) {
        super(title, initialMessage);
    }

    protected boolean useDownloadFrame() {
        return !BrowserType.isIE(); /* && !ClientState.isSeleniumMode(); */
    }

    @Override
    protected void onDeferredSuccess(DeferredProcessProgressResponse result) {

        if (result.isCompletedSuccess()) {
            downloadUrl = ((DeferredReportProcessProgressResponse) result).getDownloadLink();
            if (useDownloadFrame()) {
                new DownloadFrame(downloadUrl);
                dialog.hide();
            } else {
                VerticalPanel vp = new VerticalPanel();
                this.setWidget(vp);
                vp.add(new HTML("Report creation compleated"));
                HTML downloadLink = new HTML("<a href=\"" + downloadUrl + "\" target=\"_blank\">Download</a>");
                downloadLink.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        dialog.hide();
                    }
                });
                downloadLink.ensureDebugId("reportDownloadLink");
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
        return onClickClose();
    }
}
