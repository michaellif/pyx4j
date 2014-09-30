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
import com.propertyvista.portal.rpc.portal.resident.dto.movein.MoveInWizardStatusTO;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.MoveInWizardStep;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.MoveInWizardStepStatusTO;
import com.propertyvista.portal.rpc.portal.resident.services.movein.IMoveInPlace;
import com.propertyvista.portal.rpc.portal.resident.services.movein.MoveInWizardService;

public class MoveInWizardManager {

    private static final I18n i18n = I18n.get(MoveInWizardManager.class);

    private static MoveInWizardStatusTO wizardStatus;

    private static MoveInWizardStep currentStep;

    private static boolean attemptStarted;

    private static HandlerRegistration handlerRegistration;

    public static void init() {

        AppSite.getEventBus().addHandler(BehaviorChangeEvent.getType(), new BehaviorChangeHandler() {

            @Override
            public void onBehaviorChange(BehaviorChangeEvent event) {
                if (SecurityController.check(PortalResidentBehavior.MoveInWizardCompletionRequired)) {
                    handlerRegistration = AppSite.getEventBus().addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
                        @Override
                        public void onPlaceChange(final PlaceChangeEvent event) {
                            if (SecurityController.check(PortalResidentBehavior.MoveInWizardCompletionRequired)) {

                                GWT.<MoveInWizardService> create(MoveInWizardService.class).obtainSteps(new AsyncCallback<MoveInWizardStatusTO>() {

                                    @Override
                                    public void onSuccess(MoveInWizardStatusTO result) {
                                        wizardStatus = result;
                                        setCurrentStep(event.getNewPlace() instanceof IMoveInPlace ? currentStep : null);

                                        ClientEventBus.instance.fireEvent(new MoveInWizardStateChangeEvent());
                                        attemptStarted = true;
                                    }

                                    @Override
                                    public void onFailure(Throwable caught) {
                                        throw new Error(i18n.tr("Something went wrong! Try again later."));
                                    }
                                });
                            }
                        }
                    });
                } else {
                    if (handlerRegistration != null) {
                        handlerRegistration.removeHandler();
                        handlerRegistration = null;
                    }
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
                if (step.equals(stepStatus.step().getValue())) {
                    return stepStatus.complete().getValue();
                }
            }
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
        for (MoveInWizardStepStatusTO step : wizardStatus.steps()) {
            if (!isStepComplete(step.step().getValue())) {
                currentStep = step.step().getValue();
                return;
            }
        }
    }

    public static boolean isAttemptStarted() {
        return attemptStarted;
    }

    public static MoveInWizardStep getCurrentStep() {
        return currentStep;
    }

    public static void setCurrentStep(MoveInWizardStep step) {
        if (step != null && isStepIncluded(step) && !isStepComplete(step)) {
            currentStep = step;
        } else {
            nextStep();
        }
    }

    public static boolean isCompletionConfirmationTurn() {
        if (wizardStatus != null) {
            for (MoveInWizardStepStatusTO stepStatus : wizardStatus.steps()) {
                if (!stepStatus.complete().getValue()) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
