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
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodForm;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSureQuotationRequestForm;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSureQuoteViewer;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuotationRequestDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuotationRequestParamsDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteDTO;

public class TenantSurePurchaseViewImpl extends Composite implements TenantSurePurchaseView {

    public static enum Styles implements IStyleName {
        TSPurchaseViewSection, TSPurchaseViewBuyInsuranceButton, TSPurchaseViewCancelButton, TSPucrhaseViewMessageText, TSPurchaseViewError;
    }

    private static final I18n i18n = I18n.get(TenantSurePurchaseViewImpl.class);

    private final FormFlexPanel viewPanel;

    private Presenter presenter;

    private final Button buyInsuranceButton;

    private final Label retrievingQuoteMessage;

    private final Label processingPaymentMessage;

    private final Label paymentProcessingErrorMessage;

    private final Label pleaseFillOutTheFormMessage;

    private final Anchor cancelButton;

    private final TenantSureQuotationRequestForm quotationRequestForm;

    private final TenantSureQuoteViewer quoteViewer;

    private final PaymentMethodForm paymentMethodForm;

    public TenantSurePurchaseViewImpl() {
        viewPanel = new FormFlexPanel();

        int row = -1;

        viewPanel.setH1(++row, 0, 1, i18n.tr("Coverage"));
        quotationRequestForm = new TenantSureQuotationRequestForm();
        quotationRequestForm.initContent();
        quotationRequestForm.asWidget().addStyleName(Styles.TSPurchaseViewSection.name());
        quotationRequestForm.addValueChangeHandler(new ValueChangeHandler<TenantSureQuotationRequestDTO>() {
            @Override
            public void onValueChange(ValueChangeEvent<TenantSureQuotationRequestDTO> event) {
                setQuote(null);
                pleaseFillOutTheFormMessage.setVisible(!quotationRequestForm.isValid());
                quotationRequestForm.revalidate();
                if (quotationRequestForm.getValidationResults().isValid()) {
                    presenter.onCoverageRequestChanged();
                }
            }
        });
        viewPanel.setWidget(++row, 0, quotationRequestForm);

        viewPanel.setH1(++row, 0, 1, i18n.tr("Quote"));
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

        viewPanel.setWidget(++row, 0, quoteSection);
        viewPanel.getCellFormatter().setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_MIDDLE);
        viewPanel.getCellFormatter().getElement(row, 0).getStyle().setProperty("height", "10em");

        viewPanel.setH1(++row, 0, 1, i18n.tr("Payment"));
        paymentMethodForm = new PaymentMethodForm() {
            @Override
            public List<PaymentType> getPaymentOptions() {
                return Arrays.asList(PaymentType.CreditCard);
            }
        };
        paymentMethodForm.initContent();
//      paymentMethodForm.asWidget().addStyleName(Styles.TSPurchaseViewSection.name());
        viewPanel.setWidget(++row, 0, paymentMethodForm);

        processingPaymentMessage = new Label();
        processingPaymentMessage.addStyleName(Styles.TSPucrhaseViewMessageText.name());
        processingPaymentMessage.setText(i18n.tr("Processing payment..."));
        viewPanel.setWidget(++row, 0, processingPaymentMessage);

        paymentProcessingErrorMessage = new Label();
        paymentProcessingErrorMessage.addStyleName(Styles.TSPucrhaseViewMessageText.name());
        paymentProcessingErrorMessage.addStyleName(Styles.TSPurchaseViewError.name());

        paymentProcessingErrorMessage.setText("");
        viewPanel.setWidget(++row, 0, paymentProcessingErrorMessage);

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
        buyInsuranceButton = new Button(i18n.tr("Buy TenantSure"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                paymentMethodForm.revalidate();
                if (paymentMethodForm.isValid()) {
                    presenter.onQuoteAccepted();
                } else {
                    MessageDialog.info(i18n.tr("You need to enter all the required fields and accept pre-authorized payment to proceed"));
                }
            }
        });
        buyInsuranceButton.addStyleName(Styles.TSPurchaseViewBuyInsuranceButton.name());
        buttonsPanel.add(buyInsuranceButton);

        viewPanel.setWidget(++row, 0, buttonsPanel);

        SimplePanel container = new SimplePanel(viewPanel);
        initWidget(container);
    }

    @Override
    public void init(TenantSureQuotationRequestParamsDTO quotationRequestParams, PaymentMethod paymentMethod) {
        // quote request params section
        quotationRequestForm.setCoverageParams(quotationRequestParams);

        // quote section
        pleaseFillOutTheFormMessage.setVisible(true);
        quoteViewer.setValue(null);
        quoteViewer.setVisible(false);

        retrievingQuoteMessage.setVisible(false);

        // payment section        
        paymentMethodForm.populate(paymentMethod);

        processingPaymentMessage.setVisible(false);
        paymentProcessingErrorMessage.setVisible(false);

        buyInsuranceButton.setEnabled(false);
    }

    @Override
    public void setQuote(TenantSureQuoteDTO quote) {
        quoteViewer.setValue(quote);

        retrievingQuoteMessage.setVisible(false);
        boolean canAcceptQuote = quote != null && !quote.isNull();

        quoteViewer.setVisible(canAcceptQuote);

        buyInsuranceButton.setEnabled(canAcceptQuote);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public TenantSureQuotationRequestDTO getCoverageRequest() {
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

}
