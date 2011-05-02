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

public class PieChartModel {

    private final List<PieChartSegment> segments;

    private boolean withLegend;

    public PieChartModel() {
        this.segments = new ArrayList<PieChartSegment>();
        this.withLegend = true;
    }

    public void addSegment(PieChartSegment segment) {
        segments.add(segment);
    }

    public List<PieChartSegment> getSegments() {
        return segments;
    }

    public boolean isWithLegend() {
        return withLegend;
    }

    public void setWithLegend(boolean withLegend) {
        this.withLegend = withLegend;
    }

    public static class PieChartSegment {

        private final double value;

        private final String caption;

        private final String color;

        public PieChartSegment(double value, String caption, String color) {
            this.value = value;
            this.caption = caption;
            this.color = color;
        }

        public double getValue() {
            return value;
        }

        public String getCaption() {
            return caption;
        }

        public String getColor() {
            return color;
        }

    }
}
