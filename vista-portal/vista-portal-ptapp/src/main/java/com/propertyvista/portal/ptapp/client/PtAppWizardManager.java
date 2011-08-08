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

import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.security.client.SecurityControllerEvent;
import com.pyx4j.security.client.SecurityControllerHandler;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.VistaBehavior;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.domain.tenant.ptapp.ApplicationWizardStep;
import com.propertyvista.domain.tenant.ptapp.ApplicationWizardStep.Status;
import com.propertyvista.domain.tenant.ptapp.ApplicationWizardSubstep;
import com.propertyvista.portal.rpc.portal.services.AuthenticationService;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationService;

public class PtAppWizardManager {

    private final static Logger log = LoggerFactory.getLogger(PtAppWizardManager.class);

    private static I18n i18n = I18nFactory.getI18n(PtAppWizardManager.class);

    private static PtAppWizardManager instance;

    private Application application;

    private PtAppWizardManager() {
        AppSite.getEventBus().addHandler(SecurityControllerEvent.getType(), new SecurityControllerHandler() {

            @Override
            public void onSecurityContextChange(SecurityControllerEvent event) {
                loadCurrentApplication();
            }
        });
        //TODO implement initial application message
        //showMessageDialog(i18n.tr("Application is looking for building availability..."), i18n.tr("Loading..."), null, null);
        obtainAuthenticationData();
    }

    public static void initWizard() {
        if (instance == null) {
            instance = new PtAppWizardManager();
        } else {
            throw new RuntimeException("PtAppWizardManager is already initialized");
        }
    }

    public static PtAppWizardManager instance() {
        if (instance == null) {
            throw new RuntimeException("PtAppWizardManager is not yet initialized");
        }
        return instance;
    }

    private void obtainAuthenticationData() {
        ClientContext.obtainAuthenticationData((com.pyx4j.security.rpc.AuthenticationService) GWT.create(AuthenticationService.class),
                new DefaultAsyncCallback<Boolean>() {

                    @Override
                    public void onSuccess(Boolean result) {
//                        if (!result) {
//                            PtAppSite.instance().showMessageDialog(i18n.tr("We can't find requested Application"), "Error", "Back", new Command() {
//                                @Override
//                                public void execute() {
//                                    History.back();
//                                }
//                            });
//                        }
                    }

                    //TODO remove this when initial application message is implemented
                    @Override
                    public void onFailure(Throwable caught) {
                        PtAppSite.getHistoryHandler().handleCurrentHistory();
                        super.onFailure(caught);
                    }
                });
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
        String stepArg = place.getArg(PtSiteMap.STEP_ARG_NAME);
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
                navigationByApplicationProgress();
            }
        }, currentStep, substep);
    }

    private boolean shouldSelect(IPrimitive<Status> status) {
        return (ApplicationWizardStep.Status.latest.equals(status.getValue()) || ApplicationWizardStep.Status.invalid.equals(status.getValue()));
    }

    private void navigationByApplicationProgress() {
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
                AppSite.getPlaceController().goTo(place);
                return;
            }
        }
        throw new UnrecoverableClientError("Application Wizard doesn't have 'latest' step");
    }

    private void initApplicationProcess() {
        log.info("start application");
        navigationByApplicationProgress();
    }

    private void loadCurrentApplication() {
        if (ClientSecurityController.checkBehavior(VistaBehavior.POTENTIAL_TENANT)) {

            ((ApplicationService) GWT.create(ApplicationService.class)).getApplication(new DefaultAsyncCallback<Application>() {
                @Override
                public void onSuccess(Application result) {
                    application = result;
                    initApplicationProcess();
                }
            });

        } else {
            application = null;
            AppSite.getPlaceController().goTo(new PtSiteMap.CreateAccount());
        }
    }

}
