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
 */
package com.propertyvista.crm.client.ui.wizard.onlinepayment;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.wizard.IPrimeWizardView;
import com.pyx4j.site.client.backoffice.ui.prime.wizard.WizardForm;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.components.editors.dto.wizards.BusinessInformationForm;
import com.propertyvista.common.client.ui.components.editors.dto.wizards.PersonalInformationForm;
import com.propertyvista.crm.client.ui.components.AgreementForm;
import com.propertyvista.domain.pmc.fee.AbstractPaymentFees;
import com.propertyvista.domain.pmc.info.BusinessInformation.CompanyType;
import com.propertyvista.dto.vista2pmc.OnlinePaymentSetupDTO;
import com.propertyvista.dto.vista2pmc.OnlinePaymentSetupDTO.PropertyAccountInfo;

public class OnlinePaymentWizardForm extends WizardForm<OnlinePaymentSetupDTO> {

    private static final I18n i18n = I18n.get(OnlinePaymentWizardForm.class);

    private static final String CONFIRMATION_STEP_TITLE = i18n.tr("Confirmation");

    private static final String SIGNATURE_STEP_TITLE = i18n.tr("Signature");

    private OnlinePaymentPricingTab onlinePaymentPricingTab;

    private final Command onTermsOfServiceDisplayRequest;

    private Label companyNameLabel;

