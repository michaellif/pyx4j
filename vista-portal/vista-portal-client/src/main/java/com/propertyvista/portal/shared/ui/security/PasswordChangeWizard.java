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
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.forms.client.validators.password.PasswordStrengthValueValidator;
import com.pyx4j.forms.client.validators.password.PasswordStrengthWidget;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.rpc.PasswordChangeRequest;

import com.propertyvista.common.client.ui.components.security.TenantPasswordStrengthRule;
import com.propertyvista.portal.shared.ui.CPortalEntityWizard;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class PasswordChangeWizard extends CPortalEntityWizard<PasswordChangeRequest> {

    private final static I18n i18n = I18n.get(PasswordChangeWizard.class);

    private PasswordStrengthWidget passwordStrengthWidget;

    private final TenantPasswordStrengthRule passwordStrengthRule;

    public PasswordChangeWizard(PasswordChangeWizardViewImpl view) {
        super(PasswordChangeRequest.class, view, i18n.tr("Change Password"), i18n.tr("Submit"), ThemeColor.contrast3);
        this.passwordStrengthRule = new TenantPasswordStrengthRule(ClientContext.getUserVisit().getName(), ClientContext.getUserVisit().getName());

        addStep(createStep());

    }

    public BasicFlexFormPanel createStep() {

        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();

        int row = -1;

        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().currentPassword())).componentWidth("180px").build());
        mainPanel.setBR(++row, 0, 1);

        passwordStrengthWidget = new PasswordStrengthWidget(passwordStrengthRule);
        mainPanel.setWidget(++row, 0,
                new FormWidgetDecoratorBuilder(inject(proto().newPassword())).componentWidth("180px").assistantWidget(passwordStrengthWidget).build());
        mainPanel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().newPasswordConfirm())).componentWidth("180px").build());

        return mainPanel;
    }

    @Override
    public void addValidations() {
        get(proto().newPasswordConfirm()).addComponentValidator(new AbstractComponentValidator<String>() {
            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() == null || !getComponent().getValue().equals(get(proto().newPassword()).getValue())) {
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