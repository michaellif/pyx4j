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
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import java.math.BigDecimal;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.shared.ISignature;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.CSignature;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.common.client.theme.VistaTheme.StyleName;
import com.propertyvista.common.client.ui.MiscUtils;
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
    public IsWidget createStepContent() {
        FormPanel formPanel = new FormPanel(getWizard());

        paymentDetailsHeader = formPanel.h3(i18n.tr("Payment Details"));
        formPanel.append(Location.Left, paymentDetailsHolder);
        formPanel.append(Location.Left, inject(proto().payment().amount(), new CMoneyLabel()));
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
            public BasicValidationError isValid() {
                return (getCComponent().getValue() == null || !getCComponent().getValue().agree().getValue(false) ? new BasicValidationError(getCComponent(),
                        i18n.tr("Please agree to all applicable Terms and Conditions and our Privacy Policy in order to submit your payment.")) : null);
            }
        });
        cSignature.setDecorator(new SignatureDecorator());

        formPanel.append(Location.Left, proto().payment().convenienceFeeSignedTerm().signature(), cSignature);

        formPanel.br();

        formPanel.h3(i18n.tr("Terms and Conditions"));
        formPanel.append(Location.Left, proto().confirmationTerms(), new ConfirmationTermsFolder(getView()));

        return formPanel;
    }

    @Override
    public void onStepVizible(boolean flag) {
        super.onStepVizible(flag);

        if (flag) {
            get(proto().payment().amount()).setValue(calculatePaymentAmount());

            paymentDetailsHolder.clear();
            paymentDetailsHeader.setVisible(false);
            get(proto().payment().convenienceFeeSignedTerm().signature()).setVisible(false);

            if (!get(proto().payment().amount()).getValue().equals(BigDecimal.ZERO)) {
                paymentDetailsHeader.setVisible(true);
                paymentDetailsHolder.setWidget(createPaymentDetailsPanel());
            }
        }
    }

    private Widget createPaymentDetailsPanel() {
        final VerticalPanel panel = new VerticalPanel();

        if (!get(proto().payment().paymentMethod()).isValueEmpty()) {
            panel.add(createDecorator(i18n.tr("Payment Method:"), get(proto().payment().paymentMethod()).getValue().getStringView()));
            panel.add(createDecorator(i18n.tr("Amount to pay:"), ((CLabel<?>) get(proto().payment().amount())).getFormattedValue()));

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

                        get(proto().payment().convenienceFeeSignedTerm().signature()).setVisible(true);

                        getValue().payment().convenienceFee().setValue(result.feeAmount().getValue());
                        getValue().payment().convenienceFeeReferenceNumber().setValue(result.transactionNumber().getValue());
                    }
                }
            }, inData);
        } else {
            if (get(proto().payment().paymentMethod()).isValueEmpty()) {
                Label noPaymentAcceptLabel = new Label(i18n.tr("Can not accept payment at this time - you will be contacted by the office"));
                noPaymentAcceptLabel.setStyleName(StyleName.WarningMessage.name());

                panel.add(createDecorator(i18n.tr("Amount to pay:"), ((CLabel<?>) get(proto().payment().amount())).getFormattedValue()));
                panel.add(noPaymentAcceptLabel);
            }
        }

        MiscUtils.setPanelSpacing(panel, 8);
        return panel;
    }

    private BigDecimal calculatePaymentAmount() {
        BigDecimal amount = BigDecimal.ZERO;

        if (SecurityController.check(PortalProspectBehavior.Applicant)) {
            for (Deposit deposit : getValue().leaseChargesData().deposits()) {
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
