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

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.IListerView.Presenter;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.activity.dashboard.DashboardViewActivity;
import com.propertyvista.crm.client.ui.crud.building.BuildingView;
import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.BoilerCrudService;
import com.propertyvista.crm.rpc.services.ElevatorCrudService;
import com.propertyvista.crm.rpc.services.LockerAreaCrudService;
import com.propertyvista.crm.rpc.services.ParkingCrudService;
import com.propertyvista.crm.rpc.services.RoofCrudService;
import com.propertyvista.crm.rpc.services.UnitCrudService;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BoilerDTO;
import com.propertyvista.dto.ElevatorDTO;
import com.propertyvista.dto.LockerAreaDTO;
import com.propertyvista.dto.ParkingDTO;
import com.propertyvista.dto.RoofDTO;

public class BuildingActivityDelegate implements BuildingView.Presenter {

    private final DashboardView.Presenter dashboard;

    private final IListerView.Presenter unitLister;

    private final IListerView.Presenter elevatorLister;

    private final IListerView.Presenter boilerLister;

    private final IListerView.Presenter roofLister;

    private final IListerView.Presenter parkingLister;

    private final IListerView.Presenter lockerAreaLister;

    @SuppressWarnings("unchecked")
    public BuildingActivityDelegate(BuildingView view) {

        dashboard = new DashboardViewActivity(view.getDashboardView(), new CrmSiteMap.Dashboard.Building());

        unitLister = new ListerActivityBase<AptUnitDTO>(view.getUnitListerView(), (AbstractCrudService<AptUnitDTO>) GWT.create(UnitCrudService.class),
                AptUnitDTO.class);

        elevatorLister = new ListerActivityBase<ElevatorDTO>(view.getElevatorListerView(),
                (AbstractCrudService<ElevatorDTO>) GWT.create(ElevatorCrudService.class), ElevatorDTO.class);

        boilerLister = new ListerActivityBase<BoilerDTO>(view.getBoilerListerView(), (AbstractCrudService<BoilerDTO>) GWT.create(BoilerCrudService.class),
                BoilerDTO.class);

        roofLister = new ListerActivityBase<RoofDTO>(view.getRoofListerView(), (AbstractCrudService<RoofDTO>) GWT.create(RoofCrudService.class), RoofDTO.class);

        parkingLister = new ListerActivityBase<ParkingDTO>(view.getParkingListerView(), (AbstractCrudService<ParkingDTO>) GWT.create(ParkingCrudService.class),
                ParkingDTO.class);

        lockerAreaLister = new ListerActivityBase<LockerAreaDTO>(view.getLockerAreaListerView(),
                (AbstractCrudService<LockerAreaDTO>) GWT.create(LockerAreaCrudService.class), LockerAreaDTO.class);
    }

    @Override
    public DashboardView.Presenter getDashboardPresenter() {
        return dashboard;
    }

    @Override
    public Presenter getUnitPresenter() {
        return unitLister;
    }

    @Override
    public Presenter getElevatorPresenter() {
        return elevatorLister;
    }

    @Override
    public Presenter getBoilerPresenter() {
        return boilerLister;
    }

    @Override
    public Presenter getRoofPresenter() {
        return roofLister;
    }

    @Override
    public Presenter getParkingPresenter() {
        return parkingLister;
    }

    @Override
    public Presenter getLockerAreaDTOPresenter() {
        return lockerAreaLister;
    }

    public void populate(Key parentID) {

        dashboard.populate();

        unitLister.setParentFiltering(parentID);
        unitLister.populateData(0);

        elevatorLister.setParentFiltering(parentID);
        elevatorLister.populateData(0);

        boilerLister.setParentFiltering(parentID);
        boilerLister.populateData(0);

        roofLister.setParentFiltering(parentID);
        roofLister.populateData(0);

        parkingLister.setParentFiltering(parentID);
        parkingLister.populateData(0);

        lockerAreaLister.setParentFiltering(parentID);
        lockerAreaLister.populateData(0);
    }
}
