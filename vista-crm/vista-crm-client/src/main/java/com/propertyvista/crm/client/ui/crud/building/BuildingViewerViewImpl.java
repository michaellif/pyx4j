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
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.lister.ILister;
import com.pyx4j.site.client.ui.prime.lister.ListerInternalViewImplBase;
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
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BoilerDTO;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.ElevatorDTO;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.dto.LockerAreaDTO;
import com.propertyvista.dto.ParkingDTO;
import com.propertyvista.dto.RoofDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class BuildingViewerViewImpl extends CrmViewerViewImplBase<BuildingDTO> implements BuildingViewerView {

    private static final I18n i18n = I18n.get(BuildingViewerViewImpl.class);

    private final ILister<FloorplanDTO> floorplanLister;

    private final ILister<AptUnitDTO> unitLister;

    private final ILister<ElevatorDTO> elevatorLister;

    private final ILister<BoilerDTO> boilerLister;

    private final ILister<RoofDTO> roofLister;

    private final ILister<ParkingDTO> parkingLister;

    private final ILister<LockerAreaDTO> lockerAreaLister;

    private final ILister<Service> serviceLister;

    private final ILister<Feature> featureLister;

    private final ILister<Concession> concessionLister;

    private final ILister<BillingCycleDTO> billingCycleLister;

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
        dashboardsMenu = new ButtonMenuBar();
        dashboardButton.setMenu(dashboardsMenu);
        addHeaderToolbarItem(dashboardButton);

        addAction(new MenuItem(i18n.tr("Maintenance Requests"), new Command() {
            @Override
            public void execute() {
                if (!isVisorShown()) {
                    ((BuildingViewerView.Presenter) getPresenter()).getMaintenanceRequestVisorController().show();
                }
            }
        }));

        addAction(new MenuItem(i18n.tr("Community Events"), new Command() {
            @Override
            public void execute() {
                if (!isVisorShown()) {
                    ((BuildingViewerView.Presenter) getPresenter()).getCommunityEventVisorController().show();
                }
            }
        }));

        if (VistaFeatures.instance().yardiIntegration()) {
            addAction(new MenuItem(i18n.tr("Update From Yardi"), new Command() {
                @Override
                public void execute() {
                    ((BuildingViewerView.Presenter) getPresenter()).updateFromYardi();
                }
            }));
        }
    }

    @Override
    public ILister<FloorplanDTO> getFloorplanListerView() {
        return floorplanLister;
    }

    @Override
    public ILister<AptUnitDTO> getUnitListerView() {
        return unitLister;
    }

    @Override
    public ILister<ElevatorDTO> getElevatorListerView() {
        return elevatorLister;
    }

    @Override
    public ILister<BoilerDTO> getBoilerListerView() {
        return boilerLister;
    }

    @Override
    public ILister<RoofDTO> getRoofListerView() {
        return roofLister;
    }

    @Override
    public ILister<ParkingDTO> getParkingListerView() {
        return parkingLister;
    }

    @Override
    public ILister<LockerAreaDTO> getLockerAreaListerView() {
        return lockerAreaLister;
    }

    @Override
    public ILister<Service> getServiceListerView() {
        return serviceLister;
    }

    @Override
    public ILister<Feature> getFeatureListerView() {
        return featureLister;
    }

    @Override
    public ILister<Concession> getConcessionListerView() {
        return concessionLister;
    }

    @Override
    public ILister<BillingCycleDTO> getBillingCycleListerView() {
        return billingCycleLister;
    }

    // Internals:

    public interface RunBillData extends IEntity {

        @NotNull
        IPrimitive<BillingPeriod> billingPeriod();

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
                        ((BuildingViewerView.Presenter) getPresenter()).getDashboardController(dashboard, buildingsFilter).show();
                    }
                }

            });
        }
    }

}
