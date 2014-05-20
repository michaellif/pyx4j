/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.wizard.creditcheck;

import java.math.BigDecimal;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.wizard.IWizard;
import com.pyx4j.site.client.ui.prime.wizard.WizardForm;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.components.editors.dto.wizards.BusinessInformationForm;
import com.propertyvista.common.client.ui.components.editors.dto.wizards.PersonalInformationForm;
import com.propertyvista.common.client.ui.components.editors.payments.CreditCardInfoEditor;
import com.propertyvista.crm.client.ui.wizard.creditcheck.components.CCreditCheckReportTypeSelector;
import com.propertyvista.domain.pmc.CreditCheckReportType;
import com.propertyvista.domain.pmc.fee.AbstractEquifaxFee;
import com.propertyvista.dto.vista2pmc.CreditCheckSetupDTO;

public class CreditCheckWizardForm extends WizardForm<CreditCheckSetupDTO> {

    private static final I18n i18n = I18n.get(CreditCheckWizardForm.class);

    private static final String PRICING_STEP_NAME = i18n.tr("Pricing");

    private static final String BUSINESS_INFO_STEP_NAME = i18n.tr("Business Information");

    private static final String PERSONAL_INFO_STEP_NAME = i18n.tr("Personal Information");

    private static final String CONFIRMATION_STEP_NAME = i18n.tr("Confirmation");

    private Label companyNameLabel;

    private FormPanel personalInfoStepPanel;

    private Label costPerApplicantLabel;

    private Label setupFeeLabel;

    private AbstractEquifaxFee creditCheckFees;

    private final Command onDisplayTermsOfServiceRequest;

    public CreditCheckWizardForm(IWizard<CreditCheckSetupDTO> view, Command onDisplayTermsOfServiceRequest) {
        super(CreditCheckSetupDTO.class, view);
        this.onDisplayTermsOfServiceRequest = onDisplayTermsOfServiceRequest;
        addStep(createPricingStep().asWidget(), PRICING_STEP_NAME);
        addStep(createBusinessInfoStep().asWidget(), BUSINESS_INFO_STEP_NAME);
        addStep(createPersonalInfoStep().asWidget(), PERSONAL_INFO_STEP_NAME);
        addStep(createConfirmationStep().asWidget(), CONFIRMATION_STEP_NAME);
    }

    public void setPricingOptions(AbstractEquifaxFee creditCheckFees) {
        this.creditCheckFees = creditCheckFees;
        ((CCreditCheckReportTypeSelector) (get(proto().creditPricingOption()))).setFees(CreditCheckReportType.RecomendationReport, creditCheckFees
                .recommendationReportSetUpFee().getValue(), creditCheckFees.recommendationReportPerApplicantFee().getValue());
        ((CCreditCheckReportTypeSelector) (get(proto().creditPricingOption()))).setFees(CreditCheckReportType.FullCreditReport, creditCheckFees
                .fullCreditReportSetUpFee().getValue(), creditCheckFees.fullCreditReportPerApplicantFee().getValue());
    }

    @Override
    protected void onStepChange(SelectionEvent<Tab> event) {
        super.onStepChange(event);
        if (event.getSelectedItem().getTabTitle().equals(PERSONAL_INFO_STEP_NAME)) {
            companyNameLabel.setText(get(proto().businessInformation()).getValue().companyName().getValue());

        } else if (event.getSelectedItem().getTabTitle().equals(CONFIRMATION_STEP_NAME)) {
            BigDecimal costPerApplicant = null;
            BigDecimal setupFee = null;

            switch (get(proto().creditPricingOption()).getValue()) {
            case RecomendationReport:
                costPerApplicant = creditCheckFees.recommendationReportPerApplicantFee().getValue();
                setupFee = creditCheckFees.recommendationReportSetUpFee().getValue();
                break;
            case FullCreditReport:
                costPerApplicant = creditCheckFees.fullCreditReportPerApplicantFee().getValue();
                setupFee = creditCheckFees.fullCreditReportSetUpFee().getValue();
                break;
            }

            costPerApplicantLabel.setText(i18n.tr("Cost per applicant is: ${0,number,#,##0.00}", costPerApplicant));
            if (setupFee.compareTo(BigDecimal.ZERO) == 0) {
                setupFeeLabel.setText(i18n.tr("No Set Up Fee!"));
            } else {
                setupFeeLabel.setText(i18n.tr("Setup Fee of ${0,number,#,##0.00} will be charged", setupFee));
            }
        }
    }

