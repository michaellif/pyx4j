/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Dec 9, 2014
 * @author michaellif
 */
package com.pyx4j.widgets.client.selector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public abstract class EditableItemHolder<E> extends ItemHolder<E> {

    private final SelectorListBoxValuePanel<E> parent;

    private HandlerRegistration popupCloseHandlerRegistration;

    public EditableItemHolder(E item, IFormatter<E, String> valueFormatter, boolean removable, SelectorListBoxValuePanel<E> parent) {
        super(item, valueFormatter, removable);
        this.parent = parent;
        addDomHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                showEditor();
            }
        }, ClickEvent.getType());
        addStyleDependentName(WidgetsTheme.StyleDependent.editable.name());
    }

    protected void showEditor() {
        addStyleDependentName(WidgetsTheme.StyleDependent.editing.name());
        parent.getItemEditorPopup().hide();
        parent.getItemEditorPopup().show(EditableItemHolder.this);
        parent.getItemEditorPopup().setFocus(true);
        popupCloseHandlerRegistration = parent.getItemEditorPopup().addCloseHandler(new CloseHandler<PopupPanel>() {

            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                onEditorHidden();
            }
        });
        onEditorShown();
    }

    protected void onEditorShown() {
    }

    protected void onEditorHidden() {
        popupCloseHandlerRegistration.removeHandler();
        removeStyleDependentName(WidgetsTheme.StyleDependent.editing.name());
    }

    /**
     *
     * @return true if item was edited successfully
     */
    protected boolean onEditingComplete() {
        setLabel(getItem());
        ValueChangeEvent.fire(parent.getSelectorListBox(), parent.getSelectorListBox().getValue());
        return true;
    }

    @Override
    protected void onRemove() {
        super.onRemove();
        parent.getItemEditorPopup().hide();
    }

    public abstract boolean isEditorShownOnAttach();

    public abstract IsWidget getEditor();

}
