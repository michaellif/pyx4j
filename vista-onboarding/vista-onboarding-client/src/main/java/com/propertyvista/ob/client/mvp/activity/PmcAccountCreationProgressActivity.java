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
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.ob.client.forms.StepStatusIndicator;
import com.propertyvista.ob.client.forms.StepStatusIndicator.StepStatus;
import com.propertyvista.ob.client.views.OnboardingViewFactory;
import com.propertyvista.ob.client.views.PmcAccountCreationProgressView;
import com.propertyvista.ob.rpc.dto.OnboardingCrmURL;
import com.propertyvista.ob.rpc.dto.OnboardingUserVisit;
import com.propertyvista.ob.rpc.services.PmcRegistrationService;

public class PmcAccountCreationProgressActivity extends AbstractActivity implements PmcAccountCreationProgressView.Presenter {

    private final static I18n i18n = I18n.get(PmcAccountCreationProgressActivity.class);

    private final static List<String> PROGRESS_STEPS = Arrays.asList(//@formatter:off
            i18n.tr("Creating and verifying DNS name..."),
            i18n.tr("Reserving DNS name..."),
            i18n.tr("Creating private database..."),
            i18n.tr("Loading company profile..."),
            i18n.tr("Loading initial data..."),
            i18n.tr("Validating loaded data..."),
            i18n.tr("Applying security policies..."),
            i18n.tr("Validating security policies..."),
            i18n.tr("Almost there: final configuration Health Check..."),
            i18n.tr("Sending confirmation Email..."),
            i18n.tr("Complete")
    );//@formatter:on

    private String defferedCorrelationId;

    private final AppPlace place;

    private final PmcAccountCreationProgressView view;

    private final DeferredProcessService deferredProcessStatusService;

    private final PmcRegistrationService pmcRegService;

    private String currentStep;

    private StepStatusIndicator.StepStatus currentStepStatus;

    private Timer progressTimer;

    // Avoid timer delayed fire
    private boolean timerProgressComplete = false;

    private int gracefulErrorCount = 0;

    private static final int REAL_POLL_INTERVAL = 1000;

    private static final int SIM_POLL_INTERVAL = 200;

    private static final String SIM_ID = "sim";

    public PmcAccountCreationProgressActivity(AppPlace place) {
        this.place = place;
        this.view = OnboardingViewFactory.instance(PmcAccountCreationProgressView.class);
        this.deferredProcessStatusService = GWT.<DeferredProcessService> create(DeferredProcessService.class);
        this.pmcRegService = GWT.<PmcRegistrationService> create(PmcRegistrationService.class);
        this.currentStep = PROGRESS_STEPS.get(0);
        this.currentStepStatus = StepStatus.INCOMPLETE;
    }

    @Override
    public AppPlace getPlace() {
        return place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        this.populate();
    }

    @Override
    public void populate() {
        OnboardingUserVisit visit = ClientContext.getUserVisit(OnboardingUserVisit.class);
        defferedCorrelationId = visit.accountCreationDeferredCorrelationId;

        view.init(PROGRESS_STEPS);

        final AsyncCallback<DeferredProcessProgressResponse> callback = new DefaultAsyncCallback<DeferredProcessProgressResponse>() {

            @Override
            public void onSuccess(DeferredProcessProgressResponse progress) {
                gracefulErrorCount = 0;
                if (progress.isCompleted()) {
                    progressTimer.cancel();
                    if (progress.isError()) {
                        throw new UnrecoverableClientError(progress.getMessage() != null ? progress.getMessage() : i18n.tr("Account Creation Failed!"));
                    } else {
                        progressTimer.scheduleRepeating(SIM_POLL_INTERVAL);
                    }
                }
                currentStepStatus = (currentStepStatus == StepStatus.INCOMPLETE) ? StepStatus.INPROGRESS : StepStatus.COMPLETE;
                // don't finish the last step until server reports that it finished
                if (!progress.isCompleted() & currentStep.equals(PROGRESS_STEPS.get(PROGRESS_STEPS.size() - 1)) & currentStepStatus == StepStatus.COMPLETE) {
                    currentStepStatus = StepStatus.INPROGRESS;
                }
                view.setStepStatus(currentStep, currentStepStatus);

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
                        view.setStepStatus(currentStep, currentStepStatus);
                    } else {
                        // here we got to the final step. so:
                        timerProgressComplete = true;
                        progressTimer.cancel();
                        onStepsProgressComplete();
                    }
                }

            }

            @Override
            public void onFailure(Throwable caught) {
                // Avoid getting to error state at all cost
                gracefulErrorCount++;
                if ((gracefulErrorCount > 4) && (!timerProgressComplete)) {
                    progressTimer.cancel();
                    super.onFailure(caught);
                }
            }

        };

        progressTimer = new Timer() {
            @Override
            public void run() {
                if (ApplicationMode.isDevelopment() & PmcAccountCreationProgressActivity.this.defferedCorrelationId.equals(SIM_ID)) {
                    DeferredProcessProgressResponse response = new DeferredProcessProgressResponse();
                    if (currentStep.equals(PROGRESS_STEPS.get(PROGRESS_STEPS.size() - 1)) & currentStepStatus == StepStatus.COMPLETE) {
                        response.setProgressMaximum(PROGRESS_STEPS.size());
                        response.setCompleted();
                    }
                    callback.onSuccess(response);
                } else if (!timerProgressComplete) {
                    deferredProcessStatusService.getStatus(callback, PmcAccountCreationProgressActivity.this.defferedCorrelationId, false);
                }
            }
        };
        progressTimer.scheduleRepeating(REAL_POLL_INTERVAL);

    }

    @Override
    public void refresh() {
    }

    private void onStepsProgressComplete() {
        if (!this.defferedCorrelationId.equals(SIM_ID)) {
            deferredProcessStatusService.getStatus(null, PmcAccountCreationProgressActivity.this.defferedCorrelationId, true);
        }
        pmcRegService.obtainCrmURL(new DefaultAsyncCallback<OnboardingCrmURL>() {
            @Override
            public void onSuccess(OnboardingCrmURL url) {
                view.setCrmSiteUrl(url);
            }
        });
    }
}
