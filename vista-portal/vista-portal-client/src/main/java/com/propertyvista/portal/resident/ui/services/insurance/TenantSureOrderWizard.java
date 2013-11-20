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
package com.propertyvista.portal.resident.ui.services.insurance;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.decorators.IDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.wizard.WizardDecorator;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.tenant.insurance.TenantSureConstants;
import com.propertyvista.portal.resident.resources.tenantsure.TenantSureResources;
import com.propertyvista.portal.resident.themes.TenantSureTheme;
import com.propertyvista.portal.resident.ui.services.insurance.TenantSureOrderWizardView.TenantSureOrderWizardPersenter;
import com.propertyvista.portal.resident.ui.services.insurance.tenantsurepaymentmethod.TenantSurePaymentMethodForm;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureInsurancePolicyDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureQuoteDTO;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.themes.NavigationAnchorTheme;
import com.propertyvista.portal.shared.ui.CPortalEntityWizard;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class TenantSureOrderWizard extends CPortalEntityWizard<TenantSureInsurancePolicyDTO> {

    private static final I18n i18n = I18n.get(TenantSureOrderWizard.class);

    private TenantSure2HighCourtReferenceLinks personalInforReferenceLinks;

    private Button quoteSendButton;

    private Label pleaseFillOutTheFormMessage;

    private Label retrievingQuoteMessage;

    private TenantSureOrderWizardPersenter presenter;

    private WizardDecorator<TenantSureInsurancePolicyDTO> wizardDecorator;

    private final TenantSureQuoteViewer quoteViewer = new TenantSureQuoteViewer(true);

    private final TenantSurePaymentMethodForm paymentMethodForm = new TenantSurePaymentMethodForm(new Command() {
        @Override
        public void execute() {
            presenter.populateCurrentAddressAsBillingAddress();
        }
    });

    public TenantSureOrderWizard(TenantSureOrderWizardView view, String endButtonCaption) {
        super(TenantSureInsurancePolicyDTO.class, view, i18n.tr("TenantSure Insurance"), endButtonCaption, ThemeColor.contrast3);

        addStep(createPersonalInfoStep());
        addStep(createInsuranceCoverageStep());
        addStep(createPaymentMethodStep());

    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        ((TenantSureCoverageRequestForm) get(proto().tenantSureCoverageRequest())).setCoverageParams(getValue().agreementParams());
        paymentMethodForm.setPreAuthorizedAgreement(getValue().agreementParams().preAuthorizedDebitAgreement().getValue());

        get(proto().tenantSureCoverageRequest().tenantName()).setViewable(getValue().agreementParams().isTenantInitializedInCfc().isBooleanTrue());
        get(proto().tenantSureCoverageRequest().tenantPhone()).setViewable(getValue().agreementParams().isTenantInitializedInCfc().isBooleanTrue());

        retrievingQuoteMessage.setVisible(false);
        pleaseFillOutTheFormMessage.setVisible(true);
        quoteSendButton.setVisible(false);
        quoteViewer.setVisible(false);

        wizardDecorator.getBtnNext().setEnabled(true);
    }

    private BasicFlexFormPanel createPersonalInfoStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        int row = -1;

        panel.setH1(++row, 0, 1, PortalImages.INSTANCE.residentServicesIcon(), i18n.tr("Personal Disclaimer Terms"));
        HTMLPanel personalDisclaimer = new HTMLPanel(TenantSureResources.INSTANCE.personalDisclaimer().getText());
        Anchor privacyPolicyAnchor = new Anchor(i18n.tr("Privacy Policy"));
        privacyPolicyAnchor.setStyleName(NavigationAnchorTheme.StyleName.NavigationAnchor.name());
        privacyPolicyAnchor.setHref(TenantSureConstants.HIGHCOURT_PARTNERS_PRIVACY_POLICY_HREF);
        privacyPolicyAnchor.setTarget("_blank");
        personalDisclaimer.addAndReplaceElement(privacyPolicyAnchor, TenantSureResources.PRIVACY_POLICY_ANCHOR_ID);

        panel.setWidget(++row, 0, personalDisclaimer);

        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().isAgreedToPersonalDisclaimer())).build());
        get(proto().isAgreedToPersonalDisclaimer()).addValueValidator(new EditableValueValidator<Boolean>() {
            @Override
            public ValidationError isValid(CComponent<Boolean> component, Boolean value) {
                if (value != null && !value) {
                    return new ValidationError(component, i18n.tr("You must agree to Personal Disclaimer to continue"));
                }
                return null;
            }
        });

        panel.setH1(++row, 0, 1, PortalImages.INSTANCE.residentServicesIcon(), i18n.tr("Personal & Contact Information"));

        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().tenantSureCoverageRequest().tenantName()), 200).build());
        panel.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().tenantSureCoverageRequest().tenantPhone()), 200).build());
        panel.setWidget(++row, 0, 1, personalInforReferenceLinks = new TenantSure2HighCourtReferenceLinks());

        personalInforReferenceLinks.setCompensationDisclosureStatementHref(TenantSureConstants.HIGHCOURT_PARTNERS_COMPENSATION_DISCLOSURE_STATEMENT_HREF);
        personalInforReferenceLinks.setPrivacyPolcyHref(TenantSureConstants.HIGHCOURT_PARTNERS_PRIVACY_POLICY_HREF);

        return panel;
    }

    private BasicFlexFormPanel createInsuranceCoverageStep() {
        BasicFlexFormPanel quotationRequestStepPanel = new BasicFlexFormPanel();
        quotationRequestStepPanel.getElement().getStyle().setMarginBottom(2, Unit.EM);
        int row = -1;
        quotationRequestStepPanel.setWidget(++row, 0, 1, inject(proto().tenantSureCoverageRequest(), new TenantSureCoverageRequestForm()));
        get(proto().tenantSureCoverageRequest()).addValueChangeHandler(new ValueChangeHandler<TenantSureCoverageDTO>() {
            @Override
            public void onValueChange(ValueChangeEvent<TenantSureCoverageDTO> event) {
                if (get(proto().tenantSureCoverageRequest()).isValid()) {
                    presenter.getNewQuote();
                    get(proto().tenantSureCoverageRequestConfirmation()).setValue(event.getValue(), false);
                }
            }
        });

        quotationRequestStepPanel.setH1(++row, 0, 1, i18n.tr("Quote"));

        FlowPanel quoteSection = new FlowPanel();
        quoteSection.addStyleName(TenantSureTheme.StyleName.TSPurchaseViewSection.name());

        pleaseFillOutTheFormMessage = new Label();
        pleaseFillOutTheFormMessage.addStyleName(TenantSureTheme.StyleName.TSPucrhaseViewMessageText.name());
        pleaseFillOutTheFormMessage.setText(i18n.tr("Please fill out the form to get a quote from Highcourt Partners Limited"));
        quoteSection.add(pleaseFillOutTheFormMessage);

        quoteSendButton = new Button(i18n.tr("Email Quote Details"), new Command() {
            @Override
            public void execute() {
                presenter.sendQuoteDetailsEmail();
            }
        });
        quoteSendButton.setVisible(false);

        retrievingQuoteMessage = new Label();
        retrievingQuoteMessage.addStyleName(TenantSureTheme.StyleName.TSPucrhaseViewMessageText.name());
        retrievingQuoteMessage.setText(i18n.tr("Please wait while we preparing your quote..."));
        retrievingQuoteMessage.setVisible(false);
        quoteSection.add(retrievingQuoteMessage);

        SimplePanel quoteSendHolder = new SimplePanel(quoteSendButton);
        quoteSendHolder.setStyleName(TenantSureTheme.StyleName.TSSendDocs.name());
        quoteSection.add(quoteSendHolder);

        quoteSection.add(inject(proto().quote(), quoteViewer));

        quotationRequestStepPanel.setWidget(++row, 0, 1, quoteSection);
        quotationRequestStepPanel.getCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_MIDDLE);
        quotationRequestStepPanel.getCellFormatter().getElement(row, 0).getStyle().setProperty("height", "10em");

        return quotationRequestStepPanel;
    }

    private BasicFlexFormPanel createPaymentMethodStep() {
        BasicFlexFormPanel paymentStepPanel = new BasicFlexFormPanel();
        int row = -1;

        paymentStepPanel.setH1(++row, 0, 1, i18n.tr("Summary"));
        paymentStepPanel.setWidget(++row, 0, 1, inject(proto().tenantSureCoverageRequestConfirmation(), new TenantSureCoverageRequestForm(true)));
        paymentStepPanel.setWidget(++row, 0, 1, inject(proto().quoteConfirmation(), new TenantSureQuoteViewer(true)));

        paymentStepPanel.setH1(++row, 0, 1, i18n.tr("Payment"));
        paymentStepPanel.setWidget(++row, 0, 1, inject(proto().paymentMethod(), paymentMethodForm));

        return paymentStepPanel;
    }

    public void waitForQuote() {
        retrievingQuoteMessage.setVisible(true);
        pleaseFillOutTheFormMessage.setVisible(false);
        get(proto().quote()).setVisible(false);
        quoteSendButton.setVisible(false);
    }

    public void setQuote(TenantSureQuoteDTO quote) {
        retrievingQuoteMessage.setVisible(false);

        get(proto().quote()).setVisible(true);
        // we don't want to to show quote send button unless we get a quote with an id
        // because we can get a 'manual quote' i.e. tenant is asked to call HighCourt
        quoteSendButton.setVisible(quote != null && !quote.quoteId().isNull());

        get(proto().quote()).setValue(quote);
        get(proto().quoteConfirmation()).setValue(quote.duplicate(TenantSureQuoteDTO.class));

        wizardDecorator.getBtnNext().setEnabled(quote != null && !quote.quoteId().isNull());
    }

    public void setBillingAddress(AddressSimple billingAddress) {
        paymentMethodForm.setBillingAddress(billingAddress);
    }

    public void setPresenter(TenantSureOrderWizardPersenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected IDecorator<?> createDecorator() {
        wizardDecorator = (WizardDecorator<TenantSureInsurancePolicyDTO>) super.createDecorator();
        return wizardDecorator;
    }
}
