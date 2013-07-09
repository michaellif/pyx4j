/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-10
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.validators;

import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

public class BirthdayDateValidator implements EditableValueValidator<LogicalDate> {

    private static final I18n i18n = I18n.get(BirthdayDateValidator.class);

    @Override
    public ValidationError isValid(CComponent<LogicalDate> component, LogicalDate value) {
        if (value == null) {
            return null;
        }
        return value.before(new Date()) ? null : new ValidationError(component, i18n.tr("Future birthday date"));//yuriyl temp note: next birthday date? invalid birthday date?
    }

}
