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
import com.google.web.bindery.event.shared.HandlerRegistration;

import com.pyx4j.gwt.commons.ClientEventBus;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.client.BehaviorChangeEvent;
import com.pyx4j.security.client.BehaviorChangeHandler;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.portal.resident.events.MoveInWizardStateChangeEvent;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.MoveInWizardStatusTO;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.MoveInWizardStep;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.MoveInWizardStepStatusTO;
import com.propertyvista.portal.rpc.portal.resident.services.movein.MoveInWizardService;

public class MoveInWizardManager {

    public enum MoveInWizardState {
        preface, wizard, confirmation
    }

    private static final I18n i18n = I18n.get(MoveInWizardManager.class);

    private static MoveInWizardStatusTO wizardStatus;

    private static MoveInWizardStep currentStep;

    private static MoveInWizardState moveInWizardState;

    private static HandlerRegistration handlerRegistration;

    public static void init() {

        AppSite.getEventBus().addHandler(BehaviorChangeEvent.getType(), new BehaviorChangeHandler() {

            @Override
            public void onBehaviorChange(BehaviorChangeEvent event) {

                if (handlerRegistration == null && SecurityController.check(PortalResidentBehavior.MoveInWizardCompletionRequired)) {
                    moveInWizardState = MoveInWizardState.preface;
                    handlerRegistration = AppSite.getEventBus().addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
                        @Override
                        public void onPlaceChange(final PlaceChangeEvent event) {
                            System.out.println("++++++++++++++onPlaceChange");
                            GWT.<MoveInWizardService> create(MoveInWizardService.class).obtainSteps(new AsyncCallback<MoveInWizardStatusTO>() {

                                @Override
                                public void onSuccess(MoveInWizardStatusTO result) {
                                    wizardStatus = result;

                                    if (event.getNewPlace() instanceof ResidentPortalSiteMap.MoveIn.MoveInWizard) {
                                        nextStep();
                                    }

                                    if (isComplete()) {
                                        moveInWizardState = MoveInWizardState.confirmation;
                                    }

                                    ClientEventBus.instance.fireEvent(new MoveInWizardStateChangeEvent());

                                    if (moveInWizardState == MoveInWizardState.preface) {
                                        moveInWizardState = MoveInWizardState.wizard;
                                    }

                                }

                                @Override
                                public void onFailure(Throwable caught) {
                                    throw new Error(i18n.tr("Something went wrong! Try again later."));
                                }
                            });
                        }
                    });
                } else if (handlerRegistration != null && !SecurityController.check(PortalResidentBehavior.MoveInWizardCompletionRequired)) {
                    reset();
                }

            }

        });

    }

    public static boolean isStepComplete(MoveInWizardStep step) {
        if (wizardStatus != null) {
            for (MoveInWizardStepStatusTO stepStatus : wizardStatus.steps()) {
                if (step.equals(stepStatus.step().getValue())) {
                    return stepStatus.complete().getValue();
                }
            }
        }
        return false;
    }

    public static boolean isPartiallyComplete() {
        if (wizardStatus != null) {
            for (MoveInWizardStepStatusTO stepStatus : wizardStatus.steps()) {
                if (stepStatus.complete().getValue()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isComplete() {
        if (wizardStatus != null) {
            for (MoveInWizardStepStatusTO stepStatus : wizardStatus.steps()) {
                if (!stepStatus.complete().getValue()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean isStepIncluded(MoveInWizardStep step) {
        if (wizardStatus != null) {
            for (MoveInWizardStepStatusTO stepStatus : wizardStatus.steps()) {
                if (step.equals(stepStatus.step().getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void skipStep(MoveInWizardStep step, AsyncCallback<VoidSerializable> callback) {
        GWT.<MoveInWizardService> create(MoveInWizardService.class).skipStep(callback, step);
    }

    private static void nextStep() {
        if (currentStep != null && !isStepComplete(currentStep)) {
            return;
        }
        for (MoveInWizardStepStatusTO step : wizardStatus.steps()) {
            if (!isStepComplete(step.step().getValue())) {
                currentStep = step.step().getValue();
                return;
            }
        }
    }

    public static MoveInWizardState getMoveInWizardState() {
        return moveInWizardState;
    }

    public static MoveInWizardStep getCurrentStep() {
        return currentStep;
    }

    public static void setCurrentStep(MoveInWizardStep step) {
        System.out.println("+++++++++++" + step);
        if (step != null && isStepIncluded(step) && !isStepComplete(step)) {
            currentStep = step;
        } else {
            nextStep();
        }
    }

    public static void reset() {
        if (handlerRegistration != null) {
            handlerRegistration.removeHandler();
            handlerRegistration = null;
            moveInWizardState = MoveInWizardState.confirmation;
        }
        wizardStatus = null;
        moveInWizardState = null;
        currentStep = null;
    }

}
