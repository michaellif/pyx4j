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
package com.propertyvista.crm.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.SecurityControllerEvent;
import com.pyx4j.security.client.SecurityControllerHandler;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.style.StyleManger;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.themes.BlueColdTheme;
import com.propertyvista.crm.client.themes.BownWarmTheme;
import com.propertyvista.crm.client.themes.GainsboroTheme;
import com.propertyvista.crm.client.themes.VillageGreenTheme;
import com.propertyvista.crm.client.ui.TopRightActionsView;
import com.propertyvista.crm.client.ui.TopRightActionsViewImpl.Theme;
import com.propertyvista.crm.rpc.CrmSiteMap;

public class TopRightActionsActivity extends AbstractActivity implements TopRightActionsView.Presenter {

    private final TopRightActionsView view;

    @Inject
    public TopRightActionsActivity(TopRightActionsView view) {
        this.view = view;
        view.setPresenter(this);
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
    public void logout() {
        ClientContext.logout(null);
    }

    @Override
    public void login() {
        AppSite.getPlaceController().goTo(new CrmSiteMap.Login());
    }

    @Override
    public void setTheme(Theme theme) {
        switch (theme) {
        case Gainsboro:
            StyleManger.installTheme(new GainsboroTheme());
            break;
        case VillageGreen:
            StyleManger.installTheme(new VillageGreenTheme());
            break;
        case BlueCold:
            StyleManger.installTheme(new BlueColdTheme());
            break;
        case BrownWarm:
            StyleManger.installTheme(new BownWarmTheme());
            break;
        }
    }

    public TopRightActionsActivity withPlace(AppPlace place) {
        return this;
    }

    @Override
    public void showAccount() {
        AppSite.getPlaceController().goTo(new CrmSiteMap.Account());
    }

    @Override
    public void showAlerts() {
        AppSite.getPlaceController().goTo(new CrmSiteMap.Alert());
    }

    @Override
    public void showMessages() {
        AppSite.getPlaceController().goTo(new CrmSiteMap.Message());
    }

    @Override
    public void showSettings() {
        AppSite.getPlaceController().goTo(new CrmSiteMap.Settings.Content());
    }

    @Override
    public void back2CrmView() {
        ((CrmSite) CrmSite.instance()).loadCrm();
    }
}
