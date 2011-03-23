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

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HTML;

public class NativeLabel<E> extends HTML implements INativeReference<E> {

    private final CAbstractLabel<E> cComponent;

    private final NativeReferenceDelegate<E> delegate;

    public NativeLabel(CAbstractLabel<E> cComponent) {
        this.cComponent = cComponent;
        delegate = new NativeReferenceDelegate<E>(this);
    }

    @Override
    public CAbstractLabel<E> getCComponent() {
        return cComponent;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
        // do nothing - actually it's not enabled...
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void setEditable(boolean editable) {
        // do nothing - actually it's not editable...
    }

    @Override
    public void setNativeValue(E value) {
        delegate.setNativeValue(value);
    }

    public void setReadOnly(boolean readOnly) {
        // do nothing - actually it's always read-only ...
    }

    @Override
    public void setFocus(boolean focused) {
    }

    @Override
    public void setTabIndex(int tabIndex) {
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
        // do nothing - actually it's valid always...
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
}
