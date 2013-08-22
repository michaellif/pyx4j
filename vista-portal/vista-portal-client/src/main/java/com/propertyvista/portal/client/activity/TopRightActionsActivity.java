/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-30
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.ContextChangeEvent;
import com.pyx4j.security.client.ContextChangeHandler;
import com.pyx4j.security.client.SecurityControllerEvent;
import com.pyx4j.security.client.SecurityControllerHandler;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.client.ui.TopRightActionsView;
import com.propertyvista.portal.client.ui.viewfactories.ResidentsViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.PortalAuthenticationService;

public class TopRightActionsActivity extends AbstractActivity implements TopRightActionsView.Presenter {

    private final TopRightActionsView view;

    public TopRightActionsActivity(Place place) {
        this.view = ResidentsViewFactory.instance(TopRightActionsView.class);
        assert (view != null);
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

        eventBus.addHandler(ContextChangeEvent.getType(), new ContextChangeHandler() {

            @Override
            public void onContextChange(ContextChangeEvent event) {
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
    public void logout() {
        ClientContext.logout(GWT.<AuthenticationService> create(PortalAuthenticationService.class), new DefaultAsyncCallback<AuthenticationResponse>() {
            @Override
            public void onSuccess(AuthenticationResponse result) {
                AppSite.getPlaceController().goTo(new PortalSiteMap.Login());
            }
        });
    }

    @Override
    public void login() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Login());
    }

    @Override
    public void showAccount() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.ProfileViewer());
    }
}
