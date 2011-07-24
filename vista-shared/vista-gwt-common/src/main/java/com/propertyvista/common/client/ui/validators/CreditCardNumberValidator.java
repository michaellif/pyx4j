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

import org.xnap.commons.i18n.I18n;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.i18n.shared.I18nFactory;

import com.propertyvista.domain.util.ValidationUtils;

public class CreditCardNumberValidator implements EditableValueValidator<String> {

    private static I18n i18n = I18nFactory.getI18n(CreditCardNumberValidator.class);

    @Override
    public boolean isValid(CEditableComponent<String, ?> component, String value) {
        if (CommonsStringUtils.isStringSet(value)) {
            return ValidationUtils.isCreditCardNumberValid(value);
        } else {
            return true;
        }
    }

    @Override
    public String getValidationMessage(CEditableComponent<String, ?> component, String value) {
        return i18n.tr("Invalid credit card number.");
    }
}