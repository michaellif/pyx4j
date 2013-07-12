/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-28
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CViewer;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.common.client.theme.TransactionHistoryViewerTheme;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.yardi.YardiCredit;
import com.propertyvista.domain.financial.yardi.YardiDebit;
import com.propertyvista.domain.financial.yardi.YardiPayment;
import com.propertyvista.dto.TransactionHistoryDTO;

public class TransactionHistoryViewerYardi extends CViewer<TransactionHistoryDTO> {

    private static final I18n i18n = I18n.get(I18n.class);

    private final NumberFormat chargeFormat;

    private final NumberFormat paymentFormat;

    private final int COLUMNS_NUMBER = 4;

    public TransactionHistoryViewerYardi(String chargeFormat, String paymentFormat) {
        this.chargeFormat = NumberFormat.getFormat(chargeFormat);
        this.paymentFormat = NumberFormat.getFormat(paymentFormat);
    }

    public TransactionHistoryViewerYardi(String moneyFormat) {
        this(moneyFormat, moneyFormat);
    }

    public TransactionHistoryViewerYardi() {
        // use CR suffix for negative charges and inverse sign for payments shown in Payments sections
        this(i18n.tr("$#,##0.00;# CR"), i18n.tr("-$#,##0.00;$#,##0.00;"));
    }

    @Override
    public IsWidget createContent(TransactionHistoryDTO value) {
        FormFlexPanel contentPanel = new FormFlexPanel();
        if (value != null) {
            List<YardiPayment> unappliedPayments = new ArrayList<YardiPayment>();
            List<YardiDebit> outstangingCharges = new ArrayList<YardiDebit>();
            List<YardiCredit> accountCredits = new ArrayList<YardiCredit>();

            for (InvoiceLineItem invoiceLineItem : value.lineItems()) {
                if (invoiceLineItem.isInstanceOf(YardiPayment.class)) {
                    unappliedPayments.add((YardiPayment) invoiceLineItem);
                } else if (invoiceLineItem.isInstanceOf(YardiDebit.class)) {
                    outstangingCharges.add((YardiDebit) invoiceLineItem);
                } else if (invoiceLineItem.isInstanceOf(YardiCredit.class)) {
                    accountCredits.add((YardiCredit) invoiceLineItem);
                }
            }

            int row = -1;
            contentPanel.setH1(++row, 0, 2, i18n.tr("Outstanding Charges"));
            contentPanel.setWidget(++row, 0, 2, renderLineItems(outstangingCharges, chargeFormat, i18n.tr("Due Date")));

            contentPanel.setH1(++row, 0, COLUMNS_NUMBER, i18n.tr("Account Credits"));
            contentPanel.setWidget(++row, 0, 2, renderLineItems(accountCredits, paymentFormat, null));

            contentPanel.setH1(++row, 0, COLUMNS_NUMBER, i18n.tr("Unapplied Payments"));
            contentPanel.setWidget(++row, 0, 2, renderLineItems(unappliedPayments, paymentFormat, null));
        }
        return contentPanel;
    }

