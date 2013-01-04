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
package com.propertyvista.crm.client.ui.wizard.onlinepayment;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.wizard.IWizardView;
import com.pyx4j.site.client.ui.wizard.WizardForm;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.crm.client.ui.components.LegalTermsContentViewer;
import com.propertyvista.crm.client.ui.wizard.common.BusinessInformationForm;
import com.propertyvista.crm.client.ui.wizard.common.PersonalInformationForm;
import com.propertyvista.domain.pmc.fee.AbstractPaymentFees;
import com.propertyvista.dto.OnlinePaymentSetupDTO;
import com.propertyvista.dto.OnlinePaymentSetupDTO.PropertyAccountInfo;

public class OnlinePaymentWizardForm extends WizardForm<OnlinePaymentSetupDTO> {

    private static final I18n i18n = I18n.get(OnlinePaymentWizardForm.class);

    private static final String CONFIRMATION_STEP_TITLE = i18n.tr("Confirmation");

    private static final String SIGNATURE_STEP_TITLE = i18n.tr("Signature");

    private OnlinePaymentPricingTab onlinePaymentPricingTab;

    private final Command onTermsOfServiceDisplayRequest;

    private Label companyNameLabel;

    private Label caledonTermsAgreementLabel;

    private Label paypadTermsAgreementLabel;

    public OnlinePaymentWizardForm(IWizardView<OnlinePaymentSetupDTO> view, Command onTermsOfServiceDisplayRequest) {
        super(OnlinePaymentSetupDTO.class, view);
        this.onTermsOfServiceDisplayRequest = onTermsOfServiceDisplayRequest;

        addStep(createPricingStep(i18n.tr("Pricing")));
        addStep(createBusinessInfoStep(i18n.tr("Business Information")));
        addStep(createPersonalInfoStep(i18n.tr("Personal Information")));
        addStep(createPropertyAndBankingStep(i18n.tr("Property and Banking")));
        addStep(createConfirmationStep(CONFIRMATION_STEP_TITLE));
        addStep(createSignatureStep(SIGNATURE_STEP_TITLE));

    }

    public void setPaymentFees(AbstractPaymentFees paymentFees) {
        onlinePaymentPricingTab.setPaymentFees(paymentFees);
    }

    @Override
    protected void onStepChange(SelectionEvent<Tab> event) {
        super.onStepChange(event);
        if (event.getSelectedItem().getTabTitle().equals(CONFIRMATION_STEP_TITLE)) {
            companyNameLabel.setText(get(proto().businessInformation()).getValue().companyName().getValue());
        } else if (event.getSelectedItem().getTabTitle().equals(SIGNATURE_STEP_TITLE)) {
            String companyName = get(proto().businessInformation()).getValue().companyName().getValue();
            caledonTermsAgreementLabel.setText(i18n.tr("I {0} agree to accept Visa Debit in a Card Not Present tranactions environment", companyName));
            paypadTermsAgreementLabel.setText(i18n.tr("I {0} agree to accept Payment Pad Terms and Conditions", companyName)); // TODO ask Leonard what to write here
        }
    }

    private FormFlexPanel createPricingStep(String title) {
        FormFlexPanel main = new FormFlexPanel(title);
        int row = 0;
        main.setH1(++row, 0, 1, i18n.tr("Pricing Information for Online Payments"));
        main.setWidget(++row, 0, onlinePaymentPricingTab = new OnlinePaymentPricingTab());
        return main;
    }

    private FormFlexPanel createBusinessInfoStep(String title) {
        // TODO should we have some text that explains why we collect this information as in Equifax wizard? if yes what is the text?
        FormFlexPanel main = new FormFlexPanel(title);
        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Business Information"));
        main.setWidget(++row, 0, inject(proto().businessInformation(), new BusinessInformationForm()));
        return main;
    }

    private FormFlexPanel createPersonalInfoStep(String title) {
        // TODO should we have some text that explains why we collect this information as in Equifax wizard? if yes what is the text?
        FormFlexPanel main = new FormFlexPanel(title);
        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Personal Information"));
        main.setWidget(++row, 0, inject(proto().personalInformation(), new PersonalInformationForm()));
        return main;
    }

    private FormFlexPanel createPropertyAndBankingStep(String title) {
        // TODO what is 'refundable deposit'?
        FormFlexPanel main = new FormFlexPanel(title);
        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Property and Banking"));
        main.setWidget(++row, 0, inject(proto().propertyAccounts(), new PropertyAccountInfoFolder()));
        get(proto().propertyAccounts()).addValueValidator(new EditableValueValidator<List<PropertyAccountInfo>>() {
            @Override
            public ValidationError isValid(CComponent<List<PropertyAccountInfo>, ?> component, List<PropertyAccountInfo> value) {
                if (value != null && value.size() < 1) {
                    return new ValidationError(component, i18n.tr("At least one property account is required"));
                } else {
                    return null;
                }
            }
        });
        return main;
    }

    private FormFlexPanel createConfirmationStep(String title) {
        // TODO why do we need this step if it just repeats the information from the previous step? why not just go from "Property and Banking" straight to "Signature"?
        FormFlexPanel main = new FormFlexPanel(title);
        int row = -1;
        main.setH1(++row, 0, 2, i18n.tr("Confirmation"));
        main.setWidget(++row, 0, makeServiceAgreementLabel());

        return main;
    }

