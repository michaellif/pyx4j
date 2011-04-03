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
package com.pyx4j.forms.client.ui;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Focusable;

import com.pyx4j.widgets.client.DropDownPanel;
import com.pyx4j.widgets.client.datepicker.DatePickerComposite;

public class DatePickerDropDownPanel extends DropDownPanel implements Focusable {

    private static final Logger log = LoggerFactory.getLogger(DatePickerDropDownPanel.class);

    private final DatePickerComposite picker;

    private final FocusPanel focusPanel;

    private final NativeDatePicker nativeDatePicker;

    public DatePickerDropDownPanel(final NativeDatePicker nativeDatePicker) {
        this.nativeDatePicker = nativeDatePicker;

        focusPanel = new FocusPanel();
        focusPanel.getElement().getStyle().setProperty("outline", "0");

        picker = new DatePickerComposite();

        focusPanel.setWidget(picker);
        setWidget(focusPanel);
        picker.addDateChosenEventHandler(new DatePickerComposite.DateChosenEventHandler() {

            @Override
            public void onDateChosen(DatePickerComposite.DateChosenEvent event) {
                Date value = event.getChosenDate();
                if (value != null) { // Clone without time component!
                    value = new Date(value.getYear(), value.getMonth(), value.getDate());
                }

                nativeDatePicker.setNativeValue(value);
                nativeDatePicker.getCComponent().onEditingStop();
                hide();
                nativeDatePicker.setFocus(true);
            }
        });

        setAnimationEnabled(false);

    }

    public void showDatePicker() {
        Date selectedDate = null;
        String value = nativeDatePicker.getNativeText().trim();
        if (!value.equals("")) {
            try {
                selectedDate = nativeDatePicker.getCComponent().getValue();
            } catch (IllegalArgumentException e) {
                log.info("Cannot parse as date: " + value);
            }
        }
        if (selectedDate == null) {
            selectedDate = new Date();
        }
        picker.setDate(selectedDate);
        showRelativeTo(nativeDatePicker);
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

}
