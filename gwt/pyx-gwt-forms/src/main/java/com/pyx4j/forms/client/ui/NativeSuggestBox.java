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

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;

import com.pyx4j.commons.css.Selector;
import com.pyx4j.forms.client.ui.NativeTextBox.StyleDependent;
import com.pyx4j.widgets.client.SuggestBox;
import com.pyx4j.widgets.client.TextBox;

public class NativeSuggestBox<E> extends SuggestBox implements INativeTextComponent<E> {

    private final NativeTextBoxDelegate<E> delegate;

    private final GroupFocusHandler focusHandlerManager;

    public NativeSuggestBox(final CSuggestBox<E> cSuggestBox) {
        super(new MultiWordSuggestOracle(), new TextBox());

        focusHandlerManager = new GroupFocusHandler(this);

        ((TextBox) getWidget()).addFocusHandler(focusHandlerManager);
        ((TextBox) getWidget()).addBlurHandler(focusHandlerManager);

        setStyleName(TextBox.DEFAULT_STYLE_PREFIX);
        delegate = new NativeTextBoxDelegate<E>(this, cSuggestBox);
        addSelectionHandler(new SelectionHandler() {
            @Override
            public void onSelection(SelectionEvent event) {
                setFocus(true);
                cSuggestBox.onEditingStop();
            }
        });
    }

    @Override
    public void setEditable(boolean editable) {
        ((TextBox) getWidget()).setEnabled(editable);
        ((TextBox) getWidget()).setReadOnly(!editable);
        String dependentSuffix = Selector.getDependentName(StyleDependent.readOnly);
        if (editable) {
            removeStyleDependentName(dependentSuffix);
        } else {
            addStyleDependentName(dependentSuffix);
        }
    }

    @Override
    public boolean isEditable() {
        return !((TextBox) getWidget()).isReadOnly();
    }

    @Override
    public void setEnabled(boolean enabled) {
        ((TextBox) getWidget()).setEnabled(enabled);
        String dependentSuffix = Selector.getDependentName(StyleDependent.disabled);
        if (enabled) {
            removeStyleDependentName(dependentSuffix);
        } else {
            addStyleDependentName(dependentSuffix);
        }
    }

    @Override
    public boolean isEnabled() {
        return ((TextBox) getWidget()).isEnabled();
    }

    public void addItem(String optionName) {
        ((MultiWordSuggestOracle) getSuggestOracle()).add(optionName);
    }

    public void removeAllItems() {
        ((MultiWordSuggestOracle) getSuggestOracle()).clear();
    }

    @Override
    public CTextFieldBase<E, ?> getCComponent() {
        return delegate.getCComponent();
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
    public String getNativeText() {
        return ((TextBox) getWidget()).getText();
    }

    @Override
    public void setNativeText(String newValue) {
        ((TextBox) getWidget()).setText(newValue);
    }

    @Override
    public HandlerRegistration addChangeHandler(final ChangeHandler handler) {
        return addSelectionHandler(new SelectionHandler() {
            @Override
            public void onSelection(SelectionEvent event) {
                handler.onChange(null);
            }
        });
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
    public HandlerRegistration addFocusHandler(FocusHandler focusHandler) {
        return focusHandlerManager.addHandler(FocusEvent.getType(), focusHandler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler blurHandler) {
        return focusHandlerManager.addHandler(BlurEvent.getType(), blurHandler);
    }

    @Override
    public void installStyles(String stylePrefix) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isParsedSuccesfully() {
        return !delegate.isParseFailed();
    }
}
