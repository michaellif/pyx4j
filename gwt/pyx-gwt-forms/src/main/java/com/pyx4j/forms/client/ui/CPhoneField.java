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
import com.pyx4j.forms.client.validators.TextBoxParserValidator;

public class CPhoneField extends CTextFieldBase<String, NativeTextBox<String>> {

    public CPhoneField() {
        this(null);
    }

    public CPhoneField(String title) {
        super(title);
        setFormat(new PhoneFormat());
        addValueValidator(new TextBoxParserValidator<String>("Not a valid phone number. Must be in the format 123-4567 or 123-456-7890 (dashes optional)"));
    }

    @Override
    protected NativeTextBox<String> createWidget() {
        return new NativeTextBox<String>(this);
    }

    public static class PhoneFormat implements IFormat<String> {

        private final static String regex = "^\\s*(\\+?1\\s?)?(\\(?\\d{3}\\)?\\s?[\\s-]?){1,2}(\\d{4})$";

        public PhoneFormat() {

        }

        public static String normalize(String value) {
            if (value == null) {
                return null;
            } else {
                return value.replaceAll("[\\+\\s\\(\\)-]+", "");
            }
        }

        @Override
        public String format(String value) {
            if (value == null) {
                return null;
            }
            String unformatedPhone = normalize(value);
            if (unformatedPhone.length() == 11) {
                return "+" + unformatedPhone.subSequence(0, 1) + " " + unformatedPhone.subSequence(1, 4) + "-" + unformatedPhone.subSequence(4, 7) + "-"
                        + unformatedPhone.subSequence(7, 11);
            } else if (unformatedPhone.length() == 10) {
                return unformatedPhone.subSequence(0, 3) + "-" + unformatedPhone.subSequence(3, 6) + "-" + unformatedPhone.subSequence(6, 10);
            } else if (unformatedPhone.length() == 7) {
                return unformatedPhone.subSequence(0, 3) + "-" + unformatedPhone.subSequence(3, 7);
            } else {
                return unformatedPhone;
            }
        }

        @Override
        public String parse(String string) throws ParseException {
            if (CommonsStringUtils.isEmpty(string)) {
                return null; // empty value case
            }
            if (!string.matches(regex)) {
                throw new ParseException("PhoneFormat", 0);
            }
            return format(string);
        }
    }

    public static class PhoneSearchFormat implements IFormat<String> {

        @Override
        public String format(String value) {
            return value;
        }

        @Override
        public String parse(String string) throws ParseException {
            if (CommonsStringUtils.isEmpty(string)) {
                return null; // empty value case
            }
            if (string.contains("*")) {
                return string;
            } else if (string.matches(".*[^0-9\\s\\(\\)-]+.*")) {
                throw new ParseException("PhoneSearchFormat", 0);
            }
            String unformatedPhone = PhoneFormat.normalize(string);
            if (unformatedPhone.length() == 10) {
                return unformatedPhone.subSequence(0, 3) + "-" + unformatedPhone.subSequence(3, 6) + "-" + unformatedPhone.subSequence(6, 10);
            } else if (string.length() != unformatedPhone.length()) {
                // Contains some user formating.
                return string;
            } else if (unformatedPhone.length() == 7) {
                return unformatedPhone.subSequence(0, 3) + "-" + unformatedPhone.subSequence(3, 7);
            } else {
                return string;
            }
        }
    }
}