/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
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
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DeckPanel;

import com.pyx4j.widgets.client.event.shared.BeforeCloseEvent;
import com.pyx4j.widgets.client.event.shared.BeforeCloseHandler;
import com.pyx4j.widgets.client.event.shared.HasBeforeCloseHandlers;

public class TabPanelModel implements HasBeforeSelectionHandlers<ITab>, HasSelectionHandlers<ITab>, HasCloseHandlers<ITab>, HasBeforeCloseHandlers<ITab> {

    private final DeckPanel deck = new DeckPanel();

    private final TabBar tabBar = new TabBar(this);

    private final ArrayList<ITab> tabs = new ArrayList<ITab>();

    private HandlerManager handlerManager;

    public TabPanelModel() {
    }

    public void add(ITab tab) {
        insert(tab, deck.getWidgetCount(), false);
    }

    public void add(ITab tab, boolean closable) {
        insert(tab, getSelectedTab() + 1, closable);
    }

    public void insert(ITab tab, int index, boolean closable) {
        tabBar.insertTab(tab.getTitle(), tab.getImageResource(), index, closable);
        deck.insert(tab.getContentPane(), index);
        tabs.add(index, tab);
    }

    /**
     * Removes the given widget, and its associated tab.
     * 
     * @param widget
     *            the widget to be removed
     * @param forced
     *            the tab will be close no metter what fireBeforeTabClosed returns
     */
    public boolean remove(ITab tab, boolean forced) {
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
        tabs.remove(index);
        if (deck.getWidgetCount() == 0) {
            deck.removeStyleName("gwt-TabPanelBottom");
        }

        CloseEvent.fire(this, tab);

        return true;
    }

    public boolean remove(int index, boolean forced) {
        ITab tab = tabs.get(index);
        return remove(tab, forced);

    }

    public boolean select(ITab tab) {

        int index = tabs.indexOf(tab);

        BeforeSelectionEvent<?> event = BeforeSelectionEvent.fire(this, tab);

        if (event != null && event.isCanceled()) {
            return false;
        }

        deck.showWidget(index);
        tabBar.selectTab(index);

        SelectionEvent.fire(this, tab);

        return true;
    }

    public boolean select(int index) {
        ITab tab = tabs.get(index);
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

    public DeckPanel getDeck() {
        return deck;
    }

    public List<ITab> getTabs() {
        return tabs;
    }

    public HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<ITab> handler) {
        return addHandler(handler, BeforeSelectionEvent.getType());
    }

    public HandlerRegistration addSelectionHandler(SelectionHandler<ITab> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }

    public HandlerRegistration addBeforeCloseHandler(BeforeCloseHandler<ITab> handler) {
        return addHandler(handler, BeforeCloseEvent.getType());
    }

    public HandlerRegistration addCloseHandler(CloseHandler<ITab> handler) {
        return addHandler(handler, CloseEvent.getType());
    }

    protected final <H extends EventHandler> HandlerRegistration addHandler(final H handler, GwtEvent.Type<H> type) {
        return ensureHandlers().addHandler(type, handler);
    }

    HandlerManager ensureHandlers() {
        return handlerManager == null ? handlerManager = new HandlerManager(this) : handlerManager;
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        if (handlerManager != null) {
            handlerManager.fireEvent(event);
        }
    }

}