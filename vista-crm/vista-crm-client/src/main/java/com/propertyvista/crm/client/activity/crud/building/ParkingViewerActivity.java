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
package com.propertyvista.crm.client.activity.crud.building;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.activity.crud.ViewerActivityBase;
import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.activity.dashboard.DashboardViewActivity;
import com.propertyvista.crm.client.ui.crud.building.parking.ParkingViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.BuildingViewFactory;
import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.crm.rpc.services.ParkingCrudService;
import com.propertyvista.crm.rpc.services.ParkingSpotCrudService;
import com.propertyvista.domain.property.asset.ParkingSpot;
import com.propertyvista.dto.ParkingDTO;

public class ParkingViewerActivity extends ViewerActivityBase<ParkingDTO> implements ParkingViewerView.Presenter {

    private final DashboardView.Presenter dashboard;

    private final IListerView.Presenter spotLister;

    @SuppressWarnings("unchecked")
    public ParkingViewerActivity(Place place) {
        super((ParkingViewerView) BuildingViewFactory.instance(ParkingViewerView.class), (AbstractCrudService<ParkingDTO>) GWT.create(ParkingCrudService.class));

        dashboard = new DashboardViewActivity(((ParkingViewerView) view).getDashboardView(), place);

        spotLister = new ListerActivityBase<ParkingSpot>(((ParkingViewerView) view).getSpotView(),
                (AbstractCrudService<ParkingSpot>) GWT.create(ParkingSpotCrudService.class), ParkingSpot.class);

        setPlace(place);
        spotLister.setPlace(place);
    }

    @Override
    public DashboardView.Presenter getDashboardPresenter() {
        return dashboard;
    }

    @Override
    public IListerView.Presenter getSpotPresenter() {
        return spotLister;
    }

    @Override
    public void onPopulateSuccess(ParkingDTO result) {
        super.onPopulateSuccess(result);

        dashboard.populate();

        spotLister.setParentFiltering(result.getPrimaryKey());
        spotLister.populate();
    }

    @Override
    public void onStop() {
        ((AbstractActivity) spotLister).onStop();
        super.onStop();
    }
}
