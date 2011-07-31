/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-07-28
 * @author vadim
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets;

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
import com.pyx4j.svg.gwt.SvgFactoryForGwt;

import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata.GadgetType;

public class GaugeGadget extends GadgetBase {

    private final SimplePanel panel;

    public GaugeGadget(GadgetMetadata gmd) {
        super(gmd);
        panel = new SimplePanel();
        DataSource ds = new DataSource();
        List<Double> values = new ArrayList<Double>(1);
        values.add(32d);
        ds.addDataSet(ds.new Metric("Building 1"), values);
        ds.addSeriesDescription("2008");

        SvgFactory factory = new SvgFactoryForGwt();

        ArcBasedChartConfigurator config = new ArcBasedChartConfigurator(factory, ds);
        config.setScaleMaximum(100d);
        config.setTheme(ChartTheme.Bright);
        config.setRadius(80);
        config.setShowValueLabels(true);

        SvgRoot svgroot = factory.getSvgRoot();
        svgroot.add(new Gauge(config));

        panel.add((Widget) svgroot);
        panel.setSize("300px", "200px");
        panel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
    }

    @Override
    protected void selfInit(GadgetMetadata gmd) {
        gmd.type().setValue(GadgetType.GaugeDisplay);
        gmd.name().setValue("Gauge Display Gadget");

    }

    @Override
    public Widget asWidget() {
        ScrollPanel scroll = new ScrollPanel(panel);
        scroll.setWidth("100%");
        return scroll;
    }

    @Override
    public boolean isFullWidth() {
        return false;
    }

}
