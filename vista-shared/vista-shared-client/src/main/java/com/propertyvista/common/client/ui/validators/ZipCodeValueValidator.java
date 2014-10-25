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
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.Path;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.ref.ISOCountry;

public class ZipCodeValueValidator extends AbstractComponentValidator<String> {

    private static final I18n i18n = I18n.get(ZipCodeValueValidator.class);

    private final CForm<?> editor;

    private final Path countryPath;

    public ZipCodeValueValidator(CForm<?> editor, IObject<ISOCountry> countryMemberProto) {
        this.editor = editor;
        this.countryPath = countryMemberProto.getPath();
    }

    private ISOCountry getCountry() {
        if (editor.getValue() == null) {
            return null;
        }
        return (ISOCountry) editor.getValue().getMember(countryPath).getValue();
    }

    @Override
    public BasicValidationError isValid() {
        String value = getCComponent().getValue();
        CComponent<?, String, ?> component = getCComponent();
        if (value == null) {
            return null;
        }
        ISOCountry c = getCountry();
        if (ISOCountry.Canada.equals(c)) {
            return ValidationUtils.isCanadianPostalCodeValid(value) ? null : new BasicValidationError(component, i18n.tr("Invalid Canadian Postal Code"));
        } else if (ISOCountry.UnitedStates.equals(c)) {
            return ValidationUtils.isUSZipCodeValid(value) ? null : new BasicValidationError(component, i18n.tr("Invalid US Zip Code"));
        } else if (ISOCountry.UnitedKingdom.equals(c)) {
            return ValidationUtils.isUKPostalCodeValid(value) ? null : new BasicValidationError(component, i18n.tr("Invalid UK Postal Code"));
        } else {
            return null;
        }
    }

}
