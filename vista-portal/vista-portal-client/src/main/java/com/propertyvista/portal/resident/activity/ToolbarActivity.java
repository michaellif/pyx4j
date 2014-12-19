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
 */
package com.propertyvista.portal.resident.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.client.ClientLocaleUtils;
import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.domain.tenant.CustomerPreferencesPortalHidable;
import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.events.PortalHidableEvent;
import com.propertyvista.portal.resident.events.PortalHidableHandler;
import com.propertyvista.portal.resident.ui.ToolbarView;
import com.propertyvista.portal.resident.ui.ToolbarView.ToolbarPresenter;
import com.propertyvista.portal.resident.ui.utils.PortalHidablePreferenceManager;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.communication.MessageDTO;
import com.propertyvista.portal.rpc.portal.resident.services.MessagePortalCrudService;
import com.propertyvista.portal.rpc.shared.dto.communication.PortalCommunicationSystemNotification;
import com.propertyvista.portal.shared.CommunicationStatusUpdateEvent;
import com.propertyvista.portal.shared.CommunicationStatusUpdateHandler;
import com.propertyvista.portal.shared.activity.PortalClientCommunicationManager;
import com.propertyvista.portal.shared.ui.communication.CommunicationView;
import com.propertyvista.shared.i18n.CompiledLocale;

public class ToolbarActivity extends AbstractActivity implements ToolbarPresenter {

    private final ToolbarView view;

    private final Place place;

    private final MessagePortalCrudService communicationService;

    public ToolbarActivity(Place place) {
        this.place = place;
        this.view = ResidentPortalSite.getViewFactory().getView(ToolbarView.class);
        assert (view != null);
        communicationService = (MessagePortalCrudService) GWT.create(MessagePortalCrudService.class);
        view.setPresenter(this);
        if (place instanceof ResidentPortalSiteMap.Dashboard) {
            view.setGettingStartedVisible(PortalHidablePreferenceManager.isHidden(CustomerPreferencesPortalHidable.Type.GettingStartedGadget));
        } else {
            view.setGettingStartedVisible(false);
        }
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        if (ClientContext.isAuthenticated()) {
            view.onLogedIn(ClientContext.getUserVisit().getName());
            view.setLeasesSelectorEnabled(SecurityController.check(PortalResidentBehavior.HasMultipleLeases));
        } else {
            boolean hideLoginButton = place instanceof PortalSiteMap.Login;
            view.onLogedOut(hideLoginButton);
            view.setLeasesSelectorEnabled(false);
        }
        obtainAvailableLocales();

        eventBus.addHandler(CommunicationStatusUpdateEvent.getType(), new CommunicationStatusUpdateHandler() {

            @Override
            public void onStatusUpdate(CommunicationStatusUpdateEvent event) {
                updateCommunicationMessagesCount(event.getCommunicationSystemNotification());
            }
        });
        eventBus.addHandler(PortalHidableEvent.getType(), new PortalHidableHandler() {

            @Override
            public void onUpdate(PortalHidableEvent event) {
                if (place instanceof ResidentPortalSiteMap.Dashboard
                        && CustomerPreferencesPortalHidable.Type.GettingStartedGadget.equals(event.getPreferenceType())) {
                    view.setGettingStartedVisible(event.getPreferenceValue());
                }
            }
        });
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
        AppSite.getPlaceController().goTo(new PortalSiteMap.Account());
    }

    @Override
    public void showProfile() {
        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Profile());
    }

    private void obtainAvailableLocales() {
        view.setAvailableLocales(ClientLocaleUtils.obtainAvailableLocales());
    }

    private void updateCommunicationMessagesCount(PortalCommunicationSystemNotification communicationStatus) {
        view.updateCommunicationMessagesCount(communicationStatus);
    }

    @Override
    public void setLocale(CompiledLocale locale) {
        ClientLocaleUtils.changeApplicationLocale(locale);
    }

    @Override
    public void showLeases() {
        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.LeaseContextSelection());
    }

    @Override
    public void loadMessages() {
        final CommunicationView cview = ResidentPortalSite.getViewFactory().getView(CommunicationView.class);

        communicationService.listForHeader(new AsyncCallback<EntitySearchResult<MessageDTO>>() {

            @Override
            public void onSuccess(EntitySearchResult<MessageDTO> result) {
                if (cview != null) {
                    cview.populate(result == null || result.getData() == null ? null : result.getData());
                }
                updateCommunicationMessagesCount(PortalClientCommunicationManager.instance().getLatestCommunicationNotification());
            }

            @Override
            public void onFailure(Throwable caught) {
            }
        });
    }

}
