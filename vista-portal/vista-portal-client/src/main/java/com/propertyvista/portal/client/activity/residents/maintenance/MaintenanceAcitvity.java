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
 * @version $Id$
 */
package com.propertyvista.portal.client.activity.residents.maintenance;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.maintenance.MaintenanceView;
import com.propertyvista.portal.client.ui.viewfactories.ResidentsViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.resident.MaintenanceService;

public class MaintenanceAcitvity extends SecurityAwareActivity implements MaintenanceView.Presenter {

    private final MaintenanceView view;

    private final MaintenanceService srv;

    public MaintenanceAcitvity(Place place) {
        this.view = ResidentsViewFactory.instance(MaintenanceView.class);
        this.view.setPresenter(this);
        srv = GWT.create(MaintenanceService.class);
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        srv.listOpenIssues(new DefaultAsyncCallback<Vector<MaintenanceRequestDTO>>() {
            @Override
            public void onSuccess(Vector<MaintenanceRequestDTO> result) {
                view.populateOpenRequests(result);
            }
        });

        srv.listClosedIssues(new DefaultAsyncCallback<Vector<MaintenanceRequestDTO>>() {
            @Override
            public void onSuccess(Vector<MaintenanceRequestDTO> result) {
                view.populateClosedRequests(result);
            }
        });

    }

    @Override
    public void createNewRequest() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Maintenance.NewMaintenanceRequest());
    }

    @Override
    public void viewRequest(MaintenanceRequestDTO requests) {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Maintenance.ViewMaintenanceRequest().formPlace(requests.id().getValue()));
    }

    @Override
    public void cancelRequest(Key requestId) {
        srv.cancelMaintenanceRequest(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                srv.listOpenIssues(new DefaultAsyncCallback<Vector<MaintenanceRequestDTO>>() {
                    @Override
                    public void onSuccess(Vector<MaintenanceRequestDTO> result) {
                        view.populateOpenRequests(result);
                    }
                });
                srv.listClosedIssues(new DefaultAsyncCallback<Vector<MaintenanceRequestDTO>>() {
                    @Override
                    public void onSuccess(Vector<MaintenanceRequestDTO> result) {
                        view.populateClosedRequests(result);
                    }
                });
            }
        }, requestId);
    }

    @Override
    public void rateRequest(Key requestId, Integer rate) {
        srv.rateMaintenanceRequest(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
            }
        }, requestId, rate);
    }
}
