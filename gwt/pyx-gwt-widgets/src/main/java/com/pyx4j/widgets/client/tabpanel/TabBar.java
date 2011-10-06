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
 * @version $Id$
 */
package com.pyx4j.widgets.client.tabpanel;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.widgets.client.ImageFactory;

public class TabBar extends DockLayoutPanel {

    private static final Logger log = LoggerFactory.getLogger(TabBar.class);

    private final FlowPanel tabsHolder;

    private final TabPanel tabPanel;

    private Tab selectedTab;

    private final TabListTrigger tabListTrigger;

    private final TabListDropDown listAllTabsDropDown;

    /**
     * Creates an empty tab bar.
     */
    public TabBar(TabPanel tabPanel) {
        super(Unit.PX);

        this.tabPanel = tabPanel;

        tabListTrigger = new TabListTrigger();
        tabListTrigger.setStyleName(TabPanel.StyleName.TabBarItem.name());

        tabListTrigger.setVisible(false);

        tabListTrigger.setWidget(new Image(ImageFactory.getImages().moveTabbarRight()));

        listAllTabsDropDown = new TabListDropDown(tabListTrigger);

        tabListTrigger.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (listAllTabsDropDown.isShowing()) {
                    listAllTabsDropDown.hideSelector();
                } else {
                    listAllTabsDropDown.showSelector();
                }
            }

        }, ClickEvent.getType());

        addEast(tabListTrigger, 30);

        tabsHolder = new FlowPanel();

        add(tabsHolder);

        this.ensureDebugId(this.getClass().getName());

        sinkEvents(Event.ONCLICK);

    }

    public void addTabBarItem(Tab tab) {
        insertTabBarItem(tab, null);
    }

    public void insertTabBarItem(Tab tab, Tab beforeTab) {
        if (beforeTab == null) {
            tabsHolder.add(tab.getTabBarItem());
        } else {
            int beforeIndex = tabsHolder.getWidgetIndex(beforeTab.getTabBarItem());
            tabsHolder.insert(tab.getTabBarItem(), beforeIndex);
        }
        layout();
    }

    public void removeTabBarItem(Tab tab) {
        if (tab == selectedTab) {
            selectedTab = null;
        }
        tabsHolder.remove(tab.getTabBarItem());
        layout();
    }

    public void onTabSelection(Tab tab) {
        if (selectedTab != null) {
            selectedTab.getTabBarItem().onSelected(false);
        }
        selectedTab = tab;
        if (selectedTab != null) {
            selectedTab.getTabBarItem().onSelected(true);
        }
        ensureSelectedTabVisible();
    }

    public void enableTab(Tab tab, boolean isEnabled) {
        tab.getTabBarItem().onEnabled(isEnabled);
    }

    public Tab getSelectedTab() {
        return selectedTab;
    }

    public Tab getFollowingTab(Tab tab) {
        int index = tabsHolder.getWidgetIndex(tab.getTabBarItem()) + 1;
        if (index >= 0 && tabsHolder.getWidgetCount() > index) {
            TabBarItem item = (TabBarItem) tabsHolder.getWidget(index);
            return item.getTab();
        } else {
            return null;
        }
    }

    public Tab getPrecedingTab(Tab tab) {
        int index = tabsHolder.getWidgetIndex(tab.getTabBarItem()) - 1;
        if (index >= 0 && tabsHolder.getWidgetCount() > index) {
            TabBarItem item = (TabBarItem) tabsHolder.getWidget(index);
            return item.getTab();
        } else {
            return null;
        }
    }

    @Override
    public void onResize() {
        layout();
        super.onResize();
    }

    private void ensureTabListTriggerVisible() {
        boolean isTriggerVisible = false;
        for (int i = 0; i < tabsHolder.getWidgetCount(); i++) {
            if (getAbsoluteTop() - tabsHolder.getWidget(i).getAbsoluteTop() < 0) {
                isTriggerVisible = true;
                break;
            }
        }
        tabListTrigger.setVisible(isTriggerVisible);
    }

    private void ensureSelectedTabVisible() {
        if (selectedTab == null) {
            return;
        } else if (getAbsoluteTop() - selectedTab.getTabBarItem().getAbsoluteTop() == 0) {
            return;
        } else {
            TabBarItem item = (TabBarItem) tabsHolder.getWidget(0);
            tabsHolder.remove(item);
            tabsHolder.add(item);
            ensureSelectedTabVisible();
        }
    }

    public void layout() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                ensureSelectedTabVisible();
                ensureTabListTriggerVisible();
            }
        });
    }

    protected boolean isTabVisible(Tab tab) {
        assert (tab != null);
        return (getAbsoluteTop() - tab.getTabBarItem().getAbsoluteTop() == 0);
    }

    class TabListTrigger extends SimplePanel {

        List<Tab> getAllTabs() {
            ArrayList<Tab> ordererList = new ArrayList<Tab>();
            for (int i = 0; i < tabsHolder.getWidgetCount(); i++) {
                ordererList.add(((TabBarItem) tabsHolder.getWidget(i)).getTab());
            }
            return ordererList;
        }

        public void selectTab(Tab tab) {
            tab.setSelected();
        }

    }

    @Override
    protected void onLoad() {
        super.onLoad();
        layout();
    }

}