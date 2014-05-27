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

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.LabelPosition;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.wizard.WizardDecorator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.domain.contact.InternationalAddress;
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
import com.propertyvista.portal.shared.ui.PortalFormPanel;

public class TenantSureOrderWizard extends CPortalEntityWizard<TenantSureInsurancePolicyDTO> {

    private static final I18n i18n = I18n.get(TenantSureOrderWizard.class);

    private Button quoteSendButton;

    private Label pleaseFillOutTheFormMessage;

    private Label retrievingQuoteMessage;

    private TenantSureOrderWizardPersenter presenter;

    private final WizardDecorator<TenantSureInsurancePolicyDTO> wizardDecorator;

    private final TenantSureQuoteViewer quoteViewer = new TenantSureQuoteViewer(true);

    private final TenantSurePaymentMethodForm paymentMethodForm = new TenantSurePaymentMethodForm(new Command() {
        @Override
        public void execute() {
            presenter.populateCurrentAddressAsBillingAddress();
        }
    });

    private TenantSureCoverageRequestForm coverageRequestForm;

    public TenantSureOrderWizard(TenantSureOrderWizardView view, String endButtonCaption) {
        super(TenantSureInsurancePolicyDTO.class, view, i18n.tr("TenantSure Insurance"), endButtonCaption, ThemeColor.contrast3);

        wizardDecorator = (WizardDecorator<TenantSureInsurancePolicyDTO>) getDecorator();

        addStep(createPersonalInfoStep(), i18n.tr("Personal Info"));
        addStep(createInsuranceCoverageStep(), i18n.tr("Insurance Coverage"));
        addStep(createPaymentMethodStep(), i18n.tr("Payment Method"));

    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        coverageRequestForm.setCoverageParams(getValue().agreementParams());

        get(proto().tenantSureCoverageRequest().tenantName()).setViewable(getValue().agreementParams().isTenantInitializedInCfc().getValue(false));
        get(proto().tenantSureCoverageRequest().tenantPhone()).setViewable(getValue().agreementParams().isTenantInitializedInCfc().getValue(false));

        retrievingQuoteMessage.setVisible(false);
        pleaseFillOutTheFormMessage.setVisible(true);
        quoteSendButton.setVisible(false);
        quoteViewer.setVisible(false);

        wizardDecorator.getBtnNext().setEnabled(true);
    }

    private IsWidget createPersonalInfoStep() {
        PortalFormPanel formPanel = new PortalFormPanel(this);

        formPanel.h1(PortalImages.INSTANCE.residentServicesIcon(), i18n.tr("Personal Disclaimer Terms"));

        HTMLPanel personalDisclaimer = new HTMLPanel(TenantSureResources.INSTANCE.personalDisclaimer().getText());
        personalDisclaimer.getElement().getStyle().setTextAlign(TextAlign.LEFT);
        Anchor privacyPolicyAnchor = new Anchor(i18n.tr("Privacy Policy"));
        privacyPolicyAnchor.setHref(TenantSureConstants.HIGHCOURT_PARTNERS_PRIVACY_POLICY_HREF);
        privacyPolicyAnchor.setTarget("_blank");
        personalDisclaimer.addAndReplaceElement(privacyPolicyAnchor, TenantSureResources.PRIVACY_POLICY_ANCHOR_ID);

        formPanel.append(Location.Left, personalDisclaimer);
        formPanel.br();
        formPanel.append(Location.Left, proto().personalDisclaimerSignature()).decorate().customLabel("").labelPosition(LabelPosition.hidden);
        formPanel.h1(PortalImages.INSTANCE.residentServicesIcon(), i18n.tr("Personal & Contact Information"));
        formPanel.append(Location.Left, proto().tenantSureCoverageRequest().tenantName()).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().tenantSureCoverageRequest().tenantPhone()).decorate().componentWidth(200);

        formPanel.br();
        formPanel.append(Location.Left, createTermLink(i18n.tr("Privacy Policy"), TenantSureConstants.HIGHCOURT_PARTNERS_PRIVACY_POLICY_HREF));
        formPanel.append(Location.Left,
                createTermLink(i18n.tr("Compensation Disclosure Statement"), TenantSureConstants.HIGHCOURT_PARTNERS_COMPENSATION_DISCLOSURE_STATEMENT_HREF));

        return formPanel;
    }

    private IsWidget createInsuranceCoverageStep() {
        PortalFormPanel formPanel = new PortalFormPanel(this);

        formPanel.append(Location.Left, proto().tenantSureCoverageRequest(), coverageRequestForm = new TenantSureCoverageRequestForm());
        get(proto().tenantSureCoverageRequest()).addValueChangeHandler(new ValueChangeHandler<TenantSureCoverageDTO>() {
            @Override
            public void onValueChange(ValueChangeEvent<TenantSureCoverageDTO> event) {
                get(proto().tenantSureCoverageRequestConfirmation()).setValue(event.getValue(), false);
                presenter.getNewQuote();
            }
        });

        quoteSendButton = new Button(i18n.tr("Email Quote Details"), new Command() {
            @Override
            public void execute() {
                presenter.sendQuoteDetailsEmail();
            }
        });
        quoteSendButton.setVisible(false);

        formPanel.h1(i18n.tr("Quote"), quoteSendButton);

        FlowPanel quoteSection = new FlowPanel();
        quoteSection.addStyleName(TenantSureTheme.StyleName.TenantSurePurchaseViewSection.name());

        pleaseFillOutTheFormMessage = new Label();
        pleaseFillOutTheFormMessage.addStyleName(TenantSureTheme.StyleName.TenantSurePucrhaseViewMessageText.name());
        pleaseFillOutTheFormMessage.setText(i18n.tr("Please fill out the form to get a quote from Highcourt Partners Limited"));
        quoteSection.add(pleaseFillOutTheFormMessage);

        retrievingQuoteMessage = new Label();
        retrievingQuoteMessage.addStyleName(TenantSureTheme.StyleName.TenantSurePucrhaseViewMessageText.name());
        retrievingQuoteMessage.setText(i18n.tr("Please wait while we preparing your quote..."));
        retrievingQuoteMessage.setVisible(false);
        quoteSection.add(retrievingQuoteMessage);

        quoteSection.add(inject(proto().quote(), quoteViewer));

        formPanel.append(Location.Left, quoteSection);

        return formPanel;
    }

    private IsWidget createPaymentMethodStep() {
        PortalFormPanel formPanel = new PortalFormPanel(this);

        formPanel.h1(i18n.tr("Summary"));
        formPanel.append(Location.Left, proto().tenantSureCoverageRequestConfirmation(), new TenantSureCoverageRequestForm(true));
        formPanel.append(Location.Left, proto().quoteConfirmation(), new TenantSureQuoteViewer(true));

        formPanel.h1(i18n.tr("Payment"));
        formPanel.append(Location.Left, proto().paymentMethod(), paymentMethodForm);

        return formPanel;
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

    public void setBillingAddress(InternationalAddress billingAddress) {
        paymentMethodForm.setBillingAddress(billingAddress);
    }

    public void setPresenter(TenantSureOrderWizardPersenter presenter) {
        this.presenter = presenter;
    }

    private Anchor createTermLink(String text, String href) {
        Anchor anchor = new Anchor(text);
        anchor.setStyleName(NavigationAnchorTheme.StyleName.NavigationAnchor.name());
        anchor.setTarget("_blank");
        anchor.setHref(href);
        return anchor;
    }

}
