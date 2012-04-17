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

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.ISharedUserEntity;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;
import com.propertyvista.domain.dashboard.gadgets.type.AvailabilitySummary;
import com.propertyvista.domain.dashboard.gadgets.type.BuildingLister;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata.RefreshInterval;
import com.propertyvista.domain.dashboard.gadgets.type.TurnoverAnalysisMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailability;

public class DashboardGenerator extends Dashboards {

    private final static I18n i18n = I18n.get(DashboardGenerator.class);

    public DashboardGenerator() {

        systemDashboards.add(DefaultSystem());
        systemDashboards.add(DefaultUnitAvailability());
    }

    private DashboardMetadata DefaultSystem() {
        DashboardMetadata dmd = EntityFactory.create(DashboardMetadata.class);
        dmd.user().id().setValue(ISharedUserEntity.DORMANT_KEY); // shared for everyone usage 
        dmd.type().setValue(DashboardType.system);
        dmd.isShared().setValue(true);
        dmd.name().setValue(i18n.tr("System Dashboard"));
        dmd.description().setValue(i18n.tr("Displays default system data"));
        dmd.layoutType().setValue(LayoutType.One);

        BuildingLister buildingLister = EntityFactory.create(BuildingLister.class);
        buildingLister.user().id().setValue(ISharedUserEntity.DORMANT_KEY); // shared for everyone usage
        buildingLister.pageSize().setValue(10);
        buildingLister.pageNumber().setValue(0);
        buildingLister.refreshInterval().setValue(RefreshInterval.Never);
        buildingLister.docking().column().setValue(0);

        dmd.gadgets().add(buildingLister);

        return dmd;
    }

    private DashboardMetadata DefaultUnitAvailability() {
        DashboardMetadata dmd = EntityFactory.create(DashboardMetadata.class);

        dmd.user().id().setValue(ISharedUserEntity.DORMANT_KEY); // shared for everyone usage 
        dmd.type().setValue(DashboardType.building);
        dmd.isShared().setValue(true);
        dmd.name().setValue(i18n.tr("Availability Dashboard"));
        dmd.description().setValue(i18n.tr("Contains various availablility gadgets"));
        dmd.layoutType().setValue(LayoutType.One);

        UnitAvailability unitAvailabilityReport = EntityFactory.create(UnitAvailability.class);
        unitAvailabilityReport.user().id().setValue(ISharedUserEntity.DORMANT_KEY); // shared for everyone usage
        unitAvailabilityReport.refreshInterval().setValue(RefreshInterval.Never);
        unitAvailabilityReport.pageSize().setValue(10);
        unitAvailabilityReport.pageNumber().setValue(0);
        unitAvailabilityReport.defaultFilteringPreset().setValue(UnitAvailability.FilterPreset.VacantAndNotice);
        unitAvailabilityReport.docking().column().setValue(0);
        dmd.gadgets().add(unitAvailabilityReport);

        AvailabilitySummary availabilitySummary = EntityFactory.create(AvailabilitySummary.class);
        availabilitySummary.user().id().setValue(ISharedUserEntity.DORMANT_KEY);
        availabilitySummary.refreshInterval().setValue(RefreshInterval.Never);
        availabilitySummary.docking().column().setValue(0);
        dmd.gadgets().add(availabilitySummary);

        TurnoverAnalysisMetadata turnoverAnalysis = EntityFactory.create(TurnoverAnalysisMetadata.class);
        turnoverAnalysis.user().id().setValue(ISharedUserEntity.DORMANT_KEY);
        turnoverAnalysis.refreshInterval().setValue(RefreshInterval.Never);
        turnoverAnalysis.isTurnoverMeasuredByPercent().setValue(false);
        turnoverAnalysis.docking().column().setValue(0);
        dmd.gadgets().add(turnoverAnalysis);

        return dmd;
    }

}
