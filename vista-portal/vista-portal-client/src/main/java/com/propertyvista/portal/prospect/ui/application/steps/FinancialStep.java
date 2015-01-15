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
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.IList;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.tenant.income.CustomerScreeningAsset;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.prospect.ui.application.components.PersonalAssetFolder;
import com.propertyvista.portal.prospect.ui.application.components.PersonalIncomeFolder;

public class FinancialStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(FinancialStep.class);

    private final PersonalIncomeFolder personalIncomeFolder = new PersonalIncomeFolder();

    private final PersonalAssetFolder personalAssetFolder = new PersonalAssetFolder();

    private Widget guarantorsHeader;

    public FinancialStep() {
        super(OnlineApplicationWizardStepMeta.Financial);
    }

    @Override
    public IsWidget createStepContent() {
        FormPanel formPanel = new FormPanel(getWizard());

        formPanel.h3(i18n.tr("Income"));
        formPanel.append(Location.Left, proto().applicantData().incomes(), personalIncomeFolder);

        formPanel.h3(i18n.tr("Assets"));
        formPanel.append(Location.Left, proto().applicantData().assets(), personalAssetFolder);

        if (!SecurityController.check(PortalProspectBehavior.Guarantor)) {
            guarantorsHeader = formPanel.h3(i18n.tr("Guarantors"));
            formPanel.append(Location.Left, proto().guarantors(), new GuarantorsFolder());
        }

        return formPanel;
    }

    @Override
    public void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (!SecurityController.check(PortalProspectBehavior.Guarantor)) {
            guarantorsHeader.setVisible(!getValue().noNeedGuarantors().getValue(false));
            get(proto().guarantors()).setVisible(!getValue().noNeedGuarantors().getValue(false));
        }

        if (getWizard().isEditable()) {
            personalIncomeFolder.setRestrictionsPolicy(getValue().applicantData().restrictionsPolicy());
            personalIncomeFolder.setDocumentationPolicy(getValue().applicantData().documentsPolicy());
            personalAssetFolder.setDocumentationPolicy(getValue().applicantData().documentsPolicy());
        }
    }

    @Override
    public void addValidations() {
        super.addValidations();

        personalIncomeFolder.addComponentValidator(new AbstractComponentValidator<IList<CustomerScreeningIncome>>() {
            @Override
            public BasicValidationError isValid() {
                return (getValue().applicantData().assets().size() > 0) || (getValue().applicantData().incomes().size() > 0) ? null : new BasicValidationError(
                        getCComponent(), i18n.tr("Incomes and/or Assets are required"));
            }
        });
        personalAssetFolder.addValueChangeHandler(new RevalidationTrigger<IList<CustomerScreeningAsset>>(personalIncomeFolder));
        // --------------------------------------------------------------------------------------------------------------------
        personalAssetFolder.addComponentValidator(new AbstractComponentValidator<IList<CustomerScreeningAsset>>() {
            @Override
            public BasicValidationError isValid() {
                return (getValue().applicantData().assets().size() > 0) || (getValue().applicantData().incomes().size() > 0) ? null : new BasicValidationError(
                        getCComponent(), i18n.tr("Assets and/or Incomes are required"));
            }
        });
        personalIncomeFolder.addValueChangeHandler(new RevalidationTrigger<IList<CustomerScreeningIncome>>(personalAssetFolder));
    }
}
