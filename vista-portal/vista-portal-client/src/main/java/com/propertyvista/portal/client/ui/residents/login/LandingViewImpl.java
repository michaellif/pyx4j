/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-25
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.login;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.SystemWallMessage;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.portal.client.ui.residents.decorators.CheckBoxDecorator;

public class LandingViewImpl extends Composite implements LandingView {

    private static final I18n i18n = I18n.get(LandingViewImpl.class);

    private LandingView.Presenter presenter;

    private final LoginForm loginForm;

    private final Button loginButton;

    private class LoginForm extends CEntityDecoratableForm<AuthenticationRequest> {

        private CCaptcha captcha;

        public LoginForm() {
            super(AuthenticationRequest.class);
        }

        @Override
        public IsWidget createContent() {
            FlowPanel contentPanel = new FlowPanel();

            contentPanel.add(center(new DecoratorBuilder(inject(proto().email(), new CTextField())).customLabel("").labelWidth(0).componentWidth(15)
                    .useLabelSemicolon(false).build()));
            ((CTextField) get(proto().email())).setWatermark(i18n.tr("Email"));

            contentPanel.add(center(new DecoratorBuilder(inject(proto().password(), new CPasswordTextField())).customLabel("").labelWidth(0).componentWidth(15)
                    .useLabelSemicolon(false).build()));
            ((CPasswordTextField) get(proto().password())).setWatermark(i18n.tr("Password"));

            contentPanel.add(center((captcha = (CCaptcha) inject(proto().captcha())).asWidget()));
            setEnableCaptcha(false);

            contentPanel.add(center(new CheckBoxDecorator((CCheckBox) inject(proto().rememberID(), new CCheckBox()))));

            return contentPanel;
        }

        public final void setEnableCaptcha(boolean isEnabled) {
            captcha.setVisible(isEnabled);
            if (isEnabled) {
                captcha.createNewChallenge();
            }
        }

        private Widget center(Widget w) {
            w.getElement().getStyle().setProperty("width", "200px");
            w.getElement().getStyle().setProperty("marginLeft", "auto");
            w.getElement().getStyle().setProperty("marginRight", "auto");
            return w;
        }
    }

    public LandingViewImpl() {
        LayoutPanel viewPanel = new LayoutPanel();
        viewPanel.setSize("100%", "100%");

        FlowPanel loginPanel = new FlowPanel();
        loginPanel.getElement().getStyle().setProperty("marginLeft", "auto");
        loginPanel.getElement().getStyle().setProperty("marginRight", "auto");

        loginForm = new LoginForm();
        loginForm.initContent();
        loginForm.asWidget().getElement().getStyle().setMarginBottom(20, Unit.PX);
        loginPanel.add(loginForm);

        loginButton = new Button(i18n.tr("Login"));
        loginButton.setWidth("50px");
        loginButton.getElement().getStyle().setProperty("marginLeft", "auto");
        loginButton.getElement().getStyle().setProperty("marginRight", "auto");

        loginButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onLogin();
            }
        });

        SimplePanel loginButtonHolder = new SimplePanel();
        loginButtonHolder.setWidth("100%");
        loginButtonHolder.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        loginButtonHolder.setWidget(loginButton);
        loginPanel.add(loginButtonHolder);

        SimplePanel resetPasswordAnchorHolder = new SimplePanel();
        resetPasswordAnchorHolder.setWidth("100%");
        resetPasswordAnchorHolder.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        Anchor resetPassword = new Anchor(i18n.tr("forgot your password?"));

        resetPassword.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onResetPassword();
            }
        });
        resetPasswordAnchorHolder.add(resetPassword);
        loginPanel.add(resetPasswordAnchorHolder);

        viewPanel.add(loginPanel);
        viewPanel.setWidgetLeftRight(loginPanel, 0, Unit.PCT, 50, Unit.PCT);
        viewPanel.setWidgetTopHeight(loginPanel, 0, Unit.PCT, 300, Unit.PX);
        initWidget(viewPanel);
    }

    @Override
    public void setPresenter(com.propertyvista.common.client.ui.components.login.LoginView.Presenter presenter) {
        this.presenter = (Presenter) presenter;
    }

    @Override
    public void enableHumanVerification() {
        loginForm.setEditable(true);
    }

    @Override
    public void reset(String userId, boolean rememberUserId) {
        loginForm.populateNew();
        if (userId != null) {
            loginForm.get(loginForm.proto().email()).setValue(userId);
        }
        loginForm.get(loginForm.proto().rememberID()).setValue(rememberUserId);
        loginForm.setEnableCaptcha(false);
    }

    @Override
    public void setWallMessage(SystemWallMessage systemWallMessage) {
        // TODO Auto-generated method stub        
    }

    private void onLogin() {
        presenter.login(loginForm.getValue());
    }

    private void onResetPassword() {
        presenter.gotoResetPassword();
    }
}
