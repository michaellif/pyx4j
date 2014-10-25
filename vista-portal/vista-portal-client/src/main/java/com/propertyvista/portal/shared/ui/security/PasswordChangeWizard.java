/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui.security;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.events.NativeValueChangeEvent;
import com.pyx4j.forms.client.events.NativeValueChangeHandler;
import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.CPasswordBox;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.forms.client.validators.password.HasDescription;
import com.pyx4j.forms.client.validators.password.PasswordStrengthValueValidator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.widgets.client.PasswordBox.PasswordStrengthWidget;

import com.propertyvista.common.client.ui.components.security.TenantPasswordStrengthRule;
import com.propertyvista.portal.shared.ui.CPortalEntityWizard;
import com.propertyvista.portal.shared.ui.LoginFormPanel;

public class PasswordChangeWizard extends CPortalEntityWizard<PasswordChangeRequest> {

    private final static I18n i18n = I18n.get(PasswordChangeWizard.class);

    private final TenantPasswordStrengthRule passwordStrengthRule;

    private CCaptcha captchaField;

    public PasswordChangeWizard(PasswordChangeWizardViewImpl view) {
        super(PasswordChangeRequest.class, view, i18n.tr("Change Password"), i18n.tr("Submit"), ThemeColor.contrast3);
        this.passwordStrengthRule = new TenantPasswordStrengthRule(ClientContext.getUserVisit().getName(), ClientContext.getUserVisit().getName());

        addStep(createStep(), i18n.tr("General"));

    }

    public IsWidget createStep() {

        LoginFormPanel formPanel = new LoginFormPanel(this);

        formPanel.append(Location.Left, proto().currentPassword()).decorate();
        ((CPasswordBox) get(proto().currentPassword())).setWatermark(get(proto().currentPassword()).getTitle());

        formPanel.append(Location.Left, proto().captcha());
        captchaField = (CCaptcha) get(proto().captcha());
        captchaField.setWatermark(i18n.tr("Enter both security words above"));
        setCaptchaEnabled(false);

        formPanel.br();

        formPanel.append(Location.Left, proto().newPassword()).decorate();
        formPanel.append(Location.Left, proto().newPasswordConfirm()).decorate();

        ((CPasswordBox) get(proto().newPassword())).setWatermark(get(proto().newPassword()).getTitle());
        ((CPasswordBox) get(proto().newPasswordConfirm())).setWatermark(get(proto().newPasswordConfirm()).getTitle());

        if ((passwordStrengthRule != null) && (passwordStrengthRule instanceof HasDescription)) {
            get(proto().newPassword()).setTooltip(((HasDescription) passwordStrengthRule).getDescription());
        } else {
            get(proto().newPassword()).setTooltip(get(proto().newPassword()).getTooltip());
        }

        return formPanel;
    }

    public final void setCaptchaEnabled(boolean isEnabled) {
        captchaField.setVisible(isEnabled);
        if (isEnabled) {
            captchaField.createNewChallenge();
        }

    }

    @Override
    public void onReset() {
        setCaptchaEnabled(false);
        super.onReset();
    }

    @Override
    public void addValidations() {
        get(proto().newPasswordConfirm()).addComponentValidator(new AbstractComponentValidator<String>() {
            @Override
            public BasicValidationError isValid() {
                if (getComponent().getValue() != null && !getComponent().getValue().isEmpty()
                        && !getComponent().getValue().equals(get(proto().newPassword()).getValue())) {
                    return new BasicValidationError(getComponent(), i18n.tr("The passwords don't match."));
                } else {
                    return null;
                }
            }
        });

        get(proto().newPassword()).addValueChangeHandler(new RevalidationTrigger<String>(get(proto().newPasswordConfirm())));

        ((CPasswordBox) get(proto().newPassword())).setPasswordStrengthRule(passwordStrengthRule);

        get(proto().newPassword()).addComponentValidator(new PasswordStrengthValueValidator(passwordStrengthRule));
    }

}