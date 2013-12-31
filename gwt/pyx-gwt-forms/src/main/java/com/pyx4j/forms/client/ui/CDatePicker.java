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

import com.google.gwt.i18n.client.DateTimeFormat;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IFormat;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.TextBoxParserValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

public class CDatePicker extends CTextFieldBase<LogicalDate, NDatePicker> {

    private static final I18n i18n = I18n.get(CDatePicker.class);

    public static final String defaultDateFormat = i18n.tr("MM/dd/yyyy");

    private boolean pastDateSelectionAllowed = true;

    private String dateConditionValidationMessage;

    private final PastDateSelectionAllowedValidator pastDateSelectionAllowedValidator = new PastDateSelectionAllowedValidator();

    public CDatePicker() {
        super();
        setFormat(new DateFormat(defaultDateFormat));
        addValueValidator(new TextBoxParserValidator<LogicalDate>());
        setNativeWidget(new NDatePicker(this));
        asWidget().setWidth("100%");
    }

    public void setDateFormat(String pattern) {
        setFormat(new DateFormat(pattern));
    }

    public void setDateConditionValidationMessage(String dateConditionValidationMessage) {
        this.dateConditionValidationMessage = dateConditionValidationMessage;
    }

    public boolean isPastDateSelectionAllowed() {
        return pastDateSelectionAllowed;
    }

    public void setPastDateSelectionAllowed(boolean pastDateSelectionAllowed) {
        this.pastDateSelectionAllowed = pastDateSelectionAllowed;
        if (pastDateSelectionAllowed) {
            this.removeValueValidator(pastDateSelectionAllowedValidator);
        } else {
            this.addValueValidator(pastDateSelectionAllowedValidator);
        }

    }

    class PastDateSelectionAllowedValidator implements EditableValueValidator<LogicalDate> {

        private String getValidationMessage() {
            if (dateConditionValidationMessage == null) {
                return i18n.tr("Past Date not allowed");
            } else {
                return dateConditionValidationMessage;
            }
        }

        @Override
        @SuppressWarnings("deprecation")
        public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
            LogicalDate selectedDate = getValue();
            if (selectedDate != null && !pastDateSelectionAllowed) {
                Date now = new Date();
                Date today = new Date(now.getYear(), now.getMonth(), now.getDate());
                return selectedDate.compareTo(today) >= 0 ? null : new ValidationError(CDatePicker.this, getValidationMessage());
            } else {
                return null;
            }
        }

    }

    static class DateFormat implements IFormat<LogicalDate> {

        private final DateTimeFormat parser;

        private final DateTimeFormat formatter;

        DateFormat(String format) {
            parser = DateTimeFormat.getFormat(format);
            formatter = DateTimeFormat.getFormat(format);
        }

        @Override
        public String format(LogicalDate value) {
            return formatter.format(value);
        }

        @Override
        public LogicalDate parse(String string) throws ParseException {
            if (CommonsStringUtils.isEmpty(string)) {
                return null; // empty value case
            }
            try {
                return new LogicalDate(parser.parseStrict(string.replace('-', '/')));
            } catch (IllegalArgumentException e) {
                throw new ParseException(i18n.tr("Invalid date format. Use MM/DD/YYYY format"), 0);
            }
        }
    }
}
