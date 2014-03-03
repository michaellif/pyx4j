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
 * Created on Mar 2, 2014
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.svg.chart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.svg.basic.Circle;
import com.pyx4j.svg.basic.Group;
import com.pyx4j.svg.basic.IsSvgElement;
import com.pyx4j.svg.basic.Path;
import com.pyx4j.svg.basic.Rect;
import com.pyx4j.svg.basic.SvgElement;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.Text;
import com.pyx4j.svg.chart.GridBasedChartConfigurator.GridType;
import com.pyx4j.svg.common.Tick;
import com.pyx4j.svg.common.Tick.Rank;
import com.pyx4j.svg.util.Utils;

public class XYChart extends GridBase implements IsSvgElement {

    private final static String LINE_WIDTH = "2";

    private final static int DOT_RADIUS = 4;

    protected final XYChartConfigurator configurator;

    private final SvgFactory factory;

    private final Group container;

    protected final BasicTickProducer tickProducerY;

    protected final BasicTickProducer tickProducerX;

    protected Area canvas;

    private XYData max;

    private XYData min;

    public XYChart(XYChartConfigurator configurator) {
        super();
        this.configurator = configurator;
        factory = configurator.getFactory();
        container = factory.createGroup();
        tickProducerY = new BasicTickProducer();
        tickProducerX = new BasicTickProducer();
        drawBasics();
        drawChart();
    }

    @Override
    public SvgElement asSvgElement() {
        return container;
    }

    private void calculateMinMax() {
        min = new XYData(Double.MAX_VALUE, Double.MAX_VALUE);
        max = new XYData(-Double.MAX_VALUE, -Double.MAX_VALUE);
        for (XYSeries serie : configurator.getSeries()) {
            for (XYData value : serie.dataset) {
                if (max.x < value.x) {
                    max.x = value.x;
                }
                if (max.y < value.y) {
                    max.y = value.y;
                }

                if (min.x > value.x) {
                    min.x = value.x;
                }
                if (min.y > value.y) {
                    min.y = value.y;
                }
            }
        }
    }

