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

import static com.propertvista.generator.util.ColumnDescriptorEntityBuilder.defColumn;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.ISharedUserEntity;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;
import com.propertyvista.domain.dashboard.gadgets.ColumnDescriptorEntity;
import com.propertyvista.domain.dashboard.gadgets.arrears.LeaseArrearsSnapshotDTO;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsStatusGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsSummaryGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsYOYAnalysisChartMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.AvailabilitySummary;
import com.propertyvista.domain.dashboard.gadgets.type.BuildingLister;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata.RefreshInterval;
import com.propertyvista.domain.dashboard.gadgets.type.TurnoverAnalysisMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailability;
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.dto.BuildingDTO;

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
        buildingLister.columnDescriptors().addAll(defineBuildingListerGadgetColumns());

        dmd.gadgets().add(buildingLister);

        return dmd;
    }

    private DashboardMetadata defaultUnitAvailability() {
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
        unitAvailabilityReport.filterPreset().setValue(UnitAvailability.FilterPreset.VacantAndNotice);
        unitAvailabilityReport.docking().column().setValue(0);
        unitAvailabilityReport.columnDescriptors().addAll(defineUnitAvailabilityReportColumns());
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

    private List<? extends ColumnDescriptorEntity> defineUnitAvailabilityReportColumns() {
        UnitAvailabilityStatus proto = EntityFactory.getEntityPrototype(UnitAvailabilityStatus.class);

        return Arrays.asList(//@formatter:off
                // references
                defColumn(proto.building().propertyCode()).build(),
                defColumn(proto.building().externalId()).visible(false).build(),
                defColumn(proto.building().info().name()).visible(false).title(i18n.tr("Building Name")).build(),
                defColumn(proto.building().info().address()).visible(false).build(),
                defColumn(proto.building().propertyManager().name()).visible(false).title(i18n.tr("Property Manager")).build(),                    
                defColumn(proto.building().complex().name()).visible(false).title(i18n.tr("Complex")).build(),
                defColumn(proto.unit().info().number()).title(i18n.tr("Unit Name")).build(),
                defColumn(proto.floorplan().name()).visible(false).title(i18n.tr("Floorplan Name")).build(),
                defColumn(proto.floorplan().marketingName()).visible(false).title(i18n.tr("Floorplan Marketing Name")).build(),
                
                // status
                defColumn(proto.vacancyStatus()).build(),
                defColumn(proto.rentedStatus()).visible(true).build(),
                defColumn(proto.scoping()).visible(true).build(),
                defColumn(proto.rentReadinessStatus()).visible(true).build(),
//                column(proto.unitRent()).build(),
//                column(proto.marketRent()).build(),
//                column(proto.rentDeltaAbsolute()).visible(true).build(),
//                column(proto.rentDeltaRelative()).visible(false).build(),
                defColumn(proto.rentEndDay()).visible(true).build(),
                defColumn(proto.moveInDay()).visible(true).build(),
                defColumn(proto.rentedFromDay()).visible(true).build(),
                defColumn(proto.daysVacant()).build()
//                column(proto.revenueLost()).build()
        );//@formatter:on
    }

    private DashboardMetadata defaultArrears() {
        DashboardMetadata dmd = EntityFactory.create(DashboardMetadata.class);
        dmd.user().id().setValue(ISharedUserEntity.DORMANT_KEY); // shared for everyone usage 
        dmd.type().setValue(DashboardType.building);
        dmd.isShared().setValue(true);
        dmd.name().setValue(i18n.tr("Arrears Dashboard"));
        dmd.description().setValue(i18n.tr("Contains various arrears gadgets"));
        dmd.layoutType().setValue(LayoutType.One);

        ArrearsSummaryGadgetMetadata arrearsSummaryGadget = EntityFactory.create(ArrearsSummaryGadgetMetadata.class);
        arrearsSummaryGadget.docking().column().setValue(0);
        arrearsSummaryGadget.user().id().setValue(ISharedUserEntity.DORMANT_KEY);
        arrearsSummaryGadget.refreshInterval().setValue(RefreshInterval.Never);
        arrearsSummaryGadget.pageSize().setValue(1);
        arrearsSummaryGadget.customizeDate().setValue(false);
        arrearsSummaryGadget.columnDescriptors().addAll(defineArreasSummaryGadgetColumns());
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
        arrearsStatusGadget.columnDescriptors().addAll(defineArrearsStatusGadgetColumns());
        dmd.gadgets().add(arrearsStatusGadget);

        return dmd;

    }

    private List<ColumnDescriptorEntity> defineBuildingListerGadgetColumns() {

        BuildingDTO proto = EntityFactory.getEntityPrototype(BuildingDTO.class);

        return Arrays.asList(//@formatter:off
                defColumn(proto.complex()).visible(false).build(),
                defColumn(proto.propertyCode()).build(),
                defColumn(proto.propertyManager()).build(),
                defColumn(proto.marketing().name()).title(i18n.tr("Marketing Name")).build(),
                defColumn(proto.info().name()).build(),
                defColumn(proto.info().type()).build(),
                defColumn(proto.info().shape()).visible(false).build(),
                defColumn(proto.info().address().streetName()).visible(false).build(),
                defColumn(proto.info().address().city()).build(),
                defColumn(proto.info().address().province()).build(),
                defColumn(proto.info().address().country()).build()
        );//@formatter:on
    }

    private List<ColumnDescriptorEntity> defineArrearsStatusGadgetColumns() {
        LeaseArrearsSnapshotDTO proto = EntityFactory.getEntityPrototype(LeaseArrearsSnapshotDTO.class);

        return Arrays.asList(//@formatter:off
                defColumn(proto.billingAccount().lease().unit().belongsTo().propertyCode()).visible(true).build(),
                defColumn(proto.billingAccount().lease().unit().belongsTo().info().name()).title(i18n.tr("Building")).build(),
                defColumn(proto.billingAccount().lease().unit().belongsTo().info().address().streetNumber()).visible(false).build(),
                defColumn(proto.billingAccount().lease().unit().belongsTo().info().address().streetName()).visible(false).build(),                    
                defColumn(proto.billingAccount().lease().unit().belongsTo().info().address().province().name()).visible(false).title(i18n.tr("Province")).build(),                    
                defColumn(proto.billingAccount().lease().unit().belongsTo().info().address().country().name()).visible(false).title(i18n.tr("Country")).build(),                    
                defColumn(proto.billingAccount().lease().unit().belongsTo().complex().name()).visible(false).title(i18n.tr("Complex")).build(),
                defColumn(proto.billingAccount().lease().unit().info().number()).title(i18n.tr("Unit")).build(),
                defColumn(proto.billingAccount().lease().leaseId()).build(),
                defColumn(proto.billingAccount().lease().leaseFrom()).build(),
                defColumn(proto.billingAccount().lease().leaseTo()).build(),
                
                // arrears
                defColumn(proto.selectedBuckets().bucketCurrent()).build(),
                defColumn(proto.selectedBuckets().bucket30()).build(),
                defColumn(proto.selectedBuckets().bucket60()).build(),
                defColumn(proto.selectedBuckets().bucket90()).build(),
                defColumn(proto.selectedBuckets().bucketOver90()).build(),
                
                defColumn(proto.selectedBuckets().arrearsAmount()).build()
        //TODO calculate CREDIT AMOUNT                    
        //        column(proto.selectedBuckets().creditAmount()).build(),                    
        //        column(proto.selectedBuckets().totalBalance()).build(),
        //TODO calculate LMR                    
        //        column(proto.lmrToUnitRentDifference()).build()                   
        );//@formatter:on        
    }

    private List<ColumnDescriptorEntity> defineArreasSummaryGadgetColumns() {
        AgingBuckets proto = EntityFactory.create(AgingBuckets.class);

        return Arrays.asList(//@formatter:off
                defColumn(proto.bucketCurrent()).build(),
                defColumn(proto.bucket30()).build(),
                defColumn(proto.bucket60()).build(),
                defColumn(proto.bucket90()).build(),
                defColumn(proto.bucketOver90()).build(),
                defColumn(proto.arrearsAmount()).build()
//                column(proto.creditAmount()).build()
//                column(proto.totalBalance()).build()                
        );//@formatter:on
    }
}
