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
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import java.util.Date;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.datepicker.client.CalendarUtil;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.widgets.client.datepicker.DatePickerComposite;

public class DatePickerDropDownPanel extends DropDownPanel implements Focusable {

    private final DatePickerComposite picker;

    private final FocusPanel focusPanel;

    private final DatePickerTextBox textBox;

    public DatePickerDropDownPanel(final DatePickerTextBox textBox) {
        this.textBox = textBox;
        this.getElement().getStyle().setProperty("zIndex", "100");

        focusPanel = new FocusPanel();
        focusPanel.getElement().getStyle().setProperty("outline", "0");

        picker = new DatePickerComposite();

        focusPanel.setWidget(picker);
        setWidget(focusPanel);
        picker.addDateChosenEventHandler(new DatePickerComposite.DateChosenEventHandler() {

            @Override
            public void onDateChosen(DatePickerComposite.DateChosenEvent event) {
                LogicalDate value = event.getChosenDate();
                if (value != null) { // Clone without time component!
                    value = new LogicalDate(value.getYear(), value.getMonth(), value.getDate());
                }

                textBox.setValue(value);
                hideDatePicker();
            }
        });

        setAnimationEnabled(false);
        initializeKeyListener();
    }

    public void showDatePicker() {
        LogicalDate selectedDate = textBox.getValue();
        if (selectedDate == null) {
            selectedDate = new LogicalDate();
        }
        picker.setDate(selectedDate);
        showRelativeTo(textBox);
        focusPanel.setFocus(true);

    }

    public void hideDatePicker() {
        hide();
    }

    @Override
    public int getTabIndex() {
        return focusPanel.getTabIndex();
    }

    @Override
    public void setAccessKey(char key) {
        focusPanel.setAccessKey(key);
    }

    @Override
    public void setFocus(boolean focused) {
        focusPanel.setFocus(focused);
    }

    @Override
    public void setTabIndex(int index) {
        focusPanel.setTabIndex(index);
    }

    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return focusPanel.addBlurHandler(handler);
    }

    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return focusPanel.addFocusHandler(handler);
    }

    private void initializeKeyListener() {
        focusPanel.addKeyDownHandler(new KeyDownHandler() {

            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_UP) {
                    Date current = getCurrent();
                    CalendarUtil.addMonthsToDate(current, 12);
                    picker.setDate(current);
                } else if (event.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
                    Date current = getCurrent();
                    CalendarUtil.addMonthsToDate(current, -12);
                    picker.setDate(current);
                } else if (event.getNativeKeyCode() == KeyCodes.KEY_LEFT) {
                    Date current = getCurrent();
                    CalendarUtil.addMonthsToDate(current, -1);
                    picker.setDate(current);
                } else if (event.getNativeKeyCode() == KeyCodes.KEY_RIGHT) {
                    Date current = getCurrent();
                    CalendarUtil.addMonthsToDate(current, 1);
                    picker.setDate(current);
                }
                event.preventDefault();
            }

            private Date getCurrent() {
                Date newDate = new Date(picker.getDate().getTime());
                return newDate;
            }

        });
    }

}
