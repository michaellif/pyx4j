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
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsStatusGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsSummaryGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsYOYAnalysisChartMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.AvailabilitySummary;
import com.propertyvista.domain.dashboard.gadgets.type.BuildingLister;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata.RefreshInterval;
import com.propertyvista.domain.dashboard.gadgets.type.TurnoverAnalysisMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailability;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;

public class DashboardGenerator extends Dashboards {

    private final static I18n i18n = I18n.get(DashboardGenerator.class);

    public DashboardGenerator() {

        systemDashboards.add(defaultSystem());
        systemDashboards.add(defaultUnitAvailability());
        systemDashboards.add(defaultArrears());
    }

    private DashboardMetadata defaultSystem() {
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
        buildingLister.refreshInterval().setValue(RefreshInterval.Never);
        buildingLister.docking().column().setValue(0);

        dmd.gadgets().add(buildingLister);

        return dmd;
    }

    private DashboardMetadata defaultUnitAvailability() {
        DashboardMetadata dmd = EntityFactory.create(DashboardMetadata.class);

        dmd.user().id().setValue(ISharedUserEntity.DORMANT_KEY); // shared for everyone usage 
        dmd.type().setValue(DashboardType.building);
        dmd.isShared().setValue(true);
        dmd.name().setValue(i18n.tr("Arrears Dashboard"));
        dmd.description().setValue(i18n.tr("Contains various availablility gadgets"));
        dmd.layoutType().setValue(LayoutType.One);

        UnitAvailability unitAvailabilityReport = EntityFactory.create(UnitAvailability.class);
        unitAvailabilityReport.user().id().setValue(ISharedUserEntity.DORMANT_KEY); // shared for everyone usage
        unitAvailabilityReport.refreshInterval().setValue(RefreshInterval.Never);
        unitAvailabilityReport.pageSize().setValue(10);
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

    private DashboardMetadata defaultArrears() {
        DashboardMetadata dmd = EntityFactory.create(DashboardMetadata.class);
        dmd.user().id().setValue(ISharedUserEntity.DORMANT_KEY); // shared for everyone usage 
        dmd.type().setValue(DashboardType.building);
        dmd.isShared().setValue(true);
        dmd.name().setValue(i18n.tr("Availability Dashboard"));
        dmd.description().setValue(i18n.tr("Contains various arrears gadgets"));
        dmd.layoutType().setValue(LayoutType.One);

        ArrearsSummaryGadgetMetadata arrearsSummaryGadget = EntityFactory.create(ArrearsSummaryGadgetMetadata.class);
        arrearsSummaryGadget.docking().column().setValue(0);
        arrearsSummaryGadget.user().id().setValue(ISharedUserEntity.DORMANT_KEY);
        arrearsSummaryGadget.refreshInterval().setValue(RefreshInterval.Never);
        arrearsSummaryGadget.pageSize().setValue(1);
        arrearsSummaryGadget.customizeDate().setValue(false);
        dmd.gadgets().add(arrearsSummaryGadget);

        ArrearsYOYAnalysisChartMetadata arrearsYOYAnalysisChart = EntityFactory.create(ArrearsYOYAnalysisChartMetadata.class);
        arrearsYOYAnalysisChart.docking().column().setValue(0);
        arrearsYOYAnalysisChart.user().id().setValue(ISharedUserEntity.DORMANT_KEY);
        arrearsYOYAnalysisChart.refreshInterval().setValue(RefreshInterval.Never);
        arrearsYOYAnalysisChart.yearsToCompare().setValue(3);
        dmd.gadgets().add(arrearsYOYAnalysisChart);

        ArrearsStatusGadgetMetadata arrearsStatusGadget = EntityFactory.create(ArrearsStatusGadgetMetadata.class);
        arrearsStatusGadget.docking().column().setValue(0);
        arrearsStatusGadget.user().id().setValue(ISharedUserEntity.DORMANT_KEY);
        arrearsStatusGadget.refreshInterval().setValue(RefreshInterval.Never);
        arrearsStatusGadget.pageSize().setValue(10);
        arrearsStatusGadget.category().setValue(DebitType.total);
        dmd.gadgets().add(arrearsStatusGadget);

        return dmd;

    }
}
