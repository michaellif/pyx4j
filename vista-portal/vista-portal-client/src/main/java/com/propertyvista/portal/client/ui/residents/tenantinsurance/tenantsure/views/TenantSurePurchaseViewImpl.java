/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodForm;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSurePaymentMethodForm;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSurePersonalDisclaimerForm;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSureQuotationRequestForm;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSureQuoteViewer;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSurePersonalDisclaimerHolderDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuotationRequestParamsDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteDTO;

public class TenantSurePurchaseViewImpl extends Composite implements TenantSurePurchaseView {

    public static enum Styles implements IStyleName {
        TSPurchaseViewSection, TSPurchaseViewBuyInsuranceButton, TSPurchaseViewCancelButton, TSPucrhaseViewMessageText, TSPurchaseViewError;
    }

    private interface Step extends IsWidget {

        void reset();

        String getTitle();

        boolean onProceedToNext();

    }

    private class StepDriver extends Composite {

        private final List<Step> steps;

        private int currentStep = 0;

        private final Button nextStepButton;

        public StepDriver(List<Step> steps) {
            FlowPanel stepsPanel = new FlowPanel();
            this.steps = steps;
            for (Step step : steps) {
                stepsPanel.add(step);
            }

            FlowPanel buttonsPanel = new FlowPanel();
            buttonsPanel.addStyleName(Styles.TSPurchaseViewSection.name());
            buttonsPanel.getElement().getStyle().setPaddingBottom(30, Unit.PX);
            Anchor cancelButton = new Anchor(i18n.tr("Cancel"));
            cancelButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.cancel();
                }
            });
            cancelButton.addStyleName(Styles.TSPurchaseViewCancelButton.name());

            buttonsPanel.add(cancelButton);
            nextStepButton = new Button(i18n.tr("Buy TenantSure"), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (StepDriver.this.steps.get(currentStep).onProceedToNext()) {
                        activateStep(currentStep + 1);
                    }
                }
            });
            nextStepButton.addStyleName(Styles.TSPurchaseViewBuyInsuranceButton.name());
            buttonsPanel.add(nextStepButton);

            stepsPanel.add(buttonsPanel);
            initWidget(stepsPanel);
            reset();
        }

        public void reset() {
            for (Step step : steps) {
                step.reset();
            }
            activateStep(0);
        }

        private void activateStep(int stepNumber) {
            currentStep = stepNumber;
            for (Step step : steps) {
                step.asWidget().setVisible(false);
            }
            steps.get(stepNumber).asWidget().setVisible(true);
            if (stepNumber < steps.size() - 1) {
                nextStepButton.setTextLabel(steps.get(stepNumber + 1).getTitle());
                nextStepButton.setVisible(true);
            } else {
                nextStepButton.setVisible(false);
            }
        }

    }

    private static final I18n i18n = I18n.get(TenantSurePurchaseViewImpl.class);

    private Presenter presenter;

    private Label retrievingQuoteMessage;

    private Label processingPaymentMessage;

    private Label paymentProcessingErrorMessage;

    private Label pleaseFillOutTheFormMessage;

    private TenantSureQuotationRequestForm quotationRequestForm;

    private TenantSureQuoteViewer quoteViewer;

    private PaymentMethodForm paymentMethodForm;

    private TenantSurePersonalDisclaimerForm personalDisclaimerForm;

    private FormFlexPanel quotationRequestStepPanel;

    private FormFlexPanel paymentStepPanel;

    private StepDriver stepDriver;

    public TenantSurePurchaseViewImpl() {
        FormFlexPanel viewPanel = new FormFlexPanel();
        int row = -1;

        viewPanel.setWidget(++row, 0, stepDriver = new StepDriver(Arrays.asList(//@formatter:off
                makePersonalDisclaimerStep(),
                makeQuotationRequestStep(),
                makePaymentStep()                
        )));//@formatter:on

        initWidget(viewPanel);
    }

    @Override
    public void init(TenantSurePersonalDisclaimerHolderDTO disclaimerHolder, TenantSureQuotationRequestParamsDTO quotationRequestParams,
            PaymentMethod paymentMethod) {
        stepDriver.reset();

        personalDisclaimerForm.populate(disclaimerHolder);

        // quote request params section
        quotationRequestForm.setCoverageParams(quotationRequestParams);

        // payment section        
        paymentMethodForm.populate(paymentMethod);

    }

    @Override
    public void setQuote(TenantSureQuoteDTO quote) {
        quoteViewer.setValue(quote);

        retrievingQuoteMessage.setVisible(false);
        boolean canAcceptQuote = quote != null && !quote.isNull();

        quoteViewer.setVisible(canAcceptQuote);
    }

    @Override
    public void setBillingAddress(AddressStructured billingAddress) {
        PaymentMethod pm = paymentMethodForm.getValue();
        pm.billingAddress().set(billingAddress);
        paymentMethodForm.populate(pm);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public TenantSureCoverageDTO getCoverageRequest() {
        return quotationRequestForm.getValue();
    }

    @Override
    public TenantSureQuoteDTO getAcceptedQuote() {
        return quoteViewer.getValue().isNull() ? null : quoteViewer.getValue();
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return paymentMethodForm.getValue().duplicate();
    }

    @Override
    public void populatePaymentProcessingError(String errorReason) {
        if (false) {
            paymentProcessingErrorMessage.setText(errorReason);
            paymentProcessingErrorMessage.setVisible(true);
        }
        processingPaymentMessage.setVisible(false);
        MessageDialog.error(i18n.tr("Payment Failed"), errorReason);
    }

    @Override
    public void waitForQuote() {
        setQuote(null);
        retrievingQuoteMessage.setVisible(true);
        quoteViewer.setVisible(false);
    }

    @Override
    public void waitForPaymentProcessing() {

        processingPaymentMessage.setVisible(true);

        paymentProcessingErrorMessage.setVisible(false);
        paymentProcessingErrorMessage.setText("");
    }

    @Override
    public void populatePaymentProcessingSuccess() {
        MessageDialog.info(i18n.tr("Payment Suceedeed"));
        presenter.onPaymentProcessingSuccessAccepted();
    }

    private Step makePersonalDisclaimerStep() {
        final FlowPanel personalDisclaimerStepPanel = new FlowPanel();
        personalDisclaimerForm = new TenantSurePersonalDisclaimerForm();
        personalDisclaimerForm.initContent();
        personalDisclaimerStepPanel.add(personalDisclaimerForm);
        return new Step() {
            @Override
            public Widget asWidget() {
                return personalDisclaimerStepPanel;
            }

            @Override
            public void reset() {
                personalDisclaimerForm.setVisited(false);
            }

            @Override
            public String getTitle() {
                return i18n.tr("Personal Disclaimer");
            }

            @Override
            public boolean onProceedToNext() {
                personalDisclaimerForm.revalidate();
                return personalDisclaimerForm.isValid();
            };
        };

    }

    private Step makeQuotationRequestStep() {
        quotationRequestStepPanel = new FormFlexPanel();
        int qrpRow = -1;
        quotationRequestStepPanel.setH1(++qrpRow, 0, 1, i18n.tr("Coverage"));
        quotationRequestForm = new TenantSureQuotationRequestForm();
        quotationRequestForm.initContent();
        quotationRequestForm.asWidget().addStyleName(Styles.TSPurchaseViewSection.name());
        quotationRequestForm.addValueChangeHandler(new ValueChangeHandler<TenantSureCoverageDTO>() {
            @Override
            public void onValueChange(ValueChangeEvent<TenantSureCoverageDTO> event) {
                setQuote(null);
                pleaseFillOutTheFormMessage.setVisible(!quotationRequestForm.isValid());
                quotationRequestForm.revalidate();
                if (quotationRequestForm.getValidationResults().isValid()) {
                    presenter.onCoverageRequestChanged();
                }
            }
        });
        quotationRequestStepPanel.setWidget(++qrpRow, 0, quotationRequestForm);

        quotationRequestStepPanel.setH1(++qrpRow, 0, 1, i18n.tr("Quote"));
        FlowPanel quoteSection = new FlowPanel();
        quoteSection.addStyleName(Styles.TSPurchaseViewSection.name());

        pleaseFillOutTheFormMessage = new Label();
        pleaseFillOutTheFormMessage.addStyleName(Styles.TSPucrhaseViewMessageText.name());
        pleaseFillOutTheFormMessage.setText(i18n.tr("Please fill out the form to get a quote"));
        quoteSection.add(pleaseFillOutTheFormMessage);

        quoteViewer = new TenantSureQuoteViewer();
        quoteViewer.initContent();
        quoteSection.add(quoteViewer);

        retrievingQuoteMessage = new Label();
        retrievingQuoteMessage.addStyleName(Styles.TSPucrhaseViewMessageText.name());
        retrievingQuoteMessage.setText(i18n.tr("Please wait while we preparing your quote..."));
        quoteSection.add(retrievingQuoteMessage);

        quotationRequestStepPanel.setWidget(++qrpRow, 0, quoteSection);
        quotationRequestStepPanel.getCellFormatter().setVerticalAlignment(qrpRow, 0, HasVerticalAlignment.ALIGN_MIDDLE);
        quotationRequestStepPanel.getCellFormatter().getElement(qrpRow, 0).getStyle().setProperty("height", "10em");

        return new Step() {
            @Override
            public Widget asWidget() {
                return quotationRequestStepPanel;
            }

            @Override
            public void reset() {
                quotationRequestForm.setVisited(false);
                pleaseFillOutTheFormMessage.setVisible(true);
                quoteViewer.setValue(null);
                quoteViewer.setVisible(false);
                retrievingQuoteMessage.setVisible(false);
            }

            @Override
            public String getTitle() {
                return i18n.tr("Get Quote");
            }

            @Override
            public boolean onProceedToNext() {
                quotationRequestForm.revalidate();
                return quotationRequestForm.isValid();
            }

        };

    }

    private Step makePaymentStep() {
        int row = -1;
        paymentStepPanel = new FormFlexPanel();

        paymentStepPanel.setH1(++row, 0, 1, i18n.tr("Payment"));
        paymentMethodForm = new TenantSurePaymentMethodForm();
        paymentMethodForm.initContent();
        paymentStepPanel.setWidget(++row, 0, paymentMethodForm);

        processingPaymentMessage = new Label();
        processingPaymentMessage.addStyleName(Styles.TSPucrhaseViewMessageText.name());
        processingPaymentMessage.setText(i18n.tr("Processing payment..."));
        paymentStepPanel.setWidget(++row, 0, processingPaymentMessage);

        paymentProcessingErrorMessage = new Label();
        paymentProcessingErrorMessage.addStyleName(Styles.TSPucrhaseViewMessageText.name());
        paymentProcessingErrorMessage.addStyleName(Styles.TSPurchaseViewError.name());

        paymentProcessingErrorMessage.setText("");
        paymentStepPanel.setWidget(++row, 0, paymentProcessingErrorMessage);

        return new Step() {

            @Override
            public Widget asWidget() {
                return paymentStepPanel;
            }

            @Override
            public void reset() {
                processingPaymentMessage.setVisible(false);
                paymentProcessingErrorMessage.setVisible(false);

                paymentMethodForm.setVisited(false);
            }

            @Override
            public boolean onProceedToNext() {
                return false;
            }

            @Override
            public String getTitle() {
                return i18n.tr("Payment");
            }
        };
    }

}
