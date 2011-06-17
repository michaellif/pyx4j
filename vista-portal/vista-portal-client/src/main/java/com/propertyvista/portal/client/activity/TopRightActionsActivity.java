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
package com.propertyvista.portal.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.SecurityControllerEvent;
import com.pyx4j.security.client.SecurityControllerHandler;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.client.ui.TopRightActionsView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class TopRightActionsActivity extends AbstractActivity implements TopRightActionsView.Presenter {

    private final TopRightActionsView view;

    public TopRightActionsActivity(Place place) {
        this.view = (TopRightActionsView) PortalViewFactory.instance(TopRightActionsView.class);
        view.setPresenter(this);
        withPlace(place);
    }

    public TopRightActionsActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        updateAuthenticatedView();
        eventBus.addHandler(SecurityControllerEvent.getType(), new SecurityControllerHandler() {
            @Override
            public void onSecurityContextChange(SecurityControllerEvent event) {
                updateAuthenticatedView();
            }
        });
    }

    @Override
    public PlaceController getPlaceController() {
        return AppSite.getPlaceController();
    }

    @Override
    public Place getWhere() {
        return AppSite.getPlaceController().getWhere();
    }

    @Override
    public void logout() {
        ClientContext.logout(null);

    }

    @Override
    public void login() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.Login());

    }

    private void updateAuthenticatedView() {
        if (ClientContext.isAuthenticated()) {
            view.onLogedIn(ClientContext.getUserVisit().getName());
        } else {
            view.onLogedOut();
        }
    }

}
