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
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.propertyvista.common.client.Message;
import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.domain.VistaBehavior;
import com.propertyvista.portal.domain.pt.ApplicationWizardStep;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;
import com.propertyvista.portal.rpc.pt.CurrentApplication;
import com.propertyvista.portal.rpc.pt.PotentialTenantServices;
import com.propertyvista.portal.rpc.pt.SiteMap;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.ClientSecurityController;
import com.pyx4j.security.client.SecurityControllerEvent;
import com.pyx4j.security.client.SecurityControllerHandler;

public class PtAppWizardManager {

    private final static Logger log = LoggerFactory.getLogger(PtAppWizardManager.class);

    private static I18n i18n = I18nFactory.getI18n(PtAppWizardManager.class);

    private static PtAppWizardManager instance;

    private CurrentApplication currentApplication;

    private List<WizardStep> wizardSteps;

    private final SiteGinjector ginjector;

    private UnitSelectionCriteria unitSelectionCriteria;

    private PtAppWizardManager(SiteGinjector ginjector) {
        this.ginjector = ginjector;
        ginjector.getEventBus().addHandler(SecurityControllerEvent.getType(), new SecurityControllerHandler() {

            @Override
            public void onSecurityContextChange(SecurityControllerEvent event) {
                getCurrentApplication();
            }
        });
        wizardSteps = new Vector<WizardStep>();
        //TODO implement initial application message
        //showMessageDialog(i18n.tr("Application is looking for building availability..."), i18n.tr("Loading..."), null, null);
        obtainAuthenticationData();
    }

    public List<WizardStep> getWizardSteps() {
        return wizardSteps;
    }

    public static void initWizard(final SiteGinjector ginjector) {
        if (instance == null) {
            instance = new PtAppWizardManager(ginjector);
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
                ginjector.getPlaceHistoryHandler().handleCurrentHistory();
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
                ginjector.getPlaceHistoryHandler().handleCurrentHistory();
                if (!result) {
                    PtAppSite.instance().showMessageDialog(i18n.tr("We can't find that Building or avalable Units"), "Error", "Back",
                            new Command() {
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
                ginjector.getPlaceHistoryHandler().handleCurrentHistory();
                super.onFailure(caught);
            }
        });

    }

    private WizardStep getStep(Place current) {
        for (WizardStep step : getWizardSteps()) {
            if (step.getPlace().equals(current)) {
                return step;
            }
        }
        return null;
    }

    public void nextStep() {
        WizardStep currentStep = getStep(ginjector.getPlaceController().getWhere());
        if (currentStep != null) {
            //TODO hasAlert ?
            currentStep.setStatus(ApplicationWizardStep.Status.complete);
        }

        int idx = getWizardSteps().indexOf(currentStep);
        if (idx == -1) {
            idx = 0;
        } else {
            idx++;
        }
        WizardStep nextStep = getWizardSteps().get(idx);
        if (nextStep.getStatus() == ApplicationWizardStep.Status.notVisited) {
            nextStep.setStatus(ApplicationWizardStep.Status.latest);
        }

        ginjector.getPlaceController().goTo(nextStep.getPlace());

        // save progress to DB
        saveApplicationProgress();
    }

    private void saveApplicationProgress() {
        RPCManager.execute(PotentialTenantServices.Save.class, currentApplication.progress, new DefaultAsyncCallback<IEntity>() {
            @Override
            public void onSuccess(IEntity result) {
            }
        });
    }

    private void initApplicationProcess(CurrentApplication result) {
        currentApplication = result;
        wizardSteps = new Vector<WizardStep>();

        for (ApplicationWizardStep step : currentApplication.progress.steps()) {
            wizardSteps.add(new WizardStep(step));
        }

        log.info("start application {}", currentApplication.application);

        for (WizardStep step : wizardSteps) {
            if (step.getStatus() == ApplicationWizardStep.Status.latest) {
                ginjector.getPlaceController().goTo(step.getPlace());
                return;
            }
        }
        nextStep();
    }

    private void getCurrentApplication() {
        if (ClientSecurityController.checkBehavior(VistaBehavior.POTENCIAL_TENANT)) {

            RPCManager.execute(PotentialTenantServices.GetCurrentApplication.class, unitSelectionCriteria, new DefaultAsyncCallback<CurrentApplication>() {

                @Override
                public void onSuccess(CurrentApplication result) {
                    initApplicationProcess(result);
                }
            });
        } else {
            currentApplication = null;
            wizardSteps = new Vector<WizardStep>();
            ginjector.getPlaceController().goTo(new SiteMap.CreateAccount());
        }
    }

    public static EventBus getEventBus() {
        return instance().ginjector.getEventBus();
    }

}
