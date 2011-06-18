/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

import com.propertyvista.portal.client.activity.NavigItem;

public class MainNavigViewImpl extends SimplePanel implements MainNavigView {

    public static String DEFAULT_STYLE_PREFIX = "MainMenu";

    public static String SECONDARY_STYLE_PREFIX = "SecondaryMenu";

    public static enum StyleSuffix implements IStyleSuffix {
        Holder, Tab, LabelHolder, StatusHolder, Label
    }

    public static enum StyleDependent implements IStyleDependent {
        hover, current
    }

    private static enum MenuType {
        Main, Secondary
    }

    private MainNavigPresenter presenter;

    private final NavigTabList tabsHolder;

    private final VerticalPanel menuContainer;

    public MainNavigViewImpl() {
        setStyleName(DEFAULT_STYLE_PREFIX);
        setSize("100%", "100%");
        menuContainer = new VerticalPanel();
        menuContainer.setSize("100%", "100%");
        tabsHolder = new NavigTabList(MenuType.Main);
        menuContainer.add(tabsHolder);
        setWidget(menuContainer);
    }

    @Override
    public void setPresenter(MainNavigPresenter presenter) {
        this.presenter = presenter;
        //reset secondary menu
        if (tabsHolder != null) {
            for (NavigTab tab : tabsHolder.getTabs()) {
                NavigTabList secondaryNavig = tab.getSecondaryNavig();
                if (secondaryNavig != null) {
                    NavigTab secondSelected = secondaryNavig.getSelectedTab();
                    if (secondSelected != null) {
                        secondSelected.deselect();
                    }
                }
            }
        }

    }

    @Override
    public void setMainNavig(List<NavigItem> items) {
        Place secondarySelected = null;
        for (NavigItem item : items) {
            List<NavigItem> secondaryItems = item.getSecondaryNavigation();
            NavigTab mainNavigTab = new NavigTab(item, DEFAULT_STYLE_PREFIX);
            tabsHolder.add(mainNavigTab);

            Place currentPlace = presenter.getWhere();
            if (item.getPlace().equals(currentPlace)) {
                mainNavigTab.select();
            } else if (secondaryItems != null) {
                for (NavigItem secondary : secondaryItems) {
                    if (secondary.getPlace().equals(currentPlace)) {
                        mainNavigTab.select();
                        secondarySelected = secondary.getPlace();
                        break;
                    }
                }
            }
            if (secondaryItems != null && !secondaryItems.isEmpty()) {
                setSecondaryNavig(item.getPlace(), secondaryItems);
                if (secondarySelected != null) {
                    NavigTab secondaryTab = mainNavigTab.getSecondaryNavig().getTabByPlace(secondarySelected);
                    if (secondaryTab != null) {
                        secondaryTab.select();
                    }

                }

            }
        }
    }

    @Override
    public void setSecondaryNavig(Place mainItemPlace, List<NavigItem> secondayItems) {
        if (mainItemPlace == null) {
            return;
        }
        NavigTab mainTab = tabsHolder.getTabByPlace(mainItemPlace);
        if (mainTab != null) {
            mainTab.setSecondaryNavig(secondayItems, menuContainer);
        }
    }

    class NavigTabList extends ComplexPanel {
        private final List<NavigTab> tabs;

        private final MenuType menuType;

        private boolean active;

        private final String id;

        public NavigTabList(MenuType menuType) {
            this.menuType = menuType;
            setElement(DOM.createElement("ul"));
            tabs = new LinkedList<MainNavigViewImpl.NavigTab>();
            id = DOM.createUniqueId();
            this.getElement().setAttribute("id", id);
            if (menuType == MenuType.Secondary) {
                setStyleName(SECONDARY_STYLE_PREFIX + StyleSuffix.Holder.name());
                setActive(false);
            } else {
                setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Holder.name());
                setActive(true);
            }
        }

        @Override
        public void add(Widget w) {
            NavigTab tab = (NavigTab) w;
            tabs.add(tab);
            super.add(w, getElement());
        }

        public void setActive(boolean active) {
            this.setVisible(active);
            this.active = active;
        }

        public MenuType getMenuType() {
            return menuType;
        }

        public boolean isActive() {
            return active;
        }

        public String getId() {
            return id;
        }

        public List<NavigTab> getTabs() {
            return tabs;
        }

        public NavigTab getTabByPlace(Place place) {
            if (tabs == null || place == null)
                return null;
            for (NavigTab tab : tabs) {
                if (tab.getNavigItem().getPlace().equals(place)) {
                    return tab;
                }
            }
            return null;
        }

