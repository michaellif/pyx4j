/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-28
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets;

import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.crm.rpc.domain.GadgetMetadata;
import com.propertyvista.crm.rpc.domain.GadgetMetadata.GadgetType;

import com.pyx4j.widgets.client.svg.PieChart;
import com.pyx4j.widgets.client.svg.PieChartModel;
import com.pyx4j.widgets.client.svg.PieChartModel.PieChartSegment;
import com.pyx4j.widgets.client.svg.SvgPanel;

public class PieChartDisplayGadget extends GadgetBase {

    private final SvgPanel installerPanel = new SvgPanel();

    public PieChartDisplayGadget(GadgetMetadata gmd) {
        super(gmd);

        PieChartModel installerChartModel = new PieChartModel();
        installerChartModel.addSegment(new PieChartSegment(3, "s1", "#bbb"));
        installerChartModel.addSegment(new PieChartSegment(5, "s2", "#222"));
        installerChartModel.addSegment(new PieChartSegment(7, "s3", "#ddd"));
        installerChartModel.addSegment(new PieChartSegment(9, "s4", "#999"));
        installerChartModel.addSegment(new PieChartSegment(11, "s5", "#555"));

        PieChart installerChart = new PieChart(installerChartModel, 60);

        installerPanel.add(installerChart);
        installerPanel.setHeight("150px");

    }

    @Override
    protected void selfInit(GadgetMetadata gmd) {
        gmd.type().setValue(GadgetType.PieChartDisplay);
        gmd.name().setValue("Pie Chart Display Gadget");
    }

    @Override
    public Widget asWidget() {
        return installerPanel;
    }
}
