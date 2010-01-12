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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;

import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.INativeEditableComponent;

public class NativeCheckBox extends CheckBox implements INativeEditableComponent<Boolean> {

    private final CCheckBox checkBox;

    private boolean enabled = true;

    private boolean readOnly = false;

    public NativeCheckBox(final CCheckBox checkBox) {
        super();
        this.checkBox = checkBox;
        this.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                checkBox.setValue(Boolean.valueOf(getValue()));
            }
        });
        setText(checkBox.getTitle());
        setTabIndex(checkBox.getTabIndex());

    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        super.setEnabled(enabled && !this.isReadOnly());
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        super.setEnabled(!readOnly && this.isEnabled());
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setNativeValue(Boolean value) {
        boolean newValue = value == null ? false : value;
        if (newValue != getValue()) {
            setValue(newValue);
        }
    }

    public CCheckBox getCComponent() {
        return checkBox;
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
