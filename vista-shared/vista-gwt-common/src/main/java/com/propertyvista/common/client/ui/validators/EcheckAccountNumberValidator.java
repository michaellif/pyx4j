/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-14
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.validators;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.payment.TokenizedAccountNumber;
import com.propertyvista.domain.util.ValidationUtils;

public class EcheckAccountNumberValidator implements EditableValueValidator<TokenizedAccountNumber> {

    private static final I18n i18n = I18n.get(EcheckAccountNumberValidator.class);

    @Override
    public ValidationError isValid(CComponent<TokenizedAccountNumber, ?> component, TokenizedAccountNumber value) {
        if (CommonsStringUtils.isStringSet(value.newNumberValue().getValue())) {
            return ValidationUtils.isAccountNumberValid(value.newNumberValue().getValue()) ? null : new ValidationError(component,
                    i18n.tr("Account Number should consist of up to 12 digits"));
        } else {
            return null;
        }
    }

}