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

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.SystemWallMessage;
import com.pyx4j.site.rpc.AppPlaceInfo;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.DemoData;
import com.propertyvista.portal.client.themes.LandingPagesTheme;
import com.propertyvista.portal.client.ui.components.LandingViewLayoutPanel;
import com.propertyvista.portal.client.ui.components.LandingViewLayoutPanel.Side;
import com.propertyvista.portal.client.ui.util.decorators.CheckBoxDecorator;
import com.propertyvista.portal.client.ui.util.decorators.LoginDecoratorBuilder;

public class LandingViewImpl extends Composite implements LandingView {

    private static final I18n i18n = I18n.get(LandingViewImpl.class);

    public interface LandingHtmlTemplates extends SafeHtmlTemplates {

        public static LandingHtmlTemplates TEMPLATES = GWT.create(LandingHtmlTemplates.class);

        @Template("<div class=\"{2}\"><span class=\"{4}\">{0}</span><span class=\"{3}\">&nbsp;{1}</span></div>")
        SafeHtml landingCaption(String emph, String normal, String style, String textStlye, String emphTextStyle);

        @Template("<div class=\"{0}\"><div style=\"width:0px; height:100%; border-width:1px; border-style:inset;\"/></div>")
        SafeHtml orLineSeparator(String style);

    }

    private class LoginForm extends CEntityDecoratableForm<AuthenticationRequest> {

        private CCaptcha captchaField;

        public LoginForm() {
            super(AuthenticationRequest.class);
        }

        @Override
        public IsWidget createContent() {
            FlowPanel contentPanel = new FlowPanel();

            CTextField emailField = inject(proto().email(), new CTextField());
            contentPanel.add(center(new LoginDecoratorBuilder(emailField, true).watermark(i18n.tr("Email")).build()));
            setMandatoryValidationMessage(emailField, i18n.tr("Enter your email address"));

            CPasswordTextField passwordField = inject(proto().password(), new CPasswordTextField());
            contentPanel.add(center(new LoginDecoratorBuilder(passwordField, true).watermark(i18n.tr("Password")).build()));
            setMandatoryValidationMessage(passwordField, i18n.tr("Enter your password"));

            captchaField = (CCaptcha) inject(proto().captcha());
            contentPanel
                    .add(center((new DecoratorBuilder(captchaField).customLabel("").labelWidth(0).useLabelSemicolon(false).mandatoryMarker(false).build())));
            setEnableCaptcha(false);

            contentPanel.add(center(new CheckBoxDecorator(inject(proto().rememberID(), new CCheckBox()))));

            return contentPanel;
        }

        public final void setEnableCaptcha(boolean isEnabled) {
            captchaField.setVisible(isEnabled);
            if (isEnabled) {
                captchaField.createNewChallenge();
            }

        }

        private Widget center(Widget w) {
            w.addStyleName(LandingPagesTheme.StyleName.LandingInputField.name());
            return w;
        }

