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

import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata.GadgetType;

public class GadgetsFactory {

    public static IGadgetBase createGadget(GadgetType type, GadgetMetadata metaData) {
        switch (type) {
        case Demo:
            return new DemoGadget(metaData);
        case BuildingLister:
            return new BuildingListerGadget(metaData);
        case LineChartDisplay:
            return new LineChartGadget(metaData);
        case BarChartDisplay:
            return new BarChart2DGadget(metaData); //new BarChartDisplayGadget(metaData);
        case PieChartDisplay:
            return new PieChart2DGadget(metaData); //PieChartDisplayGadget(metaData);
        }
        return null;
    }

    public static String getGadgetTypeDescription(GadgetType type) {
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
        }
        return "";
    }
}
