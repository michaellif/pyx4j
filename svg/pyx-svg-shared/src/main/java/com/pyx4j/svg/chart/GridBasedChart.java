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

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.svg.basic.Group;
import com.pyx4j.svg.basic.IsSvgElement;
import com.pyx4j.svg.basic.Path;
import com.pyx4j.svg.basic.Rect;
import com.pyx4j.svg.basic.SvgElement;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.Text;
import com.pyx4j.svg.basic.TickProducer;
import com.pyx4j.svg.chart.DataSource.Metric;
import com.pyx4j.svg.chart.GridBasedChartConfigurator.GridType;
import com.pyx4j.svg.common.Tick;
import com.pyx4j.svg.common.Tick.Rank;

public abstract class GridBasedChart extends GridBase implements IsSvgElement {
    /**
     * Default max value of the graph, for the cases when actual max value that is present in the data set is 0.
     * It's used to avoid the bug that causes the points that represent zero to be drawn not on the X-axis.
     */
    private static final double DEFAULT_MAX_VALUE = 100d;

    private static int NUM_OF_VALUE_TICKS = 10;

    private final DataSource datasource;

    private final SvgFactory factory;

    private final Group container;

    private List<Double> metricPoints;

    private Area canvas;

    private MetricPrefix valuePostfix;

    private final GridBasedChartConfigurator configurator;

    private final TickProducer tickProducer;

    private double metricSpacing;

    private double valueSpacing;

    private double valueIncrement;

    private int numOfSeries;

    private int numOfMetrics;

    private double maxValue;

    public GridBasedChart(GridBasedChartConfigurator configurator) {
        //TODO validation
        // assert (configurator != null);
        this.configurator = configurator;
        factory = configurator.getFactory();
        datasource = configurator.getDatasourse();
        container = factory.createGroup();
        tickProducer = configurator.getValueTickProducer();
        drawBasics();
    }

    @Override
    public SvgElement asSvgElement() {
        return container;
    }

    protected abstract void drawChart();

    protected MetricPrefix getValuePostfix() {
        return valuePostfix;
    }

    protected List<Double> getMetricPoints() {
        return metricPoints;
    }

    protected Area getCanvas() {
        return canvas;
    }

    protected double getMetricSpacing() {
        return metricSpacing;
    }

    protected double getValueSpacing() {
        return valueSpacing;
    }

    protected double getValueIncrement() {
        return valueIncrement;
    }

    protected Group getContainer() {
        return container;
    }

    protected int getNumOfSeries() {
        return numOfSeries;
    }

    protected TickProducer getTickProducer() {
        return tickProducer;
    }

    public int getNumOfMetrics() {
        return numOfMetrics;
    }

    public double getMaxValue() {
        return maxValue;
    }

