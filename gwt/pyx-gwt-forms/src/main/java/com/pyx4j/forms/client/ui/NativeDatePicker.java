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

import java.text.ParseException;
import java.util.Date;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public class NativeDatePicker extends NativeTriggerComponent<Date> implements INativeTextComponent<Date> {

    private final NativeTextBox<Date> textBox;

    private DatePickerDropDownPanel datePickerDropDown;

    private final CDatePicker datePicker;

    public NativeDatePicker(final CDatePicker datePicker) {
        super();
        this.datePicker = datePicker;
        textBox = new NativeTextBox<Date>(datePicker);
        construct(textBox);
        textBox.setNativeValue(datePicker.getValue());
        setTabIndex(datePicker.getTabIndex());

        textBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (datePickerDropDown != null) {
                    datePickerDropDown.hideDatePicker();
                }
            }
        });
    }

    @Override
    public void setNativeValue(Date value) {
        textBox.setNativeValue(value);
    }

    @Override
    public Date getNativeValue() throws ParseException {
        return textBox.getNativeValue();
    }

    @Override
    public String getNativeText() {
        return textBox.getNativeText();

    }

    @Override
    public void setNativeText(String newValue) {
        textBox.setText(newValue);
    }

    @Override
    public CTextFieldBase<Date, ?> getCComponent() {
        return textBox.getCComponent();
    }

    @Override
    protected void onTrigger(boolean show) {
        if (show) {
            if (datePickerDropDown == null) {
                datePickerDropDown = new DatePickerDropDownPanel(this);
                datePickerDropDown.addFocusHandler(getGroupFocusHandler());
                datePickerDropDown.addBlurHandler(getGroupFocusHandler());
            }
            datePickerDropDown.showDatePicker();
        } else {
            if (datePickerDropDown != null) {
                datePickerDropDown.hideDatePicker();
            }
        }
    }

    @Override
    public void setEditable(boolean editable) {
        super.setReadOnly(!editable);
        textBox.setEditable(editable);
        setTrigger(isEnabled() && editable);
    }

    @Override
    public boolean isEditable() {
        return !super.isReadOnly();
    }

    public NativeTextBox<Date> getTextBox() {
        return textBox;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textBox.setEnabled(enabled);
        setTrigger(isEditable() && enabled);
    }

    @Override
    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        return textBox.addChangeHandler(handler);
    }

    @Override
    public void setValid(boolean valid) {
        if (valid) {
            textBox.removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
        } else if (datePicker.isVisited()) {
            textBox.addStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
        }
    }

    @Override
    public void installStyles(String stylePrefix) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isParsedSuccesfully() {
        return textBox.isParsedSuccesfully();
    }
}
