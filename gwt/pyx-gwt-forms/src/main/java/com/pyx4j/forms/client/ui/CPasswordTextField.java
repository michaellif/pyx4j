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

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.gwt.NativePasswordTextBox;
import com.pyx4j.forms.client.gwt.NativeTextBox;
import com.pyx4j.forms.client.ui.CTextField.StringFormat;
import com.pyx4j.forms.client.validators.RegexValidator;

public class CPasswordTextField extends CTextFieldBase<String, NativePasswordTextBox> {

    public CPasswordTextField() {
        super();
    }

    public CPasswordTextField(String title) {
        super(title);
    }

    public CPasswordTextField(String title, boolean mandatory) {
        this(title);
        this.setMandatory(mandatory);
    }

    @Override
    public NativePasswordTextBox initWidget() {
        NativePasswordTextBox nativeTextField = new NativePasswordTextBox(this);
        applyAccessibilityRules();
        return nativeTextField;
    }

    @Override
    public boolean isValueEmpty() {
        return super.isValueEmpty() || CommonsStringUtils.isEmpty(getValue());
    }

}
