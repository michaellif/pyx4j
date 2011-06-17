/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-17
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.building;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.crud.ViewerActivityBase;
import com.pyx4j.site.client.ui.crud.IListerView.Presenter;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.building.BuildingView;
import com.propertyvista.crm.client.ui.crud.building.BuildingViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.BuildingViewFactory;
import com.propertyvista.crm.rpc.services.BuildingCrudService;
import com.propertyvista.dto.BuildingDTO;

public class BuildingViewerActivity extends ViewerActivityBase<BuildingDTO> implements BuildingViewerView.Presenter {

    private final BuildingActivityDelegate delegate;

    @SuppressWarnings("unchecked")
    public BuildingViewerActivity(Place place) {
        super((BuildingViewerView) BuildingViewFactory.instance(BuildingViewerView.class), (AbstractCrudService<BuildingDTO>) GWT
                .create(BuildingCrudService.class));
        withPlace(place);

        delegate = new BuildingActivityDelegate((BuildingView) view);
    }

    @Override
    public Presenter getUnitPresenter() {
        return delegate.getUnitPresenter();
    }

    @Override
    public Presenter getElevatorPresenter() {
        return delegate.getElevatorPresenter();
    }

    @Override
    public Presenter getBoilerPresenter() {
        return delegate.getBoilerPresenter();
    }

    @Override
    public Presenter getRoofPresenter() {
        return delegate.getRoofPresenter();
    }

    @Override
    public Presenter getParkingPresenter() {
        return delegate.getParkingPresenter();
    }

    @Override
    public Presenter getLockerAreaDTOPresenter() {
        return delegate.getLockerAreaDTOPresenter();
    }

    @Override
    public void onPopulateSuccess(BuildingDTO result) {
        super.onPopulateSuccess(result);
        delegate.populate(result.getPrimaryKey());
    }
}
