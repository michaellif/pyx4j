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

import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.widgets.client.TextBox;

@Deprecated
/**
 * 
 * @deprecated Use {@link _NTextBox}
 */
public class NativeTextBox<E> extends TextBox implements INativeTextComponent<E> {

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
    public void setNativeText(String value) {
        super.setText(value);
    }

    @Override
    public void setNativeValue(E value) {
        delegate.setNativeValue(value);
    }

    @Override
    public E getNativeValue() throws ParseException {
        return delegate.getNativeValue();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.disabled.name());
        } else {
            addStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.disabled.name());
        }
    }

    @Override
    public void setEditable(boolean editable) {
        super.setReadOnly(!editable);
        if (editable) {
            removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.readonly.name());
        } else {
            addStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.readonly.name());
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
    public void onPropertyChange(PropertyChangeEvent event) {
        if (event.isEventOfType(PropertyName.repopulated)) {
            removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
        } else if (event.isEventOfType(PropertyName.valid, PropertyName.visited)) {
            if (delegate.getCComponent().isValid()) {
                removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
            } else if (delegate.getCComponent().isVisited()) {
                addStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
            }
        }
    }

    @Override
    public void setViewable(boolean editable) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isViewable() {
        // TODO Auto-generated method stub
        return false;
    }
}