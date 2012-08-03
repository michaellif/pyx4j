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

import java.util.Arrays;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView.Presenter;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.ListerActivityFactory;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.activity.dashboard.DashboardViewActivity;
import com.propertyvista.crm.client.ui.crud.building.BuildingViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.BuildingViewFactory;
import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.crm.client.visor.IDashboardVisorController;
import com.propertyvista.crm.client.visor.notes.INotesAndAttachmentsVisorController;
import com.propertyvista.crm.client.visor.notes.NotesAndAttachmentsVisorControllerImpl;
import com.propertyvista.crm.rpc.dto.billing.BillingCycleDTO;
import com.propertyvista.crm.rpc.services.billing.BillingCycleCrudService;
import com.propertyvista.crm.rpc.services.building.BuildingCrudService;
import com.propertyvista.crm.rpc.services.building.FloorplanCrudService;
import com.propertyvista.crm.rpc.services.building.LockerAreaCrudService;
import com.propertyvista.crm.rpc.services.building.ParkingCrudService;
import com.propertyvista.crm.rpc.services.building.catalog.ConcessionCrudService;
import com.propertyvista.crm.rpc.services.building.catalog.FeatureCrudService;
import com.propertyvista.crm.rpc.services.building.catalog.ServiceCrudService;
import com.propertyvista.crm.rpc.services.building.mech.BoilerCrudService;
import com.propertyvista.crm.rpc.services.building.mech.ElevatorCrudService;
import com.propertyvista.crm.rpc.services.building.mech.RoofCrudService;
import com.propertyvista.crm.rpc.services.unit.UnitCrudService;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BoilerDTO;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.ElevatorDTO;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.dto.LockerAreaDTO;
import com.propertyvista.dto.ParkingDTO;
import com.propertyvista.dto.RoofDTO;

public class BuildingViewerActivity extends CrmViewerActivity<BuildingDTO> implements BuildingViewerView.Presenter {

    private final DashboardView.Presenter dashboardPresenter;

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

    private INotesAndAttachmentsVisorController notesAndAttachmentsController;

    @SuppressWarnings("unchecked")
    public BuildingViewerActivity(CrudAppPlace place) {
        super(place, BuildingViewFactory.instance(BuildingViewerView.class), (AbstractCrudService<BuildingDTO>) GWT.create(BuildingCrudService.class));

        dashboardPresenter = new DashboardViewActivity(((BuildingViewerView) getView()).getDashboardView());

        floorplanLister = ListerActivityFactory.create(place, ((BuildingViewerView) getView()).getFloorplanListerView(),
                (AbstractCrudService<FloorplanDTO>) GWT.create(FloorplanCrudService.class), FloorplanDTO.class, VistaCrmBehavior.PropertyManagement);

        unitLister = ListerActivityFactory.create(place, ((BuildingViewerView) getView()).getUnitListerView(),
                (AbstractCrudService<AptUnitDTO>) GWT.create(UnitCrudService.class), AptUnitDTO.class, VistaCrmBehavior.PropertyManagement);

        elevatorLister = ListerActivityFactory.create(place, ((BuildingViewerView) getView()).getElevatorListerView(),
                (AbstractCrudService<ElevatorDTO>) GWT.create(ElevatorCrudService.class), ElevatorDTO.class, VistaCrmBehavior.Mechanicals);

        boilerLister = ListerActivityFactory.create(place, ((BuildingViewerView) getView()).getBoilerListerView(),
                (AbstractCrudService<BoilerDTO>) GWT.create(BoilerCrudService.class), BoilerDTO.class, VistaCrmBehavior.Mechanicals);

        roofLister = ListerActivityFactory.create(place, ((BuildingViewerView) getView()).getRoofListerView(),
                (AbstractCrudService<RoofDTO>) GWT.create(RoofCrudService.class), RoofDTO.class, VistaCrmBehavior.Mechanicals);

        parkingLister = new ListerActivityBase<ParkingDTO>(place, ((BuildingViewerView) getView()).getParkingListerView(),
                (AbstractCrudService<ParkingDTO>) GWT.create(ParkingCrudService.class), ParkingDTO.class);

        lockerAreaLister = new ListerActivityBase<LockerAreaDTO>(place, ((BuildingViewerView) getView()).getLockerAreaListerView(),
                (AbstractCrudService<LockerAreaDTO>) GWT.create(LockerAreaCrudService.class), LockerAreaDTO.class);

        serviceLister = ListerActivityFactory.create(place, ((BuildingViewerView) getView()).getServiceListerView(),
                (AbstractCrudService<Service>) GWT.create(ServiceCrudService.class), Service.class, VistaCrmBehavior.ProductCatalog);
        featureLister = ListerActivityFactory.create(place, ((BuildingViewerView) getView()).getFeatureListerView(),
                (AbstractCrudService<Feature>) GWT.create(FeatureCrudService.class), Feature.class, VistaCrmBehavior.ProductCatalog);
        concessionLister = ListerActivityFactory.create(place, ((BuildingViewerView) getView()).getConcessionListerView(),
                (AbstractCrudService<Concession>) GWT.create(ConcessionCrudService.class), Concession.class, VistaCrmBehavior.ProductCatalog);

        billingCycleLister = ListerActivityFactory.create(place, ((BuildingViewerView) getView()).getBillingCycleListerView(),
                (AbstractCrudService<BillingCycleDTO>) GWT.create(BillingCycleCrudService.class), BillingCycleDTO.class, VistaCrmBehavior.PropertyManagement);
    }

    @Override
    public boolean canEdit() {
        return SecurityController.checkBehavior(VistaCrmBehavior.PropertyManagement);
    }

    @Override
    public DashboardView.Presenter getDashboardPresenter() {
        return dashboardPresenter;
    }

    @Override
    public void onPopulateSuccess(BuildingDTO result) {
        super.onPopulateSuccess(result);

        ((BuildingViewerView) getView()).getDashboardView().setBuildings(Arrays.asList(result.<Building> duplicate(Building.class)), false);
        dashboardPresenter.populate(result.dashboard().getPrimaryKey());

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

    @Override
    public void onStop() {
        ((AbstractActivity) dashboardPresenter).onStop();
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
        ((AbstractActivity) billingCycleLister).onStop();
        super.onStop();
    }

    @Override
    public INotesAndAttachmentsVisorController getNotesAndAttachmentsController() {
        if (notesAndAttachmentsController == null) {
            notesAndAttachmentsController = new NotesAndAttachmentsVisorControllerImpl(Building.class, getEntityId());
        }
        return notesAndAttachmentsController;
    }

    @Override
    public IDashboardVisorController getDashboardController() {
        // TODO Auto-generated method stub
        return null;
    }
}
