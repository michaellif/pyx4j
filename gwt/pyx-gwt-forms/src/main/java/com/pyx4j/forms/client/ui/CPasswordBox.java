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
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.forms.client.validators.RegexValidator;
import com.pyx4j.widgets.client.NotImplementedException;
import com.pyx4j.widgets.client.PasswordBox.PasswordStrengthRule;

public class CPasswordBox extends CValueBoxBase<String, NPasswordBox> {

    private boolean unmasked;

    private PasswordStrengthRule passwordStrengthRule;

    public CPasswordBox() {
        super();
        super.setFormatter(new IFormatter<String, String>() {

            @Override
            public String format(String value) {
                if (value == null) {
                    value = "";
                }
                return value;
            }
        });

        super.setParser(new IParser<String>() {

            @Override
            public String parse(String string) throws ParseException {
                if (CommonsStringUtils.isEmpty(string)) {
                    return null;
                }
                return string;
            }

        });
        setNativeComponent(new NPasswordBox(this));
    }

    public void addRegexValidator(String regex, String regexValidationMessage) {
        this.addComponentValidator(new RegexValidator<String>(regex, regexValidationMessage));
    }

    @Override
    public boolean isValueEmpty() {
        return super.isValueEmpty() || CommonsStringUtils.isEmpty(getValue());
    }

    public boolean isUnmasked() {
        return unmasked;
    }

    public void setUnmasked(boolean unmasked) {
        this.unmasked = unmasked;
        if (getNativeComponent().getEditor() != null) {
            getNativeComponent().getEditor().revealText(unmasked);
        }
    }

    public PasswordStrengthRule getPasswordStrengthRule() {
        return passwordStrengthRule;
    }

    public void setPasswordStrengthRule(PasswordStrengthRule rule) {
        this.passwordStrengthRule = rule;
        if (getNativeComponent().getEditor() != null) {
            getNativeComponent().getEditor().setPasswordStrengthRule(passwordStrengthRule);
        }
    }

    @Override
    public void setFormatter(IFormatter<String, String> formatter) {
        throw new NotImplementedException();
    }

    @Override
    public void setParser(IParser<String> parser) {
        throw new NotImplementedException();
    }

}
