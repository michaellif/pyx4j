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
package com.propertyvista.portal.web.client.activity;

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
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRerquestEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRerquestEvent.ChangeType;

import com.propertyvista.common.client.ClientNavigUtils;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.PortalAuthenticationService;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.ui.ToolbarView;
import com.propertyvista.shared.i18n.CompiledLocale;

public class ToolbarActivity extends AbstractActivity implements ToolbarView.ToolbarPresenter {

    private final ToolbarView view;

    private final Place place;

    public ToolbarActivity(Place place) {
        this.place = place;
        this.view = PortalWebSite.getViewFactory().instantiate(ToolbarView.class);
        assert (view != null);
        view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        updateView();
        eventBus.addHandler(SecurityControllerEvent.getType(), new SecurityControllerHandler() {
            @Override
            public void onSecurityContextChange(SecurityControllerEvent event) {
                updateView();
            }
        });

        eventBus.addHandler(ContextChangeEvent.getType(), new ContextChangeHandler() {

            @Override
            public void onContextChange(ContextChangeEvent event) {
                updateView();
            }
        });

        obtainAvailableLocales();
        AppSite.getEventBus().fireEvent(new LayoutChangeRerquestEvent(ChangeType.resizeComponents));

    }

    private void updateView() {
        if (ClientContext.isAuthenticated()) {
            view.onLogedIn(ClientContext.getUserVisit().getName());
        } else {
            boolean hideLoginButton = place instanceof PortalSiteMap.Login;
            view.onLogedOut(hideLoginButton);
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
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Account());
    }

    @Override
    public void showProfile() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.ProfileViewer());
    }

    private void obtainAvailableLocales() {
        view.setAvailableLocales(ClientNavigUtils.obtainAvailableLocales());
    }

    @Override
    public void setLocale(CompiledLocale locale) {
        ClientNavigUtils.changeApplicationLocale(locale);
    }
}
