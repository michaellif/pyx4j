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
package com.pyx4j.forms.client.gwt;

import com.google.gwt.user.client.ui.TextBox;

import com.pyx4j.forms.client.ui.CTextBox;
import com.pyx4j.forms.client.ui.INativeTextComponent;

public class NativeTextBox<E> extends TextBox implements INativeTextComponent<E> {

    private final NativeTextBoxDelegate<E> delegate;

    public NativeTextBox(final CTextBox<E> cTextField) {
        super();
        delegate = new NativeTextBoxDelegate<E>(this, cTextField);
    }

    public String getNativeText() {
        if (delegate != null) {
            delegate.cancelScheduledUpdate();
        }
        return super.getText();
    }

    public void setNativeText(String newValue) {
        super.setText(newValue);
    }

    public void setNativeValue(E value) {
        delegate.setValue(value);
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

    public CTextBox<E> getCComponent() {
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

}