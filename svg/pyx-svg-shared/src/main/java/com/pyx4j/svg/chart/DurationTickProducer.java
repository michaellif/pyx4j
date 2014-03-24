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
 * Created on Mar 5, 2014
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.svg.chart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pyx4j.svg.basic.TickProducer;
import com.pyx4j.svg.common.Tick;
import com.pyx4j.svg.common.Tick.Rank;

public class DurationTickProducer implements TickProducer {

    private ArrayList<Tick> ticks;

    private final TimeUnit valueTimeUnit;

    private double scale;

    private double fromValue;

    private double toValue;

    private static final List<TimeUnit> unitsOrdered = new ArrayList<>(Arrays.asList(TimeUnit.values()));

    public DurationTickProducer() {
        valueTimeUnit = TimeUnit.MINUTES;
    }

    @Override
    public List<Tick> updateTicks(double from, double to, int plotSize) {
        fromValue = from;
        toValue = to;
        long duration = (long) (to - from);

        System.out.println();
        System.out.println();

        System.out.println("plotSize:" + plotSize);
        System.out.println("values:[" + from + ", " + to + "]");
        System.out.println("duration:" + duration);

        TimeUnit majorStepUnit;
        TimeUnit minorStepUnit;
        TimeUnit microStepUnit;
        int majorStep;
        int minorStep;
        int microStep;

        if (valueTimeUnit.toDays(duration) > 0) {
            majorStepUnit = TimeUnit.DAYS;
        } else if (valueTimeUnit.toHours(duration) > 20) {
            majorStepUnit = TimeUnit.DAYS;
        } else {
            majorStepUnit = TimeUnit.HOURS;
        }

        scale = 1.0 * plotSize / duration;
        System.out.println("scale:" + scale);

        // TODO Round from and to

        // One Tick per 50 pix
        int approxNumOfMajorTicks = plotSize / 50 + 1;
        System.out.println("approxNumOfMajorTicks:" + approxNumOfMajorTicks);
        double approxMajorStep = (duration) / approxNumOfMajorTicks;
        if (approxMajorStep == 0.0) {
            approxMajorStep = 1.0;
        }
        System.out.println("approxMajorStep:" + approxMajorStep);

        long majorStepValue = valueTimeUnit.convert(1, majorStepUnit);
        System.out.println("majorStepValue:" + majorStepValue);

        ticks = new ArrayList<>();

        for (int i = 0; i < approxNumOfMajorTicks; i++) {
            long value = (long) fromValue + i * majorStepValue;

            Tick tick = new Tick(value, Rank.MAJOR, value * scale);
            tick.scale(1);
            ticks.add(tick);

            System.out.println("tick  " + tick);
        }

        return null;
    }

    @Override
    public List<Tick> getTicks() {
        return ticks;
    }

    @Override
    public double getValuePosition(double value) {
        return (value - fromValue) * scale;
    }

}