    private FormPanel createPricingStep() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.h1(i18n.tr("Pricing"));
        formPanel.h2(proto().creditPricingOption().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().creditPricingOption(), new CCreditCheckReportTypeSelector(CreditCheckWizardResources.INSTANCE));
        return formPanel;
    }

    private FormPanel createBusinessInfoStep() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.h1(i18n.tr("Business & Corporate Information"));

        Label collectionOfbusinessInformationExplanation = new Label();
        collectionOfbusinessInformationExplanation.setHTML(CreditCheckWizardResources.INSTANCE.collectionOfBusinessInformationExplanation().getText());
        formPanel.append(Location.Dual, collectionOfbusinessInformationExplanation);
        formPanel.append(Location.Dual, proto().businessInformation(), new BusinessInformationForm());

        return formPanel;
    }

    private FormPanel createPersonalInfoStep() {
        personalInfoStepPanel = new FormPanel(this);
        personalInfoStepPanel.h1(i18n.tr("Personal Information"));

        Label collectionOfPersonalInformationForEquifaxExplanation = new Label();
        collectionOfPersonalInformationForEquifaxExplanation.setHTML(CreditCheckWizardResources.INSTANCE.collectionOfPersonalInformationForEquifaxExplanation()
                .getText());
        personalInfoStepPanel.append(Location.Dual, collectionOfPersonalInformationForEquifaxExplanation);
        personalInfoStepPanel.append(Location.Dual, proto().personalInformation(), new PersonalInformationForm());
        Widget termsOfService = makePersonalInfoServiceAgreementText();
        personalInfoStepPanel.append(Location.Dual, termsOfService);

        return personalInfoStepPanel;
    }

    private FormPanel createConfirmationStep() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.h1(i18n.tr("Confirmation and Payment Information"));

        Label confirmationAndPaymentText = new Label();
        confirmationAndPaymentText.setHTML(CreditCheckWizardResources.INSTANCE.confirmationAndPaymentText().getText());
        formPanel.append(Location.Dual, confirmationAndPaymentText);

        costPerApplicantLabel = new Label();
        formPanel.append(Location.Dual, costPerApplicantLabel);

        setupFeeLabel = new Label();
        formPanel.append(Location.Dual, setupFeeLabel);

        formPanel.append(Location.Dual, proto().creditCardInfo(), new CreditCardInfoEditor());
        formPanel.append(Location.Dual, makeConfirmationServiceAgreementText());
        return formPanel;
    }

    private HTMLPanel makePersonalInfoServiceAgreementText() {
        HTMLPanel serviceAgreement = new HTMLPanel(CreditCheckWizardResources.INSTANCE.collectionOfPersonalInformationServiceAgreement().getText());

        // this is needed to fetch translated text 
        Element termsOfServiceAnchorElement = serviceAgreement.getElementById(CreditCheckWizardResources.TERMS_OF_SERVICE_ANCHOR_ID);
        String termsOfServiceText = termsOfServiceAnchorElement.getChild(0).getNodeValue();
        Anchor termsOfServiceAnchor = new Anchor(termsOfServiceText, new Command() {
            @Override
            public void execute() {
                onDisplayTermsOfService();
            }
        });
        serviceAgreement.addAndReplaceElement(termsOfServiceAnchor, CreditCheckWizardResources.TERMS_OF_SERVICE_ANCHOR_ID);

        companyNameLabel = new Label();
        companyNameLabel.getElement().getStyle().setDisplay(Display.INLINE);
        companyNameLabel.setText(i18n.tr(""));
        serviceAgreement.addAndReplaceElement(companyNameLabel, CreditCheckWizardResources.COMPANY_NAME_ID);
        return serviceAgreement;
    }

    private HTMLPanel makeConfirmationServiceAgreementText() {
        HTMLPanel serviceAgreement = new HTMLPanel(CreditCheckWizardResources.INSTANCE.confirmationAnPaymentServiceAgreement().getText());
        Element termsOfServiceAnchorElement = serviceAgreement.getElementById(CreditCheckWizardResources.TERMS_OF_SERVICE_ANCHOR_ID);
        String termsOfServiceText = termsOfServiceAnchorElement.getChild(0).getNodeValue();
        Anchor termsOfServiceAnchor = new Anchor(termsOfServiceText, new Command() {
            @Override
            public void execute() {
                onDisplayTermsOfService();
            }
        });
        serviceAgreement.addAndReplaceElement(termsOfServiceAnchor, CreditCheckWizardResources.TERMS_OF_SERVICE_ANCHOR_ID);
        return serviceAgreement;
    }

    private void onDisplayTermsOfService() {
        if (onDisplayTermsOfServiceRequest != null) {
            onDisplayTermsOfServiceRequest.execute();
        }
    }

}
