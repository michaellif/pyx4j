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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.i18n.client.DateTimeFormat;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.TextBoxParserValidator;

public class CDatePicker extends CTextFieldBase<Date, NativeDatePicker> {

    protected static I18n i18n = I18nFactory.getI18n(CDatePicker.class);

    private boolean pastDateSelectionAllowed = true;

    private String dateConditionValidationMessage;

    private final PastDateSelectionAllowedValidator pastDateSelectionAllowedValidator = new PastDateSelectionAllowedValidator();

    public CDatePicker() {
        this(null);
    }

    public CDatePicker(String title) {
        super(title);
        setFormat(new DateFormat());
        addValueValidator(new TextBoxParserValidator<Date>(i18n.tr("Should be in format MM/dd/yyyy")));
    }

    @Override
    protected NativeDatePicker createWidget() {
        return new NativeDatePicker(this);
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

    class PastDateSelectionAllowedValidator implements EditableValueValidator<Date> {

        @Override
        public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
            if (dateConditionValidationMessage == null) {
                return i18n.tr("Date must be future date or equal to today's date");
            } else {
                return dateConditionValidationMessage;
            }
        }

        @Override
        @SuppressWarnings("deprecation")
        public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
            Date selectedDate = getValue();
            if (selectedDate != null && !pastDateSelectionAllowed) {
                Date now = new Date();
                Date today = new Date(now.getYear(), now.getMonth(), now.getDate());
                return selectedDate.compareTo(today) >= 0;
            } else {
                return true;
            }
        }

    }

    static class DateFormat implements IFormat<Date> {

        private static DateTimeFormat parser = DateTimeFormat.getFormat(i18n.tr("MM/dd/yy"));

        private static DateTimeFormat formatter = DateTimeFormat.getFormat(i18n.tr("MM/dd/yyyy"));

        @Override
        public String format(Date value) {
            return formatter.format(value);
        }

        @Override
        public Date parse(String string) {
            if (CommonsStringUtils.isEmpty(string)) {
                return null;
            }
            try {
                return parser.parseStrict(string.replace('-', '/'));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

    }

}
