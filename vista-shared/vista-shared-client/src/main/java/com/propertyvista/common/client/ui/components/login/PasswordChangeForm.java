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

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CPasswordBox;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.AbstractValidationError;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.forms.client.validators.password.DefaultPasswordStrengthRule;
import com.pyx4j.forms.client.validators.password.HasDescription;
import com.pyx4j.forms.client.validators.password.PasswordStrengthValueValidator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.PasswordChangeRequest;
import com.pyx4j.widgets.client.PasswordBox.PasswordStrengthRule;
import com.pyx4j.widgets.client.PasswordBox.PasswordStrengthRule.PasswordStrengthVerdict;

import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;

public class PasswordChangeForm extends CForm<PasswordChangeRequest> {

    private final static I18n i18n = I18n.get(PasswordChangeForm.class);

    private PasswordStrengthRule passwordStrengthRule;

    private boolean isCurrentPasswordRequired;

    private boolean isRequireChangePasswordOnNextSignInRequired;

    private PasswordStrengthValueValidator passwordStrengthValidator;

    private PasswordStrengthVerdict enforceRequireChangePasswordThreshold;

    protected Boolean requireChangePasswordOnNextSignInUserDefinedValue;

    public PasswordChangeForm(List<String> dictionary, boolean isCurrentPasswordRequired, boolean isRequireChangePasswordOnNextSignInRequired) {
        super(PasswordChangeRequest.class);
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
    protected IsWidget createContent() {

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().currentPassword()).decorate();

        formPanel.append(Location.Left, proto().newPassword()).decorate();
        formPanel.append(Location.Left, proto().newPasswordConfirm()).decorate();
        formPanel.append(Location.Left, proto().passwordChangeRequired()).decorate().componentWidth(30);

        return formPanel;
    }

    @Override
    public void addValidations() {

        get(proto().newPasswordConfirm()).addComponentValidator(new AbstractComponentValidator<String>() {
            @Override
            public BasicValidationError isValid() {
                if (getCComponent().getValue() != null && !getCComponent().getValue().equals(get(proto().newPassword()).getValue())) {
                    return new BasicValidationError(getCComponent(), i18n.tr("The passwords don't match."));
                } else {
                    return null;
                }
            }
        });

        get(proto().newPassword()).addValueChangeHandler(new RevalidationTrigger<String>(get(proto().newPasswordConfirm())));
        get(proto().newPassword()).addComponentValidator(passwordStrengthValidator = new PasswordStrengthValueValidator());

        get(proto().newPassword()).addValueChangeHandler(new RevalidationTrigger<String>(get(proto().passwordChangeRequired())));
        get(proto().newPassword()).addPropertyChangeHandler(new RevalidationTrigger<String>(get(proto().passwordChangeRequired())));
        get(proto().passwordChangeRequired()).addComponentValidator(new PasswordChangeRequiredValidator());

        setPasswordStrengthRule(new DefaultPasswordStrengthRule());

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
            get(proto().passwordChangeRequired()).setValue(requirePasswordChangeOnNextSignIn);
            this.requireChangePasswordOnNextSignInUserDefinedValue = requirePasswordChangeOnNextSignIn;
        }
        get(proto().passwordChangeRequired()).setVisible(this.isRequireChangePasswordOnNextSignInRequired);
        get(proto().passwordChangeRequired()).setEnabled(this.isRequireChangePasswordOnNextSignInRequired);

    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().currentPassword()).setVisible(isCurrentPasswordRequired);
        get(proto().passwordChangeRequired()).setVisible(isRequireChangePasswordOnNextSignInRequired);

    }

    public void setPasswordStrengthRule(PasswordStrengthRule passwordStrengthRule) {
        this.passwordStrengthRule = passwordStrengthRule;
        ((CPasswordBox) get(proto().newPassword())).setPasswordStrengthRule(passwordStrengthRule);
        this.passwordStrengthValidator.setPasswordStrengthRule(passwordStrengthRule);

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
        ((CPasswordBox) get(proto().newPassword())).setUnmasked(!maskPassword);
    }

    class PasswordChangeRequiredValidator extends AbstractComponentValidator<Boolean> {

        @Override
        public AbstractValidationError isValid() {
            if (get(proto().newPassword()).isEditingInProgress()) {
                return null;
            }
            enforceRequireChangePasswordThreshold = PasswordStrengthVerdict.Good;
            if (get(proto().newPassword()).getValue() != null && enforceRequireChangePasswordThreshold != null && passwordStrengthRule != null) {
                PasswordStrengthVerdict verdict = passwordStrengthRule.getPasswordVerdict(get(proto().newPassword()).getValue());
                if (getCComponent().getValue() != null && !getCComponent().getValue() && verdict != null
                        && verdict.compareTo(enforceRequireChangePasswordThreshold) <= 0) {
                    return new BasicValidationError(get(proto().passwordChangeRequired()),
                            i18n.tr("Password is to weak. You should require to change password on next sign in."));
                }
            }
            return null;
        }

    }

}