/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 11, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.common.client.ui.validators;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.AbstractValidationError;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.util.ValidationUtils;

public class EcheckAccountNumberStringValidator extends AbstractComponentValidator<String> {

    private static final I18n i18n = I18n.get(EcheckAccountNumberStringValidator.class);

    @Override
    public AbstractValidationError isValid() {
        if (CommonsStringUtils.isStringSet(getCComponent().getValue())) {
            return ValidationUtils.isAccountNumberValid(getCComponent().getValue()) ? null : new BasicValidationError(getCComponent(),
                    i18n.tr("Account Number should consist of up to 12 digits"));
        } else {
            return null;
        }
    }

}
