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
 * Created on Aug 24, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessService;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.widgets.client.ProgressBar;

public class DeferredProgressPanel extends FlowPanel {

    private static final Logger log = LoggerFactory.getLogger(DeferredProcessDialog.class);

    private final DeferredProcessService service;

    protected String deferredCorrelationId;

    protected long deferredProcessStartTime;

    private Timer progressTimer;

    private int checkProgressStatusSeconds = 3;

    protected final ProgressBar progressBar;

    public DeferredProgressPanel(String width, String height) {
        service = GWT.create(DeferredProcessService.class);
        this.add(progressBar = new ProgressBar());
        progressBar.setWidth(width);
        progressBar.setHeight(height);
    }

    public void startProgress(final String deferredCorrelationId) {
        this.deferredCorrelationId = deferredCorrelationId;
        deferredProcessStartTime = System.currentTimeMillis();

        progressTimer = new Timer() {
            @Override
            public void run() {
                checkProgressStatus();
            }
        };
        progressTimer.schedule(checkProgressStatusSeconds * 1000);
    }

    public void setCheckProgressStatusSeconds(int checkProgressStatusSeconds) {
        this.checkProgressStatusSeconds = checkProgressStatusSeconds;
    }

    public void reset() {
        progressBar.setProgress(0);
        if (progressTimer != null) {
            progressTimer.cancel();
            progressTimer = null;
        }
        deferredCorrelationId = null;
    }

    public void cancelProgress() {
        if (progressTimer != null) {
            progressTimer.cancel();
            progressTimer = null;
        }
        if (deferredCorrelationId != null) {
            service.cancel(null, deferredCorrelationId);
            deferredCorrelationId = null;
        }
    }

    protected void onDeferredSuccess(DeferredProcessProgressResponse result) {
    }

    protected void onDeferredError(DeferredProcessProgressResponse result) {
    }

    private void checkProgressStatus() {
        AsyncCallback<DeferredProcessProgressResponse> progressHandlingCallback = new AsyncCallback<DeferredProcessProgressResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                if (deferredCorrelationId != null) {
                    throw new UnrecoverableClientError(caught);
                }
            }

            @Override
            public void onSuccess(DeferredProcessProgressResponse result) {
                if (result.isError()) {
                    log.info("Deferred completed in " + TimeUtils.secSince(deferredProcessStartTime));
                    onDeferredError(result);
                    deferredCorrelationId = null;
                    progressTimer = null;
                } else if (result.isCompleted()) {
                    progressBar.setProgress(progressBar.getMaxProgress());
                    log.info("Deferred completed in " + TimeUtils.secSince(deferredProcessStartTime));
                    onDeferredSuccess(result);
                    deferredCorrelationId = null;
                    progressTimer = null;
                } else {
                    progressBar.setMaxProgress(result.getProgressMaximum());
                    progressBar.setProgress(result.getProgress());
                    if (progressTimer != null) {
                        progressTimer.schedule(checkProgressStatusSeconds * 1000);
                    }
                }
            }

        };

        service.getStatus(progressHandlingCallback, deferredCorrelationId);

    }
}
