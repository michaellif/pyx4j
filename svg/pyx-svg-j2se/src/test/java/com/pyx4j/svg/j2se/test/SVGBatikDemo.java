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
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGDocument;

import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.basic.SvgRoot;
import com.pyx4j.svg.j2se.SvgFactoryForBatik;
import com.pyx4j.svg.j2se.SvgRootImpl;
import com.pyx4j.svg.test.SvgTestFactory;

public class SVGBatikDemo {
    public static void main(String[] args) throws IOException {
        // Create an SVG document.
        SvgFactory factory = new SvgFactoryForBatik();

        SvgRoot svgroot = factory.getSvgRoot();
        Document doc = ((SvgRootImpl) svgroot).getDocument();

        ((SvgRootImpl) svgroot).setAttributeNS(null, "width", "1000");
        ((SvgRootImpl) svgroot).setAttributeNS(null, "height", "800");

        /**
         * TODO Most likely will not render properly. Finish it up when the JSVGCanvas is
         * resolved
         */
        SvgTestFactory.createTestRect(factory);
        SvgTestFactory.createTestLine(factory);
        SvgTestFactory.createTestPath(factory);
        SvgTestFactory.createTestCircle(factory);
        SvgTestFactory.createTestEllipse(factory);
        SvgTestFactory.createTestPolyline(factory);
        SvgTestFactory.createTestPolygon(factory);
        SvgTestFactory.createTestText(factory);
        SvgTestFactory.createTestLegendItem(factory);
        SvgTestFactory.createTestPieChart(factory);
        SvgTestFactory.createTestBarChart(factory);

        SVGGraphics2D g = new SVGGraphics2D(doc);
        g.setSVGCanvasSize(new Dimension(800, 1000));

        //TODO output into the log
        Writer out = new OutputStreamWriter(System.out, "UTF-8");
        g.stream(((SvgRootImpl) svgroot).getRootNode(), out, true, true);

        JSVGCanvas canvas = new JSVGCanvas();
        JFrame f = new JFrame();
        f.getContentPane().add(canvas);
        canvas.setSVGDocument((SVGDocument) doc);
        f.pack();
        f.setVisible(true);

    }

}
