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
 * @version $Id: TabPanel.java 7601 2010-11-27 15:40:11Z michaellif $
 */
package com.pyx4j.widgets.client.tabpanel;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;

import com.pyx4j.widgets.client.DeckLayoutPanel;
import com.pyx4j.widgets.client.event.shared.BeforeCloseEvent;
import com.pyx4j.widgets.client.event.shared.BeforeCloseHandler;
import com.pyx4j.widgets.client.event.shared.HasBeforeCloseHandlers;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class TabPanel implements HasBeforeSelectionHandlers<Tab>, HasSelectionHandlers<Tab>, HasCloseHandlers<Tab>, HasBeforeCloseHandlers<Tab> {

    private static final Logger log = LoggerFactory.getLogger(TabPanel.class);

    public static String DEFAULT_STYLE_PREFIX = "pyx4j_Tab";

    public static enum StyleSuffix implements IStyleSuffix {
        PanelBottom, BarItem, BarItemLeft, BarItemRight, BarItemLabel, BarItemImage
    }

    public static enum StyleDependent implements IStyleDependent {
        selected, disabled
    }

    private final DeckLayoutPanel deck = new DeckLayoutPanel();

    private final TabBar tabBar;

    private final HashSet<Tab> tabs = new HashSet<Tab>();

    private final String stylePrefix;

    private EventBus eventBus;

    public TabPanel() {
        this(DEFAULT_STYLE_PREFIX);
    }

    public TabPanel(String stylePrefix) {
        tabBar = new TabBar(this);
        this.stylePrefix = stylePrefix;
        tabBar.setStylePrefix(stylePrefix);
        deck.setStyleName(stylePrefix + StyleSuffix.PanelBottom);
    }

    public String getStylePrefix() {
        return stylePrefix;
    }

    public void add(Tab tab) {
        insert(tab, null);
    }

    public void insert(Tab tab, Tab beforeTab) {
        tabBar.insertTabBarItem(tab, beforeTab);
        deck.add(tab);
        tabs.add(tab);
        tab.setParentTabPanel(this);
        tab.select();
    }

    public boolean remove(Tab tab) {
        if (!tabs.contains(tab)) {
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
            select(selectTab);

        }

        tabBar.removeTabBarItem(tab);
        deck.remove(tab);
        tabs.remove(tab);
        tab.setParentTabPanel(null);
        if (deck.getWidgetCount() == 0) {
            deck.removeStyleName(getStylePrefix() + StyleSuffix.PanelBottom);
        }

        CloseEvent.fire(this, tab);

        return true;
    }

    public boolean select(Tab tab) {

        BeforeSelectionEvent<?> event = BeforeSelectionEvent.fire(this, tab);

        if (event != null && event.isCanceled()) {
            return false;
        }

        deck.showWidget(tab);
        tabBar.onTabSelection(tab);

        SelectionEvent.fire(this, tab);

        return true;
    }

    public Tab getSelectedTab() {
        return tabBar.getSelectedTab();
    }

    public HashSet<Tab> getTabs() {
        return tabs;
    }

    public DeckLayoutPanel getDeck() {
        return deck;
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

    protected final <H extends EventHandler> HandlerRegistration addHandler(final H handler, GwtEvent.Type<H> type) {
        return ensureHandlers().addHandler(type, handler);
    }

    EventBus ensureHandlers() {
        return eventBus == null ? eventBus = new SimpleEventBus() : eventBus;
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        if (eventBus != null) {
            eventBus.fireEventFromSource(event, this);
        }
    }

}