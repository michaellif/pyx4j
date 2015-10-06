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
 * @version $Id$
 */
package com.pyx4j.svg.chart;

import java.util.List;

import com.pyx4j.svg.basic.TickProducer;
import com.pyx4j.svg.common.Tick;

public class DecimalAxisProducer implements AxisProducer {

    protected TickProducer tickProducer;

    protected LabelFormatter labelFormatter;

    protected MetricPrefix postfix;

    private double fromValue;

    private double toValue;

    public DecimalAxisProducer() {
        labelFormatter = new DefaultLabelFormatter();
        tickProducer = new BasicTickProducer();
    }

    @Override
    public void setTickProducer(TickProducer tickProducer) {
        this.tickProducer = tickProducer;
    }

    @Override
    public void setLabelFormatter(LabelFormatter labelFormatter) {
        this.labelFormatter = labelFormatter;
    }

    @Override
    public void setValueRange(double from, double to) {
        postfix = MetricPrefix.getMetricPrefix(to - from);
        toValue = postfix.roundUp(to);
        fromValue = postfix.roundDown(from);
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
    public double getValue(int position) {
        return tickProducer.getValue(position);
    }

    @Override
    public int getMaxLabelLength() {
        return String.valueOf(toValue / postfix.getFactor()).length();
    }

    @Override
    public String formatLabel(double value) {
        return labelFormatter.format(value / postfix.getFactor()) + postfix.getName();
    }

}
