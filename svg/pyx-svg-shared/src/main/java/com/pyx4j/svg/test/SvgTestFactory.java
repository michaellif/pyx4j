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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pyx4j.svg.basic.Circle;
import com.pyx4j.svg.basic.Ellipse;
import com.pyx4j.svg.basic.Group;
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
import com.pyx4j.svg.chart.ChartTheme;
import com.pyx4j.svg.chart.DataSource;
import com.pyx4j.svg.chart.LegendIconType;
import com.pyx4j.svg.chart.LegendItem;
import com.pyx4j.svg.chart.PieChart;
import com.pyx4j.svg.chart.PieChart2D;
import com.pyx4j.svg.chart.PieChartModel;
import com.pyx4j.svg.chart.PieChartModel.PieChartSegment;

public class SvgTestFactory {

    public static SvgRoot createPieChart2DTest(SvgFactory factory, int x, int y) {
        SvgRoot svgroot = factory.getSvgRoot();
        Group g = factory.createGroup();
        DataSource ds = new DataSource();
        List<Double> values = new ArrayList<Double>(5);
        values.add(180.0);
        values.add(160.0);
        values.add(10.0);
        ds.addDataSet(ds.new Metric("Building 1"), values);

        values = new ArrayList<Double>(5);
        values.add(880.0);
        values.add(580.0);
        values.add(250.0);
        ds.addDataSet(ds.new Metric("Building 2"), values);

        values = new ArrayList<Double>(5);
        values.add(380.0);
        values.add(0.0);
        values.add(380.0);
        ds.addDataSet(ds.new Metric("Building 3"), values);

        values = new ArrayList<Double>(5);
        values.add(380.0);
        values.add(300.0);
        values.add(300.0);
        ds.addDataSet(ds.new Metric("Building 4"), values);

        values = new ArrayList<Double>(5);
        values.add(280.0);
        values.add(300.0);
        values.add(100.0);
        ds.addDataSet(ds.new Metric("Building 5"), values);

        values = new ArrayList<Double>(5);
        values.add(80.0);
        values.add(300.0);
        values.add(100.0);
        ds.addDataSet(ds.new Metric("Building 6"), values);

        values = new ArrayList<Double>(5);
        values.add(480.0);
        values.add(200.0);
        values.add(101.0);
        ds.addDataSet(ds.new Metric("Building 7"), values);

        //series descriptors
        List<String> sd = new ArrayList<String>(3);
        sd.add("2008");
        sd.add("2009");
        sd.add("2010");
        ds.setSeriesDescription(sd);

        PieChart2D pchart = new PieChart2D(factory, ds, 100, ChartTheme.Bright, true);
        g.add(pchart);
        g.setTransform("translate(" + x + "," + y + ")");
        svgroot.add(g);
        return svgroot;
    }

    public static SvgRoot createTestRect(SvgFactory factory, int x, int y) {
        SvgRoot svgroot = factory.getSvgRoot();
        Group g = factory.createGroup();
        Rect rect = factory.createRect(x, y, 50, 50, 0, 0);
        g.add(rect);
        rect = factory.createRect(x + 20, y + 20, 50, 50, 5, 5);
        rect.setFill("blue");
        rect.setStroke("green");
        rect.setStrokeWidth("5");
        g.add(rect);
        g.setTransform("translate(" + x + "," + y + ")");
        svgroot.add(g);
        return svgroot;
    }

    public static SvgRoot createTestLine(SvgFactory factory, int x, int y) {
        SvgRoot svgroot = factory.getSvgRoot();
        Group g = factory.createGroup();
        Line line = factory.createLine(5, 5, 55, 55);
        g.add(line);
        line = factory.createLine(25, 5, 75, 55);
        line.setStroke("green");
        line.setStrokeWidth("5");
        g.add(line);
        g.setTransform("translate(" + x + "," + y + ")");
        svgroot.add(g);
        return svgroot;
    }

    public static SvgRoot createTestPath(SvgFactory factory, int x, int y) {
        SvgRoot svgroot = factory.getSvgRoot();
        Group g = factory.createGroup();
        Path path = factory.createPath("M 10 10 L 60 10 L 35 60 z");
        g.add(path);
        path = factory.createPath("M 20 20 L 40 20 L 30 40 z");
        path.setFill("blue");
        path.setStroke("green");
        path.setStrokeWidth("3");
        g.add(path);
        g.setTransform("translate(" + x + "," + y + ")");
        svgroot.add(g);
        return svgroot;
    }

    public static SvgRoot createTestCircle(SvgFactory factory, int x, int y) {
        SvgRoot svgroot = factory.getSvgRoot();
        Group g = factory.createGroup();
        Circle circle = factory.createCircle(40, 40, 30);
        g.add(circle);
        circle = factory.createCircle(50, 50, 30);
        circle.setFill("blue");
        circle.setStroke("green");
        circle.setStrokeWidth("3");
        g.add(circle);
        g.setTransform("translate(" + x + "," + y + ")");
        svgroot.add(g);
        return svgroot;
    }

