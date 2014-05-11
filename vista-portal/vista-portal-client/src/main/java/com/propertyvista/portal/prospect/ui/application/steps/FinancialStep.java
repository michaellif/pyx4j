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

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.income.CustomerScreeningPersonalAsset;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardViewImpl;
import com.propertyvista.portal.prospect.ui.application.editors.PersonalAssetFolder;
import com.propertyvista.portal.prospect.ui.application.editors.PersonalIncomeFolder;
import com.propertyvista.portal.shared.ui.PortalFormPanel;

public class FinancialStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(FinancialStep.class);

    private Widget guarantorsHeader;

    public FinancialStep() {
        super(OnlineApplicationWizardStepMeta.Financial);
    }

    @Override
    public IsWidget createStepContent() {
        PortalFormPanel formPanel = new PortalFormPanel(getWizard());

        formPanel.h3(i18n.tr("Income"));
        formPanel.append(Location.Left, proto().applicant().incomes(), new PersonalIncomeFolder());

        formPanel.h3(i18n.tr("Assets"));
        formPanel.append(Location.Left, proto().applicant().assets(), new PersonalAssetFolder());

        if (!SecurityController.checkBehavior(PortalProspectBehavior.Guarantor)) {
            guarantorsHeader = formPanel.h3(i18n.tr("Guarantors"));
            formPanel.append(Location.Left, proto().guarantors(), new GuarantorsFolder((ApplicationWizardViewImpl) getView()));
        }

        return formPanel;
    }

    @Override
    public void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (!SecurityController.checkBehavior(PortalProspectBehavior.Guarantor)) {
            guarantorsHeader.setVisible(!getValue().noNeedGuarantors().getValue(false));
            get(proto().guarantors()).setVisible(!getValue().noNeedGuarantors().getValue(false));
        }

        if (getWizard().isEditable()) {
            ((PersonalIncomeFolder) (CComponent<?, ?, ?>) get(proto().applicant().incomes())).setDocumentsPolicy(getValue().applicant().documentsPolicy());
        }
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().applicant().incomes()).addComponentValidator(new AbstractComponentValidator<List<CustomerScreeningIncome>>() {
            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() != null) {
                    return (getValue().applicant().assets().size() > 0) || (getValue().applicant().incomes().size() > 0) ? null : new FieldValidationError(
                            getComponent(), i18n.tr("At least one source of income or one asset is required"));
                }
                return null;
            }
        });
        get(proto().applicant().assets()).addValueChangeHandler(
                new RevalidationTrigger<List<CustomerScreeningPersonalAsset>>(get(proto().applicant().incomes())));
    }
}
