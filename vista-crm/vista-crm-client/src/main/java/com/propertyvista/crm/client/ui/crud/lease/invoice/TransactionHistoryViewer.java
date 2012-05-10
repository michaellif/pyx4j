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

import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.dto.TransactionHistoryDTO;

public class TransactionHistoryViewer extends CEntityViewer<TransactionHistoryDTO> {

    private final static I18n i18n = I18n.get(TransactionHistoryViewer.class);

    @Override
    public IsWidget createContent(TransactionHistoryDTO value) {

        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        if (value != null) {
            content.setH1(++row, 0, 1, i18n.tr("Transactions History"));
            content.setWidget(++row, 0, createLineItems(value.lineItems(), value.balanceForwardAmount().getValue()));
            content.setH1(++row, 0, 1, i18n.tr("Arrears"));
            content.setWidget(++row, 0, createArrears(value.agingBuckets()));

        }
        return content;
    }

    private IsWidget createLineItems(List<InvoiceLineItem> items, BigDecimal balanceForward) {
        FormFlexPanel lineItemsView = new FormFlexPanel();
        int row = 0;

        // build header
        int headerColumn = -1;
        final int COL_DATE = ++headerColumn;
        lineItemsView.setWidget(row, COL_DATE, new HTML(new SafeHtmlBuilder().appendEscaped(i18n.tr("Date")).toSafeHtml()));
        lineItemsView.getFlexCellFormatter().setWidth(row, COL_DATE, "7em");
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

        lineItemsView.getRowFormatter().setStyleName(row, CrmTheme.TransactionHistoryStyleName.TransactionsHistoryTitle.name());
        lineItemsView.getColumnFormatter().setStyleName(COL_DATE, CrmTheme.TransactionHistoryStyleName.TansactionHistoryColumn.name());
        lineItemsView.getColumnFormatter().setStyleName(COL_ITEM, CrmTheme.TransactionHistoryStyleName.TansactionHistoryColumn.name());
        lineItemsView.getColumnFormatter().setStyleName(COL_DEBIT, CrmTheme.TransactionHistoryStyleName.TansactionHistoryColumn.name());
        lineItemsView.getColumnFormatter().setStyleName(COL_CREDIT, CrmTheme.TransactionHistoryStyleName.TansactionHistoryColumn.name());
        lineItemsView.getColumnFormatter().setStyleName(COL_BALANCE, CrmTheme.TransactionHistoryStyleName.TansactionHistoryColumn.name());

        BigDecimal balance = balanceForward != null ? balanceForward : new BigDecimal("0.0");

        ++row;

        lineItemsView.setHTML(row, COL_ITEM, toSafeHtml(i18n.tr("Balance Forward")));
        lineItemsView.setHTML(row, COL_BALANCE, toSafeHtml(balance.toString()));
        lineItemsView.getRowFormatter().setStyleName(
                row,
                row % 2 == 0 ? CrmTheme.TransactionHistoryStyleName.TransactionRecordOdd.name() : CrmTheme.TransactionHistoryStyleName.TransactionRecordEven
                        .name());
        lineItemsView.getFlexCellFormatter().setStyleName(row, COL_BALANCE, CrmTheme.TransactionHistoryStyleName.TransactionRecordMoneyCell.name());

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

            // build row
            lineItemsView.setHTML(row, COL_DATE, toSafeHtml(DateTimeFormat.getFormat(CDatePicker.defaultDateFormat).format(item.postDate().getValue())));
            lineItemsView.setHTML(row, COL_ITEM, toSafeHtml(item.description().getValue()));
            lineItemsView.setHTML(row, colAmount, toSafeHtml(amountRepresentation));
            lineItemsView.setHTML(row, COL_BALANCE, toSafeHtml(balanceRespresentation));

            // apply style
            lineItemsView.getRowFormatter().setStyleName(
                    row,
                    row % 2 == 0 ? CrmTheme.TransactionHistoryStyleName.TransactionRecordOdd.name()
                            : CrmTheme.TransactionHistoryStyleName.TransactionRecordEven.name());
            lineItemsView.getFlexCellFormatter().setStyleName(row, colAmount, CrmTheme.TransactionHistoryStyleName.TransactionRecordMoneyCell.name());
            lineItemsView.getFlexCellFormatter().setStyleName(row, colAmount, CrmTheme.TransactionHistoryStyleName.TransactionRecordMoneyCell.name());
            lineItemsView.getFlexCellFormatter().setStyleName(row, COL_BALANCE, CrmTheme.TransactionHistoryStyleName.TransactionRecordMoneyCell.name());
        }

        return lineItemsView;
    }

    private Widget createArrears(IList<AgingBuckets> agingBuckets) {
        FormFlexPanel arrearsView = new FormFlexPanel();
        int row = 0;

        AgingBuckets proto = EntityFactory.getEntityPrototype(AgingBuckets.class);
        arrearsView.setHTML(row, 0, toSafeHtml(proto.debitType().getMeta().getCaption()));
        arrearsView.setHTML(row, 1, toSafeHtml(proto.bucketCurrent().getMeta().getCaption()));
        arrearsView.setHTML(row, 2, toSafeHtml(proto.bucket30().getMeta().getCaption()));
        arrearsView.setHTML(row, 3, toSafeHtml(proto.bucket60().getMeta().getCaption()));
        arrearsView.setHTML(row, 4, toSafeHtml(proto.bucket90().getMeta().getCaption()));
        arrearsView.getRowFormatter().setStyleName(row, CrmTheme.ArrearsStyleName.ArrearsTitle.name());

        for (AgingBuckets arrears : agingBuckets) {
            ++row;
            arrearsView.setHTML(row, 0, toSafeHtml(arrears.debitType().getStringView()));
            arrearsView.setHTML(row, 1, toSafeHtml(arrears.bucketCurrent().getStringView()));
            arrearsView.setHTML(row, 2, toSafeHtml(arrears.bucket30().getStringView()));
            arrearsView.setHTML(row, 3, toSafeHtml(arrears.bucket60().getStringView()));
            arrearsView.setHTML(row, 4, toSafeHtml(arrears.bucket90().getStringView()));

            if (arrears.debitType().getValue() != DebitType.all) {
                arrearsView.getRowFormatter().setStyleName(row,
                        row % 2 == 0 ? CrmTheme.ArrearsStyleName.ArrearsCategoryEven.name() : CrmTheme.ArrearsStyleName.ArrearsCategoryOdd.name());
            } else {
                arrearsView.getRowFormatter().setStyleName(row, CrmTheme.ArrearsStyleName.ArrearsCategoryAll.name());
            }
            arrearsView.getFlexCellFormatter().setStyleName(row, 1, CrmTheme.ArrearsStyleName.ArrearsMoneyCell.name());
            arrearsView.getFlexCellFormatter().setStyleName(row, 2, CrmTheme.ArrearsStyleName.ArrearsMoneyCell.name());
            arrearsView.getFlexCellFormatter().setStyleName(row, 3, CrmTheme.ArrearsStyleName.ArrearsMoneyCell.name());
            arrearsView.getFlexCellFormatter().setStyleName(row, 4, CrmTheme.ArrearsStyleName.ArrearsMoneyCell.name());

        }
        return arrearsView;
    }

    private static SafeHtml toSafeHtml(String str) {
        return new SafeHtmlBuilder().appendEscaped(str).toSafeHtml();
    }
}