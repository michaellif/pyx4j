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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;

public class PastDateIncludeTodayValidator extends AbstractComponentValidator<LogicalDate> {

    private static final I18n i18n = I18n.get(PastDateIncludeTodayValidator.class);

    private final LogicalDate point;

    private final String message;

    public PastDateIncludeTodayValidator() {
        this(i18n.tr("The Date Must Be Earlier Than Or Equal To Today's Date"));
    }

    public PastDateIncludeTodayValidator(String message) {
        this(null, message);
    }

    public PastDateIncludeTodayValidator(LogicalDate point, String message) {
        this.point = point;
        this.message = message;
    }

    @Override
    public FieldValidationError isValid() {
        LogicalDate value = getComponent().getValue();
        return (value == null) || !value.after(point != null ? point : new LogicalDate(ClientContext.getServerDate())) ? null : new FieldValidationError(
                getComponent(), message);
    }
}
