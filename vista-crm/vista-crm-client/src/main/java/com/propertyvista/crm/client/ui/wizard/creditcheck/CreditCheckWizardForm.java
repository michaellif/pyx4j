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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.wizard.IWizard;
import com.pyx4j.site.client.ui.prime.wizard.WizardForm;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.components.editors.dto.wizards.BusinessInformationForm;
import com.propertyvista.common.client.ui.components.editors.dto.wizards.PersonalInformationForm;
import com.propertyvista.common.client.ui.components.editors.payments.CreditCardInfoEditor;
import com.propertyvista.crm.client.ui.components.WidgetDecoratorRightLabel;
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

    private TwoColumnFlexFormPanel personalInfoStepPanel;

    private Label costPerApplicantLabel;

    private Label setupFeeLabel;

    private AbstractEquifaxFee creditCheckFees;

    private final Command onDisplayTermsOfServiceRequest;

    public CreditCheckWizardForm(IWizard<CreditCheckSetupDTO> view, Command onDisplayTermsOfServiceRequest) {
        super(CreditCheckSetupDTO.class, view);
        this.onDisplayTermsOfServiceRequest = onDisplayTermsOfServiceRequest;
        addStep(createPricingStep(PRICING_STEP_NAME));
        addStep(createBusinessInfoStep(BUSINESS_INFO_STEP_NAME));
        addStep(createPersonalInfoStep(PERSONAL_INFO_STEP_NAME));
        addStep(createConfirmationStep(CONFIRMATION_STEP_NAME));
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

    private TwoColumnFlexFormPanel createPricingStep(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);
        int row = -1;
        main.setH1(++row, 0, 2, i18n.tr("Pricing"));
        main.setWidget(++row, 0,
                new WidgetDecoratorRightLabel(inject(proto().creditPricingOption(), new CCreditCheckReportTypeSelector(CreditCheckWizardResources.INSTANCE)),
                        50, 0));
        main.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        return main;
    }

    private TwoColumnFlexFormPanel createBusinessInfoStep(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);
        int row = 0;
        main.setH1(row++, 0, 2, i18n.tr("Business & Corporate Information"));

        Label collectionOfbusinessInformationExplanation = new Label();
        collectionOfbusinessInformationExplanation.setHTML(CreditCheckWizardResources.INSTANCE.collectionOfBusinessInformationExplanation().getText());
        main.setWidget(row++, 0, collectionOfbusinessInformationExplanation);

        main.setWidget(row++, 0, inject(proto().businessInformation(), new BusinessInformationForm()));

        return main;
    }

    private TwoColumnFlexFormPanel createPersonalInfoStep(String title) {
        personalInfoStepPanel = new TwoColumnFlexFormPanel(title);
        int row = 0;
        personalInfoStepPanel.setH1(++row, 0, 2, i18n.tr("Personal Information"));

        Label collectionOfPersonalInformationForEquifaxExplanation = new Label();
        collectionOfPersonalInformationForEquifaxExplanation.setHTML(CreditCheckWizardResources.INSTANCE.collectionOfPersonalInformationForEquifaxExplanation()
                .getText());
        personalInfoStepPanel.setWidget(++row, 0, collectionOfPersonalInformationForEquifaxExplanation);

        personalInfoStepPanel.setWidget(++row, 0, inject(proto().personalInformation(), new PersonalInformationForm()));

        Widget termsOfService = makePersonalInfoServiceAgreementText();
        personalInfoStepPanel.setWidget(++row, 0, termsOfService);

        return personalInfoStepPanel;
    }

    private TwoColumnFlexFormPanel createConfirmationStep(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);
        int row = 0;
        main.setH1(row++, 0, 2, i18n.tr("Confirmation and Payment Information"));

        Label confirmationAndPaymentText = new Label();
        confirmationAndPaymentText.setHTML(CreditCheckWizardResources.INSTANCE.confirmationAndPaymentText().getText());
        main.setWidget(++row, 0, confirmationAndPaymentText);

        costPerApplicantLabel = new Label();
        main.setWidget(++row, 0, costPerApplicantLabel);
        main.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        setupFeeLabel = new Label();
        main.setWidget(++row, 0, setupFeeLabel);
        main.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        main.setWidget(++row, 0, inject(proto().creditCardInfo(), new CreditCardInfoEditor()));

        main.setWidget(++row, 0, makeConfirmationServiceAgreementText());
        main.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        return main;
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
