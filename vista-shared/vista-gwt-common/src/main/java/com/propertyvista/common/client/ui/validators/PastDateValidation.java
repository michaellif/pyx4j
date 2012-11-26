/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 1, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.validators;

import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;

public class PastDateValidation {

    private static final I18n i18n = I18n.get(PastDateValidation.class);

    public PastDateValidation(CComponent<LogicalDate, ?> value) {
        this(value, i18n.tr("The Date Must Be Earlier Than Today's Date"));
    }

    public PastDateValidation(final CComponent<LogicalDate, ?> component, final String message) {
        component.addValueValidator(new EditableValueValidator<Date>() {
            @Override
            public ValidationError isValid(CComponent<Date, ?> component, Date value) {
                return (value == null) || !value.after(ClientContext.getServerDate()) ? null : new ValidationError(component, message);
            }

        });
    }
}
