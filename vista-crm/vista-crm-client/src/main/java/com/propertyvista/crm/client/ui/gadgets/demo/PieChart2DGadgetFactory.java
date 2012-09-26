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
import com.pyx4j.svg.chart.ArcBasedChartConfigurator;
import com.pyx4j.svg.chart.ChartTheme;
import com.pyx4j.svg.chart.DataSource;
import com.pyx4j.svg.chart.PieChart2D;
import com.pyx4j.svg.gwt.basic.SvgFactoryForGwt;

import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public class PieChart2DGadgetFactory extends AbstractGadgetFactory<com.propertyvista.domain.dashboard.gadgets.type.demo.PieChart2DGadgetMetadata> {
    private static final I18n i18n = I18n.get(PieChart2D.class);

    public static class PieChart2DGadgetInstance extends GadgetInstanceBase<com.propertyvista.domain.dashboard.gadgets.type.demo.PieChart2DGadgetMetadata> {

        private SimplePanel panel;

        public PieChart2DGadgetInstance(GadgetMetadata gmd) {
            super(gmd, com.propertyvista.domain.dashboard.gadgets.type.demo.PieChart2DGadgetMetadata.class);
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
            values.add(80d);
            ds.addDataSet(ds.new Metric("Building 1"), values);

            values = new ArrayList<Double>(5);
            values.add(35d);
            ds.addDataSet(ds.new Metric("Building 2"), values);

            values = new ArrayList<Double>(5);
            values.add(13d);
            ds.addDataSet(ds.new Metric("Building 3"), values);

            values = new ArrayList<Double>(5);
            values.add(41d);
            ds.addDataSet(ds.new Metric("Building 4"), values);

            values = new ArrayList<Double>(5);
            values.add(7d);
            ds.addDataSet(ds.new Metric("Building 5"), values);

            SvgFactory factory = new SvgFactoryForGwt();

            ArcBasedChartConfigurator config = new ArcBasedChartConfigurator(factory, ds);
            config.setLegend(true);
            config.setTheme(ChartTheme.Bright);
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

    public PieChart2DGadgetFactory() {
        super(com.propertyvista.domain.dashboard.gadgets.type.demo.PieChart2DGadgetMetadata.class);
    }

    @Override
    protected GadgetInstanceBase<com.propertyvista.domain.dashboard.gadgets.type.demo.PieChart2DGadgetMetadata> createInstance(GadgetMetadata gadgetMetadata)
            throws Error {
        return new PieChart2DGadgetInstance(gadgetMetadata);
    }
}