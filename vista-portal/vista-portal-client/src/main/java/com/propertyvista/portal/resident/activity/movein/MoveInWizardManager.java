/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 20, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.activity.movein;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.gwt.commons.ClientEventBus;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.portal.resident.events.MoveInWizardStateChangeEvent;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.MoveInWizardStatusTO;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.MoveInWizardStep;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.MoveInWizardStepStatusTO;
import com.propertyvista.portal.rpc.portal.resident.services.movein.IMoveInPlace;
import com.propertyvista.portal.rpc.portal.resident.services.movein.MoveInWizardService;

public class MoveInWizardManager {

    private static final I18n i18n = I18n.get(MoveInWizardManager.class);

    private static MoveInWizardStatusTO wizardStatus;

    private static MoveInWizardStep currentStep;

    private static boolean started;

    private static boolean completed;

    public static void init() {
        AppSite.getEventBus().addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
            @Override
            public void onPlaceChange(final PlaceChangeEvent event) {
                if (SecurityController.check(PortalResidentBehavior.MoveInWizardCompletionAvailable)) {

                    GWT.<MoveInWizardService> create(MoveInWizardService.class).obtainIncompleteSteps(new AsyncCallback<MoveInWizardStatusTO>() {

                        @Override
                        public void onSuccess(MoveInWizardStatusTO result) {
                            wizardStatus = result;

                            setCurrentStep(event.getNewPlace() instanceof IMoveInPlace ? getCurrentStep() : null);

                            ClientEventBus.instance.fireEvent(new MoveInWizardStateChangeEvent());
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            throw new Error(i18n.tr("Something went wrong! Try again later."));
                        }
                    });
                }
            }
        });
    }

    public static MoveInWizardStatusTO getMoveInWizardStatus() {
        return wizardStatus;
    }

    public static boolean isStepComplete(MoveInWizardStep step) {
        if (wizardStatus != null) {
            for (MoveInWizardStepStatusTO stepStatus : wizardStatus.steps()) {
                if (stepStatus.equals(step)) {
                    return stepStatus.complete().getValue();
                }
            }
        }
        return false;
    }

    public static boolean isStepIncluded(MoveInWizardStep step) {
        if (wizardStatus != null) {
            for (MoveInWizardStepStatusTO stepStatus : wizardStatus.steps()) {
                if (stepStatus.equals(step)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void markStepComplete(MoveInWizardStep step) {
        GWT.<MoveInWizardService> create(MoveInWizardService.class).skipStep(null, step);
    }

    public static void nextStep() {
        started = true;
        for (MoveInWizardStepStatusTO step : wizardStatus.steps()) {
            if (!isStepComplete(step.step().getValue())) {
                setCurrentStep(step.step().getValue());
                return;
            }
        }
    }

    public static boolean isStarted() {
        return started;
    }

    public static boolean isCompleted() {
        return completed;
    }

    public static MoveInWizardStep getCurrentStep() {
        return currentStep;
    }

    public static void setCurrentStep(MoveInWizardStep step) {
        if (!isStepComplete(step)) {
            MoveInWizardManager.currentStep = step;
        }
    }

}
