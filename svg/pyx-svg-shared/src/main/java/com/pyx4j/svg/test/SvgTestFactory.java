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
 * Created on May 4, 2011
 * @author Dad
 * @version $Id$
 */
package com.pyx4j.svg.test;

import java.util.Arrays;

import com.pyx4j.svg.basic.Circle;
import com.pyx4j.svg.basic.Ellipse;
import com.pyx4j.svg.basic.Line;
import com.pyx4j.svg.basic.Path;
import com.pyx4j.svg.basic.Polygon;
import com.pyx4j.svg.basic.Polyline;
import com.pyx4j.svg.basic.Rect;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.SvgRoot;
import com.pyx4j.svg.basic.Text;
import com.pyx4j.svg.chart.BarChart;
import com.pyx4j.svg.chart.BarChartModel;
import com.pyx4j.svg.chart.BarChartModel.BarChartItem;
import com.pyx4j.svg.chart.LegendIconType;
import com.pyx4j.svg.chart.LegendItem;
import com.pyx4j.svg.chart.PieChart;
import com.pyx4j.svg.chart.PieChartModel;
import com.pyx4j.svg.chart.PieChartModel.PieChartSegment;

public class SvgTestFactory {

    public static SvgRoot createTestRect(SvgFactory factory) {
        SvgRoot svgroot = factory.getSvgRoot();
        Rect rect = factory.createRect(5, 5, 50, 50, 0, 0);
        svgroot.add(rect);
        rect = factory.createRect(25, 25, 50, 50, 5, 5);
        rect.setFill("blue");
        rect.setStroke("green");
        rect.setStrokeWidth("5");
        svgroot.add(rect);
        return svgroot;
    }

    public static SvgRoot createTestLine(SvgFactory factory) {
        SvgRoot svgroot = factory.getSvgRoot();
        Line line = factory.createLine(5, 5, 55, 55);
        svgroot.add(line);
        line = factory.createLine(25, 5, 75, 55);
        line.setStroke("green");
        line.setStrokeWidth("5");
        svgroot.add(line);
        return svgroot;
    }

    public static SvgRoot createTestPath(SvgFactory factory) {
        SvgRoot svgroot = factory.getSvgRoot();
        Path path = factory.createPath("M 10 10 L 60 10 L 35 60 z");
        svgroot.add(path);
        path = factory.createPath("M 20 20 L 40 20 L 30 40 z");
        path.setFill("blue");
        path.setStroke("green");
        path.setStrokeWidth("3");
        svgroot.add(path);
        return svgroot;
    }

    public static SvgRoot createTestCircle(SvgFactory factory) {
        SvgRoot svgroot = factory.getSvgRoot();
        Circle circle = factory.createCircle(40, 40, 30);
        svgroot.add(circle);
        circle = factory.createCircle(50, 50, 30);
        circle.setFill("blue");
        circle.setStroke("green");
        circle.setStrokeWidth("3");
        svgroot.add(circle);
        return svgroot;
    }

    public static SvgRoot createTestEllipse(SvgFactory factory) {
        SvgRoot svgroot = factory.getSvgRoot();
        Ellipse ellipse = factory.createEllipse(50, 50, 40, 20);
        svgroot.add(ellipse);
        ellipse = factory.createEllipse(50, 50, 20, 40);
        ellipse.setFill("blue");
        ellipse.setStroke("green");
        ellipse.setStrokeWidth("3");
        svgroot.add(ellipse);
        return svgroot;
    }

    public static SvgRoot createTestPolyline(SvgFactory factory) {
        SvgRoot svgroot = factory.getSvgRoot();
        Polyline polyline = factory.createPolyline("5,5 5,50 50,10, 50,60");
        svgroot.add(polyline);
        polyline = factory.createPolyline("15,15 15,60 60,20 60,70");
        polyline.setStroke("green");
        polyline.setStrokeWidth("3");
        svgroot.add(polyline);
        return svgroot;
    }

    public static SvgRoot createTestPolygon(SvgFactory factory) {
        SvgRoot svgroot = factory.getSvgRoot();
        Polygon polygon = factory.createPolygon("5,5 5,50 50,10, 50,60");
        svgroot.add(polygon);
        polygon = factory.createPolygon("15,15 15,60 60,20 60,70");
        polygon.setFill("blue");
        polygon.setStroke("green");
        polygon.setStrokeWidth("3");
        svgroot.add(polygon);
        return svgroot;
    }

    public static SvgRoot createTestText(SvgFactory factory) {
        SvgRoot svgroot = factory.getSvgRoot();
        Text text = factory.createText("Example", 25, 25);
        svgroot.add(text);
        text = factory.createText("Example", 30, 30);
        text.setFill("blue");
        text.setStroke("green");
        svgroot.add(text);
        return svgroot;
    }

    public static SvgRoot createTestLegendItem(SvgFactory factory) {
        SvgRoot svgroot = factory.getSvgRoot();
        LegendItem lc = new LegendItem(factory, "Property 1", LegendIconType.Circle, 20, 25, 10);
        lc.setColor("blue");
        LegendItem lr = new LegendItem(factory, "Property 2", LegendIconType.Rect, 20, 55, 15);
        lr.setColor("green");
        svgroot.add(lc);
        svgroot.add(lr);
        return svgroot;
    }

    public static SvgRoot createTestPieChart(SvgFactory factory) {
        SvgRoot svgroot = factory.getSvgRoot();
        PieChartModel pchartModel = new PieChartModel();
        pchartModel.addSegment(new PieChartSegment(3, "p1", "red"));
        pchartModel.addSegment(new PieChartSegment(5, "p2", "blue"));
        pchartModel.addSegment(new PieChartSegment(7, "p3", "green"));
        pchartModel.addSegment(new PieChartSegment(9, "p4", "yellow"));
        pchartModel.addSegment(new PieChartSegment(11, "p5", "grey"));
        PieChart pchart = new PieChart(factory, pchartModel, 60);
        svgroot.add(pchart);
        return svgroot;
    }

    public static SvgRoot createTestBarChart(SvgFactory factory) {
        SvgRoot svgroot = factory.getSvgRoot();
        BarChartModel bchartModel = new BarChartModel(Arrays.asList(new String[] { "2008", "2009", "2010" }));
        bchartModel.addItem(new BarChartItem(3, "b1"), "2008");
        bchartModel.addItem(new BarChartItem(5, "b2"), "2008");
        bchartModel.addItem(new BarChartItem(7, "b3"), "2009");
        bchartModel.addItem(new BarChartItem(9, "b4"), "2009");
        bchartModel.addItem(new BarChartItem(11, "b4"), "2010");

        BarChart bchart = new BarChart(factory, bchartModel, 300, 100);
        svgroot.add(bchart);
        return svgroot;
    }

}
