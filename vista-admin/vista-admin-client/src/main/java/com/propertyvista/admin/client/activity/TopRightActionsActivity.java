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
package com.propertyvista.admin.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.SecurityControllerEvent;
import com.pyx4j.security.client.SecurityControllerHandler;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.style.StyleManger;

import com.propertyvista.admin.client.themes.BlueColdPalette;
import com.propertyvista.admin.client.themes.BownWarmPalette;
import com.propertyvista.admin.client.themes.VillageGreenPalette;
import com.propertyvista.admin.client.ui.TopRightActionsView;
import com.propertyvista.admin.client.ui.TopRightActionsViewImpl.Theme;
import com.propertyvista.admin.client.viewfactories.AdminVeiwFactory;
import com.propertyvista.admin.rpc.AdminSiteMap;
import com.propertyvista.common.client.theme.VistaPalette;
import com.propertyvista.common.client.theme.VistaTheme;

public class TopRightActionsActivity extends AbstractActivity implements TopRightActionsView.Presenter {

    private final TopRightActionsView view;

    public TopRightActionsActivity(Place place) {
        view = (TopRightActionsView) AdminVeiwFactory.instance(TopRightActionsView.class);
        assert (view != null);
        view.setPresenter(this);
        withPlace(place);
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
        AppSite.getPlaceController().goTo(new AdminSiteMap.Login());
    }

    @Override
    public void setTheme(Theme theme) {
        switch (theme) {
        case Gainsboro:
            StyleManger.installTheme(new VistaTheme(), new VistaPalette());
            break;
        case VillageGreen:
            StyleManger.installTheme(new VistaTheme(), new VillageGreenPalette());
            break;
        case BlueCold:
            StyleManger.installTheme(new VistaTheme(), new BlueColdPalette());
            break;
        case BrownWarm:
            StyleManger.installTheme(new VistaTheme(), new BownWarmPalette());
            break;
        }
    }

    public TopRightActionsActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void showAccount() {
        AppSite.getPlaceController().goTo(new AdminSiteMap.Account());
    }

    @Override
    public void showAlerts() {
        AppSite.getPlaceController().goTo(new AdminSiteMap.Alert());
    }

    @Override
    public void showMessages() {
        AppSite.getPlaceController().goTo(new AdminSiteMap.Message());
    }

    @Override
    public void showSettings() {
        AppSite.getPlaceController().goTo(new AdminSiteMap.Settings());
    }
}
