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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
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
import com.propertyvista.portal.client.ui.residents.decorators.CheckBoxDecorator;

public class LandingViewImpl extends Composite implements LandingView {

    private static final I18n i18n = I18n.get(LandingViewImpl.class);

    private LandingView.Presenter presenter;

    private LoginForm loginForm;

    private Button loginButton;

    private DevLoginPanel devLoginPanel;

    private Button signUpButton;

    private Label signUpGreeting;

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

    private static abstract class DevLoginPanel extends Composite {

        private final FlowPanel devLoginAnchorsPanel;

        private HTML applicationModeLabel;

        private int prevDevKey;

        private int devCount;

        private List<? extends DevLoginCredentials> credentialsSet;

        public DevLoginPanel() {
            FlowPanel devMessagePanel = new FlowPanel();
            devMessagePanel.getElement().getStyle().setProperty("textAlign", "center");
            devMessagePanel.getElement().getStyle().setMarginTop(3, Unit.EM);
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

    public LandingViewImpl() {
        LayoutPanel viewPanel = new LayoutPanel();
//        viewPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
//        viewPanel.getElement().getStyle().setLeft(0, Unit.PCT);
//        viewPanel.getElement().getStyle().setWidth(100, Unit.PCT);
//        viewPanel.getElement().getStyle().setTop(150, Unit.PX);
//        viewPanel.getElement().getStyle().setHeight(300, Unit.PX);

//        viewPanel.getElement().getStyle().setMarginTop(-150, Unit.PX);
//        viewPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
//        viewPanel.getElement().getStyle().setBorderWidth(2, Unit.PX);
//        viewPanel.getElement().getStyle().setBorderColor("grey");
//        viewPanel.getElement().getStyle().setProperty("borderRadius", "10px");

        FlowPanel loginPanel = makeLoginPanel();
        viewPanel.add(loginPanel);
        viewPanel.setWidgetLeftRight(loginPanel, 0, Unit.PCT, 50, Unit.PCT);
        viewPanel.setWidgetTopHeight(loginPanel, 0, Unit.PCT, 300, Unit.PX);

        FlowPanel signUpPanel = makeSignUpPanel();
        viewPanel.add(signUpPanel);
        viewPanel.setWidgetLeftRight(signUpPanel, 50, Unit.PCT, 0, Unit.PCT);
        viewPanel.setWidgetTopHeight(signUpPanel, 0, Unit.PCT, 300, Unit.PX);

        final HTML orLine = makeOrLineDecoration();
        viewPanel.add(orLine);
        viewPanel.setWidgetLeftRight(orLine, 50, Unit.PCT, 50, Unit.PCT);
        viewPanel.setWidgetTopHeight(orLine, 0, Unit.PCT, 300, Unit.PX);

        // TODO find a better way to make "singup" and "login" buttons to be properly aligned
        // TODO orLineAdjustmens
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                int commonHeight = Math.max(loginForm.asWidget().getElement().getClientHeight(), signUpGreeting.getElement().getClientHeight());
                loginForm.asWidget().getElement().getStyle().setHeight(commonHeight, Unit.PX);
                signUpGreeting.asWidget().getElement().getStyle().setHeight(commonHeight, Unit.PX);

                orLine.getElement().getParentElement().getStyle().setOverflow(Overflow.VISIBLE);
            }
        });

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

    private FlowPanel makeLoginPanel() {
        FlowPanel loginPanel = new FlowPanel();
        loginPanel.getElement().getStyle().setProperty("marginLeft", "auto");
        loginPanel.getElement().getStyle().setProperty("marginRight", "auto");
        loginPanel.addAttachHandler(new Handler() {

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

        loginPanel.add(makeCaption(i18n.tr("Welcome."), i18n.tr("Please Login")));

        loginForm = new LoginForm();
        loginForm.initContent();
        loginPanel.add(loginForm);

        loginButton = new Button(i18n.tr("LOGIN"));
        loginButton.setCommand(new Command() {

            @Override
            public void execute() {
                onLogin();
            }

        });

        SimplePanel loginButtonHolder = new SimplePanel();
        loginButtonHolder.setWidth("100%");
        loginButtonHolder.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        loginButtonHolder.getElement().getStyle().setMarginTop(20, Unit.PX);
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

        loginPanel.add(devLoginPanel = new DevLoginPanel() {
            @Override
            protected void onDevCredentialsSelected(String userId, String password) {
                loginForm.get(loginForm.proto().email()).setValue(userId);
                loginForm.get(loginForm.proto().password()).setValue(password);
            }
        });
        return loginPanel;
    }

    private FlowPanel makeSignUpPanel() {
        FlowPanel signUpPanel = new FlowPanel();
        signUpPanel.add(makeCaption(i18n.tr("First Time."), i18n.tr("Get Started")));

        signUpGreeting = new Label();
        // TODO set style via CSS
        signUpGreeting.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
        signUpGreeting.getElement().getStyle().setFontSize(18, Unit.PX);
        signUpGreeting.getElement().getStyle().setMarginLeft(40, Unit.PX);
        signUpGreeting.getElement().getStyle().setMarginRight(40, Unit.PX);

        signUpGreeting.setHTML(i18n.tr("If you are looking to find a place call home why not look at {0}'s properties. Sign&nbsp;up and just start looking.",
                "Red Rige"));
        signUpPanel.add(signUpGreeting);

        signUpButton = new Button(i18n.tr("SIGN UP"), new Command() {
            @Override
            public void execute() {
                LandingViewImpl.this.onSignUp();
            }
        });
        SimplePanel signUpButtonHolder = new SimplePanel();
        signUpButtonHolder.setWidth("100%");
        signUpButtonHolder.getElement().getStyle().setMarginTop(20, Unit.PX);
        signUpButtonHolder.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        signUpButtonHolder.setWidget(signUpButton);

        signUpPanel.add(signUpButtonHolder);
        return signUpPanel;
    }

    private HTML makeOrLineDecoration() {
        HTML orLine = new HTML(
                "<div style=\"position: relative;\"><div style=\"position: absolute; top: 0px; height: 300px; left: 50%; right: 50%; border-width:1px; border-style: solid; border-color: grey;\"/>"
                        + "<div style=\"position: absolute; top: 50%; height: 20px; left: 50%; width: 30px; margin-left:-15px; margin-top:-15px; text-align: center; background-color: white; cursor: default;\"/>"
                        + i18n.tr("or") + "</div></div>");
        orLine.getElement().getStyle().setHeight(300, Unit.PX);
        orLine.getElement().getStyle().setWidth(10, Unit.PX);
        orLine.getElement().getStyle().setProperty("display", "table-cell");
        orLine.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

        return orLine;
    }

    private HTML makeCaption(String emph, String normal) {
        // TODO fix this using SafeHtmlTemplates and CSS
        return new HTML("<div style=\"text-align: center; font-size: 20px; width: 100%; margin-top: 25px; margin-bottom:25px\"><b>" + emph + "</b>" + "&nbsp;"
                + normal);
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
