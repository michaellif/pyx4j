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
 */
package com.propertyvista.crm.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.BehaviorChangeEvent;
import com.pyx4j.security.client.BehaviorChangeHandler;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.ContextChangeEvent;
import com.pyx4j.security.client.ContextChangeHandler;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ClientLocaleUtils;
import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.login.GetSatisfaction;
import com.propertyvista.crm.client.event.CommunicationStatusUpdateEvent;
import com.propertyvista.crm.client.event.CommunicationStatusUpdateHandler;
import com.propertyvista.crm.client.ui.HeaderView;
import com.propertyvista.crm.client.ui.HeaderView.HeaderPresenter;
import com.propertyvista.crm.client.ui.crud.communication.CommunicationAlertView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.communication.CrmCommunicationSystemNotification;
import com.propertyvista.crm.rpc.services.CommunicationCrudService;
import com.propertyvista.dto.communication.CommunicationThreadDTO;
import com.propertyvista.shared.i18n.CompiledLocale;

public class HeaderActivity extends AbstractActivity implements HeaderPresenter {

    private final HeaderView view;

    private final Place place;

    private final CommunicationCrudService communicationService;

    public HeaderActivity(Place place) {
        this.place = place;
        view = CrmSite.getViewFactory().getView(HeaderView.class);
        view.setPresenter(this);

        communicationService = (CommunicationCrudService) GWT.create(CommunicationCrudService.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
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

        eventBus.addHandler(CommunicationStatusUpdateEvent.getType(), new CommunicationStatusUpdateHandler() {

            @Override
            public void onStatusUpdate(CommunicationStatusUpdateEvent event) {
                updateCommunicationMessagesCount(event.getCommunicationSystemNotification());
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
    public void navigToLanding() {
        AppSite.getPlaceController().goTo(CrmSite.getDefaultPlace());
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

    private void obtainAvailableLocales() {
        view.setAvailableLocales(ClientLocaleUtils.obtainAvailableLocales());
    }

    private void updateCommunicationMessagesCount(CrmCommunicationSystemNotification communicationStatus) {
        view.setCommunicationMessagesCount(communicationStatus);
    }

    @Override
    public void loadMessages() {
        final CommunicationAlertView cview = CrmSite.getViewFactory().getView(CommunicationAlertView.class);

        communicationService.listForHeader(new AsyncCallback<EntitySearchResult<CommunicationThreadDTO>>() {

            @Override
            public void onSuccess(EntitySearchResult<CommunicationThreadDTO> result) {
                if (cview != null) {
                    cview.populate(result == null || result.getData() == null ? null : result.getData());
                }
                updateCommunicationMessagesCount(CrmClientCommunicationManager.instance().getLatestCommunicationNotification());
            }

            @Override
            public void onFailure(Throwable caught) {
            }
        });
    }

    @Override
    public void setLocale(CompiledLocale locale) {
        ClientLocaleUtils.changeApplicationLocale(locale);
    }

    @Override
    public void showUserProfile() {
        AppSite.getPlaceController().goTo(new CrmSiteMap.Account.UserProfile());
    }

    @Override
    public void showUserPreferences() {
        AppSite.getPlaceController().goTo(new CrmSiteMap.Account.AccountPreferences());
    }

    @Override
    public void back2CrmView() {
        AppSite.getPlaceController().goTo(CrmSite.getDefaultPlace());
    }

    @Override
    public boolean isSettingsPlace() {
        return (AppSite.getPlaceController().getWhere().getClass().getName().contains(CrmSiteMap.Administration.class.getName()));
    }

    @Override
    public void getSatisfaction() {
        GetSatisfaction.open();
    };

    @Override
    public boolean isAdminPlace() {
        return place.getClass().getName().contains(CrmSiteMap.Administration.class.getName());
    }
}
