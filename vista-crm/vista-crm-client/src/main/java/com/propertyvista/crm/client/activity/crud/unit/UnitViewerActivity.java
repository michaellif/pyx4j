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
package com.propertyvista.crm.client.activity.crud.unit;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.activity.crud.ViewerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.IListerView.Presenter;

import com.propertyvista.crm.client.ui.crud.unit.UnitViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.UnitViewFactory;
import com.propertyvista.crm.rpc.services.unit.UnitCrudService;
import com.propertyvista.crm.rpc.services.unit.UnitItemCrudService;
import com.propertyvista.crm.rpc.services.unit.UnitOccupancyCrudService;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancySegment;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.dto.AptUnitDTO;

public class UnitViewerActivity extends ViewerActivityBase<AptUnitDTO> implements UnitViewerView.Presenter {

    private final IListerView.Presenter<?> unitItemsLister;

    private final IListerView.Presenter<?> OccupanciesLister;

    @SuppressWarnings("unchecked")
    public UnitViewerActivity(Place place) {
        super(place, UnitViewFactory.instance(UnitViewerView.class), (AbstractCrudService<AptUnitDTO>) GWT.create(UnitCrudService.class));

        unitItemsLister = new ListerActivityBase<AptUnitItem>(place, ((UnitViewerView) view).getUnitItemsListerView(),
                (AbstractCrudService<AptUnitItem>) GWT.create(UnitItemCrudService.class), AptUnitItem.class);

        OccupanciesLister = new ListerActivityBase<AptUnitOccupancySegment>(place, ((UnitViewerView) view).getOccupanciesListerView(),
                (AbstractCrudService<AptUnitOccupancySegment>) GWT.create(UnitOccupancyCrudService.class), AptUnitOccupancySegment.class);

    }

    @Override
    public Presenter<?> getUnitItemsPresenter() {
        return unitItemsLister;
    }

    @Override
    public Presenter<?> getOccupanciesPresenter() {
        return OccupanciesLister;
    }

    @Override
    public void onPopulateSuccess(AptUnitDTO result) {
        super.onPopulateSuccess(result);

        unitItemsLister.setParent(result.getPrimaryKey());
        unitItemsLister.populate();

        OccupanciesLister.setParent(result.getPrimaryKey());
        OccupanciesLister.populate();
    }

    @Override
    public boolean canEdit() {
        return SecurityController.checkBehavior(VistaCrmBehavior.PropertyManagement);
    }

    @Override
    public void onStop() {
        ((AbstractActivity) unitItemsLister).onStop();
        ((AbstractActivity) OccupanciesLister).onStop();
        super.onStop();
    }
}
