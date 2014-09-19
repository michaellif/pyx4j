/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-30
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui;

import java.util.List;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.gwt.commons.layout.LayoutChangeEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeHandler;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Button.ButtonMenuBar;
import com.pyx4j.widgets.client.Toolbar;
import com.pyx4j.widgets.client.style.theme.WidgetTheme;

import com.propertyvista.common.client.ClientLocaleUtils;
import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.portal.resident.activity.PortalClientCommunicationManager;
import com.propertyvista.portal.rpc.shared.dto.communication.PortalCommunicationSystemNotification;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;
import com.propertyvista.shared.i18n.CompiledLocale;

public class ToolbarViewImpl extends FlowPanel implements ToolbarView {

    private static final I18n i18n = I18n.get(ToolbarViewImpl.class);

    private ToolbarPresenter presenter;

    private final Button loginButton;

    private final Button tenantButton;

    private final Button communicationButton;

    private final MenuItem myProfileMenu;

    private final MenuItem myAccountMenu;

    private final MenuItem logoutMenu;

    private final MenuItem leaseSelectionMenu;

    private final Button sideMenuButton;

    private final Button languageButton;

    private final Toolbar rightToolbar;

    private final Image brandImage;

    private final FlowPanel brandHolder;

    private LayoutType layoutType;

    private final Image brandLabel;

    private boolean hideLoginButton = false;

    private boolean loggedIn = false;