    private void drawBasics() {
        numOfMetrics = datasource.getDataSet().size();
        metricPoints = new ArrayList<Double>(20);
        maxValue = calculateMaxValue();

        //adjust width for value labels
        valuePostfix = MetricPrefix.getMetricPrefix(maxValue);
        //round up the maximum value to avoid lots of decimal places
        // And allow to see all the value, use ceil
        maxValue = Math.ceil(10.0 * maxValue / valuePostfix.getFactor()) * valuePostfix.getFactor() / 10.0;
        Set<Entry<Metric, List<Double>>> dataset = datasource.getDataSet().entrySet();
        //find out number of series
        numOfSeries = 0;
        for (Entry<Metric, List<Double>> entry : dataset) {
            int size = entry.getValue().size();
            if (numOfSeries < size) {
                numOfSeries = size;
            }
        }

        int xstart = 0;
        int ystart = configurator.getHeight();

        int xend = configurator.getWidth() - PADDING;
        int yend = PADDING;
        if (CommonsStringUtils.isStringSet(configurator.getTitle())) {
            //adjust height to accommodate chart title
            Text title = factory.createText(configurator.getTitle(), configurator.getWidth() / 2, yend + TITLE_FONT_SIZE);
            title.setAttribute("font-size", String.valueOf(TITLE_FONT_SIZE));
            title.setAttribute("text-anchor", "middle");
            container.add(title);
            yend += TITLE_FONT_SIZE + TITLE_PADDING;

        }

        if (CommonsStringUtils.isStringSet(configurator.getHorisontalAxisTitle())) {
            ystart -= DEFAULT_FONT_SIZE;
            Text hAxisTitle = factory.createText(configurator.getHorisontalAxisTitle(), configurator.getWidth() / 2, ystart);
            hAxisTitle.setAttribute("font-size", String.valueOf(DEFAULT_FONT_SIZE));
            hAxisTitle.setAttribute("text-anchor", "middle");
            container.add(hAxisTitle);
            ystart -= PADDING;
        }

        ystart -= DEFAULT_FONT_SIZE;
        int yMetricLabel = ystart;
        ystart -= PADDING;

        if (CommonsStringUtils.isStringSet(configurator.getVerticalAxisTitle())) {
            int x = xstart + PADDING + DEFAULT_FONT_SIZE / 2;
            int y = (configurator.getHeight() + configurator.getVerticalAxisTitle().length() * DEFAULT_FONT_SIZE / 2) / 2;
            Text vAxisTitle = factory.createText(configurator.getVerticalAxisTitle(), x, y);
            vAxisTitle.setAttribute("font-size", String.valueOf(DEFAULT_FONT_SIZE));
            vAxisTitle.setTransform("rotate(-90," + x + "," + y + ")");
            container.add(vAxisTitle);
            xstart += DEFAULT_FONT_SIZE;
        }

        int maxValLabelLength = String.valueOf(maxValue / valuePostfix.getFactor()).length() * DEFAULT_FONT_SIZE + VALUE_LABEL_PADDING;
        xstart += PADDING + maxValLabelLength;

        if (configurator.isLegend()) {
            Group legend = createLegend(numOfSeries);
            int legY = ((configurator.getHeight()) - Integer.parseInt(legend.getAttribute("height"))) / 2;
            xend = xend - Integer.parseInt(legend.getAttribute("width")) - PADDING;
            legend.setTransform("translate(" + (xend + LEGEND_SPACING + PADDING) + "," + legY + ")");
            container.add(legend);
        }

        tickProducer.updateTicks(0, maxValue, ystart - yend);

        //adjust the canvas dimension after the calculations above are done
        canvas = new Area(xstart, ystart, xend - xstart, ystart - yend);
        metricSpacing = canvas.getWidth() / (configurator.isZeroBased() ? numOfMetrics - 1 : numOfMetrics + 1);

        metricPoints.add((double) xstart);
        int times = configurator.isZeroBased() ? numOfMetrics - 1 : numOfMetrics;
        for (int i = 1; i <= times; i++) {
            metricPoints.add(xstart + metricSpacing * i);
        }

        //draw metrics axis
        String metricA = "M" + xstart + "," + ystart + "L" + xend + "," + ystart;
        //draw metric ticks
        int tickEnd = ystart + MAJOR_TICK_LENGTH;
        boolean firstPass = !configurator.isZeroBased();
        Iterator<Metric> metriciterator = datasource.getDataSet().keySet().iterator();
        for (Double xx : metricPoints) {
            metricA += "M" + xx + "," + ystart + "L" + xx + "," + tickEnd;
            //draw metric labels
            if (configurator.getGridType() == GridType.Both || configurator.getGridType() == GridType.Metric) {
                if (!firstPass) {
                    Text lbl = factory.createText(metriciterator.next().getCaption(), xx.intValue(), yMetricLabel);
                    lbl.setAttribute("font-size", String.valueOf(DEFAULT_FONT_SIZE));
                    lbl.setAttribute("text-anchor", "middle");
                    container.add(lbl);
                } else {
                    firstPass = false;
                }
            }
        }

        //draw metric grid lines
        String metricGL = "";
        if (configurator.getGridType() == GridType.Both || configurator.getGridType() == GridType.Metric) {
            for (Double xx : metricPoints) {
                //skip the first
                if (xx > 0) {
                    metricGL += "M" + xx + "," + ystart + "L" + xx + "," + yend;
                }
            }
        }

        int lblxstart = xstart - VALUE_LABEL_PADDING;

        String valueGL = "";
        String valueA = "M" + xstart + "," + ystart + "L" + xstart + "," + yend;
        List<Tick> ticks = tickProducer.getTicks();
        for (Tick tick : ticks) {
            if (Rank.MAJOR.equals(tick.getRank())) {
                int scaledPosition = ystart - tick.getScaledPosition();
                valueA += "M" + xstart + "," + scaledPosition + "L" + (xstart - MAJOR_TICK_LENGTH) + "," + scaledPosition;
                String valueRepr = formatDouble(tick.getValue() / valuePostfix.getFactor(), configurator.getLabelPrecision()) + valuePostfix.getName();
                Text lbl = factory.createText(valueRepr, lblxstart, scaledPosition);
                lbl.setAttribute("font-size", String.valueOf(DEFAULT_FONT_SIZE));
                lbl.setAttribute("text-anchor", "end");
                container.add(lbl);
                if (configurator.getGridType() == GridType.Both || configurator.getGridType() == GridType.Value) {
                    valueGL += "M" + xstart + "," + scaledPosition + "L" + xend + "," + scaledPosition;
                }
            } else if (Rank.MINOR.equals(tick.getRank())) {
                int scaledPosition = ystart - tick.getScaledPosition();
                valueA += "M" + xstart + "," + scaledPosition + "L" + (xstart - MINOR_TICK_LENGTH) + "," + scaledPosition;
            } else if (Rank.MICRO.equals(tick.getRank())) {
                int scaledPosition = ystart - tick.getScaledPosition();
                valueA += "M" + xstart + "," + scaledPosition + "L" + (xstart - MICRO_TICK_LENGTH) + "," + scaledPosition;
            }
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
            valueGLP.setStrokeDasharray("1,1");
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

    private double calculateMaxValue() {
        Double max = null;
        for (Entry<Metric, List<Double>> entry : datasource.getDataSet().entrySet()) {
            for (Double d : entry.getValue()) {
                if (max == null) {
                    max = d;
                }
                if (max < d) {
                    max = d;
                }
            }
        }
        return ((max == null || max == 0d) ? DEFAULT_MAX_VALUE : max);
    }

    private Group createLegend(int numOfSeries) {
        if (numOfSeries <= 0) {
            return null;
        }
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

        Iterator<String> colors = configurator.getColorIterator();

        for (int i = 0; i < numOfSeries; i++) {
            LegendItem li = new LegendItem(factory, seriesDescription.get(i), LegendIconType.Circle, 0, y);
            li.setColor(colors.next());
            legentHeight = li.getHeight() + Y_SHIFT;
            y += legentHeight;

            legG.add(li);
            if (legendWidth < li.getWidth()) {
                legendWidth = li.getWidth();
            }
            if (iconsize == 0) {
                iconsize = li.getIconSize();
            }
        }
        int w = legendWidth + LEGEND_FRAME_PADDING;

        Rect frame = factory.createRect(-iconsize - LEGEND_FRAME_PADDING, -iconsize * 2 - LEGEND_FRAME_PADDING, w, y + LEGEND_FRAME_PADDING, 0, 0);
        legG.setAttribute("width", String.valueOf(w));
        legG.setAttribute("height", String.valueOf(y));
        legG.add(frame);
        return legG;
    }

    /**
     * Convert <code>double</code> value to its truncated string representation.
     * 
     * @param value
     *            the value to that is to be converted.
     * @param precision
     *            the number of the digits after the decimal point that will be spared.
     * @return
     */
    public static String formatDouble(double value, int precision) {
        String valueRepr = String.valueOf(value);
        int dotIndex = valueRepr.indexOf(".");

        if (dotIndex != -1) {
            valueRepr = valueRepr.substring(0, Math.min(precision + dotIndex + 1, valueRepr.length()));
        }
        return valueRepr;
    }
}
