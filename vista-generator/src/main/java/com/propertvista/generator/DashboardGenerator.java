/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 13, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertvista.generator;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsStatus;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsStatus.Category;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsYOYAnalysisChart;
import com.propertyvista.domain.dashboard.gadgets.type.AvailabilitySummary;
import com.propertyvista.domain.dashboard.gadgets.type.BuildingLister;
import com.propertyvista.domain.dashboard.gadgets.type.TurnoverAnalysisMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailability;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailability.FilterPreset;

public class DashboardGenerator extends Dashboards {

    public DashboardGenerator() {

        systemDashboards.add(DefaultSystem());

        buildingDashboards.add(DefaultBuilding1());
        buildingDashboards.add(DefaultBuilding2());
        buildingDashboards.add(DefaultBuilding3());
    }

    private DashboardMetadata DefaultSystem() {
        DashboardMetadata dmd = EntityFactory.create(DashboardMetadata.class);
        dmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage 
        dmd.type().setValue(DashboardType.system);
        dmd.isShared().setValue(true);
        dmd.name().setValue("System Dashboard");
        dmd.description().setValue("Displays default system data");
        dmd.layoutType().setValue(LayoutType.One);

        BuildingLister buildingLister = EntityFactory.create(BuildingLister.class);
        buildingLister.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage
        buildingLister.pageSize().setValue(10);
        buildingLister.pageNumber().setValue(0);
        buildingLister.refreshPeriod().setValue(-1);
        buildingLister.docking().column().setValue(0);
        Persistence.service().persist(buildingLister);

        dmd.gadgets().add(buildingLister);

        return dmd;
    }

    private DashboardMetadata DefaultBuilding1() {
        DashboardMetadata dmd = EntityFactory.create(DashboardMetadata.class);

        dmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage 
        dmd.type().setValue(DashboardType.building);
        dmd.isShared().setValue(true);
        dmd.name().setValue("General Building Dashboard");
        dmd.description().setValue("Displays some building data");
        dmd.layoutType().setValue(LayoutType.Two21);

        // TODO add some more cool gadgets
        return dmd;
    }

    private DashboardMetadata DefaultBuilding2() {
        DashboardMetadata dmd = EntityFactory.create(DashboardMetadata.class);

        dmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage 
        dmd.type().setValue(DashboardType.building);
        dmd.isShared().setValue(true);
        dmd.name().setValue("Availability Dashboard");
        dmd.description().setValue("Contains various availablility gadgets");
        dmd.layoutType().setValue(LayoutType.One);

        UnitAvailability unitAvailabilityReport = EntityFactory.create(UnitAvailability.class);
        unitAvailabilityReport.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage
        unitAvailabilityReport.refreshPeriod().setValue(-1);
        unitAvailabilityReport.pageSize().setValue(10);
        unitAvailabilityReport.pageNumber().setValue(0);
        unitAvailabilityReport.defaultFilteringButton().setValue(FilterPreset.All);
        unitAvailabilityReport.docking().column().setValue(0);
        Persistence.service().persist(unitAvailabilityReport);
        dmd.gadgets().add(unitAvailabilityReport);

        AvailabilitySummary availabilitySummary = EntityFactory.create(AvailabilitySummary.class);
        availabilitySummary.user().id().setValue(Key.DORMANT_KEY);
        availabilitySummary.refreshPeriod().setValue(-1);
        availabilitySummary.docking().column().setValue(0);
        Persistence.service().persist(availabilitySummary);
        dmd.gadgets().add(availabilitySummary);

        TurnoverAnalysisMetadata turnoverAnalysis = EntityFactory.create(TurnoverAnalysisMetadata.class);
        turnoverAnalysis.user().id().setValue(Key.DORMANT_KEY);
        turnoverAnalysis.refreshPeriod().setValue(-1);
        turnoverAnalysis.isTurnoverMeasuredByPercent().setValue(false);
        turnoverAnalysis.docking().column().setValue(0);
        Persistence.service().persist(turnoverAnalysis);
        dmd.gadgets().add(turnoverAnalysis);

        return dmd;
    }

    private DashboardMetadata DefaultBuilding3() {
        DashboardMetadata dmd = EntityFactory.create(DashboardMetadata.class);
        dmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage 
        dmd.type().setValue(DashboardType.building);
        dmd.isShared().setValue(true);
        dmd.name().setValue("Arrears Dashboard");
        dmd.description().setValue("Displays some building data");
        dmd.layoutType().setValue(LayoutType.One);

        ArrearsStatus arrearsStatus = EntityFactory.create(ArrearsStatus.class);
        arrearsStatus.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage
        arrearsStatus.refreshPeriod().setValue(-1);
        arrearsStatus.pageSize().setValue(10);
        arrearsStatus.pageNumber().setValue(0);
        arrearsStatus.category().setValue(Category.Total);
        arrearsStatus.docking().column().setValue(0);
        Persistence.service().persist(arrearsStatus);
        dmd.gadgets().add(arrearsStatus);

        arrearsStatus = EntityFactory.create(ArrearsStatus.class);
        arrearsStatus.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage
        arrearsStatus.refreshPeriod().setValue(-1);
        arrearsStatus.pageSize().setValue(10);
        arrearsStatus.pageNumber().setValue(0);
        arrearsStatus.category().setValue(Category.Rent);
        arrearsStatus.docking().column().setValue(0);
        Persistence.service().persist(arrearsStatus);
        dmd.gadgets().add(arrearsStatus);

        arrearsStatus = EntityFactory.create(ArrearsStatus.class);
        arrearsStatus.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage
        arrearsStatus.refreshPeriod().setValue(-1);
        arrearsStatus.pageSize().setValue(10);
        arrearsStatus.pageNumber().setValue(0);
        arrearsStatus.category().setValue(Category.Parking);
        arrearsStatus.docking().column().setValue(0);
        Persistence.service().persist(arrearsStatus);
        dmd.gadgets().add(arrearsStatus);

        arrearsStatus = EntityFactory.create(ArrearsStatus.class);
        arrearsStatus.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage
        arrearsStatus.refreshPeriod().setValue(-1);
        arrearsStatus.pageSize().setValue(10);
        arrearsStatus.pageNumber().setValue(0);
        arrearsStatus.category().setValue(Category.Other);
        arrearsStatus.docking().column().setValue(0);
        Persistence.service().persist(arrearsStatus);
        dmd.gadgets().add(arrearsStatus);

        ArrearsYOYAnalysisChart chart = new EntityFactory().create(ArrearsYOYAnalysisChart.class);
        chart.user().id().setValue(Key.DORMANT_KEY);
        chart.refreshPeriod().setValue(-1);
        chart.docking().column().setValue(0);
        Persistence.service().persist(chart);
        dmd.gadgets().add(chart);

        return dmd;
    }
}
