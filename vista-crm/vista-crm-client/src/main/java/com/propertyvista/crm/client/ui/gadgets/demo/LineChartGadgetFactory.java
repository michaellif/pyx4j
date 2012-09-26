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
package com.propertyvista.crm.client.ui.gadgets.demo;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.SvgRoot;
import com.pyx4j.svg.chart.ChartTheme;
import com.pyx4j.svg.chart.DataSource;
import com.pyx4j.svg.chart.GridBasedChartConfigurator;
import com.pyx4j.svg.chart.GridBasedChartConfigurator.GridType;
import com.pyx4j.svg.chart.LineChart;
import com.pyx4j.svg.gwt.basic.SvgFactoryForGwt;

import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.demo.LineChartGadgetMetadata;

public class LineChartGadgetFactory extends AbstractGadgetFactory<com.propertyvista.domain.dashboard.gadgets.type.demo.LineChartGadgetMetadata> {
    private static final I18n i18n = I18n.get(LineChartGadgetFactory.class);

    public class LineChartGadgetInstance extends GadgetInstanceBase<com.propertyvista.domain.dashboard.gadgets.type.demo.LineChartGadgetMetadata> {

        private SimplePanel panel;

        public LineChartGadgetInstance(GadgetMetadata gmd) {
            super(gmd, com.propertyvista.domain.dashboard.gadgets.type.demo.LineChartGadgetMetadata.class);
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
            values.add(58d);
            values.add(30d);
            values.add(41d);
            ds.addDataSet(ds.new Metric("Building 4"), values);

            values = new ArrayList<Double>(5);
            values.add(28d);
            values.add(70d);
            values.add(7d);
            ds.addDataSet(ds.new Metric("Building 5"), values);

            values = new ArrayList<Double>(6);
            values.add(18d);
            values.add(60d);
            values.add(37d);
            ds.addDataSet(ds.new Metric("Building 6"), values);

            List<String> sd = new ArrayList<String>(3);
            sd.add("2008");
            sd.add("2009");
            sd.add("2010");
            ds.setSeriesDescription(sd);

            SvgFactory factory = new SvgFactoryForGwt();

            GridBasedChartConfigurator config = new GridBasedChartConfigurator(factory, ds, 700, 200);
            config.setLegend(true);
            config.setZeroBased(true);
            config.setGridType(GridType.Value);
            config.setTheme(ChartTheme.Bright);
            config.setShowValueLabels(true);

            SvgRoot svgroot = factory.getSvgRoot();
            svgroot.add(new LineChart(config));

            panel.add((Widget) svgroot);
            panel.setSize("700px", "200px");
            panel.getElement().getStyle().setOverflow(Overflow.HIDDEN);

            ScrollPanel scroll = new ScrollPanel(panel);
            scroll.setWidth("100%");
            return scroll;

        }
    }

    public LineChartGadgetFactory() {
        super(com.propertyvista.domain.dashboard.gadgets.type.demo.LineChartGadgetMetadata.class);
    }

    @Override
    protected GadgetInstanceBase<LineChartGadgetMetadata> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new LineChartGadgetInstance(gadgetMetadata);
    }
}