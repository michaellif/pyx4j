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
 */
package com.pyx4j.site.client.backoffice.ui.prime;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.forms.client.ui.NFocusField;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.StringBox;
import com.pyx4j.widgets.client.TextBox;

class NEntitySelectorHyperlink<E extends IEntity> extends NFocusField<E, TextBox, CEntitySelectorHyperlink<E>, Anchor> {

    public NEntitySelectorHyperlink(CEntitySelectorHyperlink<E> cComponent) {
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
    protected StringBox createEditor() {
        StringBox editor = new StringBox() {
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
    public Anchor createViewer() {
        Anchor anchor = new Anchor("");
        anchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                AppPlace place = getCComponent().getTargetPlace();
                if (place != null) {
                    AppSite.getPlaceController().goTo(place);
                }
            }
        });
        return anchor;
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
            getEditor().setValue(nValue);
        }
    }

    @Override
    public E getNativeValue() {
        return getCComponent().getValue();
    }

}