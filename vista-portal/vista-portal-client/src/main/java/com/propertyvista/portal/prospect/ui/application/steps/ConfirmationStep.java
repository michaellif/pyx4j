/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 11, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import java.math.BigDecimal;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.shared.ISignature;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.CSignature;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.dto.payment.ConvenienceFeeCalculationResponseTO;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.shared.dto.PaymentConvenienceFeeDTO;
import com.propertyvista.portal.shared.ui.TermsAnchor;
import com.propertyvista.portal.shared.ui.util.decorators.SignatureDecorator;

public class ConfirmationStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(ConfirmationStep.class);

    private final SimplePanel paymentDetailsHolder = new SimplePanel();

    private Widget paymentDetailsHeader;

    public ConfirmationStep() {
        super(OnlineApplicationWizardStepMeta.Confirmation);
    }

    @Override
    public BasicFlexFormPanel createStepContent() {
        BasicFlexFormPanel content = new BasicFlexFormPanel(getStepTitle());
        int row = -1;

        content.setH3(++row, 0, 1, i18n.tr("Payment Details"));
        paymentDetailsHeader = content.getWidget(row, 0);
        content.setWidget(++row, 0, paymentDetailsHolder);
        content.setWidget(++row, 0, inject(proto().payment().amount(), new CMoneyLabel()));
        get(proto().payment().amount()).setVisible(false);

        SafeHtmlBuilder signatureDescriptionBuilder = new SafeHtmlBuilder();
        String anchorId = HTMLPanel.createUniqueId();
        signatureDescriptionBuilder
                .appendHtmlConstant(i18n
                        .tr("I agree to the Web Payment Fee being charged and have read the {0}. I further acknowledge and accept that the fee will appear as ''CCS*Web Payment Fee'' on my card/bank statement.",
                                "<span id=\"" + anchorId + "\"></span>"));

        HTMLPanel signatureDescriptionPanel = new HTMLPanel(signatureDescriptionBuilder.toSafeHtml());
        Anchor termsAnchor = new TermsAnchor(i18n.tr("Web Payment Fee Terms And Conditions"), PortalSiteMap.PortalTerms.WebPaymentFeeTerms.class);
        signatureDescriptionPanel.addAndReplaceElement(termsAnchor, anchorId);

        CSignature cSignature = new CSignature(signatureDescriptionPanel);
        cSignature.setSignatureCompletionValidator(new AbstractComponentValidator<ISignature>() {
            @Override
            public FieldValidationError isValid() {
                return (getComponent().getValue() == null || !getComponent().getValue().agree().getValue(false) ? new FieldValidationError(getComponent(), i18n
                        .tr("Please agree to all applicable Terms and Conditions and our Privacy Policy in order to submit your payment.")) : null);
            }
        });

        content.setWidget(++row, 0, new SignatureDecorator(inject(proto().payment().convenienceFeeSignature(), cSignature)));

        content.setBR(++row, 0, 1);

        content.setH3(++row, 0, 1, i18n.tr("Terms and Conditions"));
        content.setWidget(++row, 0, inject(proto().confirmationTerms(), new ConfirmationTermsFolder(getView())));

        return content;
    }

    @Override
    public void onStepVizible(boolean flag) {
        super.onStepVizible(flag);

        get(proto().payment().amount()).setValue(calculatePaymentAmount());

        paymentDetailsHolder.clear();
        paymentDetailsHeader.setVisible(false);
        get(proto().payment().convenienceFeeSignature()).setVisible(false);

        if (!get(proto().payment().amount()).getValue().equals(BigDecimal.ZERO)) {
            paymentDetailsHeader.setVisible(true);
            paymentDetailsHolder.setWidget(createPaymentDetailsPanel());
        }
    }

    private Widget createPaymentDetailsPanel() {
        final VerticalPanel panel = new VerticalPanel();

        panel.add(createDecorator(i18n.tr("Payment Method:"), get(proto().payment().paymentMethod()).getValue().getStringView()));
        panel.add(createDecorator(i18n.tr("Amount to pay:"), ((CLabel<?>) get(proto().payment().amount())).getFormattedValue()));

        get(proto().payment().convenienceFeeSignature()).setVisible(false);

        PaymentConvenienceFeeDTO inData = EntityFactory.create(PaymentConvenienceFeeDTO.class);
        inData.paymentMethod().set(getValue().payment().paymentMethod());
        inData.amount().setValue(getValue().payment().amount().getValue());
        getWizard().getPresenter().getConvenienceFee(new DefaultAsyncCallback<ConvenienceFeeCalculationResponseTO>() {
            @Override
            public void onSuccess(ConvenienceFeeCalculationResponseTO result) {
                if (result != null) {
                    panel.add(createDecorator(i18n.tr("Web Payment Fee:"), result.feeAmount().getStringView()));
                    panel.add(createDecorator(i18n.tr("Payment Total:"), result.total().getStringView()));

                    panel.add(new HTML("<br/>"));

                    get(proto().payment().convenienceFeeSignature()).setVisible(true);

                    getValue().payment().convenienceFee().setValue(result.feeAmount().getValue());
                    getValue().payment().convenienceFeeReferenceNumber().setValue(result.transactionNumber().getValue());
                }
            }
        }, inData);

        return panel;
    }

    private BigDecimal calculatePaymentAmount() {
        BigDecimal amount = BigDecimal.ZERO;

        if (SecurityController.checkBehavior(PortalProspectBehavior.Applicant)) {
            for (Deposit deposit : getValue().payment().deposits()) {
                amount = amount.add(deposit.amount().getValue());
            }
        }

        if (!getValue().payment().applicationFee().isNull()) {
            amount = amount.add(getValue().payment().applicationFee().getValue());
        }

        return amount;
    }

    private Widget createDecorator(String label, String value) {
        HorizontalPanel payee = new HorizontalPanel();
        Widget w;

        payee.add(w = new HTML(label));
        w.setWidth("12em");
        payee.add(w = new HTML(value));
        w.getElement().getStyle().setFontWeight(FontWeight.BOLD);

        return payee;
    }
}
