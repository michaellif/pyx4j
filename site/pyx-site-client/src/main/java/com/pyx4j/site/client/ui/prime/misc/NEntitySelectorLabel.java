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
 * Created on Mar 30, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.prime.misc;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.forms.client.ui.INativeTextComponent;
import com.pyx4j.forms.client.ui.NFocusField;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.TextBox;

class NEntitySelectorLabel<E extends IEntity> extends NFocusField<E, TextBox, CEntitySelectorLabel<E>, HTML> implements INativeTextComponent<E> {

    public NEntitySelectorLabel(CEntitySelectorLabel<E> cComponent) {
        super(cComponent);

        Button triggerButton = new Button(ImageFactory.getImages().triggerDown());
        triggerButton.setCommand(new Command() {

            @Override
            public void execute() {
                getCComponent().getSelectorDialog().show();

            }
        });

        setTriggerButton(triggerButton);
    }

    @Override
    protected TextBox createEditor() {
        TextBox editor = new TextBox() {
            {
                super.setEditable(false); // edit box is always non-editable! 
            }

            // but  - emulate state change for parent logic:
            private boolean editable = super.isEditable();

            @Override
            public void setEditable(boolean editable) {
                this.editable = editable;
            }

            @Override
            public boolean isEditable() {
                return editable;
            }
        };
        return editor;
    }

    @Override
    protected void onEditorCreate() {
        getEditor().setWidth("100%");
        super.onEditorCreate();
    }

    @Override
    public HTML createViewer() {
        return new HTML();
    }

    @Override
    protected void onViewerCreate() {
        getViewer().setWidth("100%");
        super.onViewerCreate();
    }

    @Override
    public void setNativeValue(E value) {
        String nValue = (value == null ? "" : value.getStringView());
        if (isViewable()) {
            getViewer().setText(nValue);
        } else {
            getEditor().setText(nValue);
        }
    }

    @Override
    public E getNativeValue() {
        return getCComponent().getValue();
    }

    @Override
    public String getNativeText() {
        E value = getNativeValue();
        return (value == null ? "" : value.getStringView());
    }

}