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
package com.propertyvista.crm.client.ui.crud.account;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;

import com.propertyvista.domain.security.SecurityQuestion;
import com.propertyvista.portal.rpc.shared.dto.AccountRecoveryOptionsDTO;

public class AccountRecoveryOptionsForm extends CForm<AccountRecoveryOptionsDTO> {

    public AccountRecoveryOptionsForm(boolean isEditable) {
        super(AccountRecoveryOptionsDTO.class);
        setEditable(isEditable);
    }

    @Override
    protected IsWidget createContent() {

        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Left, proto().recoveryEmail()).decorate();

        formPanel.append(Location.Left, proto().useSecurityQuestionChallengeForPasswordReset()).decorate();
        get(proto().useSecurityQuestionChallengeForPasswordReset()).asWidget().getElement().getStyle().setPaddingTop(1, Unit.EM);
        get(proto().useSecurityQuestionChallengeForPasswordReset()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                setUpSecurityQuestionControls();
            }
        });

        formPanel.append(Location.Left, proto().securityQuestion()).decorate();
        get(proto().securityQuestion()).setVisible(false);
        formPanel.append(Location.Left, proto().securityAnswer(), new CPasswordTextField()).decorate();
        get(proto().securityAnswer()).setVisible(false);

        return formPanel;
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
        if (getValue().useSecurityQuestionChallengeForPasswordReset().getValue(false)) {
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

}
