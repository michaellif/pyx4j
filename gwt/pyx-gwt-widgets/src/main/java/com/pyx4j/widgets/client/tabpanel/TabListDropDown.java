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
 * @version $Id: code-templates.xml 4670 2010-01-10 07:33:42Z vlads $
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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

import com.pyx4j.widgets.client.style.Selector;
import com.pyx4j.widgets.client.tabpanel.TabBar.TabListTrigger;

public class TabListDropDown extends PopupPanel {

    private final TabListTrigger trigger;

    private final FlowPanel itemsPanel;

    private String stylePrefix;

    private HandlerRegistration resizeHandlerRegistration;

    TabListDropDown(TabListTrigger trigger) {
        super(true);
        this.trigger = trigger;

        itemsPanel = new FlowPanel();
        setWidget(itemsPanel);
    }

    public void setStylePrefix(String stylePrefix) {
        this.stylePrefix = stylePrefix;
        setStyleName(Selector.getStyleName(stylePrefix, TabPanel.StyleSuffix.List));
    }

    public void showSelector() {
        itemsPanel.clear();
        List<Tab> allTabs = trigger.getAllTabs();
        for (final Tab tab : allTabs) {
            final Label item = new Label(tab.getTabTitle(), false);
            item.setStyleName(Selector.getStyleName(stylePrefix, TabPanel.StyleSuffix.ListItem));

            item.addDomHandler(new MouseOverHandler() {
                @Override
                public void onMouseOver(MouseOverEvent event) {
                    String dependentSuffix = Selector.getDependentSuffix(TabPanel.StyleDependent.hover);
                    item.addStyleDependentName(dependentSuffix);
                }
            }, MouseOverEvent.getType());

            item.addDomHandler(new MouseOutHandler() {
                @Override
                public void onMouseOut(MouseOutEvent event) {
                    String dependentSuffix = Selector.getDependentSuffix(TabPanel.StyleDependent.hover);
                    item.removeStyleDependentName(dependentSuffix);
                }
            }, MouseOutEvent.getType());

            item.getElement().getStyle().setPadding(2, Unit.PX);
            item.getElement().getStyle().setPaddingLeft(4, Unit.PX);
            item.getElement().getStyle().setPaddingRight(4, Unit.PX);
            if (tab.isSelected()) {
                item.getElement().getStyle().setFontWeight(FontWeight.BOLD);
            }
            if (!tab.isTabVisible()) {
                item.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
            }
            item.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    trigger.selectTab(tab);
                    hideSelector();
                }
            });
            item.getElement().getStyle().setCursor(Cursor.POINTER);

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
        resizeHandlerRegistration.removeHandler();
        super.hide(autoClosed);
    }

}
