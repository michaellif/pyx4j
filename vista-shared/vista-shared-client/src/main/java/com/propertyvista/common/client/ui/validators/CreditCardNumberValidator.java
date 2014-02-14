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
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.payment.CreditCardNumberIdentity;
import com.propertyvista.domain.util.ValidationUtils;

public class CreditCardNumberValidator extends AbstractComponentValidator<CreditCardNumberIdentity> {

    private static final I18n i18n = I18n.get(CreditCardNumberValidator.class);

    @Override
    public FieldValidationError isValid() {
        CreditCardNumberIdentity value = getComponent().getValue();
        if ((value != null) && CommonsStringUtils.isStringSet(value.newNumber().getValue())) {
            return ValidationUtils.isCreditCardNumberValid(value.newNumber().getValue()) ? null : new FieldValidationError(getComponent(),
                    i18n.tr("Invalid Credit Card Number"));
        } else {
            return null;
        }
    }

}