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

import java.util.Arrays;
import java.util.List;

import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.crm.client.ui.gadgets.arrears.ArrearsStatusGadget;
import com.propertyvista.crm.client.ui.gadgets.arrears.ArrearsSummaryGadget;
import com.propertyvista.crm.client.ui.gadgets.arrears.ArrearsYOYAnalysisChartGadget;
import com.propertyvista.crm.client.ui.gadgets.availabilityreport.AvailabiltySummaryGadget;
import com.propertyvista.crm.client.ui.gadgets.availabilityreport.TurnoverAnalysisGraphGadget;
import com.propertyvista.crm.client.ui.gadgets.availabilityreport.UnitAvailabilityReportGadget;
import com.propertyvista.crm.client.ui.gadgets.other.BuildingListerGadget;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;

/** Global container of all possible gadgets */
public class Directory {
    public enum Categories {
        Availability, Arrears, Chart, Buildings;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

    }

    //@formatter:off    
    public static List<IGadgetFactory> DIRECTORY = Arrays.asList(
        (IGadgetFactory)new BuildingListerGadget(),
        
        new ArrearsStatusGadget(),
        new ArrearsSummaryGadget(),
        new ArrearsYOYAnalysisChartGadget(),
        
        new UnitAvailabilityReportGadget(),
        new AvailabiltySummaryGadget(),
        new TurnoverAnalysisGraphGadget()
    );
    //@formatter:on

    public static IGadgetInstanceBase createGadget(GadgetMetadata gadgetMetadata) {
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
