/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Jan 10, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.widgets.client.DatePickerTextBox;

public class NDatePicker extends NTextFieldBase<LogicalDate, DatePickerTextBox, CDatePicker> implements INativeTextComponent<LogicalDate> {

    public NDatePicker(CDatePicker cComponent) {
        super(cComponent);

    }

    @Override
    protected DatePickerTextBox createEditor() {
        DatePickerTextBox datePickerTextBox = new DatePickerTextBox() {
            @Override
            protected IParser<LogicalDate> getParser() {
                return getCComponent().getParser();
            }

            @Override
            protected IFormatter<LogicalDate, String> getFormatter() {
                return getCComponent().getFormatter();
            }
        };
        datePickerTextBox.addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {

            @Override
            public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                getCComponent().stopEditing();
            }
        });
        return datePickerTextBox;
    }

}