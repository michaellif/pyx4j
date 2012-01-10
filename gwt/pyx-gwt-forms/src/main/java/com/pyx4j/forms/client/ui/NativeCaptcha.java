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
 * Created on Jan 29, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.commons.Pair;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.widgets.client.CaptchaComposite;

public class NativeCaptcha extends CaptchaComposite implements INativeFocusComponent<Pair<String, String>> {

    public static enum StyleDependent implements IStyleDependent {
        invalid
    }

    private final CCaptcha component;

    public NativeCaptcha(final CCaptcha component) {
        this.component = component;
        addResponseValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                component.onEditingStop();
            }
        });
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public void setEditable(boolean editable) {
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
    public void setNativeValue(Pair<String, String> value) {
        if (value == null) {
            super.createNewChallenge();
        }
    }

    @Override
    public Pair<String, String> getNativeValue() {
        if (ApplicationMode.offlineDevelopment) {
            return new Pair<String, String>("off", super.getValueResponse());
        } else {
            return new Pair<String, String>(super.getValueChallenge(), super.getValueResponse());
        }
    }

    @Override
    public void setTabIndex(int tabIndex) {
    }

    @Override
    public CCaptcha getCComponent() {
        return component;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {

    }

    @Override
    public void onPropertyChange(PropertyChangeEvent event) {
        if (event.isEventOfType(PropertyName.repopulated)) {
            getResponseTextBox().removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
        } else if (event.isEventOfType(PropertyName.valid, PropertyName.visited)) {
            if (component.isValid()) {
                getResponseTextBox().removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
            } else if (component.isVisited()) {
                getResponseTextBox().addStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
            }
        }
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler focusHandler) {
        return null;
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler blurHandler) {
        return null;
    }

}
