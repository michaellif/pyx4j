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
import com.pyx4j.entity.shared.IPersonalIdentity;
import com.pyx4j.forms.client.validators.RegexValidator;
import com.pyx4j.forms.client.validators.TextBoxParserValidator;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.i18n.shared.I18n;

/**
 * This field is used to protect personal identity by hiding part of ID
 */
public class CPersonalIdentityField extends CTextFieldBase<IPersonalIdentity, NPersonalIdentityField> {

    private static final I18n i18n = I18n.get(CPersonalIdentityField.class);

    public CPersonalIdentityField() {
        this(null);
    }

    public CPersonalIdentityField(String title) {
        this(null, title, false);
    }

    public CPersonalIdentityField(String pattern, String title) {
        this(pattern, title, false);
    }

    public CPersonalIdentityField(String pattern, String title, boolean mandatory) {
        super(title);
        setMandatory(mandatory);
        setPersonalIdentityFormat(pattern == null ? "" : pattern);
    }

    // Possible formats - 'XXX-XXX-xxx', 'XXXX XXXX XXXX xxxx', 'xxx XXX XXX xxx'
    public void setPersonalIdentityFormat(String pattern) {
        super.setWatermark(pattern.toUpperCase());
        setFormat(new PersonalIdentityFormat(pattern));
        addValueValidator(new TextBoxParserValidator<IPersonalIdentity>());
    }

    @Override
    public void setWatermark(String watermark) {
        //do nothing, watermark is set by component
    }

    @Override
    protected NPersonalIdentityField createWidget() {
        return new NPersonalIdentityField(this);
    }

    public void addRegexValidator(String regex, String regexValidationMessage) {
        this.addValueValidator(new RegexValidator<IPersonalIdentity>(regex, regexValidationMessage));
    }

    @Override
    public boolean isValueEmpty() {
        return super.isValueEmpty();// || CommonsStringUtils.isEmpty(getValue().obfuscatedNumber().getStringView());
    }

    @Override
    public ValidationResults getValidationResults() {
        return super.getValidationResults();
    }

    public void postprocess() {
        IPersonalIdentity value = getValue();
        if (value != null && !value.newNumber().isNull()) {
            value.obfuscatedNumber().setValue(((PersonalIdentityFormat) getFormat()).obfuscate(value.newNumber().getValue()));
            value.newNumber().setValue(null);
        }
    }

    class PersonalIdentityFormat implements IFormat<IPersonalIdentity> {

        private String pattern;

        private int dataLength;

        public PersonalIdentityFormat() {
        }

        public PersonalIdentityFormat(String pattern) {
            // pattern is interpreted as follows:
            //   X - input character in this position will be translated to 'X' (hidden data)
            //   x - input character in this position will not be modified (open data)
            //   any other chars will be treated as decorators and will not be modified
            this.pattern = pattern;
            for (int pos = 0; pos < pattern.length(); pos++) {
                char c = pattern.charAt(pos);
                if (c == 'x' || c == 'X') {
                    dataLength += 1;
                }
            }
        }

        @Override
        public String format(IPersonalIdentity value) {
            if (value == null) {
                return "";
            }
//            return value.obfuscatedNumber().getStringView();
            boolean clearText = !value.newNumber().isNull();
            String input = clearText ? value.newNumber().getValue() : value.obfuscatedNumber().getValue();
            return format(input, clearText);
        }

        @Override
        public IPersonalIdentity parse(String string) throws ParseException {
            IPersonalIdentity value = getValue();
            if (CommonsStringUtils.isEmpty(string)) {
                // empty input means no change to the model object
                // TODO - need a way to clear value
                return value;
            } else {
                String data = string.replaceAll("[\\W_]", "");
                if (data.length() == dataLength) {
                    if (value != null && data.equals(value.newNumber().getValue())) {
                        // input didn't change - return as is
                        return value;
                    } else {
                        value.newNumber().setValue(data);
                        value.obfuscatedNumber().setValue(null);
                        return value;
                    }
                }
                throw new ParseException(i18n.tr("Identity value is invalid for the given format."), 0);
            }
        }

        private String format(String input, boolean clearText) {
            if (input == null) {
                return "";
            }
            String data = input.replaceAll("[\\W_]", "");
            if (data.length() != dataLength) {
                return "";
            }
            StringBuilder output = new StringBuilder();
            for (int pos = 0, dataPos = 0; pos < pattern.length(); pos++) {
                char c = pattern.charAt(pos);
                if (c == 'x') {
                    output.append(data.charAt(dataPos++));
                } else if (c == 'X') {
                    output.append(clearText ? data.charAt(dataPos) : 'X');
                    dataPos++;
                } else {
                    output.append(c);
                }
            }
            return output.toString();
        }

        protected String obfuscate(String data) {
            if (data.length() != dataLength) {
                return "";
            }
            StringBuilder output = new StringBuilder();
            for (int pos = 0, dataPos = 0; pos < pattern.length(); pos++) {
                char c = pattern.charAt(pos);
                if (c == 'x') {
                    output.append(data.charAt(dataPos++));
                } else if (c == 'X') {
                    output.append('X');
                    dataPos++;
                }
            }
            return output.toString();
        }
    }

}
