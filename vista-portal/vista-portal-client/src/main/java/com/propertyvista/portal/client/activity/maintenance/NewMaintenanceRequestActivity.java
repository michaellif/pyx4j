/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.activity.maintenance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.maintenance.NewMaintenanceRequestView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.TenantMaintenanceService;

public class NewMaintenanceRequestActivity extends SecurityAwareActivity implements NewMaintenanceRequestView.Presenter {

    private final NewMaintenanceRequestView view;

    private final TenantMaintenanceService srv;

    public NewMaintenanceRequestActivity(Place place) {
        this.view = PortalViewFactory.instance(NewMaintenanceRequestView.class);
        this.view.setPresenter(this);
        srv = GWT.create(TenantMaintenanceService.class);
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        // create default empty request:
        MaintenanceRequestDTO request = EntityFactory.create(MaintenanceRequestDTO.class);
        request.status().setValue(MaintenanceRequestStatus.Submitted);
        view.populate(request);
    }

    @Override
    public void submit(MaintenanceRequestDTO request) {
        srv.create(new DefaultAsyncCallback<MaintenanceRequestDTO>() {
            @Override
            public void onSuccess(MaintenanceRequestDTO result) {
                AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.Maintenance());
            }
        }, request);
    }

    @Override
    public void cancel() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.Maintenance());
    }
}
