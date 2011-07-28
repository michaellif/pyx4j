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

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.crud.ViewerActivityBase;
import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.building.parking.ParkingView;
import com.propertyvista.crm.client.ui.crud.building.parking.ParkingViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.BuildingViewFactory;
import com.propertyvista.crm.client.ui.dashboard.DashboardView.Presenter;
import com.propertyvista.crm.rpc.services.ParkingCrudService;
import com.propertyvista.dto.ParkingDTO;

public class ParkingViewerActivity extends ViewerActivityBase<ParkingDTO> implements ParkingView.Presenter {

    private final ParkingActivityDelegate delegate;

    @SuppressWarnings("unchecked")
    public ParkingViewerActivity(Place place) {
        super((ParkingViewerView) BuildingViewFactory.instance(ParkingViewerView.class), (AbstractCrudService<ParkingDTO>) GWT.create(ParkingCrudService.class));
        withPlace(place);

        delegate = new ParkingActivityDelegate((ParkingView) view, place);
    }

    @Override
    public Presenter getDashboardPresenter() {
        return delegate.getDashboardPresenter();
    }

    @Override
    public IListerView.Presenter getSpotPresenter() {
        return delegate.getSpotPresenter();
    }

    @Override
    public void onPopulateSuccess(ParkingDTO result) {
        super.onPopulateSuccess(result);
        delegate.populate(result.getPrimaryKey());
    }
}
