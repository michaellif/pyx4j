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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuItem;

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

public class ToolbarViewImpl extends FlowPanel implements ToolbarView {

    private static final I18n i18n = I18n.get(ToolbarViewImpl.class);

    private ToolbarPresenter presenter;

    private final Button loginButton;

    private final Button tenantButton;

    private final MenuItem myProfileMenu;

    private final MenuItem myAccountMenu;

    private final MenuItem logoutMenu;

    private final Button sideMenuButton;

    private final Button languageButton;

    private final Image brandImage;

    private final Toolbar rightToolbar;

    public ToolbarViewImpl() {
        setStyleName(PortalWebRootPaneTheme.StyleName.MainToolbar.name());
        getElement().getStyle().setProperty("whiteSpace", "nowrap");

        rightToolbar = new Toolbar();
        rightToolbar.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);

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

        rightToolbar.add(loginButton);
        rightToolbar.add(tenantButton);
        rightToolbar.add(languageButton);

        Toolbar leftToolbar = new Toolbar();
        leftToolbar.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
        sideMenuButton = new Button(PortalImages.INSTANCE.menu(), new Command() {
            @Override
            public void execute() {
                AppSite.getEventBus().fireEvent(new LayoutChangeRerquestEvent(ChangeType.toggleSideMenu));
            }
        });
        leftToolbar.add(sideMenuButton);

        brandImage = new Image(PortalImages.INSTANCE.brand());
        brandImage.getElement().getStyle().setProperty("margin", "5px");
        brandImage.getElement().getStyle().setProperty("borderRadius", "4px");

        add(brandImage);
        add(leftToolbar);
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
    public void onLogedOut() {
        loginButton.setVisible(true);
        tenantButton.setVisible(false);
        tenantButton.setTextLabel("");
    }

    @Override
    public void onLogedIn(String userName) {
        loginButton.setVisible(false);
        tenantButton.setVisible(true);
        tenantButton.setTextLabel(userName);
        tenantButton.setImageResource(PortalImages.INSTANCE.avatar());
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
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
            sideMenuButton.setVisible(true);
            rightToolbar.setVisible(false);
            brandImage.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
            break;

        default:
            sideMenuButton.setVisible(false);
            rightToolbar.setVisible(true);
            brandImage.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
            break;
        }
    }
}
