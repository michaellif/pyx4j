/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 26, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.common.client.ui.validators;

import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

public class YouTubeVideoIdValidator extends AbstractComponentValidator<String> {

    private static final I18n i18n = I18n.get(YouTubeVideoIdValidator.class);

    @Override
    public FieldValidationError isValid() {
        String value = getComponent().getValue();
        if (value == null) {
            return null;
        }
        for (String p : new String[] { "watch?v=", "watch#!v=", "/vi/", "/?v=", "/v/" }) {
            String v = YouTubeVideoIdFormat.extract(value, p);
            if (v != null) {
                value = v;
                break;
            }
        }
        value = value.trim();
        return value.matches("[a-zA-Z0-9_-]{11}") ? null : new FieldValidationError(getComponent(), i18n.tr("This Is Not A Valid YouTube Video ID"));
    }

}