        public NavigTab getSelectedTab() {
            if (tabs == null)
                return null;
            for (NavigTab tab : tabs) {
                if (tab.isSelected()) {
                    return tab;
                }
            }
            return null;
        }

    }

    class NavigTab extends ComplexPanel {

        private final FlowPanel labelHolder;

        private final SimplePanel statusHolder;

        private final Label label;

        private boolean selected;

        private NavigTabList secondaryNavig;

        private final NavigItem navigItem;

        NavigTab(NavigItem menuItem, String styleName) {
            super();
            if (styleName == null) {
                styleName = DEFAULT_STYLE_PREFIX;
            }

            this.navigItem = menuItem;
            secondaryNavig = null;
            selected = false;

            String caption = menuItem.getCaption();
            final AppPlace place = menuItem.getPlace();

            setElement(DOM.createElement("li"));
            setStyleName(styleName + StyleSuffix.Tab.name());

            getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
            sinkEvents(Event.ONCLICK);

            labelHolder = new FlowPanel();
            labelHolder.setStyleName(styleName + StyleSuffix.LabelHolder.name());
            add(labelHolder);

            statusHolder = new SimplePanel();
            statusHolder.setStyleName(styleName + StyleSuffix.StatusHolder.name());
            labelHolder.add(statusHolder);

            label = new Label(caption);
            label.setStyleName(styleName + StyleSuffix.Label.name());
            statusHolder.add(label);
            getElement().getStyle().setFontWeight(FontWeight.BOLD);
            getElement().getStyle().setCursor(Cursor.DEFAULT);

            addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.navigTo(place);
                }
            }, ClickEvent.getType());
            addDomHandler(new MouseOverHandler() {
                @Override
                public void onMouseOver(MouseOverEvent event) {
                    label.getElement().getStyle().setProperty("color", "#555");
                }
            }, MouseOverEvent.getType());
            addDomHandler(new MouseOutHandler() {
                @Override
                public void onMouseOut(MouseOutEvent event) {
                    label.getElement().getStyle().setProperty("color", "#7B8388");
                }
            }, MouseOutEvent.getType());
            getElement().getStyle().setCursor(Cursor.POINTER);

        }

        public void deselect() {
            selected = false;
            label.removeStyleDependentName(StyleDependent.current.name());
        }

        public void select() {
            label.addStyleDependentName(StyleDependent.current.name());
            selected = true;
        }

        @Override
        public void add(Widget w) {
            super.add(w, getElement());
        }

        public boolean isSelected() {
            return selected;
        }

        public NavigItem getNavigItem() {
            return navigItem;
        }

        public NavigTabList getSecondaryNavig() {
            return secondaryNavig;
        }

        public void setSecondaryNavig(List<NavigItem> secondaryItems) {
            if (secondaryNavig != null) {
                secondaryNavig.removeFromParent();
                secondaryNavig = null;
            }
            if (secondaryItems != null && !secondaryItems.isEmpty()) {
                secondaryNavig = new NavigTabList(MenuType.Secondary);
                secondaryNavig.setActive(isSelected());

                for (NavigItem secondaryItem : secondaryItems) {
                    secondaryNavig.add(new NavigTab(secondaryItem, SECONDARY_STYLE_PREFIX));
                }
            }
        }

        public void setSecondaryNavig(List<NavigItem> secondaryItems, HasWidgets parent) {
            setSecondaryNavig(secondaryItems);
            if (parent != null && secondaryNavig != null) {
                parent.add(secondaryNavig);
            }
        }
    }

    @Override
    public void changePlace(Place place) {
        NavigTab mainTag = tabsHolder.getTabByPlace(place);
        NavigTab selectedTab = tabsHolder.getSelectedTab();

        if (mainTag != null) {//main navig tab
            if (selectedTab != null) {
                selectedTab.deselect();
                if (selectedTab.getSecondaryNavig() != null) {
                    selectedTab.getSecondaryNavig().setActive(false);
                }
            }
            mainTag.select();
            if (mainTag.getSecondaryNavig() != null) {
                mainTag.getSecondaryNavig().setActive(true);
            }
        } else {//secondary navig tab
            if (selectedTab != null) {
                NavigTabList secondaryNavig = selectedTab.getSecondaryNavig();
                if (secondaryNavig != null) {
                    NavigTab secondaryTab = secondaryNavig.getTabByPlace(place);
                    NavigTab secondarySelectedTab = secondaryNavig.getSelectedTab();
                    if (secondarySelectedTab != null) {
                        secondarySelectedTab.deselect();
                    }
                    if (secondaryTab != null) {
                        secondaryTab.select();
                    }
                }

            }
        }
    }
}
