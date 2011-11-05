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

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.activity.crud.ViewerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;

import com.propertyvista.crm.client.ui.crud.building.lockers.LockerAreaViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.BuildingViewFactory;
import com.propertyvista.crm.rpc.services.LockerAreaCrudService;
import com.propertyvista.crm.rpc.services.LockerCrudService;
import com.propertyvista.domain.property.asset.Locker;
import com.propertyvista.dto.LockerAreaDTO;

public class LockerAreaViewerActivity extends ViewerActivityBase<LockerAreaDTO> implements LockerAreaViewerView.Presenter {

    private final IListerView.Presenter lockerLister;

    @SuppressWarnings("unchecked")
    public LockerAreaViewerActivity(Place place) {
        super((LockerAreaViewerView) BuildingViewFactory.instance(LockerAreaViewerView.class), (AbstractCrudService<LockerAreaDTO>) GWT
                .create(LockerAreaCrudService.class));

        lockerLister = new ListerActivityBase<Locker>(((LockerAreaViewerView) view).getLockerView(),
                (AbstractCrudService<Locker>) GWT.create(LockerCrudService.class), Locker.class);

        setPlace(place);
        lockerLister.setPlace(place);
    }

    @Override
    public IListerView.Presenter getLockerPresenter() {
        return lockerLister;
    }

    @Override
    public void onPopulateSuccess(LockerAreaDTO result) {
        super.onPopulateSuccess(result);

        lockerLister.setParentFiltering(result.getPrimaryKey());
        lockerLister.populate();
    }

    @Override
    public void onStop() {
        ((AbstractActivity) lockerLister).onStop();
        super.onStop();
    }
}
