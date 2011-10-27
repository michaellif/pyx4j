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
 * @author Dad
 * @version $Id$
 */
package com.pyx4j.svg.chart;

import com.pyx4j.svg.basic.SvgFactory;

public abstract class BasicChartConfigurator {
    private static final int DEFAULT_LABEL_PRECISION = 2;

    private final DataSource datasourse;

    private final SvgFactory factory;

    private ChartTheme theme;

    private boolean legend;

    private boolean showValueLabels;

    private int labelPrecision;

    public BasicChartConfigurator(SvgFactory factory, DataSource datasource) {
        assert factory != null;
        assert datasource != null;
        this.factory = factory;
        this.datasourse = datasource;
        theme = ChartTheme.Monochrome;
        legend = false;
        showValueLabels = false;
        labelPrecision = DEFAULT_LABEL_PRECISION;
    }

    public ChartTheme getTheme() {
        return theme;
    }

    public void setTheme(ChartTheme theme) {
        this.theme = theme;
    }

    public boolean isLegend() {
        return legend;
    }

    public void setLegend(boolean legend) {
        this.legend = legend;
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
