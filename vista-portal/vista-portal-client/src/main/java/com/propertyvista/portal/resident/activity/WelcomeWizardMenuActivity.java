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
package com.propertyvista.portal.resident.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.WelcomeWizardMenuView;
import com.propertyvista.portal.resident.ui.WelcomeWizardMenuView.WelcomeWizardMenuPresenter;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;

public class WelcomeWizardMenuActivity extends AbstractActivity implements WelcomeWizardMenuPresenter {

    private final WelcomeWizardMenuView view;

    private final Place place;

    public WelcomeWizardMenuActivity(Place place) {
        this.place = place;
        this.view = ResidentPortalSite.getViewFactory().getView(WelcomeWizardMenuView.class);
        view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        view.setUserName(ClientContext.getUserVisit().getName());
        view.setMenuVisible(place instanceof ResidentPortalSiteMap.MoveIn.MoveInWizard);
        AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));
    }

}
