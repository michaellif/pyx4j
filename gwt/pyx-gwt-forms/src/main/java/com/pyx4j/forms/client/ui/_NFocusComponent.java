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

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FocusWidget;

public abstract class _NFocusComponent<DATA, WIDGET extends FocusWidget, CCOMP extends CFocusComponent<DATA, ?>> extends _NComponent<DATA, WIDGET, CCOMP>
        implements INativeFocusComponent<DATA> {

    public _NFocusComponent(CCOMP cComponent) {
        this(cComponent, null);
    }

    public _NFocusComponent(CCOMP cComponent, ImageResource triggerImage) {
        super(cComponent, triggerImage);

    }

    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
        getEditor().setTabIndex(getCComponent().getTabIndex());

        getEditor().addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                getCComponent().onEditingStart();
            }
        });

        getEditor().addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                getCComponent().onEditingStop();
            }
        });
    }

    @Override
    public void setTabIndex(int index) {
        getEditor().setTabIndex(index);
    }

    @Override
    public int getTabIndex() {
        return getEditor().getTabIndex();
    }

    @Override
    public void setFocus(boolean focused) {
        getEditor().setFocus(focused);
    }

    @Override
    public void setAccessKey(char key) {
        getEditor().setAccessKey(key);
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return getEditor().addFocusHandler(handler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return getEditor().addBlurHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return getEditor().addKeyDownHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return getEditor().addKeyUpHandler(handler);
    }

    @Override
    public void setEnabled(boolean enabled) {
        getTriggerButton().setEnabled(enabled);
        getEditor().setEnabled(enabled);
        if (enabled) {
            getEditor().removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.disabled.name());
        } else {
            getEditor().addStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.disabled.name());
        }
    }

    @Override
    public boolean isEnabled() {
        if (isViewable()) {
            assert false : "isEnabled shouldn't be called in viewable mode";
        }
        return getEditor().isEnabled();
    }

}
