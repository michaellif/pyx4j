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
package com.propertyvista.crm.client.ui.gadgets;

import com.propertyvista.crm.client.ui.gadgets.arrears.ArrearsListerGadget;
import com.propertyvista.crm.client.ui.gadgets.vacancyreport.AvailabiltySummaryGadget;
import com.propertyvista.crm.client.ui.gadgets.vacancyreport.TurnoverAnalysisGraphGadget;
import com.propertyvista.crm.client.ui.gadgets.vacancyreport.UnitAvailabilityReportGadget;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata.GadgetType;

/*
 * In order to work properly in VISTA environment every  
 * gadget should be registered here in 3 places:
 *      1. Compatibility lists;
 *      2  Creation routine;
 *      3  Description.
 */
public class GadgetsFactory {

    /*
     * Gadgets<->Dashboards(Reports) authorisation lists:
     */

    //@formatter:off
    private static GadgetType[] systemDashboardGadgets = 
    {
        GadgetType.Demo,
        GadgetType.BuildingLister,
        GadgetType.LineChartDisplay,
        GadgetType.BarChartDisplay,
        GadgetType.PieChartDisplay,
        GadgetType.GaugeDisplay,        
    };
    private static GadgetType[] buildingDashboardGadgets = 
    {
        GadgetType.Demo,
        GadgetType.BarChartDisplayBuilding,
        GadgetType.PieChartDisplayBuilding,
        GadgetType.GaugeDisplay,
        
        GadgetType.UnitAvailabilityReport,
        GadgetType.AvailabilitySummary,
        GadgetType.TurnoverAnalysisGraph,
        
        GadgetType.UnitAvailabilityReportMk2,
        GadgetType.AvailabilitySummaryMk2,
        GadgetType.TurnoverAnalysisGraphMk2,

        GadgetType.ArrearsGadget,
    };
    //@formatter:on

    /*
     * Gadgets creation:
     */
    public static IGadgetBase createGadget(GadgetType type, GadgetMetadata metaData) {
        switch (type) {
        case Demo:
            return new DemoGadget(metaData);
        case BuildingLister:
            return new BuildingListerGadget(metaData);
        case LineChartDisplay:
            return new LineChartGadget(metaData);
        case BarChartDisplay:
            return new BarChart2DGadget(metaData);
        case PieChartDisplay:
            return new PieChart2DGadget(metaData);
        case BarChartDisplayBuilding:
            return new BarChart2DGadget(metaData);
        case PieChartDisplayBuilding:
            return new PieChart2DGadget(metaData);
        case GaugeDisplay:
            return new GaugeGadget(metaData);

        case UnitAvailabilityReport:
            return new UnitAvailabilityReportGadget(metaData);
        case AvailabilitySummary:
            return new AvailabiltySummaryGadget(metaData);
        case TurnoverAnalysisGraph:
            return new TurnoverAnalysisGraphGadget(metaData);

        case UnitAvailabilityReportMk2:
            return new com.propertyvista.crm.client.ui.gadgets.availabilityreport.UnitAvailabilityReportGadget(metaData);
        case AvailabilitySummaryMk2:
            return new com.propertyvista.crm.client.ui.gadgets.availabilityreport.AvailabiltySummaryGadget(metaData);
        case TurnoverAnalysisGraphMk2:
            return new com.propertyvista.crm.client.ui.gadgets.availabilityreport.TurnoverAnalysisGraphGadget(metaData);

        case ArrearsGadget:
            return new ArrearsListerGadget(metaData);
        }
        return null;
    }

    /*
     * Gadgets description:
     */
    public static String getGadgetDescription(GadgetType type) {
        switch (type) {
        case Test:
            return "There is no such gadget - do not select it, sorry ;o)...";
        case Demo:
            return "Demo gadget to demonstrate basic gadget/dashboard functionality...";
        case BuildingLister:
            return "Table-list-like gadget which displays building data according to prefered rules. Query and display data can be set up";
        case BarChartDisplay:
            return "Gadget intended to demonstrate Bar Chart display functionality...";
        case PieChartDisplay:
            return "Gadget intended to demonstrate Pie Chart display functionality...";
        case LineChartDisplay:
            return "Gadget intended to demonstrate Line Chart display functionality...";
        case BarChartDisplayBuilding:
            return "Gadget intended to demonstrate Bar Chart display (Building only!)";
        case PieChartDisplayBuilding:
            return "Gadget intended to demonstrate Pie Chart display (Building only!)";
        case GaugeDisplay:
            return "Gadget intended to demonstrate Gadget display functionality...";
        case UnitAvailabilityReport:
            return "Gadget intended to demonstrate Units Vacancy Report/Status...";
        case AvailabilitySummary:
            return "Gadget intended to demonstrate Occupancy/Vacancy Summary...";
        case TurnoverAnalysisGraph:
            return "Gadget intended to demonstrate Unit Turnover graph...";
        case ArrearsGadget:
            return "Gadget intended to demonstrate Tenant's arrears...";
        }
        return "";
    }

    // internals:
    public static boolean isGadgetAllowed(GadgetType gadget, DashboardType dashboard) {
        boolean answer = false;
        switch (dashboard) {
        case system:
            answer = contains(systemDashboardGadgets, gadget);
            break;
        case building:
            answer = contains(buildingDashboardGadgets, gadget);
            break;
        }
        return answer;
    }

    private static boolean contains(GadgetType[] array, GadgetType gadget) {
        for (GadgetType type : array) {
            if (type == gadget) {
                return true;
            }
        }
        return false;
    }
}
