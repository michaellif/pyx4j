/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-25
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.dashboard;

import java.util.Arrays;

import com.propertyvista.domain.dashboard.gadgets.type.AccessDeniedGagetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.ApplicationsGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsStatusGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsSummaryGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsYOYAnalysisChartGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.BuildingListerGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.BuildingResidentInsuranceCoverageGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.CollectionsGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.LeadsAndRentalsGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.LeaseExpirationGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.MaintenanceGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.NoticesGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.PaymentRecordsGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.PaymentsSummaryGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailabilityGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailabilitySummaryGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.UnitTurnoverAnalysisGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.demo.BarChart2DGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.demo.CounterGadgetDemoGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.demo.DemoGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.demo.GaugeGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.demo.LineChartGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.demo.OccupancyChartGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.demo.OutstandingMaintenanceChartGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.demo.PieChart2DGadgetMetadata;
import com.propertyvista.server.common.gadgets.defaultsettings.ArrearsStatusGadgetMetadataDefaultSettings;
import com.propertyvista.server.common.gadgets.defaultsettings.ArrearsYOYAnalysisChartGadgetMetadataDefaultSettings;
import com.propertyvista.server.common.gadgets.defaultsettings.GadgetMetadataCommonDefaultSettings;
import com.propertyvista.server.common.gadgets.defaultsettings.PaymentRecordsGadgetMetadataDefaultSettings;
import com.propertyvista.server.common.gadgets.defaultsettings.PaymentsSummaryGadgetMetadataDefaultSettings;
import com.propertyvista.server.common.gadgets.defaultsettings.UnitAvailabilityGadgetMetadataDefaultSettings;
import com.propertyvista.server.common.gadgets.defaultsettings.UnitTurnoverAnalysisGadgetMetadataDefaultSettings;

public final class GadgetMetadataRepositoryFacadeImpl extends GadgetMetadataRepositoryFacadeBase implements GadgetMetadataRepositoryFacade {

    private static GadgetMetadataRepositoryFacadeImpl INSTANCE = new GadgetMetadataRepositoryFacadeImpl();

    private GadgetMetadataRepositoryFacadeImpl() {
        // bind gadget metadata settings here
        super(Arrays.<GadgetDefaultSettingsBinding<?>> asList(//@formatter:off
                
                bind(ArrearsSummaryGadgetMetadata.class, GadgetMetadataCommonDefaultSettings.class),
                bind(ArrearsStatusGadgetMetadata.class, ArrearsStatusGadgetMetadataDefaultSettings.class),
                bind(ArrearsYOYAnalysisChartGadgetMetadata.class, ArrearsYOYAnalysisChartGadgetMetadataDefaultSettings.class),
                
                bind(PaymentRecordsGadgetMetadata.class, PaymentRecordsGadgetMetadataDefaultSettings.class),
                bind(PaymentsSummaryGadgetMetadata.class, PaymentsSummaryGadgetMetadataDefaultSettings.class),
                
                bind(UnitAvailabilityGadgetMetadata.class, UnitAvailabilityGadgetMetadataDefaultSettings.class),
                bind(UnitAvailabilitySummaryGadgetMetadata.class, GadgetMetadataCommonDefaultSettings.class),
                bind(UnitTurnoverAnalysisGadgetMetadata.class, UnitTurnoverAnalysisGadgetMetadataDefaultSettings.class),
                
                bind(LeaseExpirationGadgetMetadata.class, GadgetMetadataCommonDefaultSettings.class),
                bind(NoticesGadgetMetadata.class, GadgetMetadataCommonDefaultSettings.class),
                bind(MaintenanceGadgetMetadata.class, GadgetMetadataCommonDefaultSettings.class),
                bind(CollectionsGadgetMetadata.class, GadgetMetadataCommonDefaultSettings.class),
                bind(LeadsAndRentalsGadgetMetadata.class, GadgetMetadataCommonDefaultSettings.class),
                bind(ApplicationsGadgetMetadata.class, GadgetMetadataCommonDefaultSettings.class),
                
                bind(BuildingResidentInsuranceCoverageGadgetMetadata.class, GadgetMetadataCommonDefaultSettings.class),
                
                // STUPID GADGETS
                bind(BuildingListerGadgetMetadata.class, GadgetMetadataCommonDefaultSettings.class),                
                
                // DEMO GADGETS (in lexicographic order)
                bind(BarChart2DGadgetMetadata.class, GadgetMetadataCommonDefaultSettings.class),
                bind(CounterGadgetDemoGadgetMetadata.class, GadgetMetadataCommonDefaultSettings.class),
                bind(DemoGadgetMetadata.class, GadgetMetadataCommonDefaultSettings.class),
                bind(GaugeGadgetMetadata.class, GadgetMetadataCommonDefaultSettings.class),
                bind(LineChartGadgetMetadata.class, GadgetMetadataCommonDefaultSettings.class),
                bind(OccupancyChartGadgetMetadata.class, GadgetMetadataCommonDefaultSettings.class),
                bind(OutstandingMaintenanceChartGadgetMetadata.class, GadgetMetadataCommonDefaultSettings.class),
                bind(PieChart2DGadgetMetadata.class, GadgetMetadataCommonDefaultSettings.class),
                
                // SPECIAL GADGETS                
                bind(AccessDeniedGagetMetadata.class, GadgetMetadataCommonDefaultSettings.class)
        ));//@formatter:on
    }

    public static GadgetMetadataRepositoryFacadeImpl get() {
        return INSTANCE;
    }

}
