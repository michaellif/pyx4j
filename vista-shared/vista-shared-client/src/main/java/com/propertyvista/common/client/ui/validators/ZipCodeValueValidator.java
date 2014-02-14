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
package com.propertyvista.common.client.ui.validators;

import com.pyx4j.commons.ValidationUtils;
import com.pyx4j.entity.core.Path;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.ref.Country;

public class ZipCodeValueValidator extends AbstractComponentValidator<String> {

    private static final I18n i18n = I18n.get(ZipCodeValueValidator.class);

    private final CEntityForm<?> editor;

    private final Path countryPath;

    public ZipCodeValueValidator(CEntityForm<?> editor, Country countryMemberProto) {
        this.editor = editor;
        this.countryPath = countryMemberProto.getPath();
    }

    private String countryName() {
        if (editor.getValue() == null) {
            return null;
        }
        Country country = (Country) editor.getValue().getMember(countryPath);
        return country.name().getValue();
    }

    @Override
    public FieldValidationError isValid() {
        String value = getComponent().getValue();
        CComponent<String> component = getComponent();
        if (value == null) {
            return null;
        }
        String c = countryName();
        if ("Canada".equals(c)) {
            return ValidationUtils.isCanadianPostalCodeValid(value) ? null : new FieldValidationError(component, i18n.tr("Invalid Canadian Postal Code"));
        } else if ("United States".equals(c)) {
            return ValidationUtils.isUSZipCodeValid(value) ? null : new FieldValidationError(component, i18n.tr("Invalid US Zip Code"));
        } else if ("United Kingdom".equals(c)) {
            return ValidationUtils.isUKPostalCodeValid(value) ? null : new FieldValidationError(component, i18n.tr("Invalid UK Postal Code"));
        } else {
            return null;
        }
    }

}
