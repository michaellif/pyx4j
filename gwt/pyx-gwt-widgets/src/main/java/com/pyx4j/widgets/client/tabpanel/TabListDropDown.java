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
 * Created on Nov 27, 2010
 * @author Misha
 */
package com.pyx4j.widgets.client.tabpanel;

import java.util.List;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.pyx4j.gwt.commons.ui.Label;

import com.pyx4j.widgets.client.DropDownPanel;
import com.pyx4j.widgets.client.tabpanel.TabBar.TabListAction;

public class TabListDropDown extends DropDownPanel {

    private final TabListAction trigger;

    private final FlowPanel itemsPanel;

    private HandlerRegistration resizeHandlerRegistration;

    TabListDropDown(TabListAction trigger) {
        this.trigger = trigger;

        showRelativeTo(trigger);

        setStyleName(TabTheme.StyleName.TabList.name());

        itemsPanel = new FlowPanel();
        setWidget(itemsPanel);

    }

    public void showSelector() {
        itemsPanel.clear();
        List<Tab> allTabs = trigger.getAllTabs();
        for (final Tab tab : allTabs) {
            if (!tab.isTabVisible()) {
                continue;
            }
            final Label item = new Label(tab.getTabTitle(), false);
            item.setStyleName(TabTheme.StyleName.TabListItem.name());

            item.getStyle().setPadding(2, Unit.PX);
            item.getStyle().setPaddingLeft(4, Unit.PX);
            item.getStyle().setPaddingRight(4, Unit.PX);
            if (tab.isTabSelected()) {
                item.getStyle().setFontWeight(FontWeight.BOLD);
            }
            if (!tab.getTabBarItem().isTabExposed() || tab.getTabBarItem().isTabMasked()) {
                item.getStyle().setFontStyle(FontStyle.ITALIC);
            }
            if (tab.isTabEnabled()) {
                item.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        trigger.selectTab(tab);
                        hideSelector();
                    }
                });
                item.getStyle().setCursor(Cursor.POINTER);

                item.addDomHandler(new MouseOverHandler() {
                    @Override
                    public void onMouseOver(MouseOverEvent event) {
                        item.addStyleDependentName(TabTheme.StyleDependent.hover.name());
                    }
                }, MouseOverEvent.getType());

                item.addDomHandler(new MouseOutHandler() {
                    @Override
                    public void onMouseOut(MouseOutEvent event) {
                        item.removeStyleDependentName(TabTheme.StyleDependent.hover.name());
                    }
                }, MouseOutEvent.getType());

            }

            itemsPanel.add(item);
        }

        ResizeHandler resizeHandler = new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                hideSelector();
            }
        };
        resizeHandlerRegistration = Window.addResizeHandler(resizeHandler);

        showRelativeTo(trigger);
    }

    public void hideSelector() {
        hide();
    }

    @Override
    public void hide(boolean autoClosed) {
        if (resizeHandlerRegistration != null) {
            resizeHandlerRegistration.removeHandler();
        }
        super.hide(autoClosed);
    }

}
