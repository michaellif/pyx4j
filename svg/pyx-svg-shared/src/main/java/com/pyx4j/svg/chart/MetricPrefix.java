/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Mar 3, 2014
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.svg.chart;

public enum MetricPrefix {

    T() {

        @Override
        public long getFactor() {
            return 1000000000000l;
        }

        @Override
        public String getName() {
            return "T";
        }

    },
    G() {
        @Override
        public long getFactor() {
            return 1000000000;
        }

        @Override
        public String getName() {
            return "G";
        }

    },
    M() {

        @Override
        public long getFactor() {
            return 1000000;
        }

        @Override
        public String getName() {
            return "M";
        }

    },
    K() {

        @Override
        public long getFactor() {
            return 1000;
        }

        @Override
        public String getName() {
            return "K";
        }

    },
    NONE() {

        @Override
        public long getFactor() {
            return 1;
        }

        @Override
        public String getName() {
            return "";
        }

    };

    public abstract long getFactor();

    public abstract String getName();

    public static MetricPrefix getMetricPrefix(double maxValue) {
        if (maxValue % MetricPrefix.T.getFactor() != maxValue) {
            return MetricPrefix.T;
        } else if (maxValue % MetricPrefix.G.getFactor() != maxValue) {
            return MetricPrefix.G;
        } else if (maxValue % MetricPrefix.M.getFactor() != maxValue) {
            return MetricPrefix.M;
        } else if (maxValue % MetricPrefix.K.getFactor() != maxValue) {
            return MetricPrefix.K;
        } else {
            return MetricPrefix.NONE;
        }
    }

    //round up the maximum value to avoid lots of decimal places
    // And allow to see all the value, use ceil
    public double roundUp(double maxValue) {
        return Math.ceil(10.0 * maxValue / this.getFactor()) * this.getFactor() / 10.0;
    }

    public double roundDown(double maxValue) {
        return Math.floor(10.0 * maxValue / this.getFactor()) * this.getFactor() / 10.0;
    }
}