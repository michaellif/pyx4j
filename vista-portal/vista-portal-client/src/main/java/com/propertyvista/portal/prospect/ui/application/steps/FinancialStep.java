/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 11, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardViewImpl;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.shared.ui.util.editors.PersonalAssetFolder;
import com.propertyvista.portal.shared.ui.util.editors.PersonalIncomeFolder;

public class FinancialStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(FinancialStep.class);

    public FinancialStep() {
        super(OnlineApplicationWizardStepMeta.Financial);
    }

    @Override
    public BasicFlexFormPanel createStepContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(getStepTitle());
        int row = -1;

        panel.setH3(++row, 0, 1, i18n.tr("Income"));
        panel.setWidget(++row, 0, inject(proto().applicant().incomes(), new PersonalIncomeFolder()));

        panel.setH3(++row, 0, 1, i18n.tr("Assets"));
        panel.setWidget(++row, 0, inject(proto().applicant().assets(), new PersonalAssetFolder()));

        panel.setH3(++row, 0, 1, i18n.tr("Guarantors"));
        panel.setWidget(++row, 0, inject(proto().guarantors(), new GuarantorsFolder((ApplicationWizardViewImpl) getView())));

        return panel;
    }

    @Override
    public void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (getWizard().isEditable()) {
            ((PersonalIncomeFolder) (CComponent<?>) get(proto().applicant().incomes())).setDocumentsPolicy(getValue().applicant().documentsPolicy());
        }
    }

    @Override
    public void addValidations() {
        super.addValidations();

        getWizard().addValueValidator(new EditableValueValidator<OnlineApplicationDTO>() {
            @Override
            public FieldValidationError isValid(CComponent<OnlineApplicationDTO> component, OnlineApplicationDTO value) {
                if (value != null) {
                    return (value.applicant().assets().size() > 0) || (value.applicant().incomes().size() > 0) ? null : new FieldValidationError(component, i18n
                            .tr("At least one source of income or one asset is required"));
                }
                return null;
            }
        });
    }
}
