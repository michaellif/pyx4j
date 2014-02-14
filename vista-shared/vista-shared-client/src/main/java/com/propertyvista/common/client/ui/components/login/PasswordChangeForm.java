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

import java.util.List;
import java.util.Set;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.events.NValueChangeEvent;
import com.pyx4j.forms.client.events.NValueChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.NTextFieldBase;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.forms.client.validators.password.DefaultPasswordStrengthRule;
import com.pyx4j.forms.client.validators.password.HasDescription;
import com.pyx4j.forms.client.validators.password.PasswordStrengthRule;
import com.pyx4j.forms.client.validators.password.PasswordStrengthRule.PasswordStrengthVerdict;
import com.pyx4j.forms.client.validators.password.PasswordStrengthValueValidator;
import com.pyx4j.forms.client.validators.password.PasswordStrengthWidget;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.PasswordChangeRequest;

import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;

public class PasswordChangeForm extends CEntityForm<PasswordChangeRequest> {

    private final static I18n i18n = I18n.get(PasswordChangeForm.class);

    private PasswordStrengthWidget passwordStrengthWidget;

    private PasswordStrengthRule passwordStrengthRule;

    private boolean isCurrentPasswordRequired;

    private boolean isRequireChangePasswordOnNextSignInRequired;

    private PasswordStrengthValueValidator passwordStrengthValidator;

    private PasswordStrengthVerdict enforceRequireChangePasswordThreshold;

    protected Boolean requireChangePasswordOnNextSignInUserDefinedValue;

    private int newPasswordFieldRow;

    private TwoColumnFlexFormPanel mainPanel;

    private NValueChangeHandler<String> passwordValueChangeHandler;

