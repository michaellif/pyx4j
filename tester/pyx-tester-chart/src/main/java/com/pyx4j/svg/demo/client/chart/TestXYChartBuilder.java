/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Mar 9, 2014
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.svg.demo.client.chart;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.svg.basic.Group;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.SvgRoot;
import com.pyx4j.svg.chart.ChartTheme;
import com.pyx4j.svg.chart.GridBasedChartConfigurator.GridType;
import com.pyx4j.svg.chart.XYChart;
import com.pyx4j.svg.chart.XYChartConfigurator;
import com.pyx4j.svg.chart.XYChartConfigurator.PointsType;
import com.pyx4j.svg.gwt.basic.SvgFactoryForGwt;
import com.pyx4j.svg.test.SvgTestFactory;

public class TestXYChartBuilder implements TestChartBuilder {

    public TestXYChartBuilder() {
    }

    @Override
    public Widget createChart(int width, int height, ChartTestConfiguration gtestConfiguration) {
        ChartXYTestConfiguration testConfiguration = (ChartXYTestConfiguration) gtestConfiguration;

        int x = 0;
        int y = 0;

        SvgFactory factory = new SvgFactoryForGwt();
        SvgRoot svgroot = factory.getSvgRoot();
        Group g = factory.createGroup();

        XYChartConfigurator config = new XYChartConfigurator(factory, testConfiguration.chartType().getValue(), SvgTestFactory.xySeries, width, height);
        config.setLegend(true);
        config.setTitle("XYChart");
        config.setGridType(GridType.Both);
        //config.setZeroBased(zeroBased);
        //config.setZeroBasedY(zeroBased);
        config.setChartColors(ChartTheme.bright);
        config.setPointsType(PointsType.Circle);

        XYChart lchart = new XYChart(config);
        g.add(lchart);
        g.setTransform("translate(" + x + "," + y + ")");
        svgroot.add(g);
        return (Widget) svgroot;
    }

}
