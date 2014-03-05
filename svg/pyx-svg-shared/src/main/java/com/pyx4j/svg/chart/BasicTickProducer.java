package com.pyx4j.svg.chart;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.svg.basic.TickProducer;
import com.pyx4j.svg.common.Tick;
import com.pyx4j.svg.common.Tick.Rank;

public class BasicTickProducer implements TickProducer {

    private ArrayList<Tick> ticks;

    private double fromValue;

    private double toValue;

    private int plotSize;

    private int majorStep;

    private int minorStep;

    private int microStep;

    private double scaleFactor;

    private Double fixedMajorStep;

    public BasicTickProducer() {

    }

    public BasicTickProducer(double fixedMajorStep) {
        this.fixedMajorStep = fixedMajorStep;
    }

    // This is Stupido!  the value returned can be only on  Ticks. 
    @Override
    public double getValuePosition(double value) {
        double position = 0;
        List<Tick> ticks = getTicks();
        if (ticks == null) {
            return position;
        }
        for (Tick tick : ticks) {
            position = tick.getScaledPosition();
            if (value <= tick.getValue()) {
                break;
            }
        }
        return position;
    }

    @Override
    public List<Tick> updateTicks(double from, double to, int plotSize) {
        boolean recalcTicks = false;
        if (fromValue != from) {
            fromValue = from;
            recalcTicks = true;
        }
        if (toValue != to) {
            toValue = to;
            recalcTicks = true;
        }
        if (this.plotSize != plotSize) {
            this.plotSize = plotSize;
            recalcTicks = true;
        }

        int prevMajorStep = majorStep;

        if (fixedMajorStep == null) {
            //Should be at least one
            int approxNumOfMajorTicks = plotSize / 50 + 1;
            double approxMajorStep = (toValue - fromValue) / approxNumOfMajorTicks;
            if (approxMajorStep == 0.0)
                approxMajorStep = 1.0;

            int scaleFactorPow = (int) Math.floor(Math.log(approxMajorStep) / Math.log(10.0)) - 1;
            scaleFactor = Math.pow(10.0, scaleFactorPow);
            approxMajorStep = approxMajorStep / scaleFactor;

            if (approxMajorStep > 75.0) {
                majorStep = 100;
                minorStep = 50;
                microStep = 10;
            } else if (approxMajorStep > 35.0) {
                majorStep = 50;
                minorStep = 25;
                microStep = 5;
            } else if (approxMajorStep > 22.5) {
                majorStep = 25;
                minorStep = 5;
                microStep = 0;
            } else if (approxMajorStep > 15.0) {
                majorStep = 20;
                minorStep = 10;
                microStep = 2;
            } else {
                majorStep = 10;
                minorStep = 5;
                microStep = 1;
            }

        } else {
            int scaleFactorPow = (int) Math.floor(Math.log(fixedMajorStep) / Math.log(10.0)) - 1;
            scaleFactor = Math.pow(10.0, scaleFactorPow);

            majorStep = (int) (fixedMajorStep / scaleFactor);
            if (majorStep > 10 && majorStep % 10 == 0) {
                minorStep = 10;
            } else {
                minorStep = 0;
            }
            microStep = 0;

        }

        if (prevMajorStep != majorStep) {
            recalcTicks = true;
        }

        if (ticks == null || recalcTicks) {
            calcTicks();
        }
        scaleTicks(plotSize);

        return ticks;
    }

    @Override
    public synchronized List<Tick> getTicks() {
        return ticks;
    }

    protected synchronized void calcTicks() {
        int step = microStep != 0 ? microStep : minorStep != 0 ? minorStep : majorStep;

        double firstValue;

        if ((int) Math.round((fromValue / scaleFactor) % step) == 0) {
            firstValue = ((int) Math.round(fromValue / (scaleFactor * step))) * step;
        } else {
            firstValue = ((int) Math.round(fromValue / (scaleFactor * step)) + 1) * step;
        }

        double lastValue = ((int) Math.round(toValue / (scaleFactor * step))) * step;

        int tickCount = (int) (lastValue - firstValue) / step + 1;

        ticks = new ArrayList<Tick>();
        for (int i = 0; i < tickCount; i++) {
            double value = firstValue + i * step;
            Rank rank;
            if (value % majorStep == 0) {
                rank = Rank.MAJOR;
            } else if (value % minorStep == 0) {
                rank = Rank.MINOR;
            } else {
                rank = Rank.MICRO;
            }
            double position = fromValue == toValue ? 0 : (value * scaleFactor - fromValue) / (toValue - fromValue);

            ticks.add(new Tick(value * scaleFactor, rank, position));
        }
    }

    void scaleTicks(int plotSize) {
        for (Tick tick : ticks) {
            tick.scale(plotSize);
        }
    }
}
