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

import java.util.ArrayList;
import java.util.List;

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

public class TabBar extends DockLayoutPanel implements ClickHandler {

    private final FlowPanel tabsHolder;

    private final TabPanel<? extends Tab> tabPanel;

    private TabBarItem selectedTab;

    private final ListAllTabsTrigger listAllTabsTrigger;

    private final ListAllTabsDropDown listAllTabsDropDown;

    /**
     * Creates an empty tab bar.
     */
    public TabBar(TabPanel<? extends Tab> tabPanel) {
        super(Unit.PX);

        this.tabPanel = tabPanel;

        listAllTabsTrigger = new ListAllTabsTrigger();
        listAllTabsTrigger.setVisible(false);

        listAllTabsTrigger.setWidget(new Image(ImageFactory.getImages().moveTabbarRight()));

        listAllTabsDropDown = new ListAllTabsDropDown(listAllTabsTrigger);

        listAllTabsTrigger.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (listAllTabsDropDown.isShowing()) {
                    listAllTabsDropDown.hideSelector();
                } else {
                    listAllTabsDropDown.showSelector();
                }
            }

        }, ClickEvent.getType());

        addEast(listAllTabsTrigger, 30);

        tabsHolder = new FlowPanel();

        add(tabsHolder);

        this.ensureDebugId(this.getClass().getName());

        sinkEvents(Event.ONCLICK);

    }

    public void setStylePrefix(String styleName) {
        setStyleName(styleName);
        listAllTabsTrigger.setStyleName(Selector.getStyleName(getStyleName(), TabPanel.StyleSuffix.BarItem));
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
        return tabsHolder.getWidgetIndex(selectedTab);
    }

    /**
     * Gets the number of tabs present.
     * 
     * @return the tab count
     */
    public int getTabCount() {
        return tabsHolder.getWidgetCount();
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
        return ((HTML) tabsHolder.getWidget(index)).getHTML();
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

        TabBarItem item = new TabBarItem(this, label, imageResource, closable, getStyleName());

        if (beforeIndex == 0) {
            if (tabsHolder.getWidgetCount() > 0) {
                Widget firstTab = tabsHolder.getWidget(0);
                firstTab.removeStyleDependentName(Selector.getDependentSuffix(TabPanel.StyleDependent.first));
            }
            item.addStyleDependentName(Selector.getDependentSuffix(TabPanel.StyleDependent.first));
        }

        tabsHolder.insert(item, beforeIndex);

    }

    public void setLabelText(int index, String labelText) {
        Widget widget = tabsHolder.getWidget(index);
        if (labelText == null || labelText.trim().length() == 0) {
            labelText = "___";
        }
        ((TabBarItem) widget).setLabel(labelText);
    }

    public void setModifyed(int index, boolean modifyed) {
        Widget widget = tabsHolder.getWidget(index);
        ((TabBarItem) widget).setModifyed(modifyed);
    }

    @Override
    public void onClick(ClickEvent event) {
        for (int i = 0; i < tabsHolder.getWidgetCount(); ++i) {

            TabBarItem tabBarItem = getTabBarItemParent((Widget) event.getSource());
            if (tabsHolder.getWidget(i) == tabBarItem) {
                if (tabBarItem.isEnabled()) {
                    checkTabIndex(i);
                    tabPanel.select(i);
                }
                return;
            }
        }
    }

    private TabBarItem getTabBarItemParent(Widget child) {
        while ((child != null) && !(child instanceof TabBarItem)) {
            child = child.getParent();
        }
        return (TabBarItem) child;
    }

    /**
     * Removes the tab at the specified index.
     * 
     * @param index
     *            the index of the tab to be removed
     */
    public void removeTab(int index) {
        checkTabIndex(index);

        Widget toRemove = tabsHolder.getWidget(index);

        if (index == 0) {
            toRemove.removeStyleDependentName("first");
            if (tabsHolder.getWidgetCount() > 1) {
                Widget nextFirstTab = tabsHolder.getWidget(1);
                nextFirstTab.addStyleDependentName("first");
            }
        }

        if (toRemove == selectedTab) {
            selectedTab = null;
        }
        tabsHolder.remove(toRemove);

    }

    public void selectTab(int index) {
        setSelected(selectedTab, false);
        selectedTab = (TabBarItem) tabsHolder.getWidget(index);
        setSelected(selectedTab, true);
    }

    public void enableTab(int index, boolean isEnabled) {
        TabBarItem tab = (TabBarItem) tabsHolder.getWidget(index);
        tab.setEnabled(isEnabled);
    }

    private void checkTabIndex(int index) {
        if ((index < 0) || (index >= getTabCount())) {
            throw new IndexOutOfBoundsException("TabBar index " + String.valueOf(index));
        }
    }

    private void setSelected(TabBarItem item, boolean selected) {
        if (item != null) {
            item.setSelected(selected);
        }
    }

    FlowPanel getTabBarPanel() {
        return tabsHolder;
    }

    TabPanel<? extends Tab> getTabPanelModel() {
        return tabPanel;
    }

    @Override
    public void onResize() {
        boolean isTriggerVisible = false;
        for (int i = 0; i < tabsHolder.getWidgetCount(); i++) {
            if (getAbsoluteTop() - tabsHolder.getWidget(i).getAbsoluteTop() < 0) {
                isTriggerVisible = true;
                break;
            }
        }
        listAllTabsTrigger.setVisible(isTriggerVisible);
        super.onResize();
    }

    class ListAllTabsTrigger extends SimplePanel {

        List<TabBarItem> getAllTabBarItems() {
            ArrayList<TabBarItem> retVal = new ArrayList<TabBarItem>();
            for (int i = 0; i < tabsHolder.getWidgetCount(); ++i) {
                retVal.add((TabBarItem) tabsHolder.getWidget(i));
            }
            return retVal;
        }

        public void selectTab(TabBarItem tabBarItem) {
            for (int i = 0; i < tabsHolder.getWidgetCount(); ++i) {
                if (tabsHolder.getWidget(i) == tabBarItem) {
                    if (tabBarItem.isEnabled()) {
                        checkTabIndex(i);
                        tabPanel.select(i);
                    }
                    return;
                }
            }

        }

    }

}