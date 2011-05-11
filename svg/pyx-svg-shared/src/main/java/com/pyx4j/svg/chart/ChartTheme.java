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
 * Created on May 11, 2011
 * @author Dad
 * @version $Id$
 */
package com.pyx4j.svg.chart;

public enum ChartTheme {

    Bright() {

        private final String[] colors = { "#910E76", "#E87A04", "#99B118", "#0E508D", "#FFC91F", "#788E08", "#900E1B", "#38B8BF" };

        private int iterator = -1;

        @Override
        public String getNextColor() {
            if (iterator == (colors.length - 1))
                iterator = 0;
            else
                ++iterator;

            return colors[iterator];
        }

        @Override
        public void rewind() {
            iterator = 0;
        }

    },

    Monochrome() {
        private final String[] colors = { "#000000", "#666362", "#BDBDBD", "#565051", "#736F6E", "#F2F2F2", "#3E3535" };

        private int iterator = -1;

        @Override
        public String getNextColor() {
            if (iterator == (colors.length - 1))
                iterator = 0;
            else
                ++iterator;

            return colors[iterator];
        }

        @Override
        public void rewind() {
            iterator = 0;
        }
    };

    public abstract String getNextColor();

    public abstract void rewind();

}
