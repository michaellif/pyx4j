/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2011-05-01
 * @author vlads
 */
package com.pyx4j.svg.demo.client;

import com.google.gwt.core.client.EntryPoint;
import com.pyx4j.gwt.commons.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.pyx4j.gwt.commons.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Consts;
import com.pyx4j.svg.basic.Group;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.SvgRoot;
import com.pyx4j.svg.chart.ChartTheme;
import com.pyx4j.svg.chart.DurationAxisProducer;
import com.pyx4j.svg.chart.GridBasedChartConfigurator.GridType;
import com.pyx4j.svg.chart.XYChart;
import com.pyx4j.svg.chart.XYChartConfigurator;
import com.pyx4j.svg.chart.XYChartConfigurator.ChartType;
import com.pyx4j.svg.gwt.ColorPicker;
import com.pyx4j.svg.gwt.basic.SvgFactoryForGwt;
import com.pyx4j.svg.gwt.chart.HoverXYChartTooltip;
import com.pyx4j.svg.test.SvgTestFactory;

public class SVGDemo implements EntryPoint {

    @Override
    public void onModuleLoad() {

        VerticalPanel content = new VerticalPanel();

        RootPanel.get().add(content);

        SvgFactory svgFactory = new SvgFactoryForGwt();

        SvgRoot svgPanel;

        content.add(new HTML("<h5>SVG Demo</h5>"));

        //=========================================//
        boolean testOnlyXYChartEvents = false;

        if (testOnlyXYChartEvents) {
            SvgRoot svgChartPanel = svgFactory.getSvgRoot();
            Group g = svgFactory.createGroup();

            XYChartConfigurator config = new XYChartConfigurator(svgFactory, ChartType.Line, SvgTestFactory.xySeries, 600, 400);
            config.setLegend(true);
            config.setTitle("XYChart");
            config.setGridType(GridType.Both);
            config.setZeroBased(true);
            //config.setZeroBasedY(true);
            config.setChartColors(ChartTheme.bright);

            XYChart lchart = new XYChart(config);
            g.add(lchart);
            svgChartPanel.add(g);
            ((Widget) svgChartPanel).setSize("600px", "400px");
            content.add((Widget) svgChartPanel);

            // This is how events are added to UI.
            HoverXYChartTooltip.inject(content, (Widget) svgChartPanel, lchart);

            return;
        }
        //=========================================//

        boolean testOnlyXYTimeChartEvents = false;

        if (testOnlyXYTimeChartEvents) {
            SvgRoot svgChartPanel = svgFactory.getSvgRoot();

            XYChartConfigurator config = new XYChartConfigurator(svgFactory, ChartType.Line, SvgTestFactory.timeSeries, 1000, 500);
            config.setLegend(true);
            config.setTitle("Time Chart");
            config.setGridType(GridType.Both);
            config.setZeroBased(true);
            config.setZeroBasedY(true);
            config.setChartColors(ChartTheme.bright);

            DurationAxisProducer xaxis = new DurationAxisProducer();
            xaxis.setFixedValueRange(0, Consts.HOURS2MIN * 12);
            config.setXAxisProducer(xaxis);

            XYChart lchart = new XYChart(config);
            svgChartPanel.add(lchart);
            ((Widget) svgChartPanel).setSize("1000px", "500px");
            content.add((Widget) svgChartPanel);

            // This is how events are added to UI.
            HoverXYChartTooltip.inject(content, (Widget) svgChartPanel, lchart);

            return;
        }

        //=========================================//
        boolean testOnlyCharts = false;

        if (testOnlyCharts) {
            svgPanel = SvgTestFactory.createXYBarChart2DTest(svgFactory, 0, 0, true);
            ((Widget) svgPanel).setSize("600px", "400px");
            content.add((Widget) svgPanel);

            svgPanel = SvgTestFactory.createXYChart2DTest(svgFactory, 0, 0, true);
            ((Widget) svgPanel).setSize("600px", "400px");
            content.add((Widget) svgPanel);

            svgPanel = SvgTestFactory.createBarChart2DTest(svgFactory, 0, 0);
            ((Widget) svgPanel).setSize("600px", "400px");
            content.add((Widget) svgPanel);
            return;
        }

        //=========================================//

        content.add(new HTML("Rect"));
        svgPanel = SvgTestFactory.createTestRect(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("150px", "100px");
        content.add((Widget) svgPanel);

        //=========================================//

        content.add(new HTML("Line"));
        svgPanel = SvgTestFactory.createTestLine(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("150px", "100px");
        content.add((Widget) svgPanel);

        //=========================================//

        content.add(new HTML("Path"));
        svgPanel = SvgTestFactory.createTestPath(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("150px", "100px");
        content.add((Widget) svgPanel);

        //=========================================//

        content.add(new HTML("Circle"));
        svgPanel = SvgTestFactory.createTestCircle(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("150px", "100px");
        content.add((Widget) svgPanel);

        //=========================================//

        content.add(new HTML("Ellipse"));
        svgPanel = SvgTestFactory.createTestEllipse(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("150px", "100px");
        content.add((Widget) svgPanel);

        //=========================================//

        content.add(new HTML("Polyline"));
        svgPanel = SvgTestFactory.createTestPolyline(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("150px", "100px");
        content.add((Widget) svgPanel);

        //=========================================//

        content.add(new HTML("Polygon"));
        svgPanel = SvgTestFactory.createTestPolygon(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("150px", "100px");
        content.add((Widget) svgPanel);

        //=========================================//

        content.add(new HTML("Text"));
        svgPanel = SvgTestFactory.createTestText(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("150px", "100px");
        content.add((Widget) svgPanel);

        //=========================================//

        content.add(new HTML("Legend Item"));
        svgPanel = SvgTestFactory.createTestLegendItem(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("150px", "150px");
        content.add((Widget) svgPanel);

        //=========================================//

        svgPanel = SvgTestFactory.createLineChart2DTest(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("600px", "400px");
        content.add((Widget) svgPanel);

        //=========================================//

        svgPanel = SvgTestFactory.createXYChart2DTest(svgFactory, 0, 0, false);
        ((Widget) svgPanel).setSize("600px", "400px");
        content.add((Widget) svgPanel);

        //=========================================//

        svgPanel = SvgTestFactory.createBarChart2DTest(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("600px", "400px");
        content.add((Widget) svgPanel);

        //=========================================//

        svgPanel = SvgTestFactory.createXYBarChart2DTest(svgFactory, 0, 0, true);
        ((Widget) svgPanel).setSize("600px", "400px");
        content.add((Widget) svgPanel);

        //=========================================//

        content.add(new HTML("Multi-Series Pie Chart"));
        svgPanel = SvgTestFactory.createPieChart2DTest(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("352px", "800px");
        content.add((Widget) svgPanel);

        //=========================================//

        content.add(new HTML("Gauge"));
        svgPanel = SvgTestFactory.createGaugeTest(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("352px", "800px");
        content.add((Widget) svgPanel);

        //=========================================//

        content.add(new HTML("Color Picker"));
        SvgRoot svgroot = svgFactory.getSvgRoot();
        ((Widget) svgroot).setSize("352px", "220px");
        Group g = svgFactory.createGroup();
        ColorPicker colorPicker = new ColorPicker(svgFactory, (Widget) svgroot, ColorPicker.PickerType.Hue, 90, 120);
        g.add(colorPicker);
        svgroot.add(g);
        content.add((Widget) svgroot);

    }
}
