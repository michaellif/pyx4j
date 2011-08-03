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

import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.IListerView.Presenter;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.activity.dashboard.DashboardViewActivity;
import com.propertyvista.crm.client.ui.crud.building.BuildingView;
import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.BoilerCrudService;
import com.propertyvista.crm.rpc.services.ConcessionCrudService;
import com.propertyvista.crm.rpc.services.ElevatorCrudService;
import com.propertyvista.crm.rpc.services.FeatureCrudService;
import com.propertyvista.crm.rpc.services.FloorplanCrudService;
import com.propertyvista.crm.rpc.services.LockerAreaCrudService;
import com.propertyvista.crm.rpc.services.ParkingCrudService;
import com.propertyvista.crm.rpc.services.RoofCrudService;
import com.propertyvista.crm.rpc.services.ServiceCrudService;
import com.propertyvista.crm.rpc.services.UnitCrudService;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BoilerDTO;
import com.propertyvista.dto.ElevatorDTO;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.dto.LockerAreaDTO;
import com.propertyvista.dto.ParkingDTO;
import com.propertyvista.dto.RoofDTO;

public class BuildingActivityDelegate implements BuildingView.Presenter {

    private final DashboardView.Presenter dashboard;

    private final IListerView.Presenter floorplanLister;

    private final IListerView.Presenter unitLister;

    private final IListerView.Presenter elevatorLister;

    private final IListerView.Presenter boilerLister;

    private final IListerView.Presenter roofLister;

    private final IListerView.Presenter parkingLister;

    private final IListerView.Presenter lockerAreaLister;

    private final IListerView.Presenter serviceLister;

    private final IListerView.Presenter featureLister;

    private final IListerView.Presenter concessionLister;

    @SuppressWarnings("unchecked")
    public BuildingActivityDelegate(BuildingView view) {

        dashboard = new DashboardViewActivity(view.getDashboardView(), new CrmSiteMap.Dashboard.Building());

        floorplanLister = new ListerActivityBase<FloorplanDTO>(view.getFloorplanListerView(),
                (AbstractCrudService<FloorplanDTO>) GWT.create(FloorplanCrudService.class), FloorplanDTO.class);

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

        serviceLister = new ListerActivityBase<Service>(view.getServiceListerView(), (AbstractCrudService<Service>) GWT.create(ServiceCrudService.class),
                Service.class);
        featureLister = new ListerActivityBase<Feature>(view.getFeatureListerView(), (AbstractCrudService<Feature>) GWT.create(FeatureCrudService.class),
                Feature.class);
        concessionLister = new ListerActivityBase<Concession>(view.getConcessionListerView(),
                (AbstractCrudService<Concession>) GWT.create(ConcessionCrudService.class), Concession.class);

    }

    @Override
    public DashboardView.Presenter getDashboardPresenter() {
        return dashboard;
    }

    @Override
    public Presenter getFloorplanPresenter() {
        return floorplanLister;
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
    public Presenter getLockerAreaPresenter() {
        return lockerAreaLister;
    }

    @Override
    public Presenter getServicePresenter() {
        return serviceLister;
    }

    @Override
    public Presenter getFeaturePresenter() {
        return featureLister;
    }

    @Override
    public Presenter getConcessionPresenter() {
        return concessionLister;
    }

    public void populate(Building parent) {

        dashboard.populate();

        // -------------------------------------------------------

        floorplanLister.setParentFiltering(parent.getPrimaryKey());
        floorplanLister.populate(0);

        unitLister.setParentFiltering(parent.getPrimaryKey());
        unitLister.populate(0);

        elevatorLister.setParentFiltering(parent.getPrimaryKey());
        elevatorLister.populate(0);

        boilerLister.setParentFiltering(parent.getPrimaryKey());
        boilerLister.populate(0);

        roofLister.setParentFiltering(parent.getPrimaryKey());
        roofLister.populate(0);

        parkingLister.setParentFiltering(parent.getPrimaryKey());
        parkingLister.populate(0);

        lockerAreaLister.setParentFiltering(parent.getPrimaryKey());
        lockerAreaLister.populate(0);

        // -----------------------------------------------------------------------

        serviceLister.setParentFiltering(parent.serviceCatalog().getPrimaryKey());
        serviceLister.populate(0);

        featureLister.setParentFiltering(parent.serviceCatalog().getPrimaryKey());
        featureLister.populate(0);

        concessionLister.setParentFiltering(parent.serviceCatalog().getPrimaryKey());
        concessionLister.populate(0);
    }
}
