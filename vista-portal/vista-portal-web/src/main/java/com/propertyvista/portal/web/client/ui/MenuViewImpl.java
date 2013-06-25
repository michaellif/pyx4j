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
package com.propertyvista.portal.web.client.ui;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.IconButton;
import com.pyx4j.widgets.client.images.IconButtonImages;

import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.themes.PortalWebRootPaneTheme;

public class MenuViewImpl extends SimplePanel implements MenuView {

    private MenuPresenter presenter;

    private final NavigTabList tabsHolder;

    public MenuViewImpl() {
        setStyleName(PortalWebRootPaneTheme.StyleName.MainMenu.name());
        tabsHolder = new NavigTabList();
        setWidget(tabsHolder);

        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout(event.getLayoutType());
            }

        });
    }

    @Override
    public void setPresenter(MenuPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setNavig(List<AppPlace> items) {
        tabsHolder.clear();
        for (AppPlace item : items) {
            NavigTab navigTab = new NavigTab(item, PortalImages.INSTANCE.dashboardButton());
            tabsHolder.add(navigTab);

            AppPlace currentPlace = presenter.getWhere();
            if (item.equals(currentPlace)) {
                navigTab.select();
            }
        }
    }

    private void doLayout(LayoutType layoutType) {
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
            addStyleDependentName(PortalWebRootPaneTheme.StyleDependent.sideMenu.name());
            removeStyleDependentName(PortalWebRootPaneTheme.StyleDependent.collapsedMenu.name());
            break;
        case tabletPortrait:
        case tabletLandscape:
            removeStyleDependentName(PortalWebRootPaneTheme.StyleDependent.sideMenu.name());
            addStyleDependentName(PortalWebRootPaneTheme.StyleDependent.collapsedMenu.name());
            break;
        default:
            removeStyleDependentName(PortalWebRootPaneTheme.StyleDependent.sideMenu.name());
            removeStyleDependentName(PortalWebRootPaneTheme.StyleDependent.collapsedMenu.name());
            break;
        }
    }

    class NavigTabList extends ComplexPanel {
        private final List<NavigTab> tabs;

        public NavigTabList() {
            setElement(DOM.createElement("ul"));
            tabs = new LinkedList<MenuViewImpl.NavigTab>();
            setStyleName(PortalWebRootPaneTheme.StyleName.MainMenuHolder.name());
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

        private final IconButton icon;

        private final Label label;

        private boolean selected;

        private final AppPlace place;

        NavigTab(AppPlace appPlace, IconButtonImages images) {
            super();

            this.place = appPlace;
            selected = false;

            setElement(DOM.createElement("li"));
            setStyleName(PortalWebRootPaneTheme.StyleName.MainMenuTab.name());

            sinkEvents(Event.ONCLICK);

            icon = new IconButton(null, images);

            icon.setStyleName(PortalWebRootPaneTheme.StyleName.MainMenuIcon.name());
            add(icon);

            label = new Label(AppSite.getHistoryMapper().getPlaceInfo(place).getNavigLabel());
            label.setStyleName(PortalWebRootPaneTheme.StyleName.MainMenuLabel.name());
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
            removeStyleDependentName(PortalWebRootPaneTheme.StyleDependent.active.name());
        }

        public void select() {
            addStyleDependentName(PortalWebRootPaneTheme.StyleDependent.active.name());
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

}
