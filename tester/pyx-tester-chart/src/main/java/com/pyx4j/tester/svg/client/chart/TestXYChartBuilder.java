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
package com.pyx4j.tester.svg.client.chart;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.svg.basic.Group;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.SvgRoot;
import com.pyx4j.svg.chart.ChartTheme;
import com.pyx4j.svg.chart.DurationAxisProducer;
import com.pyx4j.svg.chart.GridBasedChartConfigurator.GridType;
import com.pyx4j.svg.chart.XYChart;
import com.pyx4j.svg.chart.XYChartConfigurator;
import com.pyx4j.svg.chart.XYChartConfigurator.ChartType;
import com.pyx4j.svg.chart.XYChartConfigurator.PointsType;
import com.pyx4j.svg.chart.XYSeries;
import com.pyx4j.svg.gwt.basic.SvgFactoryForGwt;
import com.pyx4j.tester.svg.client.config.ChartTestConfiguration;
import com.pyx4j.tester.svg.client.config.ChartXYTestConfiguration;

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

        //Serialization convertion
        ChartType chartType = ChartType.valueOf(testConfiguration.chartType().getValue().name());

        XYChartConfigurator config = new XYChartConfigurator(factory, chartType, createXYSeries(testConfiguration), width, height);
        config.setLegend(true);
        config.setTitle("XYChart");
        config.setGridType(GridType.Both);
        //config.setZeroBased(zeroBased);
        //config.setZeroBasedY(zeroBased);
        config.setChartColors(ChartTheme.bright);
        config.setPointsType(PointsType.valueOf(testConfiguration.pointsType().getValue().name()));

        switch (testConfiguration.xValuesType().getValue()) {
        case Duration:
            config.setXAxisProducer(new DurationAxisProducer());
        }

        switch (testConfiguration.yValuesType().getValue()) {
        case Duration:
            config.setYAxisProducer(new DurationAxisProducer());
        }

        XYChart lchart = new XYChart(config);
        g.add(lchart);
        g.setTransform("translate(" + x + "," + y + ")");
        svgroot.add(g);
        return (Widget) svgroot;
    }

    private List<XYSeries> createXYSeries(ChartXYTestConfiguration testConfiguration) {
        List<XYSeries> series = new ArrayList<>();

        double height = (testConfiguration.yTo().getValue() - testConfiguration.yFrom().getValue()) * testConfiguration.yMultiplication().getValue(1.0);
        double width = (testConfiguration.xTo().getValue() - testConfiguration.xFrom().getValue()) * testConfiguration.xMultiplication().getValue(1.0);

        double yFrom = testConfiguration.yFrom().getValue() * testConfiguration.yMultiplication().getValue(1.0);
        double xFrom = testConfiguration.xFrom().getValue() * testConfiguration.xMultiplication().getValue(1.0);

        double xStep = width / (testConfiguration.points().getValue() - 1);
        {
            XYSeries serie = new XYSeries("sine");
            series.add(serie);
            for (int i = 0; i < testConfiguration.points().getValue(); i++) {
                double x = xFrom + i * xStep;
                double y = yFrom + 0.5 * height + (height / 2) * Math.sin(4.0 * Math.PI * x / width);
                serie.add(x, y);
            }
        }

        {
            XYSeries serie = new XYSeries("cosine");
            series.add(serie);
            for (int i = 0; i < testConfiguration.points().getValue() - 1; i++) {
                double x = (xStep / 2) + xFrom + i * xStep;
                double y = yFrom + 0.5 * height + (height / 2) * Math.cos(4.0 * Math.PI * x / width);
                serie.add(x, y);
            }
        }

        return series;
    }
}
