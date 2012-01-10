/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Jan 10, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.widgets.client.TextBox;

public class _NTextBox<E> extends _NViewableComponent<TextBox, CTextFieldBase<E, ?>> implements INativeTextComponent<E> {

    public _NTextBox(CTextFieldBase<E, ?> cComponent) {
        super(cComponent);
    }

    @Override
    protected TextBox createEditor() {
        return new TextBox();
    }

    @Override
    public void setTabIndex(int tabIndex) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFocus(boolean focused) {
        // TODO Auto-generated method stub

    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler focusHandler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler blurHandler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setEnabled(boolean enabled) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setEditable(boolean editable) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isEditable() {
        // TODO Auto-generated method stub
        return false;
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

    @Override
    public void setNativeValue(E value) {
        // TODO Auto-generated method stub

    }

    @Override
    public E getNativeValue() throws ParseException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onPropertyChange(PropertyChangeEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setNativeText(String newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getNativeText() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

}