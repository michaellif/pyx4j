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
package com.propertyvista.portal.client.activity.residents.yardimaintenance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.dto.YardiServiceRequestDTO;
import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.yardimaintenance.EditYardiMaintenanceRequestView;
import com.propertyvista.portal.client.ui.viewfactories.ResidentsViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.services.resident.YardiMaintenanceService;

public class EditYardiMaintenanceRequestActivity extends SecurityAwareActivity implements EditYardiMaintenanceRequestView.Presenter {

    protected final EditYardiMaintenanceRequestView view;

    protected final YardiMaintenanceService srv;

    private final Key entityId;

    public EditYardiMaintenanceRequestActivity(AppPlace place) {
        this.view = ResidentsViewFactory.instance(EditYardiMaintenanceRequestView.class);
        this.view.setPresenter(this);
        srv = GWT.create(YardiMaintenanceService.class);

        entityId = place.getItemId();
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        securityAwareStart(panel, eventBus);
        panel.setWidget(view);

        assert (entityId != null);
        srv.retrieve(new DefaultAsyncCallback<YardiServiceRequestDTO>() {
            @Override
            public void onSuccess(YardiServiceRequestDTO result) {
                view.populate(result);
            }
        }, entityId, AbstractCrudService.RetrieveTraget.Edit);
    }

    protected final void securityAwareStart(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
    }

    @Override
    public void save(YardiServiceRequestDTO entity) {
        srv.create(new DefaultAsyncCallback<Key>() {
            @Override
            public void onSuccess(Key result) {
                AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.YardiMaintenance());
            }
        }, entity);
    }

    @Override
    public void cancel() {
        History.back();
    }
}
