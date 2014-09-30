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
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.resident.activity.movein.MoveInWizardManager;
import com.propertyvista.portal.resident.ui.movein.MoveInWizardStepMenuItem.StepStatus;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.MoveInWizardStep;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;
import com.propertyvista.portal.shared.ui.AppPlaceMenuItem;
import com.propertyvista.portal.shared.ui.MenuItem;
import com.propertyvista.portal.shared.ui.MenuList;

public class MoveInWizardMenuViewImpl extends DockPanel implements MoveInWizardMenuView {

    private final HeaderHolder headerHolder;

    private final MoveInWizardStepMenuList mainHolder;

    private final MenuList<MenuItem<?>> footerHolder;

    private boolean mainMenuVisible = true;

    public MoveInWizardMenuViewImpl() {
        setStyleName(PortalRootPaneTheme.StyleName.MainMenu.name());

        headerHolder = new HeaderHolder();
        mainHolder = new MoveInWizardStepMenuList();
        footerHolder = new MenuList<>();
        footerHolder.asWidget().setStyleName(PortalRootPaneTheme.StyleName.MainMenuFooter.name());

        add(headerHolder, DockPanel.NORTH);
        setCellHeight(headerHolder, "1px");
        add(mainHolder, DockPanel.CENTER);
        add(footerHolder, DockPanel.SOUTH);
        setCellHeight(footerHolder, "1px");

        for (int i = 0; i < MoveInWizardStep.values().length; i++) {
            MoveInWizardStep step = MoveInWizardStep.values()[i];

            ThemeColor color = ThemeColor.contrast2;
            switch (step) {
            case leaseSigning:
                color = ThemeColor.contrast2;
                break;
            case pap:
                color = ThemeColor.contrast4;
                break;
            case insurance:
                color = ThemeColor.contrast3;
                break;

            }

            mainHolder.addStepItem(step, i, color);
        }

        footerHolder.addMenuItem(new AppPlaceMenuItem(new ResidentPortalSiteMap.Profile(), PortalImages.INSTANCE.profileMenu(), ThemeColor.background));
        footerHolder.addMenuItem(new AppPlaceMenuItem(new ResidentPortalSiteMap.Account(), PortalImages.INSTANCE.accountMenu(), ThemeColor.background));

        footerHolder.addMenuItem(new AppPlaceMenuItem(new PortalSiteMap.Logout(), PortalImages.INSTANCE.logoutMenu(), ThemeColor.background));

        setMenuVisible(false);

        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout(event.getLayoutType());
            }

        });
    }

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

    @Override
    public void updateState() {
        for (MoveInWizardStepMenuItem step : mainHolder.getMenuItems()) {
            if (!MoveInWizardManager.isStepIncluded(step.getStepType())) {
                step.setStatus(StepStatus.notComplete);
                step.setEnabled(false);
                step.setVisible(false);
            } else if (step.getStepType().equals(MoveInWizardManager.getCurrentStep())) {
                step.setStatus(StepStatus.current);
                step.setEnabled(false);
                step.setVisible(true);
            } else if (MoveInWizardManager.isStepComplete(step.getStepType())) {
                step.setStatus(StepStatus.complete);
                step.setEnabled(false);
                step.setVisible(true);
            } else {
                step.setStatus(StepStatus.notComplete);
                step.setEnabled(true);
                step.setVisible(true);
            }
        }

        setMenuVisible(MoveInWizardManager.isAttemptStarted() && !MoveInWizardManager.isCompletionConfirmationTurn());
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
