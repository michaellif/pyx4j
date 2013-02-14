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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Button.ButtonMenuBar;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.billing.cycle.BillingCycleLister;
import com.propertyvista.crm.client.ui.crud.building.catalog.ConcessionLister;
import com.propertyvista.crm.client.ui.crud.building.catalog.FeatureLister;
import com.propertyvista.crm.client.ui.crud.building.catalog.ServiceLister;
import com.propertyvista.crm.client.ui.crud.building.lockers.LockerAreaLister;
import com.propertyvista.crm.client.ui.crud.building.mech.BoilerLister;
import com.propertyvista.crm.client.ui.crud.building.mech.ElevatorLister;
import com.propertyvista.crm.client.ui.crud.building.mech.RoofLister;
import com.propertyvista.crm.client.ui.crud.building.parking.ParkingLister;
import com.propertyvista.crm.client.ui.crud.floorplan.FloorplanLister;
import com.propertyvista.crm.rpc.dto.billing.BillingCycleDTO;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BoilerDTO;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.ElevatorDTO;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.dto.LockerAreaDTO;
import com.propertyvista.dto.ParkingDTO;
import com.propertyvista.dto.RoofDTO;

public class BuildingViewerViewImpl extends CrmViewerViewImplBase<BuildingDTO> implements BuildingViewerView {

    private static final I18n i18n = I18n.get(BuildingViewerViewImpl.class);

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

    private final IListerView<BillingCycleDTO> billingCycleLister;

    private final ButtonMenuBar dashboardsMenu;

    public BuildingViewerViewImpl() {

        floorplanLister = new ListerInternalViewImplBase<FloorplanDTO>(new FloorplanLister());

        unitLister = new ListerInternalViewImplBase<AptUnitDTO>(new BuildingUnitLister(true));

        elevatorLister = new ListerInternalViewImplBase<ElevatorDTO>(new ElevatorLister());
        boilerLister = new ListerInternalViewImplBase<BoilerDTO>(new BoilerLister());
        roofLister = new ListerInternalViewImplBase<RoofDTO>(new RoofLister());

        parkingLister = new ListerInternalViewImplBase<ParkingDTO>(new ParkingLister());
        lockerAreaLister = new ListerInternalViewImplBase<LockerAreaDTO>(new LockerAreaLister());

        serviceLister = new ListerInternalViewImplBase<Service>(new ServiceLister());
        featureLister = new ListerInternalViewImplBase<Feature>(new FeatureLister());
        concessionLister = new ListerInternalViewImplBase<Concession>(new ConcessionLister());

        billingCycleLister = new ListerInternalViewImplBase<BillingCycleDTO>(new BillingCycleLister());

        // set main form here:
        setForm(new BuildingForm(this));

        Button dashboardButton = new Button(i18n.tr("Dashboard"));
        dashboardsMenu = dashboardButton.createMenu();
        dashboardButton.setMenu(dashboardsMenu);
        addHeaderToolbarItem(dashboardButton);
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
    public IListerView<BillingCycleDTO> getBillingCycleListerView() {
        return billingCycleLister;
    }

    // Internals:

    public interface RunBillData extends IEntity {

        @NotNull
        IPrimitive<PaymentFrequency> paymentFrequency();

        @NotNull
        IPrimitive<LogicalDate> billingPeriodStartDate();
    }

    @Override
    public void populate(BuildingDTO value) {
        value.getPrimaryKey();
        super.populate(value);
        populateDashboardsMenu(value.dashboards().iterator());
    }

    private void populateDashboardsMenu(Iterator<DashboardMetadata> dashboardsIterator) {
        dashboardsMenu.clearItems();
        while (dashboardsIterator.hasNext()) {
            final DashboardMetadata dashboard = dashboardsIterator.next();
            dashboardsMenu.addItem(dashboard.name().getValue(), new Command() {

                @Override
                public void execute() {
                    List<Building> buildingsFilter = new ArrayList<Building>();
                    buildingsFilter.add(getForm().getValue());
                    if (!isVisorShown()) {
                        ((BuildingViewerView.Presenter) getPresenter()).getDashboardController(dashboard, buildingsFilter).show(BuildingViewerViewImpl.this);
                    }
                }

            });
        }
    }

}
