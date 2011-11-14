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

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.activity.crud.ViewerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.IListerView.Presenter;

import com.propertyvista.crm.client.ui.crud.tenant.application.MasterApplicationViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.TenantViewFactory;
import com.propertyvista.crm.rpc.services.ApplicationCrudService;
import com.propertyvista.crm.rpc.services.MasterApplicationCrudService;
import com.propertyvista.dto.ApplicationDTO;
import com.propertyvista.dto.MasterApplicationDTO;

public class MasterApplicationViewerActivity extends ViewerActivityBase<MasterApplicationDTO> implements MasterApplicationViewerView.Presenter {

    private final IListerView.Presenter applicationLister;

    @SuppressWarnings("unchecked")
    public MasterApplicationViewerActivity(Place place) {
        super((MasterApplicationViewerView) TenantViewFactory.instance(MasterApplicationViewerView.class), (AbstractCrudService<MasterApplicationDTO>) GWT
                .create(MasterApplicationCrudService.class));

        applicationLister = new ListerActivityBase<ApplicationDTO>(((MasterApplicationViewerView) view).getApplicationsView(),
                (AbstractCrudService<ApplicationDTO>) GWT.create(ApplicationCrudService.class), ApplicationDTO.class);

        setPlace(place);
        applicationLister.setPlace(place);
    }

    @Override
    public Presenter getScreeningPresenter() {
        return applicationLister;
    }

    @Override
    public void onPopulateSuccess(MasterApplicationDTO result) {
        super.onPopulateSuccess(result);

        applicationLister.setParentFiltering(result.getPrimaryKey());
        applicationLister.populate();
    }

    @Override
    public void onStop() {
        ((AbstractActivity) applicationLister).onStop();
        super.onStop();
    }
}