    public ToolbarViewImpl() {
        setStyleName(PortalRootPaneTheme.StyleName.MainToolbar.name());
        getElement().getStyle().setProperty("whiteSpace", "nowrap");

        rightToolbar = new Toolbar();

        tenantButton = new Button("");
        ButtonMenuBar tenantButtonMenu = new ButtonMenuBar();

        leaseSelectionMenu = new MenuItem(i18n.tr("My Leases"), new Command() {
            @Override
            public void execute() {
                presenter.showLeases();
            }
        });
        tenantButtonMenu.addItem(leaseSelectionMenu);

        myProfileMenu = new MenuItem(i18n.tr("My Profile"), new Command() {
            @Override
            public void execute() {
                presenter.showProfile();
            }
        });
        tenantButtonMenu.addItem(myProfileMenu);

        myAccountMenu = new MenuItem(i18n.tr("My Account"), new Command() {
            @Override
            public void execute() {
                presenter.showAccount();
            }
        });
        tenantButtonMenu.addItem(myAccountMenu);

        logoutMenu = new MenuItem(i18n.tr("Logout"), new Command() {
            @Override
            public void execute() {
                presenter.logout();
            }
        });
        tenantButtonMenu.addItem(logoutMenu);

        tenantButton.setMenu(tenantButtonMenu);

        loginButton = new Button(i18n.tr("Log In"), new Command() {
            @Override
            public void execute() {
                presenter.login();
            }
        });
        loginButton.ensureDebugId("login");

        languageButton = new Button(ClientLocaleUtils.getCurrentLocale().toString());

        communicationButton = new Button(PortalImages.INSTANCE.alertsOff(), new Command() {
            @Override
            public void execute() {
                switch (layoutType) {
                case phonePortrait:
                case phoneLandscape:
                    AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.toggleSideComm));
                    break;
                default:
                    AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(communicationButton));
                    break;
                }

                presenter.loadMessages();
            }
        });
        communicationButton.setStyleName(PortalRootPaneTheme.StyleName.AllertButton.name());
        communicationButton.addStyleName(WidgetTheme.StyleName.Button.name());

        rightToolbar.addItem(loginButton);
        rightToolbar.addItem(tenantButton);

        //TODO implement lang selector
        if (false) {
            rightToolbar.addItem(languageButton);
        }

        rightToolbar.addItem(communicationButton);
        rightToolbar.getElement().getStyle().setPosition(Position.ABSOLUTE);
        rightToolbar.getElement().getStyle().setProperty("right", "0");

        Toolbar leftToolbar = new Toolbar();
        sideMenuButton = new Button(PortalImages.INSTANCE.menu(), new Command() {
            @Override
            public void execute() {
                AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.toggleSideMenu));
            }
        });
        leftToolbar.addItem(sideMenuButton);
        leftToolbar.getElement().getStyle().setPosition(Position.ABSOLUTE);
        leftToolbar.getElement().getStyle().setProperty("left", "0");

        brandImage = new Image(PortalImages.INSTANCE.myCommunityHeaderLogo());
        brandImage.getElement().getStyle().setFloat(Float.LEFT);
        brandImage.getElement().getStyle().setProperty("borderRadius", "4px");
        brandImage.getElement().getStyle().setCursor(Cursor.POINTER);
        brandImage.setTitle("Home");

        brandLabel = new Image(PortalImages.INSTANCE.myCommunityHeaderLogoLabel());
        brandLabel.getElement().getStyle().setFloat(Float.RIGHT);
        brandLabel.getElement().getStyle().setProperty("margin", "10px 0 0 0");
        brandLabel.getElement().getStyle().setCursor(Cursor.POINTER);
        brandLabel.setTitle("Home");

        ClickHandler goHomeHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                AppSite.getPlaceController().goTo(AppPlace.NOWHERE);
            }
        };
        brandImage.addClickHandler(goHomeHandler);
        brandLabel.addClickHandler(goHomeHandler);

        brandHolder = new FlowPanel();
        brandHolder.setStyleName(PortalRootPaneTheme.StyleName.BrandImage.name());
        brandHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

        brandHolder.add(brandImage);
        brandHolder.add(brandLabel);

        add(leftToolbar);
        add(brandHolder);
        add(rightToolbar);

        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout(event.getLayoutType());
            }

        });
    }

    @Override
    public void setPresenter(final ToolbarPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onLogedOut(boolean hideLoginButton) {
        this.loggedIn = false;
        this.hideLoginButton = hideLoginButton;
        tenantButton.setTextLabel("");
        //TODO add image tenantButton.setImage(PortalImages.INSTANCE.avatar());
        calculateActionsState();
    }

    @Override
    public void onLogedIn(String userName) {
        this.loggedIn = true;
        tenantButton.setTextLabel(userName);
        //TODO add image tenantButton.setImage(PortalImages.INSTANCE.avatar());
        calculateActionsState();
    }

    @Override
    public void setAvailableLocales(List<CompiledLocale> localeList) {
        languageButton.setMenu(null);
        ButtonMenuBar menuBar = new ButtonMenuBar();
        for (final CompiledLocale compiledLocale : localeList) {
            Command changeLanguage = new Command() {
                @Override
                public void execute() {
                    presenter.setLocale(compiledLocale);
                }
            };
            MenuItem item = new MenuItem(compiledLocale.getNativeDisplayName(), changeLanguage);
            menuBar.addItem(item);
        }
        languageButton.setMenu(menuBar);
    }

    @Override
    public void setLeasesSelectorEnabled(boolean enabled) {
        leaseSelectionMenu.setVisible(enabled);
    }

    private void doLayout(LayoutType layoutType) {
        this.layoutType = layoutType;
        calculateActionsState();
    }

    private void calculateActionsState() {
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
            sideMenuButton.setVisible(loggedIn);
            tenantButton.setVisible(false);
            languageButton.setVisible(false);
            brandHolder.getElement().getStyle().setProperty("margin", "0 50%");
            brandImage.getElement().getStyle().setProperty("margin", "5px -25px 0");
            break;
        default:
            sideMenuButton.setVisible(false);
            tenantButton.setVisible(loggedIn);
            languageButton.setVisible(true);
            brandHolder.getElement().getStyle().setProperty("margin", "0");
            brandImage.getElement().getStyle().setProperty("margin", "5px 0 0 10px");
            break;
        }
        loginButton.setVisible(!loggedIn && !hideLoginButton);

        updateCommunicationMessagesCount(PortalClientCommunicationManager.instance().getLatestCommunicationNotification());
        communicationButton.setVisible(loggedIn
                && (SecurityController.check(PortalResidentBehavior.Resident) || SecurityController.check(PortalResidentBehavior.Guarantor)));

        switch (layoutType) {
        case monitor:
        case huge:
        case tabletLandscape:
            brandLabel.setVisible(true);
            break;
        default:
            brandLabel.setVisible(false);
            break;
        }
    }

    @Override
    public void updateCommunicationMessagesCount(PortalCommunicationSystemNotification communicationStatus) {
        int count = communicationStatus == null ? 0 : communicationStatus.numberOfNewDirectMessages;
        if (count > 0) {
            communicationButton.setImage(PortalImages.INSTANCE.alertsOn());
            communicationButton.setTextLabel(String.valueOf(count));
            communicationButton.addStyleDependentName(PortalRootPaneTheme.StyleDependent.alertOn.name());
        } else {
            communicationButton.setImage(PortalImages.INSTANCE.alertsOff());
            communicationButton.setTextLabel("");
            communicationButton.removeStyleDependentName(PortalRootPaneTheme.StyleDependent.alertOn.name());
        }

    }
}
