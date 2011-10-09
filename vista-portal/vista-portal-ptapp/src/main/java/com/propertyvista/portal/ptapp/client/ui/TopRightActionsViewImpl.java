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


import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.StringDebugId;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.rpc.ptapp.VistaFormsDebugId;

public class TopRightActionsViewImpl extends VerticalPanel implements TopRightActionsView {

    public enum Theme {
        Gainsboro, VillageGreen
    }

    private static I18n i18n = I18n.get(TopRightActionsViewImpl.class);

    private final HorizontalPanel topLinksPanel;

    private final HorizontalPanel bottomLinksPanel;

    private Presenter presenter;

    private final HTML greetings;

    private final CHyperlink logout;

    private final CHyperlink login;

    private final CHyperlink themes;

    private Theme otherTheme = Theme.VillageGreen;

    public TopRightActionsViewImpl() {
        getElement().getStyle().setFontSize(0.9, Unit.EM);

        topLinksPanel = new HorizontalPanel();
        topLinksPanel.getElement().getStyle().setMargin(4, Unit.PX);
        add(topLinksPanel);

        bottomLinksPanel = new HorizontalPanel();
        bottomLinksPanel.getElement().getStyle().setMargin(4, Unit.PX);
        add(bottomLinksPanel);

        greetings = new HTML("");
        topLinksPanel.add(greetings);

        logout = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.logout();
            }
        });
        logout.setDebugId(VistaFormsDebugId.Auth_LogOutTop);
        logout.setValue(i18n.tr("LogOut"));
        logout.setVisible(false);
        topLinksPanel.add(logout);

        login = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.login();
            }
        });
        login.setDebugId(new StringDebugId("login"));
        login.setValue(i18n.tr("LogIn"));
        login.setDebugId(VistaFormsDebugId.Auth_LoginTop);
        topLinksPanel.add(login);

        topLinksPanel.add(new HTML("&nbsp;-&nbsp;"));

        themes = new CHyperlink(null, new Command() {
            @Override
            public void execute() {
                presenter.setTheme(otherTheme);
                otherTheme = (otherTheme == Theme.VillageGreen ? Theme.Gainsboro : Theme.VillageGreen);
                themes.setValue(otherTheme.name());
            }
        });
        themes.setValue(otherTheme.name());

        themes.setDebugId(new StringDebugId("themes"));
        topLinksPanel.add(themes);
    }

    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onLogedOut() {
        logout.setVisible(false);
        login.setVisible(true);
        greetings.setHTML("");
    }

    @Override
    public void onLogedIn(String userName) {
        logout.setVisible(true);
        login.setVisible(false);
        greetings.setHTML("Hello " + userName + "&nbsp;-&nbsp;");
    }
}
