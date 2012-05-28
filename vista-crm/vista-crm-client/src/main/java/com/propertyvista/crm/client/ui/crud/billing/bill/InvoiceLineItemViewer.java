/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 15, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.billing.bill;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.client.CEntityViewer;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.common.client.theme.BillingTheme;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.financial.billing.InvoiceSubLineItem;
import com.propertyvista.dto.InvoiceLineItemGroupDTO;

public class InvoiceLineItemViewer extends CEntityViewer<InvoiceLineItemGroupDTO> {
    private final Anchor buttonExpand = new Anchor("");

    private final HTML itemTotal = new HTML();

    private final FlexTable details = new FlexTable();

    private boolean expanded = false;

    @Override
    public IsWidget createContent(InvoiceLineItemGroupDTO value) {
        VerticalPanel content = new VerticalPanel();
        content.setWidth("100%");
        content.getElement().getStyle().setProperty("paddingLeft", "20px");
        if (value != null && !value.total().isNull()) {
            // entry
            HorizontalPanel entry = new HorizontalPanel();
            entry.setHeight("28px");
            entry.setWidth("100%");
            entry.setStyleName(BillingTheme.StyleName.BillingLineItem.name());
            HTML caption = new HTML(value.getMeta().getCaption());
            caption.getElement().getStyle().setProperty("fontSize", "14px");
            buttonExpand.getElement().getStyle().setProperty("paddingLeft", "20px");
            entry.add(caption);
            entry.add(buttonExpand);
            buttonExpand.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    switchState();
                }
            });
            // total
            itemTotal.setText(value.total().getStringView());
            itemTotal.setStyleName(BillingTheme.StyleName.BillingLineItemAmount.name());
            entry.add(itemTotal);
            content.add(entry);
            if (value.lineItems() != null && value.lineItems().size() > 0) {
                // details
                details.setWidth("100%");
                details.getElement().getStyle().setProperty("paddingLeft", "20px");
                details.getColumnFormatter().setWidth(0, "20%");
                details.getColumnFormatter().setWidth(2, "20%");
                int row = 0;
                for (InvoiceLineItem item : value.lineItems()) {
                    if (item instanceof InvoiceDebit) {
                        if (item instanceof InvoiceProductCharge) {
                            InvoiceProductCharge productCharge = (InvoiceProductCharge) item;
                            InvoiceSubLineItem charge = productCharge.chargeSubLineItem();
                            addDetailRecord(row++, ((InvoiceProductCharge) item).dueDate().getStringView(), charge.description().getValue(), charge.amount()
                                    .getStringView());
                            for (InvoiceSubLineItem adjustment : productCharge.adjustmentSubLineItems()) {
                                addDetailRecord(row++, "", adjustment.description().getValue(), adjustment.amount().getStringView());
                            }
                            for (InvoiceSubLineItem concession : productCharge.adjustmentSubLineItems()) {
                                addDetailRecord(row++, "", concession.description().getValue(), concession.amount().getStringView());
                            }
                        } else {
                            addDetailRecord(row++, ((InvoiceDebit) item).dueDate().getStringView(), item.description().getValue(), item.amount()
                                    .getStringView());
                        }
                    } else if (item instanceof InvoiceCredit) {
                        addDetailRecord(row++, ((InvoiceCredit) item).postDate().getStringView(), item.description().getValue(), item.amount().getStringView());
                    }
                }
                addTotalRecord(row++, value.getMeta().getCaption(), value.total().getStringView());
                content.add(details);
            } else {
                buttonExpand.setVisible(false);
            }
        }
        updateState();
        return content;
    }

    private void addDetailRecord(int row, String date, String description, String amount) {
        details.setHTML(row, 0, date);
        details.setHTML(row, 1, description);
        details.setHTML(row, 2, amount);
        details.getFlexCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_RIGHT);
        details.getFlexCellFormatter().setHeight(row, 0, "20px");
    }

    private void addTotalRecord(int row, String description, String amount) {
        HTML descHtml = new HTML(description);
        descHtml.getElement().getStyle().setProperty("fontSize", "16px");
        details.setWidget(row, 1, descHtml);
        HTML amountHtml = new HTML(amount);
        amountHtml.getElement().getStyle().setProperty("borderTop", "1px solid black");
        amountHtml.getElement().getStyle().setProperty("paddingTop", "4px");
//        amountHtml.setStyleName(BillingTheme.StyleName.BillingDetailTotal.name());
        details.setWidget(row, 2, amountHtml);
        details.getFlexCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_RIGHT);
        details.getFlexCellFormatter().setHeight(row, 0, "20px");
    }

    private void switchState() {
        expanded = !expanded;
        updateState();
    }

    private void updateState() {
        buttonExpand.setText(expanded ? "Hide Details" : "Show Details");
        details.setVisible(expanded);
        itemTotal.setVisible(!expanded);
    }
}
