/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2013-12-05
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.widgets.superselector;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.i18n.shared.I18n;

public class SelectedItemHolder<C> extends Composite {

    private static final I18n i18n = I18n.get(SelectedItemHolder.class);

    private final SuperSelector<C> parent;

    private final C item;

    public enum Styles implements IStyleName {

        SuperSelectedItemHolder
    }

    public SelectedItemHolder(IFormat<C> format, SuperSelector<C> parent, C item) {
        this.parent = parent;
        this.item = item;

        FlowPanel panel = new FlowPanel();
        panel.setStyleName(Styles.SuperSelectedItemHolder.name());

        Label itemLabel = new Label(format.format(item));
        itemLabel.getElement().getStyle().setDisplay(Display.INLINE);
        panel.add(itemLabel);

        Label deleteItemAction = new Label("\u2716"); // 'heavy multiplication' symbol
        deleteItemAction.setTitle(i18n.tr("Remove"));
        deleteItemAction.getElement().getStyle().setDisplay(Display.INLINE);
        deleteItemAction.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        deleteItemAction.getElement().getStyle().setPaddingLeft(3, Unit.PX);
        deleteItemAction.getElement().getStyle().setPaddingRight(3, Unit.PX);
        deleteItemAction.getElement().getStyle().setCursor(Cursor.POINTER);
        deleteItemAction.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                event.stopPropagation();
                remove();
            }
        });
        panel.add(deleteItemAction);
        initWidget(panel);

    }

    public C getItem() {
        return item;
    }

    private void remove() {
        this.parent.removeItem(item);
    }

}
