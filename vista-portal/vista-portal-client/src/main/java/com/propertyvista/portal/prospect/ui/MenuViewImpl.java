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
package com.propertyvista.portal.prospect.ui;

import java.util.List;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.wizard.WizardStep;
import com.pyx4j.gwt.commons.layout.LayoutChangeEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeHandler;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.prospect.ui.WizardStepItem.StepStatus;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizard;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;
import com.propertyvista.portal.shared.ui.AppPlaceMenuItem;
import com.propertyvista.portal.shared.ui.MenuItem;
import com.propertyvista.portal.shared.ui.MenuList;

public class MenuViewImpl extends DockPanel implements MenuView {

    private final HeaderHolder headerHolder;

    private final WizardStepList mainHolder;

    private final MenuList<MenuItem<?>> footerHolder;

    private final MenuItem<?> applicationSelectionMenu;

    private ApplicationWizard applicationWizard;

    public MenuViewImpl() {
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

        applicationSelectionMenu = new AppPlaceMenuItem(new ProspectPortalSiteMap.ApplicationContextSelection(), PortalImages.INSTANCE.selectMenu(),
                ThemeColor.background);
        footerHolder.addMenuItem(applicationSelectionMenu);

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
    public void updateStepButtons(ApplicationWizard applicationWizard) {
        this.applicationWizard = applicationWizard;
        mainHolder.reset(applicationWizard);
        if (applicationWizard != null) {

            List<WizardStep> steps = applicationWizard.getAllSteps();

            for (int i = 0; i < steps.size(); i++) {
                WizardStep step = steps.get(i);
                StepStatus stepStatus = StepStatus.notComplete;

                if (step.isStepVisible()) {
                    stepStatus = StepStatus.current;
                } else if (step.isStepVisited() && !step.isStepComplete()) {
                    stepStatus = StepStatus.invalid;
                } else if (step.isStepComplete() && (i != steps.size() - 1)) {
                    stepStatus = StepStatus.complete;
                }

                mainHolder.addStepItem(step.getStepTitle(), i, stepStatus);

            }

        }
        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));
    }

    private void doLayout(LayoutType layoutType) {
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
            addStyleDependentName(PortalRootPaneTheme.StyleDependent.sideMenu.name());
            removeStyleDependentName(PortalRootPaneTheme.StyleDependent.collapsedMenu.name());
            footerHolder.setVisible(true);
            headerHolder.setVisible(true);
            setVisible(true);
            break;
        case tabletPortrait:
        case tabletLandscape:
            removeStyleDependentName(PortalRootPaneTheme.StyleDependent.sideMenu.name());
            addStyleDependentName(PortalRootPaneTheme.StyleDependent.collapsedMenu.name());
            footerHolder.setVisible(false);
            headerHolder.setVisible(false);
            setVisible(applicationWizard != null);
            break;
        case monitor:
        case huge:
            removeStyleDependentName(PortalRootPaneTheme.StyleDependent.sideMenu.name());
            removeStyleDependentName(PortalRootPaneTheme.StyleDependent.collapsedMenu.name());
            footerHolder.setVisible(false);
            headerHolder.setVisible(false);
            setVisible(applicationWizard != null);
            break;
        }
    }

    @Override
    public void setUserName(String userName) {
        headerHolder.setName(userName);
    }

    @Override
    public void setApplicationsSelectorEnabled(boolean enabled) {
        applicationSelectionMenu.setVisible(enabled);
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
