/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 28, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.common.client.ClientLocaleUtils;
import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.login.GetSatisfaction;
import com.propertyvista.crm.client.event.BoardUpdateEvent;
import com.propertyvista.crm.client.ui.NavigView;
import com.propertyvista.crm.client.ui.NavigView.NavigPresenter;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.MessageCategoryCrudService;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataCrudService;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.shared.i18n.CompiledLocale;

public class NavigActivity extends AbstractActivity implements NavigPresenter {

    private final NavigView view;

    private DashboardMetadataCrudService dashboardMetadataCrudService;

    private MessageCategoryCrudService messageCategoryCrudService;

    private boolean isDashboardFolderUpdateRequired;

    private boolean isCommunicationFolderUpdateRequired;

    private Key previousUserPk;

    private Place place;

    public NavigActivity() {
        view = CrmSite.getViewFactory().getView(NavigView.class);
        view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        obtainAvailableLocales();
    }

    public void withPlace(Place place) {
        this.place = place;
        Key currentUserPk = ClientContext.getUserVisit() != null ? ClientContext.getUserVisit().getPrincipalPrimaryKey() : null;
        isCommunicationFolderUpdateRequired = isDashboardFolderUpdateRequired = previousUserPk == null || currentUserPk == null
                || !previousUserPk.equals(currentUserPk);
        previousUserPk = currentUserPk;

        dashboardMetadataCrudService = GWT.<DashboardMetadataCrudService> create(DashboardMetadataCrudService.class);
        messageCategoryCrudService = GWT.<MessageCategoryCrudService> create(MessageCategoryCrudService.class);
        view.updateUserName(ClientContext.getUserVisit().getName());
        updateDashboardItems();
        updateMessageCategoryItems();

        if (place instanceof AppPlace) {
            view.select((AppPlace) place);
        }
    }

    @Override
    public void getSatisfaction() {
        GetSatisfaction.open();
    };

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
        previousUserPk = null;
    }

    public void onBoardUpdate(BoardUpdateEvent event) {
        if (DashboardMetadata.class.equals(event.getTargetEntityType())) {
            isDashboardFolderUpdateRequired = true;
            updateDashboardItems();
        } else if (MessageCategory.class.equals(event.getTargetEntityType())) {
            isCommunicationFolderUpdateRequired = true;
            updateMessageCategoryItems();
        }
    }

    private void updateDashboardItems() {
        if (isDashboardFolderUpdateRequired) {
            dashboardMetadataCrudService.list(new DefaultAsyncCallback<EntitySearchResult<DashboardMetadata>>() {

                @Override
                public void onSuccess(EntitySearchResult<DashboardMetadata> result) {
                    view.updateDashboards(result.getData());
                    isDashboardFolderUpdateRequired = false;
                }

            }, EntityListCriteria.create(DashboardMetadata.class));
        }

    }

    private void updateMessageCategoryItems() {
        if (isCommunicationFolderUpdateRequired) {
            messageCategoryCrudService.list(new DefaultAsyncCallback<EntitySearchResult<MessageCategory>>() {

                @Override
                public void onSuccess(EntitySearchResult<MessageCategory> result) {
                    view.updateCommunicationGroups(result.getData());
                    isCommunicationFolderUpdateRequired = false;
                }

            }, EntityListCriteria.create(MessageCategory.class));
        }

    }

    @Override
    public boolean isAdminPlace() {
        return place.getClass().getName().contains(CrmSiteMap.Administration.class.getName());
    }

    private void obtainAvailableLocales() {
        view.setAvailableLocales(ClientLocaleUtils.obtainAvailableLocales());
    }

    @Override
    public void setLocale(CompiledLocale locale) {
        ClientLocaleUtils.changeApplicationLocale(locale);
    }

}
