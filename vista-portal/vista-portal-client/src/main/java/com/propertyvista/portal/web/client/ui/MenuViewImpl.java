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
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRequestEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.images.ButtonImages;

import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Resident;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.themes.PortalWebRootPaneTheme;
import com.propertyvista.shared.config.VistaFeatures;

public class MenuViewImpl extends DockPanel implements MenuView {

    private static final I18n i18n = I18n.get(MenuViewImpl.class);

    private MenuPresenter presenter;

    private final HeaderHolder headerHolder;

    private final NavigItemList mainHolder;

    private final NavigItemList footerHolder;

    public MenuViewImpl() {
        setStyleName(PortalWebRootPaneTheme.StyleName.MainMenu.name());

        headerHolder = new HeaderHolder();
        mainHolder = new NavigItemList();
        footerHolder = new NavigItemList();
        footerHolder.setStyleName(PortalWebRootPaneTheme.StyleName.MainMenuFooter.name());

        add(headerHolder, DockPanel.NORTH);
        setCellHeight(headerHolder, "1px");
        add(mainHolder, DockPanel.CENTER);
        add(footerHolder, DockPanel.SOUTH);
        setCellHeight(footerHolder, "1px");

        mainHolder.add(new NavigItem(new Resident.Dashboard(), PortalImages.INSTANCE.dashboardMenu(), ThemeColor.contrast1));

        mainHolder.add(new NavigItem(new Resident.Financial(), PortalImages.INSTANCE.billingMenu(), ThemeColor.contrast4));

        mainHolder.add(new NavigItem(new Resident.Maintenance(), PortalImages.INSTANCE.maintenanceMenu(), ThemeColor.contrast5));
        if (VistaTODO.ENABLE_COMMUNCATION_CENTER) {
            mainHolder.add(new NavigItem(new Resident.CommunicationCenter(), PortalImages.INSTANCE.dashboardMenu(), ThemeColor.contrast6));
        }

        if (VistaFeatures.instance().countryOfOperation() == CountryOfOperation.Canada) {
            mainHolder.add(new NavigItem(new Resident.ResidentServices(), PortalImages.INSTANCE.residentServicesMenu(), ThemeColor.contrast3));
        }

        mainHolder.add(new NavigItem(new Resident.Offers(), PortalImages.INSTANCE.offersMenu(), ThemeColor.contrast6));

//TODO Move to Toolbar        
//        if (SecurityController.checkBehavior(VistaCustomerBehavior.HasMultipleLeases)) {
//            mainHolder.add(new NavigItem(new PortalSiteMap.LeaseContextSelection(), PortalImages.INSTANCE.dashboardMenu(), ThemeColor.contrast5));
//        }

        footerHolder.add(new NavigItem(new Resident.Profile(), PortalImages.INSTANCE.profileMenu(), ThemeColor.background));
        footerHolder.add(new NavigItem(new PortalSiteMap.Resident.Account(), PortalImages.INSTANCE.accountMenu(), ThemeColor.background));

        footerHolder.add(new NavigItem(new Command() {

            @Override
            public void execute() {
                presenter.logout();
            }
        }, i18n.tr("Logout"), PortalImages.INSTANCE.logoutMenu(), ThemeColor.background));

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
        AppPlace currentPlace = presenter.getWhere();
        for (NavigItem item : mainHolder.items) {
            item.setSelected(currentPlace.getPlaceId().contains(item.getPlace().getPlaceId()));
        }
    }

