/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-28
 * @author TPRGLET
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.crm.client.activity.crud.unit;

import com.google.gwt.core.client.GWT;
import com.propertyvista.crm.client.ui.crud.unit.UnitView;
import com.propertyvista.crm.rpc.services.UnitItemCrudService;
import com.propertyvista.crm.rpc.services.UnitOccupancyCrudService;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.IListerView.Presenter;
import com.pyx4j.site.rpc.services.AbstractCrudService;

public class UnitActivityDelegate implements UnitView.Presenter {

    private final IListerView.Presenter detailsLister;

    private final IListerView.Presenter OccupanciesLister;

    @SuppressWarnings("unchecked")
    public UnitActivityDelegate(UnitView view) {

        detailsLister = new ListerActivityBase<AptUnitItem>(view.getDetailsListerView(),
                (AbstractCrudService<AptUnitItem>) GWT.create(UnitItemCrudService.class), AptUnitItem.class);

        OccupanciesLister = new ListerActivityBase<AptUnitOccupancy>(view.getOccupanciesListerView(),
                (AbstractCrudService<AptUnitOccupancy>) GWT.create(UnitOccupancyCrudService.class), AptUnitOccupancy.class);
    }

    @Override
    public Presenter getDetailsPresenter() {
        return detailsLister;
    }

    @Override
    public Presenter getOccupanciesPresenter() {
        return OccupanciesLister;
    }

    public void populate(Key parentID) {

        detailsLister.setParentFiltering(parentID);
        detailsLister.populateData(0);

        OccupanciesLister.setParentFiltering(parentID);
        OccupanciesLister.populateData(0);
    }
}
