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
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IPersonalIdentity;
import com.pyx4j.forms.client.validators.RegexValidator;
import com.pyx4j.forms.client.validators.TextBoxParserValidator;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
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
// TODO remove when watermark fixed        super.setWatermark(pattern.toUpperCase());
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

    @Override
    public void onEditingStop() {
        if (!getWidget().getEditor().getText().isEmpty() && getValue() != null) {
            // clear obfuscated value to indicate user input
            getValue().obfuscatedNumber().setValue(null);
        }
        super.onEditingStop();
    }

    public void postprocess() {
        IPersonalIdentity value = getValue();
        if (value != null && !value.newNumber().isNull()) {
            value.obfuscatedNumber().setValue(((PersonalIdentityFormat) getFormat()).obfuscate(value.newNumber().getValue()));
            value.newNumber().setValue(null);
        }
    }

    class PersonalIdentityFormat implements IFormat<IPersonalIdentity> {

        private final char FORMAT_CLEAR = 'x';

        private final char FORMAT_HIDDEN = 'X';

        private final String FORMAT_DELIM = ";";

        private final String[] patternArr;

        private final int[] dataLengthArr;

        private int patternIdx = -1;

        public PersonalIdentityFormat(String pattern) {
            // pattern is interpreted as follows:
            //   X - input character in this position will be translated to 'X' (hidden data)
            //   x - input character in this position will not be modified (open data)
            //   no other alphanumeric chars is allowed
            //   any non-alphanumeric chars will be treated as decorators and will not be modified
            // multiple applicable formats can be specified using ';' as a delimiter
            if (!isPatternValid(pattern)) {
                throw new UnrecoverableClientError("Invalid identity format: " + pattern);
            }
            patternArr = pattern.split(FORMAT_DELIM);
            dataLengthArr = new int[patternArr.length];
            for (int idx = 0; idx < patternArr.length; idx++) {
                String pat = patternArr[idx];
                for (int pos = 0; pos < pat.length(); pos++) {
                    char c = pat.charAt(pos);
                    if (c == FORMAT_CLEAR || c == FORMAT_HIDDEN) {
                        dataLengthArr[idx] += 1;
                    }
                }
            }
        }

        @Override
        public String format(IPersonalIdentity value) {
            if (value == null) {
                return "";
            }
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
                // not empty string could be either new user input or obfuscated value (formatted) of existing entity
                String data = dataFilter(string);
                if (getPatternIdx(data) == -1) {
                    throw new ParseException(i18n.tr("Identity value is invalid for the given format."), 0);
                }
                // check if we are parsing user input or obfuscated value
                boolean userInput = (value == null || value.obfuscatedNumber().isNull());
                // populate resulting value
                if (value == null) {
                    value = EntityFactory.create(IPersonalIdentity.class);
                }
                if (userInput) {
                    // if no obfuscated value then we are getting new user input
                    value.newNumber().setValue(data);
                    value.obfuscatedNumber().setValue(null);
                } else {
                    value.newNumber().setValue(null);
                    value.obfuscatedNumber().setValue(data);
                }
                return value;
            }
        }

        private int getPatternIdx(String data) {
            int dataLength = data.length();
            patternIdx = -1;
            for (int idx = 0; idx < patternArr.length; idx++) {
                if (dataLength == dataLengthArr[idx]) {
                    patternIdx = idx;
                    break;
                }
            }
            return patternIdx;
        }

        private String format(String input, boolean clearText) {
            if (input == null) {
                return "";
            }
            String data = dataFilter(input);

            // search for pattern based on user input
            if (getPatternIdx(data) == -1) {
                return "";
            }

            String pattern = patternArr[patternIdx];
            StringBuilder output = new StringBuilder();
            for (int pos = 0, dataPos = 0; pos < pattern.length(); pos++) {
                char c = pattern.charAt(pos);
                if (c == FORMAT_CLEAR) {
                    output.append(data.charAt(dataPos++));
                } else if (c == FORMAT_HIDDEN) {
                    output.append(clearText ? data.charAt(dataPos) : FORMAT_HIDDEN);
                    dataPos++;
                } else {
                    output.append(c);
                }
            }
            return output.toString();
        }

        protected String obfuscate(String data) {
            if (patternIdx == -1) {
                return "";
            }
            String pattern = patternArr[patternIdx];
            StringBuilder output = new StringBuilder();
            for (int pos = 0, dataPos = 0; pos < pattern.length(); pos++) {
                char c = pattern.charAt(pos);
                if (c == FORMAT_CLEAR) {
                    output.append(data.charAt(dataPos++));
                } else if (c == FORMAT_HIDDEN) {
                    output.append(FORMAT_HIDDEN);
                    dataPos++;
                }
            }
            return output.toString();
        }

        protected String dataFilter(String input) {
            // only alphanumeric chars allowed in user input
            return input.replaceAll("[\\W_]", "");
        }

        protected boolean isPatternValid(String pattern) {
            // check for not allowed chars in the formatting pattern
            return pattern.matches("^[\\W_" + FORMAT_CLEAR + FORMAT_HIDDEN + "]*$");
        }
    }

}
