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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;

public class FutureDateValidator<E extends Date> implements EditableValueValidator<E> {

    private static final I18n i18n = I18n.get(FutureDateValidator.class);

    private final String message;

    public FutureDateValidator() {
        this(i18n.tr("The Date Must Be Later Than Today's Date"));
    }

    public FutureDateValidator(String message) {
        this.message = message;
    }

    @Override
    public ValidationError isValid(CComponent<E> component, E value) {
        return (value == null) || !value.before(new LogicalDate(ClientContext.getServerDate())) ? null : new ValidationError(component, message);
    }
}
