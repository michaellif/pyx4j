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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pyx4j.svg.basic.Circle;
import com.pyx4j.svg.basic.Group;
import com.pyx4j.svg.basic.Path;
import com.pyx4j.svg.basic.Rect;
import com.pyx4j.svg.basic.Text;
import com.pyx4j.svg.chart.DataSource.Metric;
import com.pyx4j.svg.util.Utils;

public class PieChart2D extends ArcBasedChart {

    private final static int X_SHIFT = 40;

    private final static int Y_SHIFT = 10;

    private final static int LEGEND_FRAME_PADDING = 5;

    private final static int TITLE_FONT_SIZE = 20;

    private final static int TITLE_PADDING = 10;

    public PieChart2D(ArcBasedChartConfigurator configurator) {
        super(configurator);
    }

    @Override
    protected int drawSeries(Map<Metric, Double> series, final int x, final int y, String seriestitle) {
        List<Text> labels = new LinkedList<Text>();
        double total = 0;
        int computedwidth = 0;
        int xx;
        int yy;
        for (Entry<Metric, Double> entry : series.entrySet()) {
            total += entry.getValue();
        }

        if (total == 0)
            return 0;
        double x2;
        double y2;
        configurator.getTheme().rewind();
        if (seriestitle != null && seriestitle.length() > 0) {
            xx = x;
            yy = y + PADDING;
            x2 = PADDING;
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

        computedwidth = xx + radius + 2 * PADDING;

        int legY = 0;
        int legentHeight = 0;
        int legendWidth = 0;
        int iconsize = 0;

        Group legG = null;
        if (configurator.isLegend())
            legG = factory.createGroup();

        if (series.entrySet().size() == 1) { // 100%
            Circle c = factory.createCircle(xx, yy, radius);
            c.setFill(configurator.getTheme().getNextColor());
            c.setStrokeWidth("0");
            container.add(c);

        } else {
            double valueSum = 0;
            for (Entry<Metric, Double> entry : series.entrySet()) {
                double value = entry.getValue() * 2 * Math.PI / total;
                valueSum += value;
                String arc = value > Math.PI ? "1,1 " : "0,1 ";
                String path = "M" + xx + "," + yy + "L" + x2 + "," + y2 + "A" + radius + "," + radius + " 0 " + arc;
                x2 = Utils.round(xx - radius * Math.cos(valueSum), 2);
                y2 = Utils.round(yy - radius * Math.sin(valueSum), 2);

                path += x2 + "," + y2 + "Z";
                Path p = factory.createPath(path);
                String color = configurator.getTheme().getNextColor();
                p.setFill(color);
                p.setStrokeWidth("0");
                p.setStroke(color);
                if (configurator.isLegend()) {
                    LegendItem li = createLegendItem(entry.getKey().getCaption(), 0, legY, color);
                    legentHeight = li.getHeight() + Y_SHIFT;
                    legY += legentHeight;

                    legG.add(li);
                    if (legendWidth < li.getWidth())
                        legendWidth = li.getWidth();
                    if (iconsize == 0)
                        iconsize = li.getIconSize();
                }
                if (configurator.isShowValueLabels()) {
                    //TODO finish
                    Text label = factory.createText(String.valueOf(value), xx, yy);
                    // labels.add(label);

                }
                container.add(p);

            }
            if (configurator.isLegend()) {
                Rect frame = factory.createRect(-iconsize - LEGEND_FRAME_PADDING, -iconsize * 2 - LEGEND_FRAME_PADDING, legendWidth + LEGEND_FRAME_PADDING,
                        legY + LEGEND_FRAME_PADDING, 0, 0);
                legG.add(frame);
                legG.setTransform("translate(" + (xx + radius + X_SHIFT) + "," + (yy - (legY - legentHeight) / 2) + ")");
                container.add(legG);
                computedwidth += legendWidth + LEGEND_FRAME_PADDING;
            }

            for (Text label : labels) {
                container.add(label);
            }

        }
        return computedwidth;
    }

    private LegendItem createLegendItem(String label, int x, int y, String color) {
        LegendItem li = new LegendItem(factory, label, LegendIconType.Circle, x, y);
        li.setColor(color);
        return li;
    }

}
