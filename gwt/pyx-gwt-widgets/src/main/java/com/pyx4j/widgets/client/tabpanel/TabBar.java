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
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.widgets.client.ImageFactory;

public class TabBar extends DockLayoutPanel {

    private static final Logger log = LoggerFactory.getLogger(TabBar.class);

    private final FlowPanel tabsHolder;

    private TabBarItem selectedTabBarItem;

    private final TabListAction tabListAction;

    private final TabBarAction slideLeftAction;

    private final TabBarAction slideRightAction;

    private final TabListDropDown listAllTabsDropDown;

    private int leftOverflowIndex = 0;

    /**
     * Creates an empty tab bar.
     */
    public TabBar(TabPanel tabPanel) {
        super(Unit.PX);

        setStyleName(DefaultTabTheme.StyleName.TabBar.name());

        tabListAction = new TabListAction();
        tabListAction.setVisible(false);
        listAllTabsDropDown = new TabListDropDown(tabListAction);

        tabListAction.setComand(new Command() {
            @Override
            public void execute() {
                if (listAllTabsDropDown.isShowing()) {
                    listAllTabsDropDown.hideSelector();
                } else {
                    listAllTabsDropDown.showSelector();
                }
            }
        });
        addEast(tabListAction, 30);

        slideLeftAction = new TabBarAction(ImageFactory.getImages().moveTabbarLeft());
        slideLeftAction.setVisible(false);
        slideLeftAction.setComand(new Command() {
            @Override
            public void execute() {
                slideOneLeft();
            }
        });
        addEast(slideLeftAction, 30);

        slideRightAction = new TabBarAction(ImageFactory.getImages().moveTabbarRight());
        slideRightAction.setVisible(false);
        slideRightAction.setComand(new Command() {
            @Override
            public void execute() {
                slideOneRight();
                ensureScrollActionsExposed();
            }
        });
        slideRightAction.setEnabled(false);
        addEast(slideRightAction, 30);

        tabsHolder = new FlowPanel();

        add(tabsHolder);

        this.ensureDebugId(this.getClass().getName());

        sinkEvents(Event.ONCLICK);

    }

    public void add(TabBarItem item) {
        insert(item, tabsHolder.getWidgetCount());
    }

    public void insert(TabBarItem item, int beforeIndex) {
        tabsHolder.insert(item, beforeIndex);
        layout();
    }

    public void remove(TabBarItem item) {
        if (item == selectedTabBarItem) {
            selectedTabBarItem = null;
        }
        tabsHolder.remove(item);
        layout();
    }

    public void onTabSelected(TabBarItem item) {
        if (selectedTabBarItem != null) {
            selectedTabBarItem.onSelected(false);
        }
        selectedTabBarItem = item;
        if (selectedTabBarItem != null) {
            selectedTabBarItem.onSelected(true);
        }
        ensureSelectedTabExposed();
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

    private void ensureScrollActionsExposed() {
        boolean isTriggerVisible = false;
        if (leftOverflowIndex > 0) {
            isTriggerVisible = true;
        } else {
            isTriggerVisible = isTabBarWrapped();
        }

        tabListAction.setVisible(isTriggerVisible);
        slideRightAction.setVisible(isTriggerVisible);
        slideLeftAction.setVisible(isTriggerVisible);
    }

    private boolean isTabBarWrapped() {
        for (int i = 0; i < tabsHolder.getWidgetCount(); i++) {
            if (!((TabBarItem) tabsHolder.getWidget(i)).isTabExposed()) {
                return true;
            }
        }
        return false;
    }

    private void ensureSelectedTabExposed() {
        if (selectedTabBarItem == null) {
            return;
        }

        if (!selectedTabBarItem.isTabMasked() && selectedTabBarItem.isTabExposed()) {
            return;
        }

        int index = getTabBarIndex(selectedTabBarItem);

        if (index < leftOverflowIndex) {
            int steps = leftOverflowIndex - index;
            for (int i = 0; i < steps; i++) {
                slideOneRight();
            }
        } else {
            do {
                slideOneLeft();
            } while (!selectedTabBarItem.isTabExposed());
        }

    }

    public void layout() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                ensureSelectedTabExposed();
                ensureScrollActionsExposed();
                slideLeftAction.setEnabled(isTabBarWrapped());
            }
        });
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        layout();
    }

    public int getTabBarIndex(TabBarItem selectedTabBarItem) {
        return tabsHolder.getWidgetIndex(selectedTabBarItem);
    }

    public TabBarItem getTabBarItem(int index) {
        return (TabBarItem) tabsHolder.getWidget(index);
    }

    public int getTabBarCount() {
        return tabsHolder.getWidgetCount();
    }

    protected boolean slideOneLeft() {
        if (leftOverflowIndex < getTabBarCount() - 1) {
            leftOverflowIndex++;
            slideRightAction.setEnabled(true);
            if (!isTabBarWrapped()) {
                slideLeftAction.setEnabled(false);
            }
            return slide();
        } else {
            return false;
        }
    }

    protected boolean slideOneRight() {
        if (leftOverflowIndex > 0) {
            leftOverflowIndex--;
            slideLeftAction.setEnabled(true);
            if (leftOverflowIndex == 0) {
                slideRightAction.setEnabled(false);
            }
            return slide();
        } else {
            return false;
        }
    }

    protected boolean slide() {
        for (int i = 0; i < getTabBarCount(); i++) {
            if (i < leftOverflowIndex) {
                getTabBarItem(i).setTabMasked(true);
            } else {
                getTabBarItem(i).setTabMasked(false);
            }
        }
        return true;
    }

    class TabBarAction extends SimplePanel {

        private Command command;

        private boolean enabled = true;

        public TabBarAction(ImageResource imageResource) {

            setWidget(new Image(imageResource));

            setStyleName(DefaultTabTheme.StyleName.TabBarAction.name());

            addDomHandler(new MouseDownHandler() {

                @Override
                public void onMouseDown(MouseDownEvent event) {
                    if (enabled && command != null) {
                        command.execute();
                    }
                }

            }, MouseDownEvent.getType());
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
            String dependentSuffix = DefaultTabTheme.StyleDependent.disabled.name();
            if (!enabled) {
                addStyleDependentName(dependentSuffix);
            } else {
                removeStyleDependentName(dependentSuffix);
            }

        }

        public void setComand(Command command) {
            this.command = command;
        }
    }

    class TabListAction extends TabBarAction {

        TabListAction() {
            super(ImageFactory.getImages().tabbarDropDown());
        }

        List<Tab> getAllTabs() {
            ArrayList<Tab> ordererList = new ArrayList<>();
            for (int i = 0; i < tabsHolder.getWidgetCount(); i++) {
                ordererList.add(((TabBarItem) tabsHolder.getWidget(i)).getTab());
            }
            return ordererList;
        }

        public void selectTab(Tab tab) {
            tab.setTabSelected();
        }

    }
}