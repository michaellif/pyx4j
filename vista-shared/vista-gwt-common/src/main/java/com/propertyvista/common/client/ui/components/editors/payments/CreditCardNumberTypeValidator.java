/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 17, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors.payments;

import com.pyx4j.entity.shared.IPersonalIdentity;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.util.ValidationUtils;

public class CreditCardNumberTypeValidator implements EditableValueValidator<IPersonalIdentity> {

    private static final I18n i18n = I18n.get(CreditCardNumberTypeValidator.class);

    public interface CreditCardTypeProvider {

        CreditCardType getCreditCardType();

    }

    private final CreditCardTypeProvider creditCardTypeProvider;

    public CreditCardNumberTypeValidator(CreditCardTypeProvider creditCardTypeProvider) {
        this.creditCardTypeProvider = creditCardTypeProvider;
    }

    @Override
    public ValidationError isValid(CComponent<IPersonalIdentity, ?> component, IPersonalIdentity value) {
        if ((value == null) || value.newNumber().isNull()) {
            return null; // editing tokenized credit card.
        } else if (creditCardTypeProvider.getCreditCardType() == null
                || (!ValidationUtils.isCreditCardNumberIinValid(creditCardTypeProvider.getCreditCardType().iinsPatterns, value.newNumber().getValue()))) {
            return new ValidationError(component, i18n.tr("The credit card number doesn't match the credit card type"));
        } else {
            return null;
        }
    }
}
