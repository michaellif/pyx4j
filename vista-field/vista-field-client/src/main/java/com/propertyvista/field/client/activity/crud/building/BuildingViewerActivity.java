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
package com.propertyvista.field.client.activity.crud.building;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.ui.prime.lister.ILister.Presenter;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BoilerDTO;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.ElevatorDTO;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.dto.LockerAreaDTO;
import com.propertyvista.dto.ParkingDTO;
import com.propertyvista.dto.RoofDTO;
import com.propertyvista.field.client.activity.ListerControllerFactory;
import com.propertyvista.field.client.activity.crud.FieldViewerActivity;
import com.propertyvista.field.client.ui.crud.building.BuildingViewerView;
import com.propertyvista.field.client.ui.viewfactories.BuildingViewFactory;
import com.propertyvista.field.rpc.dto.billing.BillingCycleDTO;
import com.propertyvista.field.rpc.services.billing.BillingCycleCrudService;
import com.propertyvista.field.rpc.services.building.BuildingCrudService;
import com.propertyvista.field.rpc.services.building.FloorplanCrudService;
import com.propertyvista.field.rpc.services.building.LockerAreaCrudService;
import com.propertyvista.field.rpc.services.building.ParkingCrudService;
import com.propertyvista.field.rpc.services.building.catalog.ConcessionCrudService;
import com.propertyvista.field.rpc.services.building.catalog.FeatureCrudService;
import com.propertyvista.field.rpc.services.building.catalog.ServiceCrudService;
import com.propertyvista.field.rpc.services.building.mech.BoilerCrudService;
import com.propertyvista.field.rpc.services.building.mech.ElevatorCrudService;
import com.propertyvista.field.rpc.services.building.mech.RoofCrudService;
import com.propertyvista.field.rpc.services.unit.UnitCrudService;

public class BuildingViewerActivity extends FieldViewerActivity<BuildingDTO> implements BuildingViewerView.Presenter {

    private final Presenter<FloorplanDTO> floorplanLister;

    private final Presenter<AptUnitDTO> unitLister;

    private final Presenter<ElevatorDTO> elevatorLister;

    private final Presenter<BoilerDTO> boilerLister;

    private final Presenter<RoofDTO> roofLister;

    private final Presenter<ParkingDTO> parkingLister;

    private final Presenter<LockerAreaDTO> lockerAreaLister;

    private final Presenter<Service> serviceLister;

    private final Presenter<Feature> featureLister;

    private final Presenter<Concession> concessionLister;

    private final Presenter<BillingCycleDTO> billingCycleLister;

    @SuppressWarnings("unchecked")
    public BuildingViewerActivity(CrudAppPlace place) {
        super(place, BuildingViewFactory.instance(BuildingViewerView.class), (AbstractCrudService<BuildingDTO>) GWT.create(BuildingCrudService.class));

        floorplanLister = ListerControllerFactory.create(((BuildingViewerView) getView()).getFloorplanListerView(),
                (AbstractCrudService<FloorplanDTO>) GWT.create(FloorplanCrudService.class), FloorplanDTO.class, VistaCrmBehavior.PropertyManagement);

        unitLister = ListerControllerFactory.create(((BuildingViewerView) getView()).getUnitListerView(),
                (AbstractCrudService<AptUnitDTO>) GWT.create(UnitCrudService.class), AptUnitDTO.class, VistaCrmBehavior.PropertyManagement);

        elevatorLister = ListerControllerFactory.create(((BuildingViewerView) getView()).getElevatorListerView(),
                (AbstractCrudService<ElevatorDTO>) GWT.create(ElevatorCrudService.class), ElevatorDTO.class, VistaCrmBehavior.Mechanicals);
        boilerLister = ListerControllerFactory.create(((BuildingViewerView) getView()).getBoilerListerView(),
                (AbstractCrudService<BoilerDTO>) GWT.create(BoilerCrudService.class), BoilerDTO.class, VistaCrmBehavior.Mechanicals);
        roofLister = ListerControllerFactory.create(((BuildingViewerView) getView()).getRoofListerView(),
                (AbstractCrudService<RoofDTO>) GWT.create(RoofCrudService.class), RoofDTO.class, VistaCrmBehavior.Mechanicals);

        parkingLister = ListerControllerFactory.create(((BuildingViewerView) getView()).getParkingListerView(),
                (AbstractCrudService<ParkingDTO>) GWT.create(ParkingCrudService.class), ParkingDTO.class, VistaCrmBehavior.PropertyManagement);
        lockerAreaLister = ListerControllerFactory.create(((BuildingViewerView) getView()).getLockerAreaListerView(),
                (AbstractCrudService<LockerAreaDTO>) GWT.create(LockerAreaCrudService.class), LockerAreaDTO.class, VistaCrmBehavior.PropertyManagement);

        serviceLister = ListerControllerFactory.create(((BuildingViewerView) getView()).getServiceListerView(),
                (AbstractCrudService<Service>) GWT.create(ServiceCrudService.class), Service.class, VistaCrmBehavior.ProductCatalog);
        featureLister = ListerControllerFactory.create(((BuildingViewerView) getView()).getFeatureListerView(),
                (AbstractCrudService<Feature>) GWT.create(FeatureCrudService.class), Feature.class, VistaCrmBehavior.ProductCatalog);
        concessionLister = ListerControllerFactory.create(((BuildingViewerView) getView()).getConcessionListerView(),
                (AbstractCrudService<Concession>) GWT.create(ConcessionCrudService.class), Concession.class, VistaCrmBehavior.ProductCatalog);

        billingCycleLister = ListerControllerFactory.create(((BuildingViewerView) getView()).getBillingCycleListerView(),
                (AbstractCrudService<BillingCycleDTO>) GWT.create(BillingCycleCrudService.class), BillingCycleDTO.class, VistaCrmBehavior.PropertyManagement);
    }

    @Override
    public boolean canEdit() {
        return SecurityController.checkBehavior(VistaCrmBehavior.PropertyManagement);
    }

    @Override
    public void onPopulateSuccess(BuildingDTO result) {
        super.onPopulateSuccess(result);

        // -------------------------------------------------------

        floorplanLister.setParent(result.getPrimaryKey());
        floorplanLister.populate();

        unitLister.setParent(result.getPrimaryKey());
        unitLister.populate();

        // -------------------------------------------------------

        elevatorLister.setParent(result.getPrimaryKey());
        elevatorLister.populate();

        boilerLister.setParent(result.getPrimaryKey());
        boilerLister.populate();

        roofLister.setParent(result.getPrimaryKey());
        roofLister.populate();

        // -------------------------------------------------------

        parkingLister.setParent(result.getPrimaryKey());
        parkingLister.populate();

        lockerAreaLister.setParent(result.getPrimaryKey());
        lockerAreaLister.populate();

        // -----------------------------------------------------------------------

        serviceLister.setParent(result.productCatalog().getPrimaryKey());
        serviceLister.populate();

        featureLister.setParent(result.productCatalog().getPrimaryKey());
        featureLister.populate();

        concessionLister.setParent(result.productCatalog().getPrimaryKey());
        concessionLister.populate();

        billingCycleLister.clearPreDefinedFilters();
        billingCycleLister.addPreDefinedFilter(PropertyCriterion.eq(EntityFactory.getEntityPrototype(BillingCycleDTO.class).building(), result));
        billingCycleLister.populate();
    }

//    @Override
//    public IDashboardVisorController getDashboardController(DashboardMetadata dashboardMetadata, List<Building> buildings) {
//        return new DashboardVisorController(getView(), dashboardMetadata, buildings);
//    }
}
