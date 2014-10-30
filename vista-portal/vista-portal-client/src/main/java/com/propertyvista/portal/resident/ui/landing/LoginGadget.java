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
package com.propertyvista.portal.resident.ui.landing;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.login.LoginView.DevLoginCredentials;
import com.propertyvista.domain.legal.TermsAndPoliciesType;
import com.propertyvista.portal.resident.ui.landing.LandingView.LandingPresenter;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.GadgetToolbar;
import com.propertyvista.portal.shared.ui.landing.ILoginGadget;
import com.propertyvista.portal.shared.ui.landing.LoginForm;
import com.propertyvista.portal.shared.ui.landing.TermsLinkPanel;

public class LoginGadget extends AbstractGadget<LandingViewImpl> implements ILoginGadget {

    static final I18n i18n = I18n.get(LandingViewImpl.class);

    private LandingPresenter presenter;

    private final LoginForm loginForm;

    private final LoginToolbar loginToolbar;

    LoginGadget(final LandingViewImpl view) {
        super(view, null, i18n.tr("Returning Users"), ThemeColor.contrast2, 1);

        loginToolbar = new LoginToolbar();

        setActionsToolbar(loginToolbar);

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);

        loginForm = new LoginForm(this);
        loginForm.init();
        contentPanel.add(loginForm);

        contentPanel.add(new TermsLinkPanel(i18n.tr("LOGIN"), TermsAndPoliciesType.PVResidentPortalTermsAndConditions,
                PortalSiteMap.PortalTerms.VistaTermsAndConditions.class, TermsAndPoliciesType.PMCResidentPortalTermsAndConditions,
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

    @Override
    public void onLogin() {
        loginForm.setVisitedRecursive();
        if (loginForm.isValid()) {
            presenter.login(loginForm.getValue());
        } else {
            showValidationDialog();
        }
    }

    protected void showValidationDialog() {
        MessageDialog.error(i18n.tr("Error"), loginForm.getValidationResults().getValidationMessage(true));
    }

    @Override
    public void onResetPassword() {
        presenter.gotoResetPassword();
    }

    public void setDevLogin(List<? extends DevLoginCredentials> devCredientials, String appModeName) {
        loginToolbar.setDevLogin(devCredientials, appModeName);
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
