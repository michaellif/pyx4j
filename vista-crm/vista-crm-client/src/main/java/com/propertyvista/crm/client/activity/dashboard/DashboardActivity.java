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
package com.propertyvista.crm.client.activity.dashboard;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.DashboardPrinterDialog;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataService;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public class DashboardActivity extends AbstractActivity implements DashboardView.Presenter {

    private final DashboardMetadataService service = GWT.create(DashboardMetadataService.class);

    private final DashboardView view;

    private final Key dashboardId;

    private final AppPlace place;

    public DashboardActivity(AppPlace place) {
        this.place = place;
        view = CrmSite.getViewFactory().getView(DashboardView.class);
        dashboardId = getIdFromPlace(place);
    }

    @Override
    public AppPlace getPlace() {
        return place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        view.setPresenter(this);
        this.populate();
    }

    @Override
    public void populate() {
        service.retrieveMetadata(new DefaultAsyncCallback<DashboardMetadata>() {

            @Override
            public void onSuccess(DashboardMetadata result) {
                view.setDashboardMetadata(result);
                view.setReadOnly(result == null || !ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(result.ownerUser().getPrimaryKey()));
            }

        }, dashboardId);
    }

    @Override
    public void save() {
        DashboardMetadata dm = view.getDashboardMetadata();

        // the if statement is only for sake of defense programming, because a View shouldn't request save() when in ReadOnly mode
        if (ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(dm.ownerUser().getPrimaryKey())) {
            dm.gadgetMetadataList().clear();

            service.saveDashboardMetadata(new DefaultAsyncCallback<DashboardMetadata>() {
                @Override
                public void onSuccess(DashboardMetadata result) {

                }
            }, dm);
        }
    }

    @Override
    public void print() {
        DashboardPrinterDialog.print(dashboardId, view.getSelectedBuildingsStubs());
    }

    @Override
    @Deprecated
    public void refresh() {
        // TODO Auto-generated method stub

    }

    private static Key getIdFromPlace(AppPlace place) {
        String val;
        Key entityId = null;
        if ((val = place.getFirstArg(CrudAppPlace.ARG_NAME_ID)) != null) {
            entityId = new Key(val);
            // Validate argument
            try {
                entityId.asLong();
            } catch (NumberFormatException e) {
                entityId = null;
            }

        }
        return entityId;
    }
}