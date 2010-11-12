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
 * @version $Id$
 */
package com.pyx4j.widgets.client.tabpanel;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.widgets.client.DecoratorPanel;
import com.pyx4j.widgets.client.style.CSSClass;

public class TabPanelComposite extends SimplePanel {

    private final TabPanel<Tab> model;

    public TabPanelComposite() {
        model = new TabPanel<Tab>();
        model.getDeck().setStyleName("gwt-TabPanelBottom");
        VerticalPanel panel = new VerticalPanel();
        TabBar tabBar = model.getTabBar();
        LayoutPanel deck = model.getDeck();
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
        setStyleName("gwt-TabPanel");
    }

    public void insert(Tab tab, int beforeIndex, boolean closable) {
        model.insert(tab, beforeIndex, closable);
    }

    public void selectTab(int index) {
        model.select(index);
    }

    public boolean remove(Tab tab) {
        return model.remove(tab, false);
    }

    public Tab getTab(int index) {
        return model.getTabs().get(index);
    }

    public int getTabCount() {
        return model.getTabs().size();
    }

    public int getTabIndex(Tab tab) {
        return model.getTabs().indexOf(tab);
    }

}
