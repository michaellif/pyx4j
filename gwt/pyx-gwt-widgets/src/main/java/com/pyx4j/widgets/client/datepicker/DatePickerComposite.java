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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.MonthSelector;

import com.pyx4j.widgets.client.style.Selector;

public class DatePickerComposite extends Composite implements HasHandlers {

    public static final Type<DateChosenEventHandler> TYPE = new Type<DateChosenEventHandler>();

    public class DateChosenEvent extends GwtEvent<DateChosenEventHandler> {
        private final Date date;

        public DateChosenEvent(Date date) {
            this.date = date;
        }

        public Date getChosenDate() {
            return this.date;
        }

        @Override
        public Type<DateChosenEventHandler> getAssociatedType() {
            return TYPE;
        }

        @Override
        protected void dispatch(DateChosenEventHandler handler) {
            handler.onDateChosen(this);
        }
    }

    public interface DateChosenEventHandler extends EventHandler {
        void onDateChosen(DateChosenEvent event);
    }

    public static enum DateGridDebugID {
        DateGrid_Index_
    }

    public enum DateCellDebugID {
        row_, _col_
    }

    HandlerManager handlerManager;

    HorizontalPanel panel = new HorizontalPanel();

    ArrayList<DatePickerExtended> pickers = new ArrayList<DatePickerExtended>();

    ArrayList<Date> disabledDates;

    Date minDate;

    Date maxDate;

    public Date selectedField;

    public DatePickerComposite() {
        this(1, new Date(), new Date(0, 1, 1), new Date(200, 1, 1), new ArrayList<Date>());
    }

    public DatePickerComposite(int numberOfMonths, Date starting, Date minDate, Date maxDate, ArrayList<Date> disabledDates) {
        DatePickerExtended picker;
        this.disabledDates = disabledDates;
        this.minDate = minDate;
        this.maxDate = maxDate;
        handlerManager = new HandlerManager(this);
        MonthSelector tempMonthSelector;

        for (int i = 0; i < numberOfMonths; i++) {
            if (numberOfMonths == 1) {
                picker = new DatePickerSingle(starting, minDate, maxDate, disabledDates);
            } else {
                picker = new DatePickerMultiple(starting, minDate, maxDate, disabledDates);
            }
            picker.setDebugId(i);
            pickers.add(picker);
            picker.addValueChangeHandler(new ValueChangeHandler<Date>() {
                @Override
                public void onValueChange(ValueChangeEvent<Date> event) {
                    setGridSelection(event);
                }
            });
            picker.setParent(this);
            panel.add(picker);
            CalendarUtil.addMonthsToDate(starting, 1);
        }
        if (numberOfMonths > 1) {
            pickers.get(0).addStyleName(Selector.getDependentName(DatePickerExtended.StyleDependent.first));
            tempMonthSelector = pickers.get(0).getMyMonthSelector();
            ((MonthSelectorMultiple) tempMonthSelector).setAsFirstCalendar();
            tempMonthSelector = pickers.get(pickers.size() - 1).getMyMonthSelector();
            ((MonthSelectorMultiple) tempMonthSelector).setAsLastCalendar();
        }
        initWidget(panel);
    }

    public void setGridSelection(ValueChangeEvent<Date> event) {
        DatePickerExtended clickedPicker = (DatePickerExtended) event.getSource();
        for (DatePickerExtended eachPicker : pickers) {
            if (!eachPicker.equals(clickedPicker)) {
                eachPicker.clearSelection();
            }
        }
        selectedField = event.getValue();
        DateChosenEvent dateChosen = new DateChosenEvent(selectedField);
        fireEvent(dateChosen);
    }

    public void updateComponents(DatePickerExtended picker) {
        Date startingDate = picker.getMyModel().getCurrentMonth();

        Date tempStartingDate = new Date(startingDate.getTime());
        int calendars = pickers.size();

        if (picker != pickers.get(0)) {
            CalendarUtil.addMonthsToDate(tempStartingDate, -(calendars - 1));
        }

        for (DatePickerExtended eachPicker : pickers) {
            eachPicker.setCurrentMonth(tempStartingDate);
            CalendarUtil.addMonthsToDate(tempStartingDate, 1);
        }

    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

    public HandlerRegistration addDateChosenEventHandler(DateChosenEventHandler handler) {
        return handlerManager.addHandler(TYPE, handler);
    }

    public void setDate(Date selectedDate) {
        Date tempDate = new Date(selectedDate.getTime());
        pickers.get(0).setSelectedDate(tempDate);
        for (DatePickerExtended picker : pickers) {
            tempDate = new Date(selectedDate.getTime());
            picker.setCurrentMonth(tempDate);
            CalendarUtil.addMonthsToDate(selectedDate, 1);
        }
    }

    public Date getDate() {
        return pickers.get(0).getCurrentMonth();
    }

}
