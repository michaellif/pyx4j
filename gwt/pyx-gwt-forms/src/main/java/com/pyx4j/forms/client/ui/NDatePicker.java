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

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.PopupPanel;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.widgets.client.TextBox;

public class NDatePicker extends NTextFieldBase<LogicalDate, TextBox, CDatePicker> implements INativeTextComponent<LogicalDate> {

    private DatePickerDropDownPanel datePickerDropDown;

    public NDatePicker(CDatePicker cComponent) {
        super(cComponent);

    }

    @Override
    protected TextBox createEditor() {
        return new DatePickerTextBox();
    }

    class DatePickerTextBox extends TextBox {
        public DatePickerTextBox() {
            setAction(new Command() {

                @Override
                public void execute() {
                    if (datePickerDropDown == null) {
                        datePickerDropDown = new DatePickerDropDownPanel(NDatePicker.this) {
                            @Override
                            public void hideDatePicker() {
                                super.hideDatePicker();
                                if (DatePickerTextBox.this.isActive()) {
                                    DatePickerTextBox.this.toggleActive();
                                }
                            };

                            @Override
                            public void showDatePicker() {
                                super.showDatePicker();
                                if (!DatePickerTextBox.this.isActive()) {
                                    DatePickerTextBox.this.toggleActive();
                                }
                            };
                        };
                        datePickerDropDown.addFocusHandler(getGroupFocusHandler());
                        datePickerDropDown.addBlurHandler(getGroupFocusHandler());

                        datePickerDropDown.addCloseHandler(new CloseHandler<PopupPanel>() {

                            @Override
                            public void onClose(CloseEvent<PopupPanel> event) {
                                if (DatePickerTextBox.this.isActive()) {
                                    DatePickerTextBox.this.toggleActive();
                                }
                            }
                        });
                    }

                    if (DatePickerTextBox.this.isActive()) {
                        datePickerDropDown.showDatePicker();
                    } else {
                        datePickerDropDown.hideDatePicker();
                    }

                }
            }, ImageFactory.getImages().datePicker());

            addMouseDownHandler(new MouseDownHandler() {
                @Override
                public void onMouseDown(MouseDownEvent event) {
                    if (datePickerDropDown != null) {
                        datePickerDropDown.hideDatePicker();
                    }
                }
            });
        }
    }

}