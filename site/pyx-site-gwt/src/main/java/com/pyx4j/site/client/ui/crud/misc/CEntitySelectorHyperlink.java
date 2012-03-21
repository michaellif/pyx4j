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
 * Created on Sep 14, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.site.client.ui.crud.misc;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.INativeTextComponent;
import com.pyx4j.forms.client.ui.NFocusComponent;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.lister.EntitySelectorDialog;
import com.pyx4j.site.client.ui.crud.misc.CEntitySelectorHyperlink.NEntitySelectorHyperlink;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.TextBox;

public abstract class CEntitySelectorHyperlink<E extends IEntity> extends CTextFieldBase<E, NEntitySelectorHyperlink<E>> {
    private final CrudAppPlace place;

    private final Class<E> entityClass;

    public CEntitySelectorHyperlink(Class<E> entityClass, CrudAppPlace place) {
        this.entityClass = entityClass;
        this.place = place;
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

    public CrudAppPlace getPlace() {
        return place;
    }

    @Override
    protected NEntitySelectorHyperlink<E> createWidget() {
        return new NEntitySelectorHyperlink<E>(this);
    }

    static class NEntitySelectorHyperlink<E extends IEntity> extends NFocusComponent<E, TextBox, CEntitySelectorHyperlink<E>, Anchor> implements
            INativeTextComponent<E> {

        public NEntitySelectorHyperlink(CEntitySelectorHyperlink<E> cComponent) {
            super(cComponent, ImageFactory.getImages().arrowLightBlueRight());
        }

        @Override
        public Anchor createViewer() {
            Anchor anchor = new Anchor("");
            anchor.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    E value = getNativeValue();
                    if (!value.id().isNull() && getCComponent().getPlace() != null) {
                        AppSite.getPlaceController().goTo(getCComponent().getPlace().formViewerPlace(value.getPrimaryKey()));
                    }
                }
            });
            return anchor;
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
        public void setNativeText(String newValue) {
            // does nothing; use native value setter instead
        }

        @Override
        public String getNativeText() {
            E value = getNativeValue();
            return (value == null ? "" : value.getStringView());
        }

        @Override
        public HandlerRegistration addChangeHandler(ChangeHandler handler) {
            return getEditor().addChangeHandler(handler);
        }

        @Override
        protected TextBox createEditor() {
            TextBox editor = new TextBox();
            editor.setEditable(false);
            return editor;
        }

        @Override
        public void setEditable(boolean editable) {
            Button triggerButton = getTriggerButton();
            if (triggerButton != null) {
                triggerButton.setEnabled(editable);
            }
        }

        @Override
        public void onToggle() {
            getCComponent().getSelectorDialog().show();
        }
    }

    public abstract EntitySelectorDialog<E> getSelectorDialog();
}
