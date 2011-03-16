package com.pyx4j.widgets.client.datepicker;

import java.util.Date;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.datepicker.client.CalendarModel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.MonthSelector;

public class MonthAndYearSelector extends MonthSelector {
    private static String BASE_NAME = "datePicker";

    private PushButton backwards;

    private PushButton forwards;

    private PushButton backwardsYear;

    private PushButton forwardsYear;

    private ListBox years;

    private ListBox months;

    private Grid grid;

    private final int previousYearColumn = 0;

    private final int previousMonthColumn = 1;

    private final int yearsColumn = 2;

    private final int monthsColumn = 3;

    private final int nextMonthColumn = 4;

    private final int nextYearColumn = 5;

    private CalendarModel model;

    private DatePickerWithYearSelector picker;

    private final Date minDate;

    private final Date maxDate;

    private final int index;

    private DatePickerExtended parent;

    private final String[] monthName = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November",
            "December" };

    public MonthAndYearSelector(Date minDate, Date maxDate, int index) {
        this.minDate = minDate;
        this.maxDate = maxDate;
        this.index = index;
    }

    public void setModel(CalendarModel model) {
        this.model = model;
        picker.refreshComponents();
    }

    public void setPicker(DatePickerWithYearSelector picker) {
        this.picker = picker;
    }

    public void setParent(DatePickerExtended parent) {
        this.parent = parent;
    }

    @Override
    public DatePickerExtended getParent() {
        return this.parent;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void refresh() {
        if (this.model != null) {
            Date current = this.model.getCurrentMonth();
            if (this.index == 0) {
                if (current != null && months != null && years != null) {
                    months.setSelectedIndex(current.getMonth());
                    years.setSelectedIndex(current.getYear() - minDate.getYear());
                }
            } else {
                int monthIndex = this.model.getCurrentMonth().getMonth();
                String month = monthName[monthIndex];
                HTML monthWidget = new HTML(month);

                monthWidget.setStyleName("headerCenter");
                grid.setWidget(0, 0, monthWidget);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void setup() {
        // Set up backwards.
        if (this.index == 0) {
            backwards = new PushButton();
            backwards.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    updateDate(-1);
                    getParent().updateComponents();
                }
            });
            backwards.getUpFace().setHTML("&lsaquo;");
            backwards.setStyleName(BASE_NAME + "PreviousButton");

            forwards = new PushButton();
            forwards.getUpFace().setHTML("&rsaquo;");
            forwards.setStyleName(BASE_NAME + "NextButton");
            forwards.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    updateDate(+1);
                    getParent().updateComponents();
                }
            });
            // Set up backwards year
            backwardsYear = new PushButton();
            backwardsYear.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    updateDate(-12);
                    getParent().updateComponents();
                }
            });
            backwardsYear.getUpFace().setHTML("&laquo;");
            backwardsYear.setStyleName(BASE_NAME + "PreviousButton");

            forwardsYear = new PushButton();
            forwardsYear.getUpFace().setHTML("&raquo;");
            forwardsYear.setStyleName(BASE_NAME + "NextButton");
            forwardsYear.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    updateDate(+12);
                    getParent().updateComponents();
                }
            });

            years = new ListBox();
            for (int i = minDate.getYear(); i <= maxDate.getYear(); i++) {
                years.addItem(String.valueOf(i + 1900));
            }
            years.addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
                    int selected = years.getSelectedIndex();
                    int year = Integer.parseInt(years.getItemText(selected));
                    Date currentDate = model.getCurrentMonth();
                    currentDate.setYear(year - 1900);
                    setValidDate(currentDate);
                    getParent().updateComponents();
                }
            });

            months = new ListBox();

            for (String month : monthName) {
                months.addItem(month);
            }
            months.addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
                    int selected = months.getSelectedIndex();
                    Date currentDate = model.getCurrentMonth();
                    currentDate.setMonth(selected);
                    setValidDate(currentDate);
                    getParent().updateComponents();
                }
            });
            // Set up grid.

            grid = new Grid(1, 6);
            grid.setWidget(0, previousYearColumn, backwardsYear);
            grid.setWidget(0, previousMonthColumn, backwards);
            grid.setWidget(0, yearsColumn, years);
            grid.setWidget(0, monthsColumn, months);
            grid.setWidget(0, nextMonthColumn, forwards);
            grid.setWidget(0, nextYearColumn, forwardsYear);
            CellFormatter formatter = grid.getCellFormatter();
            formatter.setStyleName(0, yearsColumn, BASE_NAME + "Month");
            formatter.setWidth(0, previousYearColumn, "1");
            formatter.setWidth(0, previousMonthColumn, "1");
            formatter.setWidth(0, yearsColumn, "100%");
            formatter.setWidth(0, nextMonthColumn, "1");
            formatter.setWidth(0, nextYearColumn, "1");
        } else {
            grid = new Grid(1, 1);
        }
        grid.setStyleName(BASE_NAME + "MonthSelector");
        initWidget(grid);
    }

    public void setValidDateWithShift(int shift) {
        Date current = this.model.getCurrentMonth();
        CalendarUtil.addMonthsToDate(current, shift);
        setValidDate(current);
    }

    public void setValidDate(Date checkDate) {
        int minDateMultiplier = minDate.getYear() * 12 + minDate.getMonth();
        int maxDateMultiplier = maxDate.getYear() * 12 + maxDate.getMonth();
        int checkDateMultiplier = checkDate.getYear() * 12 + checkDate.getMonth();

        if (checkDate.compareTo(minDate) >= 0 && checkDate.compareTo(maxDate) <= 0) {
            model.setCurrentMonth(checkDate);
        } else if (checkDateMultiplier > maxDateMultiplier) {
            model.setCurrentMonth(maxDate);
        } else if (checkDateMultiplier < minDateMultiplier) {
            model.setCurrentMonth(minDate);
        }
    }

    @Override
    public void addMonths(int numMonths) {
        Date current = model.getCurrentMonth();
        CalendarUtil.addMonthsToDate(current, numMonths);
        model.setCurrentMonth(current);
        picker.refreshComponents();
    }

    private void updateDate(int months) {
        setValidDateWithShift(months);
        getParent().updateComponents();
    }
}
