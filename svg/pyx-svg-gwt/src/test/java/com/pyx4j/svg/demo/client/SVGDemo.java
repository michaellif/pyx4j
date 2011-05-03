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
 * Created on 2011-05-01
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.svg.demo.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

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
import com.pyx4j.svg.chart.LegendIconType;
import com.pyx4j.svg.gwt.SvgFactoryForGwt;
import com.pyx4j.svg.gwt.chart.LegendItem;

public class SVGDemo implements EntryPoint {

    @Override
    public void onModuleLoad() {

        VerticalPanel content = new VerticalPanel();

        RootPanel.get().add(content);

        SvgFactory svgFactory = new SvgFactoryForGwt();

        content.add(new HTML("<h5>SVG Demo</h5>"));

        //=========================================//

        content.add(new HTML("Rect"));
        {
            SvgRoot svgPanel = svgFactory.createSvgRoot();
            ((Widget) svgPanel).setSize("150px", "100px");

            Rect rect = svgFactory.createRect(5, 5, 50, 50, 0, 0);
            svgPanel.add(rect);

            rect = svgFactory.createRect(25, 25, 50, 50, 5, 5);
            rect.setFill("blue");
            rect.setStroke("green");
            rect.setStrokeWidth("5");
            svgPanel.add(rect);

            content.add((Widget) svgPanel);
        }

        //=========================================//

        content.add(new HTML("Line"));
        {
            SvgRoot svgPanel = svgFactory.createSvgRoot();
            ((Widget) svgPanel).setSize("150px", "100px");

            Line line = svgFactory.createLine(5, 5, 55, 55);
            svgPanel.add(line);

            line = svgFactory.createLine(25, 5, 75, 55);
            line.setStroke("green");
            line.setStrokeWidth("5");
            svgPanel.add(line);

            content.add((Widget) svgPanel);
        }

        //=========================================//

        content.add(new HTML("Path"));
        {
            SvgRoot svgPanel = svgFactory.createSvgRoot();
            ((Widget) svgPanel).setSize("150px", "100px");

            Path path = svgFactory.createPath("M 10 10 L 60 10 L 35 60 z");
            svgPanel.add(path);

            path = svgFactory.createPath("M 20 20 L 40 20 L 30 40 z");
            path.setFill("blue");
            path.setStroke("green");
            path.setStrokeWidth("3");
            svgPanel.add(path);

            content.add((Widget) svgPanel);
        }

        //=========================================//

        content.add(new HTML("Circle"));
        {
            SvgRoot svgPanel = svgFactory.createSvgRoot();
            ((Widget) svgPanel).setSize("150px", "100px");

            Circle circle = svgFactory.createCircle(40, 40, 30);
            svgPanel.add(circle);

            circle = svgFactory.createCircle(50, 50, 30);
            circle.setFill("blue");
            circle.setStroke("green");
            circle.setStrokeWidth("3");
            svgPanel.add(circle);

            content.add((Widget) svgPanel);
        }

        //=========================================//

        content.add(new HTML("Elipse"));
        {
            SvgRoot svgPanel = svgFactory.createSvgRoot();
            ((Widget) svgPanel).setSize("150px", "100px");

            Ellipse ellipse = svgFactory.createEllipse(50, 50, 40, 20);
            svgPanel.add(ellipse);

            ellipse = svgFactory.createEllipse(50, 50, 20, 40);
            ellipse.setFill("blue");
            ellipse.setStroke("green");
            ellipse.setStrokeWidth("3");
            svgPanel.add(ellipse);

            content.add((Widget) svgPanel);
        }

        //=========================================//

        content.add(new HTML("Polyline"));
        {
            SvgRoot svgPanel = svgFactory.createSvgRoot();
            ((Widget) svgPanel).setSize("150px", "100px");

            Polyline polyline = svgFactory.createPolyline("5,5 5,50 50,10, 50,60");
            svgPanel.add(polyline);

            polyline = svgFactory.createPolyline("15,15 15,60 60,20 60,70");
            polyline.setStroke("green");
            polyline.setStrokeWidth("3");
            svgPanel.add(polyline);

            content.add((Widget) svgPanel);
        }

        //=========================================//

        content.add(new HTML("Polyline"));
        {
            SvgRoot svgPanel = svgFactory.createSvgRoot();
            ((Widget) svgPanel).setSize("150px", "100px");

            Polygon polygon = svgFactory.createPolygon("5,5 5,50 50,10, 50,60");
            svgPanel.add(polygon);

            polygon = svgFactory.createPolygon("15,15 15,60 60,20 60,70");
            polygon.setFill("blue");
            polygon.setStroke("green");
            polygon.setStrokeWidth("3");
            svgPanel.add(polygon);

            content.add((Widget) svgPanel);
        }

        //=========================================//

        content.add(new HTML("Text"));
        {
            SvgRoot svgPanel = svgFactory.createSvgRoot();
            ((Widget) svgPanel).setSize("150px", "100px");

            Text text = svgFactory.createText("Example", 25, 25);
            svgPanel.add(text);

            text = svgFactory.createText("Example", 30, 30);
            text.setFill("blue");
            text.setStroke("green");
            svgPanel.add(text);

            content.add((Widget) svgPanel);
        }

        //=========================================//

        content.add(new HTML("Legend Item"));
        {
            SvgRoot svgPanel = svgFactory.createSvgRoot();
            ((Widget) svgPanel).setSize("150px", "100px");

            LegendItem lc = new LegendItem("Property 1", LegendIconType.Circle, svgFactory, 20, 25, 15);
            lc.setColor("blue");
            LegendItem lr = new LegendItem("Property 2", LegendIconType.Rect, svgFactory, 20, 55, 15);
            lr.setColor("green");

            svgPanel.add(lc);
            svgPanel.add(lr);
            content.add((Widget) svgPanel);
        }
    }
}
