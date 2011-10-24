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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

public class NavigViewImpl extends SimplePanel implements NavigView {

    public static String DEFAULT_STYLE_PREFIX = "MainMenu";

    public static enum StyleSuffix implements IStyleName {
        Holder, Tab, Label
    }

    public static enum StyleDependent implements IStyleDependent {
        hover, current
    }

    private NavigPresenter presenter;

    private final NavigTabList tabsHolder;

    private final VerticalPanel menuContainer;

    public NavigViewImpl() {
        setStyleName(DEFAULT_STYLE_PREFIX);
        setSize("100%", "100%");
        menuContainer = new VerticalPanel();
        menuContainer.setSize("100%", "100%");
        tabsHolder = new NavigTabList();
        menuContainer.add(tabsHolder);
        setWidget(menuContainer);
    }

    @Override
    public void setPresenter(NavigPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setNavig(List<AppPlace> items) {
        tabsHolder.clear();
        for (AppPlace item : items) {
            NavigTab navigTab = new NavigTab(item, DEFAULT_STYLE_PREFIX);
            tabsHolder.add(navigTab);

            AppPlace currentPlace = (AppPlace) presenter.getWhere();
            if (item.equals(currentPlace)) {
                navigTab.select();
            }
        }
    }

    class NavigTabList extends ComplexPanel {
        private final List<NavigTab> tabs;

        public NavigTabList() {
            setElement(DOM.createElement("ul"));
            tabs = new LinkedList<NavigViewImpl.NavigTab>();
            setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Holder.name());
            setActive(true);

        }

        @Override
        public void add(Widget w) {
            NavigTab tab = (NavigTab) w;
            tabs.add(tab);
            super.add(w, getElement());
        }

        public void setActive(boolean active) {
            this.setVisible(active);
        }

        public List<NavigTab> getTabs() {
            return tabs;
        }

        public NavigTab getTabByPlace(Place place) {
            if (tabs == null || place == null)
                return null;
            for (NavigTab tab : tabs) {
                if (tab.getPlace().equals(place)) {
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

        private final Label label;

        private boolean selected;

        private final AppPlace place;

        NavigTab(AppPlace appPlace, String styleName) {
            super();
            if (styleName == null) {
                styleName = DEFAULT_STYLE_PREFIX;
            }

            this.place = appPlace;
            selected = false;

            setElement(DOM.createElement("li"));
            setStyleName(styleName + StyleSuffix.Tab.name());

            getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
            sinkEvents(Event.ONCLICK);

            label = new Label(AppSite.getHistoryMapper().getPlaceInfo(place).getNavigLabel());
            label.setStyleName(styleName + StyleSuffix.Label.name());
            add(label);

            addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.navigTo(place);
                }
            }, ClickEvent.getType());
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

        public Label getLabel() {
            return label;
        }

        @Override
        public void add(Widget w) {
            super.add(w, getElement());
        }

        public boolean isSelected() {
            return selected;
        }

        public AppPlace getPlace() {
            return place;
        }

    }

    @Override
    public void changePlace(AppPlace place) {
        NavigTab mainTag = tabsHolder.getTabByPlace(place);
        NavigTab selectedTab = tabsHolder.getSelectedTab();

        if (mainTag != null) {//main navig tab
            if (selectedTab != null) {
                selectedTab.deselect();
            }
            mainTag.select();
        }
    }
}
