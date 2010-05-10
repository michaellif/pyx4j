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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.report.DeferredReportProcessProgressResponse;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.essentials.rpc.report.ReportServices;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.client.BlockingAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.widgets.client.util.BrowserType;

public class ReportDialog extends DeferredProcessDialog {

    private String downloadUrl;

    public static void start(EntitySearchCriteria<?> criteria) {
        start(ReportServices.Search.class, criteria);
    }

    public static void start(Class<? extends ReportServices.Search> reportServiceInterface, EntitySearchCriteria<?> criteria) {

        final ReportDialog rd = new ReportDialog("Report", "Creating report...");
        rd.show();

        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setTimezoneOffset(TimeUtils.getTimezoneOffset());
        reportRequest.setCriteria(criteria);

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

        RPCManager.execute(reportServiceInterface, reportRequest, callback);
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
            RPCManager.execute(ReportServices.CancelDownload.class, downloadUrl, null);
        }
        return onClickClose();
    }
}
