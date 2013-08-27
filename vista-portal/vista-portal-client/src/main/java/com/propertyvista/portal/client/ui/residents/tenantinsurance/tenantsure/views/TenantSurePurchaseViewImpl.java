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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.tenant.insurance.TenantSureConstants;
import com.propertyvista.portal.client.themes.TenantSureTheme;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSurePaymentMethodForm;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSurePersonalDisclaimerForm;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSureQuotationRequestForm;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSureQuoteViewer;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSureViewDecorator;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.resources.TenantSureResources;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSurePersonalDisclaimerHolderDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuotationRequestParamsDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteDTO;

// TODO refactor this one 
public class TenantSurePurchaseViewImpl extends Composite implements TenantSurePurchaseView {

    static final I18n i18n = I18n.get(TenantSurePurchaseViewImpl.class);

    private Presenter presenter;

    private Label retrievingQuoteMessage;

    private Label processingPaymentMessage;

    private Label paymentProcessingErrorMessage;

    private Label pleaseFillOutTheFormMessage;

    private TenantSureQuotationRequestForm quoteRequestForm;

    private TenantSureQuoteViewer quoteViewer;

    private TenantSureQuoteViewer paymentStepQuoteViewer;

    private TenantSurePaymentMethodForm paymentMethodForm;

    private TenantSurePersonalDisclaimerForm personalDisclaimerForm;

    private TwoColumnFlexFormPanel quotationRequestStepPanel;

    private TwoColumnFlexFormPanel paymentStepPanel;

    private Button acceptQuoteButton;

    protected AsyncCallback<VoidSerializable> paymentSucceededCallback;

    private final Label tenantSureServiceUnavailable;

    private final StepDriver stepDriver;

    private TenantSurePersonalDisclaimerHolderDTO personalDiscalmerHolder;

    private Button quoteSend;

    public TenantSurePurchaseViewImpl() {
        TenantSureViewDecorator viewDecorator = new TenantSureViewDecorator();
        viewDecorator.setCompensationDisclosureStatement(TenantSureConstants.HIGHCOURT_PARTNERS_COMPENSATION_DISCLOSURE_STATEMENT_HREF);
        viewDecorator.setPrivacyPolcyAddress(TenantSureConstants.HIGHCOURT_PARTNERS_PRIVACY_POLICY_HREF);

        TwoColumnFlexFormPanel viewPanel = new TwoColumnFlexFormPanel();
        int row = -1;

        stepDriver = new StepDriver(Arrays.asList(//@formatter:off
                        makePersonalDisclaimerStep(),
                        makeQuotationRequestStep(),
                        makePaymentStep(),
                        makePaymentSucceededStep()),
                new Command() {            
                @Override
                public void execute() {
                    presenter.cancel();
                }
        });//@formatter:on
        viewPanel.setWidget(++row, 0, stepDriver);

        tenantSureServiceUnavailable = new Label();
        tenantSureServiceUnavailable.setStyleName(TenantSureTheme.StyleName.TSUnavailableMessage.name());
        tenantSureServiceUnavailable.setVisible(false);
        viewPanel.setWidget(++row, 0, tenantSureServiceUnavailable);

        viewDecorator.setContent(viewPanel);
        initWidget(viewDecorator);
    }

    @Override
    public void init(TenantSureQuotationRequestParamsDTO quotationRequestParams, InsurancePaymentMethod paymentMethod) {
        tenantSureServiceUnavailable.setVisible(false);

        acceptQuoteButton = null;
        stepDriver.setVisible(true);
        stepDriver.reset();

        personalDiscalmerHolder = EntityFactory.create(TenantSurePersonalDisclaimerHolderDTO.class);
        personalDiscalmerHolder.terms().setValue(TenantSureResources.INSTANCE.personalDisclaimer().getText());
        personalDiscalmerHolder.isAgreed().setValue(false);
        personalDisclaimerForm.populate(personalDiscalmerHolder);

        quoteRequestForm.setCoverageParams(quotationRequestParams);

        paymentMethodForm.setVisited(false);
        paymentMethodForm.populate(paymentMethod);
        paymentMethodForm.setPreAuthorizedAgreement(quotationRequestParams.preAuthorizedDebitAgreement().getValue());

        setQuote(null);
    }

    @Override
    public void reportError(String message) {
        // if error happened it means that all processing stopped
        // so we hide all the progress related UI:
        // TODO i think this kind of error processing should be done inside the current step context
        retrievingQuoteMessage.setVisible(false);
        paymentProcessingErrorMessage.setVisible(false);
        processingPaymentMessage.setVisible(false);
        MessageDialog.info(message);
    }

    @Override
    public void setTenantSureOnMaintenance(String message) {
        stepDriver.setVisible(false);
        tenantSureServiceUnavailable.setVisible(true);
        tenantSureServiceUnavailable.setText(message);
    }

