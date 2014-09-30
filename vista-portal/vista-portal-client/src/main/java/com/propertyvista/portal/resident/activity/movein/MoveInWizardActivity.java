/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-11
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.resident.activity.movein;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.events.MoveInWizardStateChangeEvent;
import com.propertyvista.portal.resident.events.MoveInWizardStateChangeHandler;
import com.propertyvista.portal.resident.ui.movein.MoveInWizardView;
import com.propertyvista.portal.resident.ui.movein.MoveInWizardView.MoveInWizardPresenter;
import com.propertyvista.portal.shared.activity.SecurityAwareActivity;

public class MoveInWizardActivity extends SecurityAwareActivity implements MoveInWizardPresenter {

    private final MoveInWizardView view;

    public MoveInWizardActivity(AppPlace place) {
        this.view = ResidentPortalSite.getViewFactory().getView(MoveInWizardView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);

        panel.setWidget(view);

        eventBus.addHandler(MoveInWizardStateChangeEvent.getType(), new MoveInWizardStateChangeHandler() {

            @Override
            public void onStateChange(MoveInWizardStateChangeEvent event) {
                if (!MoveInWizardManager.isAttemptStarted()) {
                    if (SecurityController.check(PortalResidentBehavior.Resident)) {
                        view.showTenantWelcomeScreen();
                    } else if (SecurityController.check(PortalResidentBehavior.Guarantor)) {
                        view.showGuarantorWelcomeScreen();
                    }
                } else if (MoveInWizardManager.isCompletionConfirmationTurn()) {
                    view.showCompletionConfirmationScreen();
                } else {
                    view.showStepPreview(MoveInWizardManager.getCurrentStep());
                }
            }
        });

    }

}
