/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 7, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.SvgRoot;
import com.pyx4j.svg.chart.BarChart2D;
import com.pyx4j.svg.chart.ChartTheme;
import com.pyx4j.svg.chart.DataSource;
import com.pyx4j.svg.chart.GridBasedChartConfigurator;
import com.pyx4j.svg.chart.GridBasedChartConfigurator.GridType;
import com.pyx4j.svg.gwt.SvgFactoryForGwt;

import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata.GadgetType;

public class BarChart2DGadget extends GadgetBase {

    private final SimplePanel panel;

    public BarChart2DGadget(GadgetMetadata gmd) {
        super(gmd);
        panel = new SimplePanel();

        DataSource ds = new DataSource();
        List<Double> values = new ArrayList<Double>(5);
        values.add(80d);
        values.add(60d);
        values.add(10d);
        ds.addDataSet(ds.new Metric("Building 1"), values);

        values = new ArrayList<Double>(5);
        values.add(80d);
        values.add(58d);
        values.add(35d);
        ds.addDataSet(ds.new Metric("Building 2"), values);

        values = new ArrayList<Double>(5);
        values.add(38d);
        values.add(54d);
        values.add(13d);
        ds.addDataSet(ds.new Metric("Building 3"), values);

        values = new ArrayList<Double>(5);
        values.add(38d);
        values.add(30d);
        values.add(41d);
        ds.addDataSet(ds.new Metric("Building 4"), values);

        values = new ArrayList<Double>(5);
        values.add(28d);
        values.add(30d);
        values.add(7d);
        ds.addDataSet(ds.new Metric("Building 5"), values);

        values = new ArrayList<Double>(6);
        values.add(18d);
        values.add(60d);
        values.add(17d);
        ds.addDataSet(ds.new Metric("Building 6"), values);

        List<String> sd = new ArrayList<String>(3);
        sd.add("2008");
        sd.add("2009");
        sd.add("2010");
        ds.setSeriesDescription(sd);

        SvgFactory factory = new SvgFactoryForGwt();

        GridBasedChartConfigurator config = new GridBasedChartConfigurator(factory, ds, 700, 200);
        config.setLegend(true);
        config.setGridType(GridType.Both);
        config.setTheme(ChartTheme.Bright);

        SvgRoot svgroot = factory.getSvgRoot();
        svgroot.add(new BarChart2D(config));

        panel.add((Widget) svgroot);
        panel.setSize("700px", "200px");

    }

    @Override
    protected void selfInit(GadgetMetadata gmd) {
        gmd.type().setValue(GadgetType.BarChartDisplay);
        gmd.name().setValue("Bar Chart 2D Display Gadget");

    }

    @Override
    public Widget asWidget() {
        ScrollPanel scroll = new ScrollPanel(panel);
        scroll.setWidth("100%");
        return scroll;
    }

}
