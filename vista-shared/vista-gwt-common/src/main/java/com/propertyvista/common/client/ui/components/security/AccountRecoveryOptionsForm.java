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

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CPasswordTextField;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.CrudEntityForm;

import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.domain.security.SecurityQuestion;
import com.propertyvista.portal.rpc.shared.dto.AccountRecoveryOptionsDTO;

public class AccountRecoveryOptionsForm extends CrudEntityForm<AccountRecoveryOptionsDTO> {

    private static final I18n i18n = I18n.get(AccountRecoveryOptionsForm.class);

    public AccountRecoveryOptionsForm() {
        super(AccountRecoveryOptionsDTO.class, VistaTheme.defaultTabHeight);
    }

    public AccountRecoveryOptionsForm(boolean viewMode) {
        super(AccountRecoveryOptionsDTO.class, viewMode, VistaTheme.defaultTabHeight);
    }

    @Override
    protected void createTabs() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().recoveryEmail())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().mobilePhone())).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().securityQuestion())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().securityAnswer(), new CPasswordTextField())).build());

        selectTab(addTab(content, i18n.tr("General")));

    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        List<String> suggestedSecurityQuestions = new ArrayList<String>();
        for (SecurityQuestion q : getValue().securityQuestionsSuggestions()) {
            suggestedSecurityQuestions.add(q.question().getValue());
        }
    }

    // decoration stuff:
    protected class DecoratorBuilder extends WidgetDecorator.Builder {

        public DecoratorBuilder(CComponent<?, ?> component) {
            super(component);
            readOnlyMode(!isEditable());
        }

        public DecoratorBuilder(CComponent<?, ?> component, double componentWidth) {
            super(component);
            readOnlyMode(!isEditable());
            componentWidth(componentWidth);
        }

        public DecoratorBuilder(CComponent<?, ?> component, double componentWidth, double labelWidth) {
            super(component);
            readOnlyMode(!isEditable());
            componentWidth(componentWidth);
            labelWidth(labelWidth);
        }

    }

}
