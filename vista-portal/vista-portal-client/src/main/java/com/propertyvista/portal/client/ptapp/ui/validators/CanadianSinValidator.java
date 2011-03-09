/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui.validators;

import org.xnap.commons.i18n.I18n;

import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.i18n.shared.I18nFactory;

public class CanadianSinValidator implements EditableValueValidator<String> {

    private static I18n i18n = I18nFactory.getI18n(CanadianSinValidator.class);

    @Override
    public boolean isValid(CEditableComponent<String, ?> component, String value) {
        return value.trim().matches("^\\d{3}[ ]?\\d{3}[ ]?\\d{3}$") && isValidCC(value.trim().replaceAll(" ", ""));
    }

    @Override
    public String getValidationMessage(CEditableComponent<String, ?> component, String value) {
        return i18n.tr("Invalid SIN.");
    }

    /*
     * Generic Luhn algorithm implementation (see
     * http://en.wikipedia.org/wiki/Luhn_algorithm for details)
     * could be useful for other ID verification like CreditCard #, etc.
     */
    public static boolean isValidCC(String num) {

        final int[][] sumTable = { { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, { 0, 2, 4, 6, 8, 1, 3, 5, 7, 9 } };
        int sum = 0, flip = 0;

        for (int i = num.length() - 1; i >= 0; i--)
            sum += sumTable[flip++ & 0x1][Character.digit(num.charAt(i), 10)];
        return (sum % 10 == 0);
    }
}
