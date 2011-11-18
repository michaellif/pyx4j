/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 9, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors;

import java.text.ParseException;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CEmailField;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.contact.Email;

public class EmailFormatter implements IFormat<Email> {

    private static I18n i18n = I18n.get(EmailFormatter.class);

    public EmailFormatter() {
    }

    @Override
    public String format(Email value) {
        return value.address().getStringView();
    }

    @Override
    public Email parse(String string) throws ParseException {

        if (CommonsStringUtils.isEmpty(string)) {
            return null; // empty value case
        }
        if (!string.matches(CEmailField.EMAIL_REGEXPR)) {
            throw new ParseException(i18n.tr("Not A Valid Email"), 0);
        }

        Email email = EntityFactory.create(Email.class);
        email.address().setValue(string);
        return email;
    }
}