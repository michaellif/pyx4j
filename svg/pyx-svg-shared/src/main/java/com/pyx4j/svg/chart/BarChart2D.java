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
 * Created on May 31, 2011
 * @author Dad
 * @version $Id$
 */
package com.pyx4j.svg.chart;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.pyx4j.svg.basic.Group;
import com.pyx4j.svg.basic.Rect;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.chart.DataSource.Metric;

public class BarChart2D extends GridBasedChart {

    private final GridBasedChartConfigurator configurator;

    private final int MIN_GROUP_GAP = 10;

    public BarChart2D(GridBasedChartConfigurator configurator) {
        super(configurator);
        this.configurator = configurator;
        drawChart();
    }

    @Override
    protected final void drawChart() {

        SvgFactory factory = configurator.getFactory();
        List<Double> metricPoints = getMetricPoints();
        double valueIncrement = getValueIncrement();
        double valueSpacing = getValueSpacing();
        Group container = getContainer();
        int numOfSeries = getNumOfSeries();
        int ystart = getCanvas().getY();
        int barWidth = (int) (getMetricSpacing() - MIN_GROUP_GAP) / numOfSeries;

        ChartTheme theme = configurator.getTheme();
        Set<Entry<Metric, List<Double>>> dataset = configurator.getDatasourse().getDataSet().entrySet();
        double hShift = numOfSeries / 2d;
        for (int idx = 0; idx < numOfSeries; idx++) {

            int metricIdx = configurator.isZeroBased() ? 0 : 1;
            String color = theme.getNextColor();
            for (Entry<Metric, List<Double>> entry : dataset) {
                List<Double> values = entry.getValue();
                Double value;
                if (values == null || values.size() == 0)
                    value = 0.0;
                else {
                    try {
                        value = values.get(idx);
                    } catch (Exception ex) {
                        value = 0.0;
                    }
                }
                //draw series data
                double x = metricPoints.get(metricIdx) - (numOfSeries - idx - hShift) * barWidth;
                double height = value / valueIncrement * valueSpacing;
                double y = ystart - height;
                Rect bar = factory.createRect((int) x, (int) y, barWidth, (int) height, 0, 0);
                bar.setFill(color);
                bar.setStroke(color);
                container.add(bar);

                ++metricIdx;

            }

        }

    }

}
