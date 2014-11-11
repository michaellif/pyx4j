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
package com.pyx4j.widgets.client.selector;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class ItemHolder<E> extends Composite {

    private static final I18n i18n = I18n.get(ItemHolder.class);

    public ItemHolder(final SelectorListBoxValuePanel<E> parent, final E item, String label) {
        super();

        FlowPanel panel = new FlowPanel();
        panel.setStyleName(WidgetsTheme.StyleName.SelectedItemHolder.name());
        Label lbl = new Label(label);
        lbl.getElement().getStyle().setDisplay(Display.INLINE);
        panel.add(lbl);

        Label deleteItemAction = new Label("\u2716");
        deleteItemAction.setTitle(i18n.tr("Remove"));
        deleteItemAction.setStyleName(WidgetsTheme.StyleName.SelectedItemClose.name());
        deleteItemAction.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                parent.removeItem(item);
            }
        });
        panel.add(deleteItemAction);
        this.initWidget(panel);
    }

}
