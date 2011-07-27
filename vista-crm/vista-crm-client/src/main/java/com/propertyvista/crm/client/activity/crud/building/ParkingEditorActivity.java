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

import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.building.ParkingEditorView;
import com.propertyvista.crm.client.ui.crud.building.ParkingView;
import com.propertyvista.crm.client.ui.crud.viewfactories.BuildingViewFactory;
import com.propertyvista.crm.client.ui.dashboard.DashboardView.Presenter;
import com.propertyvista.crm.rpc.services.ParkingCrudService;
import com.propertyvista.dto.ParkingDTO;

public class ParkingEditorActivity extends EditorActivityBase<ParkingDTO> implements ParkingView.Presenter {

    private final ParkingActivityDelegate delegate;

    @SuppressWarnings("unchecked")
    public ParkingEditorActivity(Place place) {
        super((ParkingEditorView) BuildingViewFactory.instance(ParkingEditorView.class),
                (AbstractCrudService<ParkingDTO>) GWT.create(ParkingCrudService.class), ParkingDTO.class);
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
}
