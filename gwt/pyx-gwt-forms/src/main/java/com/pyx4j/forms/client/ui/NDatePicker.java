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

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.forms.client.ImageFactory;

public class NDatePicker extends NTextBox<Date> implements INativeTextComponent<Date> {

    private DatePickerDropDownPanel datePickerDropDown;

    public NDatePicker(CDatePicker cComponent) {
        super(cComponent, ImageFactory.getImages().triggerBlueUp());

    }

    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
        getEditor().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (datePickerDropDown != null) {
                    datePickerDropDown.hideDatePicker();
                }
            }
        });

    }

    @Override
    public void onTriggerOn() {
        if (datePickerDropDown == null) {
            datePickerDropDown = new DatePickerDropDownPanel(NDatePicker.this);
            datePickerDropDown.addFocusHandler(getGroupFocusHandler());
            datePickerDropDown.addBlurHandler(getGroupFocusHandler());
        }
        datePickerDropDown.showDatePicker();
    }

    @Override
    public void onTriggerOff() {
        if (datePickerDropDown != null) {
            datePickerDropDown.hideDatePicker();
        }
    }

}