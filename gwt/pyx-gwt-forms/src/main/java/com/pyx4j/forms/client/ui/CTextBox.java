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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.FocusWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.gwt.NativeTextBox;

public abstract class CTextBox<E> extends CTextComponent<E> {

    INativeTextComponent<E> nativeTextField;

    private IFormat<E> format;

    private boolean isEditing = false;

    public CTextBox(String title) {
        super(title);
        setWidth("100%");
    }

    public CTextBox() {
        this(null);
    }

    public void setFormat(IFormat<E> format) {
        this.format = format;
    }

    public IFormat<E> getFormat() {
        return format;
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void onEditingStart() {
        isEditing = true;
    }

    public void onEditingStop() {
        isEditing = false;
        if (isParsedSuccesfully()) {
            setNativeComponentValue(getValue());
        }
        PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.TOOLTIP_PROPERTY);
    }

    @Override
    public void setValue(E value) {
        if (getValue() == null && value == null) {
            PropertyChangeEvent.fire(this, PropertyChangeEvent.PropertyName.TOOLTIP_PROPERTY);
        } else {
            super.setValue(value);
        }
    }

    @Override
    public INativeEditableComponent<E> getNativeComponent() {
        return nativeTextField;
    }

    @Override
    public INativeEditableComponent<E> initNativeComponent() {
        if (nativeTextField == null) {
            nativeTextField = new NativeTextBox<E>(this);
            applyAccessibilityRules();
        }
        return nativeTextField;
    }

    public void requestFocus() {
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                INativeEditableComponent<E> impl = initNativeComponent();
                if (impl instanceof FocusWidget) {
                    ((FocusWidget) impl).setFocus(true);
                }
            }
        });
    }

    @Override
    public boolean isValueEmpty() {
        if (nativeTextField != null) {
            if (!CommonsStringUtils.isEmpty(nativeTextField.getNativeText())) {
                return false;
            }
        }
        return super.isValueEmpty() || ((getValue() instanceof String) && CommonsStringUtils.isEmpty((String) getValue()));
    }

    public boolean isParsedSuccesfully() {
        if (nativeTextField != null) {
            String text = nativeTextField.getNativeText();
            if (text != null && !text.trim().equals("") && getValue() == null) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

}
