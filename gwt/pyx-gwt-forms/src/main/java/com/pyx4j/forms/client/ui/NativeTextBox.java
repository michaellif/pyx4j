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

import com.pyx4j.widgets.client.TextBox;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.Selector;

public class NativeTextBox<E> extends TextBox implements INativeTextComponent<E> {

    public static enum StyleDependent implements IStyleDependent {
        disabled, readOnly, invalid
    }

    private final NativeTextBoxDelegate<E> delegate;

    public NativeTextBox(final CTextFieldBase<E, ?> cTextField) {
        super();
        delegate = new NativeTextBoxDelegate<E>(this, cTextField);
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
    public void setNativeValue(E value) {
        delegate.setNativeValue(value);
    }

    @Override
    public E getNativeValue() {
        return delegate.getNativeValue();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        String dependentSuffix = Selector.getDependentName(StyleDependent.disabled);
        if (enabled) {
            removeStyleDependentName(dependentSuffix);
        } else {
            addStyleDependentName(dependentSuffix);
        }
    }

    @Override
    public void setEditable(boolean editable) {
        super.setReadOnly(!editable);
        String dependentSuffix = Selector.getDependentName(StyleDependent.readOnly);
        if (editable) {
            removeStyleDependentName(dependentSuffix);
        } else {
            addStyleDependentName(dependentSuffix);
        }

    }

    @Override
    public boolean isEditable() {
        return !super.isReadOnly();
    }

    @Override
    public CTextFieldBase<E, ?> getCComponent() {
        return delegate.getCComponent();
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        DomDebug.attachedWidget();
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        DomDebug.detachWidget();
    }

    @Override
    public void setValid(boolean valid) {
        String dependentSuffix = Selector.getDependentName(StyleDependent.invalid);
        if (valid) {
            removeStyleDependentName(dependentSuffix);
        } else {
            addStyleDependentName(dependentSuffix);
        }
    }

    @Override
    public void installStyles(String stylePrefix) {
        // TODO Auto-generated method stub

    }
}