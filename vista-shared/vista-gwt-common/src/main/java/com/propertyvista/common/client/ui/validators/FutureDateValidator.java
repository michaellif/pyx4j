/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-14
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.validators;

import java.util.Date;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

public class FutureDateValidator implements EditableValueValidator<Date> {

    private static final I18n i18n = I18n.get(FutureDateValidator.class);

    private String message = i18n.tr("The Date Must Be Later Than Today's Date");

    public FutureDateValidator() {
        this(i18n.tr("The Date Must Be Later Than Today's Date"));
    }

    public FutureDateValidator(String message) {
        this.message = message;
    }

    @Override
    public ValidationError isValid(CComponent<Date, ?> component, Date value) {
        return (value == null) || !value.before(TimeUtils.today()) ? null : new ValidationError(component, message);
    }
}
