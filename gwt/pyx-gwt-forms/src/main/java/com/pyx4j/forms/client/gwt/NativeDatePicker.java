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
package com.pyx4j.forms.client.gwt;

import java.util.Date;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CTextBox;
import com.pyx4j.forms.client.ui.INativeTextComponent;

public class NativeDatePicker extends NativeTriggerComponent<Date> implements INativeTextComponent<Date> {

    private final NativeTextBox<Date> textBox;

    public NativeDatePicker(final CDatePicker datePicker) {
        super();
        textBox = new NativeTextBox<Date>(datePicker);
        construct(textBox);
        textBox.setNativeValue(datePicker.getValue());
        setTabIndex(datePicker.getTabIndex());

        textBox.setStyleName("gwt-DateBox");

        textBox.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                DatePickerDropDownPanel.hideDatePicker();
            }
        });
        setWidth(datePicker.getWidth());
        setHeight(datePicker.getHeight());

    }

    public void setNativeValue(Date value) {
        textBox.setNativeValue(value);
    }

    public String getNativeText() {
        return textBox.getNativeText();

    }

    public CTextBox<Date> getCComponent() {
        return textBox.getCComponent();
    }

    @Override
    protected void onTrigger(boolean show) {
        if (show) {
            DatePickerDropDownPanel.showDatePicker(this);
        } else {
            DatePickerDropDownPanel.hideDatePicker();
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        textBox.setReadOnly(readOnly);
    }

    public NativeTextBox<Date> getTextBox() {
        return textBox;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textBox.setEnabled(enabled);
    }

    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        return textBox.addChangeHandler(handler);
    }

    public void setNativeText(String newValue) {
        textBox.setText(newValue);
    }

}