    private void drawBasics() {
        calculateMinMax();

        if (configurator.isZeroBased()) {
            min.x = 0;
        }
        if (configurator.isZeroBasedY()) {
            min.y = 0;
        }

        MetricPrefix yPostfix = MetricPrefix.getMetricPrefix(max.y - min.y);
        max.y = yPostfix.roundUp(max.y);
        min.y = yPostfix.roundDown(min.y);

        MetricPrefix xPostfix = MetricPrefix.getMetricPrefix(max.x - min.x);
        max.x = xPostfix.roundUp(max.x);
        min.x = xPostfix.roundDown(min.x);

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

        int maxValLabelLength = String.valueOf(max.y / yPostfix.getFactor()).length() * DEFAULT_FONT_SIZE + VALUE_LABEL_PADDING;
        xstart += PADDING + maxValLabelLength;

        if (configurator.isLegend()) {
            Group legend = createLegend();
            int legY = ((configurator.getHeight()) - Integer.parseInt(legend.getAttribute("height"))) / 2;
            xend = xend - Integer.parseInt(legend.getAttribute("width")) - PADDING;
            legend.setTransform("translate(" + (xend + LEGEND_SPACING + PADDING) + "," + legY + ")");
            container.add(legend);
        }

        tickProducerY.updateTicks(min.y, max.y, ystart - yend);
        tickProducerX.updateTicks(min.x, max.x, xend - xstart);

        //adjust the canvas dimension after the calculations above are done
        canvas = new Area(xstart, ystart, xend - xstart, ystart - yend);

        //X Axis
        {
            String xGL = "";
            String xA = "M" + xstart + "," + ystart + "L" + xend + "," + ystart;
            for (Tick tick : tickProducerX.getTicks()) {
                int scaledPosition = xstart + tick.getScaledPosition();
                if (Rank.MAJOR.equals(tick.getRank())) {
                    xA += "M" + scaledPosition + "," + ystart + "L" + scaledPosition + "," + (ystart + MAJOR_TICK_LENGTH);
                    String valueRepr = formatXDouble(tick.getValue() / xPostfix.getFactor(), configurator.getLabelPrecision()) + xPostfix.getName();
                    Text lbl = factory.createText(valueRepr, scaledPosition, ystart + PADDING);
                    lbl.setAttribute("font-size", String.valueOf(DEFAULT_FONT_SIZE));
                    lbl.setAttribute("text-anchor", "middle");
                    container.add(lbl);
                    if (configurator.getGridType() == GridType.Both || configurator.getGridType() == GridType.Metric) {
                        xGL += "M" + scaledPosition + "," + ystart + "L" + scaledPosition + "," + yend;
                    }
                } else if (Rank.MINOR.equals(tick.getRank())) {
                    xA += "M" + scaledPosition + "," + ystart + "L" + scaledPosition + "," + (ystart + MINOR_TICK_LENGTH);
                } else if (Rank.MICRO.equals(tick.getRank())) {
                    xA += "M" + scaledPosition + "," + ystart + "L" + scaledPosition + "," + (ystart + MICRO_TICK_LENGTH);
                }
            }

            if (xGL.length() > 0) {
                Path valueGLP = factory.createPath(xGL);
                valueGLP.setStroke(GRID_COLOR);
                valueGLP.setStrokeWidth("1");
                valueGLP.setStrokeDasharray("1,1");
                container.add(valueGLP);
            }

            Path xAsix = factory.createPath(xA);
            xAsix.setStroke(AXIS_COLOR);
            xAsix.setStrokeWidth("1");
            container.add(xAsix);
        }

        //Y Axis
        {
            int lblxstart = xstart - VALUE_LABEL_PADDING;

            String yGL = "";
            String yA = "M" + xstart + "," + ystart + "L" + xstart + "," + yend;
            for (Tick tick : tickProducerY.getTicks()) {
                if (Rank.MAJOR.equals(tick.getRank())) {
                    int scaledPosition = ystart - tick.getScaledPosition();
                    yA += "M" + xstart + "," + scaledPosition + "L" + (xstart - MAJOR_TICK_LENGTH) + "," + scaledPosition;
                    String valueRepr = formatYDouble(tick.getValue() / yPostfix.getFactor(), configurator.getLabelPrecision()) + yPostfix.getName();
                    Text lbl = factory.createText(valueRepr, lblxstart, scaledPosition);
                    lbl.setAttribute("font-size", String.valueOf(DEFAULT_FONT_SIZE));
                    lbl.setAttribute("text-anchor", "end");
                    container.add(lbl);
                    if (configurator.getGridType() == GridType.Both || configurator.getGridType() == GridType.Value) {
                        yGL += "M" + xstart + "," + scaledPosition + "L" + xend + "," + scaledPosition;
                    }
                } else if (Rank.MINOR.equals(tick.getRank())) {
                    int scaledPosition = ystart - tick.getScaledPosition();
                    yA += "M" + xstart + "," + scaledPosition + "L" + (xstart - MINOR_TICK_LENGTH) + "," + scaledPosition;
                } else if (Rank.MICRO.equals(tick.getRank())) {
                    int scaledPosition = ystart - tick.getScaledPosition();
                    yA += "M" + xstart + "," + scaledPosition + "L" + (xstart - MICRO_TICK_LENGTH) + "," + scaledPosition;
                }
            }

            if (yGL.length() > 0) {
                Path valueGLP = factory.createPath(yGL);
                valueGLP.setStroke(GRID_COLOR);
                valueGLP.setStrokeWidth("1");
                valueGLP.setStrokeDasharray("1,1");
                container.add(valueGLP);
            }

            Path yAsix = factory.createPath(yA);
            yAsix.setStroke(AXIS_COLOR);
            yAsix.setStrokeWidth("1");
            container.add(yAsix);
        }

    }