    private <E extends InvoiceLineItem> FlexTable renderLineItems(List<E> items, NumberFormat format, String dateHdr) {
        // this code should be very defensive because you never know the quality of information that is coming from Yardi
        int row = -1;
        FlexTable panel = new FlexTable();
        panel.getElement().getStyle().setWidth(100, Unit.PCT);
        panel.getColumnFormatter().setWidth(0, "25%");
        panel.getColumnFormatter().setWidth(1, "25%");
        panel.getColumnFormatter().setWidth(2, "25%");
        panel.getColumnFormatter().setWidth(3, "25%");

        if (!items.isEmpty()) {
            int COL_DATE = 0;
            int COL_TYPE = 1;
            int COL_DESCRIPTION = 2;
            int COL_AMOUNT = 3;

            ++row;
            panel.setWidget(row, COL_DATE, new HTML(dateHdr == null ? i18n.tr("Post Date") : dateHdr));
            panel.setWidget(row, COL_TYPE, new HTML(i18n.tr("AR Code Type")));
            panel.setWidget(row, COL_DESCRIPTION, new HTML(i18n.tr("Description")));
            panel.setWidget(row, COL_AMOUNT, new HTML(i18n.tr("Amount")));

            panel.getRowFormatter().setStyleName(row, TransactionHistoryViewerTheme.StyleName.FinancialTransactionHeaderRow.name());
            panel.getCellFormatter().addStyleName(row, COL_TYPE, TransactionHistoryViewerTheme.StyleName.FinancialTransactionDataColumn.name());
            panel.getCellFormatter().addStyleName(row, COL_AMOUNT, TransactionHistoryViewerTheme.StyleName.FinancialTransactionMoneyColumn.name());

            DateTimeFormat dateFormat = DateTimeFormat.getFormat(CDatePicker.defaultDateFormat);

            BigDecimal totalAmount = new BigDecimal("0.00");

            for (E item : items) {
                LogicalDate date = item.isInstanceOf(YardiDebit.class) ? ((YardiDebit) item).dueDate().getValue() : item.postDate().getValue();
                HTML dateHtml = date != null ? new HTML(dateFormat.format(date)) : new HTML("&nbsp;");

                String description = !item.description().isNull() ? item.description().getValue() : "";
                HTML descriptionHtml = new HTML(htmlEscape(description));

                BigDecimal amount = item.amount().getValue();
                HTML amountHtml = amount != null ? new HTML(htmlEscape(format.format(amount))) : new HTML("&nbsp;");

                HTML typeHtml = new HTML(htmlEscape(item.arCode().type().getValue().toString()));

                ++row;
                panel.setWidget(row, COL_DATE, dateHtml);
                panel.setWidget(row, COL_TYPE, typeHtml);
                panel.setWidget(row, COL_DESCRIPTION, descriptionHtml);
                panel.setWidget(row, COL_AMOUNT, amountHtml);

                panel.getCellFormatter().addStyleName(row, COL_AMOUNT, TransactionHistoryViewerTheme.StyleName.FinancialTransactionRow.name());
                panel.getRowFormatter().addStyleName(//@formatter:off
                        row, 
                        row % 2 == 0 ? 
                                TransactionHistoryViewerTheme.StyleName.FinancialTransactionEvenRow.name() : 
                                TransactionHistoryViewerTheme.StyleName.FinancialTransactionOddRow.name()
                );//@formatter:on
                panel.getCellFormatter().addStyleName(row, COL_DATE, TransactionHistoryViewerTheme.StyleName.FinancialTransactionDataColumn.name());
                panel.getCellFormatter().addStyleName(row, COL_TYPE, TransactionHistoryViewerTheme.StyleName.FinancialTransactionDataColumn.name());
                panel.getCellFormatter().addStyleName(row, COL_DESCRIPTION, TransactionHistoryViewerTheme.StyleName.FinancialTransactionDataColumn.name());
                panel.getCellFormatter().addStyleName(row, COL_AMOUNT, TransactionHistoryViewerTheme.StyleName.FinancialTransactionMoneyCell.name());
                panel.getCellFormatter().addStyleName(row, COL_AMOUNT, TransactionHistoryViewerTheme.StyleName.FinancialTransactionMoneyColumn.name());

                if (amount != null) {
                    totalAmount = totalAmount.add(amount);
                }
            }

            HTML totalDescription = new HTML(htmlEscape(i18n.tr("Total")));
            HTML totalHtml = new HTML(htmlEscape(format.format(totalAmount)));

            ++row;
            panel.setWidget(row, COL_DESCRIPTION, totalDescription);
            panel.setWidget(row, COL_AMOUNT, totalHtml);
            panel.getRowFormatter().addStyleName(row, TransactionHistoryViewerTheme.StyleName.FinancialTransactionRow.name());
            panel.getRowFormatter().addStyleName(row, TransactionHistoryViewerTheme.StyleName.FinancialTransactionTotalRow.name());
            panel.getCellFormatter().addStyleName(row, COL_DESCRIPTION, TransactionHistoryViewerTheme.StyleName.FinancialTransactionDataColumn.name());
            panel.getCellFormatter().addStyleName(row, COL_AMOUNT, TransactionHistoryViewerTheme.StyleName.FinancialTransactionMoneyColumn.name());
            panel.getCellFormatter().addStyleName(row, COL_AMOUNT, TransactionHistoryViewerTheme.StyleName.FinancialTransactionMoneyCell.name());

        } else {
            Label noItems = new Label();
            noItems.setText(i18n.tr("None"));
            ++row;
            panel.setWidget(row, 0, noItems);
            panel.getFlexCellFormatter().setColSpan(row, 0, 4);
            panel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        }

        return panel;
    }

    private static SafeHtml htmlEscape(String s) {
        return new SafeHtmlBuilder().appendEscaped(s).toSafeHtml();
    }
}
