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

import java.util.Date;

import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.i18n.shared.I18n;

public class OldAgeValidator implements EditableValueValidator<Date> {

    private static I18n i18n = I18n.get(EditableValueValidator.class);

    @SuppressWarnings("deprecation")
    @Override
    public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
        Date current = new Date();
        return current.getYear() - value.getYear() < 150;
    }

    @Override
    public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
        return i18n.tr("Age cannot be greater than 150 years");
    }
}
