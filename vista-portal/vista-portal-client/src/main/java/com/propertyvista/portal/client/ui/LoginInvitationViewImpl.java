/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 14, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class LoginInvitationViewImpl extends SimplePanel implements LoginInvitationView {
    private Presenter presenter;

    private static I18n i18n = I18nFactory.getI18n(LoginInvitationViewImpl.class);

    public LoginInvitationViewImpl() {

        FlowPanel panel = new FlowPanel();
        HTML message = new HTML("Upon selecting <b>Login</b> button the portal has to be reloaded via https<br>"
                + "The portal should navigate a user to this very point and depict a login form<br>");

        panel.add(message);
        Button btn = new Button(i18n.tr("Login"));
        btn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.gotoLoginForm();
            }

        });
        panel.add(btn);

        btn = new Button(i18n.tr("Sign Up(to be removed)"));
        btn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.gotoCreatrAccountForm();
            }

        });
        btn.getElement().getStyle().setMarginLeft(2, Unit.EM);
        panel.add(btn);

        setWidget(panel);

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

}
