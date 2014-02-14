/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-10
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.validators;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

public class BirthdayDateValidator extends PastDateIncludeTodayValidator {

    private static final I18n i18n = I18n.get(BirthdayDateValidator.class);

    private static final String message1 = i18n.tr("The date must be earlier than or equal to today's date");

    private static final String message2 = i18n.tr("This date is too far in the past. Please enter your birth date.");

    public BirthdayDateValidator() {
        super(message1);
    }

    public BirthdayDateValidator(LogicalDate point) {
        super(point, message1);
    }

    @Override
    public FieldValidationError isValid() {
        if (getComponent().getValue() != null
                && getComponent().getValue().compareTo(new LogicalDate(System.currentTimeMillis() - 120L * 365 * 24 * 60 * 60 * 1000)) < 0) {
            return new FieldValidationError(getComponent(), message2);
        }
        return super.isValid();
    }
}
