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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.shared.DateTimeFormatInfo;
import com.google.gwt.user.datepicker.client.CalendarModel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.CalendarView;
import com.google.gwt.user.datepicker.client.DatePicker;

import com.pyx4j.commons.LogicalDate;

public class CalendarViewExtended extends CalendarView {

    private LogicalDate firstDisplayed;

    private final LogicalDate lastDisplayed = new LogicalDate();

    private final DateGrid grid;

    private DatePicker picker;

    public CalendarViewExtended(LogicalDate minDate, LogicalDate maxDate, ArrayList<LogicalDate> disabledDates, boolean inMultiple) {
        grid = new DateGrid(minDate, maxDate, disabledDates, inMultiple);
        grid.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                DateGrid grid = (DateGrid) event.getSource();

                Date selectedDate = grid.getSelectedValue();
                if (selectedDate != null) {
                    if (picker.getValue() != null && picker.getValue().equals(selectedDate)) {
                        picker.setValue(selectedDate, true);

                        ValueChangeEvent.fire(picker, selectedDate); // Fix to close dropdown panel. DatePicker will not fire event for the same date
                    } else {
                        picker.setValue(selectedDate, true);
                    }
                }
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
    public LogicalDate getFirstDate() {
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
        firstDisplayed = new LogicalDate(getModel().getCurrentFirstDayOfFirstWeek());
        grid.redraw(firstDisplayed);
    }

    @Override
    protected void setup() {
        DateTimeFormatInfo dateTimeFormatInfo = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo();
        int firstDayOfWeekend = dateTimeFormatInfo.weekendStart();
        int lastDayOfWeekend = dateTimeFormatInfo.weekendEnd();

        for (int i = 0; i < CalendarModel.DAYS_IN_WEEK; i++) {
            int shift = CalendarUtil.getStartingDayOfWeek();
            int dayIdx = i + shift < CalendarModel.DAYS_IN_WEEK ? i + shift : i + shift - CalendarModel.DAYS_IN_WEEK;

            grid.setText(0, i, getModel().formatDayOfWeek(dayIdx));

            if (dayIdx == firstDayOfWeekend || dayIdx == lastDayOfWeekend) {
                grid.getCellFormatter().addStyleName(0, i, DatePickerTheme.StyleName.DatePickerWeekendDayLabel.name());
            }
        }

        grid.getRowFormatter().setStyleName(0, DatePickerTheme.StyleName.DatePickerGridDaysRow.name());
        initWidget(grid);
    }

    public void setSelectedDate(LogicalDate selectedDate) {
        this.grid.setSelectedDate(selectedDate);
    }
}