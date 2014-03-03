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
 * Created on May 29, 2011
 * @author vadims
 * @version $Id$
 */
package com.pyx4j.svg.chart;

import java.util.Iterator;

import com.pyx4j.svg.basic.SvgFactory;

public abstract class BasicChartConfigurator {

    private static final int DEFAULT_LABEL_PRECISION = 2;

    private final DataSource datasourse;

    private final SvgFactory factory;

    private ChartColors chartColors;

    private String title;

    private boolean legend;

    private String legendBackgroundColor;

    private boolean showValueLabels;

    private int labelPrecision;

    public BasicChartConfigurator(SvgFactory factory, DataSource datasource) {
        assert factory != null;
        this.factory = factory;
        this.datasourse = datasource;
        chartColors = ChartTheme.monochrome;
        legend = false;
        title = null;
        showValueLabels = false;
        labelPrecision = DEFAULT_LABEL_PRECISION;
    }

    public ChartColors getChartColors() {
        return chartColors;
    }

    public void setChartColors(ChartColors chartColors) {
        this.chartColors = chartColors;
    }

    public Iterator<String> getColorIterator() {
        return new ChartColorsCircularIterator(getChartColors());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isLegend() {
        return legend;
    }

    public void setLegend(boolean legend) {
        this.legend = legend;
    }

    public String getLegendBackgroundColor() {
        return legendBackgroundColor;
    }

    public void setLegendBackgroundColor(String legendBackgroundColor) {
        this.legendBackgroundColor = legendBackgroundColor;
    }

    public DataSource getDatasourse() {
        return datasourse;
    }

    public SvgFactory getFactory() {
        return factory;
    }

    public boolean isShowValueLabels() {
        return showValueLabels;
    }

    public void setShowValueLabels(boolean showValueLabels) {
        this.showValueLabels = showValueLabels;
    }

    public void setLabelPrecision(int precision) {
        if (precision < 0) {
            this.labelPrecision = 0;
        }
    }

    public int getLabelPrecision() {
        return labelPrecision;
    }
}
