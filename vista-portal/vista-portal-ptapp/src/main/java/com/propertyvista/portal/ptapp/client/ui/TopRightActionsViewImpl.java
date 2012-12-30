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
package com.propertyvista.portal.ptapp.client.ui;

import java.util.List;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.common.client.ClientNavigUtils;
import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.portal.rpc.ptapp.VistaFormsDebugId;
import com.propertyvista.shared.i18n.CompiledLocale;

public class TopRightActionsViewImpl extends VerticalPanel implements TopRightActionsView {

    public enum Theme {
        Gainsboro, VillageGreen
    }

    private static final I18n i18n = I18n.get(TopRightActionsViewImpl.class);

    private final HorizontalPanel topLinksPanel;

    private final HorizontalPanel bottomLinksPanel;

    private Presenter presenter;

    private final HTML greetings;

    private final Anchor logout;

    private final Anchor login;

    private final Anchor selectApplication;

    private final Anchor passwordChange;

    MenuBar languageMenu;

    MenuBar languages;

    private final HorizontalPanel locales;

    private HTML selectApplicationSeparator;

    public TopRightActionsViewImpl() {
        getElement().getStyle().setFontSize(0.9, Unit.EM);
        getElement().getStyle().setFloat(Float.RIGHT);

        topLinksPanel = new HorizontalPanel();
        topLinksPanel.getElement().getStyle().setMargin(4, Unit.PX);
        add(topLinksPanel);

        bottomLinksPanel = new HorizontalPanel();
        bottomLinksPanel.getElement().getStyle().setMargin(4, Unit.PX);
        add(bottomLinksPanel);

        greetings = new HTML("");
        topLinksPanel.add(greetings);

        passwordChange = new Anchor(null);
        passwordChange.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.changePassword();
            }
        });
        passwordChange.setHTML(i18n.tr("Change Password"));
        topLinksPanel.add(passwordChange);
        topLinksPanel.add(new HTML("&nbsp;-&nbsp;"));

        selectApplication = new Anchor(null);
        selectApplication.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.changeApplication();
            }
        });
        selectApplication.setHTML(i18n.tr("Select an Application"));
        topLinksPanel.add(selectApplication);
        topLinksPanel.add(selectApplicationSeparator = new HTML("&nbsp; - &nbsp;"));

        logout = new Anchor(null);
        logout.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.logout();
            }
        });

        logout.ensureDebugId(VistaFormsDebugId.Auth_LogOutTop.name());
        logout.setHTML(i18n.tr("Log Out"));
        logout.setVisible(false);
        topLinksPanel.add(logout);

        login = new Anchor(null);
        login.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.login();
            }
        });

        login.ensureDebugId(VistaFormsDebugId.Auth_LoginTop.name());
        login.setHTML(i18n.tr("Log In"));
        topLinksPanel.add(login);

        topLinksPanel.add(new HTML("&nbsp;-&nbsp;"));

        locales = new HorizontalPanel();
        locales.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        languageMenu = new MenuBar();
        languageMenu.setAutoOpen(false);
        languageMenu.setAnimationEnabled(false);
        languageMenu.setFocusOnHoverEnabled(true);
        languages = new MenuBar(true);
        MenuItem item = new MenuItem(ClientNavigUtils.getCurrentLocale().toString(), languages);
        languageMenu.addItem(item);
        languageMenu.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        topLinksPanel.add(languageMenu);
    }

    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onLogedOut() {
        logout.setVisible(false);
        login.setVisible(true);
        passwordChange.setVisible(false);
        greetings.setHTML("");
        selectApplication.setVisible(false);
    }

    @Override
    public void onLogedIn(String userName) {
        logout.setVisible(true);
        login.setVisible(false);
        passwordChange.setVisible(true);
        greetings.setHTML("Hello " + userName + "&nbsp;-&nbsp;");
        selectApplication.setVisible(SecurityController.checkBehavior(VistaCustomerBehavior.HasMultipleApplications));
        selectApplicationSeparator.setVisible(SecurityController.checkBehavior(VistaCustomerBehavior.HasMultipleApplications));
    }

    @Override
    public void setAvailableLocales(List<CompiledLocale> localeList) {
        languages.clearItems();
        for (final CompiledLocale compiledLocale : localeList) {
            Command changeLanguage = new Command() {
                @Override
                public void execute() {
                    presenter.setLocale(compiledLocale);
                }
            };
            MenuItem item = new MenuItem(compiledLocale.getNativeDisplayName(), changeLanguage);
            languages.addItem(item);
        }
    }
}
