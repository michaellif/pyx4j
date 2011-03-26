/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.propertyvista.portal.domain.Money;

import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;

public class DefaultMoneyValidator implements EditableValueValidator<Money> {

    private static I18n i18n = I18nFactory.getI18n(DefaultMoneyValidator.class);

    public DefaultMoneyValidator() {
    }

    @Override
    public boolean isValid(CEditableComponent<Money, ?> component, Money value) {
        return (value != null);
    }

    @Override
    public String getValidationMessage(CEditableComponent<Money, ?> component, Money value) {
        return i18n.tr("Amount should be a numeric value");
    }
}
