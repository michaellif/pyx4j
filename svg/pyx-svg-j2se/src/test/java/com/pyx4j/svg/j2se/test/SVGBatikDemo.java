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
 * @author vadims
 * @version $Id$
 */
package com.pyx4j.svg.j2se.test;

import java.awt.Dimension;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.swing.JFrame;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.JSVGScrollPane;
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGDocument;

import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.SvgRoot;
import com.pyx4j.svg.j2se.SvgFactoryForBatik;
import com.pyx4j.svg.j2se.SvgRootImpl;
import com.pyx4j.svg.test.SvgTestFactory;

public class SVGBatikDemo {
    private static final int HEIGHT = 1000;// 4000;

    public static void main(String[] args) throws IOException {
        // Create an SVG document.
        SvgFactory factory = new SvgFactoryForBatik();

        SvgRoot svgroot = factory.getSvgRoot();
        Document doc = ((SvgRootImpl) svgroot).getDocument();

        ((SvgRootImpl) svgroot).setAttributeNS(null, "width", "800");
        ((SvgRootImpl) svgroot).setAttributeNS(null, "height", String.valueOf(HEIGHT));

/*
 * SvgTestFactory.createTestRect(factory, 10, 10);
 * SvgTestFactory.createTestLine(factory, 10, 110);
 * SvgTestFactory.createTestPath(factory, 10, 210);
 * SvgTestFactory.createTestCircle(factory, 10, 310);
 * SvgTestFactory.createTestEllipse(factory, 10, 410);
 * SvgTestFactory.createTestPolyline(factory, 10, 510);
 * SvgTestFactory.createTestPolygon(factory, 10, 610);
 * SvgTestFactory.createTestText(factory, 10, 710);
 * SvgTestFactory.createTestLegendItem(factory, 10, 810);
 * SvgTestFactory.createLineChart2DTest(factory, 10, 910);
 * SvgTestFactory.createBarChart2DTest(factory, 10, 1350);
 * SvgTestFactory.createPieChart2DTest(factory, 10, 1800);
 */
        SvgTestFactory.createGaugeTest(factory, 20, 10);

        SVGGraphics2D g = new SVGGraphics2D(doc);
        g.setSVGCanvasSize(new Dimension(800, HEIGHT));

        //TODO output into the log
        Writer out = new OutputStreamWriter(System.out, "UTF-8");
        g.stream(((SvgRootImpl) svgroot).getRootNode(), out, true, true);

        JSVGCanvas canvas = new JSVGCanvas();
        canvas.setSVGDocument((SVGDocument) doc);
        JSVGScrollPane view = new JSVGScrollPane(canvas);
        view.setScrollbarsAlwaysVisible(true);
        //  view.setVisible(true);

        JFrame f = new JFrame();
        f.getContentPane().add(view);
        f.pack();
        f.setSize(800, HEIGHT);
        f.setVisible(true);

    }
}