    @Override
    public void setQuote(TenantSureQuoteDTO quote) {
        retrievingQuoteMessage.setVisible(false);

        quoteViewer.setValue(quote);
        paymentStepQuoteViewer.setValue(quote);

        quoteViewer.setVisible(quote != null);

        boolean canAcceptQuote = quote != null && !quote.isNull() && quote.specialQuote().isNull();
        if (acceptQuoteButton != null) { // this is hack and it's needed there
            acceptQuoteButton.setEnabled(canAcceptQuote);
        }
        quoteSend.setVisible(canAcceptQuote);

    }

    @Override
    public void setBillingAddress(AddressSimple billingAddress) {
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
        return quoteRequestForm.getValue();
    }

    @Override
    public TenantSureQuoteDTO getAcceptedQuote() {
        return quoteViewer.getValue().duplicate(TenantSureQuoteDTO.class);
    }

    @Override
    public InsurancePaymentMethod getPaymentMethod() {
        return paymentMethodForm.getValue().duplicate(InsurancePaymentMethod.class);
    }

    @Override
    public void populatePaymentProcessingError(String errorReason) {
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

    @Override
    public void populateSendQuoteDetailSuccess(String email) {
        MessageDialog.info(i18n.tr("Your quote documentation was sent to {0}", email));
    }

    private Step makePersonalDisclaimerStep() {
        final FlowPanel personalDisclaimerStepPanel = new FlowPanel();
        personalDisclaimerStepPanel.getElement().getStyle().setMarginLeft(20, Unit.PX);
        personalDisclaimerStepPanel.getElement().getStyle().setMarginRight(20, Unit.PX);

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
            public void onProceedToNext(AsyncCallback<VoidSerializable> callback) {
                if (!personalDisclaimerForm.getValue().isAgreed().isBooleanTrue()) {
                    MessageDialog.info("You must accept the agreement to continue");
                } else {
                    callback.onSuccess(null);
                }
            }

            @Override
            public void setNextButton(Button next) {
                next.setEnabled(true);
            };
        };

    }

    private Step makeQuotationRequestStep() {
        quotationRequestStepPanel = new TwoColumnFlexFormPanel();
        quotationRequestStepPanel.getElement().getStyle().setMarginBottom(2, Unit.EM);
        int qrpRow = -1;
        quotationRequestStepPanel.setH1(++qrpRow, 0, 2, i18n.tr("Coverage"));
        quoteRequestForm = new TenantSureQuotationRequestForm();
        quoteRequestForm.initContent();
        quoteRequestForm.asWidget().addStyleName(TenantSureTheme.StyleName.TSPurchaseViewSection.name());
        quotationRequestStepPanel.setWidget(++qrpRow, 0, 2, quoteRequestForm);

        quotationRequestStepPanel.setH1(++qrpRow, 0, 2, i18n.tr("Quote"));
        FlowPanel quoteSection = new FlowPanel();
        quoteSection.addStyleName(TenantSureTheme.StyleName.TSPurchaseViewSection.name());

        pleaseFillOutTheFormMessage = new Label();
        pleaseFillOutTheFormMessage.addStyleName(TenantSureTheme.StyleName.TSPucrhaseViewMessageText.name());
        pleaseFillOutTheFormMessage.setText(i18n.tr("Please fill out the form to get a quote from Highcourt Partners Limited"));
        quoteSection.add(pleaseFillOutTheFormMessage);

        quoteSend = new Button(i18n.tr("Email Quote Details"), new Command() {
            @Override
            public void execute() {
                presenter.sendQuoteDetails(quoteViewer.getValue().quoteId().getValue());
            }
        });
        SimplePanel quoteSendHolder = new SimplePanel(quoteSend);
        quoteSendHolder.setStyleName(TenantSureTheme.StyleName.TSSendDocs.name());
        quoteSection.add(quoteSendHolder);

        quoteViewer = new TenantSureQuoteViewer(true);
        quoteSection.add(quoteViewer);

        retrievingQuoteMessage = new Label();
        retrievingQuoteMessage.addStyleName(TenantSureTheme.StyleName.TSPucrhaseViewMessageText.name());
        retrievingQuoteMessage.setText(i18n.tr("Please wait while we preparing your quote..."));
        quoteSection.add(retrievingQuoteMessage);

        quotationRequestStepPanel.setWidget(++qrpRow, 0, 2, quoteSection);
        quotationRequestStepPanel.getCellFormatter().setVerticalAlignment(qrpRow, 0, HasVerticalAlignment.ALIGN_MIDDLE);
        quotationRequestStepPanel.getCellFormatter().getElement(qrpRow, 0).getStyle().setProperty("height", "10em");

        return new Step() {

            {
                quoteRequestForm.addValueChangeHandler(new ValueChangeHandler<TenantSureCoverageDTO>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<TenantSureCoverageDTO> event) {
                        setQuote(null);
                        pleaseFillOutTheFormMessage.setVisible(!quoteRequestForm.isValid());
                        quoteRequestForm.revalidate();
                        if (quoteRequestForm.getValidationResults().isValid()) {
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
                quoteRequestForm.setVisited(false);
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
                quoteRequestForm.revalidate();
                if (quoteRequestForm.isValid()) {
                    callback.onSuccess(null);
                } else {
                    MessageDialog.info(i18n.tr("Please fill out the form to continue"));
                }
            }

            @Override
            public void setNextButton(Button next) {
                TenantSurePurchaseViewImpl.this.acceptQuoteButton = next;
                TenantSurePurchaseViewImpl.this.acceptQuoteButton.setEnabled(false);
            }

        };

    }

    private Step makePaymentStep() {
        int row = -1;
        paymentStepPanel = new TwoColumnFlexFormPanel();
        paymentStepQuoteViewer = new TenantSureQuoteViewer(true);
        paymentStepPanel.setH1(++row, 0, 2, i18n.tr("Quote"));
        paymentStepPanel.setWidget(++row, 0, 2, paymentStepQuoteViewer);

        paymentStepPanel.setH1(++row, 0, 2, i18n.tr("Payment"));
        paymentMethodForm = new TenantSurePaymentMethodForm(new Command() {
            @Override
            public void execute() {
                presenter.onBillingAddressSameAsCurrentSelected();
            }
        });
        paymentMethodForm.initContent();
        paymentStepPanel.setWidget(++row, 0, 2, paymentMethodForm);

        processingPaymentMessage = new Label();
        processingPaymentMessage.addStyleName(TenantSureTheme.StyleName.TSPucrhaseViewMessageText.name());
        processingPaymentMessage.setText(i18n.tr("Processing payment..."));
        paymentStepPanel.setWidget(++row, 0, 2, processingPaymentMessage);

        paymentProcessingErrorMessage = new Label();
        paymentProcessingErrorMessage.addStyleName(TenantSureTheme.StyleName.TSPucrhaseViewMessageText.name());
        paymentProcessingErrorMessage.addStyleName(TenantSureTheme.StyleName.TSPurchaseViewError.name());

        paymentProcessingErrorMessage.setText("");
        paymentStepPanel.setWidget(++row, 0, 2, paymentProcessingErrorMessage);

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
                paymentStepQuoteViewer.setValue(null);
            }

            @Override
            public void onProceedToNext(final AsyncCallback<VoidSerializable> callback) {
                paymentMethodForm.revalidate();
                paymentMethodForm.setUnconditionalValidationErrorRendering(true);

                if (paymentMethodForm.isAgreedToPreauthorizedPayments()) {
                    // this delay validaion was made to wait until CreditCardEditor to checks visa debit asyncroneously on server side
                    paymentFormValidationHack(callback, 0);
                } else {
                    MessageDialog.info(i18n.tr("You must accept the Pre-Authorized Payments Agreement in order to proceed!"));
                }

            }

            @Override
            public String getTitle() {
                return i18n.tr("Accept quote and proceed to payment");
            }

            @Override
            public void setNextButton(Button next) {
                next.setEnabled(true);
            }
        };
    }

