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

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.activity.crud.ViewerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.IListerView.Presenter;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.activity.dashboard.DashboardViewActivity;
import com.propertyvista.crm.client.ui.crud.building.BuildingViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.BuildingViewFactory;
import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.BoilerCrudService;
import com.propertyvista.crm.rpc.services.BuildingCrudService;
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
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BoilerDTO;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.ElevatorDTO;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.dto.LockerAreaDTO;
import com.propertyvista.dto.ParkingDTO;
import com.propertyvista.dto.RoofDTO;

public class BuildingViewerActivity extends ViewerActivityBase<BuildingDTO> implements BuildingViewerView.Presenter {

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
    public BuildingViewerActivity(Place place) {
        super((BuildingViewerView) BuildingViewFactory.instance(BuildingViewerView.class), (AbstractCrudService<BuildingDTO>) GWT
                .create(BuildingCrudService.class));

        dashboard = new DashboardViewActivity(((BuildingViewerView) view).getDashboardView(), new CrmSiteMap.Dashboard.Building());

        floorplanLister = new ListerActivityBase<FloorplanDTO>(((BuildingViewerView) view).getFloorplanListerView(),
                (AbstractCrudService<FloorplanDTO>) GWT.create(FloorplanCrudService.class), FloorplanDTO.class);

        unitLister = new ListerActivityBase<AptUnitDTO>(((BuildingViewerView) view).getUnitListerView(),
                (AbstractCrudService<AptUnitDTO>) GWT.create(UnitCrudService.class), AptUnitDTO.class);

        elevatorLister = new ListerActivityBase<ElevatorDTO>(((BuildingViewerView) view).getElevatorListerView(),
                (AbstractCrudService<ElevatorDTO>) GWT.create(ElevatorCrudService.class), ElevatorDTO.class);

        boilerLister = new ListerActivityBase<BoilerDTO>(((BuildingViewerView) view).getBoilerListerView(),
                (AbstractCrudService<BoilerDTO>) GWT.create(BoilerCrudService.class), BoilerDTO.class);

        roofLister = new ListerActivityBase<RoofDTO>(((BuildingViewerView) view).getRoofListerView(),
                (AbstractCrudService<RoofDTO>) GWT.create(RoofCrudService.class), RoofDTO.class);

        parkingLister = new ListerActivityBase<ParkingDTO>(((BuildingViewerView) view).getParkingListerView(),
                (AbstractCrudService<ParkingDTO>) GWT.create(ParkingCrudService.class), ParkingDTO.class);

        lockerAreaLister = new ListerActivityBase<LockerAreaDTO>(((BuildingViewerView) view).getLockerAreaListerView(),
                (AbstractCrudService<LockerAreaDTO>) GWT.create(LockerAreaCrudService.class), LockerAreaDTO.class);

        serviceLister = new ListerActivityBase<Service>(((BuildingViewerView) view).getServiceListerView(),
                (AbstractCrudService<Service>) GWT.create(ServiceCrudService.class), Service.class);
        featureLister = new ListerActivityBase<Feature>(((BuildingViewerView) view).getFeatureListerView(),
                (AbstractCrudService<Feature>) GWT.create(FeatureCrudService.class), Feature.class);
        concessionLister = new ListerActivityBase<Concession>(((BuildingViewerView) view).getConcessionListerView(),
                (AbstractCrudService<Concession>) GWT.create(ConcessionCrudService.class), Concession.class);

        setPlace(place);
        floorplanLister.setPlace(place);
        unitLister.setPlace(place);
        elevatorLister.setPlace(place);
        boilerLister.setPlace(place);
        roofLister.setPlace(place);
        parkingLister.setPlace(place);
        lockerAreaLister.setPlace(place);
        serviceLister.setPlace(place);
        featureLister.setPlace(place);
        concessionLister.setPlace(place);
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

    @Override
    public void onPopulateSuccess(BuildingDTO result) {
        super.onPopulateSuccess(result);

        dashboard.populate();

        // -------------------------------------------------------

        floorplanLister.setParentFiltering(result.getPrimaryKey());
        floorplanLister.populate();

        unitLister.setParentFiltering(result.getPrimaryKey());
        unitLister.populate();

        // -------------------------------------------------------

        elevatorLister.setParentFiltering(result.getPrimaryKey());
        elevatorLister.populate();

        boilerLister.setParentFiltering(result.getPrimaryKey());
        boilerLister.populate();

        roofLister.setParentFiltering(result.getPrimaryKey());
        roofLister.populate();

        // -------------------------------------------------------

        parkingLister.setParentFiltering(result.getPrimaryKey());
        parkingLister.populate();

        lockerAreaLister.setParentFiltering(result.getPrimaryKey());
        lockerAreaLister.populate();

        // -----------------------------------------------------------------------

        serviceLister.setParentFiltering(result.serviceCatalog().getPrimaryKey());
        serviceLister.populate();

        featureLister.setParentFiltering(result.serviceCatalog().getPrimaryKey());
        featureLister.populate();

        concessionLister.setParentFiltering(result.serviceCatalog().getPrimaryKey());
        concessionLister.populate();
    }

    @Override
    public void onStop() {
        ((AbstractActivity) floorplanLister).onStop();
        ((AbstractActivity) unitLister).onStop();
        ((AbstractActivity) elevatorLister).onStop();
        ((AbstractActivity) boilerLister).onStop();
        ((AbstractActivity) roofLister).onStop();
        ((AbstractActivity) parkingLister).onStop();
        ((AbstractActivity) lockerAreaLister).onStop();
        ((AbstractActivity) serviceLister).onStop();
        ((AbstractActivity) featureLister).onStop();
        ((AbstractActivity) concessionLister).onStop();
        super.onStop();
    }
}
