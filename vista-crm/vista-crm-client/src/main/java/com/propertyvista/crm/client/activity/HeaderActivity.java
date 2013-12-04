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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.config.shared.ApplicationBackend;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.BehaviorChangeEvent;
import com.pyx4j.security.client.BehaviorChangeHandler;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.ContextChangeEvent;
import com.pyx4j.security.client.ContextChangeHandler;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ClientNavigUtils;
import com.propertyvista.common.client.config.VistaFeaturesCustomizationClient;
import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.login.GetSatisfaction;
import com.propertyvista.crm.client.ui.HeaderView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.CrmSiteMap.Administration.Financial;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.shared.config.VistaDemo;
import com.propertyvista.shared.i18n.CompiledLocale;

public class HeaderActivity extends AbstractActivity implements HeaderView.Presenter {

    private final HeaderView view;

    public HeaderActivity(Place place) {
        view = CrmSite.getViewFactory().getView(HeaderView.class);
        view.setPresenter(this);
        withPlace(place);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        view.setDisplayThisIsDemoWarning(VistaFeaturesCustomizationClient.enviromentTitleVisible && VistaDemo.isDemo());
        updateAuthenticatedView();
        eventBus.addHandler(BehaviorChangeEvent.getType(), new BehaviorChangeHandler() {
            @Override
            public void onBehaviorChange(BehaviorChangeEvent event) {
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
            if (SecurityController.checkBehavior(VistaCrmBehavior.PropertyVistaSupport)) {
                view.setDisplayThisIsProductionWarning(ApplicationBackend.isProductionBackend());
            }
        } else {
            view.onLogedOut();
        }
    }

    @Override
    public void navigToLanding() {
        AppSite.getPlaceController().goTo(CrmSite.getSystemDashboardPlace());
    }

    @Override
    public void logout() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                ClientContext.logout(new DefaultAsyncCallback<AuthenticationResponse>() {
                    @Override
                    public void onSuccess(AuthenticationResponse result) {
                        AppSite.getPlaceController().goTo(AppPlace.NOWHERE);
                    }
                });
            }
        });
    }

    @Override
    public void login() {
        AppSite.getPlaceController().goTo(AppPlace.NOWHERE);
    }

    private void obtainAvailableLocales() {
        view.setAvailableLocales(ClientNavigUtils.obtainAvailableLocales());
    }

    @Override
    public void setLocale(CompiledLocale locale) {
        ClientNavigUtils.changeApplicationLocale(locale);
    }

    public HeaderActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void showHome() {
        AppSite.getPlaceController().goTo(CrmSite.getSystemDashboardPlace());
    }

    @Override
    public void showAccount() {
        AppSite.getPlaceController().goTo(new CrmSiteMap.Account.AccountData());
    }

    @Override
    public void showMessages() {
        AppSite.getPlaceController().goTo(new CrmSiteMap.Message());
    }

    @Override
    public void showSettings() {
        AppSite.getPlaceController().goTo(new Financial.ARCode());
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
        return (AppSite.getPlaceController().getWhere().getClass().getName().contains(CrmSiteMap.Administration.class.getName()));
    }

    @Override
    public void getSatisfaction() {
        GetSatisfaction.open();
    };

}
