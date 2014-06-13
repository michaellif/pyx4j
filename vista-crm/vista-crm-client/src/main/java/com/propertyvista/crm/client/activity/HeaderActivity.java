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
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;

import com.pyx4j.config.shared.ApplicationBackend;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
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

import com.propertyvista.common.client.ClientLocaleUtils;
import com.propertyvista.common.client.config.VistaFeaturesCustomizationClient;
import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.login.GetSatisfaction;
import com.propertyvista.crm.client.ui.HeaderView;
import com.propertyvista.crm.client.ui.HeaderView.HeaderPresenter;
import com.propertyvista.crm.client.ui.crud.communication.CommunicationView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.MessageCrudService;
import com.propertyvista.domain.communication.MessageCategory.MessageGroupCategory;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.dto.MessageDTO;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.shared.config.VistaDemo;
import com.propertyvista.shared.i18n.CompiledLocale;

public class HeaderActivity extends AbstractActivity implements HeaderPresenter {

    private final HeaderView view;

    private MessageCrudService communicationService;

    private final Place place;

    public HeaderActivity(Place place) {
        this.place = place;
        view = CrmSite.getViewFactory().getView(HeaderView.class);
        view.setPresenter(this);
        if (ApplicationMode.isDevelopment() && VistaTODO.COMMUNICATION_FUNCTIONALITY_ENABLED) {
            communicationService = (MessageCrudService) GWT.create(MessageCrudService.class);
        }
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

        obtainAvailableLocales();
    }

    private void updateAuthenticatedView() {
        if (ClientContext.isAuthenticated()) {
            view.onLogedIn(ClientContext.getUserVisit().getName());
            if (ApplicationMode.isDevelopment() && VistaTODO.COMMUNICATION_FUNCTIONALITY_ENABLED && SecurityController.checkBehavior(VistaBasicBehavior.CRM)) {
                fetchUnreadMessages(null);
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
        view.setAvailableLocales(ClientLocaleUtils.obtainAvailableLocales());
    }

    @Override
    public void setLocale(CompiledLocale locale) {
        ClientLocaleUtils.changeApplicationLocale(locale);
    }

    @Override
    public void showAccount() {
        AppSite.getPlaceController().goTo(new CrmSiteMap.Account.AccountData());
    }

    @Override
    public void showProperties() {
        AppSite.getPlaceController().goTo(new CrmSiteMap.Dashboard.Manage());
    }

    @Override
    public void showMessages(UIObject relativePosition) {
        if (ApplicationMode.isDevelopment() && VistaTODO.COMMUNICATION_FUNCTIONALITY_ENABLED && SecurityController.checkBehavior(VistaBasicBehavior.CRM)) {
            final CommunicationView cview = CrmSite.getViewFactory().getView(CommunicationView.class);
            PopupPanel popupPanel = new PopupPanel(true, true);
            popupPanel.setWidget(cview);
            popupPanel.showRelativeTo(relativePosition);

            fetchUnreadMessages(cview);
        }
    }

    private void fetchUnreadMessages(final CommunicationView cview) {
        final EntityListCriteria<MessageDTO> criteria = EntityListCriteria.create(MessageDTO.class);
        criteria.eq(criteria.proto().thread().content().$().recipients().$().isRead(), false);
        criteria.eq(criteria.proto().thread().content().$().recipients().$().recipient(), ClientContext.getUserVisit().getPrincipalPrimaryKey());
        criteria.eq(criteria.proto().thread().topic().category(), MessageGroupCategory.Custom);
        criteria.setPageSize(50);
        criteria.setPageNumber(0);

        communicationService.list(new AsyncCallback<EntitySearchResult<MessageDTO>>() {
            @Override
            public void onSuccess(EntitySearchResult<MessageDTO> result) {
                if (cview != null) {
                    cview.populate(result == null || result.getData() == null ? null : result.getData());
                }
            }

            @Override
            public void onFailure(Throwable caught) {
            }
        }, criteria);
    }

    @Override
    public void back2CrmView() {
        AppSite.getPlaceController().goTo(CrmSite.getSystemDashboardPlace());
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
