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
package com.propertyvista.portal.client.ptapp;

import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.propertyvista.portal.client.ptapp.activity.SecondNavigActivity;
import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.domain.VistaBehavior;
import com.propertyvista.portal.domain.pt.ApplicationProgress;
import com.propertyvista.portal.domain.pt.ApplicationWizardStep;
import com.propertyvista.portal.domain.pt.ApplicationWizardSubstep;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;
import com.propertyvista.portal.rpc.pt.CurrentApplication;
import com.propertyvista.portal.rpc.pt.PotentialTenantServices;
import com.propertyvista.portal.rpc.pt.SiteMap;
import com.propertyvista.portal.rpc.pt.services.ApplicationServices;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.security.client.SecurityControllerEvent;
import com.pyx4j.security.client.SecurityControllerHandler;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

public class PtAppWizardManager {

    private final static Logger log = LoggerFactory.getLogger(PtAppWizardManager.class);

    private static I18n i18n = I18nFactory.getI18n(PtAppWizardManager.class);

    private static PtAppWizardManager instance;

    private ApplicationProgress applicationProgress;

    private UnitSelectionCriteria unitSelectionCriteria;

    private PtAppWizardManager() {
        AppSite.instance().getEventBus().addHandler(SecurityControllerEvent.getType(), new SecurityControllerHandler() {

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
        ClientContext.obtainAuthenticationData(new DefaultAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                obtainUnitSelection();
            }

            //TODO remove this when initial application message is implemented
            @Override
            public void onFailure(Throwable caught) {
                PtAppSite.instance().getHistoryHandler().handleCurrentHistory();
                super.onFailure(caught);
            }
        });
    }

    private void obtainUnitSelection() {
        unitSelectionCriteria = EntityFactory.create(UnitSelectionCriteria.class);
        unitSelectionCriteria.propertyCode().setValue(Window.Location.getParameter("b"));
        unitSelectionCriteria.floorplanName().setValue(Window.Location.getParameter("u"));
        unitSelectionCriteria.availableFrom().setValue(new Date());
        Date d = new Date();
        // Now + 1 months
        CalendarUtil.addMonthsToDate(d, 1);
        unitSelectionCriteria.availableTo().setValue(d);

        if (ApplicationMode.isDevelopment()) {
            if (unitSelectionCriteria.floorplanName().isNull()) {
                unitSelectionCriteria.floorplanName().setValue(DemoData.REGISTRATION_DEFAULT_FLOORPLAN);
            }
            if (unitSelectionCriteria.propertyCode().isNull()) {
                unitSelectionCriteria.propertyCode().setValue(DemoData.REGISTRATION_DEFAULT_PROPERTY_CODE);
            }
        }

        RPCManager.execute(PotentialTenantServices.UnitExists.class, unitSelectionCriteria, new DefaultAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                PtAppSite.instance().getHistoryHandler().handleCurrentHistory();
                if (!result) {
                    PtAppSite.instance().showMessageDialog(i18n.tr("We can't find that Building or avalable Units"), "Error", "Back", new Command() {
                        @Override
                        public void execute() {
                            History.back();
                        }
                    });
                }
            }

            //TODO remove this when initial application message is implemented
            @Override
            public void onFailure(Throwable caught) {
                PtAppSite.instance().getHistoryHandler().handleCurrentHistory();
                super.onFailure(caught);
            }
        });

    }

    public ApplicationProgress getApplicationProgress() {
        return applicationProgress;
    }

    private ApplicationWizardStep getStep(Place current) {
        String placeId = AppSite.instance().getHistoryMapper().getPlaceId(current);
        if (placeId == null) {
            return null;
        }
        for (ApplicationWizardStep step : applicationProgress.steps()) {
            if (placeId.equals(step.placeId().getValue())) {
                return step;
            }
        }
        return null;
    }

    public void nextStep() {
        ApplicationWizardStep currentStep = getStep(AppSite.instance().getPlaceController().getWhere());
        ((ApplicationServices) GWT.create(ApplicationServices.class)).getApplicationProgress(new DefaultAsyncCallback<ApplicationProgress>() {
            @Override
            public void onSuccess(ApplicationProgress result) {
                applicationProgress = result;
                navigationByApplicationProgress();
            }
        }, currentStep);
    }

    private void navigationByApplicationProgress() {
        for (ApplicationWizardStep step : applicationProgress.steps()) {
            if (ApplicationWizardStep.Status.latest.equals(step.status().getValue())) {
                AppPlace place = AppSite.instance().getHistoryMapper().getPlace(step.placeId().getValue());
                if (step.substeps().size() > 0) {
                    for (ApplicationWizardSubstep substep : step.substeps()) {
                        if (ApplicationWizardStep.Status.latest.equals(substep.status().getValue())) {
                            HashMap<String, String> args = new HashMap<String, String>();
                            args.put(SecondNavigActivity.STEP_ARG_NAME, substep.placeArgument().getStringView());
                            place.setArgs(args);
                        }
                    }
                }
                AppSite.instance().getPlaceController().goTo(place);
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
        if (ClientSecurityController.checkBehavior(VistaBehavior.POTENCIAL_TENANT)) {

            ((ApplicationServices) GWT.create(ApplicationServices.class)).getCurrentApplication(new DefaultAsyncCallback<CurrentApplication>() {
                @Override
                public void onSuccess(CurrentApplication result) {
                    applicationProgress = result.progress;
                    initApplicationProcess();
                }
            }, unitSelectionCriteria);

        } else {
            applicationProgress = null;
            AppSite.instance().getPlaceController().goTo(new SiteMap.CreateAccount());
        }
    }

}
