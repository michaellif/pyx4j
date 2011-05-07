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

import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.SvgRoot;
import com.pyx4j.svg.gwt.SvgFactoryForGwt;
import com.pyx4j.svg.test.SvgTestFactory;

public class SVGDemo implements EntryPoint {

    @Override
    public void onModuleLoad() {

        VerticalPanel content = new VerticalPanel();

        RootPanel.get().add(content);

        SvgFactory svgFactory = new SvgFactoryForGwt();

        SvgRoot svgPanel;

        content.add(new HTML("<h5>SVG Demo</h5>"));

        //=========================================//

        content.add(new HTML("Rect"));
        svgPanel = SvgTestFactory.createTestRect(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("150px", "100px");
        content.add((Widget) svgPanel);

        //=========================================//

        content.add(new HTML("Line"));
        svgPanel = SvgTestFactory.createTestLine(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("150px", "100px");
        content.add((Widget) svgPanel);

        //=========================================//

        content.add(new HTML("Path"));
        svgPanel = SvgTestFactory.createTestPath(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("150px", "100px");
        content.add((Widget) svgPanel);

        //=========================================//

        content.add(new HTML("Circle"));
        svgPanel = SvgTestFactory.createTestCircle(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("150px", "100px");
        content.add((Widget) svgPanel);

        //=========================================//

        content.add(new HTML("Ellipse"));
        svgPanel = SvgTestFactory.createTestEllipse(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("150px", "100px");
        content.add((Widget) svgPanel);

        //=========================================//

        content.add(new HTML("Polyline"));
        svgPanel = SvgTestFactory.createTestPolyline(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("150px", "100px");
        content.add((Widget) svgPanel);

        //=========================================//

        content.add(new HTML("Polygon"));
        svgPanel = SvgTestFactory.createTestPolygon(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("150px", "100px");
        content.add((Widget) svgPanel);

        //=========================================//

        content.add(new HTML("Text"));
        svgPanel = SvgTestFactory.createTestText(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("150px", "100px");
        content.add((Widget) svgPanel);

        //=========================================//

        content.add(new HTML("Legend Item"));
        svgPanel = SvgTestFactory.createTestLegendItem(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("150px", "150px");
        content.add((Widget) svgPanel);

        //=========================================//

        content.add(new HTML("Pie Chart"));
        svgPanel = SvgTestFactory.createTestPieChart(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("300px", "200px");
        content.add((Widget) svgPanel);

        //=========================================//

        content.add(new HTML("Bar Chart"));
        svgPanel = SvgTestFactory.createTestBarChart(svgFactory, 0, 0);
        ((Widget) svgPanel).setSize("750px", "150px");
        content.add((Widget) svgPanel);

    }
}
