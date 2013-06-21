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

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.portal.web.client.themes.PortalWebRootPaneTheme;

public class ToolbarViewImpl extends FlowPanel implements ToolbarView {

    private static final I18n i18n = I18n.get(ToolbarViewImpl.class);

    private ToolbarPresenter presenter;

    private final Anchor greetings;

    private final Anchor logout;

    private final Anchor login;

    public ToolbarViewImpl() {
        setStyleName(PortalWebRootPaneTheme.StyleName.MainToolbar.name());

        SimplePanel toolbarHolder = new SimplePanel();
        toolbarHolder.setStyleName(PortalWebRootPaneTheme.StyleName.MainToolbarActions.name());
        Toolbar toolbar = new Toolbar();
        toolbarHolder.setWidget(toolbar);
        add(toolbarHolder);

        greetings = new Anchor(null);
        greetings.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.showAccount();
            }
        });
        greetings.ensureDebugId("account");
        greetings.asWidget().getElement().getStyle().setDisplay(Display.INLINE);
        greetings.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLD);
        greetings.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        logout = new Anchor(null);
        logout.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.logout();
            }
        });

        logout.ensureDebugId("logout");
        logout.setHTML(i18n.tr("Log Out"));
        logout.setVisible(false);
        logout.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        login = new Anchor(null);
        login.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.login();
            }
        });

        login.ensureDebugId("login");
        login.setHTML(i18n.tr("Log In"));
        login.asWidget().getElement().getStyle().setMarginRight(1, Unit.EM);

        toolbar.add(greetings);
        toolbar.add(login);
        toolbar.add(logout);

    }

    @Override
    public void setPresenter(final ToolbarPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onLogedOut() {
        logout.setVisible(false);
        login.setVisible(true);
        greetings.setVisible(false);
        greetings.setHTML("");
    }

    @Override
    public void onLogedIn(String userName) {
        logout.setVisible(true);
        login.setVisible(false);
        greetings.setHTML(i18n.tr("Welcome {0}", userName));
        greetings.setVisible(true);
    }

}
