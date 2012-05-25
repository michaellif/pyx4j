/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 9, 2011
 * @author dad
 * @version $Id$
 */
package com.propertyvista.portal.client.activity.maintenance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.maintenance.EditMaintenanceRequestView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.TenantMaintenanceService;

public class EditMaintenanceRequestActivity extends SecurityAwareActivity implements EditMaintenanceRequestView.Presenter {

    private final EditMaintenanceRequestView view;

    private final TenantMaintenanceService srv;

    private Key entityId;

    public EditMaintenanceRequestActivity(Place place) {
        this.view = PortalViewFactory.instance(EditMaintenanceRequestView.class);
        this.view.setPresenter(this);
        srv = GWT.create(TenantMaintenanceService.class);

        String val;
        assert (place instanceof AppPlace);
        if ((val = ((AppPlace) place).getFirstArg(PortalSiteMap.ARG_ENTITY_ID)) != null) {
            entityId = new Key(val);
        }

        assert (entityId != null);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        srv.retrieve(new DefaultAsyncCallback<MaintenanceRequestDTO>() {
            @Override
            public void onSuccess(MaintenanceRequestDTO result) {
                view.populate(result);
            }
        }, entityId, AbstractCrudService.RetrieveTraget.Edit);

    }

    @Override
    public void save(MaintenanceRequestDTO entity) {
        srv.create(new DefaultAsyncCallback<MaintenanceRequestDTO>() {
            @Override
            public void onSuccess(MaintenanceRequestDTO result) {
                AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.Maintenance());
            }
        }, entity);
    }

    @Override
    public void cancel() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.Maintenance());
    }
}
