/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jan 6, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client.svg;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Timer;

import com.pyx4j.widgets.client.svg.PieChartModel.PieChartSegment;

public class PieChart extends Group {

    private final int radius;

    private final int R_SHIFT = 20;

    private final int V_SHIFT = 20; //TODO Ideally both constants has to be calculated dynamically

    public PieChart(PieChartModel model, int radius) {
        this.radius = radius;
        setPieChartModel(model);
        setTransform("translate(" + (radius * 1.5 + 10) + ", " + (radius + 10) + ") scale(1.5 1)");

    }

    private void setPieChartModel(PieChartModel model) {
        clear();

        double total = 0;
        for (PieChartSegment segment : model.getSegments()) {
            total += segment.getValue();
        }

        double startangle = Math.PI;

        ArrayList<GraphicsElement> components = new ArrayList<GraphicsElement>();

        int legY = -radius + V_SHIFT;
        int legX = radius + R_SHIFT;

        for (int i = 0; i < model.getSegments().size(); i++) {
            //for (int i = 0; i < 3; i++) {

            PieChartSegment segment = model.getSegments().get(i);

            double endangle = startangle + model.getSegments().get(i).getValue() * Math.PI * 2 / total;

            final double x1 = radius * Math.sin(startangle);
            final double y1 = -radius * Math.cos(startangle);
            final double x2 = radius * Math.sin(endangle);
            final double y2 = -radius * Math.cos(endangle);

            int big = endangle - startangle > Math.PI ? 1 : 0;

            int pieHeight = 10;

            //Edges

            String edge = "M " + x1 + "," + (y1 + pieHeight) + //
                    " A " + radius + "," + radius + " 0 " + big + " 1 " + x2 + "," + (y2 + pieHeight) + //
                    " L " + x2 + "," + y2 + //
                    " A " + radius + "," + radius + " 0 " + big + " 0 " + x1 + "," + y1 + // 
                    " Z"; // 

            final Path edgePath = new Path(edge);

            edgePath.setFill(segment.getColor());
            components.add(0, edgePath);

            //Cut 1

            String cut1 = "M 0,0" + //
                    " L " + x1 + "," + y1 + // 
                    " L " + x1 + "," + (y1 + pieHeight) + // 
                    " L " + 0 + "," + pieHeight + // 
                    " Z"; // 

            final Path cut1Path = new Path(cut1);

            cut1Path.setFill(segment.getColor());
            components.add(0, cut1Path);

            //Cut 2

            String cut2 = "M 0,0" + //
                    " L " + x2 + "," + y2 + // 
                    " L " + x2 + "," + (y2 + pieHeight) + // 
                    " L " + 0 + "," + pieHeight + // 
                    " Z"; // 

            final Path cut2Path = new Path(cut2);

            cut2Path.setFill(segment.getColor());
            components.add(0, cut2Path);

            //Faces

            String face = "M 0,0" + //
                    " L " + x1 + "," + y1 + //
                    " A " + radius + "," + radius + " 0 " + big + " 1 " + x2 + "," + y2 + //
                    " Z"; //

            final Path facePath = new Path(face);

            facePath.setFill(segment.getColor());

            components.add(facePath);

            if (model.isWihtLegend()) {
                LegendItem legend = new LegendItem(segment.getCaption(), LegendIcon.Circle, legX, legY, 6);
                legend.setColor(segment.getColor());
                legend.setX(legX);
                legend.setY(legY);
                legY = legY + V_SHIFT;
                components.add(legend.getIcon());
                Text ltxt = legend.getText();
                if (ltxt != null)
                    components.add(ltxt);
            }

            facePath.addMouseOverHandler(new MouseOverHandler() {
                @Override
                public void onMouseOver(MouseOverEvent event) {
                    new Timer() {
                        @Override
                        public void run() {
                            facePath.setStrokeWidth("4");
                            edgePath.setStrokeWidth("4");
                            facePath.setStroke("lightGray");
                            edgePath.setStroke("lightGray");
                            String translate = "translate(" + (x1 + (x2 - x1) / 2) / 8 + ", " + (y1 + (y2 - y1) / 2) / 8 + ")";
                            facePath.setTransform(translate);
                            edgePath.setTransform(translate);
                            cut1Path.setTransform(translate);
                            cut2Path.setTransform(translate);
                        }
                    }.schedule(100);
                }
            });

            facePath.addMouseOutHandler(new MouseOutHandler() {
                @Override
                public void onMouseOut(MouseOutEvent event) {
                    new Timer() {
                        @Override
                        public void run() {
                            facePath.setStrokeWidth("0");
                            edgePath.setStrokeWidth("0");
                            facePath.setStroke(null);
                            edgePath.setStroke(null);
                            facePath.setTransform(null);
                            edgePath.setTransform(null);
                            cut1Path.setTransform(null);
                            cut2Path.setTransform(null);
                        }
                    }.schedule(100);
                }
            });

            startangle = endangle;

            //            // Now draw a little matching square for the key
            //            var icon = document.createElementNS(SVG.ns, "rect");
            //            icon.setAttribute("x", lx); // Position the square
            //            icon.setAttribute("y", ly + 30 * i);
            //            icon.setAttribute("width", 20); // Size the square
            //            icon.setAttribute("height", 20);
            //            icon.setAttribute("fill", colors[i]); // Same fill color as wedge
            //            icon.setAttribute("stroke", "black"); // Same outline, too.
            //            icon.setAttribute("stroke-width", "2");
            //            canvas.appendChild(icon); // Add to the canvas
            //
            //            // And add a label to the right of the rectangle
            //            var label = document.createElementNS(SVG.ns, "text");
            //            label.setAttribute("x", lx + 30); // Position the text
            //            label.setAttribute("y", ly + 30 * i + 18);
            //            // Text style attributes could also be set via CSS
            //            label.setAttribute("font-family", "sans-serif");
            //            label.setAttribute("font-size", "16");
            //            // Add a DOM text node to the <svg:text> element
            //            label.appendChild(document.createTextNode(labels[i]));
            //            canvas.appendChild(label); // Add text to the canvas
        }

        //add according to order (edges and cuts first, faces last)
        for (GraphicsElement element : components) {
            add(element);
        }
    }
}
