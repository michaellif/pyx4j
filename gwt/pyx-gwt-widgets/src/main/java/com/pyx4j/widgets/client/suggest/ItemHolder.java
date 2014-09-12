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
 * Created on Sep 11, 2014
 * @author arminea
 * @version $Id$
 */
package com.pyx4j.widgets.client.suggest;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.widgets.client.style.theme.WidgetTheme;

public class ItemHolder<E> extends Composite {

    private final E item;

    private final SelectorListBoxValuePanel<E> parent;

    public ItemHolder(SelectorListBoxValuePanel<E> parent, E item, String label) {
        super();

        this.item = item;

        this.parent = parent;
        FlowPanel panel = new FlowPanel();
        panel.setStyleName(WidgetTheme.StyleName.SelectedItemHolder.name());
        Label lbl = new Label(label);
        lbl.getElement().getStyle().setDisplay(Display.INLINE);
        lbl.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                event.stopPropagation();
                setFocus(true);
            }
        });
        panel.add(lbl);

        Label deleteItemAction = new Label("\u2716");
        deleteItemAction.setTitle("Remove");
        deleteItemAction.setStyleName(WidgetTheme.StyleName.SelectedItemClose.name());
        deleteItemAction.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                event.stopPropagation();
                remove();
            }
        });
        panel.add(deleteItemAction);
        this.initWidget(panel);
    }

    //}

    public E getItem() {
        return this.item;
    }

    public void remove() {
        parent.removeItem(this.item);
    }

    public void setFocus(boolean focused) {
        parent.setFocus(focused);
    }

}
