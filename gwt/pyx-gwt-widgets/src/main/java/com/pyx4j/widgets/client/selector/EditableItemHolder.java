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
 * @version $Id$
 */
package com.pyx4j.widgets.client.selector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class EditableItemHolder<E> extends ItemHolder<E> {

    private IsWidget editor;

    private HandlerRegistration clickHandlerRegistration;

    private final SelectorListBoxValuePanel<E> parent;

    public EditableItemHolder(E item, IFormatter<E, String> valueFormatter, boolean removable, SelectorListBoxValuePanel<E> parent) {
        super(item, valueFormatter, removable);
        this.parent = parent;
    }

    public void setEditor(IsWidget editor) {
        if (editor == null && this.editor != null) {
            clickHandlerRegistration.removeHandler();
            clickHandlerRegistration = null;
        } else if (editor != null && this.editor == null) {
            clickHandlerRegistration = addDomHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    System.out.println("CLick");
                    parent.getItemEditorPopup().show(EditableItemHolder.this);
                }
            }, ClickEvent.getType());
            addStyleDependentName(WidgetsTheme.StyleDependent.editable.name());
        }
        this.editor = editor;
    }

    public IsWidget getEditor() {
        return editor;
    }

    protected void onEditingComplete() {

    }

    @Override
    protected void onRemove() {
        super.onRemove();
        parent.getItemEditorPopup().hide();
    }
}
