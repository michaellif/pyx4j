/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-03
 * @author leont
 * @version $Id$
 */

package com.pyx4j.widgets.client.datepicker;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
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
    
    private String[] daysOfWeek = new String[7];

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
    
    private void setDaysOfWeek(){
        Date date = new Date();
        for (int i = 1; i <= 7; i++) {
          date.setDate(i);
          int dayOfWeek = date.getDay();
          daysOfWeek[dayOfWeek] = DateTimeFormat.getFormat("E").format(date).substring(0,2);
        }
    }

    @Override
    protected void setup() {
    	setDaysOfWeek();
        for (int i = 0; i < CalendarModel.DAYS_IN_WEEK; i++) {
            int shift = CalendarUtil.getStartingDayOfWeek();
            int dayIdx = i + shift < CalendarModel.DAYS_IN_WEEK ? i + shift : i + shift - CalendarModel.DAYS_IN_WEEK;
            grid.setText(0, i, daysOfWeek[dayIdx]);
        }
        //grid.getRowFormatter().setStyleName(0, "datePickerGridDaysRow");
        grid.getRowFormatter().setStyleName(0, Selector.getStyleName(DatePickerExtended.BASE_NAME, DatePickerExtended.StyleSuffix.GridDaysRow));
        initWidget(grid);
    }

    public void setSelectedDate(Date selectedDate) {
        this.grid.setSelectedDate(selectedDate);
    }
}