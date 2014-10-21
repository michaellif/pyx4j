/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Oct 21, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import java.text.ParseException;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.PopupPanel;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.i18n.shared.I18n;

public class DatePickerTextBox extends TextBoxBase implements HasValueChangeHandlers<LogicalDate>, IValueWidget<LogicalDate> {

    private static final I18n i18n = I18n.get(DatePickerTextBox.class);

    public static final String defaultDateFormat = i18n.tr("MM/dd/yyyy");

    private LogicalDate value;

    private IParser<LogicalDate> parser;

    private IFormatter<LogicalDate, String> formatter;

    private boolean parsedOk;

    private DatePickerDropDownPanel datePickerDropDown;

    public DatePickerTextBox() {
        setTextBoxWidget(new com.google.gwt.user.client.ui.TextBox());

        getTextBoxWidget().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                try {
                    setValue(getParser().parse(event.getValue()), true);
                } catch (ParseException e) {
                    setValue(null, false);
                }
            }
        });

        setAction(new Command() {

            @Override
            public void execute() {
                if (datePickerDropDown == null) {
                    datePickerDropDown = new DatePickerDropDownPanel(DatePickerTextBox.this) {
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

        getTextBoxWidget().addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                if (datePickerDropDown != null) {
                    datePickerDropDown.hideDatePicker();
                }
            }
        });
    }

    @Override
    public LogicalDate getValue() {
        return value;
    }

    @Override
    public void setValue(LogicalDate value) {
        if (this.value == null && value == null) {
            return;
        } else if (this.value != null && this.value.equals(value)) {
            return;
        }
        this.parsedOk = true;
        this.value = value;
        setText(getFormatter().format(value));
    }

    @Override
    public void setParser(IParser<LogicalDate> parser) {
        this.parser = parser;
    }

    protected IParser<LogicalDate> getParser() {
        if (parser == null) {
            parser = new DateParser(defaultDateFormat);
        }
        return parser;
    }

    @Override
    public void setFormatter(IFormatter<LogicalDate, String> formatter) {
        this.formatter = formatter;
    }

    protected IFormatter<LogicalDate, String> getFormatter() {
        if (formatter == null) {
            formatter = new DateFormatter(defaultDateFormat);
        }
        return formatter;
    }

    @Override
    public boolean isParsedOk() {
        return parsedOk;
    }

    protected void setValue(LogicalDate value, boolean parsedOk) {
        if (this.parsedOk == parsedOk) {
            if (this.value == null && value == null) {
                return;
            } else if (this.value != null && this.value.equals(value)) {
                return;
            }
        }
        this.value = value;
        this.parsedOk = parsedOk;
        if (parsedOk) {
            setText(getFormatter().format(value));
        }
        ValueChangeEvent.fire(this, getValue());

    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<LogicalDate> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public static class DateParser implements IParser<LogicalDate> {

        private final DateTimeFormat parser;

        public DateParser(String format) {
            parser = DateTimeFormat.getFormat(format);
        }

        @Override
        public LogicalDate parse(String string) throws ParseException {
            if (CommonsStringUtils.isEmpty(string)) {
                return null;
            }
            try {
                return new LogicalDate(parser.parseStrict(string));
            } catch (IllegalArgumentException e) {
                throw new ParseException(i18n.tr("Invalid date format. Use {0} format", parser.getPattern()), 0);
            }
        }
    }

    public static class DateFormatter implements IFormatter<LogicalDate, String> {

        private final DateTimeFormat formatter;

        public DateFormatter(String format) {
            formatter = DateTimeFormat.getFormat(format);
        }

        @Override
        public String format(LogicalDate value) {
            return value == null ? null : formatter.format(value);
        }

    }

}