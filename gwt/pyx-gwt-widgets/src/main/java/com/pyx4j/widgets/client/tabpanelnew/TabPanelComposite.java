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
 * Created on Apr 29, 2009
 * @author michaellif
 * @version $Id: TabPanelComposite.java 7601 2010-11-27 15:40:11Z michaellif $
 */
package com.pyx4j.widgets.client.tabpanelnew;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.widgets.client.DecoratorPanel;
import com.pyx4j.widgets.client.style.CSSClass;

public class TabPanelComposite extends LayoutPanel {

    private final TabPanel tabPanel;

    public TabPanelComposite() {
        this(TabPanel.DEFAULT_STYLE_PREFIX);
    }

    public TabPanelComposite(String stylePrefix) {
        tabPanel = new TabPanel(stylePrefix);
        VerticalPanel panel = new VerticalPanel();
        TabBar tabBar = tabPanel.getTabBar();
        LayoutPanel deck = tabPanel.getDeck();
        panel.add(tabBar);

        DecoratorPanel deckDecorator = new DecoratorPanel(true, true, true, true, 2, CSSClass.pyx4j_Section_SelectionBorder.name());
        deckDecorator.setWidget(deck);

        panel.add(deckDecorator);

        deckDecorator.setSize("100%", "100%");
        panel.setCellHeight(deck, "100%");
        panel.setCellWidth(deck, "100%");

        tabBar.setWidth("100%");
        panel.setCellHeight(tabBar, "1px");
        panel.setCellWidth(tabBar, "100%");

        DecoratorPanel tabPanelDecorator = new DecoratorPanel(true, true, true, true, 1, CSSClass.pyx4j_Section_Border.name());
        tabPanelDecorator.setWidget(panel);

        add(tabPanelDecorator);
    }

    public void add(Tab tab) {
        tabPanel.add(tab);
    }

    public void insert(Tab tab, Tab beforeTab) {
        tabPanel.insert(tab, beforeTab);
    }

    public void selectTab(Tab tab) {
        tabPanel.select(tab);
    }

    public boolean remove(Tab tab) {
        return tabPanel.remove(tab);
    }

    public int getTabCount() {
        return tabPanel.getTabs().size();
    }

}
