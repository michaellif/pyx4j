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

import java.util.List;

import com.pyx4j.svg.basic.TickProducer;
import com.pyx4j.svg.common.Tick;

public class DurationAxisProducer implements AxisProducer {

    protected TickProducer tickProducer;

    protected LabelFormatter labelFormatter;

    private final TimeUnit valueTimeUnit;

    private double fromValue;

    private double toValue;

    private boolean valueRangeFixed = false;

    public DurationAxisProducer() {
        this(TimeUnit.MINUTES);
    }

    public DurationAxisProducer(TimeUnit valueTimeUnit) {
        this.valueTimeUnit = valueTimeUnit;
        labelFormatter = new DurationLabelFormatter();
        tickProducer = new DurationTickProducer(valueTimeUnit);
    }

    @Override
    public void setTickProducer(TickProducer tickProducer) {
        this.tickProducer = tickProducer;
    }

    @Override
    public void setLabelFormatter(LabelFormatter labelFormatter) {
        this.labelFormatter = labelFormatter;
    }

    public void setFixedValueRange(double from, double to) {
        setValueRange(from, to);
        valueRangeFixed = true;
    }

    @Override
    public void setValueRange(double from, double to) {
        if (valueRangeFixed) {
            return;
        }
        long duration = (long) (to - from);

        TimeUnit stepUnit;
        if (valueTimeUnit.toDays(duration) > 4) {
            stepUnit = TimeUnit.DAYS;
        } else {
            stepUnit = TimeUnit.HOURS;
        }

        long majorStepValue = valueTimeUnit.convert(1, stepUnit);

        toValue = roundUp(to, majorStepValue);
        fromValue = roundDown(from, majorStepValue);
    }

    private double roundUp(double value, long majorStepValue) {
        return Math.ceil(value / majorStepValue) * majorStepValue;
    }

    private double roundDown(double value, long majorStepValue) {
        return Math.floor(value / majorStepValue) * majorStepValue;
    }

    @Override
    public void setPlotSize(int plotSize) {
        tickProducer.updateTicks(fromValue, toValue, plotSize);
    }

    @Override
    public List<Tick> getTicks() {
        return tickProducer.getTicks();
    }

    @Override
    public double getValuePosition(double value) {
        return tickProducer.getValuePosition(value);
    }

    @Override
    public int getMaxLabelLength() {
        return String.valueOf(toValue).length();
    }

    @Override
    public String formatLabel(double value) {
        return labelFormatter.format(value);
    }

}
