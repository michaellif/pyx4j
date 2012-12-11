/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.ob.client.mvp.activity;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.ob.client.forms.StepStatusIndicator;
import com.propertyvista.ob.client.forms.StepStatusIndicator.StepStatus;
import com.propertyvista.ob.client.views.OnboardingViewFactory;
import com.propertyvista.ob.client.views.PmcAccountCreationProgressView;
import com.propertyvista.ob.rpc.OnboardingSiteMap;

public class PmcAccountCreationProgressActivity extends AbstractActivity implements PmcAccountCreationProgressView.Presenter {

    private final static I18n i18n = I18n.get(PmcAccountCreationProgressActivity.class);

    private final static List<String> PROGRESS_STEPS = Arrays.asList(//@formatter:off
            i18n.tr("Creating and verifyng DNS name"),
            i18n.tr("Reserving DNS name"),
            i18n.tr("Creating private database"),
            i18n.tr("Loading company profile"),
            i18n.tr("Loading initial data"),
            i18n.tr("Validating loaded data"),
            i18n.tr("Applying security policies"),
            i18n.tr("Validating security policies"),
            i18n.tr("Almost there... final configuration Health Check"),
            i18n.tr("Sending confirmation Email")
    );//@formatter:on

    private final String defferedCorrelationId;

    private final PmcAccountCreationProgressView view;

    private final DeferredProcessService service;

    private String currentStep;

    private StepStatusIndicator.StepStatus currentStepStatus;

    private Timer progressTimer;

    private final int pollIntervalInSeconds;

    public PmcAccountCreationProgressActivity(AppPlace place) {
        this.view = OnboardingViewFactory.instance(PmcAccountCreationProgressView.class);
        this.defferedCorrelationId = place.getFirstArg("id");
        this.service = GWT.<DeferredProcessService> create(DeferredProcessService.class);

        this.currentStep = PROGRESS_STEPS.get(0);
        this.currentStepStatus = StepStatus.INCOMPLETE;
        this.pollIntervalInSeconds = 1;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        this.populate();
    }

    @Override
    public void populate() {
        view.init(PROGRESS_STEPS);

        final AsyncCallback<DeferredProcessProgressResponse> callback = new AsyncCallback<DeferredProcessProgressResponse>() {

            @Override
            public void onSuccess(DeferredProcessProgressResponse result) {
                currentStepStatus = (currentStepStatus == StepStatus.INCOMPLETE) ? StepStatus.INPROGRESS : StepStatus.COMPLETE;
                view.setStatus(currentStep, currentStepStatus);

                if (currentStepStatus == StepStatus.COMPLETE) {

                    Iterator<String> steps = PROGRESS_STEPS.iterator();
                    while (steps.hasNext()) {
                        if (steps.next().equals(currentStep)) {
                            break;
                        }
                    }
                    if (steps.hasNext()) {
                        currentStep = steps.next();
                        currentStepStatus = StepStatus.INPROGRESS;
                        view.setStatus(currentStep, currentStepStatus);
                    }
                }

                if (result.isCompleted()) {
                    onStepsProgressComplete(result.isCompletedSuccess(), result.getMessage());
                    progressTimer.cancel();
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                progressTimer.cancel();
                onStepsProgressComplete(false, caught.getMessage());
            }

        };

        progressTimer = new Timer() {
            @Override
            public void run() {
                if (ApplicationMode.isDevelopment() & PmcAccountCreationProgressActivity.this.defferedCorrelationId.equals("sim")) {
                    DeferredProcessProgressResponse response = new DeferredProcessProgressResponse();
                    if (currentStep.equals(PROGRESS_STEPS.get(PROGRESS_STEPS.size() - 1)) & currentStepStatus == StepStatus.COMPLETE) {
                        response.setCompleted();
                        response.setProgressMaximum(PROGRESS_STEPS.size());
                    }
                    callback.onSuccess(response);
                } else {
                    service.continueExecution(callback, PmcAccountCreationProgressActivity.this.defferedCorrelationId);
                }
            }
        };
        progressTimer.scheduleRepeating(this.pollIntervalInSeconds * 1000);

    }

    @Override
    @Deprecated
    public void refresh() {
        // TODO Auto-generated method stub
    }

    private void onStepsProgressComplete(boolean completedSuccess, String message) {
        AppSite.getPlaceController().goTo(new OnboardingSiteMap.PmcAccountCreationComplete());
    }
}
