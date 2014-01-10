/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.prospect.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRequestEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRequestEvent.ChangeType;

import com.propertyvista.portal.prospect.ProspectPortalSite;
import com.propertyvista.portal.prospect.events.ApplicationWizardStateChangeEvent;
import com.propertyvista.portal.prospect.events.ApplicationWizardStateChangeHandler;
import com.propertyvista.portal.prospect.ui.MenuView;
import com.propertyvista.portal.prospect.ui.MenuView.MenuPresenter;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizard;

public class MenuActivity extends AbstractActivity implements MenuPresenter {

    private final MenuView view;

    private ApplicationWizard applicationWizard;

    public MenuActivity(Place place) {
        this.view = ProspectPortalSite.getViewFactory().getView(MenuView.class);
        view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        view.setUserName(ClientContext.getUserVisit().getName());
        view.updateStepButtons(applicationWizard);

        eventBus.addHandler(ApplicationWizardStateChangeEvent.getType(), new ApplicationWizardStateChangeHandler() {
            @Override
            public void onStateChange(ApplicationWizardStateChangeEvent event) {
                if (event.getChangeType() == ApplicationWizardStateChangeEvent.ChangeType.init
                        || event.getChangeType() == ApplicationWizardStateChangeEvent.ChangeType.discard) {
                    applicationWizard = event.getApplicationWizard();
                    view.updateStepButtons(applicationWizard);
                } else if (event.getChangeType() == ApplicationWizardStateChangeEvent.ChangeType.stepChange) {
                    view.updateStepButtons(applicationWizard);
                }
                AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));
            }
        });

        AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));
    }

}
