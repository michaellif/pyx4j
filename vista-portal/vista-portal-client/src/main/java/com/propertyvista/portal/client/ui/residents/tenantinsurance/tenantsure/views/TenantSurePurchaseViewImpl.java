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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.PreAuthorizedCreditCardPaymentForm;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSureCoverageRequestForm;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSureQuoteForm;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.PreAuthorizedCreditCardPaymentDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantSureCoverageRequestDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantSureQuotationRequestParamsDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantSureQuoteDTO;

public class TenantSurePurchaseViewImpl extends Composite implements TenantSurePurchaseView {

    private static final I18n i18n = I18n.get(TenantSurePurchaseViewImpl.class);

    private final FormFlexPanel panel;

    private final TenantSureCoverageRequestForm quotationRequestForm;

    private final Button getQuoteButton;

    private Presenter presenter;

    private final TenantSureQuoteForm quoteForm;

    private final PreAuthorizedCreditCardPaymentForm preAuthorizedCreditCardPaymentForm;

    private final Button buyInsuranceButton;

    private final Label retrievingQuoteMessage;

    private final Label processingPaymentMessage;

    private final Label paymentProcessingErrorMessage;

    private final Button cancelButton;

    public TenantSurePurchaseViewImpl() {
        panel = new FormFlexPanel();

        int row = -1;

        panel.setH1(++row, 0, 1, i18n.tr("Coverage"));
        quotationRequestForm = new TenantSureCoverageRequestForm();
        quotationRequestForm.initContent();
        quotationRequestForm.addValueChangeHandler(new ValueChangeHandler<TenantSureCoverageRequestDTO>() {
            @Override
            public void onValueChange(ValueChangeEvent<TenantSureCoverageRequestDTO> event) {
                setQuote(null);
            }
        });
        panel.setWidget(++row, 0, quotationRequestForm);

        panel.setH1(++row, 0, 1, i18n.tr("Quote"));
        getQuoteButton = new Button(i18n.tr("Get Quote"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                quotationRequestForm.revalidate();
                if (quotationRequestForm.getValidationResults().isValid()) {
                    presenter.onCoverageRequestChanged();
                }
            }
        });
        panel.setWidget(++row, 0, getQuoteButton);
        quoteForm = new TenantSureQuoteForm();
        quoteForm.initContent();
        panel.setWidget(++row, 0, quoteForm);

        retrievingQuoteMessage = new Label();
        retrievingQuoteMessage.setText(i18n.tr("Please wait while we preparing your quote..."));
        panel.setWidget(++row, 0, retrievingQuoteMessage);

        panel.setH1(++row, 0, 1, i18n.tr("Credit Card Details"));
        preAuthorizedCreditCardPaymentForm = new PreAuthorizedCreditCardPaymentForm();
        preAuthorizedCreditCardPaymentForm.initContent();
        panel.setWidget(++row, 0, preAuthorizedCreditCardPaymentForm);

        FlowPanel buttonsPanel = new FlowPanel();
        buyInsuranceButton = new Button(i18n.tr("Buy Insurance"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                preAuthorizedCreditCardPaymentForm.revalidate();
                if (preAuthorizedCreditCardPaymentForm.isValid()) {
                    presenter.onQuoteAccepted();
                }
            }
        });
        buttonsPanel.add(buyInsuranceButton);

        cancelButton = new Button(i18n.tr("Cancel"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.cancel();
            }
        });
        buttonsPanel.add(cancelButton);
        panel.setWidget(++row, 0, buttonsPanel);

        processingPaymentMessage = new Label();
        processingPaymentMessage.setText(i18n.tr("Processing payment..."));
        panel.setWidget(++row, 0, processingPaymentMessage);

        paymentProcessingErrorMessage = new Label();
        paymentProcessingErrorMessage.setText("");
        panel.setWidget(++row, 0, paymentProcessingErrorMessage);

        initWidget(panel);
    }

    @Override
    public void init(TenantSureQuotationRequestParamsDTO quotationRequestParams) {
        quotationRequestForm.setCoverageParams(quotationRequestParams);

        quoteForm.setValue(null);
        quoteForm.setVisible(false);
        getQuoteButton.setVisible(true);
        retrievingQuoteMessage.setVisible(false);
        processingPaymentMessage.setVisible(false);
        paymentProcessingErrorMessage.setVisible(false);

        PreAuthorizedCreditCardPaymentDTO preAuthorizedCreditCardPaymentDTO = EntityFactory.create(PreAuthorizedCreditCardPaymentDTO.class);
        preAuthorizedCreditCardPaymentDTO.agreementLegalBlurbAndPreAuthorizationAgreeement().addAll(
                quotationRequestParams.agreementLegalBlurbAndPreAuthorizationAgreeement());
        preAuthorizedCreditCardPaymentForm.populate(preAuthorizedCreditCardPaymentDTO);
    }

    @Override
    public void setQuote(TenantSureQuoteDTO quote) {
        quoteForm.setValue(quote);

        retrievingQuoteMessage.setVisible(false);
        boolean canAcceptQuote = quote != null && !quote.isNull();

        getQuoteButton.setVisible(!canAcceptQuote);
        quoteForm.setVisible(canAcceptQuote);

        buyInsuranceButton.setEnabled(canAcceptQuote);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public TenantSureCoverageRequestDTO getCoverageRequest() {
        return quotationRequestForm.getValue();
    }

    @Override
    public TenantSureQuoteDTO getAcceptedQuote() {
        return quoteForm.getValue().isNull() ? null : quoteForm.getValue();
    }

    @Override
    public CreditCardInfo getCreditCardInfo() {
        return preAuthorizedCreditCardPaymentForm.getValue().creditCardInfo().duplicate();
    }

    @Override
    public void populatePaymentProcessingError(String errorReason) {
        paymentProcessingErrorMessage.setText(errorReason);
        paymentProcessingErrorMessage.setVisible(true);
    }

    @Override
    public void waitForQuote() {
        setQuote(null);
        retrievingQuoteMessage.setVisible(true);
        getQuoteButton.setVisible(false);
        quoteForm.setVisible(false);
    }

    @Override
    public void waitForPaymentProcessing() {
        buyInsuranceButton.setVisible(false);

        processingPaymentMessage.setVisible(true);

        paymentProcessingErrorMessage.setVisible(false);
        paymentProcessingErrorMessage.setText("");
    }

}
