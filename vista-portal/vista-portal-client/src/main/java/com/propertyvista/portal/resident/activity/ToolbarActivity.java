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
package com.propertyvista.portal.resident.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.LayoutChangeRequestEvent;
import com.pyx4j.site.client.ui.layout.LayoutChangeRequestEvent.ChangeType;

import com.propertyvista.common.client.ClientNavigUtils;
import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.ToolbarView;
import com.propertyvista.portal.resident.ui.ToolbarView.ToolbarPresenter;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.shared.i18n.CompiledLocale;

public class ToolbarActivity extends AbstractActivity implements ToolbarPresenter {

    private final ToolbarView view;

    private final Place place;

    public ToolbarActivity(Place place) {
        this.place = place;
        this.view = ResidentPortalSite.getViewFactory().getView(ToolbarView.class);
        assert (view != null);
        view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        if (ClientContext.isAuthenticated()) {
            view.onLogedIn(ClientContext.getUserVisit().getName());
            view.setLeasesSelectorEnabled(SecurityController.checkAnyBehavior(PortalResidentBehavior.HasMultipleLeases));
        } else {
            boolean hideLoginButton = place instanceof PortalSiteMap.Login;
            view.onLogedOut(hideLoginButton);
            view.setLeasesSelectorEnabled(false);
        }
        obtainAvailableLocales();
        AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));
    }

    @Override
    public void logout() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Logout());
    }

    @Override
    public void login() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Login());
    }

    @Override
    public void showAccount() {
        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Account());
    }

    @Override
    public void showProfile() {
        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Profile());
    }

    private void obtainAvailableLocales() {
        view.setAvailableLocales(ClientNavigUtils.obtainAvailableLocales());
    }

    @Override
    public void setLocale(CompiledLocale locale) {
        ClientNavigUtils.changeApplicationLocale(locale);
    }

    @Override
    public void showLeases() {
        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.LeaseContextSelection());
    }
}
