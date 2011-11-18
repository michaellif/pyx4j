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
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.contact.Phone;

public class PhoneFormatter implements IFormat<Phone> {

    private static I18n i18n = I18n.get(PhoneFormatter.class);

    private final static String regex = "^\\s*(\\+?1\\s?)?(\\(?\\d{3}\\)?\\s?[\\s-]?){1,2}(\\d{4})$";

    public PhoneFormatter() {

    }

    private static String normalize(Phone value) {
        if (value == null) {
            return null;
        } else {
            return value.number().getStringView().replaceAll("[\\+\\s\\(\\)-]+", "");
        }
    }

    @Override
    public String format(Phone value) {
        if (value == null) {
            return null;
        }

        String formatedPhone;
        String unformatedPhone = normalize(value);
        if (unformatedPhone.length() == 11) {
            formatedPhone = "+" + unformatedPhone.subSequence(0, 1) + " " + unformatedPhone.subSequence(1, 4) + "-" + unformatedPhone.subSequence(4, 7) + "-"
                    + unformatedPhone.subSequence(7, 11);
        } else if (unformatedPhone.length() == 10) {
            formatedPhone = unformatedPhone.subSequence(0, 3) + "-" + unformatedPhone.subSequence(3, 6) + "-" + unformatedPhone.subSequence(6, 10);
        } else if (unformatedPhone.length() == 7) {
            formatedPhone = unformatedPhone.subSequence(0, 3) + "-" + unformatedPhone.subSequence(3, 7);
        } else {
            formatedPhone = unformatedPhone;
        }

        return formatedPhone;
    }

    @Override
    public Phone parse(String string) throws ParseException {
        if (CommonsStringUtils.isEmpty(string)) {
            return null; // empty value case
        }
        if (!string.matches(regex)) {
            throw new ParseException(
                    i18n.tr("The Number You Have Entered Is Not A Valid Phone Number. Please Use The Following Formats: 123-4567 OR 123-456-7890 (Dashes Optional)"),
                    0);
        }

        Phone phone = EntityFactory.create(Phone.class);
        phone.number().setValue(string);
        return phone;
    }
}