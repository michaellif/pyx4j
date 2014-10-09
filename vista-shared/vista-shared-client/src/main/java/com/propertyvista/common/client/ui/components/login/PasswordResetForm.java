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
package com.propertyvista.common.client.ui.components.login;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.events.NValueChangeEvent;
import com.pyx4j.forms.client.events.NValueChangeHandler;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.forms.client.validators.password.HasDescription;
import com.pyx4j.forms.client.validators.password.PasswordStrengthRule;
import com.pyx4j.forms.client.validators.password.PasswordStrengthValueValidator;
import com.pyx4j.forms.client.validators.password.PasswordStrengthWidget;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.ui.components.security.PasswordResetView;

public class PasswordResetForm extends CForm<PasswordChangeRequest> {

    private final static I18n i18n = I18n.get(PasswordResetForm.class);

    private PasswordStrengthWidget passwordStrengthWidget;

    private PasswordStrengthRule passwordStrengthRule;

    private PasswordStrengthValueValidator passwordStrengthValidator;

    private final PasswordResetView view;

    public PasswordResetForm(PasswordResetView view) {
        super(PasswordChangeRequest.class);
        this.view = view;
        asWidget().setStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name(), true);
        asWidget().getElement().getStyle().setMarginTop(50, Unit.PX);
        asWidget().getElement().getStyle().setMarginBottom(50, Unit.PX);

    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().securityQuestion()).decorate().labelWidth(200);
        formPanel.append(Location.Left, proto().securityAnswer()).decorate().labelWidth(200);

        passwordStrengthWidget = new PasswordStrengthWidget(passwordStrengthRule);
        formPanel.append(Location.Left, proto().newPassword()).decorate().assistantWidget(passwordStrengthWidget).labelWidth(200);
        formPanel.append(Location.Left, proto().newPasswordConfirm()).decorate().labelWidth(200);
        formPanel.append(Location.Left, createSubmitButton());

        get(proto().securityQuestion()).setVisible(false);
        get(proto().securityAnswer()).setVisible(false);

        return formPanel;
    }

    private Button createSubmitButton() {
        final Button submitButton = new Button(i18n.tr("Submit"), new Command() {

            @Override
            public void execute() {
                setVisitedRecursive();
                if (isValid()) {
                    view.getPresenter().resetPassword(getValue());
                } else {
                    // here we hope that because the focus left the form and moved to submitButton,
                    // we get the relevant validation error on the form.
                }
            }
        });
        return submitButton;
    }

    @Override
    public void onReset() {
        super.onReset();
        get(proto().securityQuestion()).setVisible(false);
        get(proto().securityAnswer()).setVisible(false);
    }

    @Override
    public void addValidations() {
        get(proto().newPasswordConfirm()).addComponentValidator(new AbstractComponentValidator<String>() {
            @Override
            public BasicValidationError isValid() {
                if (getComponent().getValue() == null || !getComponent().getValue().equals(get(proto().newPassword()).getValue())) {
                    return new BasicValidationError(getComponent(), i18n.tr("The passwords don't match."));
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

        get(proto().newPassword()).addComponentValidator(passwordStrengthValidator = new PasswordStrengthValueValidator(passwordStrengthRule));
    }

    public void setPasswordStrengthRule(PasswordStrengthRule passwordStrengthRule) {
        this.passwordStrengthRule = passwordStrengthRule;
        passwordStrengthWidget.setPasswordStrengthRule(passwordStrengthRule);
        passwordStrengthValidator.setPasswordStrengthRule(passwordStrengthRule);
        if (passwordStrengthRule != null && (passwordStrengthRule instanceof HasDescription)) {
            get(proto().newPassword()).setTooltip(((HasDescription) passwordStrengthRule).getDescription());
        } else {
            get(proto().newPassword()).setTooltip(null);
        }
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
    }

}