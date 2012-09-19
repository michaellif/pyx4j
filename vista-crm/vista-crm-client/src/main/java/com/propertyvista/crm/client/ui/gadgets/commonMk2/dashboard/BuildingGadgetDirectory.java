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
import com.propertyvista.crm.client.ui.gadgets.arrears.ArrearsStatusGadget;
import com.propertyvista.crm.client.ui.gadgets.arrears.ArrearsSummaryGadget;
import com.propertyvista.crm.client.ui.gadgets.arrears.ArrearsYOYAnalysisChartGadget;
import com.propertyvista.crm.client.ui.gadgets.availability.TurnoverAnalysisGraphGadget;
import com.propertyvista.crm.client.ui.gadgets.availability.UnitAvailabilityReportGadget;
import com.propertyvista.crm.client.ui.gadgets.availability.UnitAvailabilitySummaryGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.collections.CollectionsGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.common.IGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.common.IGadgetInstance;
import com.propertyvista.crm.client.ui.gadgets.demo.BarChart2DGadget;
import com.propertyvista.crm.client.ui.gadgets.demo.CounterGadgetDemoFactory;
import com.propertyvista.crm.client.ui.gadgets.demo.DemoGadget;
import com.propertyvista.crm.client.ui.gadgets.demo.GaugeGadget;
import com.propertyvista.crm.client.ui.gadgets.demo.LineChartGadget;
import com.propertyvista.crm.client.ui.gadgets.demo.PieChart2DGadget;
import com.propertyvista.crm.client.ui.gadgets.leadsandrentals.LeadsAndRentalsGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.leasexpiration.LeaseExpirationGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.maintenance.MaintenanceGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.notices.NoticesGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.payments.PaymentRecordsGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.payments.PaymentsSummaryGadgetFactory;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public class BuildingGadgetDirectory implements IGadgetDirectory {

    private static List<IGadgetFactory> DIRECTORY = Arrays.asList(//@formatter:off            
            (IGadgetFactory) new ArrearsStatusGadget(),            
            new ArrearsSummaryGadget(),
            new ArrearsYOYAnalysisChartGadget(),
            new ArrearsGadgetFactory(),
                    
            new UnitAvailabilityReportGadget(),
            new UnitAvailabilitySummaryGadgetFactory(),
            new TurnoverAnalysisGraphGadget(),
            
            new PaymentRecordsGadgetFactory(),
            new PaymentsSummaryGadgetFactory(),
            
            new LeaseExpirationGadgetFactory(),
            new NoticesGadgetFactory(),
            new MaintenanceGadgetFactory(),
            new CollectionsGadgetFactory(),
            new LeadsAndRentalsGadgetFactory(),
            new ApplicationsGadgetFactory(),
            
            // DEMO GADGETS
            new BarChart2DGadget(),
            new DemoGadget(),
            new GaugeGadget(),
            new LineChartGadget(),
            new PieChart2DGadget(),
            new CounterGadgetDemoFactory()
    );//@formatter:on

    @Override
    public IGadgetInstance createGadgetInstance(GadgetMetadata gadgetMetadata) {
        if (gadgetMetadata == null) {
            return null;
        }
        String requestedGadgetType = gadgetMetadata.cast().getObjectClass().getName();
        for (IGadgetFactory g : DIRECTORY) {
            if (g.getType().equals(requestedGadgetType)) {
                return g.createGadget(gadgetMetadata);
            }
        }
        return null;
    }

    @Override
    public Collection<? extends IGadgetFactory> getAvailableGadgets() {
        return DIRECTORY;
    }

}