    protected final void drawChart() {
        int xstart = canvas.getX();
        int ystart = canvas.getY();
        List<Text> labels = new LinkedList<Text>();

        List<Rect> bars = new ArrayList<>();

        Iterator<String> colors = configurator.getColorIterator();
        for (XYSeries serie : configurator.getSeries()) {
            String path = "";
            String color = colors.next();
            for (XYData value : serie.dataset) {
                //draw series data
                double x = Utils.round((xstart + tickProducerX.getValuePosition(value.x)), 2);
                double y = Utils.round((ystart - tickProducerY.getValuePosition(value.y)), 2);

                switch (configurator.getChartType()) {
                case Line:
                    if (path.length() == 0) {
                        path = "M";
                    } else {
                        path += "L";
                    }
                    path += x + "," + y;
                    break;
                case Bar:
                    int bx = (int) x - configurator.getBarWidth() / 2;
                    int by = (int) y;
                    int height = ystart - (int) y;
                    if (Math.abs(height) < 3) {
                        height = 3;
                        // Place small bar on the X axis
                        if (by == ystart) {
                            by--;
                        }
                    }
                    Rect bar = factory.createRect(bx, by, configurator.getBarWidth(), height, 0, 0);
                    bar.setFill(color);
                    bar.setStroke(color);
                    bars.add(bar);
                    break;
                }

                switch (configurator.getPointsType()) {
                case Circle:
                    Circle dot = factory.createCircle((int) x, (int) y, DOT_RADIUS);
                    dot.setFill(color);
                    dot.setStroke(color);
                    container.add(dot);
                    break;
                case None:
                    break;
                }

                if (configurator.isShowValueLabels()) {
                    String valueRepr = formatYDouble(value.y, configurator.getLabelPrecision());
                    Text label = factory.createText(valueRepr, (int) x, (int) (y - DOT_RADIUS - CHART_LABEL_PADDING));
                    label.setAttribute("text-anchor", "middle");
                    label.setFill(color);
                    labels.add(label);
                }

            }

            if (path.length() > 0) {
                Path line = factory.createPath(path);
                line.setStroke(color);
                line.setStrokeWidth(LINE_WIDTH);
                container.add(line);
            }
        }

        // Order short bars last; So they will be visible and not covered by large one
        Collections.sort(bars, new Comparator<Rect>() {
            @Override
            public int compare(Rect o1, Rect o2) {
                return Integer.compare(o2.getHeight(), o1.getHeight());
            }
        });

        for (Rect bar : bars) {
            container.add(bar);
        }

        for (Text label : labels) {
            container.add(label);
        }
    }

    protected String formatYDouble(double value, int precision) {
        String valueRepr = String.valueOf(value);
        int dotIndex = valueRepr.indexOf(".");

        if (dotIndex != -1) {
            valueRepr = valueRepr.substring(0, Math.min(precision + dotIndex + 1, valueRepr.length()));
        }
        return valueRepr;
    }

    protected String formatXDouble(double value, int precision) {
        String valueRepr = String.valueOf(value);
        int dotIndex = valueRepr.indexOf(".");

        if (dotIndex != -1) {
            valueRepr = valueRepr.substring(0, Math.min(precision + dotIndex + 1, valueRepr.length()));
        }
        return valueRepr;
    }

    private Group createLegend() {
        Group legG = factory.createGroup();
        int Y_SHIFT = 10;
        int legendWidth = 0;
        int legentHeight = 0;
        int iconsize = 0;
        int y = 0;

        Iterator<String> colors = configurator.getColorIterator();

        for (XYSeries serie : configurator.getSeries()) {
            LegendItem li = new LegendItem(factory, serie.seriesDescription, LegendIconType.Circle, 0, y);
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

}
