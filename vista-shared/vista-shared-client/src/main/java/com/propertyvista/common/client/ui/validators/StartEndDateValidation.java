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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

public class StartEndDateValidation {

    private static final I18n i18n = I18n.get(StartEndDateValidation.class);

    public StartEndDateValidation(final CComponent<LogicalDate> value1, final CComponent<LogicalDate> value2) {
        this(value1, value2, null);
    }

    public StartEndDateValidation(final CComponent<LogicalDate> value1, final CComponent<LogicalDate> value2, String message) {
        if (message == null) {
            message = i18n.tr("The Start Date Must Be Earlier Than The End Date");
        }
        final String msg = message;

        value1.addValueValidator(new EditableValueValidator<LogicalDate>() {

            @Override
            public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                if (value1.getValue() == null || value2.getValue() == null) {
                    return null;
                }
                LogicalDate end = value2.getValue();
                return (value != null) && !value.after(end) ? null : new ValidationError(component, msg);
            }

        });

        value2.addValueValidator(new EditableValueValidator<LogicalDate>() {
            @Override
            public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
                if (value1.getValue() == null || value2.getValue() == null) {
                    return null;
                }
                LogicalDate start = value1.getValue();
                return (value != null) && !value.before(start) ? null : new ValidationError(component, msg);
            }

        });
    }
}
