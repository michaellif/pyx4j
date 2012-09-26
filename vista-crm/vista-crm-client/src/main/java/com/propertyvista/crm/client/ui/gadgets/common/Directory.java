/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.common;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.crm.client.ui.gadgets.applications.ApplicationsGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.arrears.ArrearsGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.arrears.ArrearsStatusGadget;
import com.propertyvista.crm.client.ui.gadgets.arrears.ArrearsYOYAnalysisChartGadget;
import com.propertyvista.crm.client.ui.gadgets.availability.TurnoverAnalysisGraphGadget;
import com.propertyvista.crm.client.ui.gadgets.availability.UnitAvailabilityReportGadget;
import com.propertyvista.crm.client.ui.gadgets.availability.UnitAvailabilitySummaryGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.collections.CollectionsGadgetFactory;
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
import com.propertyvista.crm.client.ui.gadgets.other.BuildingListerGadget;
import com.propertyvista.crm.client.ui.gadgets.payments.PaymentRecordsGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.payments.PaymentsSummaryGadgetFactory;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

/** Global container of all possible gadgets. */
@Deprecated
public class Directory {

    public enum Categories {

        Availability, Arrears, Chart, Buildings, Payments, Leases, Notices, Maintenance, Leads, Rentals, Demo, Applications;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

    }

    public static List<IGadgetFactory> DIRECTORY = Arrays.asList(//@formatter:off
        (IGadgetFactory)new BuildingListerGadget(),
        
        new ArrearsGadgetFactory(),
        new ArrearsStatusGadget(),
        new ArrearsYOYAnalysisChartGadget(),
                
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
        
        new BarChart2DGadget(),
        new DemoGadget(),
        new GaugeGadget(),
        new LineChartGadget(),
        new PieChart2DGadget(),
        new CounterGadgetDemoFactory()
    );//@formatter:on

    public static IGadgetInstance createGadget(GadgetMetadata gadgetMetadata) {
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
}
