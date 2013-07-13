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
package com.propertyvista.portal.web.client.ui.landing;

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
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.forms.client.validators.password.DefaultPasswordStrengthRule;
import com.pyx4j.forms.client.validators.password.PasswordStrengthValueValidator;
import com.pyx4j.forms.client.validators.password.PasswordStrengthWidget;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.PasswordChangeRequest;

import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;

public class PasswordResetForm extends CEntityDecoratableForm<PasswordChangeRequest> {

    private final static I18n i18n = I18n.get(PasswordResetForm.class);

    private PasswordStrengthWidget passwordStrengthWidget;

    private final DefaultPasswordStrengthRule passwordStrengthRule;

    private List<String> dictionary;

    public PasswordResetForm(List<String> dictionary) {
        super(PasswordChangeRequest.class);
        this.passwordStrengthRule = new DefaultPasswordStrengthRule();
        setDictionary(dictionary);
        asWidget().setStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name(), true);
        asWidget().getElement().getStyle().setMarginTop(50, Unit.PX);
        asWidget().getElement().getStyle().setMarginBottom(50, Unit.PX);

    }

    public PasswordResetForm() {
        this(null);
    }

    @Override
    public IsWidget createContent() {

        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
        main.setWidth("40em");
        main.setStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name(), true);

        int row = -1;

        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().securityQuestion())).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().securityAnswer())).build());

        passwordStrengthWidget = new PasswordStrengthWidget(passwordStrengthRule);
        main.setWidget(++row, 0,
                new FormDecoratorBuilder(inject(proto().newPassword())).componentWidth(15).labelWidth(15).assistantWidget(passwordStrengthWidget).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().newPasswordConfirm())).componentWidth(15).labelWidth(15).build());

        get(proto().securityQuestion()).setVisible(false);
        get(proto().securityAnswer()).setVisible(false);

        return main;
    }

    @Override
    public void onReset() {
        super.onReset();
        get(proto().securityQuestion()).setVisible(false);
        get(proto().securityAnswer()).setVisible(false);
    }

    @Override
    public void addValidations() {
        get(proto().newPasswordConfirm()).addValueValidator(new EditableValueValidator<String>() {
            @Override
            public ValidationError isValid(CComponent<String> component, String value) {
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

        passwordStrengthRule.setDictionary(dictionary);
    }

}