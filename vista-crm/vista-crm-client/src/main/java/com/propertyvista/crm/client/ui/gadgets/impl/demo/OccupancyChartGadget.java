/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 27, 2012
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.gadgets.impl.demo;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Overflow;
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
import com.pyx4j.svg.gwt.basic.SvgFactoryForGwt;

import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.domain.dashboard.gadgets.type.demo.OccupancyChartGadgetMetadata;

public class OccupancyChartGadget extends GadgetInstanceBase<OccupancyChartGadgetMetadata> {

    private SimplePanel panel;

    public OccupancyChartGadget(OccupancyChartGadgetMetadata gmd) {
        super(gmd, com.propertyvista.domain.dashboard.gadgets.type.demo.OccupancyChartGadgetMetadata.class);
        setDefaultPopulator(new Populator() {
            @Override
            public void populate() {
                populateSucceded();
            }
        });
    }

    @Override
    public Widget initContentPanel() {
        panel = new SimplePanel();

        DataSource ds = new DataSource();
        List<Double> values = new ArrayList<Double>(5);
        values.add(80d);
        values.add(60d);
        values.add(10d);
        ds.addDataSet(ds.new Metric("emerson1206"), values);

        values = new ArrayList<Double>(5);
        values.add(80d);
        values.add(58d);
        values.add(35d);
        ds.addDataSet(ds.new Metric("commercial1428"), values);

        values = new ArrayList<Double>(5);
        values.add(38d);
        values.add(54d);
        values.add(13d);
        ds.addDataSet(ds.new Metric("chainlake201"), values);

        values = new ArrayList<Double>(5);
        values.add(38d);
        values.add(30d);
        values.add(41d);
        ds.addDataSet(ds.new Metric("hollis1568"), values);

        List<String> sd = new ArrayList<String>(3);
        sd.add("Occupancy");
        sd.add("Notices");
        sd.add("Vacant");
        ds.setSeriesDescription(sd);

        SvgFactory factory = new SvgFactoryForGwt();

        GridBasedChartConfigurator config = new GridBasedChartConfigurator(factory, ds, 600, 200);
        config.setLegend(true);
        config.setGridType(GridType.Both);
        config.setChartColors(ChartTheme.bright);

        SvgRoot svgroot = factory.getSvgRoot();
        svgroot.add(new BarChart2D(config));

        panel.add((Widget) svgroot);
        panel.setSize("600px", "200px");
        panel.getElement().getStyle().setOverflow(Overflow.HIDDEN);

        ScrollPanel scroll = new ScrollPanel(panel);
        scroll.setWidth("100%");

        return scroll;
    }
}