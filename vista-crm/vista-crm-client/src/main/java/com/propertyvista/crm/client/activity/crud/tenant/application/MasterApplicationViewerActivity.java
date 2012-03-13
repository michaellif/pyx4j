/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.tenant.application;

import java.util.List;
import java.util.Vector;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.tenant.application.MasterApplicationViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.TenantViewFactory;
import com.propertyvista.crm.rpc.dto.MasterApplicationActionDTO;
import com.propertyvista.crm.rpc.services.tenant.TenantInLeaseCrudService;
import com.propertyvista.crm.rpc.services.tenant.application.ApplicationCrudService;
import com.propertyvista.crm.rpc.services.tenant.application.MasterApplicationCrudService;
import com.propertyvista.dto.ApplicationDTO;
import com.propertyvista.dto.ApplicationUserDTO;
import com.propertyvista.dto.MasterApplicationDTO;
import com.propertyvista.dto.TenantInLeaseDTO;

public class MasterApplicationViewerActivity extends CrmViewerActivity<MasterApplicationDTO> implements MasterApplicationViewerView.Presenter {

    private final IListerView.Presenter<ApplicationDTO> applicationLister;

    private final IListerView.Presenter<TenantInLeaseDTO> tenantLister;

    @SuppressWarnings("unchecked")
    public MasterApplicationViewerActivity(Place place) {
        super(place, TenantViewFactory.instance(MasterApplicationViewerView.class), (AbstractCrudService<MasterApplicationDTO>) GWT
                .create(MasterApplicationCrudService.class));

        applicationLister = new ListerActivityBase<ApplicationDTO>(place, ((MasterApplicationViewerView) view).getApplicationsView(),
                (AbstractCrudService<ApplicationDTO>) GWT.create(ApplicationCrudService.class), ApplicationDTO.class);

        tenantLister = new ListerActivityBase<TenantInLeaseDTO>(place, ((MasterApplicationViewerView) view).getTenantsView(),
                (AbstractCrudService<TenantInLeaseDTO>) GWT.create(TenantInLeaseCrudService.class), TenantInLeaseDTO.class);
    }

    @Override
    public void onPopulateSuccess(MasterApplicationDTO result) {
        super.onPopulateSuccess(result);

        applicationLister.setParent(result.getPrimaryKey());
        applicationLister.populate();

        tenantLister.setParent(result.lease().getPrimaryKey());
        tenantLister.populate();
    }

    @Override
    public void onStop() {
        ((AbstractActivity) applicationLister).onStop();
        ((AbstractActivity) tenantLister).onStop();
        super.onStop();
    }

    @Override
    public void action(MasterApplicationActionDTO action) {
        ((MasterApplicationCrudService) service).action(new DefaultAsyncCallback<MasterApplicationDTO>() {
            @Override
            public void onSuccess(MasterApplicationDTO result) {
                view.populate(result);
            }
        }, action);
    }

    @Override
    public void retrieveUsers(final AsyncCallback<List<ApplicationUserDTO>> callback) {
        ((MasterApplicationCrudService) service).retrieveUsers(new DefaultAsyncCallback<Vector<ApplicationUserDTO>>() {
            @Override
            public void onSuccess(Vector<ApplicationUserDTO> result) {
                callback.onSuccess(result);
            }
        }, entityId);
    }

    @Override
    public void inviteUsers(List<ApplicationUserDTO> users) {
        Vector<ApplicationUserDTO> vector = new Vector<ApplicationUserDTO>(users);
        ((MasterApplicationCrudService) service).inviteUsers(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, entityId, vector);
    }
}
