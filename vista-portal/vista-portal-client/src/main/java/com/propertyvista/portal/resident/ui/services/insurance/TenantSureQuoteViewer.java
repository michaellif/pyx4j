/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-15
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.services.insurance;

import java.math.BigDecimal;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.forms.client.ui.CViewer;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.common.client.theme.BillingTheme;
import com.propertyvista.common.client.ui.components.tenantinsurance.MoneyComboBox;
import com.propertyvista.domain.tenant.insurance.TenantSurePaymentSchedule;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureQuoteDTO;

public class TenantSureQuoteViewer extends CViewer<TenantSureQuoteDTO> {

    private static final I18n i18n = I18n.get(TenantSureQuoteViewer.class);

    public TenantSureQuoteViewer(final NumberFormat currencyFormat, final boolean underwriterFeeAsFootnote) {

        setFormatter(new IFormatter<TenantSureQuoteDTO, IsWidget>() {

            @Override
            public IsWidget format(TenantSureQuoteDTO quote) {
                FlowPanel contentPanel = new FlowPanel();
                BasicFlexFormPanel paymentBreakdownPanel = new BasicFlexFormPanel();

                if (quote != null && !quote.isNull()) {
                    if (quote.specialQuote().isNull()) {
                        int row = 0;
                        paymentBreakdownPanel.setH2(++row, 0, 1, i18n.tr("Quote Number"));

                        paymentBreakdownPanel.setWidget(++row, 0, 1, new HTML(quote.quoteId().getValue()));
                        paymentBreakdownPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

                        if (quote.paymentSchedule().getValue() == TenantSurePaymentSchedule.Monthly) {
                            if (!quote.totalFirstPayable().isNull()) {
                                paymentBreakdownPanel.setH2(++row, 0, 1, i18n.tr("First Payment*"));
                                new ViewerRecordWriter(currencyFormat).addDetailRecord(paymentBreakdownPanel, ++row, "", quote.totalFirstPayable().getValue());
                            }

                            paymentBreakdownPanel.setH2(++row, 0, 1, i18n.tr("Recurring Monthly Payments"));
                            new ViewerRecordWriter(currencyFormat).addDetailRecord(paymentBreakdownPanel, ++row, "", quote.totalMonthlyPayable().getValue());

                            paymentBreakdownPanel.setH2(++row, 0, 1, i18n.tr("Total Annual Payment"));
                            new ViewerRecordWriter(currencyFormat).addDetailRecord(paymentBreakdownPanel, ++row, quote.annualPremium().getMeta().getCaption(),
                                    quote.annualPremium().getValue());
                        } else {
                            paymentBreakdownPanel.setH2(++row, 0, 1, i18n.tr("Annual Payment"));
                            new ViewerRecordWriter(currencyFormat).addDetailRecord(paymentBreakdownPanel, ++row, quote.annualPremium().getMeta().getCaption(),
                                    quote.annualPremium().getValue());
                        }

                        if (!quote.underwriterFee().isNull()) {
                            new ViewerRecordWriter(currencyFormat).addDetailRecord(paymentBreakdownPanel, ++row, i18n.tr("Underwriter Fee*"), quote
                                    .underwriterFee().getValue());
                        }
                        if (!quote.brokerFee().isNull()) {
                            new ViewerRecordWriter(currencyFormat).addDetailRecord(paymentBreakdownPanel, ++row, i18n.tr("Broker Fee*"), quote.brokerFee()
                                    .getValue());
                        }
                        if (!quote.totalAnnualTax().isNull() && quote.totalAnnualTax().getValue().compareTo(BigDecimal.ZERO) > 0) {
                            new ViewerRecordWriter(currencyFormat).addDetailRecord(paymentBreakdownPanel, ++row, i18n.tr("Tax"), quote.totalAnnualTax()
                                    .getValue());
                        }
                        new ViewerRecordWriter(currencyFormat).addTotalRecord(paymentBreakdownPanel, ++row, i18n.tr("Total"), quote.totalAnnualPayable()
                                .getValue());
                        contentPanel.add(paymentBreakdownPanel);

                        if (underwriterFeeAsFootnote) {
                            Label underwriterFeeLabel = new Label();
                            underwriterFeeLabel.setStyleName(BillingTheme.StyleName.BillingDetailItem.name());
                            underwriterFeeLabel.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
                            underwriterFeeLabel.getElement().getStyle().setMarginTop(1, Unit.EM);
                            underwriterFeeLabel.setText(i18n
                                    .tr("*A one time underwriter fee and broker fee (plus applicable taxes) will be charged upon enrollment."));
                            contentPanel.add(underwriterFeeLabel);
                        }

                    } else {
                        Label specialQuoteText = new Label();
                        specialQuoteText.getElement().getStyle().setTextAlign(TextAlign.CENTER);
                        specialQuoteText.getElement().getStyle().setFontWeight(FontWeight.BOLD);
                        specialQuoteText.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
                        specialQuoteText.setWidth("100%");
                        specialQuoteText.setText(quote.specialQuote().getValue());

                        contentPanel.add(specialQuoteText);
                    }
                }

                return contentPanel;
            }
        });
    }

    public TenantSureQuoteViewer(boolean underwriterFeeAsFootnote) {
        this(MoneyComboBox.CANADIAN_CURRENCY_DETAILED_FORMAT, underwriterFeeAsFootnote);
    }

}