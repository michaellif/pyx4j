/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Jun 15, 2015
 * @author vlads
 */
package com.pyx4j.commons.formatters;

import com.pyx4j.commons.IFormatter;

public class PhoneFormatter implements IFormatter<String, String> {

    protected final PhoneType phoneType;

    public PhoneFormatter(PhoneType phoneType) {
        this.phoneType = phoneType;
    }

    @Override
    public String format(String value) {
        if (value == null) {
            return null;
        }
        String unformatedPhone = normalize(value);
        if (unformatedPhone.length() == 11) {
            // Remove 1 in front
            unformatedPhone = unformatedPhone.substring(1);
            return "(" + unformatedPhone.subSequence(0, 3) + ") " + unformatedPhone.subSequence(3, 6) + "-" + unformatedPhone.subSequence(6, 10);
        } else if (unformatedPhone.length() == 10) {
            return "(" + unformatedPhone.subSequence(0, 3) + ") " + unformatedPhone.subSequence(3, 6) + "-" + unformatedPhone.subSequence(6, 10);
        } else if (unformatedPhone.length() > 10) {
            return "(" + unformatedPhone.subSequence(0, 3) + ") " + unformatedPhone.subSequence(3, 6) + "-" + unformatedPhone.subSequence(6, 10) + " "
                    + unformatedPhone.subSequence(10, unformatedPhone.length());
        } else {
            return unformatedPhone;
        }
    }

    protected String normalize(String value) {
        if (value == null) {
            return null;
        } else {
            return value.replaceAll("[\\s\\(\\)-]+", "");
        }
    }
}