/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 8, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.VistaBehavior;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.domain.tenant.ptapp.ApplicationWizardStep;
import com.propertyvista.domain.tenant.ptapp.ApplicationWizardStep.Status;
import com.propertyvista.domain.tenant.ptapp.ApplicationWizardSubstep;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationService;

public class PtAppWizardManager {

    private static I18n i18n = I18nFactory.getI18n(PtAppWizardManager.class);

    private final static Logger log = LoggerFactory.getLogger(PtAppWizardManager.class);

    private Application application;

    PtAppWizardManager() {
    }

    public List<ApplicationWizardStep> getApplicationWizardSteps() {
        return application.steps();
    }

    private ApplicationWizardStep getStep(Place place) {
        String placeId = AppSite.getPlaceId(place);
        if (placeId == null) {
            return null;
        }
        for (ApplicationWizardStep step : application.steps()) {
            if (placeId.equals(step.placeId().getValue())) {
                return step;
            }
        }
        return null;
    }

    private ApplicationWizardSubstep getSubStep(AppPlace place, ApplicationWizardStep currentStep) {
        if ((place == null) || (currentStep == null)) {
            return null;
        }
        String stepArg = place.getFirstArg(PtSiteMap.STEP_ARG_NAME);
        // Find current Substep
        if (stepArg == null) {
            return null;
        }

        for (ApplicationWizardSubstep substep : currentStep.substeps()) {
            if (stepArg.equals(substep.placeArgument().getStringView())) {
                return substep;
            }
        }
        return null;
    }

    public void nextStep() {
        AppPlace currentPlace = AppSite.getWhere();
        ApplicationWizardStep currentStep = getStep(currentPlace);
        ApplicationWizardSubstep substep = getSubStep(currentPlace, currentStep);
        ((ApplicationService) GWT.create(ApplicationService.class)).getApplicationProgress(new DefaultAsyncCallback<Application>() {
            @Override
            public void onSuccess(Application result) {
                application = result;
                forwardByApplicationProgress(new DefaultAsyncCallback<AppPlace>() {

                    @Override
                    public void onSuccess(AppPlace result) {
                        AppSite.getPlaceController().goTo(result);
                    }
                });
            }
        }, currentStep, substep);
    }

    public void forwardTo(final AppPlace place, final AsyncCallback<AppPlace> callback) {
        if (!ClientSecurityController.checkAnyBehavior(VistaBehavior.PROSPECTIVE_TENANT, VistaBehavior.GUARANTOR)) {
            callback.onSuccess(null);
        }
        if (application != null) {
            forwardToApplicationNavigablePlace(place, callback);
        } else {
            ((ApplicationService) GWT.create(ApplicationService.class)).getApplication(new DefaultAsyncCallback<Application>() {
                @Override
                public void onSuccess(Application result) {
                    application = result;
                    forwardToApplicationNavigablePlace(place, callback);
                }
            });
        }
    }

    public void onLogout() {
        application = null;
    }

    private boolean isStepNavigable(AppPlace place) {
        for (ApplicationWizardStep step : application.steps()) {
            if (place.getPlaceId().equals(step.placeId().getValue()) && (!ApplicationWizardStep.Status.notVisited.equals(step.status().getValue()))) {
                if (step.substeps().size() > 0) {
                    for (ApplicationWizardSubstep substep : step.substeps()) {
                        if (substep.placeArgument().getStringView().equals(place.getArg(PtSiteMap.STEP_ARG_NAME))) {
                            return (!ApplicationWizardStep.Status.notVisited.equals(substep.status().getValue()));
                        }
                    }
                } else {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    private void forwardToApplicationNavigablePlace(AppPlace place, AsyncCallback<AppPlace> callback) {
        if (place != null && isStepNavigable(place)) {
            callback.onSuccess(place);
        } else {
            forwardByApplicationProgress(callback);
        }
    }

    private boolean shouldSelect(IPrimitive<Status> status) {
        return (ApplicationWizardStep.Status.latest.equals(status.getValue()) || ApplicationWizardStep.Status.invalid.equals(status.getValue()));
    }

    private void forwardByApplicationProgress(AsyncCallback<AppPlace> callback) {
        for (ApplicationWizardStep step : application.steps()) {
            if (shouldSelect(step.status())) {
                AppPlace place = AppSite.getHistoryMapper().getPlace(step.placeId().getValue());
                if (step.substeps().size() > 0) {
                    loopOversubsteps: for (ApplicationWizardSubstep substep : step.substeps()) {
                        if (shouldSelect(substep.status())) {
                            place.putArg(PtSiteMap.STEP_ARG_NAME, substep.placeArgument().getStringView());
                            break loopOversubsteps;
                        }
                    }
                }
                callback.onSuccess(place);
                return;
            }
        }
        callback.onFailure(new UnrecoverableClientError("Application Wizard doesn't have 'latest' step"));
    }
}
