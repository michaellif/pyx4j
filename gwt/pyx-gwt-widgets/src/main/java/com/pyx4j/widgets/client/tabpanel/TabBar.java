/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 22, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.tabpanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.ResizableWidget;
import com.google.gwt.widgetideas.client.ResizableWidgetCollection;

import com.pyx4j.widgets.client.WidgetsImageBundle;
import com.pyx4j.widgets.client.ImageFactory;

public class TabBar extends Composite implements ClickHandler, ResizableWidget {

    private static final String FIRST_TAB_DEPENDENT_STYLE = "first";

    private final WidgetsImageBundle images = ImageFactory.getImages();

    private final HorizontalPanel tabBarPanel;

    private final HorizontalPanel tabsPanel;

    private final SimplePanel scrollPanel;

    private final SimplePanel scrollContainer;

    private final TabPanelModel tabPanel;

    private TabBarItem selectedTab;

    private final Image moveLeft;

    private final Image moveRight;

    private int tabScrollPosition = 0;

    /**
     * Creates an empty tab bar.
     */
    public TabBar(TabPanelModel tabPanel) {
        this.tabPanel = tabPanel;

        tabBarPanel = new HorizontalPanel();

        initWidget(tabBarPanel);
        sinkEvents(Event.ONCLICK);
        setStyleName("gwt-TabBar");

        tabBarPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);

