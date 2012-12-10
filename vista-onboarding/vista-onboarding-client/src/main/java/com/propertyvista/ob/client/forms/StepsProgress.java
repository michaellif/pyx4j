/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-10
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.ob.client.forms;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessService;
import com.pyx4j.widgets.client.Label;

public class StepsProgress extends Composite {

    private final DeferredProcessService service;

    enum StepStatusValue {

        COMPLETE, INCOMPLETE, INPROGRESS;
    }

    public class StepStatus extends Composite {

        private Image complete;

        private Image inProgress;

        private Image incomplete;

        private StepStatusValue status;

        public StepStatus(String stepName) {
            FlowPanel stepStatusPanel = new FlowPanel();
            stepStatusPanel.getElement().getStyle().setProperty("width", "100%");

            Label stepNameLabel = new Label();

            stepNameLabel.setText(stepName);
            stepStatusPanel.add(stepNameLabel);

            stepStatusPanel.add(complete = new Image(StepsProgress.this.resources.complete()));
            complete.setVisible(false);
            complete.getElement().getStyle().setFloat(Float.RIGHT);

            stepStatusPanel.add(inProgress = new Image(StepsProgress.this.resources.inProgress()));
            inProgress.setVisible(false);
            inProgress.getElement().getStyle().setFloat(Float.RIGHT);

            stepStatusPanel.add(incomplete = new Image(StepsProgress.this.resources.incomplete()));
            incomplete.setVisible(true);
            incomplete.getElement().getStyle().setFloat(Float.RIGHT);

            stepNameLabel.getElement().getStyle().setProperty("display", "inline-block");
            stepNameLabel.setHeight("" + complete.getHeight() + "px");

            setStatus(StepStatusValue.INCOMPLETE);
            initWidget(stepStatusPanel);
        }

        public void setStatus(StepStatusValue value) {
            this.status = value;
            complete.setVisible(value == StepStatusValue.COMPLETE);
            inProgress.setVisible(value == StepStatusValue.INCOMPLETE);
            inProgress.setVisible(value == StepStatusValue.INPROGRESS);
        }

        public StepStatusValue getStatus() {
            return this.status;
        }
    }

    private final StepsProgressResources resources;

    private final List<StepStatus> stepStatuses;

    private Timer progressTimer;

    private final boolean pretendMode;

    private final int pollIntervalInSeconds;

    public StepsProgress(int pollIntervalInSecconds, boolean pretendMode, List<String> steps, StepsProgressResources resources) {
        this.pollIntervalInSeconds = pollIntervalInSecconds;
        this.service = GWT.create(DeferredProcessService.class);
        this.pretendMode = pretendMode;
        this.resources = resources;
        FlowPanel stepsPanel = new FlowPanel();
        this.stepStatuses = new ArrayList<StepsProgress.StepStatus>();

        for (String stepName : steps) {
            StepStatus stepStatus = new StepStatus(stepName);
            stepStatuses.add(stepStatus);
            stepsPanel.add(stepStatus);
        }
        initWidget(stepsPanel);
    }

    public StepsProgress(int pollIntervalInSecconds, boolean pretendMode, List<String> steps) {
        this(pollIntervalInSecconds, pretendMode, steps, StepsProgressResources.INSTANCE);
    }

    public void startProgresss(final String deferredCorrelationId) {

        final AsyncCallback<DeferredProcessProgressResponse> callback = new AsyncCallback<DeferredProcessProgressResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSuccess(DeferredProcessProgressResponse result) {
                if (!pretendMode) {
                    // TODO
                    throw new Error("Not Implemented");
                } else {
                    boolean startNext = stepStatuses.get(0).getStatus() == StepStatusValue.INCOMPLETE;
                    for (StepStatus stepStatus : stepStatuses) {
                        if (startNext) {
                            stepStatus.setStatus(StepStatusValue.INPROGRESS);
                            break;
                        } else {
                            if (stepStatus.getStatus() == StepStatusValue.INCOMPLETE) {
                                stepStatus.setStatus(StepStatusValue.COMPLETE);
                                startNext = true;
                            }
                        }
                    }
                }
                if (result.isCompleted()) {
                    onStepsProgressComplete(result.isCompletedSuccess(), result.getMessage());
                    progressTimer.cancel();
                }
            }

        };

        progressTimer = new Timer() {
            @Override
            public void run() {
                service.continueExecution(callback, deferredCorrelationId);
            }
        };
        progressTimer.scheduleRepeating(this.pollIntervalInSeconds * 1000);
    }

    public void onStepsProgressComplete(boolean isSuccessful, String message) {

    }

}
