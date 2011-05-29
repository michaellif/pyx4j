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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.pyx4j.svg.basic.Group;
import com.pyx4j.svg.basic.IsSvgElement;
import com.pyx4j.svg.basic.Path;
import com.pyx4j.svg.basic.Rect;
import com.pyx4j.svg.basic.SvgElement;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.Text;
import com.pyx4j.svg.chart.DataSource.Metric;
import com.pyx4j.svg.chart.GridBasedChartConfigurator.GridType;

public class GridBasedChart implements IsSvgElement {

    public final static int PADDING = 15;

    private static final String AXIS_COLOR = "gray";

    private static final String GRID_COLOR = "lightgray";

    private static final int TICK_LENGTH = 5;

    private static int NUM_OF_VALUE_TICKS = 10;

    private static int DEFAULT_FONT_SIZE = 11;

    private final static int LEGEND_FRAME_PADDING = 5;

    private final static int TITLE_FONT_SIZE = 20;

    private final static int TITLE_PADDING = 10;

    private static int VALUE_LABEL_PADDING = 7;

    private static int LEGEND_SPACING = 7;

    private final DataSource datasource;

    private final SvgFactory factory;

    private final boolean showLegend;

    private final String chartTitle;

    private final Group container;

    private final int width;

    private final int height;

    private List<Double> metricPoints;

    private List<Double> valuePoints;

    private double maximumValue;

    private Dimension canvas;

    private final GridType gridtype;

    private MetricPrefix valuePostfix;

    private final ChartTheme theme;

    public enum MetricPrefix {
        T() {

            @Override
            public long getFactor() {
                return 1000000000000l;
            }

            @Override
            public String getName() {
                return "T";
            }

        },
        G() {
            @Override
            public long getFactor() {
                return 1000000000;
            }

            @Override
            public String getName() {
                return "G";
            }

        },
        M() {

            @Override
            public long getFactor() {
                return 1000000;
            }

            @Override
            public String getName() {
                return "M";
            }

        },
        K() {

            @Override
            public long getFactor() {
                return 1000;
            }

            @Override
            public String getName() {
                return "K";
            }

        },
        NONE() {

            @Override
            public long getFactor() {
                return 1;
            }

            @Override
            public String getName() {
                return "";
            }

        };
        public abstract long getFactor();

        public abstract String getName();
    }

    public GridBasedChart(GridBasedChartConfigurator configurator) {
        factory = configurator.getFactory();
        datasource = configurator.getDatasourse();
        width = configurator.getWidht();
        height = configurator.getWidht();
        showLegend = configurator.isLegend();
        container = factory.createGroup();
        gridtype = configurator.getGridType();
        chartTitle = configurator.getTitle();
        theme = configurator.getTheme();
        drawChart();
    }

    @Override
    public SvgElement asSvgElement() {
        return container;
    }

