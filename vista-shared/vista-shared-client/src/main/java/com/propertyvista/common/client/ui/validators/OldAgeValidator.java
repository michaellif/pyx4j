/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-27
 * @author TPRGLET
 * @version $Id$
 */
package com.propertyvista.common.client.ui.validators;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;

public class OldAgeValidator extends AbstractComponentValidator<LogicalDate> {

    private static final I18n i18n = I18n.get(OldAgeValidator.class);

    @Override
    public BasicValidationError isValid() {
        LogicalDate value = getComponent().getValue();
        if (value == null) {
            return null;
        }
        LogicalDate current = new LogicalDate(ClientContext.getServerDate());
        return current.getYear() - value.getYear() < 150 ? null : new BasicValidationError(getComponent(), i18n.tr("Age cannot be greater than 150 years"));
    }

}
