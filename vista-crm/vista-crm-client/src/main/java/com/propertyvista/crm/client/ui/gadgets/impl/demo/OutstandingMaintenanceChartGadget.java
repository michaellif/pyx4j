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
 * @version $Id$
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
import com.pyx4j.svg.chart.ArcBasedChartConfigurator;
import com.pyx4j.svg.chart.ChartTheme;
import com.pyx4j.svg.chart.DataSource;
import com.pyx4j.svg.chart.PieChart2D;
import com.pyx4j.svg.gwt.basic.SvgFactoryForGwt;

import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.domain.dashboard.gadgets.type.demo.OutstandingMaintenanceChartGadgetMetadata;

public class OutstandingMaintenanceChartGadget extends
        GadgetInstanceBase<com.propertyvista.domain.dashboard.gadgets.type.demo.OutstandingMaintenanceChartGadgetMetadata> {

    private SimplePanel panel;

    public OutstandingMaintenanceChartGadget(OutstandingMaintenanceChartGadgetMetadata gmd) {
        super(gmd, com.propertyvista.domain.dashboard.gadgets.type.demo.OutstandingMaintenanceChartGadgetMetadata.class);
        setDefaultPopulator(new Populator() {
            @Override
            public void populate() {
                populateSucceded();
            }
        });
    }

    @Override
    public boolean isFullWidth() {
        return false;
    }

    @Override
    public Widget initContentPanel() {
        // TODO Auto-generated method stub
        panel = new SimplePanel();

        DataSource ds = new DataSource();
        List<Double> values = new ArrayList<Double>(5);
        values.add(7d);
        ds.addDataSet(ds.new Metric("Under 24h"), values);

        values = new ArrayList<Double>(5);
        values.add(13d);
        ds.addDataSet(ds.new Metric("Today"), values);

        values = new ArrayList<Double>(5);
        values.add(35d);
        ds.addDataSet(ds.new Metric("5 days"), values);

        values = new ArrayList<Double>(5);
        values.add(41d);
        ds.addDataSet(ds.new Metric("2 weeks"), values);

        values = new ArrayList<Double>(5);
        values.add(80d);
        ds.addDataSet(ds.new Metric("1 month"), values);

        SvgFactory factory = new SvgFactoryForGwt();

        ArcBasedChartConfigurator config = new ArcBasedChartConfigurator(factory, ds);
        config.setLegend(true);
        config.setChartColors(ChartTheme.bright);
        config.setRadius(65);

        SvgRoot svgroot = factory.getSvgRoot();
        svgroot.add(new PieChart2D(config));

        panel.add((Widget) svgroot);
        panel.setSize("300px", "150px");
        panel.getElement().getStyle().setOverflow(Overflow.HIDDEN);

        ScrollPanel scroll = new ScrollPanel(panel);
        scroll.setWidth("100%");
        return scroll;
    }
}