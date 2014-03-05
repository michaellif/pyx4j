package com.pyx4j.svg.basic;

import java.util.List;

import com.pyx4j.svg.common.Tick;

public interface TickProducer {

    List<Tick> updateTicks(double from, double to, int width);

    List<Tick> getTicks();

    double getValuePosition(double value);

}
