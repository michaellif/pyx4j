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
package com.pyx4j.widgets.client.tabpanelnew;

import java.util.List;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.widgets.client.DropDownPanel;
import com.pyx4j.widgets.client.tabpanelnew.TabBar.ListAllTabsTrigger;

public class ListAllTabsDropDown extends DropDownPanel {

    private final ListAllTabsTrigger trigger;

    private final FlowPanel itemsPanel;

    ListAllTabsDropDown(ListAllTabsTrigger trigger) {
        this.trigger = trigger;
        getElement().getStyle().setBorderColor("gray");
        getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
        getElement().getStyle().setBorderWidth(1, Unit.PX);

        itemsPanel = new FlowPanel();
        itemsPanel.getElement().getStyle().setBackgroundColor("white");
        itemsPanel.getElement().getStyle().setPadding(2, Unit.PX);
        setWidget(itemsPanel);
    }

    public void showSelector() {
        itemsPanel.clear();
        List<Tab> allTabs = trigger.getAllTabs();
        for (final Tab tab : allTabs) {
            Label item = new Label(tab.getTabTitle());
            item.getElement().getStyle().setPadding(2, Unit.PX);
            item.getElement().getStyle().setPaddingLeft(4, Unit.PX);
            item.getElement().getStyle().setPaddingRight(4, Unit.PX);
            //            if (selectedSearch.equals(searchType)) {
            //                item.getElement().getStyle().setBackgroundColor("#88AEB5");
            //                item.getElement().getStyle().setColor("#fff");
            //            }
            item.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    trigger.selectTab(tab);
                    hide();
                }
            });
            item.getElement().getStyle().setCursor(Cursor.POINTER);

            itemsPanel.add(item);
        }
        showRelativeTo(trigger);
    }

    public void hideSelector() {
        hide();
    }

}
