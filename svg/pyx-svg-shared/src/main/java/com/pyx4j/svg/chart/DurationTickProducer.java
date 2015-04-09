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
 */
package com.pyx4j.svg.chart;

import java.util.ArrayList;
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

    public DurationTickProducer() {
        this(TimeUnit.MINUTES);
    }

    public DurationTickProducer(TimeUnit valueTimeUnit) {
        this.valueTimeUnit = valueTimeUnit;
    }

    @Override
    public List<Tick> updateTicks(double from, double to, int plotSize) {
        fromValue = from;
        toValue = to;
        long duration = (long) (to - from);

        TimeUnit stepUnit;

        if (valueTimeUnit.toDays(duration) > 3) {
            stepUnit = TimeUnit.DAYS;
        } else {
            stepUnit = TimeUnit.HOURS;
        }

        scale = 1.0 * plotSize / duration;
        long stepValue = valueTimeUnit.convert(1, stepUnit);

        ticks = new ArrayList<>();

        Tick previousMajorTick = null;
        for (int i = 0;; i++) {
            long value = (long) fromValue + i * stepValue;
            if (value > toValue) {
                break;
            }
            double position = getValuePosition(value);
            Rank rank = Rank.MAJOR;
            if ((previousMajorTick != null) && (position - previousMajorTick.getScaledPosition() < 50)) {
                rank = Rank.MINOR;
            }

            Tick tick = new Tick(value, rank, getValuePosition(value));
            tick.scale(1);
            ticks.add(tick);

            if (rank == Rank.MAJOR) {
                previousMajorTick = tick;
            }
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
