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
import com.pyx4j.forms.client.validators.RegexValidator;

public class CTextField extends CTextFieldBase<String, NativeTextBox<String>> {

    public CTextField() {
        this(null);
    }

    public CTextField(String title) {
        super(title);
        setFormat(new StringFormat());

    }

    public CTextField(String title, boolean mandatory) {
        this(title);
        this.setMandatory(mandatory);
    }

    @Override
    protected NativeTextBox<String> createWidget() {
        return new NativeTextBox<String>(this);
    }

    public void addRegexValidator(String regex, String regexValidationMessage) {
        this.addValueValidator(new RegexValidator<String>(regex, regexValidationMessage));
    }

    @Override
    public boolean isValueEmpty() {
        return super.isValueEmpty() || CommonsStringUtils.isEmpty(getValue());
    }

    static class StringFormat implements IFormat<String> {

        @Override
        public String format(String value) {
            if (value == null) {
                value = "";
            }
            return value;
        }

        @Override
        public String parse(String string) throws ParseException {
            if (CommonsStringUtils.isEmpty(string)) {
                return null; // empty value case
            }
            return string;
        }

    }

}