        moveLeft = new Image(images.moveTabbarEmpty());
        moveLeft.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                tabScrollPosition = setTabScrollPosition(tabScrollPosition + 50);
            }
        });
        moveRight = new Image(images.moveTabbarEmpty());
        moveRight.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                tabScrollPosition = setTabScrollPosition(tabScrollPosition - 50);
            }
        });

        moveLeft.setStyleName("gwt-TabBarMoveLeft");
        moveRight.setStyleName("gwt-TabBarMoveRight");

        tabBarPanel.add(moveLeft);

        tabsPanel = new HorizontalPanel();

        scrollPanel = new SimplePanel();
        scrollPanel.add(tabsPanel);
        DOM.setStyleAttribute(scrollPanel.getElement(), "overflow", "hidden");
        DOM.setStyleAttribute(scrollPanel.getElement(), "position", "absolute");
        DOM.setStyleAttribute(scrollPanel.getElement(), "top", "0px");
        DOM.setStyleAttribute(scrollPanel.getElement(), "left", "0px");

        scrollContainer = new SimplePanel();
        DOM.setStyleAttribute(scrollContainer.getElement(), "overflow", "hidden");
        DOM.setStyleAttribute(scrollContainer.getElement(), "position", "relative");
        scrollContainer.setWidth("100%");
        scrollContainer.setHeight("100%");
        scrollContainer.setHeight("1.6em");

        scrollContainer.add(scrollPanel);

        tabBarPanel.add(scrollContainer);
        DOM.setStyleAttribute(tabBarPanel.getElement(), "position", "relative");
        tabBarPanel.setCellWidth(scrollContainer, "100%");
        tabBarPanel.setCellHeight(scrollContainer, "100%");

        tabBarPanel.add(moveRight);

        tabBarPanel.setCellHeight(moveLeft, "100%");
        tabBarPanel.setCellHeight(moveRight, "100%");
        tabBarPanel.setCellVerticalAlignment(moveLeft, HasVerticalAlignment.ALIGN_TOP);
        tabBarPanel.setCellVerticalAlignment(moveRight, HasVerticalAlignment.ALIGN_TOP);

    }

    private int setTabScrollPosition(int position) {
        int newPosition = position;
        if (newPosition >= 0) {
            newPosition = 0;
        } else if (newPosition < (scrollContainer.getOffsetWidth() - scrollPanel.getOffsetWidth())) {
            newPosition = scrollContainer.getOffsetWidth() - scrollPanel.getOffsetWidth();
        }
        DOM.setStyleAttribute(scrollPanel.getElement(), "left", newPosition + "px");
        return newPosition;
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
        return tabsPanel.getWidgetIndex(selectedTab);
    }

    /**
     * Gets the number of tabs present.
     * 
     * @return the tab count
     */
    public int getTabCount() {
        return tabsPanel.getWidgetCount();
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
        return ((HTML) tabsPanel.getWidget(index)).getHTML();
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

        TabBarItem item = new TabBarItem(this, label, imageResource, closable);

        if (beforeIndex == 0) {
            if (tabsPanel.getWidgetCount() > 0) {
                Widget firstTab = tabsPanel.getWidget(0);
                firstTab.removeStyleDependentName(FIRST_TAB_DEPENDENT_STYLE);
            }
            item.addStyleDependentName(FIRST_TAB_DEPENDENT_STYLE);
        }

        tabsPanel.insert(item, beforeIndex);

        adjustScrollPanelWidth();
    }

    public void setLabelText(int index, String labelText) {
        Widget widget = tabsPanel.getWidget(index);
        if (labelText == null || labelText.trim().length() == 0) {
            labelText = "___";
        }
        ((TabBarItem) widget).setLabel(labelText);
    }

    public void setModifyed(int index, boolean modifyed) {
        Widget widget = tabsPanel.getWidget(index);
        ((TabBarItem) widget).setModifyed(modifyed);
    }

    @Override
    public void onClick(ClickEvent event) {
        for (int i = 0; i < tabsPanel.getWidgetCount(); ++i) {

            TabBarItem tabBarItem = getTabBarItemParent((Widget) event.getSource());
            if (tabsPanel.getWidget(i) == tabBarItem) {
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

        Widget toRemove = tabsPanel.getWidget(index);

        if (index == 0) {
            toRemove.removeStyleDependentName(FIRST_TAB_DEPENDENT_STYLE);
            if (tabsPanel.getWidgetCount() > 1) {
                Widget nextFirstTab = tabsPanel.getWidget(1);
                nextFirstTab.addStyleDependentName(FIRST_TAB_DEPENDENT_STYLE);
            }
        }

        if (toRemove == selectedTab) {
            selectedTab = null;
        }
        tabsPanel.remove(toRemove);

        adjustScrollPanelWidth();
    }

    public void selectTab(int index) {
        setSelected(selectedTab, false);
        selectedTab = (TabBarItem) tabsPanel.getWidget(index);
        setSelected(selectedTab, true);
    }

    public void enableTab(int index, boolean isEnabled) {
        TabBarItem tab = (TabBarItem) tabsPanel.getWidget(index);
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

    HorizontalPanel getTabBarPanel() {
        return tabsPanel;
    }

    TabPanelModel getTabPanelModel() {
        return tabPanel;
    }

    private void adjustScrollPanelWidth() {
        if (scrollContainer.getOffsetWidth() > 0) {
            if (scrollPanel.getOffsetWidth() > scrollContainer.getOffsetWidth()) {
                moveLeft.setResource(images.moveTabbarLeft());
                moveRight.setResource(images.moveTabbarRight());
                DOM.setStyleAttribute(moveLeft.getElement(), "cursor", "pointer");
                DOM.setStyleAttribute(moveLeft.getElement(), "cursor", "hand");
                DOM.setStyleAttribute(moveRight.getElement(), "cursor", "pointer");
                DOM.setStyleAttribute(moveRight.getElement(), "cursor", "hand");
            } else if (scrollPanel.getOffsetWidth() < scrollContainer.getOffsetWidth()) {
                moveLeft.setResource(images.moveTabbarEmpty());
                moveRight.setResource(images.moveTabbarEmpty());
                DOM.setStyleAttribute(moveLeft.getElement(), "cursor", "default");
                DOM.setStyleAttribute(moveRight.getElement(), "cursor", "default");
            }
            tabScrollPosition = setTabScrollPosition(tabScrollPosition);
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        ResizableWidgetCollection.get().add(this);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        ResizableWidgetCollection.get().remove(this);
    }

    @Override
    public void onResize(int width, int height) {
        adjustScrollPanelWidth();
    }

}