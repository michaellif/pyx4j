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
import com.pyx4j.svg.chart.ArcBasedChartConfigurator;
import com.pyx4j.svg.chart.ChartTheme;
import com.pyx4j.svg.chart.DataSource;
import com.pyx4j.svg.chart.Gauge;
import com.pyx4j.svg.gwt.basic.SvgFactoryForGwt;

import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.domain.dashboard.gadgets.type.demo.GaugeGadgetMetadata;

public class GaugeGadget extends GadgetInstanceBase<com.propertyvista.domain.dashboard.gadgets.type.demo.GaugeGadgetMetadata> {

    private SimplePanel panel;

    public GaugeGadget(GaugeGadgetMetadata gmd) {
        super(gmd, com.propertyvista.domain.dashboard.gadgets.type.demo.GaugeGadgetMetadata.class);
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
        panel = new SimplePanel();
        DataSource ds = new DataSource();
        List<Double> values = new ArrayList<Double>(1);
        values.add(32d);
        ds.addDataSet(ds.new Metric("Building 1"), values);
        ds.addSeriesDescription("2008");

        SvgFactory factory = new SvgFactoryForGwt();

        ArcBasedChartConfigurator config = new ArcBasedChartConfigurator(factory, ds);
        config.setScaleMaximum(100d);
        config.setChartColors(ChartTheme.bright);
        config.setRadius(80);
        config.setShowValueLabels(true);

        SvgRoot svgroot = factory.getSvgRoot();
        svgroot.add(new Gauge(config));

        panel.add((Widget) svgroot);
        panel.setSize("300px", "200px");
        panel.getElement().getStyle().setOverflow(Overflow.HIDDEN);

        ScrollPanel scroll = new ScrollPanel(panel);
        scroll.setWidth("100%");

        return scroll;
    }
}