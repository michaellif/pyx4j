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

import java.util.Map;
import java.util.Map.Entry;

import com.pyx4j.svg.basic.Path;
import com.pyx4j.svg.basic.Text;
import com.pyx4j.svg.chart.DataSource.Metric;
import com.pyx4j.svg.util.Utils;

public class Gauge extends ArcBasedChart {

    private final static int TITLE_FONT_SIZE = 20;

    private final static int RIM_WIDTH = 20;

    private final static double STARTING_ANGLE = Utils.degree2radian(-45);

    private final static double GAUGE_SECTOR = Utils.degree2radian(270);

    private final static int TOTAL_SECTORS = 4;

    private final static double SECTOR_SIZE = GAUGE_SECTOR / TOTAL_SECTORS;

    public Gauge(ArcBasedChartConfigurator configurator) {
        super(configurator);

    }

    //assumes only one entry per metric
    @Override
    protected int drawSeries(Map<Metric, Double> series, final int x, final int y, String seriestitle) {
        double total = 0;
        int computedwidth = 0;
        int innerRadius = radius - RIM_WIDTH;
        String arc = (SECTOR_SIZE) > Math.PI ? " 1 1 " : " 0 1 ";
        int xStart;
        int yStart;

        for (Entry<Metric, Double> entry : series.entrySet()) {
            total += entry.getValue();
        }

        if (total == 0) {
            return 0;
        }
        double xOuter;
        double yOuter;
        double xInner;
        double yInner;
        configurator.getTheme().rewind();

        if (seriestitle != null && seriestitle.length() > 0) {
            xStart = x;
            yStart = y + PADDING;
            xOuter = PADDING;
            Text title = factory.createText(seriestitle, radius + PADDING, yStart + radius);
            title.setAttribute("font-size", String.valueOf(TITLE_FONT_SIZE));
            title.setAttribute("text-anchor", "middle");
            container.add(title);
            yOuter = yStart;

        } else {
            xStart = x;
            yStart = y;
            xOuter = PADDING;
            yOuter = y;
        }

        //starting angles
        xOuter = Utils.round(xStart - radius * Math.cos(STARTING_ANGLE), 2);
        yOuter = Utils.round(yStart - radius * Math.sin(STARTING_ANGLE), 2);

        xInner = Utils.round(xStart - innerRadius * Math.cos(STARTING_ANGLE), 2);
        yInner = Utils.round(yStart - innerRadius * Math.sin(STARTING_ANGLE), 2);

        double valueSum = 0;
        for (int i = 0; i < TOTAL_SECTORS; i++) {
            valueSum += SECTOR_SIZE;

            //outer arc
            String path = "M" + xInner + "," + yInner + "L" + xOuter + "," + yOuter + "A" + radius + "," + radius + " 0" + arc;
            xOuter = Utils.round(xStart - radius * Math.cos(STARTING_ANGLE + valueSum), 2);
            yOuter = Utils.round(yStart - radius * Math.sin(STARTING_ANGLE + valueSum), 2);
            path += xOuter + "," + yOuter;

            //inner arc
            path += "M" + xInner + "," + yInner + "A" + (innerRadius) + "," + (innerRadius) + " 0" + arc;
            xInner = Utils.round(xStart - innerRadius * Math.cos(STARTING_ANGLE + valueSum), 2);
            yInner = Utils.round(yStart - innerRadius * Math.sin(STARTING_ANGLE + valueSum), 2);
            path += xInner + "," + yInner + "L" + xOuter + "," + yOuter;

            Path p = factory.createPath(path);
            //   p.setFill(configurator.getTheme().getNextColor());
            container.add(p);
        }
        return computedwidth;

    }
}
