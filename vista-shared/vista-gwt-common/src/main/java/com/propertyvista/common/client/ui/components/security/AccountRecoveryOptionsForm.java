/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 20, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.security;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.security.SecurityQuestion;
import com.propertyvista.portal.rpc.shared.dto.AccountRecoveryOptionsDTO;

public class AccountRecoveryOptionsForm extends CEntityDecoratableForm<AccountRecoveryOptionsDTO> {

    private static final I18n i18n = I18n.get(AccountRecoveryOptionsForm.class);

    public AccountRecoveryOptionsForm(boolean isEditable) {
        super(AccountRecoveryOptionsDTO.class);
        setEditable(isEditable);
    }

    @Override
    public IsWidget createContent() {

        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));
        int row = -1;

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().recoveryEmail())).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().useSecurityQuestionChallengeForPasswordReset())).build());
        get(proto().useSecurityQuestionChallengeForPasswordReset()).asWidget().getElement().getStyle().setPaddingTop(1, Unit.EM);
        get(proto().useSecurityQuestionChallengeForPasswordReset()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                setUpSecurityQuestionControls();
            }
        });

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().securityQuestion())).build());
        get(proto().securityQuestion()).setVisible(false);
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().securityAnswer(), new CPasswordTextField())).build());
        get(proto().securityAnswer()).setVisible(false);

        return content;
    }

    public void setSecurityQuestionRequired(boolean isSecurityQuestionEntiryRequired) {
        get(proto().useSecurityQuestionChallengeForPasswordReset()).setViewable(isSecurityQuestionEntiryRequired);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        setUpSecurityQuestionControls();
    }

    private void setUpSecurityQuestionControls() {
        if (getValue().useSecurityQuestionChallengeForPasswordReset().isBooleanTrue()) {
            get(proto().securityQuestion()).setVisible(true);
            List<String> suggestedSecurityQuestions = new ArrayList<String>();
            for (SecurityQuestion q : getValue().securityQuestionsSuggestions()) {
                suggestedSecurityQuestions.add(q.question().getValue());
            }
            // TODO in this very place set security question options component, with the suggested security questions (pending a component that can support this)
            get(proto().securityAnswer()).setVisible(true);

        } else {
            get(proto().securityQuestion()).setVisible(false);
            get(proto().securityQuestion()).setValue("");
            get(proto().securityAnswer()).setVisible(false);
            get(proto().securityAnswer()).setValue("");
        }
    }

    // decoration stuff:
    protected class DecoratorBuilder extends WidgetDecorator.Builder {

        public DecoratorBuilder(CComponent<?, ?> component) {
            super(component);
        }

        public DecoratorBuilder(CComponent<?, ?> component, double componentWidth) {
            super(component);
            componentWidth(componentWidth);
        }

        public DecoratorBuilder(CComponent<?, ?> component, double componentWidth, double labelWidth) {
            super(component);
            componentWidth(componentWidth);
            labelWidth(labelWidth);
        }

    }

}
