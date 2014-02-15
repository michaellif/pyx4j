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
import java.util.List;

public class BarChart extends Group {

    private final int width;

    private final int height;

    private final int R_SHIFT = 200;

    private final int T_SHIFT = 20; //TODO Ideally both constants has to be calculated dynamically

    public BarChart(BarChartModel model, int width, int height) {
        this.width = width;
        this.height = height;
        setBarChartModel(model);
        setTransform("translate(20 120)");

    }

    public void setBarChartModel(BarChartModel model) {
        clear();

        List<String> positions = model.getPositions();

        ArrayList<GraphicsElement> components = new ArrayList<>();

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

                final Path frontFacePath = new Path(frontFace);
                frontFacePath.setFill(color);
                components.add(frontFacePath);
            }

            {
                String backFace = "M " + (x + width) + "," + (-height) + //
                        " L " + (x + width) + "," + 0 + //
                        " L " + (x + width + width / 2) + "," + (-width / 2) + //
                        " L " + (x + width + width / 2) + "," + (-height - width / 2) + //
                        " Z"; // 

                final Path backFacePath = new Path(backFace);
                backFacePath.setFill(color);
                components.add(backFacePath);
            }

            {
                String topFace = "M " + x + "," + (-height) + //
                        " L " + (x + width / 2) + "," + (-height - width / 2) + //
                        " L " + (x + width + width / 2) + "," + (-height - width / 2) + //
                        " L " + (x + width) + "," + (-height) + //
                        " Z"; // 

                final Path topFacePath = new Path(topFace);
                topFacePath.setFill(color);
                components.add(topFacePath);

            }

        }
        if (model.isWhithLegend()) {
            String color = "";
            String title;
            for (int i = 0; i < 3; i++) {
                title = "property: " + i;
                LegendItem legend = new LegendItem(title, LegendIcon.Rect, legX, legY, 15);
                if (i % 3 == 0) {
                    color = "#888";
                } else if (i % 3 == 1) {
                    color = "#666";
                } else if (i % 3 == 2) {
                    color = "#ccc";
                }

                legend.setColor(color);
                legend.setX(legX);
                legend.setY(legY);
                legY = legY + T_SHIFT;
                components.add(legend.getIcon());
                Text ltxt = legend.getText();
                if (ltxt != null)
                    components.add(ltxt);
            }

        }
        for (GraphicsElement element : components) {
            add(element);
        }

    }
}
