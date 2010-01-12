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

import com.pyx4j.forms.client.validators.TextBoxParserValidator;

public class CPhoneField extends CTextBox<String> {

    public CPhoneField() {
        this(null, "617");
    }

    public CPhoneField(String title) {
        this(title, "617");
    }

    public CPhoneField(String title, String defaultAreaCode) {
        super(title);
        setFormat(new PhoneFormat(defaultAreaCode));
        addValueValidator(new TextBoxParserValidator<String>("Not a valid phone number. Must be in the format 123-4567 or 123-456-7890 (dashes optional)"));
    }

    public static class PhoneFormat implements IFormat<String> {

        private final String defaultAreaCode;

        private final static String regex = "^(\\(?\\d{3}\\)?\\s?[\\s-]?){1,2}(\\d{4})$";

        public PhoneFormat(String defaultAreaCode) {
            this.defaultAreaCode = defaultAreaCode;
        }

        public PhoneFormat() {
            this(null);
        }

        public static String normalize(String value) {
            if (value == null) {
                return null;
            } else {
                return value.replaceAll("[\\s\\(\\)-]+", "");
            }
        }

        public String format(String value) {
            if (value == null) {
                return null;
            }
            String unformatedPhone = normalize(value);
            if (unformatedPhone.length() == 7 && defaultAreaCode != null) {
                unformatedPhone = defaultAreaCode + unformatedPhone;
            }
            if (unformatedPhone.length() == 10) {
                return unformatedPhone.subSequence(0, 3) + "-" + unformatedPhone.subSequence(3, 6) + "-" + unformatedPhone.subSequence(6, 10);
            } else {
                return unformatedPhone;
            }
        }

        public String parse(String string) {
            if (string == null || !string.matches(regex)) {
                return null;
            }
            return format(string).replaceAll("-", "");
        }

    }
}