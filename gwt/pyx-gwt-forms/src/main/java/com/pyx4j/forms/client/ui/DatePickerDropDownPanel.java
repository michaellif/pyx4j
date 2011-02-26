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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.datepicker.client.DatePicker;

import com.pyx4j.widgets.client.DropDownPanel;

public class DatePickerDropDownPanel extends DropDownPanel {

    private static final Logger log = LoggerFactory.getLogger(DatePickerDropDownPanel.class);

    private final DatePicker picker;

    private static DatePickerDropDownPanel instance;

    private NativeDatePicker currenttextBox;

    private DatePickerDropDownPanel() {
        picker = new DatePicker();
        //TODO DOM.setStyleAttribute(picker.getElement(), "border", "1px solid " + popupBorderColor);
        setWidget(picker);
        setAnimationEnabled(false);

        picker.addValueChangeHandler(new ValueChangeHandler<Date>() {

            @Override
            public void onValueChange(ValueChangeEvent<Date> event) {
                NativeDatePicker receiver = currenttextBox;
                if (receiver != null) {
                    Date value = event.getValue();
                    // Clone
                    if (value != null) {
                        value = new Date(value.getTime());
                    }
                    receiver.getCComponent().setValue(value);
                    hide();
                    receiver.setFocus(true);
                }
            }
        });

    }

    public static DatePickerDropDownPanel instance() {
        if (instance == null) {
            instance = new DatePickerDropDownPanel();
        }
        return instance;
    }

    public static void showDatePicker(NativeDatePicker textBox) {
        instance().attachAndShow(textBox);
    }

    private void attachAndShow(NativeDatePicker textBox) {
        if (currenttextBox == textBox) {
            hideDatePicker();
            return;
        }
        currenttextBox = null;
        Date selectedDate = null;
        String value = textBox.getNativeText().trim();
        if (!value.equals("")) {
            try {
                selectedDate = textBox.getCComponent().getValue();
            } catch (IllegalArgumentException e) {
                log.info("Cannot parse as date: " + value);
            }
        }
        if (selectedDate == null) {
            selectedDate = new Date();
        }
        picker.setValue(selectedDate);
        picker.setCurrentMonth(selectedDate);
        showRelativeTo(textBox);
        currenttextBox = textBox;
    }

    public static void hideDatePicker() {
        instance().hide();
    }

    @Override
    public void hide(boolean autohide) {
        super.hide(autohide);
        currenttextBox = null;
    }

}