    private void doLayout(LayoutType layoutType) {
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
            addStyleDependentName(PortalWebRootPaneTheme.StyleDependent.sideMenu.name());
            removeStyleDependentName(PortalWebRootPaneTheme.StyleDependent.collapsedMenu.name());
            footerHolder.setVisible(true);
            headerHolder.setVisible(true);
            break;
        case tabletPortrait:
        case tabletLandscape:
            removeStyleDependentName(PortalWebRootPaneTheme.StyleDependent.sideMenu.name());
            addStyleDependentName(PortalWebRootPaneTheme.StyleDependent.collapsedMenu.name());
            footerHolder.setVisible(false);
            headerHolder.setVisible(false);
            break;
        case monitor:
        case huge:
            removeStyleDependentName(PortalWebRootPaneTheme.StyleDependent.sideMenu.name());
            removeStyleDependentName(PortalWebRootPaneTheme.StyleDependent.collapsedMenu.name());
            footerHolder.setVisible(false);
            headerHolder.setVisible(false);
            break;
        }
    }

    @Override
    public void onLogedOut() {
        headerHolder.setName(null);
    }

    @Override
    public void onLogedIn(String userName) {
        headerHolder.setName(userName);
    }

    class NavigItemList extends ComplexPanel {
        private final List<NavigItem> items;

        public NavigItemList() {
            setElement(DOM.createElement("ul"));
            items = new LinkedList<MenuViewImpl.NavigItem>();
            setStyleName(PortalWebRootPaneTheme.StyleName.MainMenuHolder.name());
            setActive(true);

        }

        @Override
        public void add(Widget w) {
            NavigItem item = (NavigItem) w;
            items.add(item);
            super.add(w, getElement());
        }

        public void setActive(boolean active) {
            this.setVisible(active);
        }

        public List<NavigItem> getNavigItems() {
            return items;
        }

        public NavigItem getNavigItem(Place place) {
            if (items == null || place == null)
                return null;
            for (NavigItem item : items) {
                if (item.getPlace().equals(place)) {
                    return item;
                }
            }
            return null;
        }

        public NavigItem getSelectedNavigItem() {
            if (items == null)
                return null;
            for (NavigItem item : items) {
                if (item.isSelected()) {
                    return item;
                }
            }
            return null;
        }

    }

    class NavigItem extends ComplexPanel {

        private final Image icon;

        private final Label label;

        private boolean selected;

        private AppPlace place;

        private final ButtonImages images;

        private final String color;

        NavigItem(final Command command, String labelString, ButtonImages images, ThemeColor color) {
            super();

            this.images = images;
            this.color = StyleManager.getPalette().getThemeColor(color, 1);
            selected = false;

            setElement(DOM.createElement("li"));
            setStyleName(PortalWebRootPaneTheme.StyleName.MainMenuNavigItem.name());

            sinkEvents(Event.ONCLICK);

            icon = new Image(images.regular());

            icon.setStyleName(PortalWebRootPaneTheme.StyleName.MainMenuIcon.name());
            add(icon);

            label = new Label(labelString);
            label.setStyleName(PortalWebRootPaneTheme.StyleName.MainMenuLabel.name());
            add(label);

            addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    command.execute();
                    LayoutType layout = LayoutType.getLayoutType(Window.getClientWidth());
                    if (LayoutType.phonePortrait.equals(layout) || (LayoutType.phoneLandscape.equals(layout))) {
                        AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.toggleSideMenu));
                    }
                }
            }, ClickEvent.getType());

            getElement().getStyle().setCursor(Cursor.POINTER);

        }

        NavigItem(final AppPlace appPlace, ButtonImages images, ThemeColor color) {
            this(new Command() {

                @Override
                public void execute() {
                    presenter.navigTo(appPlace);
                }
            }, AppSite.getHistoryMapper().getPlaceInfo(appPlace).getNavigLabel(), images, color);
            this.place = appPlace;
        }

        public void setSelected(boolean select) {
            selected = select;
            if (select) {
                addStyleDependentName(PortalWebRootPaneTheme.StyleDependent.active.name());
                getElement().getStyle().setProperty("background", color);
                label.getElement().getStyle().setProperty("background", color);
                icon.setResource(images.active());
            } else {
                removeStyleDependentName(PortalWebRootPaneTheme.StyleDependent.active.name());
                getElement().getStyle().setProperty("background", "");
                label.getElement().getStyle().setProperty("background", "");
                icon.setResource(images.regular());
            }
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

    class HeaderHolder extends FlowPanel {

        private final Label nameLabel;

        private final Image photoImage;

        public HeaderHolder() {

            setStyleName(PortalWebRootPaneTheme.StyleName.MainMenuHeader.name());

            getElement().getStyle().setPosition(Position.RELATIVE);

            photoImage = new Image(PortalImages.INSTANCE.avatar());
            photoImage.setStyleName(PortalWebRootPaneTheme.StyleName.MainMenuHeaderPhoto.name());
            photoImage.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

            nameLabel = new Label("Name");
            nameLabel.setStyleName(PortalWebRootPaneTheme.StyleName.MainMenuHeaderName.name());
            nameLabel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

            add(photoImage);
            add(nameLabel);

        }

        public void setName(String name) {
            nameLabel.setText(name);
        }
    }

}
