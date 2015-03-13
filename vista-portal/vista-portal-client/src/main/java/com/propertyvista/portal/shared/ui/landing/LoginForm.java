/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 10, 2014
 * @author michaellif
 */
package com.propertyvista.portal.shared.ui.landing;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CEmailField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CPasswordBox;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.dialog.Dialog;

import com.propertyvista.portal.shared.ui.LoginFormPanel;
import com.propertyvista.portal.shared.ui.util.decorators.CheckBoxDecorator;

public class LoginForm extends CForm<AuthenticationRequest> {

    static final I18n i18n = I18n.get(LoginForm.class);

    private CCaptcha captchaField;

    private final ILoginGadget loginGadget;

    public LoginForm(ILoginGadget loginGadget) {
        super(AuthenticationRequest.class);
        this.loginGadget = loginGadget;
    }

    @Override
    protected IsWidget createContent() {

        CEmailField emailField = new CEmailField();
        emailField.setWatermark(i18n.tr("Email Address"));
        emailField.setMandatoryValidationMessage(i18n.tr("Enter your email address"));
        emailField.getNativeComponent().addKeyUpHandler(new EnterKeyHandler());

        CPasswordBox passwordField = new CPasswordBox();
        passwordField.setMandatoryValidationMessage(i18n.tr("Enter your password"));
        passwordField.getNativeComponent().addKeyUpHandler(new EnterKeyHandler());

        captchaField = new CCaptcha();
        captchaField.setWatermark(i18n.tr("Enter both security words above"));
        captchaField.setMandatoryValidationMessage(i18n.tr("Captcha code is required"));
        captchaField.getNativeComponent().addKeyUpHandler(new EnterKeyHandler());
        setCaptchaEnabled(false);

        CCheckBox rememberID = new CCheckBox();
        rememberID.setDecorator(new CheckBoxDecorator());
        rememberID.getNativeComponent().addKeyUpHandler(new EnterKeyHandler());

        Anchor resetPassword = new Anchor(i18n.tr("Forgot your password?"));
        resetPassword.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                loginGadget.onResetPassword();
            }
        });

        // put it all together:
        LoginFormPanel formPanel = new LoginFormPanel(this);

        formPanel.br();
        formPanel.append(Location.Left, proto().email(), emailField).decorate();
        formPanel.append(Location.Left, proto().password(), passwordField).decorate();
        formPanel.append(Location.Left, proto().captcha(), captchaField).decorate();
        formPanel.append(Location.Left, proto().rememberID(), rememberID);
        formPanel.append(Location.Left, resetPassword);
        formPanel.br();

        return formPanel;
    }

    public final void setCaptchaEnabled(boolean isEnabled) {
        captchaField.setMandatory(isEnabled);
        captchaField.setVisible(isEnabled);
        captchaField.setVisited(!isEnabled);
        if (isEnabled) {
            captchaField.createNewChallenge();
        }
    }

    class EnterKeyHandler implements KeyUpHandler {
        @Override
        public void onKeyUp(KeyUpEvent event) {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                loginGadget.setLoginButtonFocus();
                //Dirty fix for Chrome that yields execution to let focus move out, triggering focus lost event on component, which in turn will execute onEditingStop()  
                new Timer() {

                    @Override
                    public void run() {
                        if (!Dialog.isDialogOpen()) {
                            Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                                @Override
                                public void execute() {
                                    loginGadget.onLogin();
                                }
                            });
                        }
                    }

                }.schedule(100);

            }
        }

    }

}