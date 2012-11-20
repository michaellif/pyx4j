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

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityViewer;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.common.client.theme.BillingTheme;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSurePremiumTaxDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteDTO;

public class TenantSureQuoteViewer extends CEntityViewer<TenantSureQuoteDTO> {

    @Override
    public IsWidget createContent(TenantSureQuoteDTO quote) {
        FormFlexPanel contentPanel = new FormFlexPanel();
        if (quote != null) {
            if (quote.specialQuote().isNull()) {
                int row = 0;
                addDetailRecord(contentPanel, ++row, quote.grossPremium().getMeta().getCaption(), quote.grossPremium().getStringView());
                addDetailRecord(contentPanel, ++row, quote.underwriterFee().getMeta().getCaption(), quote.underwriterFee().getStringView());
                for (TenantSurePremiumTaxDTO tax : quote.taxBreakdown()) {
                    addDetailRecord(contentPanel, ++row, tax.taxName().getValue(), tax.absoluteAmount().getStringView());
                }
                addTotalRecord(contentPanel, ++row, quote.totalMonthlyPayable().getMeta().getCaption(), quote.totalMonthlyPayable().getStringView());
            } else {
                Label specialQuoteText = new Label();
                specialQuoteText.getElement().getStyle();
                specialQuoteText.setText(quote.specialQuote().getValue());
                contentPanel.setWidget(0, 0, specialQuoteText);
                contentPanel.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
                contentPanel.getCellFormatter().getElement(0, 0).getStyle().setProperty("height", "10em");
            }
        }
        return contentPanel;
    }

    private void addDetailRecord(FlexTable table, int row, String description, String amount) {
        table.setHTML(row, 1, description);
        table.setHTML(row, 2, amount);
        // styling:
        table.getRowFormatter().setStyleName(row, BillingTheme.StyleName.BillingDetailItem.name());
        table.getFlexCellFormatter().setStyleName(row, 0, BillingTheme.StyleName.BillingDetailItemDate.name());
        table.getFlexCellFormatter().setStyleName(row, 1, BillingTheme.StyleName.BillingDetailItemTitle.name());
        table.getFlexCellFormatter().setStyleName(row, 2, BillingTheme.StyleName.BillingDetailItemAmount.name());
    }

    private void addTotalRecord(FlexTable table, int row, String description, String amount) {
        table.setHTML(row, 1, description);
        table.setHTML(row, 2, amount);
        // styling:
        table.getRowFormatter().setStyleName(row, BillingTheme.StyleName.BillingDetailTotal.name());
        table.getFlexCellFormatter().setStyleName(row, 1, BillingTheme.StyleName.BillingDetailTotalTitle.name());
        table.getFlexCellFormatter().setStyleName(row, 2, BillingTheme.StyleName.BillingDetailTotalAmount.name());

    }

}