/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 14, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports;

import java.util.Set;

import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

public class NotEmptySetValidator extends AbstractComponentValidator<Set<?>> {

    private static final I18n i18n = I18n.get(NotEmptySetValidator.class);

    @Override
    public FieldValidationError isValid() {
        Set<?> value = getComponent().getValue();
        boolean isEmpty = value == null;
        if (value != null) {
            isEmpty = value.isEmpty();
        }
        if (isEmpty) {
            return new FieldValidationError(getComponent(), i18n.tr("at least one status is required"));
        } else {
            return null;
        }
    }

}
