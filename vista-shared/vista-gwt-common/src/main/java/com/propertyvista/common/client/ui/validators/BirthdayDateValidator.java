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
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;

public class BirthdayDateValidator implements EditableValueValidator<LogicalDate> {

    private static final I18n i18n = I18n.get(BirthdayDateValidator.class);

    @Override
    public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
        if (value != null && value.compareTo(new LogicalDate(System.currentTimeMillis() - 120L * 365 * 24 * 60 * 60 * 1000)) < 0) {
            return new ValidationError(component, i18n.tr("This date is too far in the past. Please enter your birthdate."));
        }
        return (value == null) || value.before(new LogicalDate(ClientContext.getServerDate())) ? null : new ValidationError(component,
                i18n.tr("The Date Must Be Earlier Than Today's Date"));
    }
}
