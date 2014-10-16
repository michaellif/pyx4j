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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.gwt.commons.layout.LayoutChangeEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeHandler;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.domain.tenant.CustomerPreferencesPortalHidable;
import com.propertyvista.portal.resident.ui.utils.PortalHidablePreferenceManager;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;
import com.propertyvista.portal.shared.ui.AppPlaceMenuItem;
import com.propertyvista.portal.shared.ui.MenuItem;
import com.propertyvista.portal.shared.ui.MenuList;
import com.propertyvista.portal.shared.ui.NotNavigableMenuItem;
import com.propertyvista.shared.config.VistaFeatures;

public class PortalMenuViewImpl extends DockPanel implements PortalMenuView {
    private static final I18n i18n = I18n.get(PortalMenuViewImpl.class);

    private final HeaderHolder headerHolder;

    private final MenuList<MenuItem<?>> mainHolder;

    private final MenuList<MenuItem<?>> footerHolder;

    private final MenuItem<?> leaseSelectionMenu;

    private final MenuItem<?> showGettingStartedGadgetMenu;

    private boolean mainMenuVisible = true;

    public PortalMenuViewImpl() {
        setStyleName(PortalRootPaneTheme.StyleName.MainMenu.name());

        headerHolder = new HeaderHolder();
        mainHolder = new MenuList<>();
        footerHolder = new MenuList<>();
        footerHolder.asWidget().setStyleName(PortalRootPaneTheme.StyleName.MainMenuFooter.name());

        add(headerHolder, DockPanel.NORTH);
        setCellHeight(headerHolder, "1px");
        add(mainHolder, DockPanel.CENTER);
        add(footerHolder, DockPanel.SOUTH);
        setCellHeight(footerHolder, "1px");

        mainHolder.addMenuItem(new AppPlaceMenuItem(new ResidentPortalSiteMap.Dashboard(), PortalImages.INSTANCE.dashboardMenu(), ThemeColor.contrast1));

        mainHolder.addMenuItem(new AppPlaceMenuItem(new ResidentPortalSiteMap.Financial(), PortalImages.INSTANCE.billingMenu(), ThemeColor.contrast4));

        if (SecurityController.check(PortalResidentBehavior.Resident)) {

            mainHolder
                    .addMenuItem(new AppPlaceMenuItem(new ResidentPortalSiteMap.Maintenance(), PortalImages.INSTANCE.maintenanceMenu(), ThemeColor.contrast5));

            if (VistaFeatures.instance().countryOfOperation() == CountryOfOperation.Canada) {
                mainHolder.addMenuItem(new AppPlaceMenuItem(new ResidentPortalSiteMap.ResidentServices(), PortalImages.INSTANCE.residentServicesMenu(),
                        ThemeColor.contrast3));
            }

        }

        mainHolder.addMenuItem(new AppPlaceMenuItem(new ResidentPortalSiteMap.Offers(), PortalImages.INSTANCE.offersMenu(), ThemeColor.contrast6));

        footerHolder.addMenuItem(new AppPlaceMenuItem(new ResidentPortalSiteMap.Profile(), PortalImages.INSTANCE.profileMenu(), ThemeColor.formBackground));
        footerHolder.addMenuItem(new AppPlaceMenuItem(new ResidentPortalSiteMap.Account(), PortalImages.INSTANCE.accountMenu(), ThemeColor.formBackground));

        leaseSelectionMenu = new AppPlaceMenuItem(new ResidentPortalSiteMap.LeaseContextSelection(), PortalImages.INSTANCE.selectMenu(), ThemeColor.formBackground);
        footerHolder.addMenuItem(leaseSelectionMenu);

        showGettingStartedGadgetMenu = new NotNavigableMenuItem(i18n.tr("Show Getting Started"), PortalImages.INSTANCE.dashboardMenu(), ThemeColor.formBackground,
                new Command() {
                    @Override
                    public void execute() {
                        PortalHidablePreferenceManager.updatePreference(CustomerPreferencesPortalHidable.Type.GettingStartedGadget, false);
                    }
                });
        footerHolder.addMenuItem(showGettingStartedGadgetMenu);

        footerHolder.addMenuItem(new AppPlaceMenuItem(new PortalSiteMap.Logout(), PortalImages.INSTANCE.logoutMenu(), ThemeColor.formBackground));

        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout(event.getLayoutType());
            }

        });
    }

    @Override
    public void setMenuVisible(boolean visible) {
        mainMenuVisible = visible;
        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));
    }

    @Override
    public void setPresenter(PortalMenuPresenter presenter) {
        AppPlace currentPlace = AppSite.getPlaceController().getWhere();
        for (MenuItem<?> item : mainHolder.getMenuItems()) {
            if (item instanceof AppPlaceMenuItem) {
                item.setSelected(currentPlace.getPlaceId().contains(((AppPlaceMenuItem) item).getPlace().getPlaceId()));
            }
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
            mainHolder.setVisible(mainMenuVisible);
            setVisible(true);
            break;
        case tabletPortrait:
        case tabletLandscape:
            removeStyleDependentName(PortalRootPaneTheme.StyleDependent.sideMenu.name());
            addStyleDependentName(PortalRootPaneTheme.StyleDependent.collapsedMenu.name());
            footerHolder.setVisible(false);
            headerHolder.setVisible(false);
            mainHolder.setVisible(true);
            setVisible(mainMenuVisible);
            break;
        case monitor:
        case huge:
            removeStyleDependentName(PortalRootPaneTheme.StyleDependent.sideMenu.name());
            removeStyleDependentName(PortalRootPaneTheme.StyleDependent.collapsedMenu.name());
            footerHolder.setVisible(false);
            headerHolder.setVisible(false);
            mainHolder.setVisible(true);
            setVisible(mainMenuVisible);
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

    @Override
    public void setGettingStartedVisible(boolean visible) {
        showGettingStartedGadgetMenu.setVisible(visible);
    }

}
