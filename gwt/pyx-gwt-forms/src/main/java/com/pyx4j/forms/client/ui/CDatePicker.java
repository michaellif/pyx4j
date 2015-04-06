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
 */
package com.pyx4j.forms.client.ui;

import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.DatePicker;

public class CDatePicker extends CValueBoxBase<LogicalDate, NDatePicker> {

    private static final I18n i18n = I18n.get(CDatePicker.class);

    private boolean pastDateSelectionAllowed = true;

    private String dateConditionValidationMessage;

    private final PastDateSelectionAllowedValidator pastDateSelectionAllowedValidator = new PastDateSelectionAllowedValidator();

    public CDatePicker() {
        super();
        setDateFormat(DatePicker.defaultDateFormat);
        setNativeComponent(new NDatePicker(this));
    }

    public void setDateFormat(String pattern) {
        setFormatter(new DatePicker.DateFormatter(pattern));
        setParser(new DatePicker.DateParser(pattern));
        setWatermark(pattern.toUpperCase());
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
            this.removeComponentValidator(pastDateSelectionAllowedValidator);
        } else {
            this.addComponentValidator(pastDateSelectionAllowedValidator);
        }

    }

    class PastDateSelectionAllowedValidator extends AbstractComponentValidator<LogicalDate> {

        private String getValidationMessage() {
            if (dateConditionValidationMessage == null) {
                return i18n.tr("Past Date not allowed");
            } else {
                return dateConditionValidationMessage;
            }
        }

        @Override
        @SuppressWarnings("deprecation")
        public BasicValidationError isValid() {
            LogicalDate selectedDate = getValue();
            if (selectedDate != null && !pastDateSelectionAllowed) {
                Date now = new Date();
                Date today = new Date(now.getYear(), now.getMonth(), now.getDate());
                return selectedDate.compareTo(today) >= 0 ? null : new BasicValidationError(CDatePicker.this, getValidationMessage());
            } else {
                return null;
            }
        }

    }

}