    public PasswordChangeForm(List<String> dictionary, boolean isCurrentPasswordRequired, boolean isRequireChangePasswordOnNextSignInRequired) {
        super(PasswordChangeRequest.class);
        this.passwordStrengthRule = new DefaultPasswordStrengthRule();
        this.isCurrentPasswordRequired = isCurrentPasswordRequired;
        this.isRequireChangePasswordOnNextSignInRequired = isRequireChangePasswordOnNextSignInRequired;
        asWidget().setStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name(), true);
        asWidget().getElement().getStyle().setMarginTop(50, Unit.PX);
        asWidget().getElement().getStyle().setMarginBottom(50, Unit.PX);

    }

    public PasswordChangeForm() {
        this(null, true, true);
    }

    @Override
    public IsWidget createContent() {

        mainPanel = new TwoColumnFlexFormPanel();
        mainPanel.setWidth("40em");
        mainPanel.setStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name(), true);

        int row = -1;

        mainPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().currentPassword())).componentWidth(15).labelWidth(15).build());
        mainPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setPaddingBottom(1., Unit.EM);

        passwordStrengthWidget = new PasswordStrengthWidget(passwordStrengthRule);
        mainPanel.setWidget(newPasswordFieldRow = ++row, 0, 2, new FormDecoratorBuilder(inject(proto().newPassword())).componentWidth(15).labelWidth(15)
                .assistantWidget(passwordStrengthWidget).build());
        mainPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().newPasswordConfirm())).componentWidth(15).labelWidth(15).build());

        mainPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().requireChangePasswordOnNextSignIn())).componentWidth(15).labelWidth(15)
                .build());

        return mainPanel;
    }

    @Override
    public void addValidations() {
        get(proto().newPasswordConfirm()).addValueValidator(new EditableValueValidator<String>() {
            @Override
            public FieldValidationError isValid(CComponent<String> component, String value) {
                if (value == null || !value.equals(get(proto().newPassword()).getValue())) {
                    return new FieldValidationError(component, i18n.tr("The passwords don't match."));
                } else {
                    return null;
                }
            }
        });

        get(proto().newPassword()).addValueChangeHandler(new RevalidationTrigger<String>(get(proto().newPasswordConfirm())));

        ((CTextFieldBase<?, ?>) get(proto().newPassword())).addNValueChangeHandler(passwordValueChangeHandler = new NValueChangeHandler<String>() {
            @Override
            public void onNValueChange(NValueChangeEvent<String> event) {
                passwordStrengthWidget.ratePassword(event.getValue());
                if (event.getValue() != null && enforceRequireChangePasswordThreshold != null && passwordStrengthRule != null) {
                    PasswordStrengthVerdict verdict = passwordStrengthRule.getPasswordVerdict(event.getValue());
                    if (verdict != null && verdict.compareTo(enforceRequireChangePasswordThreshold) <= 0) {
                        requireChangePasswordOnNextSignInUserDefinedValue = get(proto().requireChangePasswordOnNextSignIn()).getValue();
                        get(proto().requireChangePasswordOnNextSignIn()).setValue(true);
                        get(proto().requireChangePasswordOnNextSignIn()).setEnabled(false);
                    } else {
                        get(proto().requireChangePasswordOnNextSignIn()).setValue(requireChangePasswordOnNextSignInUserDefinedValue);
                        get(proto().requireChangePasswordOnNextSignIn()).setEnabled(true);
                    }
                }
            }
        });

        get(proto().newPassword()).addComponentValidator(passwordStrengthValidator = new PasswordStrengthValueValidator(passwordStrengthRule));
    }

    public void setAskForCurrentPassword(boolean isCurrentPasswordRequired) {
        this.isCurrentPasswordRequired = isCurrentPasswordRequired;
        get(proto().currentPassword()).setVisible(this.isCurrentPasswordRequired);
    }

    public void setAskForRequireChangePasswordOnNextSignIn(boolean isRequireChangePasswordOnNextSignInRequired, Boolean requirePasswordChangeOnNextSignIn,
            PasswordStrengthVerdict enforceRequireChangePasswordThreshold) {
        this.isRequireChangePasswordOnNextSignInRequired = isRequireChangePasswordOnNextSignInRequired;
        this.enforceRequireChangePasswordThreshold = enforceRequireChangePasswordThreshold;

        if (requirePasswordChangeOnNextSignIn != null && isRequireChangePasswordOnNextSignInRequired) {
            get(proto().requireChangePasswordOnNextSignIn()).setValue(requirePasswordChangeOnNextSignIn);
            this.requireChangePasswordOnNextSignInUserDefinedValue = requirePasswordChangeOnNextSignIn;
        }
        get(proto().requireChangePasswordOnNextSignIn()).setVisible(this.isRequireChangePasswordOnNextSignInRequired);
        get(proto().requireChangePasswordOnNextSignIn()).setEnabled(this.isRequireChangePasswordOnNextSignInRequired);

    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().currentPassword()).setVisible(isCurrentPasswordRequired);
        get(proto().requireChangePasswordOnNextSignIn()).setVisible(isRequireChangePasswordOnNextSignInRequired);

    }

    public void setPasswordStrengthRule(PasswordStrengthRule passwordStrengthRule) {
        this.passwordStrengthRule = passwordStrengthRule;
        this.passwordStrengthValidator.setPasswordStrengthRule(passwordStrengthRule);
        this.passwordStrengthWidget.setPasswordStrengthRule(passwordStrengthRule);

        if ((passwordStrengthRule != null) && (passwordStrengthRule instanceof HasDescription)) {
            get(proto().newPassword()).setTooltip(((HasDescription) passwordStrengthRule).getDescription());
        } else {
            get(proto().newPassword()).setTooltip(null);
        }

    }

    public void setEnforcedPasswordStrengths(Set<PasswordStrengthVerdict> validPasswordStrengths) {
        this.passwordStrengthValidator.setAcceptedVerdicts(validPasswordStrengths);
    }

    public void setMaskPassword(boolean maskPassword) {
        get(proto().newPasswordConfirm()).setVisible(maskPassword);
        unbind(proto().newPassword());
        CTextFieldBase<String, ? extends NTextFieldBase<String, ?, CTextFieldBase<String, ?>>> c = maskPassword ? new CPasswordTextField() : new CTextField();
        c.addNValueChangeHandler(passwordValueChangeHandler);
        c.addComponentValidator(passwordStrengthValidator);
        c.addValueChangeHandler(new RevalidationTrigger<String>(get(proto().newPasswordConfirm())));

        mainPanel.setWidget(newPasswordFieldRow, 0, 2, new FormDecoratorBuilder(inject(proto().newPassword(), c)).componentWidth(15).labelWidth(15)
                .assistantWidget(passwordStrengthWidget).build());
        setPasswordStrengthRule(passwordStrengthRule); // to redraw tooltip

    }

}