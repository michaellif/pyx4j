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

import com.pyx4j.widgets.client.PasswordTextBox;

public class NativePasswordTextBox extends PasswordTextBox implements INativeTextComponent<String> {

    private final NativeTextBoxDelegate<String> delegate;

    public NativePasswordTextBox(final CPasswordTextField cTextField) {
        super();
        delegate = new NativeTextBoxDelegate<String>(this, cTextField);
    }

    @Override
    public String getNativeText() {
        return super.getText();
    }

    @Override
    public void setNativeText(String newValue) {
        super.setText(newValue);
    }

    @Override
    public void setNativeValue(String value) {
        delegate.setNativeValue(value);
    }

    @Override
    public String getNativeValue() throws ParseException {
        return delegate.getNativeValue();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    @Override
    public void setEditable(boolean editable) {
        super.setReadOnly(!editable);
    }

    @Override
    public boolean isEditable() {
        return !super.isReadOnly();
    }

    @Override
    public CTextFieldBase<String, ?> getCComponent() {
        return delegate.getCComponent();
    }

    @Override
    public void setValid(boolean valid) {
        if (valid) {
            removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
        } else if (delegate.getCComponent().isVisited()) {
            addStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
        }
    }

    @Override
    public boolean isParsedSuccesfully() {
        return !delegate.isParseFailed();
    }
}