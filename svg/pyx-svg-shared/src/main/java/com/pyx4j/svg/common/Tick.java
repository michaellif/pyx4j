package com.pyx4j.svg.common;

import java.text.DecimalFormat;

public class Tick {

    public enum Rank {
        MAJOR, MINOR, MICRO
    }

    private static final DecimalFormat format = new DecimalFormat("#.###");

    private final double value;

    // Normalized position - between 0 and 1
    private final double position;

    private int scaledPosition;

    private final Rank rank;

    public Tick(double value, Rank rank, double position) {
        this.value = value;
        this.rank = rank;
        this.position = position;
    }

    public double getValue() {
        return value;
    }

    public String getLabel() {
         return format.format(value);
    }

    public Rank getRank() {
        return rank;
    }

    public double getPosition() {
        return position;
    }

    public void scale(int plotSize) {
        scaledPosition = (int) Math.round(position * plotSize);
    }

    public int getScaledPosition() {
        return scaledPosition;
    }

    @Override
    public String toString() {
        switch (rank) {
        case MAJOR:
            return "==== " + value + " " + scaledPosition;
        case MINOR:
            return "==   " + value + " " + scaledPosition;
        case MICRO:
            return "-    " + value + " " + scaledPosition;
        default:
            return "";
        }
    }
}
