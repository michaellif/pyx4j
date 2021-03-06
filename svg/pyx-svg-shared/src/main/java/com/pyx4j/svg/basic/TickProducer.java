package com.pyx4j.svg.basic;

import java.util.List;

import com.pyx4j.svg.common.Tick;

public interface TickProducer {

    List<Tick> updateTicks(double from, double to, int plotSize);

    List<Tick> getTicks();

    double getValuePosition(double value);

    double getValue(int position);
}
