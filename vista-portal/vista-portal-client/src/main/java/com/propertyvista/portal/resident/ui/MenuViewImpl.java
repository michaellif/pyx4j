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
package com.propertyvista.portal.resident.ui;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;
import com.propertyvista.portal.shared.ui.MenuItem;
import com.propertyvista.portal.shared.ui.MenuList;
import com.propertyvista.shared.config.VistaFeatures;

public class MenuViewImpl extends DockPanel implements MenuView {

    private static final I18n i18n = I18n.get(MenuViewImpl.class);

    private MenuPresenter presenter;

    private final HeaderHolder headerHolder;

    private final MenuList mainHolder;

    private final MenuList footerHolder;

    private final MenuItem leaseSelectionMenu;

    public MenuViewImpl() {
        setStyleName(PortalRootPaneTheme.StyleName.MainMenu.name());

        headerHolder = new HeaderHolder();
        mainHolder = new MenuList();
        footerHolder = new MenuList();
        footerHolder.asWidget().setStyleName(PortalRootPaneTheme.StyleName.MainMenuFooter.name());

        add(headerHolder, DockPanel.NORTH);
        setCellHeight(headerHolder, "1px");
        add(mainHolder, DockPanel.CENTER);
        add(footerHolder, DockPanel.SOUTH);
        setCellHeight(footerHolder, "1px");

        mainHolder.addMenuItem(new MenuItem(new ResidentPortalSiteMap.Dashboard(), PortalImages.INSTANCE.dashboardMenu(), ThemeColor.contrast1));

        mainHolder.addMenuItem(new MenuItem(new ResidentPortalSiteMap.Financial(), PortalImages.INSTANCE.billingMenu(), ThemeColor.contrast4));

        mainHolder.addMenuItem(new MenuItem(new ResidentPortalSiteMap.Maintenance(), PortalImages.INSTANCE.maintenanceMenu(), ThemeColor.contrast5));
        if (VistaTODO.ENABLE_COMMUNCATION_CENTER) {
            mainHolder.addMenuItem(new MenuItem(new ResidentPortalSiteMap.CommunicationCenter(), PortalImages.INSTANCE.dashboardMenu(), ThemeColor.contrast6));
        }

        if (VistaFeatures.instance().countryOfOperation() == CountryOfOperation.Canada) {
            mainHolder.addMenuItem(new MenuItem(new ResidentPortalSiteMap.ResidentServices(), PortalImages.INSTANCE.residentServicesMenu(),
                    ThemeColor.contrast3));
        }

        mainHolder.addMenuItem(new MenuItem(new ResidentPortalSiteMap.Offers(), PortalImages.INSTANCE.offersMenu(), ThemeColor.contrast6));

        footerHolder.addMenuItem(new MenuItem(new ResidentPortalSiteMap.Profile(), PortalImages.INSTANCE.profileMenu(), ThemeColor.background));
        footerHolder.addMenuItem(new MenuItem(new ResidentPortalSiteMap.Account(), PortalImages.INSTANCE.accountMenu(), ThemeColor.background));

        leaseSelectionMenu = new MenuItem(new ResidentPortalSiteMap.LeaseContextSelection(), PortalImages.INSTANCE.accountMenu(), ThemeColor.background);
        footerHolder.addMenuItem(leaseSelectionMenu);

        footerHolder.addMenuItem(new MenuItem(new PortalSiteMap.Logout(), PortalImages.INSTANCE.logoutMenu(), ThemeColor.background));

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
        AppPlace currentPlace = AppSite.getPlaceController().getWhere();
        for (MenuItem item : mainHolder.getMenuItems()) {
            item.setSelected(currentPlace.getPlaceId().contains(item.getPlace().getPlaceId()));
        }
    }

    @Override
    public void setLeasesSelectorEnabled(boolean enabled) {
        leaseSelectionMenu.setVisible(enabled);
    }

    private void doLayout(LayoutType layoutType) {
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
            addStyleDependentName(PortalRootPaneTheme.StyleDependent.sideMenu.name());
            removeStyleDependentName(PortalRootPaneTheme.StyleDependent.collapsedMenu.name());
            footerHolder.setVisible(true);
            headerHolder.setVisible(true);
            break;
        case tabletPortrait:
        case tabletLandscape:
            removeStyleDependentName(PortalRootPaneTheme.StyleDependent.sideMenu.name());
            addStyleDependentName(PortalRootPaneTheme.StyleDependent.collapsedMenu.name());
            footerHolder.setVisible(false);
            headerHolder.setVisible(false);
            break;
        case monitor:
        case huge:
            removeStyleDependentName(PortalRootPaneTheme.StyleDependent.sideMenu.name());
            removeStyleDependentName(PortalRootPaneTheme.StyleDependent.collapsedMenu.name());
            footerHolder.setVisible(false);
            headerHolder.setVisible(false);
            break;
        }
    }

    @Override
    public void setUserName(String userName) {
        headerHolder.setName(userName);
    }

    class HeaderHolder extends FlowPanel {

        private final Label nameLabel;

        private final Image photoImage;

        public HeaderHolder() {

            setStyleName(PortalRootPaneTheme.StyleName.MainMenuHeader.name());

            getElement().getStyle().setPosition(Position.RELATIVE);

            photoImage = new Image(PortalImages.INSTANCE.avatar());
            photoImage.setStyleName(PortalRootPaneTheme.StyleName.MainMenuHeaderPhoto.name());
            photoImage.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

            nameLabel = new Label("Name");
            nameLabel.setStyleName(PortalRootPaneTheme.StyleName.MainMenuHeaderName.name());
            nameLabel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

            add(photoImage);
            add(nameLabel);

        }

        public void setName(String name) {
            nameLabel.setText(name);
        }
    }

}
