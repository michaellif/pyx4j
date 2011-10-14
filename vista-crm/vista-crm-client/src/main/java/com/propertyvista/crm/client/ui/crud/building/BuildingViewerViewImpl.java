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
package com.propertyvista.crm.client.ui.crud.building;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.building.catalog.ConcessionLister;
import com.propertyvista.crm.client.ui.crud.building.catalog.FeatureLister;
import com.propertyvista.crm.client.ui.crud.building.catalog.ServiceLister;
import com.propertyvista.crm.client.ui.crud.building.lockers.LockerAreaLister;
import com.propertyvista.crm.client.ui.crud.building.mech.BoilerLister;
import com.propertyvista.crm.client.ui.crud.building.mech.ElevatorLister;
import com.propertyvista.crm.client.ui.crud.building.mech.RoofLister;
import com.propertyvista.crm.client.ui.crud.building.parking.ParkingLister;
import com.propertyvista.crm.client.ui.crud.floorplan.FloorplanLister;
import com.propertyvista.crm.client.ui.crud.unit.UnitLister;
import com.propertyvista.crm.client.ui.dashboard.DashboardPanel;
import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
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

public class BuildingViewerViewImpl extends CrmViewerViewImplBase<BuildingDTO> implements BuildingViewerView {

    private final IListerView<FloorplanDTO> floorplanLister;

    private final IListerView<AptUnitDTO> unitLister;

    private final IListerView<ElevatorDTO> elevatorLister;

    private final IListerView<BoilerDTO> boilerLister;

    private final IListerView<RoofDTO> roofLister;

    private final IListerView<ParkingDTO> parkingLister;

    private final IListerView<LockerAreaDTO> lockerAreaLister;

    private final IListerView<Service> serviceLister;

    private final IListerView<Feature> featureLister;

    private final IListerView<Concession> concessionLister;

    private final DashboardPanel dashboardView = new DashboardPanel();

    private final CEntityComboBox<DashboardMetadata> dashboardSelect = new CEntityComboBox<DashboardMetadata>(DashboardMetadata.class);

    public BuildingViewerViewImpl() {
        super(CrmSiteMap.Properties.Building.class);

        dashboardSelect.setWidth("25em");
        dashboardSelect.addCriterion(PropertyCriterion.eq(dashboardSelect.proto().type(), DashboardType.building));
        dashboardSelect.addValueChangeHandler(new ValueChangeHandler<DashboardMetadata>() {
            @Override
            public void onValueChange(ValueChangeEvent<DashboardMetadata> event) {
                dashboardView.fill(event.getValue());
            }
        });

        dashboardView.addAction(dashboardSelect.asWidget());

        floorplanLister = new ListerInternalViewImplBase<FloorplanDTO>(new FloorplanLister(/* readOnly */));

        unitLister = new ListerInternalViewImplBase<AptUnitDTO>(new UnitLister(/* readOnly */));

        elevatorLister = new ListerInternalViewImplBase<ElevatorDTO>(new ElevatorLister(/* readOnly */));
        boilerLister = new ListerInternalViewImplBase<BoilerDTO>(new BoilerLister(/* readOnly */));
        roofLister = new ListerInternalViewImplBase<RoofDTO>(new RoofLister(/* readOnly */));

        parkingLister = new ListerInternalViewImplBase<ParkingDTO>(new ParkingLister(/* readOnly */));
        lockerAreaLister = new ListerInternalViewImplBase<LockerAreaDTO>(new LockerAreaLister(/* readOnly */));

        serviceLister = new ListerInternalViewImplBase<Service>(new ServiceLister(/* readOnly */));
        featureLister = new ListerInternalViewImplBase<Feature>(new FeatureLister(/* readOnly */));
        concessionLister = new ListerInternalViewImplBase<Concession>(new ConcessionLister(/* readOnly */));

        // create/init/set main form here: 
        CrmEntityForm<BuildingDTO> form = new BuildingEditorForm(new CrmViewersComponentFactory(), this);
        form.initContent();
        setForm(form);
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

    @Override
    public IListerView<Service> getServiceListerView() {
        return serviceLister;
    }

    @Override
    public IListerView<Feature> getFeatureListerView() {
        return featureLister;
    }

    @Override
    public IListerView<Concession> getConcessionListerView() {
        return concessionLister;
    }

    @Override
    public void populate(BuildingDTO value) {
        super.populate(value);

        dashboardSelect.setValue(value.dashboard());
    }
}
