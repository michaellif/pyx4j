package com.pyx4j.widgets.client.datepicker;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.user.datepicker.client.CalendarModel;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public abstract class DatePickerExtended extends DatePicker {
	
	public static String BASE_NAME = "datePicker";
	
    public static enum StyleSuffix implements IStyleSuffix {
    	Grid, MonthSelector, GridDaysRow, Navigation, MonthLabel, YearLabel, YearNavigation
    }

    public static enum StyleDependent implements IStyleDependent {
    	disabled, heighlighted, selected, right, multiple, first
    }

    protected MonthSelectorExtended monthSelector;

    protected CalendarViewExtended calendarView;

    protected Date minDate;

    protected Date maxDate;

    protected ArrayList<Date> disabledDates;

    public DatePickerExtended(MonthSelectorExtended selector, ArrayList<Date> disabledDates) {
        super(selector, new CalendarViewExtended(selector.getMinDate(), selector.getMaxDate(), disabledDates), new CalendarModel());
        this.minDate = selector.getMinDate();
        this.maxDate = selector.getMaxDate();
        this.disabledDates = disabledDates;
        monthSelector = (MonthSelectorExtended) this.getMonthSelector();
        monthSelector.setPicker(this);
        monthSelector.setModel(this.getModel());
        calendarView = (CalendarViewExtended) this.getView();
        calendarView.setPicker(this);
    }

    public void refreshComponents() {
        super.refreshAll();
    }

    public void setParent(DatePickerComposite parent) {
        monthSelector.setCompositeParent(parent);
    }

    public CalendarViewExtended getMyView() {
        return (CalendarViewExtended) this.getView();
    }

    public CalendarModel getMyModel() {
        return this.getModel();
    }

    public MonthSelectorExtended getMyMonthSelector() {
        return (MonthSelectorExtended) this.getMonthSelector();
    }

    public void clearSelection() {
        getMyView().clearSelection();
    }

}