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
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.event.BoardUpdateEvent;
import com.propertyvista.crm.client.event.BoardUpdateHandler;
import com.propertyvista.crm.client.ui.NavigView;
import com.propertyvista.crm.client.ui.NavigView.NavigPresenter;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataCrudService;

public class NavigActivity extends AbstractActivity implements NavigPresenter, BoardUpdateHandler {

    private final NavigView view;

    private final DashboardMetadataCrudService dashboardMetadataCrudService;

    private boolean isDashboardFolderUpdateRequired;

    private static Key previousUserPk;

    private final Place place;

    public NavigActivity(Place place) {
        this.place = place;
        Key currentUserPk = ClientContext.getUserVisit() != null ? ClientContext.getUserVisit().getPrincipalPrimaryKey() : null;
        isDashboardFolderUpdateRequired = previousUserPk == null || currentUserPk == null || !previousUserPk.equals(currentUserPk);
        previousUserPk = currentUserPk;

        dashboardMetadataCrudService = GWT.<DashboardMetadataCrudService> create(DashboardMetadataCrudService.class);

        view = CrmSite.getViewFactory().getView(NavigView.class);
        view.setPresenter(this);

    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        eventBus.addHandler(BoardUpdateEvent.getType(), this);
        panel.setWidget(view);
        updateDashboardItems();
        if (place instanceof AppPlace) {
            view.select((AppPlace) place);
        }
    }

    @Override
    public void onBoardUpdate(BoardUpdateEvent event) {
        isDashboardFolderUpdateRequired = true;
        updateDashboardItems();
    }

    private void updateDashboardItems() {
        // TODO Auto-generated method stub

    }
}
