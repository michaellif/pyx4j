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

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.ValueBoxBase;

public abstract class _NTextComponent<DATA, WIDGET extends ValueBoxBase<?>, CCOMP extends CTextComponent<DATA, ?>> extends
        _NFocusComponent<DATA, WIDGET, CCOMP> implements INativeTextComponent<DATA> {

    public _NTextComponent(CCOMP cComponent) {
        this(cComponent, null);
    }

    public _NTextComponent(CCOMP cComponent, ImageResource triggerImage) {
        super(cComponent, triggerImage);

    }

    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
        getEditor().addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    getCComponent().onEditingStop();
                }
            }
        });

    }

    @Override
    @Deprecated
    public void setNativeText(String newValue) {
        assert false : "setNativeText shouldn't be called";
    }

    @Override
    public String getNativeText() {
        if (!isViewable()) {
            return getEditor().getText();
        } else {
            return null;
        }
    }

    @Override
    public void setEditable(boolean editable) {
        getTriggerButton().setEnabled(editable);
        if (!isViewable()) {
            getEditor().setReadOnly(!editable);
            if (editable) {
                getEditor().removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.readonly.name());
            } else {
                getEditor().addStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.readonly.name());
            }
        }
    }

    @Override
    public boolean isEditable() {
        return !getEditor().isReadOnly();
    }

    @Override
    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        return getEditor().addChangeHandler(handler);
    }

}