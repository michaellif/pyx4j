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
 * Created on Apr 22, 2009
 * @author michaellif
 * @version $Id: TabBar.java 7480 2010-11-13 03:06:33Z michaellif $
 */
package com.pyx4j.widgets.client.tabpanel;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.style.Selector;

public class TabBarNew extends DockLayoutPanel implements ClickHandler {

    private final FlowPanel tabsBar;

    private final TabPanel<? extends Tab> tabPanel;

    private TabBarItemNew selectedTab;

    private final SimplePanel dropdownHandler;

    /**
     * Creates an empty tab bar.
     */
    public TabBarNew(TabPanel<? extends Tab> tabPanel) {
        super(Unit.PX);

        this.tabPanel = tabPanel;

        dropdownHandler = new SimplePanel();
        dropdownHandler.setVisible(false);

        Image listAllTabsImage = new Image(ImageFactory.getImages().moveTabbarRight());
        dropdownHandler.setWidget(listAllTabsImage);

        addEast(dropdownHandler, 30);

        tabsBar = new FlowPanel();

        add(tabsBar);

        this.ensureDebugId(this.getClass().getName());

        sinkEvents(Event.ONCLICK);

    }

    public void setStylePrefix(String styleName) {
        setStyleName(styleName);
        dropdownHandler.setStyleName(Selector.getStyleName(getStyleName(), TabPanel.StyleSuffix.BarItem));
    }

    /**
     * Adds a new tab with the specified text.
     * 
     * @param text
     *            the new tab's text
     * @param asHTML
     *            <code>true</code> to treat the specified text as html
     */
    public void addTab(String text, ImageResource imageResource, boolean closable) {
        insertTab(text, imageResource, getTabCount(), closable);
    }

    /**
     * Gets the tab that is currently selected.
     * 
     * @return the selected tab
     */
    public int getSelectedTab() {
        if (selectedTab == null) {
            return -1;
        }
        return tabsBar.getWidgetIndex(selectedTab);
    }

    /**
     * Gets the number of tabs present.
     * 
     * @return the tab count
     */
    public int getTabCount() {
        return tabsBar.getWidgetCount();
    }

    /**
     * Gets the specified tab's HTML.
     * 
     * @param index
     *            the index of the tab whose HTML is to be retrieved
     * @return the tab's HTML
     */
    public String getTabHTML(int index) {
        if (index >= getTabCount()) {
            return null;
        }
        return ((HTML) tabsBar.getWidget(index)).getHTML();
    }

    /**
     * Inserts a new tab at the specified index.
     * 
     * @param text
     *            the new tab's text
     * @param asHTML
     *            <code>true</code> to treat the specified text as HTML
     * @param beforeIndex
     *            the index before which this tab will be inserted
     */
    public void insertTab(String label, ImageResource imageResource, int beforeIndex, boolean closable) {
        if ((beforeIndex < 0) || (beforeIndex > getTabCount())) {
            throw new IndexOutOfBoundsException();
        }

        TabBarItemNew item = new TabBarItemNew(this, label, imageResource, closable, getStyleName());

        if (beforeIndex == 0) {
            if (tabsBar.getWidgetCount() > 0) {
                Widget firstTab = tabsBar.getWidget(0);
                firstTab.removeStyleDependentName(Selector.getDependentSuffix(TabPanel.StyleDependent.first));
            }
            item.addStyleDependentName(Selector.getDependentSuffix(TabPanel.StyleDependent.first));
        }

        tabsBar.insert(item, beforeIndex);

    }

    public void setLabelText(int index, String labelText) {
        Widget widget = tabsBar.getWidget(index);
        if (labelText == null || labelText.trim().length() == 0) {
            labelText = "___";
        }
        ((TabBarItemNew) widget).setLabel(labelText);
    }

    public void setModifyed(int index, boolean modifyed) {
        Widget widget = tabsBar.getWidget(index);
        ((TabBarItemNew) widget).setModifyed(modifyed);
    }

    @Override
    public void onClick(ClickEvent event) {
        for (int i = 0; i < tabsBar.getWidgetCount(); ++i) {

            TabBarItemNew tabBarItem = getTabBarItemParent((Widget) event.getSource());
            if (tabsBar.getWidget(i) == tabBarItem) {
                if (tabBarItem.isEnabled()) {
                    checkTabIndex(i);
                    tabPanel.select(i);
                }
                return;
            }
        }
    }

    private TabBarItemNew getTabBarItemParent(Widget child) {
        while ((child != null) && !(child instanceof TabBarItemNew)) {
            child = child.getParent();
        }
        return (TabBarItemNew) child;
    }

    /**
     * Removes the tab at the specified index.
     * 
     * @param index
     *            the index of the tab to be removed
     */
    public void removeTab(int index) {
        checkTabIndex(index);

        Widget toRemove = tabsBar.getWidget(index);

        if (index == 0) {
            toRemove.removeStyleDependentName("first");
            if (tabsBar.getWidgetCount() > 1) {
                Widget nextFirstTab = tabsBar.getWidget(1);
                nextFirstTab.addStyleDependentName("first");
            }
        }

        if (toRemove == selectedTab) {
            selectedTab = null;
        }
        tabsBar.remove(toRemove);

    }

    public void selectTab(int index) {
        setSelected(selectedTab, false);
        selectedTab = (TabBarItemNew) tabsBar.getWidget(index);
        setSelected(selectedTab, true);
    }

    public void enableTab(int index, boolean isEnabled) {
        TabBarItemNew tab = (TabBarItemNew) tabsBar.getWidget(index);
        tab.setEnabled(isEnabled);
    }

    private void checkTabIndex(int index) {
        if ((index < 0) || (index >= getTabCount())) {
            throw new IndexOutOfBoundsException("TabBar index " + String.valueOf(index));
        }
    }

    private void setSelected(TabBarItemNew item, boolean selected) {
        if (item != null) {
            item.setSelected(selected);
        }
    }

    FlowPanel getTabBarPanel() {
        return tabsBar;
    }

    TabPanel<? extends Tab> getTabPanelModel() {
        return tabPanel;
    }

    @Override
    public void onResize() {
        boolean isVisibleHandler = false;
        for (int i = 0; i < tabsBar.getWidgetCount(); i++) {
            if (getAbsoluteTop() - tabsBar.getWidget(i).getAbsoluteTop() < 0) {
                isVisibleHandler = true;
                break;
            }
        }
        dropdownHandler.setVisible(isVisibleHandler);
        super.onResize();
    }

}