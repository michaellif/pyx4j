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
 * Created on May 19, 2011
 * @author vadims
 * @version $Id$
 */
package com.pyx4j.svg.chart;

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.pyx4j.svg.basic.Group;
import com.pyx4j.svg.basic.IsSvgElement;
import com.pyx4j.svg.basic.Path;
import com.pyx4j.svg.basic.Rect;
import com.pyx4j.svg.basic.SvgElement;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.chart.DataSource.Metric;

public class ChartAxis implements IsSvgElement {

    public final static int PADDING = 15;

    private static final String AXIS_COLOR = "gray";

    private static final String GRID_COLOR = "lightgray";

    private static final int TICK_LENGTH = 5;

    private static int NUM_OF_VALUE_TICKS = 10;

    private final DataSource datasource;

    private final SvgFactory factory;

    private final boolean showLegend;

    private final Group container;

    private final int width;

    private final int height;

    private List<Double> metricPoints;

    private List<Double> valuePoints;

    private double maximumValue;

    private final Dimension canvas;

    private final ChartGridType gridtype;

    public ChartAxis(SvgFactory factory, DataSource datasource, int width, int height, ChartGridType gridtype, boolean showLegen) {
        this.factory = factory;
        this.datasource = datasource;
        this.width = width;
        this.height = height;
        this.showLegend = showLegen;
        container = this.factory.createGroup();
        canvas = new Dimension(this.width - 2 * PADDING, height - 2 * PADDING);
        this.gridtype = gridtype;
        drawAxes();
    }

    @Override
    public SvgElement asSvgElement() {
        return container;
    }

    private void drawAxes() {
        List<String> sdesc = datasource.getSeriesDescription();

        int xstart = PADDING;
        int ystart = canvas.height + PADDING;

        int xend = PADDING + canvas.width;
        int yend = PADDING;

        int numOfMetrics = datasource.getDataSet().size();
        metricPoints = new ArrayList<Double>(20);
        double metricSpacing = roundTwoDecimals(canvas.width / (numOfMetrics + 1));
        metricPoints.add((double) xstart);
        for (int i = 1; i <= numOfMetrics; i++)
            metricPoints.add(xstart + metricSpacing * i);

        maximumValue = getMaxValue();
        valuePoints = new ArrayList<Double>(NUM_OF_VALUE_TICKS + 1);
        double valueSpacing = roundTwoDecimals(canvas.height / (NUM_OF_VALUE_TICKS + 1));
        for (int i = 1; i <= NUM_OF_VALUE_TICKS; i++)
            valuePoints.add(ystart - valueSpacing * i);

        Set<Entry<Metric, List<Double>>> dataset = datasource.getDataSet().entrySet();
        //find out number of series
        int numOfSeries = 0;
        for (Entry<Metric, List<Double>> entry : dataset) {
            int size = entry.getValue().size();
            if (numOfSeries < size)
                numOfSeries = size;
        }

        //TODO remove later
        Rect frame = factory.createRect(0, 0, width, height, 0, 0);
        frame.setStroke(GRID_COLOR);

        container.add(frame);

        //draw metrics axis
        String metricA = "M" + xstart + "," + ystart + "L" + xend + "," + ystart;
        //draw metric ticks and grid lines
        int tickEnd = ystart + TICK_LENGTH;
        for (Double xx : metricPoints)
            metricA += "M" + xx + "," + ystart + "L" + xx + "," + tickEnd;

        //draw metric grid lines
        String metricGL = "";
        if (gridtype == ChartGridType.Both || gridtype == ChartGridType.Metric) {
            for (Double xx : metricPoints) {
                if (xx > 0)//skip the first
                    metricGL += "M" + xx + "," + ystart + "L" + xx + "," + yend;
            }
        }
        //
        String valueA = "M" + xstart + "," + ystart + "L" + xstart + "," + yend;
        //draw value ticks
        tickEnd = xstart - TICK_LENGTH;
        for (Double yy : valuePoints)
            valueA += "M" + xstart + "," + yy + "L" + tickEnd + "," + yy;
        //draw value grid lines
        String valueGL = "";
        if (gridtype == ChartGridType.Both || gridtype == ChartGridType.Value) {
            for (Double yy : valuePoints)
                valueGL += "M" + xstart + "," + yy + "L" + xend + "," + yy;
        }

        if (metricGL.length() > 0) {
            Path metricGLP = factory.createPath(metricGL);
            metricGLP.setStroke(GRID_COLOR);
            metricGLP.setStrokeWidth("1");
            container.add(metricGLP);
        }

        if (valueGL.length() > 0) {
            Path valueGLP = factory.createPath(valueGL);
            valueGLP.setStroke(GRID_COLOR);
            valueGLP.setStrokeWidth("1");
            container.add(valueGLP);
        }

        Path valueAsix = factory.createPath(valueA);
        valueAsix.setStroke(AXIS_COLOR);
        valueAsix.setStrokeWidth("1");
        container.add(valueAsix);

        Path metricAsix = factory.createPath(metricA);
        metricAsix.setStroke(AXIS_COLOR);
        metricAsix.setStrokeWidth("1");
        container.add(metricAsix);

    }

    public double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    private double getMaxValue() {
        Double max = null;
        for (Entry<Metric, List<Double>> entry : datasource.getDataSet().entrySet()) {
            for (Double d : entry.getValue()) {
                if (max == null)
                    max = d;
                if (max < d)
                    max = d;
            }
        }
        return ((max == null) ? 0 : max);
    }
}
