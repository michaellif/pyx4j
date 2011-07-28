/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-16
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.building;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.IListerView.Presenter;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.activity.dashboard.DashboardViewActivity;
import com.propertyvista.crm.client.ui.crud.building.lockers.LockerAreaView;
import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.crm.rpc.services.LockerCrudService;
import com.propertyvista.domain.property.asset.Locker;

public class LockerAreaActivityDelegate implements LockerAreaView.Presenter {

    private final DashboardView.Presenter dashboard;

    private final IListerView.Presenter lockerLister;

    @SuppressWarnings("unchecked")
    public LockerAreaActivityDelegate(LockerAreaView view, Place place) {

        dashboard = new DashboardViewActivity(view.getDashboardView(), place);

        lockerLister = new ListerActivityBase<Locker>(view.getLockerView(), (AbstractCrudService<Locker>) GWT.create(LockerCrudService.class), Locker.class);
    }

    @Override
    public DashboardView.Presenter getDashboardPresenter() {
        return dashboard;
    }

    @Override
    public Presenter getLockerPresenter() {
        return lockerLister;
    }

    public void populate(Key parentID) {

        dashboard.populate();

        lockerLister.setParentFiltering(parentID);
        lockerLister.populateData(0);
    }
}