        @Deprecated
        // TODO this is workaround to override default validation message(just 'setMandatoryValidationMessage()' is not enough)        
        private <E, C extends CTextFieldBase<E, ?>> void setMandatoryValidationMessage(C c, final String message) {
            c.setMandatory(false);
            c.addValueValidator(new EditableValueValidator<E>() {
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

    private static abstract class DevLoginPanel extends Composite {

        private final FlowPanel devLoginAnchorsPanel;

        private HTML applicationModeLabel;

        private int prevDevKey;

        private int devCount;

        private List<? extends DevLoginCredentials> credentialsSet;

        public DevLoginPanel() {
            FlowPanel devMessagePanel = new FlowPanel();
            devMessagePanel.getElement().getStyle().setMargin(20, Unit.PX);
            devMessagePanel.getElement().getStyle().setProperty("textAlign", "center");
            devMessagePanel.addAttachHandler(new AttachEvent.Handler() {
                private HandlerRegistration handlerRegistration;

                @Override
                public void onAttachOrDetach(AttachEvent event) {
                    if (event.isAttached()) {
                        handlerRegistration = Event.addNativePreviewHandler(new NativePreviewHandler() {
                            @Override
                            public void onPreviewNativeEvent(NativePreviewEvent event) {
                                if (event.getTypeInt() == Event.ONKEYDOWN && event.getNativeEvent().getCtrlKey()) {
                                    if (devCredentialsSelected(event.getNativeEvent().getKeyCode())) {
                                        event.getNativeEvent().preventDefault();
                                    }
                                }
                            }
                        });
                    } else {
                        handlerRegistration.removeHandler();
                    }
                }
            });

            devMessagePanel.add(applicationModeLabel = new HTML());

            devLoginAnchorsPanel = new FlowPanel();
            devMessagePanel.add(devLoginAnchorsPanel);

            initWidget(devMessagePanel);
        }

        public void setApplicationModeName(String applicationModeName) {
            applicationModeLabel.setHTML("This application is running in <B>" + DemoData.applicationModeName() + "</B> mode.");
        }

        public void setDevCredentials(final List<? extends DevLoginCredentials> credentialsSet) {
            this.credentialsSet = credentialsSet;
            devLoginAnchorsPanel.clear();

            for (final DevLoginCredentials credentials : credentialsSet) {
                Anchor touchAnchor = new Anchor(SimpleMessageFormat.format("Press Ctrl+{0} to login as {1}", (char) credentials.getHotKey(), credentials
                        .getUserType().toString()));
                touchAnchor.getElement().getStyle().setProperty("textDecoration", "none");
                touchAnchor.getElement().getStyle().setDisplay(Display.BLOCK);
                touchAnchor.addClickHandler(new ClickHandler() {

                    private final int hotKey = credentials.getHotKey();

                    @Override
                    public void onClick(ClickEvent event) {
                        devCredentialsSelected(hotKey);
                    }

                });
                devLoginAnchorsPanel.add(touchAnchor);
            }
        }

        protected abstract void onDevCredentialsSelected(String userId, String password);

        private boolean devCredentialsSelected(int hotKey) {
            if (prevDevKey != hotKey) {
                devCount = 0;
            }
            prevDevKey = hotKey;
            for (DevLoginCredentials credentials : credentialsSet) {
                if (hotKey == credentials.getHotKey()) {
                    devCount = (devCount % credentials.getUserType().getDefaultMax()) + 1;
                    onDevCredentialsSelected(credentials.getUserType().getEmail(devCount), credentials.getUserType().getEmail(devCount));
                    return true;
                }
            }
            return false;
        }
    }

    private LandingView.Presenter presenter;

    private LoginForm loginForm;

    private Button loginButton;

    private DevLoginPanel devLoginPanel;

    private Button signUpButton;

    private Label signUpGreeting;

    private Anchor termsAndConditionsAnchor;

    public LandingViewImpl() {
        LandingViewLayoutPanel viewPanel = new LandingViewLayoutPanel();
        // attach handler to invoke login via ENTER key
        viewPanel.addAttachHandler(new Handler() {

            private HandlerRegistration handlerRegistration;

            @Override
            public void onAttachOrDetach(AttachEvent event) {
                if (event.isAttached()) {
                    handlerRegistration = Event.addNativePreviewHandler(new NativePreviewHandler() {
                        @Override
                        public void onPreviewNativeEvent(NativePreviewEvent event) {
                            if (event.getTypeInt() == Event.ONKEYUP && (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)) {
                                onLogin();
                            }
                        }
                    });
                } else {
                    handlerRegistration.removeHandler();
                }
            }

        });

        bindLoginWidgets(viewPanel.getLeft());
        bindSingupWidgets(viewPanel.getRight());

        initWidget(viewPanel);
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
        signUpGreeting.setHTML("");
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

    private void bindLoginWidgets(Side sideLayout) {
        sideLayout.getHeader().add(makeCaption(i18n.tr("Welcome."), i18n.tr("Please Login")));

        loginForm = new LoginForm();
        loginForm.initContent();
        sideLayout.getContent().add(loginForm);

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
        sideLayout.getContent().add(loginTermsLinkPanel);

        loginButton = new Button(i18n.tr("LOGIN"), new Command() {
            @Override
            public void execute() {
                onLogin();
            }

        });
        loginButton.addStyleName(LandingPagesTheme.StyleName.LandingButton.name());
        SimplePanel loginButtonHolder = new SimplePanel();
        loginButtonHolder.setStyleName(LandingPagesTheme.StyleName.LandingButtonHolder.name());
        loginButtonHolder.setWidget(loginButton);
        sideLayout.getFooter().add(loginButtonHolder);

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
        sideLayout.getFooter().add(resetPasswordAnchorHolder);

        sideLayout.getFooter().add(devLoginPanel = new DevLoginPanel() {
            @Override
            protected void onDevCredentialsSelected(String userId, String password) {
                loginForm.get(loginForm.proto().email()).setValue(userId);
                loginForm.get(loginForm.proto().password()).setValue(password);
            }
        });
    }

    private void bindSingupWidgets(Side sideLayout) {
        sideLayout.getHeader().add(makeCaption(i18n.tr("First Time."), i18n.tr("Get Started")));

        signUpGreeting = new Label();
        signUpGreeting.setStyleName(LandingPagesTheme.StyleName.LandingGreetingText.name());

        FlowPanel signUpGreetingPanel = new FlowPanel();
        signUpGreetingPanel.setStyleName(LandingPagesTheme.StyleName.LandingGreetingPanel.name());
        // TODO customizable sign up greeting is not implemented and images are used instead        
        // signUpGreetingPanel.setWidget(signUpGreeting);
        signUpGreetingPanel.add(new LoginImageDecoration(LoginAndSignUpResources.INSTANCE.home(), i18n.tr("For a place to call home")));
        signUpGreetingPanel.add(new LoginImageDecoration(LoginAndSignUpResources.INSTANCE.secure(), i18n.tr("Safe and secure")));
        signUpGreetingPanel.add(new LoginImageDecoration(LoginAndSignUpResources.INSTANCE.easy(), i18n.tr("Easy Access")));

        sideLayout.getContent().add(signUpGreetingPanel);

        signUpButton = new Button(i18n.tr("SIGN UP"), new Command() {
            @Override
            public void execute() {
                LandingViewImpl.this.onSignUp();
            }
        });
        signUpButton.addStyleName(LandingPagesTheme.StyleName.LandingButton.name());

        SimplePanel signUpButtonHolder = new SimplePanel();
        signUpButtonHolder.setStyleName(LandingPagesTheme.StyleName.LandingButtonHolder.name());
        signUpButtonHolder.setWidget(signUpButton);
        sideLayout.getFooter().add(signUpButtonHolder);
    }

    private HTML makeCaption(String emph, String normal) {
        return new HTML(LandingHtmlTemplates.TEMPLATES.landingCaption(//@formatter:off
                emph,
                normal,
                LandingPagesTheme.StyleName.LandingCaption.name(),
                LandingPagesTheme.StyleName.LandingCaptionText.name(),
                LandingPagesTheme.StyleName.LandingCaptionTextEmph.name())
        );//@formatter:on
    }

    @Override
    public void setDevLogin(List<? extends DevLoginCredentials> devCredientials, String appModeName) {
        if (devCredientials == null) {
            devLoginPanel.setVisible(false);
            devLoginPanel.setDevCredentials(new LinkedList<DevLoginCredentials>());
        } else {
            devLoginPanel.setVisible(true);
            devLoginPanel.setApplicationModeName(appModeName);
            devLoginPanel.setDevCredentials(devCredientials);
        }
    }

    @Override
    public void setSignupGreetingHtml(String html) {
        signUpGreeting.setHTML(html);
    }

}
