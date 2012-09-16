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

/**
 * This field is used to protect personal identity by hiding part of ID
 */
public class CPersonalIdentityField extends CTextFieldBase<IPersonalIdentity, NTextBox<IPersonalIdentity>> {

    public CPersonalIdentityField() {
        this(null);
    }

    public CPersonalIdentityField(String title) {
        super(title);
        setFormat(new PersonalIdentityFormat());
    }

    public CPersonalIdentityField(String title, boolean mandatory) {
        this(title);
        this.setMandatory(mandatory);
    }

    // Possible formats - 'XXX-XXX-xxx', 'XXXX XXXX XXXX xxxx', 'xxx XXX XXX xxx'
    public void setPersonalIdentityFormat(String pattern) {
        setFormat(new PersonalIdentityFormat(pattern));
    }

    @Override
    protected NTextBox<IPersonalIdentity> createWidget() {
        return new NTextBox<IPersonalIdentity>(this);
    }

    public void addRegexValidator(String regex, String regexValidationMessage) {
        this.addValueValidator(new RegexValidator<IPersonalIdentity>(regex, regexValidationMessage));
    }

    @Override
    public boolean isValueEmpty() {
        return super.isValueEmpty() || CommonsStringUtils.isEmpty(getValue().obfuscatedNumber().getStringView());
    }

    class PersonalIdentityFormat implements IFormat<IPersonalIdentity> {

        public PersonalIdentityFormat() {
        }

        public PersonalIdentityFormat(String pattern) {
        }

        @Override
        public String format(IPersonalIdentity value) {
            if (value == null) {
                return "";
            }
            return value.obfuscatedNumber().getStringView();
        }

        @Override
        public IPersonalIdentity parse(String string) throws ParseException {
            if (!CommonsStringUtils.isEmpty(string)) {
                getValue().newNumber().setValue(string);
                getValue().obfuscatedNumber().setValue(null);
            }
            return getValue();
        }

    }

}
