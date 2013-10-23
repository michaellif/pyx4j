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
package com.propertyvista.portal.web.client.ui.services.insurance;

import java.math.BigDecimal;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CViewer;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.common.client.theme.BillingTheme;
import com.propertyvista.common.client.ui.components.tenantinsurance.MoneyComboBox;
import com.propertyvista.domain.tenant.insurance.TenantSurePaymentSchedule;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureQuoteDTO;

public class TenantSureQuoteViewer extends CViewer<TenantSureQuoteDTO> {

    private static final I18n i18n = I18n.get(TenantSureQuoteViewer.class);

    private final NumberFormat currencyFormat;

    private final boolean underwirterFeeAsFootnote;

    public TenantSureQuoteViewer(NumberFormat currencyFormat, boolean underwriterFeeAsFootnote) {
        this.currencyFormat = currencyFormat;
        this.underwirterFeeAsFootnote = underwriterFeeAsFootnote;
    }

    public TenantSureQuoteViewer(boolean underwriterFeeAsFootnote) {
        this(MoneyComboBox.CANADIAN_CURRENCY_DETAILED_FORMAT, underwriterFeeAsFootnote);
    }

    @Override
    public IsWidget createContent(TenantSureQuoteDTO quote) {
        FlowPanel contentPanel = new FlowPanel();
        BasicFlexFormPanel paymentBreakdownPanel = new BasicFlexFormPanel();
        paymentBreakdownPanel.setWidth("100%");
        if (quote != null && !quote.isNull()) {
            if (quote.specialQuote().isNull()) {
                int row = 0;
                paymentBreakdownPanel.setH2(++row, 0, 1, i18n.tr("Quote Number"));

                paymentBreakdownPanel.setWidget(++row, 0, 1, new HTML(quote.quoteId().getValue()));
                paymentBreakdownPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

                if (quote.paymentSchedule().getValue() == TenantSurePaymentSchedule.Monthly) {
                    if (!quote.totalFirstPayable().isNull()) {
                        paymentBreakdownPanel.setH2(++row, 0, 1, i18n.tr("First Payment*"));
                        addDetailRecord(paymentBreakdownPanel, ++row, "", quote.totalFirstPayable().getValue());
                    }

                    paymentBreakdownPanel.setH2(++row, 0, 1, i18n.tr("Recurring Monthly Payments"));
                    addDetailRecord(paymentBreakdownPanel, ++row, "", quote.totalMonthlyPayable().getValue());

                    paymentBreakdownPanel.setH2(++row, 0, 1, i18n.tr("Total Annual Payment"));
                    addDetailRecord(paymentBreakdownPanel, ++row, quote.annualPremium().getMeta().getCaption(), quote.annualPremium().getValue());
                } else {
                    paymentBreakdownPanel.setH2(++row, 0, 1, i18n.tr("Annual Payment"));
                    addDetailRecord(paymentBreakdownPanel, ++row, quote.annualPremium().getMeta().getCaption(), quote.annualPremium().getValue());
                }

                if (!quote.underwriterFee().isNull()) {
                    addDetailRecord(paymentBreakdownPanel, ++row, i18n.tr("Underwriter Fee*"), quote.underwriterFee().getValue());
                }
                if (!quote.totalAnnualTax().isNull() && quote.totalAnnualTax().getValue().compareTo(BigDecimal.ZERO) > 0) {
                    addDetailRecord(paymentBreakdownPanel, ++row, i18n.tr("Tax"), quote.totalAnnualTax().getValue());
                }
                addTotalRecord(paymentBreakdownPanel, ++row, i18n.tr("Total"), quote.totalAnnualPayable().getValue());
                contentPanel.add(paymentBreakdownPanel);

                if (underwirterFeeAsFootnote) {
                    Label underwriterFeeLabel = new Label();
                    underwriterFeeLabel.setStyleName(BillingTheme.StyleName.BillingDetailItem.name());
                    underwriterFeeLabel.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
                    underwriterFeeLabel.getElement().getStyle().setMarginTop(1, Unit.EM);
                    underwriterFeeLabel.setText(i18n.tr("*A one time underwriter fee (plus applicable taxes) will be charged upon enrollment."));
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

    private void addDetailRecord(FlexTable table, int row, String description, BigDecimal amount) {
        table.setHTML(row, 0, description);
        table.setHTML(row, 1, currencyFormat.format(amount));
        // styling:
        table.getRowFormatter().setStyleName(row, BillingTheme.StyleName.BillingDetailItem.name());
        table.getFlexCellFormatter().setStyleName(row, 0, BillingTheme.StyleName.BillingDetailItemTitle.name());
        table.getFlexCellFormatter().setStyleName(row, 1, BillingTheme.StyleName.BillingDetailItemAmount.name());
    }

    private void addTotalRecord(FlexTable table, int row, String description, BigDecimal amount) {
        table.setHTML(row, 0, description);
        table.setHTML(row, 1, currencyFormat.format(amount));
        // styling:
        table.getRowFormatter().setStyleName(row, BillingTheme.StyleName.BillingDetailTotal.name());
        table.getFlexCellFormatter().setStyleName(row, 0, BillingTheme.StyleName.BillingDetailTotalTitle.name());
        table.getFlexCellFormatter().setStyleName(row, 1, BillingTheme.StyleName.BillingDetailTotalAmount.name());
    }

}