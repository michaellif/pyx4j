package com.pyx4j.widgets.client.datepicker;

import java.util.Date;

import com.google.gwt.user.datepicker.client.CalendarModel;
import com.google.gwt.user.datepicker.client.CalendarView;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.user.datepicker.client.DefaultCalendarView;

public class DatePickerWithYearSelector extends DatePicker {

    private MonthAndYearSelector monthSelector;

    public DatePickerWithYearSelector(Date current, Date minDate, Date maxDate, int index) {
        super(new MonthAndYearSelector(minDate, maxDate, index), new DefaultCalendarView(), new CalendarModel());
        monthSelector = (MonthAndYearSelector) this.getMonthSelector();
        monthSelector.setPicker(this);
        monthSelector.setModel(this.getModel());
        monthSelector = (MonthAndYearSelector) this.getMonthSelector();
        this.setCurrentMonth(current);
    }

    public void refreshComponents() {
        super.refreshAll();
    }

    public void setParent(DatePickerExtended parent) {
        this.monthSelector.setParent(parent);
    }

    public CalendarView getMyView() {
        return this.getView();
    }

    public CalendarModel getMyModel() {
        return this.getModel();
    }
}
