/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.services.insurance;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.wizard.WizardStep;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.domain.tenant.insurance.TenantSureConstants;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureInsurancePolicyDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.resources.tenantsure.TenantSureResources;
import com.propertyvista.portal.web.client.themes.TenantSureTheme;
import com.propertyvista.portal.web.client.ui.CPortalEntityWizard;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class TenantSureOrderWizard extends CPortalEntityWizard<TenantSureInsurancePolicyDTO> {

    private static final I18n i18n = I18n.get(TenantSureOrderWizard.class);

    private final WizardStep personalInfoStep;

    private final WizardStep insuranceCoverageStep;

    private final WizardStep paymentMethodStep;

    private final WizardStep confirmationStep;

    private TenantSure2HighCourtReferenceLinks personalInforReferenceLinks;

    private Button quoteSendButton;

    private Label pleaseFillOutTheFormMessage;

    private Label retrievingQuoteMessage;

    private TenantSureQuoteViewer quoteViewer;

    public TenantSureOrderWizard(TenantSureOrderWizardView view, String endButtonCaption) {
        super(TenantSureInsurancePolicyDTO.class, view, i18n.tr("TenantSure Insurance"), endButtonCaption, ThemeColor.contrast3);

        personalInfoStep = addStep(createPersonalInfoStep());

        insuranceCoverageStep = addStep(createInsuranceCoverageStep());

        paymentMethodStep = addStep(createPaymentMethodStep());

        confirmationStep = addStep(createConfirmationStep());

    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        ((TenantSureCoverageRequestForm) get(proto().tenantSureCoverageRequest())).setCoverageParams(getValue().agreementParams());
        ((TenantSurePaymentMethodForm) get(proto().paymentMethod())).setPreAuthorizedAgreement(getValue().agreementParams().preAuthorizedDebitAgreement()
                .getValue());
    }

    private BasicFlexFormPanel createPersonalInfoStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        int row = -1;

        panel.setH1(++row, 0, 1, PortalImages.INSTANCE.residentServicesIcon(), i18n.tr("Personal Disclaimer Terms"));
        HTMLPanel personalDisclaimer = new HTMLPanel(TenantSureResources.INSTANCE.personalDisclaimer().getText());
        Anchor privacyPolicyAnchor = new Anchor(i18n.tr("Privacy Policy"));
        privacyPolicyAnchor.setHref(TenantSureConstants.HIGHCOURT_PARTNERS_PRIVACY_POLICY_HREF);
        privacyPolicyAnchor.setTarget("_blank");
        personalDisclaimer.addAndReplaceElement(privacyPolicyAnchor, TenantSureResources.PRIVACY_POLICY_ANCHOR_ID);

        panel.setWidget(++row, 0, personalDisclaimer);

        panel.setH1(++row, 0, 1, PortalImages.INSTANCE.residentServicesIcon(), i18n.tr("Personal & Contact Information"));

        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().tenantSureCoverageRequest().tenantName())).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().tenantSureCoverageRequest().tenantPhone())).build());
        panel.setWidget(++row, 0, personalInforReferenceLinks = new TenantSure2HighCourtReferenceLinks());
        personalInforReferenceLinks.setCompensationDisclosureStatement(TenantSureConstants.HIGHCOURT_PARTNERS_COMPENSATION_DISCLOSURE_STATEMENT_HREF);
        personalInforReferenceLinks.setPrivacyPolcyAddress(TenantSureConstants.HIGHCOURT_PARTNERS_PRIVACY_POLICY_HREF);

        return panel;
    }

    private BasicFlexFormPanel createInsuranceCoverageStep() {
        BasicFlexFormPanel quotationRequestStepPanel = new BasicFlexFormPanel();
        quotationRequestStepPanel.getElement().getStyle().setMarginBottom(2, Unit.EM);
        int row = -1;
        quotationRequestStepPanel.setH1(++row, 0, 2, i18n.tr("Coverage"));
        quotationRequestStepPanel.setWidget(++row, 0, 2, inject(proto().tenantSureCoverageRequest(), new TenantSureCoverageRequestForm()));

        quotationRequestStepPanel.setH1(++row, 0, 2, i18n.tr("Quote"));

        FlowPanel quoteSection = new FlowPanel();
        quoteSection.addStyleName(TenantSureTheme.StyleName.TSPurchaseViewSection.name());

        pleaseFillOutTheFormMessage = new Label();
        pleaseFillOutTheFormMessage.addStyleName(TenantSureTheme.StyleName.TSPucrhaseViewMessageText.name());
        pleaseFillOutTheFormMessage.setText(i18n.tr("Please fill out the form to get a quote from Highcourt Partners Limited"));
        quoteSection.add(pleaseFillOutTheFormMessage);

        quoteSendButton = new Button(i18n.tr("Email Quote Details"), new Command() {
            @Override
            public void execute() {
                // TODO
                // presenter.sendQuoteDetails(quoteViewer.getValue().quoteId().getValue());
            }
        });
        retrievingQuoteMessage = new Label();
        retrievingQuoteMessage.addStyleName(TenantSureTheme.StyleName.TSPucrhaseViewMessageText.name());
        retrievingQuoteMessage.setText(i18n.tr("Please wait while we preparing your quote..."));
        quoteSection.add(retrievingQuoteMessage);

        SimplePanel quoteSendHolder = new SimplePanel(quoteSendButton);
        quoteSendHolder.setStyleName(TenantSureTheme.StyleName.TSSendDocs.name());
        quoteSection.add(quoteSendHolder);

        quoteSection.add(inject(proto().quote(), quoteViewer = new TenantSureQuoteViewer(true)));

        quotationRequestStepPanel.setWidget(++row, 0, 2, quoteSection);
        quotationRequestStepPanel.getCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_MIDDLE);
        quotationRequestStepPanel.getCellFormatter().getElement(row, 0).getStyle().setProperty("height", "10em");

        return quotationRequestStepPanel;
    }

    private BasicFlexFormPanel createPaymentMethodStep() {
        BasicFlexFormPanel paymentStepPanel = new BasicFlexFormPanel();
        int row = -1;

        paymentStepPanel.setH1(++row, 0, 2, i18n.tr("Coverage"));

        TenantSureCoverageRequestForm confirmationCoverageRequestForm = new TenantSureCoverageRequestForm();
        paymentStepPanel.setWidget(++row, 0, 2, inject(proto().tenantSureCoverageRequestConfirmation(), confirmationCoverageRequestForm));
        confirmationCoverageRequestForm.setViewable(true);

        TenantSureQuoteViewer paymentStepQuoteViewer = new TenantSureQuoteViewer(true);

        paymentStepPanel.setH1(++row, 0, 2, i18n.tr("Quote"));
        paymentStepPanel.setWidget(++row, 0, 2, inject(proto().quoteConfirmation(), paymentStepQuoteViewer));

        paymentStepPanel.setH1(++row, 0, 2, i18n.tr("Payment"));
        TenantSurePaymentMethodForm paymentMethodForm = new TenantSurePaymentMethodForm(new Command() {
            @Override
            public void execute() {
                // TODO enable this
                // presenter.onBillingAddressSameAsCurrentSelected();
            }
        });
        paymentStepPanel.setWidget(++row, 0, 2, inject(proto().paymentMethod(), paymentMethodForm));

        // TODO i'm not sure this is possible with this wizard
//        processingPaymentMessage = new Label();
//        processingPaymentMessage.addStyleName(TenantSureTheme.StyleName.TSPucrhaseViewMessageText.name());
//        processingPaymentMessage.setText(i18n.tr("Processing payment..."));
//        paymentStepPanel.setWidget(++row, 0, 2, processingPaymentMessage);
//
//        paymentProcessingErrorMessage = new Label();
//        paymentProcessingErrorMessage.addStyleName(TenantSureTheme.StyleName.TSPucrhaseViewMessageText.name());
//        paymentProcessingErrorMessage.addStyleName(TenantSureTheme.StyleName.TSPurchaseViewError.name());
//
//        paymentProcessingErrorMessage.setText("");
//        paymentStepPanel.setWidget(++row, 0, 2, paymentProcessingErrorMessage);
        return paymentStepPanel;
    }

    private BasicFlexFormPanel createConfirmationStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        int row = -1;

        return panel;
    }
}
