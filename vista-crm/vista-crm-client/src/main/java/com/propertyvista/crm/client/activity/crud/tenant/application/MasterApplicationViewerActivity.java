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

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.activity.crud.ViewerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;

import com.propertyvista.crm.client.ui.crud.tenant.application.MasterApplicationViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.TenantViewFactory;
import com.propertyvista.crm.rpc.services.ApplicationCrudService;
import com.propertyvista.crm.rpc.services.MasterApplicationCrudService;
import com.propertyvista.crm.rpc.services.TenantInLeaseCrudService;
import com.propertyvista.dto.ApplicationDTO;
import com.propertyvista.dto.MasterApplicationDTO;
import com.propertyvista.dto.TenantInLeaseDTO;

public class MasterApplicationViewerActivity extends ViewerActivityBase<MasterApplicationDTO> implements MasterApplicationViewerView.Presenter {

    private final IListerView.Presenter applicationLister;

    private final IListerView.Presenter tenantLister;

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

        applicationLister.setParentFiltering(result.getPrimaryKey());
        applicationLister.populate();

        tenantLister.setParentFiltering(result.lease().getPrimaryKey());
        tenantLister.populate();
    }

    @Override
    public void onStop() {
        ((AbstractActivity) applicationLister).onStop();
        ((AbstractActivity) tenantLister).onStop();
        super.onStop();
    }

    @Override
    public void approve(MasterApplicationDTO application) {
        save(application);
    }

    @Override
    public void decline(MasterApplicationDTO application) {
        save(application);
    }

    @Override
    public void moreInfo(MasterApplicationDTO application) {
        save(application);
    }

    @Override
    public void cancelApp(MasterApplicationDTO application) {
        service.delete(new AsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                History.back();
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        }, application.getPrimaryKey());
    }

    private void save(MasterApplicationDTO application) {
        service.save(new AsyncCallback<MasterApplicationDTO>() {

            @Override
            public void onSuccess(MasterApplicationDTO result) {
                view.populate(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        }, application);
    }
}
