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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.event.shared.BeforeCloseEvent;
import com.pyx4j.widgets.client.event.shared.BeforeCloseHandler;
import com.pyx4j.widgets.client.event.shared.HasBeforeCloseHandlers;

public class TabPanel extends ResizeComposite implements HasWidgets, ProvidesResize, IndexedPanel.ForIsWidget, HasBeforeSelectionHandlers<Tab>,
        HasSelectionHandlers<Tab>, HasCloseHandlers<Tab>, HasBeforeCloseHandlers<Tab> {

    private static final Logger log = LoggerFactory.getLogger(TabPanel.class);

    private final DeckLayoutPanel deckPanel;

    private final TabBar tabBar;

    private final HashMap<TabBarItem, Tab> tabs = new HashMap<TabBarItem, Tab>();

    private EventBus eventBus;

    private Tab selectedTab;

    public TabPanel(double barHeight, Unit barUnit) {

        LayoutPanel panel = new LayoutPanel();
        initWidget(panel);

        setStyleName(DefaultTabTheme.StyleName.TabPanel.name());

        tabBar = new TabBar(this);

        deckPanel = new DeckLayoutPanel();
        deckPanel.setStyleName(DefaultTabTheme.StyleName.TabDeckPanel.name());

        panel.add(tabBar);
        panel.setWidgetLeftRight(tabBar, 0, Unit.PX, 0, Unit.PX);
        panel.setWidgetTopHeight(tabBar, 0, Unit.PX, barHeight, barUnit);

        panel.add(deckPanel);
        panel.setWidgetLeftRight(deckPanel, 0, Unit.PX, 0, Unit.PX);
        panel.setWidgetTopBottom(deckPanel, barHeight, barUnit, 0, Unit.PX);

    }

    public void addTab(Tab tab) {
        insertTab(tab, tabs.size());
    }

    public void insertTab(Tab tab, int beforeIndex) {
        tabBar.insert(tab.getTabBarItem(), beforeIndex);
        deckPanel.add(tab);
        tabs.put(tab.getTabBarItem(), tab);
        tab.setTabPanel(this);
    }

    public boolean removeTab(Tab tab) {
        if (!tabs.containsKey(tab.getTabBarItem())) {
            log.error("Tab can't be removed. TabPanel doesn't contain this Tab.");
            return false;
        }

        BeforeCloseEvent<?> event = BeforeCloseEvent.fire(this, tab);

        if (event != null && event.isCanceled()) {
            return false;
        }

        if (getSelectedTab() == tab) {
            Tab selectTab = tabBar.getFollowingTab(tab);
            if (selectTab == null) {
                selectTab = tabBar.getPrecedingTab(tab);
            }
            selectTab(selectTab);

        }

        tabBar.remove(tab.getTabBarItem());
        deckPanel.remove(tab);
        tabs.remove(tab.getTabBarItem());
        tab.setTabPanel(null);
        if (deckPanel.getWidgetCount() == 0) {
            deckPanel.removeStyleName(DefaultTabTheme.StyleName.TabDeckPanel.name());
        }

        CloseEvent.fire(this, tab);

        return true;
    }

    @Override
    public boolean remove(int index) {
        return removeTab(tabs.get(index));
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub

    }

    public void setTabEnabled(Tab tab, boolean enabled) {
        tab.setTabEnabled(enabled);
    }

    public boolean isTabEnabled(Tab tab) {
        return tab.isTabEnabled();
    }

    public void setTabVisible(Tab tab, boolean visible) {
        tab.setTabVisible(visible);
    }

    public boolean isTabVisible(Tab tab) {
        return tab.isTabVisible();
    }

    public boolean selectTab(Tab tab) {

        BeforeSelectionEvent<?> event = BeforeSelectionEvent.fire(this, tab);

        if (event != null && event.isCanceled()) {
            return false;
        }

        selectedTab = tab;

        deckPanel.showWidget(selectedTab);

        tabBar.onTabSelected(selectedTab.getTabBarItem());

        SelectionEvent.fire(this, selectedTab);

        return true;
    }

    public void selectTab(int index) {
        selectTab(tabs.get(tabBar.getTabBarItem(index)));
    }

    public Tab getSelectedTab() {
        return selectedTab;
    }

    public int getSelectedIndex() {
        return tabBar.getTabBarIndex(selectedTab.getTabBarItem());
    }

    public Collection<Tab> getTabs() {
        return tabs.values();
    }

    public DeckLayoutPanel getDeck() {
        return deckPanel;
    }

    public int size() {
        return tabs.size();
    }

    public TabBar getTabBar() {
        return tabBar;
    }

    @Override
    public HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<Tab> handler) {
        return addHandler(handler, BeforeSelectionEvent.getType());
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<Tab> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }

    @Override
    public HandlerRegistration addBeforeCloseHandler(BeforeCloseHandler<Tab> handler) {
        return addHandler(handler, BeforeCloseEvent.getType());
    }

    @Override
    public HandlerRegistration addCloseHandler(CloseHandler<Tab> handler) {
        return addHandler(handler, CloseEvent.getType());
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        if (eventBus != null) {
            eventBus.fireEventFromSource(event, this);
        }
    }

    @Override
    public Widget getWidget(int index) {
        return deckPanel.getWidget(index);
    }

    @Override
    public int getWidgetCount() {
        return deckPanel.getWidgetCount();
    }

    @Override
    public int getWidgetIndex(Widget child) {
        return getWidgetIndex(asWidgetOrNull(child));
    }

    @Override
    public int getWidgetIndex(IsWidget child) {
        return getWidgetIndex(asWidgetOrNull(child));
    }

    @Override
    public void add(Widget w) {
        throw new UnsupportedOperationException("Use TabPanel.add(Tab tab) instead");
    }

    @Override
    public boolean remove(Widget w) {
        throw new UnsupportedOperationException("Use TabPanel.remove(Tab tab) instead");
    }

    @Override
    public Iterator<Widget> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

}