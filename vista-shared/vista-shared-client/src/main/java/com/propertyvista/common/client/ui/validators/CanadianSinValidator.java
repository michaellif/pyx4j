/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.validators;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.util.ValidationUtils;

public class CanadianSinValidator extends AbstractComponentValidator<String> {

    private static final I18n i18n = I18n.get(CanadianSinValidator.class);

    @Override
    public FieldValidationError isValid() {
        if (CommonsStringUtils.isStringSet(getComponent().getValue())) {
            return ValidationUtils.isSinValid(getComponent().getValue().trim().replaceAll(" ", "")) ? null : new FieldValidationError(getComponent(),
                    i18n.tr("Invalid SIN"));
        } else {
            return null;
        }
    }

    /**
     * here is one VALID SIN as example: 046 454 286
     * (http://en.wikipedia.org/wiki/Social_Insurance_Number)
     */
}
