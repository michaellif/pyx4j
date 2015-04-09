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

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.validators.RegexValidator;

public class CTextField extends CTextFieldBase<String, NTextBox<String>> {

    public CTextField(boolean mandatory) {
        this();
        this.setMandatory(mandatory);
    }

    public CTextField() {
        super();
        setFormatter(new StringFormat());
        setParser(new StringParser());
        setNativeComponent(new NTextBox<String>(this));
    }

    public void addRegexValidator(String regex, String regexValidationMessage) {
        this.addComponentValidator(new RegexValidator<String>(regex, regexValidationMessage));
    }

    @Override
    public boolean isValueEmpty() {
        return super.isValueEmpty() || CommonsStringUtils.isEmpty(getValue());
    }

}
