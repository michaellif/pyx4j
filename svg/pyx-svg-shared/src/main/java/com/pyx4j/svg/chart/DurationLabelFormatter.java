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
 * Created on Mar 24, 2014
 * @author vlads
 */
package com.pyx4j.svg.chart;

public class DurationLabelFormatter implements LabelFormatter {

    @Override
    public String format(double value) {
        long min = Math.round(value);
        if (min == 0) {
            return "now";
        }
        boolean past = false;
        if (min < 0) {
            past = true;
            min = -min;
        }

        long h = min / 60;
        min -= h * 60;
        long days = h / 24;
        h -= days * 24;

        StringBuilder sb = new StringBuilder();
        if (past) {
            sb.append('-');
        }

        if (days != 0) {
            sb.append((int) days).append("d");
        }
        if (h != 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append((int) h).append("hr");
        }
        if (min != 0) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append((int) min).append("m");
        }
        return sb.toString();
    }

}
