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
package com.propertyvista.portal.client.activity.maintenance;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.maintenance.MaintenanceView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.TenantMaintenanceService;

public class MaintenanceAcitvity extends SecurityAwareActivity implements MaintenanceView.Presenter {

    private final MaintenanceView view;

    private final TenantMaintenanceService srv;

    public MaintenanceAcitvity(Place place) {
        this.view = PortalViewFactory.instance(MaintenanceView.class);
        this.view.setPresenter(this);
        srv = GWT.create(TenantMaintenanceService.class);
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

        srv.listHistoryIssues(new DefaultAsyncCallback<Vector<MaintenanceRequestDTO>>() {
            @Override
            public void onSuccess(Vector<MaintenanceRequestDTO> result) {
                view.populateHistoryRequests(result);
            }
        });

    }

    @Override
    public void createNewRequest() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.Maintenance.NewMaintenanceRequest());
    }

    @Override
    public void editRequest(MaintenanceRequestDTO requests) {
        AppPlace place = new PortalSiteMap.Residents.Maintenance.EditMaintenanceRequest();
        place.placeArg(PortalSiteMap.ARG_ENTITY_ID, requests.id().getValue().toString());
        AppSite.getPlaceController().goTo(place);
    }

    @Override
    public void cancelRequest(MaintenanceRequestDTO request) {
        if (!Window.confirm("You are about to cancel ticket '" + request.description().getStringView() + "'")) {
            return;
        }

        srv.cancelTicket(new DefaultAsyncCallback<Vector<MaintenanceRequestDTO>>() {
            @Override
            public void onSuccess(Vector<MaintenanceRequestDTO> result) {
                view.populateOpenRequests(result);
            }
        }, request);
    }

    @Override
    public void rateRequest(MaintenanceRequestDTO request, Integer rate) {
        srv.rateTicket(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
            }
        }, request, rate);
    }
}
