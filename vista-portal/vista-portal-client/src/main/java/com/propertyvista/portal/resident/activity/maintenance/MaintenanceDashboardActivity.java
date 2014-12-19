/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 18, 2011
 * @author vadims
 */
package com.propertyvista.portal.resident.activity.maintenance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.maintenance.MaintenanceDashboardView;
import com.propertyvista.portal.resident.ui.maintenance.MaintenanceDashboardView.MaintenanceDashboardPresenter;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.dto.maintenance.MaintenanceSummaryDTO;
import com.propertyvista.portal.rpc.portal.resident.services.maintenance.MaintenanceRequestCrudService;
import com.propertyvista.portal.shared.activity.SecurityAwareActivity;

public class MaintenanceDashboardActivity extends SecurityAwareActivity implements MaintenanceDashboardPresenter {

    private final MaintenanceDashboardView view;

    private final MaintenanceRequestCrudService maintenanceRequestCrudService = (MaintenanceRequestCrudService) GWT.create(MaintenanceRequestCrudService.class);

    public MaintenanceDashboardActivity(Place place) {
        this.view = ResidentPortalSite.getViewFactory().getView(MaintenanceDashboardView.class);
        this.view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);
        view.setPresenter(this);
        populate();
    }

    private void populate() {
        maintenanceRequestCrudService.retreiveMaintenanceSummary(new DefaultAsyncCallback<MaintenanceSummaryDTO>() {
            @Override
            public void onSuccess(MaintenanceSummaryDTO result) {
                view.populateOpenMaintenanceRequests(result);
            }
        });
    }

    @Override
    public void createMaintenanceRequest() {
        AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.Maintenance.MaintenanceRequestWizard());
    }

    @Override
    public void rateRequest(Key entityKey, Integer rate) {
        maintenanceRequestCrudService.rateMaintenanceRequest(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
            }
        }, entityKey, rate);
    }

}
