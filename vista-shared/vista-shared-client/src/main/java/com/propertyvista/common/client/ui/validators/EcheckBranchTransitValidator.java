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
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.util.ValidationUtils;

public class EcheckBranchTransitValidator extends AbstractComponentValidator<String> {

    private static final I18n i18n = I18n.get(EcheckBranchTransitValidator.class);

    @Override
    public AbstractValidationError isValid() {
        if (CommonsStringUtils.isStringSet(getComponent().getValue())) {
            return ValidationUtils.isBranchTransitNumberValid(getComponent().getValue()) ? null : new FieldValidationError(getComponent(),
                    i18n.tr("Number should consist of 5 digits"));
        } else {
            return null;
        }
    }

}
