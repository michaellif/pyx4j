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
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
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

    public TenantSureQuoteViewer(NumberFormat currencyFormat) {
        this.currencyFormat = currencyFormat;
    }

    public TenantSureQuoteViewer() {
        this(NumberFormat.getFormat(i18n.tr("$#,##0.00")));
    }

    @Override
    public IsWidget createContent(TenantSureQuoteDTO quote) {
        FormFlexPanel contentPanel = new FormFlexPanel();
        if (quote != null) {
            if (quote.specialQuote().isNull()) {
                int row = 0;

                addDetailRecord(contentPanel, ++row, quote.grossPremium().getMeta().getCaption(), quote.grossPremium().getValue());
                addDetailRecord(contentPanel, ++row, quote.underwriterFee().getMeta().getCaption(), quote.underwriterFee().getValue());

                for (InsuranceTenantSureTax tax : quote.taxBreakdown()) {
                    addDetailRecord(contentPanel, ++row, tax.description().getValue(), tax.absoluteAmount().getValue());
                }

                addTotalRecord(contentPanel, ++row, quote.totalMonthlyPayable().getMeta().getCaption(), quote.totalMonthlyPayable().getValue());
            } else {
                Label specialQuoteText = new Label();
                specialQuoteText.getElement().getStyle().setTextAlign(TextAlign.CENTER);
                specialQuoteText.getElement().getStyle().setFontWeight(FontWeight.BOLD);
                specialQuoteText.setText(quote.specialQuote().getValue());

                contentPanel.setWidget(0, 0, specialQuoteText);
                contentPanel.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
                contentPanel.getCellFormatter().getElement(0, 0).getStyle().setProperty("height", "10em");
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