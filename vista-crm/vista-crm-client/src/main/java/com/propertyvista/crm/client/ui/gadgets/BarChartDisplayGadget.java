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

import java.util.Arrays;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.crm.rpc.domain.GadgetMetadata;
import com.propertyvista.crm.rpc.domain.GadgetMetadata.GadgetType;

import com.pyx4j.widgets.client.svg.BarChart;
import com.pyx4j.widgets.client.svg.BarChartModel;
import com.pyx4j.widgets.client.svg.BarChartModel.BarChartItem;
import com.pyx4j.widgets.client.svg.SvgPanel;

public class BarChartDisplayGadget extends GadgetBase {

    private final SvgPanel activeOrdersPanel = new SvgPanel();

    public BarChartDisplayGadget(GadgetMetadata gmd) {
        super(gmd);

        BarChartModel activeOrdersBarModel = new BarChartModel(Arrays.asList(new String[] { "2008", "2009", "2010" }));
        activeOrdersBarModel.addItem(new BarChartItem(3, "s1"), "2008");
        activeOrdersBarModel.addItem(new BarChartItem(5, "s2"), "2008");
        activeOrdersBarModel.addItem(new BarChartItem(7, "s3"), "2009");
        activeOrdersBarModel.addItem(new BarChartItem(9, "s4"), "2009");
        activeOrdersBarModel.addItem(new BarChartItem(11, "s4"), "2010");

        BarChart activeOrdersChart = new BarChart(activeOrdersBarModel, 300, 100);
        activeOrdersPanel.add(activeOrdersChart);
        activeOrdersPanel.setHeight("150px");
    }

    @Override
    protected void selfInit(GadgetMetadata gmd) {
        gmd.type().setValue(GadgetType.BarChartDisplay);
        gmd.name().setValue("Bar Chart Display Gadget");
    }

    @Override
    public Widget getWidget() {
        ScrollPanel scroll = new ScrollPanel(activeOrdersPanel);
        scroll.setWidth("100%");
        return scroll;
    }
}
