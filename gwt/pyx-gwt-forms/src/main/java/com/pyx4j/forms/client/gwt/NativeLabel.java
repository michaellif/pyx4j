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

import com.google.gwt.user.client.ui.Label;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.INativeEditableComponent;

public class NativeLabel extends Label implements INativeEditableComponent<String> {

    private final CLabel cLabel;

    public NativeLabel(CLabel label) {
        this.cLabel = label;
        setWordWrap(cLabel.isWordWrap());
        setWidth(cLabel.getWidth());
    }

    public CComponent<?> getCComponent() {
        return cLabel;
    }

    public boolean isEnabled() {
        return false;
    }

    public void setEnabled(boolean enabled) {
    }

    public void updateLookAndFeel() {
    }

    public boolean isEditable() {
        return false;
    }

    @Override
    public void setEditable(boolean editable) {
    }

    public void setNativeValue(String value) {
        setText(value);
    }

    public void setReadOnly(boolean readOnly) {
    }

    public void setFocus(boolean focused) {
    }

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
}
