/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 3, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.landing;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CEmailField;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.Dialog;

import com.propertyvista.common.client.ui.components.login.LoginView.DevLoginCredentials;
import com.propertyvista.domain.legal.TermsAndPoliciesType;
import com.propertyvista.portal.prospect.ui.landing.LandingView.LandingPresenter;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.GadgetToolbar;
import com.propertyvista.portal.shared.ui.landing.TermsLinkPanel;
import com.propertyvista.portal.shared.ui.util.decorators.CheckBoxDecorator;
import com.propertyvista.portal.shared.ui.util.decorators.LoginWidgetDecoratorBuilder;

public class LoginGadget extends AbstractGadget<LandingViewImpl> {

    static final I18n i18n = I18n.get(LandingViewImpl.class);

    private LandingPresenter presenter;

    private final LoginForm loginForm;

    private final LoginToolbar loginToolbar;

    LoginGadget(final LandingViewImpl view) {
        super(view, null, i18n.tr("Login to continue Online Application"), ThemeColor.contrast2, 1);

        loginToolbar = new LoginToolbar();

        setActionsToolbar(loginToolbar);

        final FlowPanel contentPanel = new FlowPanel();
        contentPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);

        loginForm = new LoginForm(this);
        loginForm.initContent();
        contentPanel.add(loginForm);

        contentPanel.add(new TermsLinkPanel(i18n.tr("LOGIN"), TermsAndPoliciesType.PVProspectPortalTermsAndConditions,
                PortalSiteMap.PortalTerms.VistaTermsAndConditions.class, TermsAndPoliciesType.PMCProspectPortalTermsAndConditions,
                PortalSiteMap.PortalTerms.PmcTermsAndConditions.class));

        setContent(contentPanel);
    }

    public void setPresenter(LandingPresenter presenter) {
        this.presenter = presenter;
    }

    public void enableHumanVerification() {
        loginForm.setCaptchaEnabled(true);
    }

    public void reset(String userId, boolean rememberUserId) {
        loginForm.reset();
        loginForm.populateNew();
        if (userId != null) {
            loginForm.get(loginForm.proto().email()).setValue(userId);
        }
        loginForm.get(loginForm.proto().rememberID()).setValue(rememberUserId);
        loginForm.setCaptchaEnabled(false);
    }

    private void onLogin() {
        loginForm.setVisitedRecursive();
        if (loginForm.isValid()) {
            presenter.login(loginForm.getValue());
        }
    }

    void onResetPassword() {
        presenter.gotoResetPassword();
    }

    public void setDevLogin(List<? extends DevLoginCredentials> devCredientials, String appModeName) {
        loginToolbar.setDevLogin(devCredientials, appModeName);
    }

    public void setTermsAndConditions(Class<? extends Place> place) {
    }

    class LoginForm extends CEntityForm<AuthenticationRequest> {

        private CCaptcha captchaField;

        private final LoginGadget loginGadget;

        public LoginForm(LoginGadget loginGadget) {
            super(AuthenticationRequest.class);
            this.loginGadget = loginGadget;
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel contentPanel = new BasicFlexFormPanel();

            int row = -1;

            contentPanel.setBR(++row, 0, 2);

            CEmailField emailField = inject(proto().email(), new CEmailField());
            emailField.setMandatoryValidationMessage(i18n.tr("Enter your email address"));
            emailField.getWidget().addKeyUpHandler(new EnterKeyHandler());
            contentPanel.setWidget(++row, 0, new LoginWidgetDecoratorBuilder(emailField).watermark(LandingViewImpl.i18n.tr("Email Address")).build());

            CPasswordTextField passwordField = inject(proto().password(), new CPasswordTextField());
            passwordField.setMandatoryValidationMessage(i18n.tr("Enter your password"));
            passwordField.getWidget().addKeyUpHandler(new EnterKeyHandler());
            contentPanel.setWidget(++row, 0, new LoginWidgetDecoratorBuilder(passwordField).watermark(LandingViewImpl.i18n.tr("Password")).build());

            CCheckBox rememberID = inject(proto().rememberID(), new CCheckBox());
            rememberID.getWidget().addKeyUpHandler(new EnterKeyHandler());
            contentPanel.setWidget(++row, 0, new CheckBoxDecorator(rememberID));

            Anchor resetPassword = new Anchor(i18n.tr("Forgot your password?"));
            resetPassword.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    loginGadget.onResetPassword();
                }
            });
            contentPanel.setWidget(++row, 0, resetPassword);

            captchaField = (CCaptcha) inject(proto().captcha());
            captchaField.getWidget().addKeyUpHandler(new EnterKeyHandler());
            contentPanel.setWidget(++row, 0, (new LoginWidgetDecoratorBuilder(captchaField).watermark(i18n.tr("Enter both security words above")).build()));
            setCaptchaEnabled(false);

            contentPanel.setBR(++row, 0, 2);

            return contentPanel;
        }

        public final void setCaptchaEnabled(boolean isEnabled) {
            captchaField.setVisible(isEnabled);
            if (isEnabled) {
                captchaField.createNewChallenge();
            }

        }

    }

    class LoginToolbar extends GadgetToolbar {

        private final LoginButton loginButton;

        private final DevLoginButton devLoginButton;

        public LoginToolbar() {

            loginButton = new LoginButton();
            devLoginButton = new DevLoginButton();

            loginButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast2, 1));
            devLoginButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast2, 0.7));

            addItem(loginButton);
            addItem(devLoginButton);

        }

        public void setDevLogin(List<? extends DevLoginCredentials> devCredientials, String appModeName) {
            if (devCredientials == null) {
                loginToolbar.devLoginButton.setVisible(false);
                loginToolbar.devLoginButton.setDevLogin(new LinkedList<DevLoginCredentials>(), "ERROR!!!");
            } else {
                loginToolbar.devLoginButton.setVisible(true);
                loginToolbar.devLoginButton.setDevLogin(devCredientials, appModeName);
            }
        }

    }

    class LoginButton extends Button {

        public LoginButton() {
            super(i18n.tr("LOGIN"), new Command() {
                @Override
                public void execute() {
                    onLogin();
                }
            });

        }

    }

    class DevLoginButton extends Button {

        private final ButtonMenuBar menu;

        public DevLoginButton() {
            super(i18n.tr("LOGIN"));
            menu = new ButtonMenuBar();
            setMenu(menu);
        }

        public void setDevLogin(List<? extends DevLoginCredentials> devCredientials, String appModeName) {
            setTextLabel(i18n.tr("{0} LOGIN", appModeName));

            menu.clearItems();
            for (final DevLoginCredentials credentials : devCredientials) {

                for (int i = 0; i < credentials.getUserType().getDefaultMax(); i++) {
                    final int index = i + 1;
                    menu.addItem(credentials.getUserType().toString() + " " + index, new Command() {
                        @Override
                        public void execute() {
                            loginForm.get(loginForm.proto().email()).setValue(credentials.getUserType().getEmail(index));
                            loginForm.get(loginForm.proto().password()).setValue(credentials.getUserType().getEmail(index));
                            onLogin();
                        }
                    });
                }

            }
        }
    }

    class EnterKeyHandler implements KeyUpHandler {

        @Override
        public void onKeyUp(KeyUpEvent event) {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                if (!Dialog.isDialogOpen()) {
                    onLogin();
                }
            }
        }

    }
}
