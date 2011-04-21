package com.pyx4j.dashboard.client;

/**
 * Dashboard layout data type. Represents desirable layout for dashboard
 */
public class Layout {
    private int columns = 1; // at least one column exists by default...

    // geometry:
    private double horizontalSpacing = 0; // horizontal (%-values!) and

    private int verticalSpacing = 0; // vertical (pixels) cell spacing value...

    // column relative widths (in per-cents):
    private byte[] columnWidths = new byte[0]; // could be filled with widths...

    public Layout() {
    }

    public Layout(int columns) throws IllegalArgumentException {
        if (columns > 0) {
            this.columns = columns;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public Layout(int columns, double spacingH_PCT, int spacingV_PX) throws IllegalArgumentException {
        if (columns > 0) {
            this.columns = columns;
        } else {
            throw new IllegalArgumentException();
        }

        if (!setHorizontalSpacing(spacingH_PCT)) {
            throw new IllegalArgumentException();
        }

        this.verticalSpacing = spacingV_PX;
    }

    public int getColumns() {
        return columns;
    }

    public double getHorizontalSpacing() {
        return horizontalSpacing;
    }

    /**
     * Horizontal spacing set by %, so their sum (doubled value multiplied by column
     * number) may not exceed 100%, at least (in reality we want to leave space for
     * the columns itself!). Then, the spacing is formed by means of column padding,
     * so its doubled value may not exceed the size of the smallest column also...
     */
    public boolean setHorizontalSpacing(double spacingH_PCT) {
        if (getColumns() * spacingH_PCT * 2 >= 100.0) {
            return false; // percentage looks strange!?.
        }

        double pcMin = 100.0 / getColumns();
        for (int i = 0; i < columnWidths.length; ++i) {
            pcMin = Math.min(pcMin, columnWidths[i]);
        }

        if (pcMin <= spacingH_PCT * 2) {
            return false; // ok, smallest column should be wider than spacing...
        }

        horizontalSpacing = spacingH_PCT;
        return true;
    }

    public int getVerticalSpacing() {
        return verticalSpacing;
    }

    public void setVerticalSpacing(int spacingV_PX) {
        verticalSpacing = spacingV_PX;
    }

    public boolean isColumnWidths() {
        return (columnWidths.length != 0);
    }

    /**
     * The column widths set by %, so their sum shouldn't exceed 100%. But there is
     * horizontal spacing also, and spacing formed by column padding, so the smallest
     * column width should be greater that doubled spacing value...
     */
    public boolean setColumnWidths(byte[] columnWidths) throws IllegalArgumentException {
        if (columnWidths == null) {
            columnWidths = new byte[0];
        }

        if (columnWidths.length > 0) { // note: zero length array is 'reset to default' case!..
            if (columnWidths.length < getColumns()) {
                throw new IllegalArgumentException();
            }

            byte pcSum = 0;
            double pcMin = 100.0 / getColumns();
            for (int i = 0; i < columnWidths.length; ++i) {
                pcSum += columnWidths[i];
                pcMin = Math.min(pcMin, columnWidths[i]);
            }

            if (pcSum > 100) {
                return false; // mmm, the widths percentage looks strange!?.
            }

            if (pcMin <= getHorizontalSpacing() * 2) {
                return false; // ok, smallest column should be wider than spacing...
            }
        }

        this.columnWidths = columnWidths;
        return true;
    }

    public float getCoumnWidth(int column) throws ArrayIndexOutOfBoundsException {
        return columnWidths[column];
    }
}