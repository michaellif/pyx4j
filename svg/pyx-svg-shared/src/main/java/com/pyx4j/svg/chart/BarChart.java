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

public class BarChart implements IsSvgElement {

    private final SvgFactory svgFactory;

    private final Group group;

    private String id;

    private final int width;

    private final int height;

    private final int R_SHIFT = 200;

    private final int T_SHIFT = 20; //TODO Ideally both constants has to be calculated dynamically

    public BarChart(SvgFactory svgfactory, BarChartModel model, int width, int height) {
        this.svgFactory = svgfactory;
        group = svgFactory.createGroup();
        this.width = width;
        this.height = height;
        setBarChartModel(model);
        group.setTransform("translate(20 120)");

    }

    public void setBarChartModel(BarChartModel model) {

        //   List<String> positions = model.getPositions();

        double x = 0;

        int legY = -height;
        int legX = width + R_SHIFT;

        for (int i = 0; i < 18; i++) {

            x += 20;

            String color = "#333";

            if (i % 3 == 0) {
                x += 10;
                color = "#888";
            } else if (i % 3 == 1) {
                color = "#666";
            } else if (i % 3 == 2) {
                color = "#ccc";
            }

            double height = Math.random() * 100;

            double width = 20;

            {
                String frontFace = "M " + x + "," + (-height) + //
                        " L " + x + "," + 0 + //
                        " L " + (x + width) + "," + 0 + //
                        " L " + (x + width) + "," + (-height) + //
                        " Z"; // 

                final Path frontFacePath = svgFactory.createPath(frontFace);
                frontFacePath.setFill(color);
                group.add(frontFacePath);
            }

            {
                String backFace = "M " + (x + width) + "," + (-height) + //
                        " L " + (x + width) + "," + 0 + //
                        " L " + (x + width + width / 2) + "," + (-width / 2) + //
                        " L " + (x + width + width / 2) + "," + (-height - width / 2) + //
                        " Z"; // 

                final Path backFacePath = svgFactory.createPath(backFace);
                backFacePath.setFill(color);
                group.add(backFacePath);
            }

            {
                String topFace = "M " + x + "," + (-height) + //
                        " L " + (x + width / 2) + "," + (-height - width / 2) + //
                        " L " + (x + width + width / 2) + "," + (-height - width / 2) + //
                        " L " + (x + width) + "," + (-height) + //
                        " Z"; // 

                final Path topFacePath = svgFactory.createPath(topFace);
                topFacePath.setFill(color);
                group.add(topFacePath);

            }

        }
        if (model.isWhithLegend()) {
            String color = "";
            String title;
            for (int i = 0; i < 3; i++) {
                title = "property: " + i;
                LegendItem legend = new LegendItem(svgFactory, title, LegendIconType.Rect, legX, legY);
                if (i % 3 == 0) {
                    color = "#888";
                } else if (i % 3 == 1) {
                    color = "#666";
                } else if (i % 3 == 2) {
                    color = "#ccc";
                }

                legend.setColor(color);

                legY = legY + T_SHIFT;
                group.add(legend);

            }

        }
/*
 * for (GraphicsElement element : components) {
 * add(element);
 * }
 */
    }

    @Override
    public SvgElement asSvgElement() {
        return group;
    }

}
