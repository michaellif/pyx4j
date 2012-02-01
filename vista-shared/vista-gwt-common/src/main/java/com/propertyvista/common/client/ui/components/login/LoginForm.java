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
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.essentials.client.crud.CrudDebugId;
import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AuthenticationRequest;

import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.ui.components.login.AbstractLoginViewImpl.DevLoginData;
import com.propertyvista.domain.DemoData;

public class LoginForm extends CEntityEditor<AuthenticationRequest> {

    private static final I18n i18n = I18n.get(LoginForm.class);

    private final String caption;

    private final Command loginCommand;

    private final Command resetPasswordCommand;

    private final List<DevLoginData> devLoginValues;

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
        if (ApplicationMode.isDevelopment()) {
            resetDevLoginHistory();
            this.devLoginValues = devLoginValues;
        } else {
            this.devLoginValues = null;
        }
        setWidth("30em");
    }

    @Override
    protected void onWidgetCreated() {
        super.onWidgetCreated();
        asWidget().setStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name(), true);
        asWidget().getElement().getStyle().setMarginTop(5, Unit.PCT);
        asWidget().getElement().getStyle().setMarginBottom(5, Unit.PCT);

    }

    @Override
    public IsWidget createContent() {
        HTML header = new HTML(h2(caption));
        header.getElement().getStyle().setMarginBottom(3, Unit.EM);
        header.getElement().getStyle().setProperty("textAlign", "center");

        FlowPanel main = new FlowPanel();
        main.add(header);

        main.add(new LoginPanelWidgetDecorator(inject(proto().email())));
        main.add(new LoginPanelWidgetDecorator(inject(proto().password())));
        main.add(new LoginPanelWidgetDecorator(inject(proto().captcha())));

        Button loginButton = new Button(i18n.tr("Login"));
        loginButton.ensureDebugId(CrudDebugId.Criteria_Submit.toString());
        loginButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                login();
            }
        });

        loginButton.getElement().getStyle().setMarginLeft(9, Unit.EM);
        loginButton.getElement().getStyle().setMarginRight(1, Unit.EM);
        loginButton.getElement().getStyle().setMarginTop(0.5, Unit.EM);
        main.add(loginButton);

        if (resetPasswordCommand != null) {
            CHyperlink forgotPassword = new CHyperlink(null, resetPasswordCommand);
            forgotPassword.setValue(i18n.tr("Reset Password"));
            main.add(forgotPassword);
        }

        if (ApplicationMode.isDevelopment() & devLoginValues != null) {
            main.add(createDevMessagePanel());
        }

        main.addAttachHandler(new AttachEvent.Handler() {
            private HandlerRegistration handlerRegistration;

            @Override
            public void onAttachOrDetach(AttachEvent event) {
                if (event.isAttached()) {
                    handlerRegistration = Event.addNativePreviewHandler(new NativePreviewHandler() {
                        @Override
                        public void onPreviewNativeEvent(NativePreviewEvent event) {
                            if ((ApplicationMode.isDevelopment()) && (event.getTypeInt() == Event.ONKEYDOWN && event.getNativeEvent().getCtrlKey())) {
                                if (onDevLoginRequest(event.getNativeEvent().getKeyCode())) {
                                    event.getNativeEvent().preventDefault();
                                }
                            }
                            if (event.getTypeInt() == Event.ONKEYUP && (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)) {
                                login();
                            }
                        }
                    });
                } else {
                    handlerRegistration.removeHandler();
                }
            }
        });
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
    }

    /**
     * Renders captcha invisible.
     */
    public void disableCaptcha() {
        CCaptcha captcha = (CCaptcha) get(proto().captcha());
        captcha.setVisible(false);
    }

    public void resetDevLoginHistory() {
        devCount = 0;
        prevDevKey = -1;
    }

    protected void login() {
        resetDevLoginHistory();
        loginCommand.execute();
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
                return true;
            }
        }
        return false;
    }
}