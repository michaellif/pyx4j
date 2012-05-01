/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 26, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.invoice;

import java.math.BigDecimal;
import java.util.List;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.client.CEntityViewer;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.dto.TransactionHistoryDTO;

public class TransactionHistoryViewer extends CEntityViewer<TransactionHistoryDTO> {

    private final static I18n i18n = I18n.get(TransactionHistoryViewer.class);

    @Override
    public IsWidget createContent(TransactionHistoryDTO value) {

        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        if (value != null) {
            content.setWidget(++row, 0, createLineItems(value.lineItems()));
            content.setWidget(++row, 0, createArrears(value.agingBuckets()));
        }
        return content;
    }

    private IsWidget createLineItems(List<InvoiceLineItem> items) {
        FormFlexPanel lineItemsView = new FormFlexPanel();
        lineItemsView.setCellPadding(10);
        lineItemsView.setCellSpacing(10);
        // TODO add style
        int row = -1;

        lineItemsView.setH1(++row, 0, 5, i18n.tr("Transactions History"));
        ++row;

        int headerColumn = -1;
        final int COL_DATE = ++headerColumn;
        lineItemsView.setWidget(row, COL_DATE, new HTML(new SafeHtmlBuilder().appendEscaped(i18n.tr("Date")).toSafeHtml()));
        lineItemsView.getFlexCellFormatter().setWidth(row, COL_DATE, "10em");
        final int COL_ITEM = ++headerColumn;
        lineItemsView.setWidget(row, COL_ITEM, new HTML(new SafeHtmlBuilder().appendEscaped(i18n.tr("Item")).toSafeHtml()));
        lineItemsView.getFlexCellFormatter().setWidth(row, COL_ITEM, "20em");
        final int COL_DEBIT = ++headerColumn;
        lineItemsView.setWidget(row, COL_DEBIT, new HTML(i18n.tr("Debit")));
        lineItemsView.getFlexCellFormatter().setWidth(row, COL_DEBIT, "10em");
        final int COL_CREDIT = ++headerColumn;
        lineItemsView.setWidget(row, COL_CREDIT, new HTML(i18n.tr("Credit")));
        lineItemsView.getFlexCellFormatter().setWidth(row, COL_CREDIT, "10em");
        final int COL_BALANCE = ++headerColumn;
        lineItemsView.setWidget(row, COL_BALANCE, new HTML(i18n.tr("Balance")));
        lineItemsView.getFlexCellFormatter().setWidth(row, COL_BALANCE, "10em");

        BigDecimal balance = new BigDecimal("0.0");

        for (InvoiceLineItem item : items) {
            ++row;

            int colAmount = -1;
            String amountRepresentation = "error";
            if (item.isInstanceOf(InvoiceDebit.class)) {
                colAmount = COL_DEBIT;
                amountRepresentation = item.amount().getValue().toString();
                balance = balance.add(item.amount().getValue());
            } else if (item.isInstanceOf(InvoiceCredit.class)) {
                colAmount = COL_CREDIT;
                amountRepresentation = "(" + item.amount().getValue().toString() + ")";
                balance = balance.subtract(item.amount().getValue());
            } else if (ApplicationMode.isDevelopment()) {
                throw new Error("Unknown line item class " + item.getInstanceValueClass().getName());
            }
            String balanceRespresentation = balance.toString();

            lineItemsView.setHTML(row, COL_DATE, toSafeHtml(DateTimeFormat.getFormat(CDatePicker.defaultDateFormat).format(item.postDate().getValue())));
            lineItemsView.setHTML(row, COL_ITEM, toSafeHtml(item.description().getValue()));
            lineItemsView.setHTML(row, colAmount, toSafeHtml(amountRepresentation));
            lineItemsView.setHTML(row, COL_BALANCE, toSafeHtml(balanceRespresentation));
        }

        return lineItemsView;
    }

    private Widget createArrears(IList<AgingBuckets> agingBuckets) {
        FormFlexPanel arrearsView = new FormFlexPanel();
        int row = -1;
        arrearsView.setH1(++row, 0, 5, i18n.tr("Arrears"));

        ++row;
        AgingBuckets proto = EntityFactory.getEntityPrototype(AgingBuckets.class);
        arrearsView.setHTML(row, 0, toSafeHtml(proto.label().getMeta().getCaption()));
        arrearsView.setHTML(row, 1, toSafeHtml(proto.current().getMeta().getCaption()));
        arrearsView.setHTML(row, 2, toSafeHtml(proto.bucket30().getMeta().getCaption()));
        arrearsView.setHTML(row, 3, toSafeHtml(proto.bucket60().getMeta().getCaption()));
        arrearsView.setHTML(row, 4, toSafeHtml(proto.bucket90().getMeta().getCaption()));

        for (AgingBuckets arrears : agingBuckets) {
            ++row;
            arrearsView.setHTML(row, 0, toSafeHtml(arrears.label().getValue()));
            arrearsView.setHTML(row, 1, toSafeHtml(arrears.current().getValue().toString()));
            arrearsView.setHTML(row, 2, toSafeHtml(arrears.bucket30().getValue().toString()));
            arrearsView.setHTML(row, 3, toSafeHtml(arrears.bucket60().getValue().toString()));
            arrearsView.setHTML(row, 4, toSafeHtml(arrears.bucket90().getValue().toString()));
        }
        return arrearsView;
    }

    private static SafeHtml toSafeHtml(String str) {
        return new SafeHtmlBuilder().appendEscaped(str).toSafeHtml();
    }
}