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
package com.propertyvista.crm.client.ui.crud.building;

import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.crud.floorplan.FloorplanLister;
import com.propertyvista.crm.client.ui.crud.unit.UnitLister;
import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.crm.client.ui.dashboard.DashboardViewImpl;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BoilerDTO;
import com.propertyvista.dto.ElevatorDTO;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.dto.LockerAreaDTO;
import com.propertyvista.dto.ParkingDTO;
import com.propertyvista.dto.RoofDTO;

public class BuildingViewDelegate implements BuildingView {

    private final DashboardView dashboardView;

    private final IListerView<FloorplanDTO> floorplanLister;

    private final IListerView<AptUnitDTO> unitLister;

    private final IListerView<ElevatorDTO> elevatorLister;

    private final IListerView<BoilerDTO> boilerLister;

    private final IListerView<RoofDTO> roofLister;

    private final IListerView<ParkingDTO> parkingLister;

    private final IListerView<LockerAreaDTO> lockerAreaLister;

    public BuildingViewDelegate(boolean readOnly) {
        dashboardView = new DashboardViewImpl("");
        floorplanLister = new ListerInternalViewImplBase<FloorplanDTO>(new FloorplanLister(/* readOnly */));
        unitLister = new ListerInternalViewImplBase<AptUnitDTO>(new UnitLister(/* readOnly */));
        elevatorLister = new ListerInternalViewImplBase<ElevatorDTO>(new ElevatorLister(/* readOnly */));
        boilerLister = new ListerInternalViewImplBase<BoilerDTO>(new BoilerLister(/* readOnly */));
        roofLister = new ListerInternalViewImplBase<RoofDTO>(new RoofLister(/* readOnly */));
        parkingLister = new ListerInternalViewImplBase<ParkingDTO>(new ParkingLister(/* readOnly */));
        lockerAreaLister = new ListerInternalViewImplBase<LockerAreaDTO>(new LockerAreaLister(/* readOnly */));
    }

    @Override
    public DashboardView getDashboardView() {
        return dashboardView;
    }

    @Override
    public IListerView<FloorplanDTO> getFloorplanListerView() {
        return floorplanLister;
    }

    @Override
    public IListerView<AptUnitDTO> getUnitListerView() {
        return unitLister;
    }

    @Override
    public IListerView<ElevatorDTO> getElevatorListerView() {
        return elevatorLister;
    }

    @Override
    public IListerView<BoilerDTO> getBoilerListerView() {
        return boilerLister;
    }

    @Override
    public IListerView<RoofDTO> getRoofListerView() {
        return roofLister;
    }

    @Override
    public IListerView<ParkingDTO> getParkingListerView() {
        return parkingLister;
    }

    @Override
    public IListerView<LockerAreaDTO> getLockerAreaListerView() {
        return lockerAreaLister;
    }
}
