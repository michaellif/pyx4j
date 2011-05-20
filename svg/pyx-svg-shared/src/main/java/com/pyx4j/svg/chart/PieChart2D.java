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
 * Created on May 10, 2011
 * @author vadims
 * @version $Id$
 */
package com.pyx4j.svg.chart;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.pyx4j.svg.basic.Circle;
import com.pyx4j.svg.basic.Group;
import com.pyx4j.svg.basic.IsSvgElement;
import com.pyx4j.svg.basic.Path;
import com.pyx4j.svg.basic.Rect;
import com.pyx4j.svg.basic.SvgElement;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.Text;
import com.pyx4j.svg.chart.DataSource.Metric;

public class PieChart2D implements IsSvgElement {
    private final DataSource datasource;

    private final SvgFactory factory;

    private final int radius;

    private final static int X_SHIFT = 40;

    private final static int Y_SHIFT = 10;

    private final static int PADDING = 15;

    private final static int LEGEND_FRAME_PADDING = 5;

    private final static int TITLE_FONT_SIZE = 20;

    private final static int TITLE_PADDING = 10;

    private final boolean showLegend;

    private final ChartTheme theme;

    private final Group container;

    public PieChart2D(SvgFactory factory, DataSource datasource, int radius, ChartTheme theme, boolean showLegend) {
        this.datasource = datasource;
        this.radius = radius;
        this.factory = factory;
        this.showLegend = showLegend;
        this.theme = theme;
        container = this.factory.createGroup();
        drawChart();
    }

    private void drawChart() {

        List<String> sdesc = datasource.getSeriesDescription();
        int x = radius + PADDING;
        int y = radius + PADDING;

        Set<Entry<Metric, List<Double>>> dataset = datasource.getDataSet().entrySet();
        //find out number of series
        int numOfSerires = 0;
        for (Entry<Metric, List<Double>> entry : dataset) {
            int size = entry.getValue().size();
            if (numOfSerires < size)
                numOfSerires = size;
        }

        for (int idx = 0; idx < numOfSerires; idx++) {
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

            drawSeries(series, x, y, SeriesTitle);
            y += radius * 2 + PADDING;
            if (SeriesTitle != null && SeriesTitle.length() > 0)
                y += 2 * PADDING;
        }

    }

    private void drawSeries(Map<Metric, Double> series, final int x, final int y, String seriestitle) {
        double total = 0;
        int xx;
        int yy;
        for (Entry<Metric, Double> entry : series.entrySet()) {
            total += entry.getValue();
        }

        if (total == 0)
            return;
        double x2;
        double y2;
        theme.rewind();
        if (seriestitle != null && seriestitle.length() > 0) {
            xx = x; //+ TITLE_FONT_SIZE;
            yy = y + PADDING;
            x2 = PADDING; //+ TITLE_FONT_SIZE;
            Text title = factory.createText(seriestitle, radius + PADDING, yy - radius);
            title.setAttribute("font-size", String.valueOf(TITLE_FONT_SIZE));
            title.setAttribute("text-anchor", "middle");
            container.add(title);
            //add a gap between title and chart
            yy += TITLE_PADDING;
            y2 = yy;

        } else {
            xx = x;
            yy = y;
            x2 = PADDING;
            y2 = y;
        }

        int legY = 0;
        int legentHeight = 0;
        int legendWidth = 0;
        int iconsize = 0;

        Group legG = null;
        if (showLegend)
            legG = factory.createGroup();

        if (series.entrySet().size() == 1) { // 100%
            Circle c = factory.createCircle(xx, yy, radius);
            c.setFill(theme.getNextColor());
            c.setStrokeWidth("0");
            container.add(c);

        } else {
            double valueSum = 0;
            for (Entry<Metric, Double> entry : series.entrySet()) {
                double value = entry.getValue() * 2 * Math.PI / total;
                valueSum += value;
                String arc = value > Math.PI ? "1,1 " : "0,1 ";
                String path = "M" + xx + "," + yy + "L" + x2 + "," + y2 + "A" + radius + "," + radius + " 0 " + arc;
                x2 = xx - radius * Math.cos(valueSum);
                y2 = yy - radius * Math.sin(valueSum);

                path += x2 + "," + y2 + "Z";
                Path p = factory.createPath(path);
                String color = theme.getNextColor();
                p.setFill(color);
                p.setStrokeWidth("0");
                if (showLegend) {
                    LegendItem li = createLegendItem(entry.getKey().getCaption(), 0, legY, color);
                    legentHeight = li.getHeight() + Y_SHIFT;
                    legY += legentHeight;

                    legG.add(li);
                    if (legendWidth < li.getWidth())
                        legendWidth = li.getWidth();
                    if (iconsize == 0)
                        iconsize = li.getIconSize();
                }
                //TODO implement labels String sv = entry.getValue().toString();
                //  Text t = factory.createText(sv, (int) x2, (int) y2);
                container.add(p);
                //  container.add(t);
            }
            if (showLegend) {
                Rect frame = factory.createRect(-iconsize - LEGEND_FRAME_PADDING, -iconsize * 2 - LEGEND_FRAME_PADDING, legendWidth + LEGEND_FRAME_PADDING,
                        legY + LEGEND_FRAME_PADDING, 0, 0);
                legG.add(frame);
                legG.setTransform("translate(" + (xx + radius + X_SHIFT) + "," + (yy - (legY - legentHeight) / 2) + ")");
                container.add(legG);
            }
        }
    }

    private LegendItem createLegendItem(String label, int x, int y, String color) {
        LegendItem li = new LegendItem(factory, label, LegendIconType.Circle, x, y);
        li.setColor(color);
        return li;
    }

    @Override
    public SvgElement asSvgElement() {
        return container;
    }
}