    private void drawChart() {
        int numOfMetrics = datasource.getDataSet().size();
        metricPoints = new ArrayList<Double>(20);
        maximumValue = getMaxValue();
        valuePoints = new ArrayList<Double>(NUM_OF_VALUE_TICKS + 1);

        //adjust width for value labels
        if (maximumValue % MetricPrefix.T.getFactor() != maximumValue)
            valuePostfix = MetricPrefix.T;
        else if (maximumValue % MetricPrefix.G.getFactor() != maximumValue)
            valuePostfix = MetricPrefix.G;
        else if (maximumValue % MetricPrefix.M.getFactor() != maximumValue)
            valuePostfix = MetricPrefix.M;
        else if (maximumValue % MetricPrefix.K.getFactor() != maximumValue)
            valuePostfix = MetricPrefix.K;
        else
            valuePostfix = MetricPrefix.NONE;
        //round up the maximum value to avoid lots of decimal places
        maximumValue = Math.round(maximumValue / valuePostfix.getFactor()) * valuePostfix.getFactor();
        Set<Entry<Metric, List<Double>>> dataset = datasource.getDataSet().entrySet();
        //find out number of series
        int numOfSeries = 0;
        for (Entry<Metric, List<Double>> entry : dataset) {
            int size = entry.getValue().size();
            if (numOfSeries < size)
                numOfSeries = size;
        }

        //roundTwoDecimals(maximumValue / valuePostfix.getFactor())
        int maxValLabelLength = String.valueOf(maximumValue / valuePostfix.getFactor()).length() * DEFAULT_FONT_SIZE + VALUE_LABEL_PADDING;
        int xstart = PADDING + maxValLabelLength;
        int ystart = height - PADDING - DEFAULT_FONT_SIZE;

        int yMetricLabel = ystart + PADDING;
        ;

        int xend = width - PADDING;
        int yend = PADDING;
        if (chartTitle != null && chartTitle.length() > 0) {
            //adjust height to accommodate chart title
            Text title = factory.createText(chartTitle, width / 2, yend + TITLE_FONT_SIZE);
            title.setAttribute("font-size", String.valueOf(TITLE_FONT_SIZE));
            title.setAttribute("text-anchor", "middle");
            container.add(title);
            yend += TITLE_FONT_SIZE + TITLE_PADDING;

        }

        if (showLegend) {
            Group legend = createLegend(numOfSeries);
            int legY = (width - Integer.parseInt(legend.getAttribute("height"))) / 2;
            xend = xend - Integer.parseInt(legend.getAttribute("width")) - PADDING;
            legend.setTransform("translate(" + (xend + LEGEND_SPACING + PADDING) + "," + legY + ")");
            container.add(legend);
        }

        //adjust the canvas dimension after the calculations above are done 
        canvas = new Dimension(xend - xstart, ystart - yend);

        //roundTwoDecimals(canvas.getWidth() / (numOfMetrics + 1));
        double metricSpacing = canvas.getWidth() / (numOfMetrics + 1);

        metricPoints.add((double) xstart);
        for (int i = 1; i <= numOfMetrics; i++)
            metricPoints.add(xstart + metricSpacing * i);

        //roundTwoDecimals(canvas.getHeight() / (NUM_OF_VALUE_TICKS + 1));
        double valueSpacing = canvas.getHeight() / (NUM_OF_VALUE_TICKS + 1);
        for (int i = 1; i <= NUM_OF_VALUE_TICKS; i++) {
            valuePoints.add(ystart - valueSpacing * i);
        }

        //TODO remove later
        Rect frame = factory.createRect(0, 0, width, height, 0, 0);
        frame.setStroke(GRID_COLOR);

        container.add(frame);

        //draw metrics axis
        String metricA = "M" + xstart + "," + ystart + "L" + xend + "," + ystart;
        //draw metric ticks 
        int tickEnd = ystart + TICK_LENGTH;
        boolean firstPass = true;
        Iterator<Metric> metriciterator = datasource.getDataSet().keySet().iterator();
        for (Double xx : metricPoints) {
            metricA += "M" + xx + "," + ystart + "L" + xx + "," + tickEnd;
            //draw metric labels
            if (!firstPass) {
                Text lbl = factory.createText(metriciterator.next().getCaption(), xx.intValue(), yMetricLabel);
                lbl.setAttribute("font-size", String.valueOf(DEFAULT_FONT_SIZE));
                lbl.setAttribute("text-anchor", "middle");
                container.add(lbl);
            } else
                firstPass = false;
        }

        //draw metric grid lines
        String metricGL = "";
        if (gridtype == GridType.Both || gridtype == GridType.Metric) {
            for (Double xx : metricPoints) {
                if (xx > 0)//skip the first
                    metricGL += "M" + xx + "," + ystart + "L" + xx + "," + yend;
            }
        }

        //
        String valueA = "M" + xstart + "," + ystart + "L" + xstart + "," + yend;
        //draw value ticks
        tickEnd = xstart - TICK_LENGTH;
        //roundTwoDecimals(maximumValue / NUM_OF_VALUE_TICKS);
        double valueIncrement = maximumValue / NUM_OF_VALUE_TICKS;
        double value = valueIncrement;

        int lblxstart = xstart - VALUE_LABEL_PADDING;
        Text lblfirst = factory.createText("0", lblxstart, ystart);
        lblfirst.setAttribute("font-size", String.valueOf(DEFAULT_FONT_SIZE));
        lblfirst.setAttribute("text-anchor", "end");
        container.add(lblfirst);
        for (Double yy : valuePoints) {
            valueA += "M" + xstart + "," + yy + "L" + tickEnd + "," + yy;
            Text lbl = factory.createText(String.valueOf(value / valuePostfix.getFactor()) + valuePostfix.getName(), lblxstart, yy.intValue());
            lbl.setAttribute("font-size", String.valueOf(DEFAULT_FONT_SIZE));
            lbl.setAttribute("text-anchor", "end");
            container.add(lbl);
            value += valueIncrement;
        }
        //draw value grid lines
        String valueGL = "";
        if (gridtype == GridType.Both || gridtype == GridType.Value) {
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

//TODO does not work for GWT
/*
 * public double roundTwoDecimals(double d) {
 * DecimalFormat twoDForm = new DecimalFormat("#.##");
 * return Double.valueOf(twoDForm.format(d));
 * }
 */

    public MetricPrefix getValuePostfix() {
        return valuePostfix;
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

    private Group createLegend(int numOfSeries) {
        if (numOfSeries <= 0)
            return null;
        List<String> sdesc = datasource.getSeriesDescription();
        List<String> seriesDescription = new ArrayList<String>(numOfSeries);
        for (int i = 0; i < numOfSeries; i++) {
            try {
                seriesDescription.add(sdesc.get(i));
            } catch (Exception e) {
                seriesDescription.add("");
            }
        }

        Group legG = factory.createGroup();
        int Y_SHIFT = 10;
        int legendWidth = 0;
        int legentHeight = 0;
        int iconsize = 0;
        int y = 0;

        Set<Metric> metrics = datasource.getDataSet().keySet();
        for (int i = 0; i < numOfSeries; i++) {
            LegendItem li = new LegendItem(factory, seriesDescription.get(i), LegendIconType.Circle, 0, y);
            li.setColor(theme.getNextColor());
            legentHeight = li.getHeight() + Y_SHIFT;
            y += legentHeight;

            legG.add(li);
            if (legendWidth < li.getWidth())
                legendWidth = li.getWidth();
            if (iconsize == 0)
                iconsize = li.getIconSize();
        }
        int w = legendWidth + LEGEND_FRAME_PADDING;

        Rect frame = factory.createRect(-iconsize - LEGEND_FRAME_PADDING, -iconsize * 2 - LEGEND_FRAME_PADDING, w, y + LEGEND_FRAME_PADDING, 0, 0);
        legG.setAttribute("width", String.valueOf(w));
        legG.setAttribute("height", String.valueOf(y));
        legG.add(frame);
        return legG;
    }
}
