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
package com.propertyvista.portal.client.ptapp.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.portal.client.ptapp.themes.GainsboroTheme;
import com.propertyvista.portal.client.ptapp.themes.LightSkyBlueTheme;
import com.propertyvista.portal.client.ptapp.ui.TopRightActionsView;
import com.propertyvista.portal.client.ptapp.ui.TopRightActionsViewImpl.Theme;
import com.propertyvista.portal.rpc.pt.SiteMap;

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.SecurityControllerEvent;
import com.pyx4j.security.client.SecurityControllerHandler;
import com.pyx4j.site.client.place.AppPlaceListing;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.style.StyleManger;

public class TopRightActionsActivity extends AbstractActivity implements TopRightActionsView.Presenter {

    private final TopRightActionsView view;

    private final PlaceController placeController;

    private final AppPlaceListing appPlaceListing;

    @Inject
    public TopRightActionsActivity(TopRightActionsView view, PlaceController placeController, AppPlaceListing appPlaceListing) {
        this.view = view;
        this.placeController = placeController;
        this.appPlaceListing = appPlaceListing;
        view.setPresenter(this);
    }

    public TopRightActionsActivity withPlace(AppPlace place) {
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

    private void updateAuthenticatedView() {
        if (ClientContext.isAuthenticated()) {
            view.onLogedIn(ClientContext.getUserVisit().getName());
        } else {
            view.onLogedOut();
        }
    }

    @Override
    public PlaceController getPlaceController() {
        return placeController;
    }

    @Override
    public AppPlaceListing getAppPlaceListing() {
        return appPlaceListing;
    }

    @Override
    public void setTheme(Theme theme) {
        switch (theme) {
        case Gainsboro:
            StyleManger.installTheme(new GainsboroTheme());
            break;
        case LightSkyBlue:
            StyleManger.installTheme(new LightSkyBlueTheme());
            break;
        }

    }

    @Override
    public void logout() {
        ClientContext.logout(null);
    }

    @Override
    public void login() {
        placeController.goTo(new SiteMap.Login());
    }

}
