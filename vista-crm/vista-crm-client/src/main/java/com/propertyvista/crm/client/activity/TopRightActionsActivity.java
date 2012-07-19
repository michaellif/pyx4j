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
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.config.shared.ApplicationBackend;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.ContextChangeEvent;
import com.pyx4j.security.client.ContextChangeHandler;
import com.pyx4j.security.client.SecurityControllerEvent;
import com.pyx4j.security.client.SecurityControllerHandler;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.client.ClentNavigUtils;
import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.login.GetSatisfaction;
import com.propertyvista.crm.client.ui.TopRightActionsView;
import com.propertyvista.crm.client.ui.viewfactories.CrmVeiwFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.shared.CompiledLocale;
import com.propertyvista.shared.config.VistaDemo;

public class TopRightActionsActivity extends AbstractActivity implements TopRightActionsView.Presenter {

    private static final I18n i18n = I18n.get(TopRightActionsActivity.class);

    private final TopRightActionsView view;

    public TopRightActionsActivity(Place place) {
        view = CrmVeiwFactory.instance(TopRightActionsView.class);
        assert (view != null);
        view.setPresenter(this);
        withPlace(place);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        view.setDisplayThisIsDemoWarning(VistaDemo.isDemo());
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

        obtainAvailableLocales();
    }

    private void updateAuthenticatedView() {
        if (ClientContext.isAuthenticated()) {
            view.onLogedIn(ClientContext.getUserVisit().getName());
            view.setDisplayThisIsProductionWarning(ApplicationBackend.isProductionBackend());
        } else {
            view.onLogedOut();
        }
    }

    @Override
    public void logout() {
        AppSite.getPlaceController().goTo(new CrmSiteMap.SigningOut());
    }

    @Override
    public void login() {
        AppSite.getPlaceController().goTo(new CrmSiteMap.Login());
    }

    private void obtainAvailableLocales() {
        view.setAvailableLocales(ClentNavigUtils.obtainAvailableLocales());
    }

    @Override
    public void setLocale(CompiledLocale locale) {
        UrlBuilder builder = Window.Location.createUrlBuilder().setParameter("locale", locale.name());
        Window.Location.replace(builder.buildString());
    }

    public TopRightActionsActivity withPlace(Place place) {
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
        AppSite.getPlaceController().goTo(new CrmSiteMap.Settings.ProductDictionary());
    }

    @Override
    public void back2CrmView() {
        AppSite.getPlaceController().goTo(CrmSite.getSystemDashboardPlace());
    }

    @Override
    public void SwitchCrmAndSettings() {
        if (isSettingsPlace()) {
            back2CrmView();
        } else {
            showSettings();
        }
    }

    @Override
    public boolean isSettingsPlace() {
        return (AppSite.getPlaceController().getWhere().getClass().getName().contains(CrmSiteMap.Settings.class.getName()));
    }

    @Override
    public void getSatisfaction() {
        GetSatisfaction.open();
    };

}
