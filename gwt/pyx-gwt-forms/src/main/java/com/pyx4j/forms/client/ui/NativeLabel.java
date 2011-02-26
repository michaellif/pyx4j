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

import com.google.gwt.user.client.ui.HTML;


public class NativeLabel<E> extends HTML implements INativeEditableComponent<E> {

    private final CAbstractLabel<E> cComponent;

    public NativeLabel(CAbstractLabel<E> cComponent) {
        this.cComponent = cComponent;
    }

    @Override
    public CComponent<?> getCComponent() {
        return cComponent;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void setEditable(boolean editable) {
    }

    @Override
    public void setNativeValue(E value) {
        String text = "";
        if (value != null) {
            if (cComponent.getFormat() != null) {
                text = cComponent.getFormat().format(value);
            } else {
                text = value.toString();
            }
        }
        if (cComponent.isAllowHtml()) {
            setHTML(text);
        } else {
            setText(text);
        }
    }

    public void setReadOnly(boolean readOnly) {
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
    }
}
