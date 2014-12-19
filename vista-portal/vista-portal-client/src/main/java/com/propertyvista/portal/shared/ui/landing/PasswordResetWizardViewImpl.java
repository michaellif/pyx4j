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
 */
package com.propertyvista.portal.shared.ui.landing;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.PasswordResetQuestion;
import com.pyx4j.widgets.client.PasswordBox.PasswordStrengthRule;

import com.propertyvista.common.client.ui.components.login.PasswordResetForm;
import com.propertyvista.common.client.ui.components.security.PasswordResetView;

public class PasswordResetWizardViewImpl extends VerticalPanel implements PasswordResetView {

    private final static I18n i18n = I18n.get(PasswordResetWizardViewImpl.class);

    private Presenter presenter;

    private final PasswordResetForm form;

    public PasswordResetWizardViewImpl() {
        setWidth("100%");
        setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        form = new PasswordResetForm(this);
        form.init();
        add(form);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        form.populateNew();
    }

    @Override
    public void setQuestion(PasswordResetQuestion question) {
        if (!question.securityQuestion().isNull()) {
            form.get(form.proto().securityQuestion()).setValue(question.securityQuestion().getValue());
            form.get(form.proto().securityQuestion()).setVisible(true);
            form.get(form.proto().securityAnswer()).setVisible(true);
        } else {
            form.get(form.proto().securityQuestion()).setValue("");
            form.get(form.proto().securityQuestion()).setVisible(false);
            form.get(form.proto().securityAnswer()).setVisible(false);
        }
    }

    @Override
    public void reset() {
        form.reset();
    }

    @Override
    public void setPasswordStrengthRule(PasswordStrengthRule passwordStrengthRule) {
        form.setPasswordStrengthRule(passwordStrengthRule);
    }

    @Override
    public Presenter getPresenter() {
        return this.presenter;
    }

}
