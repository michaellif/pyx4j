/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 14, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.login;

import static com.pyx4j.commons.HtmlUtils.h2;

import java.util.List;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.NFocusField;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.security.rpc.SystemWallMessage;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.Dialog;

import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.ui.components.login.AbstractLoginViewImpl.DevLoginData;
import com.propertyvista.domain.DemoData;
import com.propertyvista.shared.config.VistaDemo;

// TODO Dev Login needs refactoring (populate devLoginData, and mode from outside, see portal LandingViewImpl for how to)
@Deprecated
public class LoginForm extends CForm<AuthenticationRequest> {

    private static final I18n i18n = I18n.get(LoginForm.class);

    private final String caption;

    private FlowPanel wallMessagePanel;

    private final Command loginCommand;

    private final Command resetPasswordCommand;

    private final List<DevLoginData> devLoginValues;

    private CTextField emailField;

    private int devCount = 0;

    private int prevDevKey = -1;

    public LoginForm(String caption, Command loginCommand, Command resetPasswordCommand) {
        this(caption, loginCommand, resetPasswordCommand, null);
    }

    public LoginForm(String caption, Command loginCommand, Command resetPasswordCommand, List<DevLoginData> devLoginValues) {
        super(AuthenticationRequest.class);
        this.caption = caption;
        this.loginCommand = loginCommand;
        this.resetPasswordCommand = resetPasswordCommand;
        if (ApplicationMode.isDevelopment() || VistaDemo.isDemo()) {
            resetDevLoginHistory();
            this.devLoginValues = devLoginValues;
        } else {
            this.devLoginValues = null;
        }
        asWidget().setWidth("30em");
        asWidget().setStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name(), true);
        asWidget().getElement().getStyle().setMarginTop(50, Unit.PX);
        asWidget().getElement().getStyle().setMarginBottom(50, Unit.PX);
    }

    @Override
    protected IsWidget createContent() {
        HTML header = new HTML(h2(caption));
        header.getElement().getStyle().setMarginBottom(3, Unit.EM);
        header.getElement().getStyle().setProperty("textAlign", "center");

        FlowPanel main = new FlowPanel();
        main.add(header);

        emailField = inject(proto().email(), new CTextField(), new LoginPanelWidgetDecorator());
        emailField.getNativeComponent().addKeyUpHandler(new EnterKeyHandler());
        main.add(emailField);

        CPasswordTextField passwordField = inject(proto().password(), new CPasswordTextField(), new LoginPanelWidgetDecorator());
        passwordField.getNativeComponent().addKeyUpHandler(new EnterKeyHandler());
        main.add(passwordField);

        CCaptcha captchaField = (CCaptcha) inject(proto().captcha(), new LoginPanelWidgetDecorator(30));
        captchaField.getNativeComponent().addKeyUpHandler(new EnterKeyHandler());
        main.add(captchaField);

        CCheckBox rememberID = inject(proto().rememberID(), new CCheckBox(), new LoginPanelWidgetDecorator());
        rememberID.getNativeComponent().addKeyUpHandler(new EnterKeyHandler());
        main.add(rememberID);

        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        buttonPanel.getElement().getStyle().setMarginTop(10, Unit.PX);
        Button loginButton = new Button(i18n.tr("Login"), new Command() {

            @Override
            public void execute() {
                // TODO Auto-generated method stub
                login();
            }

        });
        loginButton.ensureDebugId(CrudDebugId.Criteria_Submit.toString());

        loginButton.getElement().getStyle().setMarginLeft(9, Unit.EM);
        loginButton.getElement().getStyle().setMarginRight(1, Unit.EM);
        buttonPanel.add(loginButton);

        if (resetPasswordCommand != null) {
            Anchor forgotPassword = new Anchor(null);
            forgotPassword.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    resetPasswordCommand.execute();
                }
            });

            forgotPassword.setHTML(i18n.tr("Reset Password"));
            buttonPanel.add(forgotPassword);
        }
        main.add(buttonPanel);

        wallMessagePanel = new FlowPanel();
        main.add(wallMessagePanel);

        if ((ApplicationMode.isDevelopment() || VistaDemo.isDemo()) && devLoginValues != null) {
            main.add(createDevMessagePanel());
            main.addAttachHandler(new AttachEvent.Handler() {
                private HandlerRegistration handlerRegistration;

                @Override
                public void onAttachOrDetach(AttachEvent event) {
                    if (event.isAttached()) {
                        handlerRegistration = Event.addNativePreviewHandler(new NativePreviewHandler() {
                            @Override
                            public void onPreviewNativeEvent(NativePreviewEvent event) {
                                if ((event.getTypeInt() == Event.ONKEYDOWN && event.getNativeEvent().getCtrlKey())) {
                                    if (onDevLoginRequest(event.getNativeEvent().getKeyCode())) {
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
        }
        return main;
    }

    private Widget createDevMessagePanel() {
        FlowPanel devMessagePanel = new FlowPanel();

        devMessagePanel.add(new HTML("This application is running in <B>" + DemoData.applicationModeName() + "</B> mode."));

        for (final DevLoginData loginVal : devLoginValues) {
            Anchor touchAnchor = new Anchor(SimpleMessageFormat.format("Press Ctrl+{0} to login as {1}", loginVal.shortcut, loginVal.user.toString()));
            touchAnchor.addClickHandler(new ClickHandler() {
                private final int shortcut = loginVal.shortcut;

                @Override
                public void onClick(ClickEvent event) {
                    if (onDevLoginRequest(shortcut)) {
                        event.getNativeEvent().preventDefault();
                    }
                }
            });
            touchAnchor.getElement().getStyle().setProperty("textDecoration", "none");
            touchAnchor.getElement().getStyle().setDisplay(Display.BLOCK);
            devMessagePanel.add(touchAnchor);
        }
        devMessagePanel.getElement().getStyle().setProperty("textAlign", "center");
        devMessagePanel.getElement().getStyle().setMarginTop(3, Unit.EM);
        return devMessagePanel;
    }

    /**
     * Creates new challenge and makes captcha visible.
     */
    public void reEnableCaptcha() {
        CCaptcha captcha = (CCaptcha) get(proto().captcha());
        captcha.createNewChallenge();
        captcha.setVisible(true);
        captcha.setVisited(false);
        captcha.setMandatory(true);
    }

    /**
     * Renders captcha invisible.
     */
    public void disableCaptcha() {
        ((CCaptcha) get(proto().captcha())).setMandatory(false);
        ((CCaptcha) get(proto().captcha())).setVisible(false);
    }

    public void resetDevLoginHistory() {
        devCount = 0;
        prevDevKey = -1;
    }

    protected void login() {
        resetDevLoginHistory();
        loginCommand.execute();
    }

    public void setWallMessage(SystemWallMessage message) {
        wallMessagePanel.clear();
        wallMessagePanel.getElement().getStyle().setProperty("textAlign", "center");
        wallMessagePanel.getElement().getStyle().setMargin(2, Unit.EM);

        if (message != null) {
            HTML html = new HTML(message.getMessage());
            wallMessagePanel.add(html);

            if (message.isWarning()) {
                html.getElement().getStyle().setColor("orange");
            }
        }
    }

    /**
     * Should be passed as callback to even handlers that monitor developer login.
     * 
     * @param shortcut
     *            key code of the developer login
     * @return <code>true</code> if developer's credentials that correspond to the provided key were found, otherwise <code>false</code>.
     */
    protected boolean onDevLoginRequest(int shortcut) {
        if (prevDevKey != shortcut) {
            devCount = 0;
        }
        prevDevKey = shortcut;

        for (DevLoginData val : devLoginValues) {
            if (shortcut == val.shortcut) {
                devCount = (devCount % val.user.getDefaultMax()) + 1;
                get(proto().email()).setValue(val.user.getEmail(devCount));
                get(proto().password()).setValue(val.user.getEmail(devCount));
                ((NFocusField) get(proto().email()).asWidget()).setFocus(true);
                return true;
            }
        }
        return false;
    }

    class EnterKeyHandler implements KeyUpHandler {

        @Override
        public void onKeyUp(KeyUpEvent event) {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                if (!Dialog.isDialogOpen()) {
                    login();
                }
            }
        }

    }

}