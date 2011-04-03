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

import com.pyx4j.widgets.client.DropDownPanel;
import com.pyx4j.widgets.client.datepicker.DatePickerComposite;

public class DatePickerDropDownPanel extends DropDownPanel {

    private static final Logger log = LoggerFactory.getLogger(DatePickerDropDownPanel.class);

    private DatePickerComposite picker;

    private NativeDatePicker currenttextBox;

    public DatePickerDropDownPanel() {
        createDatePicker();
        setAnimationEnabled(false);
    }

    public void showDatePicker(NativeDatePicker textBox) {
        attachAndShow(textBox);
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
        picker.setDate(selectedDate);
        showRelativeTo(textBox);
        currenttextBox = textBox;
    }

    public void hideDatePicker() {
        hide();
    }

    @Override
    public void hide(boolean autohide) {
        super.hide(autohide);
        currenttextBox = null;
//        picker = null;
    }

    private void createDatePicker() {
        setWidget(picker = new DatePickerComposite());
        picker.addDateChosenEventHandler(new DatePickerComposite.DateChosenEventHandler() {

            @Override
            public void onDateChosen(DatePickerComposite.DateChosenEvent event) {
                NativeDatePicker receiver = currenttextBox;
                if (receiver != null) {
                    Date value = event.getChosenDate();
                    if (value != null) { // Clone without time component!
                        value = new Date(value.getYear(), value.getMonth(), value.getDate());
                    }

                    currenttextBox.setNativeValue(value);
                    receiver.getCComponent().onEditingStop();
                    hide();
                    receiver.setFocus(true);
                }
            }
        });
    }

}
