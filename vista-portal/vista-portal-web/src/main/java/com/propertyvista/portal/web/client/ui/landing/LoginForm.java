/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 11, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.landing;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AuthenticationRequest;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.portal.web.client.ui.util.decorators.CheckBoxDecorator;
import com.propertyvista.portal.web.client.ui.util.decorators.LoginDecoratorBuilder;

class LoginForm extends CEntityForm<AuthenticationRequest> {

    static final I18n i18n = I18n.get(LoginForm.class);

    private CCaptcha captchaField;

    private final LoginGadget loginGadget;

    public LoginForm(LoginGadget loginGadget) {
        super(AuthenticationRequest.class);
        this.loginGadget = loginGadget;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel contentPanel = new FormFlexPanel();

        int row = -1;

        contentPanel.setBR(++row, 0, 2);

        CTextField emailField = inject(proto().email(), new CTextField());
        contentPanel.setWidget(++row, 0, new LoginDecoratorBuilder(emailField, "280px").watermark(LandingViewImpl.i18n.tr("Email Address")).build());
        addValidator(emailField, LandingViewImpl.i18n.tr("Enter your email address"));

        CPasswordTextField passwordField = inject(proto().password(), new CPasswordTextField());
        contentPanel.setWidget(++row, 0, new LoginDecoratorBuilder(passwordField, "280px").watermark(LandingViewImpl.i18n.tr("Password")).build());
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
                (new LoginDecoratorBuilder(captchaField, "280px").watermark(LandingViewImpl.i18n.tr("Enter both security words above")).build()));
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