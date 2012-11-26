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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodForm;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSureLogo;
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
        TSPurchaseViewSection, TSPurchaseViewNextStepButton, TSPurchaseViewCancelButton, TSPucrhaseViewMessageText, TSPurchaseViewError;
    }

    private interface Step extends IsWidget {

        void reset();

        String getTitle();

        void onProceedToNext(AsyncCallback<VoidSerializable> callback);

        void setNextButton(Button next);

    }

    private class StepDriver extends Composite {

        private final List<Step> steps;

        private int currentStep = 0;

        private final Button nextStepButton;

        private final Anchor cancelButton;

        public StepDriver(List<Step> steps) {
            FlowPanel stepsPanel = new FlowPanel();
            this.steps = steps;
            for (Step step : steps) {
                stepsPanel.add(step);
            }

            FlowPanel buttonsPanel = new FlowPanel();
            buttonsPanel.addStyleName(Styles.TSPurchaseViewSection.name());
            buttonsPanel.getElement().getStyle().setPaddingBottom(30, Unit.PX);
            cancelButton = new Anchor(i18n.tr("Cancel"));
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
                    StepDriver.this.steps.get(currentStep).onProceedToNext(new AsyncCallback<VoidSerializable>() {

                        @Override
                        public void onSuccess(VoidSerializable result) {
                            activateStep(currentStep + 1);
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            // TODO display error
                        }

                    });
                }
            });
            nextStepButton.addStyleName(Styles.TSPurchaseViewNextStepButton.name());
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
                step.setNextButton(nextStepButton);
            }
            steps.get(stepNumber).asWidget().setVisible(true);
            if (stepNumber < steps.size() - 1) {
                nextStepButton.setTextLabel(steps.get(stepNumber + 1).getTitle());
                nextStepButton.setVisible(true);
                nextStepButton.setEnabled(false);
                cancelButton.setVisible(true);
            } else {
                nextStepButton.setVisible(false);
                cancelButton.setVisible(false);
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

    private PaymentMethodForm<InsurancePaymentMethod> paymentMethodForm;

    private TenantSurePersonalDisclaimerForm personalDisclaimerForm;

    private FormFlexPanel quotationRequestStepPanel;

    private FormFlexPanel paymentStepPanel;

    private StepDriver stepDriver;

    private Button acceptQuoteButton;

    protected AsyncCallback<VoidSerializable> paymentSucceededCallback;

    public TenantSurePurchaseViewImpl() {
        FormFlexPanel viewPanel = new FormFlexPanel();
        int row = -1;

        TenantSureLogo logo = new TenantSureLogo();
        logo.getElement().getStyle().setMarginTop(20, Unit.PX);
        logo.getElement().getStyle().setMarginBottom(20, Unit.PX);
        viewPanel.setWidget(++row, 0, logo);
        viewPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        viewPanel.getFlexCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_MIDDLE);

        viewPanel.setWidget(++row, 0, stepDriver = new StepDriver(Arrays.asList(//@formatter:off
                makePersonalDisclaimerStep(),
                makeQuotationRequestStep(),
                makePaymentStep(),
                makePaymentSucceededStep()
        )));//@formatter:on

        initWidget(viewPanel);
    }

    @Override
    public void init(TenantSurePersonalDisclaimerHolderDTO disclaimerHolder, TenantSureQuotationRequestParamsDTO quotationRequestParams,
            InsurancePaymentMethod paymentMethod) {
        stepDriver.reset();

        personalDisclaimerForm.populate(disclaimerHolder);

        // quote request params section
        quotationRequestForm.setCoverageParams(quotationRequestParams);

        // payment section        
        paymentMethodForm.populate(paymentMethod);

        // reset quote
        setQuote(null);
    }

    @Override
    public void setQuote(TenantSureQuoteDTO quote) {
        quoteViewer.setValue(quote);

        retrievingQuoteMessage.setVisible(false);
        boolean canAcceptQuote = quote != null && !quote.isNull() && quote.specialQuote().isNull();
        if (acceptQuoteButton != null) {
            acceptQuoteButton.setEnabled(canAcceptQuote);
        }
        quoteViewer.setVisible(quote != null);

    }

    @Override
    public void setBillingAddress(AddressStructured billingAddress) {
        InsurancePaymentMethod pm = paymentMethodForm.getValue();
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
        return quoteViewer.getValue();
    }

    @Override
    public InsurancePaymentMethod getPaymentMethod() {
        return paymentMethodForm.getValue().duplicate(InsurancePaymentMethod.class);
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
        paymentSucceededCallback.onSuccess(null);
    }

    private Step makePersonalDisclaimerStep() {
        final FlowPanel personalDisclaimerStepPanel = new FlowPanel();
        personalDisclaimerForm = new TenantSurePersonalDisclaimerForm();
        personalDisclaimerForm.initContent();
        personalDisclaimerStepPanel.add(personalDisclaimerForm);
        return new Step() {
            private Button next;

            {
                personalDisclaimerForm.addValueChangeHandler(new ValueChangeHandler<TenantSurePersonalDisclaimerHolderDTO>() {

                    @Override
                    public void onValueChange(ValueChangeEvent<TenantSurePersonalDisclaimerHolderDTO> event) {
                        personalDisclaimerForm.revalidate();
                        if (next != null) {
                            next.setEnabled(personalDisclaimerForm.isValid());
                        }
                    }
                });
            }

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
            public void onProceedToNext(AsyncCallback<VoidSerializable> callback) {
                personalDisclaimerForm.revalidate();
                if (personalDisclaimerForm.isValid()) {
                    callback.onSuccess(null);
                } else {
                    MessageDialog.info("You must accept the agreement to continue");
                }
            }

            @Override
            public void setNextButton(Button next) {
                this.next = next;
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

            {
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

            }

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
            public void onProceedToNext(AsyncCallback<VoidSerializable> callback) {
                quotationRequestForm.revalidate();
                if (quotationRequestForm.isValid()) {
                    callback.onSuccess(null);
                } else {
                    MessageDialog.info(i18n.tr("Please fill out the form to continue"));
                }
            }

            @Override
            public void setNextButton(Button next) {
                TenantSurePurchaseViewImpl.this.acceptQuoteButton = next;
            }

        };

    }

    private Step makePaymentStep() {
        int row = -1;
        paymentStepPanel = new FormFlexPanel();

        paymentStepPanel.setH1(++row, 0, 1, i18n.tr("Payment"));
        paymentMethodForm = new TenantSurePaymentMethodForm(new Command() {
            @Override
            public void execute() {
                presenter.onBillingAddressSameAsCurrentSelected();
            }
        });
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

            private Button next = null;

            {
                paymentMethodForm.addValueChangeHandler(new ValueChangeHandler<InsurancePaymentMethod>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<InsurancePaymentMethod> event) {
                        if (next != null) {
                            paymentMethodForm.revalidate();
                            next.setEnabled(paymentMethodForm.isValid());
                        }

                    }
                });
            }

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
            public void onProceedToNext(AsyncCallback<VoidSerializable> callback) {
                presenter.onQuoteAccepted();
                TenantSurePurchaseViewImpl.this.paymentSucceededCallback = callback;
            }

            @Override
            public String getTitle() {
                return i18n.tr("Accept quote and proceed to payment");
            }

            @Override
            public void setNextButton(Button next) {
                this.next = next;
            }
        };
    }

    private Step makePaymentSucceededStep() {
        final FlowPanel finishStepPanel = new FlowPanel();
        finishStepPanel.getElement().getStyle().setProperty("display", "table-cell");
        finishStepPanel.getElement().getStyle().setProperty("verticalAlign", "middle");
        finishStepPanel.getElement().getStyle().setProperty("textAlign", "center");
        finishStepPanel.getElement().getStyle().setHeight(10., Unit.EM);

        Label label = new Label();
        label.addStyleName(Styles.TSPucrhaseViewMessageText.name());
        label.setText(i18n.tr("Payment Processed Successfuly: an email with your insurance policy has been sent to your email."));
        finishStepPanel.add(label);

        Anchor returnToInsuranceManagement = new Anchor(i18n.tr("return to Tenant Insurance"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.onPaymentProcessingSuccessAccepted();
            }
        });
        finishStepPanel.add(returnToInsuranceManagement);

        return new Step() {

            @Override
            public Widget asWidget() {
                return finishStepPanel;
            }

            @Override
            public void reset() {
                return;
            }

            @Override
            public void onProceedToNext(AsyncCallback<VoidSerializable> callback) {
                // this is not required;
            }

            @Override
            public String getTitle() {
                return i18n.tr("Buy TenantSure");
            }

            @Override
            public void setNextButton(Button next) {
                // this is not required;
            }
        };
    }

}
