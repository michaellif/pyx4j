/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-07
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui.validators;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.propertyvista.portal.domain.ref.Country;

import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;

public class ZipCodeValueValidator implements EditableValueValidator<String> {

    private static I18n i18n = I18nFactory.getI18n(ZipCodeValueValidator.class);

    public ZipCodeValueValidator() {

    }

    public ZipCodeValueValidator(CEditableComponent<Country, ?> country) {

    }

    @Override
    public boolean isValid(CEditableComponent<String, ?> component, String value) {
        // see http://en.wikipedia.org/wiki/Postal_codes_in_Canada#Number_of_possible_postal_codes
        return (value.trim().toUpperCase().matches("^[ABCEGHJKLMNPRSTVXY]{1}\\d{1}[A-Z]{1} *\\d{1}[A-Z]{1}\\d{1}$") && !value.trim().toUpperCase()
                .matches(".*[DFIOQU].*"))
                || value.trim().matches("^\\d{5}(-\\d{4})?$"); // this is US zip... 
    }

    @Override
    public String getValidationMessage(CEditableComponent<String, ?> component, String value) {
        return i18n.tr("Invalid Postal/Zip code.");
    }
}
