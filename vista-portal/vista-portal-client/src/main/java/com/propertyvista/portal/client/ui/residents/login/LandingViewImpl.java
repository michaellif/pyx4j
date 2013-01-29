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

import com.google.gwt.core.shared.GWT;
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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.SystemWallMessage;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.DemoData;
import com.propertyvista.portal.client.themes.LandingPagesTheme;
import com.propertyvista.portal.client.ui.residents.decorators.CheckBoxDecorator;

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

        private CCaptcha captcha;

        public LoginForm() {
            super(AuthenticationRequest.class);
        }

        @Override
        public IsWidget createContent() {
            FlowPanel contentPanel = new FlowPanel();
            contentPanel.add(center(new DecoratorBuilder(inject(proto().email(), new CTextField())).customLabel("").labelWidth(0).componentWidth(15)
                    .useLabelSemicolon(false).mandatoryMarker(false).build()));
            ((CTextField) get(proto().email())).setWatermark(i18n.tr("Email"));

            // TODO this is workaround to override default validation message(just 'setMandatoryValidationMessage()' is not enough)
            ((CTextField) get(proto().email())).setMandatory(false);
            ((CTextField) get(proto().email())).setMandatoryValidationMessage(i18n.tr("Enter your email address"));
            ((CTextField) get(proto().email())).setMandatory(true);

            contentPanel.add(center(new DecoratorBuilder(inject(proto().password(), new CPasswordTextField())).customLabel("").labelWidth(0).componentWidth(15)
                    .useLabelSemicolon(false).mandatoryMarker(false).build()));
            ((CPasswordTextField) get(proto().password())).setWatermark(i18n.tr("Password"));

            // TODO this is workaround to override default validation message(just 'setMandatoryValidationMessage()' is not enough) 
            ((CPasswordTextField) get(proto().password())).setMandatory(false);
            ((CPasswordTextField) get(proto().password())).setMandatoryValidationMessage(i18n.tr("Enter your password"));
            ((CPasswordTextField) get(proto().password())).setMandatory(true);

            captcha = (CCaptcha) inject(proto().captcha());
            contentPanel.add(center((new DecoratorBuilder(captcha).customLabel("").labelWidth(0).useLabelSemicolon(false).mandatoryMarker(false).build())));
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

    private static class LandingSide extends FlowPanel {

        public LandingSide(String style) {
            setStyleName(style);
        }

    }

    private LandingView.Presenter presenter;

    private LoginForm loginForm;

    private Button loginButton;

    private DevLoginPanel devLoginPanel;

    private Button signUpButton;

    private Label signUpGreeting;

    public LandingViewImpl() {
        FlowPanel viewPanel = new FlowPanel();
        viewPanel.addStyleName(LandingPagesTheme.StyleName.LandingPage.name());
        // attach handler to invoke login via ENTER key
        viewPanel.addAttachHandler(new Handler() {

            private HandlerRegistration handlerRegistration;

            @Override
            public void onAttachOrDetach(AttachEvent event) {
                // TODO Auto-generated method stub
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

        FlowPanel header = new FlowPanel();
        FlowPanel leftHeader = new LandingSide(LandingPagesTheme.StyleName.LandingPageHeader.name());
        FlowPanel rightHeader = new LandingSide(LandingPagesTheme.StyleName.LandingPageHeader.name());
        header.add(leftHeader);
        header.add(rightHeader);
        viewPanel.add(header);

        FlowPanel content = new FlowPanel();
        FlowPanel leftContent = new LandingSide(LandingPagesTheme.StyleName.LandingPageContent.name());
        FlowPanel rightContent = new LandingSide(LandingPagesTheme.StyleName.LandingPageContent.name());
        content.add(leftContent);
        content.add(rightContent);
        viewPanel.add(content);

        FlowPanel footer = new FlowPanel();
        FlowPanel leftFooter = new LandingSide(LandingPagesTheme.StyleName.LandingPageFooter.name());
        FlowPanel rightFooter = new LandingSide(LandingPagesTheme.StyleName.LandingPageFooter.name());
        footer.add(leftFooter);
        footer.add(rightFooter);
        viewPanel.add(footer);

        bindLoginWidgets(leftHeader, leftContent, leftFooter);
        bindSingupWidgets(rightHeader, rightContent, rightFooter);

        final HTML orLine = makeOrLineDecoration();
        initWidget(viewPanel);
    }

    @Override
    public void setPresenter(com.propertyvista.common.client.ui.components.login.LoginView.Presenter presenter) {
        this.presenter = (Presenter) presenter;
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
        // TODO Auto-generated method stub        
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

    private void bindLoginWidgets(FlowPanel leftHeader, FlowPanel leftContent, FlowPanel leftFooter) {
        leftHeader.add(makeCaption(i18n.tr("Welcome."), i18n.tr("Please Login")));

        loginForm = new LoginForm();
        loginForm.initContent();
        leftContent.add(loginForm);

        loginButton = new Button(i18n.tr("LOGIN"));
        loginButton.setStyleName(LandingPagesTheme.StyleName.PortalLandingButton.name());
        loginButton.setCommand(new Command() {

            @Override
            public void execute() {
                onLogin();
            }

        });
        SimplePanel loginButtonHolder = new SimplePanel();
        loginButtonHolder.setStyleName(LandingPagesTheme.StyleName.LandingButtonHolder.name());
        loginButtonHolder.setWidget(loginButton);
        leftFooter.add(loginButtonHolder);

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
        leftFooter.add(resetPasswordAnchorHolder);

        leftFooter.add(devLoginPanel = new DevLoginPanel() {
            @Override
            protected void onDevCredentialsSelected(String userId, String password) {
                loginForm.get(loginForm.proto().email()).setValue(userId);
                loginForm.get(loginForm.proto().password()).setValue(password);
            }
        });
    }

    private void bindSingupWidgets(FlowPanel header, FlowPanel content, FlowPanel footer) {
        header.add(makeCaption(i18n.tr("First Time."), i18n.tr("Get Started")));

        signUpGreeting = new Label();
        signUpGreeting.setStyleName(LandingPagesTheme.StyleName.LandingGreetingText.name());
        // TODO populate this text via activity/presenter
        signUpGreeting.setHTML("TBD TBD TBD TBD TBD TBD TBD TBD TBD TBD TBD TBD TBD TBD TBD TBD TBD TBD.");

        SimplePanel signUpGreetingPanel = new SimplePanel();
        signUpGreetingPanel.setStyleName(LandingPagesTheme.StyleName.LandingGreeting.name());
        signUpGreetingPanel.setWidget(signUpGreeting);
        content.add(signUpGreetingPanel);

        signUpButton = new Button(i18n.tr("SIGN UP"), new Command() {
            @Override
            public void execute() {
                LandingViewImpl.this.onSignUp();
            }
        });
        signUpButton.setStyleName(LandingPagesTheme.StyleName.PortalLandingButton.name());

        SimplePanel signUpButtonHolder = new SimplePanel();
        signUpButtonHolder.setStyleName(LandingPagesTheme.StyleName.LandingButtonHolder.name());
        signUpButtonHolder.setWidget(signUpButton);
        footer.add(signUpButtonHolder);
    }

    private HTML makeOrLineDecoration() {
        HTML orLine = new HTML(LandingHtmlTemplates.TEMPLATES.orLineSeparator(LandingPagesTheme.StyleName.LandingOrLineSeparator.name()));
        return orLine;
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

}
