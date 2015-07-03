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
 */
package com.pyx4j.widgets.client;

import java.text.ParseException;
import java.util.Date;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.datepicker.DatePickerComposite;

public class DatePicker extends ValueBoxBase<LogicalDate> implements HasValueChangeHandlers<LogicalDate> {

    private static final I18n i18n = I18n.get(DatePicker.class);

    public static final String defaultDateFormat = i18n.tr("MM/dd/yyyy");

    private DatePickerDropDownPanel datePickerDropDown;

    public DatePicker() {
        setTextBoxWidget(new com.google.gwt.user.client.ui.TextBox());

        setAction(new Command() {

            @Override
            public void execute() {
                setFocus(true);
                if (datePickerDropDown == null) {
                    datePickerDropDown = new DatePickerDropDownPanel();
                    datePickerDropDown.addFocusHandler(getGroupFocusHandler());
                    datePickerDropDown.addBlurHandler(getGroupFocusHandler());

                    datePickerDropDown.addCloseHandler(new CloseHandler<PopupPanel>() {

                        @Override
                        public void onClose(CloseEvent<PopupPanel> event) {
                            if (DatePicker.this.isActive()) {
                                DatePicker.this.toggleActive();
                            }
                        }
                    });
                }

                if (DatePicker.this.isActive()) {
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
        if (super.getValue() == null) {
            return null;
        } else {
            return new LogicalDate(super.getValue());//Copy-constructor to simulate immutable state
        }
    }

    @Override
    protected IParser<LogicalDate> getParser() {
        if (super.getParser() == null) {
            setParser(new DateParser(defaultDateFormat));
        }
        return super.getParser();
    }

    @Override
    protected IFormatter<LogicalDate, String> getFormatter() {
        if (super.getFormatter() == null) {
            setFormatter(new DateFormatter(defaultDateFormat));
        }
        return super.getFormatter();
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

    class DatePickerDropDownPanel extends DropDownPanel implements Focusable {

        private final DatePickerComposite picker;

        private final FocusPanel focusPanel;

        public DatePickerDropDownPanel() {
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

                    DatePicker.this.updateTextBox(value, true, null);
                    hideDatePicker();
                }
            });

            setAnimationEnabled(false);
            initializeKeyListener();
        }

        public void showDatePicker() {
            LogicalDate selectedDate = DatePicker.this.getValue();
            if (selectedDate == null) {
                selectedDate = new LogicalDate();
            }
            picker.setDate(selectedDate);
            showRelativeTo(DatePicker.this);
            focusPanel.setFocus(true);
            if (!DatePicker.this.isActive()) {
                DatePicker.this.toggleActive();
            }
        }

        public void hideDatePicker() {
            hide();
            if (DatePicker.this.isActive()) {
                DatePicker.this.toggleActive();
            }
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

}