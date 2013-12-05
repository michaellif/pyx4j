/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Dec 2, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.widgets.client.ITextWidget;

public class NSignature extends NTextFieldBase<Boolean, SignaturePanel, CSignature> {

    public NSignature(CSignature cComponent) {
        super(cComponent);
    }

    @Override
    protected SignaturePanel createEditor() {
        return new SignaturePanel();
    }

}

class SignaturePanel extends FlowPanel implements ITextWidget {

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
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
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
    public int getTabIndex() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setAccessKey(char key) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFocus(boolean focused) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTabIndex(int index) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getText() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setText(String text) {
        // TODO Auto-generated method stub

    }

    @Override
    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

}
