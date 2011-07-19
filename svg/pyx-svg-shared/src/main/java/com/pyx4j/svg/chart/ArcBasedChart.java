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
 * Created on 2011-07-18
 * @author vadim
 * @version $Id$
 */
package com.pyx4j.svg.chart;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.pyx4j.svg.basic.Group;
import com.pyx4j.svg.basic.IsSvgElement;
import com.pyx4j.svg.basic.SvgElement;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.chart.DataSource.Metric;

public abstract class ArcBasedChart implements IsSvgElement {

    private final DataSource datasource;

    protected final SvgFactory factory;

    protected final int radius;

    protected int computedWidth;

    protected int computedHeight;

    protected final static int PADDING = 15;

    protected final Group container;

    protected final ArcBasedChartConfigurator configurator;

    public ArcBasedChart(ArcBasedChartConfigurator configurator) {
        this.configurator = configurator;
        this.datasource = configurator.getDatasourse();
        this.radius = configurator.getRadius();
        this.factory = configurator.getFactory();
        container = this.factory.createGroup();
        computedWidth = 0;
        computedHeight = 0;
        drawChart();
    }

    private void drawChart() {

        List<String> sdesc = datasource.getSeriesDescription();
        int x = radius + PADDING;
        int y = radius + PADDING;

        Set<Entry<Metric, List<Double>>> dataset = datasource.getDataSet().entrySet();
        //find out number of series
        int numOfSeries = 0;
        for (Entry<Metric, List<Double>> entry : dataset) {
            int size = entry.getValue().size();
            if (numOfSeries < size)
                numOfSeries = size;
        }

        for (int idx = 0; idx < numOfSeries; idx++) {
            Map<Metric, Double> series = new LinkedHashMap<Metric, Double>(10);
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
                series.put(entry.getKey(), value);
            }

            String SeriesTitle = null;
            if (sdesc != null) {
                try {
                    SeriesTitle = sdesc.get(idx);
                } catch (Exception e) {
                    ;
                }
            }

            computedWidth = drawSeries(series, x, y, SeriesTitle);
            y += radius * 2 + PADDING;
            if (SeriesTitle != null && SeriesTitle.length() > 0)
                y += 2 * PADDING;
        }
        computedHeight = y + radius + PADDING;

    }

    protected abstract int drawSeries(Map<Metric, Double> series, final int x, final int y, String seriestitle);

    @Override
    public SvgElement asSvgElement() {
        return container;
    }

    public int getComputedWidth() {
        return computedWidth;
    }

    public int getComputedHeight() {
        return computedHeight;
    }

}