    private FormFlexPanel createSignatureStep(String title) {
        // TODO need to add actual signature, but pending the following questions:
        //     - why the text on the spec PDF indicates that there may be more than one person?
        //     - the full text above the signature is required
        //     - the full text of the agreements is required
        //     - why payment pad doesn't have "I <company name> agree to accept <bla bla bla...>", but caledon has.
        //     - if payment pad indeed needs "I <company name> agree to accept <bla bla bla...>" checkbox, what should be in placed instead of <bla bla bla>
        // TODO since "I agree" checkboxes don't use the default decorator how can they be validated? 

        FormFlexPanel main = new FormFlexPanel(title);
        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Signature"));

        HorizontalPanel caledonPaymentMethodsLogoHeader = new HorizontalPanel();
        caledonPaymentMethodsLogoHeader.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        Label caledonCaption = new Label();
        caledonCaption.setText("Caledon Card Services");
        caledonPaymentMethodsLogoHeader.add(caledonCaption);
        caledonPaymentMethodsLogoHeader.add(new Image(OnlinePaymentWizardResources.INSTANCE.visaLogo()));
        caledonPaymentMethodsLogoHeader.add(new Image(OnlinePaymentWizardResources.INSTANCE.masterCardLogo()));
        caledonPaymentMethodsLogoHeader.add(new Image(OnlinePaymentWizardResources.INSTANCE.visaDebitLogo()));
        caledonPaymentMethodsLogoHeader.add(new Image(OnlinePaymentWizardResources.INSTANCE.echequeLogo()));
        main.setWidget(++row, 0, caledonPaymentMethodsLogoHeader);

        main.setWidget(++row, 0, inject(proto().caledonAgreement(), new LegalTermsContentViewer("20em")));

        HorizontalPanel caledonTermsAgreementPanel = new HorizontalPanel();
        caledonTermsAgreementPanel.add(inject(proto().caledonIAgree()));
        caledonTermsAgreementLabel = new Label();
        caledonTermsAgreementPanel.add(caledonTermsAgreementLabel);
        main.setWidget(++row, 0, caledonTermsAgreementPanel);

        main.setWidget(++row, 0, new HTML("&nbsp;")); // separator

        HorizontalPanel paypadPaymentMethodsLogoHader = new HorizontalPanel();
        paypadPaymentMethodsLogoHader.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        Label paypadCaption = new Label();
        paypadCaption.setText("Payment Pad Inc.");
        paypadPaymentMethodsLogoHader.add(paypadCaption);
        paypadPaymentMethodsLogoHader.add(new Image(OnlinePaymentWizardResources.INSTANCE.interacLogo()));
        paypadPaymentMethodsLogoHader.add(new Image(OnlinePaymentWizardResources.INSTANCE.directBankingLogo()));
        main.setWidget(++row, 0, paypadPaymentMethodsLogoHader);

        main.setWidget(++row, 0, inject(proto().paymentPadAgreement(), new LegalTermsContentViewer("20em")));

        HorizontalPanel paypadTermsAgreementPanel = new HorizontalPanel();
        paypadTermsAgreementPanel.add(inject(proto().paymentPadIAgree()));
        paypadTermsAgreementLabel = new Label();
        paypadTermsAgreementPanel.add(paypadTermsAgreementLabel);
        main.setWidget(++row, 0, paypadTermsAgreementPanel);

        // add Validators:
        get(proto().caledonIAgree()).addValueValidator(new EditableValueValidator<Boolean>() {
            @Override
            public ValidationError isValid(CComponent<Boolean, ?> component, Boolean value) {
                if (value != null && !value) {
                    return new ValidationError(component, i18n.tr("You must agree with Caledon terms to continue."));
                } else {
                    return null;
                }
            }
        });
        get(proto().paymentPadIAgree()).addValueValidator(new EditableValueValidator<Boolean>() {
            @Override
            public ValidationError isValid(CComponent<Boolean, ?> component, Boolean value) {
                if (value != null && !value) {
                    return new ValidationError(component, i18n.tr("You must agree with Payment Pad terms to continue."));
                } else {
                    return null;
                }
            }
        });
        return main;
    }

    private HTMLPanel makeServiceAgreementLabel() {
        HTMLPanel panel = new HTMLPanel(OnlinePaymentWizardResources.INSTANCE.serviceAgreement().getText());

        Element termsOfServiceAnchorElement = panel.getElementById(OnlinePaymentWizardResources.TERMS_OF_SERVICE_ANCHOR_ID);
        String termsOfServiceAnchorText = termsOfServiceAnchorElement.getChild(0).getNodeValue();
        Anchor termsOfServiceAnchor = new Anchor(termsOfServiceAnchorText);
        termsOfServiceAnchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (onTermsOfServiceDisplayRequest != null) {
                    onTermsOfServiceDisplayRequest.execute();
                }
            }
        });
        panel.addAndReplaceElement(termsOfServiceAnchor, OnlinePaymentWizardResources.TERMS_OF_SERVICE_ANCHOR_ID);

        companyNameLabel = new Label();
        companyNameLabel.getElement().getStyle().setDisplay(Display.INLINE);
        panel.addAndReplaceElement(companyNameLabel, OnlinePaymentWizardResources.COMPANY_NAME_ID);

        return panel;
    }

}
