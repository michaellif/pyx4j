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

public class TabPanel<E extends Tab> implements HasBeforeSelectionHandlers<E>, HasSelectionHandlers<E>, HasCloseHandlers<E>, HasBeforeCloseHandlers<E> {

    public static String DEFAULT_STYLE_PREFIX = "pyx4j_Tab";

    public static enum StyleSuffix implements IStyleSuffix {
        PanelBottom, BarMoveLeft, BarMoveRight, BarItem, BarItemLeft, BarItemRight, BarItemLabel, BarItemImage
    }

    public static enum StyleDependent implements IStyleDependent {
        selected, disabled, first
    }

    private final DeckLayoutPanel deck = new DeckLayoutPanel();

    private final TabBar tabBar;

    private final ArrayList<E> tabs = new ArrayList<E>();

    private EventBus eventBus;

    private String styleName;

    public TabPanel() {
        tabBar = new TabBar(this);
        setStylePrefix(DEFAULT_STYLE_PREFIX);
    }

    public void setStylePrefix(String styleName) {
        this.styleName = styleName;
        tabBar.setStylePrefix(styleName);
        getDeck().setStyleName(styleName + StyleSuffix.PanelBottom);
    }

    public void add(E tab) {
        insert(tab, deck.getWidgetCount(), false);
    }

    public void add(E tab, boolean closable) {
        insert(tab, getSelectedTab() + 1, closable);
    }

    public void insert(E tab, int index, boolean closable) {
        tabBar.insertTab(tab.getTabTitle(), tab.getTabImage(), index, closable);
        deck.insert(tab, index);
        tabs.add(index, tab);
        tab.setParentTabPanel(this);
    }

    /**
     * Removes the given widget, and its associated tab.
     * 
     * @param widget
     *            the widget to be removed
     * @param forced
     *            the tab will be close no metter what fireBeforeTabClosed returns
     */
    public boolean remove(E tab, boolean forced) {
        int index = tabs.indexOf(tab);
        if (index == -1) {
            return false;
        }

        BeforeCloseEvent<?> event = BeforeCloseEvent.fire(this, tab);

        if (event != null && event.isCanceled()) {
            return false;
        }

        if (getSelectedTab() == index) {
            if (index > 0) {
                select(index - 1);
            } else if ((index == 0) && (tabBar.getTabBarPanel().getWidgetCount() > 1)) {
                select(index + 1);
            }
        }

        tabBar.removeTab(index);
        deck.remove(index);
        tabs.remove(index).setParentTabPanel(null);
        if (deck.getWidgetCount() == 0) {
            deck.removeStyleName(styleName + StyleSuffix.PanelBottom);
        }

        CloseEvent.fire(this, tab);

        return true;
    }

    public boolean remove(int index, boolean forced) {
        E tab = tabs.get(index);
        return remove(tab, forced);

    }

    public boolean select(E tab) {

        int index = tabs.indexOf(tab);

        BeforeSelectionEvent<?> event = BeforeSelectionEvent.fire(this, tab);

        if (event != null && event.isCanceled()) {
            return false;
        }

        deck.showWidget(tab);
        tabBar.selectTab(index);

        SelectionEvent.fire(this, tab);

        return true;
    }

    public boolean select(int index) {
        E tab = tabs.get(index);
        return select(tab);

    }

    public void enableTab(int index, boolean isEnabled) {
        tabBar.enableTab(index, isEnabled);
    }

    public void setLabelText(int index, String labelText) {
        tabBar.setLabelText(index, labelText);
    }

    public void setModifyed(int index, boolean modifyed) {
        tabBar.setModifyed(index, modifyed);
    }

    public int getSelectedTab() {
        return tabBar.getSelectedTab();
    }

    public int size() {
        return deck.getWidgetCount();
    }

    /**
     * Gets the tab bar within this tab panel
     * 
     * @return the tab bar
     */
    public TabBar getTabBar() {
        return tabBar;
    }

    public DeckLayoutPanel getDeck() {
        return deck;
    }

    protected List<E> getTabs() {
        return tabs;
    }

    @Override
    public HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<E> handler) {
        return addHandler(handler, BeforeSelectionEvent.getType());
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<E> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }

    @Override
    public HandlerRegistration addBeforeCloseHandler(BeforeCloseHandler<E> handler) {
        return addHandler(handler, BeforeCloseEvent.getType());
    }

    @Override
    public HandlerRegistration addCloseHandler(CloseHandler<E> handler) {
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