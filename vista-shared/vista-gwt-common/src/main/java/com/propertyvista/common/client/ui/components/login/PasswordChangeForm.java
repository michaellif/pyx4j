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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.events.NValueChangeEvent;
import com.pyx4j.forms.client.events.NValueChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.forms.client.validators.password.DefaultPasswordStrengthRule;
import com.pyx4j.forms.client.validators.password.PasswordStrengthValueValidator;
import com.pyx4j.forms.client.validators.password.PasswordStrengthWidget;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.PasswordChangeRequest;

import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;

public class PasswordChangeForm extends CEntityDecoratableForm<PasswordChangeRequest> {

    private final static I18n i18n = I18n.get(PasswordChangeForm.class);

    private PasswordStrengthWidget passwordStrengthWidget;

    private final DefaultPasswordStrengthRule passwordStrengthRule;

    private boolean isCurrentPasswordRequired;

    private boolean isRequireChangePasswordOnNextSignInRequired;

    private List<String> dictionary;

    public PasswordChangeForm(List<String> dictionary, boolean isCurrentPasswordRequired, boolean isRequireChangePasswordOnNextSignInRequired) {
        super(PasswordChangeRequest.class);
        this.passwordStrengthRule = new DefaultPasswordStrengthRule();
        this.isCurrentPasswordRequired = isCurrentPasswordRequired;
        this.isRequireChangePasswordOnNextSignInRequired = isRequireChangePasswordOnNextSignInRequired;
        setDictionary(dictionary);
    }

    public PasswordChangeForm() {
        this(null, true, true);
    }

    @Override
    public IsWidget createContent() {

        FormFlexPanel main = new FormFlexPanel();
        main.setWidth("40em");
        main.setStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name(), true);

        int row = -1;

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().currentPassword())).componentWidth(15).labelWidth(15).build());
        main.getFlexCellFormatter().getElement(row, 0).getStyle().setPaddingBottom(1., Unit.EM);

        passwordStrengthWidget = new PasswordStrengthWidget(passwordStrengthRule);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().newPassword())).componentWidth(15).labelWidth(15).assistantWidget(passwordStrengthWidget)
                .build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().newPasswordConfirm())).componentWidth(15).labelWidth(15).build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().requireChangePasswordOnNextSignIn())).componentWidth(15).labelWidth(15).build());

        return main;
    }

    @Override
    public void addValidations() {
        get(proto().newPasswordConfirm()).addValueValidator(new EditableValueValidator<String>() {
            @Override
            public ValidationError isValid(CComponent<String, ?> component, String value) {
                if (value == null || !value.equals(get(proto().newPassword()).getValue())) {
                    return new ValidationError(component, i18n.tr("The passwords don't match."));
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

        get(proto().newPassword()).addValueValidator(new PasswordStrengthValueValidator(passwordStrengthRule));
    }

    public void setAskForCurrentPassword(boolean isCurrentPasswordRequired) {
        this.isCurrentPasswordRequired = isCurrentPasswordRequired;
        get(proto().currentPassword()).setVisible(this.isCurrentPasswordRequired);
    }

    public void setAskForRequireChangePasswordOnNextSignIn(boolean isRequireChangePasswordOnNextSignInRequired) {
        this.isRequireChangePasswordOnNextSignInRequired = isRequireChangePasswordOnNextSignInRequired;
        get(proto().requireChangePasswordOnNextSignIn()).setVisible(this.isRequireChangePasswordOnNextSignInRequired);
    }

    public void setDictionary(List<String> dictionary) {
        if (dictionary != null) {
            this.dictionary = new ArrayList<String>(dictionary);
        } else {
            this.dictionary = Collections.emptyList();
        }
        passwordStrengthRule.setDictionary(this.dictionary);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().currentPassword()).setVisible(isCurrentPasswordRequired);
        get(proto().requireChangePasswordOnNextSignIn()).setVisible(isRequireChangePasswordOnNextSignInRequired);

        passwordStrengthRule.setDictionary(dictionary);
    }

    @Override
    protected void onWidgetCreated() {
        super.onWidgetCreated();
        asWidget().setStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name(), true);
        asWidget().getElement().getStyle().setMarginTop(50, Unit.PX);
        asWidget().getElement().getStyle().setMarginBottom(50, Unit.PX);
    }

}