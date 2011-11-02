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

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.pyx4j.svg.basic.Circle;
import com.pyx4j.svg.basic.Group;
import com.pyx4j.svg.basic.Path;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.Text;
import com.pyx4j.svg.chart.DataSource.Metric;
import com.pyx4j.svg.util.Utils;

public class LineChart extends GridBasedChart {

    private final GridBasedChartConfigurator configurator;

    private final static int DOT_RADIUS = 4;

    private final static String LINE_WIDTH = "2";

    public LineChart(GridBasedChartConfigurator configurator) {
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
        int numOfMetrics = getNumOfMetrics();

        List<Text> labels = new LinkedList<Text>();

        ChartTheme theme = configurator.getTheme();
        Set<Entry<Metric, List<Double>>> dataset = configurator.getDatasourse().getDataSet().entrySet();

        for (int idx = 0; idx < numOfSeries; idx++) {
            int metricIdx = configurator.isZeroBased() ? 0 : 1;
            String path = "M";
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
                double x = Utils.round(metricPoints.get(metricIdx), 2);
                double y = Utils.round((ystart - value / valueIncrement * valueSpacing), 2);

                Circle dot = factory.createCircle((int) x, (int) y, DOT_RADIUS);
                dot.setFill(color);
                dot.setStroke(color);
                container.add(dot);
                if (configurator.isShowValueLabels()) {
                    String valueRepr = GridBasedChart.formatDouble(value, configurator.getLabelPrecision());
                    Text label = factory.createText(valueRepr, (int) x, (int) (y - DOT_RADIUS - CHART_LABEL_PADDING));
                    label.setAttribute("text-anchor", "middle");
                    label.setFill(color);
                    labels.add(label);
                }
                path += x + "," + y;
                if (metricIdx == numOfMetrics - 1 & configurator.isZeroBased()) {
                    ;
                } else if (metricIdx % 2 == 0) {
                    path += "L";
                } else
                    path += "M" + x + "," + y + "L";
                ++metricIdx;

            }
            Path line = factory.createPath(path);
            line.setStroke(color);
            line.setStrokeWidth(LINE_WIDTH);
            container.add(line);
        }

        for (Text label : labels) {
            container.add(label);
        }

    }
}
