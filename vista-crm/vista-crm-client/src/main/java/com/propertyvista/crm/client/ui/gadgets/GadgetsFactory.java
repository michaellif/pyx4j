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

import com.propertyvista.crm.client.ui.gadgets.arrears.ArrearsSummaryGadget;
import com.propertyvista.crm.client.ui.gadgets.arrears.ArrearsYOYAnalysisChart;
import com.propertyvista.crm.client.ui.gadgets.arrears.OtherArrearsListerGadget;
import com.propertyvista.crm.client.ui.gadgets.arrears.ParkingArrearsListerGadget;
import com.propertyvista.crm.client.ui.gadgets.arrears.RentArrearsListerGadget;
import com.propertyvista.crm.client.ui.gadgets.arrears.TotalArrearsListerGadget;
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

        GadgetType.RentArrearsGadget,
        GadgetType.ParkingArrearsGadget,
        GadgetType.OtherArrearsGadget,
        GadgetType.TotalArrearsGadget,
        GadgetType.ArrearsSummaryGadget,
        GadgetType.ArrearsARBalanceComparisonChart,
    };
    
    private static GadgetType[] deprecatedGadgets =
    {
        GadgetType.DeprecatedUnitAvailabilityReport,
        GadgetType.DeprecatedAvailabilitySummary,
        GadgetType.DeprecatedTurnoverAnalysisGraph,
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

        case DeprecatedUnitAvailabilityReport:
            return new UnitAvailabilityReportGadget(metaData);
        case DeprecatedAvailabilitySummary:
            return new AvailabiltySummaryGadget(metaData);
        case DeprecatedTurnoverAnalysisGraph:
            return new TurnoverAnalysisGraphGadget(metaData);

        case UnitAvailabilityReport:
            return new com.propertyvista.crm.client.ui.gadgets.availabilityreport.UnitAvailabilityReportGadget(metaData);
        case AvailabilitySummary:
            return new com.propertyvista.crm.client.ui.gadgets.availabilityreport.AvailabiltySummaryGadget(metaData);
        case TurnoverAnalysisGraph:
            return new com.propertyvista.crm.client.ui.gadgets.availabilityreport.TurnoverAnalysisGraphGadget(metaData);

        case RentArrearsGadget:
            return new RentArrearsListerGadget(metaData);
        case ParkingArrearsGadget:
            return new ParkingArrearsListerGadget(metaData);
        case OtherArrearsGadget:
            return new OtherArrearsListerGadget(metaData);
        case TotalArrearsGadget:
            return new TotalArrearsListerGadget(metaData);
        case ArrearsSummaryGadget:
            return new ArrearsSummaryGadget(metaData);
        case ArrearsARBalanceComparisonChart:
            return new ArrearsYOYAnalysisChart(metaData);
        default:
            return null;
        }
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
            return "Shows the information about units, whether they are available or rented, how long they have been vacant for and revenue lost as a result. Can be customized to show various information about buildings and units, for example their physical condition.";
        case AvailabilitySummary:
            return "Shows a summary of information about all units, including the total number of units, vacancy, notice and net exposure information in both percentages and quantity.";
        case TurnoverAnalysisGraph:
            return "A graph that visually demonstrates the turnover rate in either percentage or quantity over the course of multiple years";

        case TotalArrearsGadget:
            return "Shows the information about total tenant arrears, including how long it is overdue, total balance, legal status information etc.";
        case ArrearsARBalanceComparisonChart:
            return "A graph that visually demonstrates the arrear balance each month over the course of multiple years";
        case RentArrearsGadget:
            return "Shows the information about tenant rent arrears, including how long it is overdue, total balance, legal status information etc.";
        case ParkingArrearsGadget:
            return "Shows the information about tenant parking arrears, including how long it is overdue, total balance, legal status information etc.";
        case OtherArrearsGadget:
            return "Shows the information about tenant other arrears, including how long it is overdue, total balance, legal status information etc.";
        case ArrearsSummaryGadget:
            return "Shows a short summary of the total arrears";

            // TODO add description for rest of the gadgets
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
        return answer && !contains(deprecatedGadgets, gadget);
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
