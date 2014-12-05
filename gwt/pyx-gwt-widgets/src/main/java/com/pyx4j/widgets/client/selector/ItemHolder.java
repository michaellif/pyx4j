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

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.ImageButton;
import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class ItemHolder<E> extends Composite {

    private static final I18n i18n = I18n.get(ItemHolder.class);

    public ItemHolder(final SelectorListBoxValuePanel<E> parent, final E item, String text) {
        super();

        FlowPanel panel = new FlowPanel();
        panel.setStyleName(WidgetsTheme.StyleName.SelectedItemHolder.name());

        Label label = new Label(text);
        label.setStyleName(WidgetsTheme.StyleName.SelectedItemHolderLabel.name());
        panel.add(label);

        ImageButton removeItemAction = new ImageButton(ImageFactory.getImages().delButton(), new Command() {

            @Override
            public void execute() {
                parent.removeItem(item);
            }

        });

        //Prevent focus grabbing on 'Remove' Button 
        removeItemAction.addMouseDownHandler(new MouseDownHandler() {

            @Override
            public void onMouseDown(MouseDownEvent event) {
                event.preventDefault();
                parent.setFocus(true);
            }
        });

        removeItemAction.setTitle(i18n.tr("Remove"));
        removeItemAction.addStyleName(WidgetsTheme.StyleName.SelectedItemClose.name());
        panel.add(removeItemAction);
        initWidget(panel);
    }
}
