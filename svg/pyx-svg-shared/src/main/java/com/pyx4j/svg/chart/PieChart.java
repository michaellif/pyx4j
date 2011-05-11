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
 * Created on May 3, 2011
 * @author Dad
 * @version $Id$
 */
package com.pyx4j.svg.chart;

import com.pyx4j.svg.basic.Group;
import com.pyx4j.svg.basic.IsSvgElement;
import com.pyx4j.svg.basic.Path;
import com.pyx4j.svg.basic.SvgElement;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.chart.PieChartModel.PieChartSegment;

public class PieChart implements IsSvgElement {

    private final SvgFactory svgFactory;

    private final Group group;

    private final int radius;

    private final int R_SHIFT = 20;

    private final int V_SHIFT = 20; //TODO Ideally both constants has to be calculated dynamically

    public PieChart(SvgFactory svgfactory, PieChartModel model, int radius) {
        this.svgFactory = svgfactory;
        group = svgFactory.createGroup();
        this.radius = radius;
        setPieChartModel(model);
        group.setTransform("translate(" + (radius * 1.5 + 10) + ", " + (radius + 10) + ") scale(1.5 1)");

    }

    private void setPieChartModel(PieChartModel model) {
        double total = 0;
        for (PieChartSegment segment : model.getSegments()) {
            total += segment.getValue();
        }

        double startangle = Math.PI;

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

            final Path edgePath = svgFactory.createPath(edge);

            edgePath.setFill(segment.getColor());
            group.add(edgePath);

            //Cut 1

            String cut1 = "M 0,0" + //
                    " L " + x1 + "," + y1 + // 
                    " L " + x1 + "," + (y1 + pieHeight) + // 
                    " L " + 0 + "," + pieHeight + // 
                    " Z"; // 

            final Path cut1Path = svgFactory.createPath(cut1);

            cut1Path.setFill(segment.getColor());
            group.add(cut1Path);

            //Cut 2

            String cut2 = "M 0,0" + //
                    " L " + x2 + "," + y2 + // 
                    " L " + x2 + "," + (y2 + pieHeight) + // 
                    " L " + 0 + "," + pieHeight + // 
                    " Z"; // 

            final Path cut2Path = svgFactory.createPath(cut2);

            cut2Path.setFill(segment.getColor());
            group.add(cut2Path);

            //Faces

            String face = "M 0,0" + //
                    " L " + x1 + "," + y1 + //
                    " A " + radius + "," + radius + " 0 " + big + " 1 " + x2 + "," + y2 + //
                    " Z"; //

            final Path facePath = svgFactory.createPath(face);

            facePath.setFill(segment.getColor());

            group.add(facePath);

            if (model.isWithLegend()) {
                LegendItem legend = new LegendItem(svgFactory, segment.getCaption(), LegendIconType.Circle, legX, legY);
                legend.setColor(segment.getColor());
                legY = legY + V_SHIFT;
                group.add(legend);

            }

            /*
             * TODO
             * facePath.addMouseOverHandler(new MouseOverHandler() {
             * 
             * @Override
             * public void onMouseOver(MouseOverEvent event) {
             * new Timer() {
             * 
             * @Override
             * public void run() {
             * facePath.setStrokeWidth("4");
             * edgePath.setStrokeWidth("4");
             * facePath.setStroke("lightGray");
             * edgePath.setStroke("lightGray");
             * String translate = "translate(" + (x1 + (x2 - x1) / 2) / 8 + ", " + (y1 +
             * (y2 - y1) / 2) / 8 + ")";
             * facePath.setTransform(translate);
             * edgePath.setTransform(translate);
             * cut1Path.setTransform(translate);
             * cut2Path.setTransform(translate);
             * }
             * }.schedule(100);
             * }
             * });
             * 
             * facePath.addMouseOutHandler(new MouseOutHandler() {
             * 
             * @Override
             * public void onMouseOut(MouseOutEvent event) {
             * new Timer() {
             * 
             * @Override
             * public void run() {
             * facePath.setStrokeWidth("0");
             * edgePath.setStrokeWidth("0");
             * facePath.setStroke(null);
             * edgePath.setStroke(null);
             * facePath.setTransform(null);
             * edgePath.setTransform(null);
             * cut1Path.setTransform(null);
             * cut2Path.setTransform(null);
             * }
             * }.schedule(100);
             * }
             * });
             */

            startangle = endangle;
        }

    }

    @Override
    public SvgElement asSvgElement() {
        return group;
    }

}
