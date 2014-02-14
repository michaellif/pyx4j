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
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

public class StartEndDateValidation {

    private static final I18n i18n = I18n.get(StartEndDateValidation.class);

    public StartEndDateValidation(final CComponent<LogicalDate> start, final CComponent<LogicalDate> end) {
        this(start, end, null);
    }

    public StartEndDateValidation(final CComponent<LogicalDate> start, final CComponent<LogicalDate> end, String message) {
        if (message == null) {
            message = i18n.tr("The Start Date Must Be Earlier Than The End Date");
        }
        final String msg = message;

        start.addComponentValidator(new AbstractComponentValidator<LogicalDate>() {
            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() == null || !end.isVisible() || end.getValue() == null) {
                    return null;
                }
                LogicalDate endDate = end.getValue();
                return (!getComponent().getValue().after(endDate) ? null : new FieldValidationError(getComponent(), msg));
            }
        });
        start.addValueChangeHandler(new RevalidationTrigger<LogicalDate>(end));

        end.addComponentValidator(new AbstractComponentValidator<LogicalDate>() {
            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() == null || !start.isVisible() || start.getValue() == null) {
                    return null;
                }
                LogicalDate startDate = start.getValue();
                return (!getComponent().getValue().before(startDate) ? null : new FieldValidationError(getComponent(), msg));
            }
        });
        end.addValueChangeHandler(new RevalidationTrigger<LogicalDate>(start));
    }
}
