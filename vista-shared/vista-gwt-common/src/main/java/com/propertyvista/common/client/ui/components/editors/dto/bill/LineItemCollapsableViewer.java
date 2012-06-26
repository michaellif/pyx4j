/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors.dto.bill;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.CEntityCollapsableViewer;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.theme.BillingTheme;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.financial.billing.InvoiceSubLineItem;
import com.propertyvista.dto.InvoiceLineItemGroupDTO;

public class LineItemCollapsableViewer extends CEntityCollapsableViewer<InvoiceLineItemGroupDTO> {

    public LineItemCollapsableViewer() {
        super(VistaImages.INSTANCE);
        setWidth("46em");
    }

    @Override
    public IsWidget createCollapsedContent(InvoiceLineItemGroupDTO value) {
        FlexTable content = new FlexTable();

        content.getColumnFormatter().setWidth(0, "80%"); //overrides FlexTable's width auto-management for cells
        content.getColumnFormatter().setWidth(2, "20%");

        int row = 0;
        if (value != null && !value.total().isNull()) {
            content.getFlexCellFormatter().setColSpan(row, 0, 2);
            content.setWidget(row, 0, new HTML(value.getMeta().getCaption()));
            content.setWidget(row, 2, new HTML(value.total().getStringView()));
            // styling:
            content.getRowFormatter().setStyleName(row, BillingTheme.StyleName.BillingLineItem.name());
            content.getFlexCellFormatter().setStyleName(row, 0, BillingTheme.StyleName.BillingLineItemTitle.name());
            content.getFlexCellFormatter().setStyleName(row, 2, BillingTheme.StyleName.BillingLineItemAmount.name());
        }

        content.setWidth("100%");
        return content;
    }

    @Override
    public IsWidget createExpandedContent(InvoiceLineItemGroupDTO value) {
        FlexTable content = new FlexTable();

        content.getColumnFormatter().setWidth(0, "40%"); //overrides FlexTable's width auto-management for cells
        content.getColumnFormatter().setWidth(1, "40%");
        content.getColumnFormatter().setWidth(2, "20%");

        int row = 0;
        if (value != null && !value.total().isNull()) {
            content.setWidget(row, 0, new HTML(value.getMeta().getCaption()));
            // styling:
            content.getRowFormatter().setStyleName(row, BillingTheme.StyleName.BillingLineItem.name());
            content.getFlexCellFormatter().setStyleName(row, 0, BillingTheme.StyleName.BillingLineItemTitle.name());

            row++;
            if (value.lineItems() != null && value.lineItems().size() > 0) {
                for (InvoiceLineItem item : value.lineItems()) {
                    if (item instanceof InvoiceDebit) {
                        if (item instanceof InvoiceProductCharge) {
                            InvoiceProductCharge productCharge = (InvoiceProductCharge) item;
                            InvoiceSubLineItem charge = productCharge.chargeSubLineItem();
                            addDetailRecord(content, row++, ((InvoiceProductCharge) item).dueDate().getStringView(), charge.description().getValue(), charge
                                    .amount().getStringView());
                            for (InvoiceSubLineItem adjustment : productCharge.adjustmentSubLineItems()) {
                                addDetailRecord(content, row++, "", adjustment.description().getValue(), adjustment.amount().getStringView());
                            }
                            for (InvoiceSubLineItem concession : productCharge.adjustmentSubLineItems()) {
                                addDetailRecord(content, row++, "", concession.description().getValue(), concession.amount().getStringView());
                            }
                        } else {
                            addDetailRecord(content, row++, ((InvoiceDebit) item).dueDate().getStringView(), item.description().getValue(), item.amount()
                                    .getStringView());
                        }
                    } else if (item instanceof InvoiceCredit) {
                        addDetailRecord(content, row++, ((InvoiceCredit) item).postDate().getStringView(), item.description().getValue(), item.amount()
                                .getStringView());
                    }
                }
            }
            addTotalRecord(content, row++, value.getMeta().getCaption(), value.total().getStringView());
        }

        content.setWidth("100%");
        return content;
    }

    private void addDetailRecord(FlexTable table, int row, String date, String description, String amount) {
        table.setHTML(row, 0, date);
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
