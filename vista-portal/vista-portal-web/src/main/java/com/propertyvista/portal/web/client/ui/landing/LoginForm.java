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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.security.rpc.AuthenticationRequest;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.portal.web.client.ui.util.decorators.CheckBoxDecorator;
import com.propertyvista.portal.web.client.ui.util.decorators.LoginDecoratorBuilder;

class LoginForm extends CEntityDecoratableForm<AuthenticationRequest> {

    private CCaptcha captchaField;

    public LoginForm() {
        super(AuthenticationRequest.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel contentPanel = new FlowPanel();

        CTextField emailField = inject(proto().email(), new CTextField());
        contentPanel.add(center(new LoginDecoratorBuilder(emailField).watermark(LandingViewImpl.i18n.tr("Email")).build()));
        addValidator(emailField, LandingViewImpl.i18n.tr("Enter your email address"));

        CPasswordTextField passwordField = inject(proto().password(), new CPasswordTextField());
        contentPanel.add(center(new LoginDecoratorBuilder(passwordField).watermark(LandingViewImpl.i18n.tr("Password")).build()));
        addValidator(passwordField, LandingViewImpl.i18n.tr("Enter your password"));

        captchaField = (CCaptcha) inject(proto().captcha());
        contentPanel
                .add(center((new FormDecoratorBuilder(captchaField).customLabel("").labelWidth(0).useLabelSemicolon(false).mandatoryMarker(false).build())));
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
        return w;
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