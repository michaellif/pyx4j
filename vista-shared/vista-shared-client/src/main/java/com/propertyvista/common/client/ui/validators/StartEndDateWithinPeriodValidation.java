/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 3, 2012
 * @author yuriyl
 * @version $Id$
 */
package com.propertyvista.common.client.ui.validators;

import com.google.gwt.user.datepicker.client.CalendarUtil;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

public class StartEndDateWithinPeriodValidation {

    private static final I18n i18n = I18n.get(StartEndDateWithinPeriodValidation.class);

    public StartEndDateWithinPeriodValidation(final CComponent<LogicalDate> start, final CComponent<LogicalDate> end, int months, int days) {
        this(start, end, months, days, null);
    }

    public StartEndDateWithinPeriodValidation(final CComponent<LogicalDate> start, final CComponent<LogicalDate> end, final int months, final int days,
            String message) {
        if (message == null) {
            message = i18n.tr("The Start Date must be within Specified Period of the End Date");
        }
        final String msg = message;

        start.addValueValidator(new EditableValueValidator<LogicalDate>() {
            @Override
            public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                if (value == null || !end.isVisible() || end.getValue() == null) {
                    return null;
                }
                LogicalDate endDate = add(end.getValue(), -months, -days);
                return (!value.before(endDate) ? null : new ValidationError(component, msg));
            }
        });
        start.addValueChangeHandler(new RevalidationTrigger<LogicalDate>(end));

        end.addValueValidator(new EditableValueValidator<LogicalDate>() {
            @Override
            public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                if (value == null || !start.isVisible() || start.getValue() == null) {
                    return null;
                }
                LogicalDate startDate = add(start.getValue(), months, days);
                return (!value.after(startDate) ? null : new ValidationError(component, msg));
            }
        });
        end.addValueChangeHandler(new RevalidationTrigger<LogicalDate>(start));
    }

    private static LogicalDate add(LogicalDate date, int months, int days) {
        CalendarUtil.addMonthsToDate(date, months);
        CalendarUtil.addDaysToDate(date, days);
        return date;
    }
}
