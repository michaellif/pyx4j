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

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.login.GetSatisfaction;
import com.propertyvista.crm.client.event.BoardUpdateEvent;
import com.propertyvista.crm.client.event.BoardUpdateHandler;
import com.propertyvista.crm.client.ui.NavigView;
import com.propertyvista.crm.client.ui.NavigView.NavigPresenter;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataCrudService;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public class NavigActivity extends AbstractActivity implements NavigPresenter, BoardUpdateHandler {

    private final NavigView view;

    private DashboardMetadataCrudService dashboardMetadataCrudService;

    private boolean isDashboardFolderUpdateRequired;

    private static Key previousUserPk;

    private Place place;

    public NavigActivity() {
        view = CrmSite.getViewFactory().getView(NavigView.class);
        view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        eventBus.addHandler(BoardUpdateEvent.getType(), this);
        panel.setWidget(view);
    }

    public void withPlace(Place place) {
        this.place = place;
        Key currentUserPk = ClientContext.getUserVisit() != null ? ClientContext.getUserVisit().getPrincipalPrimaryKey() : null;
        isDashboardFolderUpdateRequired = previousUserPk == null || currentUserPk == null || !previousUserPk.equals(currentUserPk);
        previousUserPk = currentUserPk;

        dashboardMetadataCrudService = GWT.<DashboardMetadataCrudService> create(DashboardMetadataCrudService.class);

        view.updateUserName(ClientContext.getUserVisit().getName());
        updateDashboardItems();

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
    }

    @Override
    public void onBoardUpdate(BoardUpdateEvent event) {
        isDashboardFolderUpdateRequired = true;
        updateDashboardItems();
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

    @Override
    public boolean isAdminPlace() {
        return place.getClass().getName().contains(CrmSiteMap.Administration.class.getName());
    }
}
