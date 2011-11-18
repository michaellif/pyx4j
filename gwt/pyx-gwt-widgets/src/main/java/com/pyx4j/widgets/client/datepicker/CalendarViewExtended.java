/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
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
import com.google.gwt.i18n.client.DateTimeFormatInfo;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.datepicker.client.CalendarModel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.CalendarView;
import com.google.gwt.user.datepicker.client.DatePicker;

public class CalendarViewExtended extends CalendarView {

    private Date firstDisplayed;

    private final Date lastDisplayed = new Date();

    private final DateGrid grid;

    private DatePicker picker;

    private final String[] daysOfWeek = new String[7];

    public CalendarViewExtended(Date minDate, Date maxDate, ArrayList<Date> disabledDates, boolean inMultiple) {
        grid = new DateGrid(minDate, maxDate, disabledDates, inMultiple);
        grid.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                DateGrid grid = (DateGrid) event.getSource();
                picker.setValue(grid.getSelectedValue(), true);
            }
        });
    }

    @Override
    protected void onEnsureDebugId(String id) {
        grid.ensureDebugId(id);
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

    private void setDaysOfWeek() {
        Date date = new Date();

        for (int i = 1; i <= 7; i++) {
            date.setDate(i);
            int dayOfWeek = date.getDay();
            daysOfWeek[dayOfWeek] = DateTimeFormat.getFormat("E").format(date).substring(0, 2);
        }
    }

    @Override
    protected void setup() {
        setDaysOfWeek();

        DateTimeFormatInfo dateTimeFormatInfo = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo();
        int firstDayOfWeekend = dateTimeFormatInfo.weekendStart();
        int lastDayOfWeekend = dateTimeFormatInfo.weekendEnd();

        for (int i = 0; i < CalendarModel.DAYS_IN_WEEK; i++) {
            int shift = CalendarUtil.getStartingDayOfWeek();
            int dayIdx = i + shift < CalendarModel.DAYS_IN_WEEK ? i + shift : i + shift - CalendarModel.DAYS_IN_WEEK;

            grid.setText(0, i, daysOfWeek[dayIdx]);

            if (dayIdx == firstDayOfWeekend || dayIdx == lastDayOfWeekend) {
                grid.getCellFormatter().addStyleName(0, i, DefaultDatePickerTheme.StyleName.DatePickerWeekendDay.name());
            }
        }
        //grid.getRowFormatter().setStyleName(0, "datePickerGridDaysRow");
        grid.getRowFormatter().setStyleName(0, DefaultDatePickerTheme.StyleName.DatePickerGridDaysRow.name());
        initWidget(grid);
    }

    public void setSelectedDate(Date selectedDate) {
        this.grid.setSelectedDate(selectedDate);
    }
}