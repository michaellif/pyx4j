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
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.ref.Country;

public class ZipCodeValueValidator implements EditableValueValidator<String> {

    private static I18n i18n = I18n.get(ZipCodeValueValidator.class);

    private final CEntityEditor<?> editor;

    private final Path countryPath;

    public ZipCodeValueValidator(CEntityEditor<?> editor, Country countryMemeberProto) {
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
            return ValidationUtils.isCanadianPostalCodeValid(value);
        } else if ("United States".equals(c)) {
            return ValidationUtils.isUSZipCodeValid(value);
        } else {
            return true;
        }
    }

    @Override
    public String getValidationMessage(CEditableComponent<String, ?> component, String value) {
        String c = countryName();
        if ("Canada".equals(c)) {
            return i18n.tr("Invalid Canadian Postal Code");
        } else if ("United States".equals(c)) {
            return i18n.tr("Invalid US Zip code");
        } else {
            return null;
        }
    }

}
