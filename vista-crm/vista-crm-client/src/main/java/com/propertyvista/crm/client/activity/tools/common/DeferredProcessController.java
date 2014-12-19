/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-04-11
 * @author ArtyomB
 */
package com.propertyvista.crm.client.activity.tools.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.gwt.client.deferred.DeferredProcessDialog;
import com.pyx4j.gwt.client.deferred.DeferredProgressListener;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessService;

public class DeferredProcessController {

    private static final Logger log = LoggerFactory.getLogger(DeferredProcessDialog.class);

    private final DeferredProcessService service;

    private String deferredCorrelationId;

    private boolean completed;

    /** interval in milliseconds */
    private final int checkInterval = 3000;

    private int checkProgressErrorCount;

    private long deferredProcessStartTime;

    private Timer progressTimer;

    private DeferredProgressListener callback;

    public DeferredProcessController() {
        service = GWT.<DeferredProcessService> create(DeferredProcessService.class);
    }

    public void startCheckingProgress(String deferredCorrelationId, DeferredProgressListener callback) {
        this.callback = callback;
        this.completed = false;
        this.deferredCorrelationId = deferredCorrelationId;
        this.checkProgressErrorCount = 0;
        this.deferredProcessStartTime = System.currentTimeMillis();
        this.progressTimer = new Timer() {
            @Override
            public void run() {
                checkProgressStatus(false);
            }
        };
        this.progressTimer.schedule(checkInterval);
    }

    public void cancelDeferredProcess() {
        if (progressTimer != null) {
            progressTimer.cancel();
            progressTimer = null;
        }
        if (deferredCorrelationId != null) {
            service.cancel(null, deferredCorrelationId);
            deferredCorrelationId = null;
        }
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
                            progressTimer.schedule(checkInterval);
                        }
                    }
                }
            }

            @Override
            public void onSuccess(DeferredProcessProgressResponse progress) {
                checkProgressErrorCount = 0;
                if (progress.isError()) {
                    log.info("Deferred erred in " + TimeUtils.secSince(deferredProcessStartTime));
                    if (!completed) {
                        callback.onDeferredError(progress);
                        completed = true;
                    }
                    deferredCorrelationId = null;
                    progressTimer = null;
                } else if (progress.isCompleted()) {
                    log.info("Deferred completed in " + TimeUtils.secSince(deferredProcessStartTime));
                    if (!completed) {
                        callback.onDeferredSuccess(progress);
                        completed = true;
                    }
                    deferredCorrelationId = null;
                    progressTimer = null;
                } else {
                    callback.onDeferredProgress(progress);
                    if (progressTimer != null) {
                        progressTimer.schedule(checkInterval);
                    }
                }
            }

        };
        service.getStatus(progressHandlingCallback, deferredCorrelationId, finalize);

    }

}
