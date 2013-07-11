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
package com.propertyvista.portal.web.client.ui.landing;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.SystemWallMessage;
import com.pyx4j.site.rpc.AppPlaceInfo;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;
import com.pyx4j.widgets.client.dialog.Dialog;

import com.propertyvista.portal.web.client.themes.LandingPagesTheme;

public class LandingViewImpl extends FlowPanel implements LandingView {

    static final I18n i18n = I18n.get(LandingViewImpl.class);

    private LandingView.Presenter presenter;

    private final LoginForm loginForm;

    private final LoginButton loginButton;

    private final DevLoginButton devLoginButton;

    private final Button signUpButton;

    private final Anchor termsAndConditionsAnchor;

    public LandingViewImpl() {

        setStyleName(LandingPagesTheme.StyleName.LandingPage.name());

        loginForm = new LoginForm();
        loginForm.initContent();
        add(loginForm);

        HTMLPanel loginTermsLinkPanel = new HTMLPanel(LoginAndSignUpResources.INSTANCE.loginViewTermsAgreementText().getText());
        termsAndConditionsAnchor = new Anchor(i18n.tr("RESIDENT PORTAL TERMS AND CONDITIONS"));
        termsAndConditionsAnchor.setStylePrimaryName(DefaultWidgetsTheme.StyleName.Anchor.name());
        termsAndConditionsAnchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.showVistaTerms();
                DOM.eventPreventDefault((com.google.gwt.user.client.Event) event.getNativeEvent());
            }
        });
        loginTermsLinkPanel.addAndReplaceElement(termsAndConditionsAnchor, LoginAndSignUpResources.TERMS_AND_AGREEMENTS_ANCHOR_TAG);
        add(loginTermsLinkPanel);

        loginButton = new LoginButton();
        devLoginButton = new DevLoginButton();

        FlowPanel loginButtonHolder = new FlowPanel();
        loginButtonHolder.add(loginButton);
        loginButtonHolder.add(devLoginButton);

        add(loginButtonHolder);

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
        add(resetPasswordAnchorHolder);

        add(new HTML(i18n.tr("First Time.")));
        add(new HTML(i18n.tr("Get Started")));

        FlowPanel signUpGreetingPanel = new FlowPanel();

        add(signUpGreetingPanel);

        signUpButton = new Button(i18n.tr("SIGN UP"), new Command() {
            @Override
            public void execute() {
                LandingViewImpl.this.onSignUp();
            }
        });

        SimplePanel signUpButtonHolder = new SimplePanel();
        signUpButtonHolder.setWidget(signUpButton);
        add(signUpButtonHolder);

    }

    @Override
    public void setPresenter(com.propertyvista.common.client.ui.components.login.LoginView.Presenter presenter) {
        this.presenter = (Presenter) presenter;
        this.termsAndConditionsAnchor.setHref(AppPlaceInfo.absoluteUrl(GWT.getModuleBaseURL(), true, this.presenter.getPortalTermsPlace()));
    }

    @Override
    public void enableHumanVerification() {
        loginForm.setEnableCaptcha(true);
    }

    @Override
    public void reset(String userId, boolean rememberUserId) {
        loginForm.setVisited(false);
        loginForm.populateNew();
        if (userId != null) {
            loginForm.get(loginForm.proto().email()).setValue(userId);
        }
        loginForm.get(loginForm.proto().rememberID()).setValue(rememberUserId);
        loginForm.setEnableCaptcha(false);
    }

    @Override
    public void setWallMessage(SystemWallMessage systemWallMessage) {
        // implement SetWallMessage
        // VladS tells that it the message that says our system is on maintenance, so probably it means the login and singup should be disabled hidden        
    }

    private void onLogin() {
        loginForm.revalidate();
        loginForm.setUnconditionalValidationErrorRendering(true);
        if (loginForm.isValid()) {
            presenter.login(loginForm.getValue());
        }
    }

    private void onSignUp() {
        presenter.signUp();
    }

    private void onResetPassword() {
        presenter.gotoResetPassword();
    }

    @Override
    public void setDevLogin(List<? extends DevLoginCredentials> devCredientials, String appModeName) {
        if (devCredientials == null) {
            devLoginButton.setVisible(false);
            devLoginButton.setDevLogin(new LinkedList<DevLoginCredentials>(), "ERROR!!!");
        } else {
            devLoginButton.setVisible(true);
            devLoginButton.setDevLogin(devCredientials, appModeName);
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

            // attach handler to invoke login via ENTER key
            addAttachHandler(new Handler() {

                private HandlerRegistration handlerRegistration;

                @Override
                public void onAttachOrDetach(AttachEvent event) {
                    if (event.isAttached()) {
                        handlerRegistration = Event.addNativePreviewHandler(new NativePreviewHandler() {
                            @Override
                            public void onPreviewNativeEvent(NativePreviewEvent event) {
                                if (event.getTypeInt() == Event.ONKEYUP && (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)) {
                                    if (!Dialog.isDialogOpen()) {
                                        onLogin();
                                    }
                                }
                            }
                        });
                    } else {
                        handlerRegistration.removeHandler();
                    }
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
            setTextLabel(i18n.tr("{0} FAST LOGIN", appModeName));

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

}
