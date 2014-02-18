/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-14
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.validators;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.forms.client.validators.AbstractValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;

public class FutureDateIncludeTodayValidator extends AbstractComponentValidator<LogicalDate> {

    private static final I18n i18n = I18n.get(FutureDateIncludeTodayValidator.class);

    private final LogicalDate point;

    private final String message;

    public FutureDateIncludeTodayValidator() {
        this(i18n.tr("The Date Must Be Later Than Or Equal To Today's Date"));
    }

    public FutureDateIncludeTodayValidator(String message) {
        this(null, message);
    }

    public FutureDateIncludeTodayValidator(LogicalDate point, String message) {
        this.point = point;
        this.message = message;
    }

    @Override
    public FieldValidationError isValid() {
        return (getComponent().getValue() == null) || !getComponent().getValue().before(point != null ? point : new LogicalDate(ClientContext.getServerDate())) ? null
                : new FieldValidationError(getComponent(), message);
    }

    public AbstractValidationError isValid(CComponent<LogicalDate> component) {
        setComponent(component);
        return isValid();
    }

}
