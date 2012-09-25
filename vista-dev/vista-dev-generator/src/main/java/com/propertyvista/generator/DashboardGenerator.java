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
package com.propertyvista.generator;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.ISharedUserEntity;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;
import com.propertyvista.domain.dashboard.gadgets.type.ApplicationsGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsStatusGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsYOYAnalysisChartGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.BuildingListerGadgetMetadata;
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
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.server.common.gadgets.GadgetMetadataFactory;

@SuppressWarnings("unchecked")
public class DashboardGenerator extends Dashboards {

    private final static I18n i18n = I18n.get(DashboardGenerator.class);

    public DashboardGenerator() {
        systemDashboards.add(makeDefaultMiscDashboard());
        systemDashboards.add(makeDefaultSystemDashboard());
        systemDashboards.add(makeDefaultUnitAvailabilityDashboard());
        systemDashboards.add(makeDefaultArrearsDashboard());
        systemDashboards.add(makeDefaultPaymentsDashboard());
    }

    private DashboardMetadata makeDefaultArrearsDashboard() {
        return makeDashboard(//@formatter:off
                i18n.tr("Arrears"),
                i18n.tr("Contains various arrears gadgets"),                
                ArrearsGadgetMetadata.class,
                ArrearsYOYAnalysisChartGadgetMetadata.class,
                ArrearsStatusGadgetMetadata.class
        );//@formatter:on    
    }

    private DashboardMetadata makeDefaultMiscDashboard() {
        return makeDashboard(//@formatter:off
                i18n.tr("Misc"),
                i18n.tr("Contains various gadgets"),
                LeaseExpirationGadgetMetadata.class,
                NoticesGadgetMetadata.class,                
                MaintenanceGadgetMetadata.class,                
                CollectionsGadgetMetadata.class,                
                LeadsAndRentalsGadgetMetadata.class,                
                ApplicationsGadgetMetadata.class
        );//@formatter:on    
    }

    private DashboardMetadata makeDefaultUnitAvailabilityDashboard() {
        return makeDashboard(//@formatter:off
                i18n.tr("Availablility"),
                i18n.tr("Contains various availability gadgets"),                
                UnitAvailabilityGadgetMetadata.class,
                UnitAvailabilitySummaryGadgetMetadata.class,
                UnitTurnoverAnalysisGadgetMetadata.class
        );//@formatter:on    
    }

    private DashboardMetadata makeDefaultPaymentsDashboard() {
        return makeDashboard(//@formatter:off
                i18n.tr("Payments"),
                i18n.tr("Contains various gadgets regarding payments"),                
                PaymentRecordsGadgetMetadata.class,
                PaymentsSummaryGadgetMetadata.class                
        );//@formatter:on
    }

    private DashboardMetadata makeDefaultSystemDashboard() {
        return makeSystemDashboard(//@formatter:off
                i18n.tr("System"),
                i18n.tr("Displays default system data"),
                BuildingListerGadgetMetadata.class                
        );//@formatter:on

    }

    private DashboardMetadata makeDashboard(String name, String description, Class<? extends GadgetMetadata>... gadgetMetadatas) {
        DashboardMetadata dmd = EntityFactory.create(DashboardMetadata.class);
        dmd.user().id().setValue(ISharedUserEntity.DORMANT_KEY); // shared for everyone usage 
        dmd.type().setValue(DashboardType.building);
        dmd.isShared().setValue(true);
        dmd.name().setValue(name);
        dmd.description().setValue(description);
        dmd.layoutType().setValue(LayoutType.One);
        for (Class<? extends GadgetMetadata> gadgetMetadata : gadgetMetadatas) {
            dmd.gadgets().add(GadgetMetadataFactory.createGadgetMetadata(gadgetMetadata));
        }
        return dmd;
    }

    private DashboardMetadata makeSystemDashboard(String name, String description, Class<? extends GadgetMetadata>... gadgetMetadatas) {
        DashboardMetadata dm = makeDashboard(name, description, gadgetMetadatas);
        dm.type().setValue(DashboardType.system);
        return dm;
    }

}
