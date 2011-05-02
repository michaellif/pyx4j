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
 * Created on Apr 25, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client.svg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BarChartModel {
    private final HashMap<String, List<BarChartItem>> items;

    private final List<String> positions;

    private final List<String> colors;

    private boolean wihtLegend;

    public BarChartModel(List<String> positions) {
        this.positions = positions;
        items = new HashMap<String, List<BarChartItem>>();
        for (String position : positions) {
            items.put(position, new ArrayList<BarChartItem>());
        }
        colors = new ArrayList<String>();
        colors.add("red");
        colors.add("yellow");
        colors.add("blue");
        colors.add("green");
        colors.add("orange");
        this.wihtLegend = true;
    }

    public void addItem(BarChartItem item, String position) {
        items.get(position).add(item);
    }

    public String getColor(int position) {
        return colors.get(position);
    }

    public Map<String, List<BarChartItem>> getItems() {
        return items;
    }

    public List<String> getPositions() {
        return positions;
    }

    public List<BarChartItem> getItems(String position) {
        return items.get(position);
    }

    public boolean isWihtLegend() {
        return wihtLegend;
    }

    public void setWihtLegend(boolean wihtLegend) {
        this.wihtLegend = wihtLegend;
    }

    public static class BarChartItem {

        private final double value;

        private final String caption;

        public BarChartItem(double value, String caption) {
            this.value = value;
            this.caption = caption;
        }

        public double getValue() {
            return value;
        }

        public String getCaption() {
            return caption;
        }

    }
}
