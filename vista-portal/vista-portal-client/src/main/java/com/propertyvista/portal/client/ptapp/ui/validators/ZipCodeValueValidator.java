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

import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;

public class ZipCodeValueValidator implements EditableValueValidator<String> {

    private static I18n i18n = I18nFactory.getI18n(ZipCodeValueValidator.class);

    private final CEntityEditableComponent<?> editor;

    private final Path countryPath;

    public ZipCodeValueValidator(CEntityEditableComponent<?> editor, Country countryMemeberProto) {
        this.editor = editor;
        this.countryPath = countryMemeberProto.getPath();
    }

    private String countryName() {
        Country country = (Country) editor.getValue().getMember(countryPath);
        return country.name().getValue();
    }

    @Override
    public boolean isValid(CEditableComponent<String, ?> component, String value) {
        String c = countryName();
        if ("Canada".equals(c)) {
            // see http://en.wikipedia.org/wiki/Postal_codes_in_Canada#Number_of_possible_postal_codes
            return canadianPostalCodeValidation(value);
        } else if ("United States".equals(c)) {
            return usZipCodeValidation(value);
        } else {
            return true;
        }
    }

    @Override
    public String getValidationMessage(CEditableComponent<String, ?> component, String value) {
        String c = countryName();
        if ("Canada".equals(c)) {
            return i18n.tr("Invalid Canadian Postal code.");
        } else if ("United States".equals(c)) {
            return i18n.tr("Invalid US Zip code.");
        } else {
            return null;
        }
    }

    private boolean canadianPostalCodeValidation(String value) {
        return value.toUpperCase().matches("^[ABCEGHJKLMNPRSTVXY]{1}\\d{1}[A-Z]{1} *\\d{1}[A-Z]{1}\\d{1}$") && !value.toUpperCase().matches(".*[DFIOQU].*");
    }

    private boolean usZipCodeValidation(String value) {
        return value.matches("^\\d{5}(-\\d{4})?$");
    }
}
