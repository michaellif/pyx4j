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
package com.propertyvista.portal.client.activity.residents.yardimaintenance;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.dto.YardiServiceRequestDTO;
import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.yardimaintenance.YardiMaintenanceView;
import com.propertyvista.portal.client.ui.viewfactories.ResidentsViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.resident.YardiMaintenanceService;

public class YardiMaintenanceActivity extends SecurityAwareActivity implements YardiMaintenanceView.Presenter {

    private final YardiMaintenanceView view;

    private final YardiMaintenanceService srv;

    public YardiMaintenanceActivity(Place place) {
        this.view = ResidentsViewFactory.instance(YardiMaintenanceView.class);
        this.view.setPresenter(this);
        srv = GWT.create(YardiMaintenanceService.class);
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        srv.listOpenIssues(new DefaultAsyncCallback<Vector<YardiServiceRequestDTO>>() {
            @Override
            public void onSuccess(Vector<YardiServiceRequestDTO> result) {
                view.populateOpenRequests(result);
            }
        });

        srv.listHistoryIssues(new DefaultAsyncCallback<Vector<YardiServiceRequestDTO>>() {
            @Override
            public void onSuccess(Vector<YardiServiceRequestDTO> result) {
                view.populateHistoryRequests(result);
            }
        });

    }

    @Override
    public void createNewRequest() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.Maintenance.NewMaintenanceRequest());
    }

    @Override
    public void editRequest(YardiServiceRequestDTO requests) {
        AppPlace place = new PortalSiteMap.Residents.Maintenance.EditMaintenanceRequest();
        AppSite.getPlaceController().goTo(place.formPlace(requests.id().getValue()));
    }

    @Override
    public void cancelRequest(YardiServiceRequestDTO request) {
        srv.cancelTicket(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                srv.listOpenIssues(new DefaultAsyncCallback<Vector<YardiServiceRequestDTO>>() {
                    @Override
                    public void onSuccess(Vector<YardiServiceRequestDTO> result) {
                        view.populateOpenRequests(result);
                    }
                });
                srv.listHistoryIssues(new DefaultAsyncCallback<Vector<YardiServiceRequestDTO>>() {
                    @Override
                    public void onSuccess(Vector<YardiServiceRequestDTO> result) {
                        view.populateHistoryRequests(result);
                    }
                });
            }
        }, request);
    }
}
