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
package com.propertyvista.portal.web.client.activity.residents.maintenance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.resident.MaintenanceService;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.web.client.ui.residents.maintenance.EditMaintenanceRequestView;

public class EditMaintenanceRequestActivity extends SecurityAwareActivity implements EditMaintenanceRequestView.Presenter {

    protected final EditMaintenanceRequestView view;

    protected final MaintenanceService srv;

    private final Key entityId;

    public EditMaintenanceRequestActivity(AppPlace place) {
        srv = GWT.create(MaintenanceService.class);
        this.view = PortalWebSite.getViewFactory().instantiate(EditMaintenanceRequestView.class);
        this.view.setPresenter(this);

        entityId = place.getItemId();
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        securityAwareStart(panel, eventBus);
        panel.setWidget(view);

        assert (entityId != null);
        srv.retrieve(new DefaultAsyncCallback<MaintenanceRequestDTO>() {
            @Override
            public void onSuccess(MaintenanceRequestDTO result) {
                view.populate(result);
            }
        }, entityId, AbstractCrudService.RetrieveTarget.Edit);
    }

    protected final void securityAwareStart(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
    }

    @Override
    public void save(MaintenanceRequestDTO entity) {
        // don't want all the attached info back over the wire
        MaintenanceRequestCategory newCat = EntityFactory.createIdentityStub(MaintenanceRequestCategory.class, entity.category().getPrimaryKey());
        entity.category().set(newCat);

        srv.create(new DefaultAsyncCallback<Key>() {
            @Override
            public void onSuccess(Key result) {
                AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Maintenance());
            }
        }, entity);
    }

    @Override
    public void cancel() {
        History.back();
    }

    @Override
    public void getCategoryMeta(final AsyncCallback<MaintenanceRequestMetadata> callback) {
        srv.getCategoryMeta(new DefaultAsyncCallback<MaintenanceRequestMetadata>() {
            @Override
            public void onSuccess(MaintenanceRequestMetadata result) {
                callback.onSuccess(result);
            }
        }, false);
    }

}
