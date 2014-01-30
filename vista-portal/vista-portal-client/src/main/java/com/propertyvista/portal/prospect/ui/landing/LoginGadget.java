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
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.Place;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.Dialog;

import com.propertyvista.common.client.ui.components.login.LoginView.DevLoginCredentials;
import com.propertyvista.portal.prospect.ui.landing.LandingView.LandingPresenter;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.GadgetToolbar;
import com.propertyvista.portal.shared.ui.TermsAnchor;
import com.propertyvista.portal.shared.ui.util.decorators.CheckBoxDecorator;
import com.propertyvista.portal.shared.ui.util.decorators.LoginWidgetDecoratorBuilder;

public class LoginGadget extends AbstractGadget<LandingViewImpl> {

    static final I18n i18n = I18n.get(LandingViewImpl.class);

    private LandingPresenter presenter;

    private final LoginForm loginForm;

    private final LoginToolbar loginToolbar;

    LoginGadget(final LandingViewImpl view) {
        super(view, null, i18n.tr("Already Started an Application?"), ThemeColor.contrast2, 1);

        loginToolbar = new LoginToolbar();

        setActionsToolbar(loginToolbar);

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);

        loginForm = new LoginForm(this);
        loginForm.initContent();
        contentPanel.add(loginForm);

        SafeHtmlBuilder loginTermsBuilder = new SafeHtmlBuilder();
        String portalTermsAndConditionsAnchorId = HTMLPanel.createUniqueId();
        String pmcTermsAndConditionsAnchorId = HTMLPanel.createUniqueId();

        loginTermsBuilder.appendHtmlConstant(i18n.tr("By clicking LOGIN, you are acknowledging that you have read and agree to the {0} and {1}.", "<span id=\""
                + portalTermsAndConditionsAnchorId + "\"></span>", "<span id=\"" + pmcTermsAndConditionsAnchorId + "\"></span>"));

        HTMLPanel loginTermsLinkPanel = new HTMLPanel(loginTermsBuilder.toSafeHtml());
        loginTermsLinkPanel.getElement().getStyle().setTextAlign(TextAlign.LEFT);

        Anchor portalTermsAndConditionsAnchor = new TermsAnchor(i18n.tr("ONLINE APPLICATION TERMS AND CONDITIONS"),
                PortalSiteMap.PortalTerms.PortalTermsAndConditions.class);
        loginTermsLinkPanel.addAndReplaceElement(portalTermsAndConditionsAnchor, portalTermsAndConditionsAnchorId);

        Anchor pmcTermsAndConditionsAnchor = new TermsAnchor(i18n.tr("GENERAL RENTAL AND OCCUPANCY CRITERIA GUIDELINES"),
                PortalSiteMap.PortalTerms.PMCTermsAndConditions.class);
        loginTermsLinkPanel.addAndReplaceElement(pmcTermsAndConditionsAnchor, pmcTermsAndConditionsAnchorId);

        contentPanel.add(loginTermsLinkPanel);

        setContent(contentPanel);
    }

    public void setPresenter(LandingPresenter presenter) {
        this.presenter = presenter;
    }

    public void enableHumanVerification() {
        loginForm.setEnableCaptcha(true);
    }

    public void reset(String userId, boolean rememberUserId) {
        loginForm.setVisited(false);
        loginForm.populateNew();
        if (userId != null) {
            loginForm.get(loginForm.proto().email()).setValue(userId);
        }
        loginForm.get(loginForm.proto().rememberID()).setValue(rememberUserId);
        loginForm.setEnableCaptcha(false);
    }

    private void onLogin() {
        loginForm.revalidate();
        loginForm.setUnconditionalValidationErrorRendering(true);
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

            CTextField emailField = inject(proto().email(), new CTextField());
            contentPanel.setWidget(++row, 0, new LoginWidgetDecoratorBuilder(emailField).watermark(LandingViewImpl.i18n.tr("Email Address")).build());
            addValidator(emailField, LandingViewImpl.i18n.tr("Enter your email address"));

            CPasswordTextField passwordField = inject(proto().password(), new CPasswordTextField());
            contentPanel.setWidget(++row, 0, new LoginWidgetDecoratorBuilder(passwordField).watermark(LandingViewImpl.i18n.tr("Password")).build());
            addValidator(passwordField, LandingViewImpl.i18n.tr("Enter your password"));

            contentPanel.setWidget(++row, 0, new CheckBoxDecorator(inject(proto().rememberID(), new CCheckBox())));

            Anchor resetPassword = new Anchor(i18n.tr("Forgot your password?"));
            resetPassword.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    loginGadget.onResetPassword();
                }
            });
            contentPanel.setWidget(++row, 0, resetPassword);

            captchaField = (CCaptcha) inject(proto().captcha());
            contentPanel.setWidget(++row, 0,
                    (new LoginWidgetDecoratorBuilder(captchaField).watermark(LandingViewImpl.i18n.tr("Enter both security words above")).build()));
            setEnableCaptcha(false);

            contentPanel.setBR(++row, 0, 2);

            return contentPanel;
        }

        public final void setEnableCaptcha(boolean isEnabled) {
            captchaField.setVisible(isEnabled);
            if (isEnabled) {
                captchaField.createNewChallenge();
            }

        }

        private <E> void addValidator(CComponent<E> component, final String message) {
            component.setMandatory(false);
            component.addValueValidator(new EditableValueValidator<E>() {
                @Override
                public ValidationError isValid(CComponent<E> component, E value) {
                    if (value == null || ((value instanceof String) && CommonsStringUtils.isEmpty((String) value))) {
                        return new ValidationError(component, message);
                    } else {
                        return null;
                    }
                }
            });
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

}
