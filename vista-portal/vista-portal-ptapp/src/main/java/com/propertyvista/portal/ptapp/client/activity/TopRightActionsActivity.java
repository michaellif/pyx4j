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
package com.propertyvista.portal.ptapp.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.SecurityControllerEvent;
import com.pyx4j.security.client.SecurityControllerHandler;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.client.ClentNavigUtils;
import com.propertyvista.portal.ptapp.client.ui.TopRightActionsView;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.PtAppViewFactory;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.portal.rpc.ptapp.services.PtAuthenticationService;
import com.propertyvista.shared.CompiledLocale;

public class TopRightActionsActivity extends AbstractActivity implements TopRightActionsView.Presenter {

    private final TopRightActionsView view;

    public TopRightActionsActivity(Place place) {
        view = PtAppViewFactory.instance(TopRightActionsView.class);
        assert (view != null);
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
        obtainAvailableLocales();
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

    private void obtainAvailableLocales() {
        view.setAvailableLocales(ClentNavigUtils.obtainAvailableLocales());
    }

    @Override
    public void setLocale(CompiledLocale locale) {
        UrlBuilder builder = Window.Location.createUrlBuilder().setParameter("locale", locale.name());
        Window.Location.replace(builder.buildString());
    }

    @Override
    public void logout() {
        ClientContext.logout((AuthenticationService) GWT.create(PtAuthenticationService.class), null);
    }

    @Override
    public void login() {
        AppSite.getPlaceController().goTo(new PtSiteMap.Login());
    }

    @Override
    public void changePassword() {
        AppSite.getPlaceController().goTo(new PtSiteMap.PasswordChange());
    }

}
