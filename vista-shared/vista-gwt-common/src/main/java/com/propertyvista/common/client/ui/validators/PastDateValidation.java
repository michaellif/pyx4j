/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 1, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.validators;

import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.i18n.shared.I18n;

public class PastDateValidation {

    private static I18n i18n = I18n.get(PastDateValidation.class);

    public PastDateValidation(CEditableComponent<LogicalDate, ?> value) {
        value.addValueValidator(new EditableValueValidator<Date>() {
            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                return (value != null) && value.after(TimeUtils.today());
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return i18n.tr("The Date cannot be earlier than Today Date");
            }
        });
    }
}
