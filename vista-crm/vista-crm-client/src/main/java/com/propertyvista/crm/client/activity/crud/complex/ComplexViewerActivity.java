/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.complex;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.activity.crud.ViewerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;

import com.propertyvista.crm.client.activity.dashboard.DashboardViewActivity;
import com.propertyvista.crm.client.ui.crud.complex.ComplexViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.BuildingViewFactory;
import com.propertyvista.crm.rpc.services.building.BuildingCrudService;
import com.propertyvista.crm.rpc.services.building.ComplexCrudService;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.ComplexDTO;

// TODO possible optimization: when fetching buildings for the lister, propagate these results to the dashboard filter

public class ComplexViewerActivity extends ViewerActivityBase<ComplexDTO> implements ComplexViewerView.Presenter {

    private final DashboardViewActivity dashboardViewActivity;

    private final IListerView.Presenter buildingListerActivity;

    @SuppressWarnings("unchecked")
    public ComplexViewerActivity(Place place) {
        super(place, BuildingViewFactory.instance(ComplexViewerView.class), (AbstractCrudService<ComplexDTO>) GWT.create(ComplexCrudService.class));

        dashboardViewActivity = new DashboardViewActivity(getView().getDashboardView());

        buildingListerActivity = new ListerActivityBase<BuildingDTO>(place, getView().getBuildingListerView(),
                (AbstractCrudService<BuildingDTO>) GWT.create(BuildingCrudService.class), BuildingDTO.class);

    }

    private ComplexViewerView getView() {
        return (ComplexViewerView) view;
    }

    @Override
    public void onStop() {
        ((AbstractActivity) buildingListerActivity).onStop();
        ((AbstractActivity) dashboardViewActivity).onStop();
        super.onStop();
    }

    @Override
    protected void onPopulateSuccess(ComplexDTO result) {

        // should be called before call to super, because
        // ComplexViewerViewImpl.populate sets building filtering!  
        dashboardViewActivity.populate(result.dashboard());

        super.onPopulateSuccess(result);

        buildingListerActivity.setParent(result.id().getValue());
        buildingListerActivity.populate();
    }
}
