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

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.events.NValueChangeEvent;
import com.pyx4j.forms.client.events.NValueChangeHandler;
import com.pyx4j.forms.client.ui.CCaptcha;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.forms.client.validators.password.HasDescription;
import com.pyx4j.forms.client.validators.password.PasswordStrengthValueValidator;
import com.pyx4j.forms.client.validators.password.PasswordStrengthWidget;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.PasswordChangeRequest;

import com.propertyvista.common.client.ui.components.security.TenantPasswordStrengthRule;
import com.propertyvista.portal.shared.ui.CPortalEntityWizard;
import com.propertyvista.portal.shared.ui.util.decorators.LoginWidgetDecoratorBuilder;

public class PasswordChangeWizard extends CPortalEntityWizard<PasswordChangeRequest> {

    private final static I18n i18n = I18n.get(PasswordChangeWizard.class);

    private PasswordStrengthWidget passwordStrengthWidget;

    private final TenantPasswordStrengthRule passwordStrengthRule;

    private CCaptcha captchaField;

    public PasswordChangeWizard(PasswordChangeWizardViewImpl view) {
        super(PasswordChangeRequest.class, view, i18n.tr("Change Password"), i18n.tr("Submit"), ThemeColor.contrast3);
        this.passwordStrengthRule = new TenantPasswordStrengthRule(ClientContext.getUserVisit().getName(), ClientContext.getUserVisit().getName());

        addStep(createStep());

    }

    public BasicFlexFormPanel createStep() {

        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();

        int row = -1;

        mainPanel.setWidget(++row, 0, inject(proto().currentPassword(), new LoginWidgetDecoratorBuilder().build()));
        captchaField = (CCaptcha) inject(proto().captcha(), new LoginWidgetDecoratorBuilder().watermark(i18n.tr("Enter both security words above")).build());
        mainPanel.setWidget(++row, 0, captchaField);
        setCaptchaEnabled(false);

        mainPanel.setBR(++row, 0, 1);

        passwordStrengthWidget = new PasswordStrengthWidget(passwordStrengthRule);
        mainPanel.setWidget(++row, 0, inject(proto().newPassword(), new LoginWidgetDecoratorBuilder().assistantWidget(passwordStrengthWidget).build()));
        mainPanel.setWidget(++row, 0, inject(proto().newPasswordConfirm(), new LoginWidgetDecoratorBuilder().build()));

        if ((passwordStrengthRule != null) && (passwordStrengthRule instanceof HasDescription)) {
            get(proto().newPassword()).setTooltip(((HasDescription) passwordStrengthRule).getDescription());
        } else {
            get(proto().newPassword()).setTooltip(get(proto().newPassword()).getTooltip());
        }

        return mainPanel;
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
            public FieldValidationError isValid() {
                if (getComponent().getValue() != null && !getComponent().getValue().isEmpty()
                        && !getComponent().getValue().equals(get(proto().newPassword()).getValue())) {
                    return new FieldValidationError(getComponent(), i18n.tr("The passwords don't match."));
                } else {
                    return null;
                }
            }
        });

        get(proto().newPassword()).addValueChangeHandler(new RevalidationTrigger<String>(get(proto().newPasswordConfirm())));

        ((CTextFieldBase<?, ?>) get(proto().newPassword())).addNValueChangeHandler(new NValueChangeHandler<String>() {

            @Override
            public void onNValueChange(NValueChangeEvent<String> event) {
                passwordStrengthWidget.ratePassword(event.getValue());
            }
        });

        get(proto().newPassword()).addComponentValidator(new PasswordStrengthValueValidator(passwordStrengthRule));
    }

}