    private Step makePaymentSucceededStep() {
        final FlowPanel finishStepPanel = new FlowPanel();
        // TODO move styling to theme
        finishStepPanel.getElement().getStyle().setProperty("display", "table-cell");
        finishStepPanel.getElement().getStyle().setProperty("verticalAlign", "middle");
        finishStepPanel.getElement().getStyle().setProperty("textAlign", "center");
        finishStepPanel.getElement().getStyle().setHeight(10., Unit.EM);

        Label label = new Label();
        label.addStyleName(TenantSureTheme.StyleName.TSPucrhaseViewMessageText.name());
        label.setText(i18n.tr("Payment Processed Successfully: an email with your insurance policy has been sent to your email."));
        finishStepPanel.add(label);

        Anchor returnToInsuranceManagement = new Anchor(i18n.tr("Return to Tenant Insurance"), new Command() {
            @Override
            public void execute() {
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
                // NOT APPLICABLE
            }

            @Override
            public String getTitle() {
                return i18n.tr("Buy TenantSure");
            }

            @Override
            public void setNextButton(Button next) {
                // NOT APPLICABLE
            }
        };
    }

    private void paymentFormValidationHack(final AsyncCallback<VoidSerializable> callback, final int iterNumber) {
        if (iterNumber == 10) {
            MessageDialog.info(i18n.tr("You must fill out the form and accept the Pre-Authorized Payments Agreement in order to proceed!"));
        } else {
            new Timer() {
                @Override
                public void run() {
                    if (paymentMethodForm.isValid()) {
                        // save the callback to use it's 'onSuccess()' when we get acknowledgment
                        // from the presenter that we've processed the payment and have bound the quote successfully
                        TenantSurePurchaseViewImpl.this.paymentSucceededCallback = callback;
                        presenter.onQuoteAccepted();
                    } else {
                        paymentMethodForm.revalidate();
                        paymentMethodForm.setUnconditionalValidationErrorRendering(true);

                        paymentFormValidationHack(callback, iterNumber + 1);
                    }
                }
            }.schedule(iterNumber * 200);
        }
    }

}
