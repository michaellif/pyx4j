package com.pyx4j.widgets.client.datepicker;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.datepicker.client.CalendarModel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.CalendarView;
import com.google.gwt.user.datepicker.client.DatePicker;

import com.pyx4j.widgets.client.style.Selector;

public class CalendarViewExtended extends CalendarView {

    private Date firstDisplayed;

    private final Date lastDisplayed = new Date();

    private final DateGrid grid;

    private DatePicker picker;

    public CalendarViewExtended(Date minDate, Date maxDate, ArrayList<Date> disabledDates) {
        grid = new DateGrid(minDate, maxDate, disabledDates);
        grid.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                DateGrid grid = (DateGrid) event.getSource();
                picker.setValue(grid.getSelectedValue(), true);
            }

        });
    }

    public void clearSelection() {
        grid.clearSelection();
    }

    public void setPicker(DatePickerExtended picker) {
        this.picker = picker;
    }

    @Override
    public void addStyleToDate(String styleName, Date date) {
    }

    @Override
    public Date getFirstDate() {
        return firstDisplayed;
    }

    @Override
    public Date getLastDate() {
        return lastDisplayed;
    }

    @Override
    public boolean isDateEnabled(Date date) {
        //not implemented
        return false;
    }

    @Override
    public void removeStyleFromDate(String styleName, Date date) {
    }

    @Override
    public void setEnabledOnDate(boolean enabled, Date date) {
    }

    @Override
    protected void refresh() {
        firstDisplayed = getModel().getCurrentFirstDayOfFirstWeek();
        grid.redraw(firstDisplayed);
    }

    @Override
    protected void setup() {
        for (int i = 0; i < CalendarModel.DAYS_IN_WEEK; i++) {
            int shift = CalendarUtil.getStartingDayOfWeek();
            int dayIdx = i + shift < CalendarModel.DAYS_IN_WEEK ? i + shift : i + shift - CalendarModel.DAYS_IN_WEEK;
            grid.setText(0, i, getModel().formatDayOfWeek(dayIdx));
        }
        //grid.getRowFormatter().setStyleName(0, "datePickerGridDaysRow");
        grid.getRowFormatter().setStyleName(0, Selector.getStyleName(DatePickerExtended.BASE_NAME, DatePickerExtended.StyleSuffix.GridDaysRow));
        initWidget(grid);
    }

    public void setSelectedDate(Date selectedDate) {
        this.grid.setSelectedDate(selectedDate);
    }
}