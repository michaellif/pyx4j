/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.forms.client.validators.TextBoxParserValidator;
import com.pyx4j.i18n.shared.I18n;

public class CPhoneField extends CTextFieldBase<String, NTextBox<String>> {

    private static final I18n i18n = I18n.get(CPhoneField.class);

    public enum PhoneType {
        northAmerica, northAmericaWithExtension, search
    }

    public CPhoneField() {
        this(PhoneType.northAmericaWithExtension);
    }

    public CPhoneField(PhoneType phoneType) {
        super();
        setFormatter(new PhoneFormatter(phoneType));
        setParser(new PhoneParser(phoneType));
        addComponentValidator(new TextBoxParserValidator<String>());
        setNativeComponent(new NTextBox<String>(this));
        setWatermark(phoneType);
        asWidget().setWidth("100%");
    }

    private void setWatermark(PhoneType phoneType) {
        switch (phoneType) {
        case northAmerica:
            setWatermark("(___) ___-____");
            break;
        case northAmericaWithExtension:
            setWatermark("(___) ___-____ x___");
            break;
        case search:
            break;
        default:
            break;
        }
    }

    public static class PhoneFormatter implements IFormatter<String> {

        final PhoneType phoneType;

        public PhoneFormatter(PhoneType phoneType) {
            this.phoneType = phoneType;
        }

        @Override
        public String format(String value) {
            if (value == null) {
                return null;
            }
            String unformatedPhone = normalize(value);
            if (unformatedPhone.length() == 10) {
                return "(" + unformatedPhone.subSequence(0, 3) + ") " + unformatedPhone.subSequence(3, 6) + "-" + unformatedPhone.subSequence(6, 10);
            } else if (unformatedPhone.length() > 10) {
                return "(" + unformatedPhone.subSequence(0, 3) + ") " + unformatedPhone.subSequence(3, 6) + "-" + unformatedPhone.subSequence(6, 10) + " "
                        + unformatedPhone.subSequence(10, unformatedPhone.length());
            } else {
                return unformatedPhone;
            }
        }

        String normalize(String value) {
            if (value == null) {
                return null;
            } else {
                return value.replaceAll("[\\s\\(\\)-]+", "");
            }
        }
    }

    public static class PhoneParser extends PhoneFormatter implements IParser<String> {

        private final static String NORTH_AMERICA_REGEX = "^[\\s\\d\\(\\)-]+$";

        private final static String NORTH_AMERICA_WITH_EXTENSION_REGEX = "^[\\s\\d\\(\\)-]+(x\\d{1,4}){0,1}$";

        private final static String SEARCH_REGEX = ".*[^0-9\\s\\(\\)-]+.*";

        public PhoneParser(PhoneType phoneType) {
            super(phoneType);
        }

        @Override
        public String parse(String string) throws ParseException {
            if (CommonsStringUtils.isEmpty(string)) {
                return null; // empty value case
            }

            StringBuilder errorMessage = new StringBuilder(i18n.tr("Invalid phone format."));
            String regex = null;

            switch (phoneType) {
            case northAmerica:
                errorMessage.append(" ").append(i18n.tr("Use (123) 456-7890 format"));
                regex = NORTH_AMERICA_REGEX;
                break;

            case northAmericaWithExtension:
                errorMessage.append(" ").append(i18n.tr("Use (123) 456-7890 x1234 format"));
                regex = NORTH_AMERICA_WITH_EXTENSION_REGEX;
                break;

            case search:
                if (string.contains("*")) {
                    return string;
                }
                regex = SEARCH_REGEX;
                break;
            }

            if (!string.matches(regex)) {
                throw new ParseException(errorMessage.toString(), 0);
            }

            String unformatedPhone = normalize(string);

            /**
             * Validate length
             */
            switch (phoneType) {
            case northAmerica:
                if (unformatedPhone.length() != 10) {
                    throw new ParseException(errorMessage.toString(), 0);
                }
                break;
            case northAmericaWithExtension:
                if (!(unformatedPhone.indexOf("x") == 10 || unformatedPhone.length() == 10)) {
                    throw new ParseException(errorMessage.toString(), 0);
                }
                break;
            case search:
                break;

            }

            return format(string);
        }
    }

}