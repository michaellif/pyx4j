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
package com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms;

import java.math.BigDecimal;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityViewer;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.common.client.theme.BillingTheme;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureTax;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteDTO;

public class TenantSureQuoteViewer extends CEntityViewer<TenantSureQuoteDTO> {

    private static final I18n i18n = I18n.get(TenantSureQuoteViewer.class);

    private final NumberFormat currencyFormat;

    private final boolean underwirterFeeAsFootnote;

    public TenantSureQuoteViewer(NumberFormat currencyFormat, boolean underwriterFeeAsFootnote) {
        this.currencyFormat = currencyFormat;
        this.underwirterFeeAsFootnote = underwriterFeeAsFootnote;
    }

    public TenantSureQuoteViewer(boolean underwriterFeeAsFootnote) {
        this(NumberFormat.getFormat(i18n.tr("CAD #,##0.00")), underwriterFeeAsFootnote);
    }

    @Override
    public IsWidget createContent(TenantSureQuoteDTO quote) {
        FlowPanel contentPanel = new FlowPanel();
        FormFlexPanel paymentBreakdownPanel = new FormFlexPanel();
        if (quote != null) {
            if (quote.specialQuote().isNull()) {
                int row = 0;
                addDetailRecord(paymentBreakdownPanel, ++row, quote.grossPremium().getMeta().getCaption(), quote.grossPremium().getValue());
                for (InsuranceTenantSureTax tax : quote.grossPremiumTaxBreakdown()) {
                    addDetailRecord(paymentBreakdownPanel, ++row, tax.description().getValue(), tax.absoluteAmount().getValue());
                }
                if (!underwirterFeeAsFootnote) {
                    addDetailRecord(paymentBreakdownPanel, ++row, i18n.tr("Underwriter Fee"), quote.underwriterFee().getValue());
                    // TODO underwriter fee taxes (don't forget to add to total as well)
                    addTotalRecord(paymentBreakdownPanel, ++row, i18n.tr("Total"), quote.totalPayable().getValue().add(quote.underwriterFee().getValue()));
                } else {
                    addTotalRecord(paymentBreakdownPanel, ++row, i18n.tr("Total"), quote.totalPayable().getValue());
                }

                contentPanel.add(paymentBreakdownPanel);

                if (!quote.underwriterFee().isNull() & underwirterFeeAsFootnote) {
                    Label underwriterFeeLabel = new Label();
                    underwriterFeeLabel.setStyleName(BillingTheme.StyleName.BillingDetailItem.name());
                    underwriterFeeLabel.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
                    underwriterFeeLabel.getElement().getStyle().setMarginTop(1, Unit.EM);
                    underwriterFeeLabel.setText(i18n.tr("*A one time underwriter fee of {0} will be charged upon enrollment.",
                            currencyFormat.format(quote.underwriterFee().getValue())));
                    // TODO underwriter fee tax
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
        table.setHTML(row, 1, description);
        table.setHTML(row, 2, currencyFormat.format(amount));
        // styling:
        table.getRowFormatter().setStyleName(row, BillingTheme.StyleName.BillingDetailItem.name());
        table.getFlexCellFormatter().setStyleName(row, 0, BillingTheme.StyleName.BillingDetailItemDate.name());
        table.getFlexCellFormatter().setStyleName(row, 1, BillingTheme.StyleName.BillingDetailItemTitle.name());
        table.getFlexCellFormatter().setStyleName(row, 2, BillingTheme.StyleName.BillingDetailItemAmount.name());
    }

    private void addTotalRecord(FlexTable table, int row, String description, BigDecimal amount) {
        table.setHTML(row, 1, description);
        table.setHTML(row, 2, currencyFormat.format(amount));
        // styling:
        table.getRowFormatter().setStyleName(row, BillingTheme.StyleName.BillingDetailTotal.name());
        table.getFlexCellFormatter().setStyleName(row, 1, BillingTheme.StyleName.BillingDetailTotalTitle.name());
        table.getFlexCellFormatter().setStyleName(row, 2, BillingTheme.StyleName.BillingDetailTotalAmount.name());
    }

}