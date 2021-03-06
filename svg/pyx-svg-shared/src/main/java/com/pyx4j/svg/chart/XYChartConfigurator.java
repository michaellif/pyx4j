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
 * Created on Mar 3, 2014
 * @author vlads
 */
package com.pyx4j.svg.chart;

import java.util.List;

import com.pyx4j.svg.basic.SvgFactory;

public class XYChartConfigurator extends GridBasedChartConfigurator {

    private final ChartType chartType;

    private final List<XYSeries> series;

    private boolean zeroBasedY;

    private PointsType pointsType = PointsType.None;

    private int barWidth = 10;

    private AxisProducer xAxisProducer;

    private AxisProducer yAxisProducer;

    public enum ChartType {
        Line, Bar, BarDistribution
    }

    public enum PointsType {
        None, Circle
    }

    public XYChartConfigurator(SvgFactory factory, ChartType chartType, List<XYSeries> series, int width, int height) {
        super(factory, null, width, height);
        this.series = series;
        this.chartType = chartType;
    }

    public ChartType getChartType() {
        return chartType;
    }

    public List<XYSeries> getSeries() {
        return series;
    }

    public boolean isZeroBasedY() {
        return zeroBasedY;
    }

    public void setZeroBasedY(boolean zeroBasedY) {
        this.zeroBasedY = zeroBasedY;
    }

    public PointsType getPointsType() {
        return pointsType;
    }

    public void setPointsType(PointsType pointsType) {
        this.pointsType = pointsType;
    }

    public int getBarWidth() {
        return barWidth;
    }

    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;
    }

    public AxisProducer getXAxisProducer() {
        if (xAxisProducer == null) {
            xAxisProducer = new DecimalAxisProducer();
        }
        return xAxisProducer;
    }

    public void setXAxisProducer(AxisProducer xAxisProducer) {
        this.xAxisProducer = xAxisProducer;
    }

    public AxisProducer getYAxisProducer() {
        if (yAxisProducer == null) {
            yAxisProducer = new DecimalAxisProducer();
        }
        return yAxisProducer;
    }

    public void setYAxisProducer(AxisProducer yAxisProducer) {
        this.yAxisProducer = yAxisProducer;
    }

    @Deprecated
    public void setXLabelFormatter(LabelFormatter xLabelFormatter) {
        getXAxisProducer().setLabelFormatter(xLabelFormatter);
    }

}