    public static SvgRoot createTestEllipse(SvgFactory factory, int x, int y) {
        SvgRoot svgroot = factory.getSvgRoot();
        Group g = factory.createGroup();
        Ellipse ellipse = factory.createEllipse(50, 50, 40, 20);
        g.add(ellipse);
        ellipse = factory.createEllipse(50, 50, 20, 40);
        ellipse.setFill("blue");
        ellipse.setStroke("green");
        ellipse.setStrokeWidth("3");
        g.add(ellipse);
        g.setTransform("translate(" + x + "," + y + ")");
        svgroot.add(g);
        return svgroot;
    }

    public static SvgRoot createTestPolyline(SvgFactory factory, int x, int y) {
        SvgRoot svgroot = factory.getSvgRoot();
        Group g = factory.createGroup();
        Polyline polyline = factory.createPolyline("5,5 5,50 50,10, 50,60");
        g.add(polyline);
        polyline = factory.createPolyline("15,15 15,60 60,20 60,70");
        polyline.setStroke("green");
        polyline.setStrokeWidth("3");
        g.add(polyline);
        g.setTransform("translate(" + x + "," + y + ")");
        svgroot.add(g);
        return svgroot;
    }

    public static SvgRoot createTestPolygon(SvgFactory factory, int x, int y) {
        SvgRoot svgroot = factory.getSvgRoot();
        Group g = factory.createGroup();
        Polygon polygon = factory.createPolygon("5,5 5,50 50,10, 50,60");
        g.add(polygon);
        polygon = factory.createPolygon("15,15 15,60 60,20 60,70");
        polygon.setFill("blue");
        polygon.setStroke("green");
        polygon.setStrokeWidth("3");
        g.setTransform("translate(" + x + "," + y + ")");
        svgroot.add(g);
        return svgroot;
    }

    public static SvgRoot createTestText(SvgFactory factory, int x, int y) {
        SvgRoot svgroot = factory.getSvgRoot();
        Group g = factory.createGroup();
        Text text = factory.createText("Example", 25, 25);
        g.add(text);
        text = factory.createText("Example", 30, 30);
        text.setFill("blue");
        text.setStroke("green");
        g.add(text);
        g.setTransform("translate(" + x + "," + y + ")");
        svgroot.add(g);
        return svgroot;
    }

    public static SvgRoot createTestLegendItem(SvgFactory factory, int x, int y) {
        SvgRoot svgroot = factory.getSvgRoot();
        Group g = factory.createGroup();
        LegendItem lc = new LegendItem(factory, "Property 1", LegendIconType.Circle, 20, 25);
        lc.setColor("blue");
        LegendItem lr = new LegendItem(factory, "Property 2", LegendIconType.Rect, 20, 55);
        lr.setColor("green");
        g.add(lc);
        g.add(lr);
        g.setTransform("translate(" + x + "," + y + ")");
        svgroot.add(g);
        return svgroot;
    }

    public static SvgRoot createTestPieChart(SvgFactory factory, int x, int y) {
        SvgRoot svgroot = factory.getSvgRoot();
        Group g = factory.createGroup();
        PieChartModel pchartModel = new PieChartModel();
        pchartModel.addSegment(new PieChartSegment(3, "p1", "red"));
        //  pchartModel.addSegment(new PieChartSegment(5, "p2", "blue"));
/*
 * pchartModel.addSegment(new PieChartSegment(7, "p3", "green"));
 * pchartModel.addSegment(new PieChartSegment(9, "p4", "yellow"));
 * pchartModel.addSegment(new PieChartSegment(11, "p5", "grey"))
 */     ;
        PieChart pchart = new PieChart(factory, pchartModel, 60);
        g.add(pchart);
        g.setTransform("translate(" + x + "," + y + ")");
        svgroot.add(g);
        return svgroot;
    }

    public static SvgRoot createTestBarChart(SvgFactory factory, int x, int y) {
        SvgRoot svgroot = factory.getSvgRoot();
        Group g = factory.createGroup();
        BarChartModel bchartModel = new BarChartModel(Arrays.asList(new String[] { "2008", "2009", "2010" }));
        bchartModel.addItem(new BarChartItem(3, "b1"), "2008");
        bchartModel.addItem(new BarChartItem(5, "b2"), "2008");
        bchartModel.addItem(new BarChartItem(7, "b3"), "2009");
        bchartModel.addItem(new BarChartItem(9, "b4"), "2009");
        bchartModel.addItem(new BarChartItem(11, "b4"), "2010");

        BarChart bchart = new BarChart(factory, bchartModel, 300, 100);
        g.add(bchart);
        g.setTransform("translate(" + x + "," + y + ")");
        svgroot.add(g);
        return svgroot;
    }

}
