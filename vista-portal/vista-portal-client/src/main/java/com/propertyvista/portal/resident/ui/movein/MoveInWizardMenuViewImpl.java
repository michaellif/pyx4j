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
package com.propertyvista.portal.resident.ui.movein;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
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
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.resident.ui.WizardStepItem;
import com.propertyvista.portal.resident.ui.WizardStepList;
import com.propertyvista.portal.resident.ui.WizardStepItem.StepStatus;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;
import com.propertyvista.portal.shared.ui.AppPlaceMenuItem;
import com.propertyvista.portal.shared.ui.MenuItem;
import com.propertyvista.portal.shared.ui.MenuList;

public class MoveInWizardMenuViewImpl extends DockPanel implements MoveInWizardMenuView {

    private static final I18n i18n = I18n.get(MoveInWizardMenuViewImpl.class);

    private final HeaderHolder headerHolder;

    private final WizardStepList mainHolder;

    private final MenuList<MenuItem<?>> footerHolder;

    private boolean mainMenuVisible = true;

    public MoveInWizardMenuViewImpl() {
        setStyleName(PortalRootPaneTheme.StyleName.MainMenu.name());

        headerHolder = new HeaderHolder();
        mainHolder = new WizardStepList();
        footerHolder = new MenuList<>();
        footerHolder.asWidget().setStyleName(PortalRootPaneTheme.StyleName.MainMenuFooter.name());

        add(headerHolder, DockPanel.NORTH);
        setCellHeight(headerHolder, "1px");
        add(mainHolder, DockPanel.CENTER);
        add(footerHolder, DockPanel.SOUTH);
        setCellHeight(footerHolder, "1px");

        mainHolder.addMenuItem(new WizardStepItem(i18n.tr("Lease Signing"), null, 0, StepStatus.notComplete));

        mainHolder.addMenuItem(new WizardStepItem(i18n.tr("PAP"), null, 1, StepStatus.notComplete));

        mainHolder.addMenuItem(new WizardStepItem(i18n.tr("Insurance"), null, 2, StepStatus.notComplete));

        mainHolder.addMenuItem(new WizardStepItem(i18n.tr("Profile Update"), null, 3, StepStatus.notComplete));

        footerHolder.addMenuItem(new AppPlaceMenuItem(new ResidentPortalSiteMap.Profile(), PortalImages.INSTANCE.profileMenu(), ThemeColor.background));
        footerHolder.addMenuItem(new AppPlaceMenuItem(new ResidentPortalSiteMap.Account(), PortalImages.INSTANCE.accountMenu(), ThemeColor.background));

        footerHolder.addMenuItem(new AppPlaceMenuItem(new PortalSiteMap.Logout(), PortalImages.INSTANCE.logoutMenu(), ThemeColor.background));

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
    public void setPresenter(MoveInWizardMenuPresenter presenter) {
        AppPlace currentPlace = AppSite.getPlaceController().getWhere();
        for (MenuItem<?> item : mainHolder.getMenuItems()) {
            if (item instanceof AppPlaceMenuItem) {
                item.setSelected(currentPlace.getPlaceId().contains(((AppPlaceMenuItem) item).getPlace().getPlaceId()));
            }
        }
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

}
