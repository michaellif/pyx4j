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
package com.pyx4j.gwt.client.deferred;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessService;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.ProgressBar;

public class DeferredProgressPanel extends FlowPanel {

    private static final Logger log = LoggerFactory.getLogger(DeferredProcessDialog.class);

    private final DeferredProcessService service;

    /*
     * The Process executed in User request threads, e.g. On GAE
     */
    private final boolean executeByUserRequests;

    protected String deferredCorrelationId;

    protected long deferredProcessStartTime;

    private Timer progressTimer;

    private int checkProgressStatusSeconds = 3;

    private int checkProgressErrorCount = 0;

    protected final ProgressBar progressBar;

    private boolean completed;

    DeferredProgressListener listener;

    private Label messageBar;

    public DeferredProgressPanel(String width, String height, boolean executeByUserRequests, DeferredProgressListener listener) {
        this.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        service = GWT.create(DeferredProcessService.class);
        this.executeByUserRequests = executeByUserRequests;
        this.listener = listener;
        this.add(messageBar = new Label());
        this.add(progressBar = new ProgressBar());
        progressBar.setWidth(width);
        progressBar.setHeight(height);
        progressBar.getElement().getStyle().setProperty("marginLeft", "auto");
        progressBar.getElement().getStyle().setProperty("marginRight", "auto");
    }

    public void startProgress(final String deferredCorrelationId) {
        this.deferredCorrelationId = deferredCorrelationId;
        completed = false;
        checkProgressErrorCount = 0;
        deferredProcessStartTime = System.currentTimeMillis();

        progressTimer = new Timer() {
            @Override
            public void run() {
                checkProgressStatus(false);
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
        checkProgressErrorCount = 0;
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

    public void complete() {
        checkProgressStatus(true);
        progressBar.setProgress(0);
        if (progressTimer != null) {
            progressTimer.cancel();
            progressTimer = null;
        }
        deferredCorrelationId = null;
    }

    private void checkProgressStatus(boolean finalize) {
        AsyncCallback<DeferredProcessProgressResponse> progressHandlingCallback = new AsyncCallback<DeferredProcessProgressResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                if (deferredCorrelationId != null) {
                    checkProgressErrorCount++;
                    if (checkProgressErrorCount > 3) {
                        throw new UnrecoverableClientError(caught);
                    } else {
                        if (progressTimer != null) {
                            progressTimer.schedule(checkProgressStatusSeconds * 1000);
                        }
                    }
                }
            }

            @Override
            public void onSuccess(DeferredProcessProgressResponse result) {
                checkProgressErrorCount = 0;
                if (result.isError()) {
                    log.info("Deferred erred in " + TimeUtils.secSince(deferredProcessStartTime));
                    if (!completed) {
                        listener.onDeferredError(result);
                        completed = true;
                    }
                    deferredCorrelationId = null;
                    progressTimer = null;
                } else if (result.isCompleted()) {
                    progressBar.setProgress(progressBar.getMaxProgress());
                    log.info("Deferred completed in " + TimeUtils.secSince(deferredProcessStartTime));
                    if (!completed) {
                        listener.onDeferredSuccess(result);
                        completed = true;
                    }
                    deferredCorrelationId = null;
                    progressTimer = null;
                } else {
                    messageBar.setText(result.getMessage() != null ? result.getMessage() : "");
                    progressBar.setMaxProgress(result.getProgressMaximum());
                    progressBar.setProgress(result.getProgress());
                    if (progressTimer != null) {
                        progressTimer.schedule(checkProgressStatusSeconds * 1000);
                    }
                }
            }

        };

        if (executeByUserRequests && !finalize) {
            service.continueExecution(progressHandlingCallback, deferredCorrelationId);
        } else {
            service.getStatus(progressHandlingCallback, deferredCorrelationId, finalize);
        }

    }
}