    public OnlinePaymentWizardForm(IPrimeWizardView<OnlinePaymentSetupDTO> view, Command onTermsOfServiceDisplayRequest) {
        super(OnlinePaymentSetupDTO.class, view);
        this.onTermsOfServiceDisplayRequest = onTermsOfServiceDisplayRequest;

        addStep(createPricingStep().asWidget(), i18n.tr("Pricing"));
        addStep(createBusinessInfoStep().asWidget(), i18n.tr("Business Information"));
        addStep(createPersonalInfoStep().asWidget(), i18n.tr("Personal Information"));
        addStep(createPropertyAndBankingStep().asWidget(), i18n.tr("Property and Banking"));
        addStep(createConfirmationStep().asWidget(), CONFIRMATION_STEP_TITLE);
        addStep(createSignatureStep().asWidget(), SIGNATURE_STEP_TITLE);

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
            CompanyType companyType = get(proto().businessInformation()).getValue().companyType().getValue();
            get(proto().caledonSoleProprietorshipAgreement()).setVisible(companyType == CompanyType.SoleProprietorship);

            String companyName = get(proto().businessInformation()).getValue().companyName().getValue();
            String companyOwnersFullName = (get(proto().personalInformation())).getValue().name().firstName().getValue() + " "
                    + (get(proto().personalInformation())).getValue().name().lastName().getValue();

            ((AgreementForm) get(proto().caledonAgreement())).setSignature(companyOwnersFullName, companyName);
            ((AgreementForm) get(proto().caledonSoleProprietorshipAgreement())).setSignature(companyOwnersFullName, companyName);
            ((AgreementForm) get(proto().paymentPadAgreement())).setSignature(companyOwnersFullName, companyName);

            ((AgreementForm) get(proto().caledonAgreement())).setIsAgreedTitle(i18n.tr(
                    "I, {0} agree to accept Visa Debit in a Card Not Present transactions environment", companyName));
            ((AgreementForm) get(proto().caledonSoleProprietorshipAgreement())).setIsAgreedTitle(i18n.tr(
                    "I, {0} agree to accept Visa Debit in a Card Not Present transactions environment", companyName));
            ((AgreementForm) get(proto().paymentPadAgreement())).setIsAgreedTitle(i18n.tr("I, {0} agree to accept Payment Pad Terms and Conditions",
                    companyName)); // TODO write the correct text            
        }
    }

    private FormPanel createPricingStep() {
        FormPanel main = new FormPanel(this);
        main.h1(i18n.tr("Pricing Information for Online Payments"));
        main.append(Location.Dual, onlinePaymentPricingTab = new OnlinePaymentPricingTab());
        return main;
    }

    private FormPanel createBusinessInfoStep() {
        FormPanel main = new FormPanel(this);

        main.h1(i18n.tr("Business Information"));
        Label collectionOfBusinessInformation = new Label();
        collectionOfBusinessInformation.setHTML(OnlinePaymentWizardResources.INSTANCE.collectionOfBusinessInformationExplanation().getText());
        main.append(Location.Dual, collectionOfBusinessInformation);
        main.append(Location.Dual, proto().businessInformation(), new BusinessInformationForm());
        return main;
    }

    private FormPanel createPersonalInfoStep() {
        FormPanel main = new FormPanel(this);
        main.h1(i18n.tr("Personal Information"));
        Label collectionOfPersonalInformation = new Label();
        collectionOfPersonalInformation.setHTML(OnlinePaymentWizardResources.INSTANCE.collectionOfPersonalInformationForEquifaxExplanation().getText());
        main.append(Location.Dual, collectionOfPersonalInformation);
        main.append(Location.Dual, inject(proto().personalInformation(), new PersonalInformationForm()));
        return main;
    }

    private FormPanel createPropertyAndBankingStep() {
        // TODO add 'refundable deposit'? or not?
        FormPanel main = new FormPanel(this);
        main.h1(i18n.tr("Property and Banking"));
        main.append(Location.Dual, proto().propertyAccounts(), new PropertyAccountInfoFolder());
        get(proto().propertyAccounts()).addComponentValidator(new AbstractComponentValidator<List<PropertyAccountInfo>>() {
            @Override
            public BasicValidationError isValid() {
                if (getCComponent().getValue() != null && getCComponent().getValue().size() < 1) {
                    return new BasicValidationError(getCComponent(), i18n.tr("At least one property account is required"));
                } else {
                    return null;
                }
            }
        });
        return main;
    }

    private FormPanel createConfirmationStep() {
        FormPanel main = new FormPanel(this);
        main.h1(i18n.tr("Confirmation"));
        main.append(Location.Dual, makeServiceAgreementLabel());
        return main;
    }

    private FormPanel createSignatureStep() {
        // TODO need to add actual signature, but pending the following questions:
        //     - the full text of the agreements is required
        //     - if payment pad indeed needs "I <company name> agree to accept <bla bla bla...>" checkbox, what should be in placed instead of <bla bla bla> 
        final int TOP_I_AGREE_PANEL_PADDING = 20;
        final int AGREEMENTS_SEPARATOR_PADDING = 20;

        FormPanel main = new FormPanel(this);
        main.h1(i18n.tr("Signature"));

        // CALEDON START
        HorizontalPanel caledonPaymentMethodsLogoHeader = new HorizontalPanel();
        caledonPaymentMethodsLogoHeader.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        Label caledonCaption = new Label();
        caledonCaption.setText("Caledon Card Services");
        caledonPaymentMethodsLogoHeader.add(caledonCaption);
        caledonPaymentMethodsLogoHeader.add(new Image(OnlinePaymentWizardResources.INSTANCE.visaLogo()));
        caledonPaymentMethodsLogoHeader.add(new Image(OnlinePaymentWizardResources.INSTANCE.masterCardLogo()));
        caledonPaymentMethodsLogoHeader.add(new Image(OnlinePaymentWizardResources.INSTANCE.visaDebitLogo()));
        caledonPaymentMethodsLogoHeader.add(new Image(OnlinePaymentWizardResources.INSTANCE.echequeLogo()));
        main.append(Location.Dual, caledonPaymentMethodsLogoHeader);

        main.append(Location.Dual, proto().caledonAgreement(), new AgreementForm(OnlinePaymentWizardResources.INSTANCE.caledonSignatureText().getText()));
        main.append(Location.Dual, proto().caledonSoleProprietorshipAgreement(), new AgreementForm(OnlinePaymentWizardResources.INSTANCE.caledonSignatureText()
                .getText()));
        HTML sep = new HTML("&nbsp;");
        sep.getElement().getStyle().setPaddingTop(AGREEMENTS_SEPARATOR_PADDING, Unit.PX);
        sep.getElement().getStyle().setPaddingBottom(AGREEMENTS_SEPARATOR_PADDING, Unit.PX);
        main.append(Location.Dual, sep); // separator        

        HorizontalPanel paypadPaymentMethodsLogoHader = new HorizontalPanel();
        paypadPaymentMethodsLogoHader.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        Label paypadCaption = new Label();
        paypadCaption.setText("Payment Pad Inc.");
        paypadPaymentMethodsLogoHader.add(paypadCaption);
        paypadPaymentMethodsLogoHader.add(new Image(OnlinePaymentWizardResources.INSTANCE.interacLogo()));
        paypadPaymentMethodsLogoHader.add(new Image(OnlinePaymentWizardResources.INSTANCE.directBankingLogo()));
        main.append(Location.Dual, paypadPaymentMethodsLogoHader);
        main.append(Location.Dual, proto().paymentPadAgreement(), new AgreementForm(OnlinePaymentWizardResources.INSTANCE.paymentPadSignatureText().getText()));

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
