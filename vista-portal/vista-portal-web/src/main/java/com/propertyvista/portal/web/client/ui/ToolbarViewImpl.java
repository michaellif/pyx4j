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
package com.propertyvista.portal.web.client.ui;

import java.util.List;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRerquestEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRerquestEvent.ChangeType;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Button.ButtonMenuBar;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.common.client.ClientNavigUtils;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.themes.PortalWebRootPaneTheme;
import com.propertyvista.shared.i18n.CompiledLocale;

public class ToolbarViewImpl extends FlowPanel implements ToolbarView, RequiresResize {

    private static final I18n i18n = I18n.get(ToolbarViewImpl.class);

    private ToolbarPresenter presenter;

    private final Button loginButton;

    private final Button tenantButton;

    private final Button notificationsButton;

    private final MenuItem myProfileMenu;

    private final MenuItem myAccountMenu;

    private final MenuItem logoutMenu;

    private final Button sideMenuButton;

    private final Button languageButton;

    private final Toolbar rightToolbar;

    private final SimplePanel brandImageHolder;

    private LayoutType layoutType;

    private boolean hideLoginButton = false;

    private boolean loggedIn = false;

    public ToolbarViewImpl() {
        setStyleName(PortalWebRootPaneTheme.StyleName.MainToolbar.name());
        getElement().getStyle().setProperty("whiteSpace", "nowrap");

        rightToolbar = new Toolbar();

        tenantButton = new Button("");
        ButtonMenuBar tenantButtonMenu = new ButtonMenuBar();

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

        languageButton = new Button(ClientNavigUtils.getCurrentLocale().toString());

        notificationsButton = new Button(PortalImages.INSTANCE.alert(), new Command() {
            @Override
            public void execute() {
                switch (layoutType) {
                case phonePortrait:
                case phoneLandscape:
                    AppSite.getEventBus().fireEvent(new LayoutChangeRerquestEvent(ChangeType.toggleSideNotifications));
                    break;
                default:
                    AppSite.getEventBus().fireEvent(new LayoutChangeRerquestEvent(notificationsButton));
                    break;
                }
            }
        });

        rightToolbar.add(notificationsButton);
        rightToolbar.add(loginButton);
        rightToolbar.add(tenantButton);
        rightToolbar.add(languageButton);
        rightToolbar.getElement().getStyle().setPosition(Position.ABSOLUTE);
        rightToolbar.getElement().getStyle().setProperty("right", "0");

        Toolbar leftToolbar = new Toolbar();
        sideMenuButton = new Button(PortalImages.INSTANCE.menu(), new Command() {
            @Override
            public void execute() {
                AppSite.getEventBus().fireEvent(new LayoutChangeRerquestEvent(ChangeType.toggleSideMenu));
            }
        });
        leftToolbar.add(sideMenuButton);
        leftToolbar.getElement().getStyle().setPosition(Position.ABSOLUTE);
        leftToolbar.getElement().getStyle().setProperty("left", "0");

        Image brandImage = new Image(PortalImages.INSTANCE.brand());
        brandImage.getElement().getStyle().setProperty("margin", "7px");
        brandImage.getElement().getStyle().setProperty("borderRadius", "4px");
        brandImageHolder = new SimplePanel(brandImage);
        brandImageHolder.setStyleName(PortalWebRootPaneTheme.StyleName.BrandImage.name());
        brandImageHolder.getElement().getStyle().setPosition(Position.ABSOLUTE);

        add(leftToolbar);
        add(brandImageHolder);
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
        calculateActionsState();
    }

    @Override
    public void onLogedIn(String userName) {
        this.loggedIn = true;
        tenantButton.setTextLabel(userName);
        tenantButton.setImage(PortalImages.INSTANCE.avatar());
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

    private void doLayout(LayoutType layoutType) {
        this.layoutType = layoutType;
        calculateActionsState();
    }

    private void calculateActionsState() {
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
            sideMenuButton.setVisible(true);
            tenantButton.setVisible(false);
            languageButton.setVisible(false);
            brandImageHolder.getElement().getStyle().setProperty("margin", "0 auto");
            break;
        default:
            sideMenuButton.setVisible(false);
            tenantButton.setVisible(loggedIn);
            languageButton.setVisible(true);
            brandImageHolder.getElement().getStyle().setProperty("margin", "0");
            break;
        }
        loginButton.setVisible(!loggedIn && !hideLoginButton);
        notificationsButton.setVisible(loggedIn);

    }

    @Override
    public void onResize() {
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
            brandImageHolder.getElement().getStyle().setProperty("left", ((getOffsetWidth() - brandImageHolder.getOffsetWidth()) / 2) + "px");
            break;
        default:
            brandImageHolder.getElement().getStyle().setProperty("left", "0");
            break;
        }

    }
}
