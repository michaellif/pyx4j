/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 16, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.propertyvista.crm.client.ui.gadgets.applications.ApplicationsGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.arrears.ArrearsGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.arrears.ArrearsStatusGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.arrears.ArrearsYOYAnalysisChartGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.availability.TurnoverAnalysisGraphGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.availability.UnitAvailabilityReportGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.availability.UnitAvailabilitySummaryGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.collections.CollectionsGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.common.IGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.common.IGadgetInstance;
import com.propertyvista.crm.client.ui.gadgets.demo.BarChart2DGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.demo.CounterGadgetDemoFactory;
import com.propertyvista.crm.client.ui.gadgets.demo.DemoGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.demo.GaugeGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.demo.LineChartGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.demo.PieChart2DGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.leadsandrentals.LeadsAndRentalsGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.leasexpiration.LeaseExpirationGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.maintenance.MaintenanceGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.notices.NoticesGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.other.BuildingListerGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.payments.PaymentRecordsGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.payments.PaymentsSummaryGadgetFactory;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public class GadgetFactory implements IGadgetFactory {

    private static List<IGadgetFactory> DIRECTORY = Arrays.asList(//@formatter:off            
            (IGadgetFactory) new ArrearsStatusGadgetFactory(),
            new ArrearsYOYAnalysisChartGadgetFactory(),
            new ArrearsGadgetFactory(),
                    
            new UnitAvailabilityReportGadgetFactory(),
            new UnitAvailabilitySummaryGadgetFactory(),
            new TurnoverAnalysisGraphGadgetFactory(),
            
            new PaymentRecordsGadgetFactory(),
            new PaymentsSummaryGadgetFactory(),
            
            new LeaseExpirationGadgetFactory(),
            new NoticesGadgetFactory(),
            new MaintenanceGadgetFactory(),
            new CollectionsGadgetFactory(),
            new LeadsAndRentalsGadgetFactory(),
            new ApplicationsGadgetFactory(),
            
            // DEMO GADGETS
            new BuildingListerGadgetFactory(),
            new BarChart2DGadgetFactory(),
            new DemoGadgetFactory(),
            new GaugeGadgetFactory(),
            new LineChartGadgetFactory(),
            new PieChart2DGadgetFactory(),
            new CounterGadgetDemoFactory()
    );//@formatter:on

    public Collection<? extends IGadgetFactory> getAvailableGadgets() {
        return DIRECTORY;
    }

    @Override
    public Class<? extends GadgetMetadata> getGadgetMetadataClass() {
        return null;
    }

    @Override
    public IGadgetInstance createGadget(GadgetMetadata gadgetMetadata) throws Error {
        if (gadgetMetadata == null) {
            return null;
        }
        for (IGadgetFactory g : DIRECTORY) {
            if (g.getGadgetMetadataClass().equals(gadgetMetadata.getInstanceValueClass())) {
                return g.createGadget(gadgetMetadata);
            }
        }
        return null;
    }

}
