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

import com.pyx4j.svg.basic.Circle;
import com.pyx4j.svg.basic.Line;
import com.pyx4j.svg.basic.Path;
import com.pyx4j.svg.basic.Text;
import com.pyx4j.svg.chart.DataSource.Metric;
import com.pyx4j.svg.util.Utils;

public class Gauge extends ArcBasedChart {

    private final static int TITLE_FONT_SIZE = 20;

    private final static int SCALE_LABEL_SPACING = 10;

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
        double scaleMax = configurator.getScaleMaximum();
        double valueToDepict = 0;//take the first value in a series
        String metricText = null;
        boolean valueSet = false;

        for (Entry<Metric, Double> entry : series.entrySet()) {
            double v = entry.getValue();
            if (!valueSet) {
                valueToDepict = v;
                if (configurator.isShowValueLabels()) {
                    metricText = entry.getKey().getCaption();
                }
                valueSet = true;

            }
            total += v;
            if (v > scaleMax) {
                scaleMax = v;
            }

        }
        if (total == 0 || scaleMax == 0) {
            return 0;
        }

        double sectorWeight = Utils.round(scaleMax / TOTAL_SECTORS, 2);
        int computedwidth = 0;
        int innerRadius = radius - RIM_WIDTH;
        String outerArc = (SECTOR_SIZE) > Math.PI ? " 0 1 1 " : " 0 0 1 ";
        String innerArc = (SECTOR_SIZE) < Math.PI ? " 0 0 0 " : " 0 1 0 ";
        int xStart;
        int yStart;
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
        double scaleValue = 0;
        double valueSumAdjusted = valueSum + STARTING_ANGLE;

        /**
         * Draw the scale
         */
        //first label
        Text lbl = createLabel(String.valueOf((int) scaleValue), valueSumAdjusted, xOuter, yOuter);
        computedWidth = Integer.parseInt(lbl.getAttribute("x"));
        container.add(lbl);
        for (int i = 0; i < TOTAL_SECTORS; i++) {
            valueSum += SECTOR_SIZE;
            valueSumAdjusted = valueSum + STARTING_ANGLE;
            scaleValue += sectorWeight;
            //outer arc
            String path = "M" + xInner + "," + yInner + "L" + xOuter + "," + yOuter + "A" + radius + "," + radius + outerArc;
            xOuter = Utils.round(xStart - radius * Math.cos(valueSumAdjusted), 2);
            yOuter = Utils.round(yStart - radius * Math.sin(valueSumAdjusted), 2);
            path += xOuter + "," + yOuter;
            //put label
            double xTmp = Utils.round(xStart - innerRadius * Math.cos(valueSumAdjusted), 2);
            double yTmp = Utils.round(yStart - innerRadius * Math.sin(valueSumAdjusted), 2);
            lbl = createLabel(String.valueOf((int) scaleValue), valueSumAdjusted, xOuter, yOuter);
            container.add(lbl);

            //inner arc
            path += "L" + xTmp + "," + yTmp + "A" + innerRadius + "," + innerRadius + innerArc + xInner + "," + yInner + "Z";

            xInner = xTmp;
            yInner = yTmp;

            Path p = factory.createPath(path);
            p.setFill(configurator.getTheme().getNextColor());
            container.add(p);

        }
        computedwidth = Integer.parseInt(lbl.getAttribute("x")) - computedwidth;
        /**
         * Draw the arrow
         */
        Circle c = factory.createCircle(xStart, yStart, 10);
        if (metricText != null && !metricText.isEmpty()) {
            lbl = factory.createText(metricText, xStart, yStart - 10 - SCALE_LABEL_SPACING);
            lbl.setAttribute("text-anchor", "middle");
            container.add(lbl);

        }
        //value position
        valueToDepict = valueToDepict * GAUGE_SECTOR / scaleMax;
        xInner = Utils.round(xStart - innerRadius * Math.cos(STARTING_ANGLE + valueToDepict), 2);
        yInner = Utils.round(yStart - innerRadius * Math.sin(STARTING_ANGLE + valueToDepict), 2);
        Line arrow = factory.createLine(xStart, yStart, (int) xInner, (int) yInner);

        container.add(c);
        container.add(arrow);
        return computedwidth;

    }

    private Text createLabel(String text, double value, double x, double y) {
        String anchor;
        int _x = (int) x;
        int _y = (int) y;
        int lableSpacing = Text.DEFAULT_FONT_SIZE + 5;

        if (value < Math.PI / 2) {
            anchor = "end";
            _x -= lableSpacing;
        } else if (value > Math.PI / 2) {
            anchor = "start";
            _x += lableSpacing;
        } else {
            anchor = "middle";
            _y -= lableSpacing;
        }

        Text lbl = factory.createText(String.valueOf(text), _x, _y);
        lbl.setAttribute("text-anchor", anchor);
        return lbl;

    }
}
