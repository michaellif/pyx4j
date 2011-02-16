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
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.activity;

import java.util.Arrays;
import java.util.Collection;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.portal.client.ptapp.SiteMap;
import com.propertyvista.portal.client.ptapp.themes.GainsboroTheme;
import com.propertyvista.portal.client.ptapp.themes.LightSkyBlueTheme;
import com.propertyvista.portal.client.ptapp.ui.TopRightActionsView;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.site.client.place.AppPlace;
import com.pyx4j.site.client.place.AppPlaceListing;
import com.pyx4j.widgets.client.style.StyleManger;

public class TopRightActionsActivity extends AbstractActivity implements TopRightActionsView.Presenter {

    public enum Theme {
        gainsboro, lightSkyBlue
    }

    private final TopRightActionsView view;

    private final PlaceController placeController;

    private final AppPlaceListing appPlaceListing;

    private Theme currentTheme;

    @Inject
    public TopRightActionsActivity(TopRightActionsView view, PlaceController placeController, AppPlaceListing appPlaceListing) {
        this.view = view;
        this.placeController = placeController;
        this.appPlaceListing = appPlaceListing;
        this.currentTheme = Theme.gainsboro;
        view.setPresenter(this);
    }

    public TopRightActionsActivity withPlace(AppPlace place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
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
    public Collection<Theme> getThemes() {
        return Arrays.asList(Theme.values());
    }

    @Override
    public Theme getCurrentTheme() {
        return currentTheme;
    }

    @Override
    public void setTheme(Theme theme) {
        currentTheme = theme;
        switch (theme) {
        case gainsboro:
            StyleManger.installTheme(new GainsboroTheme());
            break;
        case lightSkyBlue:
            StyleManger.installTheme(new LightSkyBlueTheme());
            break;
        }

    }

    @Override
    public void logout() {
        ClientContext.logout(new DefaultAsyncCallback<AuthenticationResponse>() {
            @Override
            public void onSuccess(AuthenticationResponse result) {
                //Do nothing
            }
        });
    }

}
