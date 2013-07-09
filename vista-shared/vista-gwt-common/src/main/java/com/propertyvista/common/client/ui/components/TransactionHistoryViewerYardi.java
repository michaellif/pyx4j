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

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
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

            int[] row = { -1 };

            contentPanel.setH1(++row[0], 0, COLUMNS_NUMBER, i18n.tr("Outstanding Charges"));
            renderLineItems(row, contentPanel, outstangingCharges, chargeFormat, i18n.tr("Due Date"));

            if (!accountCredits.isEmpty()) {
                contentPanel.setH1(++row[0], 0, COLUMNS_NUMBER, i18n.tr("Account Credits"));
                renderLineItems(row, contentPanel, accountCredits, paymentFormat, null);
            }

            if (!unappliedPayments.isEmpty()) {
                contentPanel.setH1(++row[0], 0, COLUMNS_NUMBER, i18n.tr("Unapplied Payments"));
                renderLineItems(row, contentPanel, unappliedPayments, paymentFormat, null);
            }
        }
        return contentPanel;
    }

    private <E extends InvoiceLineItem> void renderLineItems(int[] row, FormFlexPanel panel, List<E> items, NumberFormat format, String dateHdr) {
        // this code should be very defensive because you never know the quality of information that is coming from Yardi
        if (!items.isEmpty()) {
            int COL_DATE = 0;
            int COL_TYPE = 1;
            int COL_DESCRIPTION = 2;
            int COL_AMOUNT = 3;

            ++row[0];
            panel.setWidget(row[0], COL_DATE, new HTML(dateHdr == null ? i18n.tr("Post Date") : dateHdr));
            panel.setWidget(row[0], COL_TYPE, new HTML(i18n.tr("AR Code Type")));
            panel.setWidget(row[0], COL_DESCRIPTION, new HTML(i18n.tr("Description")));
            panel.setWidget(row[0], COL_AMOUNT, new HTML(i18n.tr("Amount")));

            panel.getRowFormatter().setStyleName(row[0], TransactionHistoryViewerTheme.StyleName.FinancialTransactionHeaderRow.name());
            panel.getCellFormatter().addStyleName(row[0], COL_AMOUNT, TransactionHistoryViewerTheme.StyleName.FinancialTransactionMoneyColumn.name());

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

                ++row[0];
                panel.setWidget(row[0], COL_DATE, dateHtml);
                panel.setWidget(row[0], COL_TYPE, typeHtml);
                panel.setWidget(row[0], COL_DESCRIPTION, descriptionHtml);
                panel.setWidget(row[0], COL_AMOUNT, amountHtml);

                panel.getCellFormatter().addStyleName(row[0], COL_AMOUNT, TransactionHistoryViewerTheme.StyleName.FinancialTransactionRow.name());
                panel.getRowFormatter().addStyleName(//@formatter:off
                        row[0], 
                        row[0] % 2 == 0 ? 
                                TransactionHistoryViewerTheme.StyleName.FinancialTransactionEvenRow.name() : 
                                TransactionHistoryViewerTheme.StyleName.FinancialTransactionOddRow.name()
                );//@formatter:on
                panel.getCellFormatter().addStyleName(row[0], COL_AMOUNT, TransactionHistoryViewerTheme.StyleName.FinancialTransactionMoneyCell.name());
                panel.getCellFormatter().addStyleName(row[0], COL_AMOUNT, TransactionHistoryViewerTheme.StyleName.FinancialTransactionMoneyColumn.name());

                if (amount != null) {
                    totalAmount = totalAmount.add(amount);
                }
            }

            HTML totalDescription = new HTML(htmlEscape(i18n.tr("Total")));
            HTML totalHtml = new HTML(htmlEscape(format.format(totalAmount)));

            ++row[0];
            panel.setWidget(row[0], COL_DESCRIPTION, totalDescription);
            panel.setWidget(row[0], COL_AMOUNT, totalHtml);
            panel.getRowFormatter().addStyleName(row[0], TransactionHistoryViewerTheme.StyleName.FinancialTransactionRow.name());
            panel.getRowFormatter().addStyleName(row[0], TransactionHistoryViewerTheme.StyleName.FinancialTransactionTotalRow.name());
            panel.getCellFormatter().addStyleName(row[0], COL_AMOUNT, TransactionHistoryViewerTheme.StyleName.FinancialTransactionMoneyColumn.name());
            panel.getCellFormatter().addStyleName(row[0], COL_AMOUNT, TransactionHistoryViewerTheme.StyleName.FinancialTransactionMoneyCell.name());

        } else {
            Label noItems = new Label();
            noItems.setText(i18n.tr("None"));
            ++row[0];
            panel.setWidget(row[0], 0, noItems);
            panel.getFlexCellFormatter().setColSpan(row[0], 0, 3);
            panel.getFlexCellFormatter().setHorizontalAlignment(row[0], 0, HasHorizontalAlignment.ALIGN_CENTER);
        }

    }

    private static SafeHtml htmlEscape(String s) {
        return new SafeHtmlBuilder().appendEscaped(s).toSafeHtml();
    }
}
