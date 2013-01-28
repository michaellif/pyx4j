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
package com.propertyvista.crm.client.ui.crud.lease.invoice;

import java.math.BigDecimal;
import java.util.List;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CEntityViewer;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.dto.LeaseYardiFinancialInfoDTO;

public class LeaseYardiFinancialInfoViewer extends CEntityViewer<LeaseYardiFinancialInfoDTO> {

    private static final I18n i18n = I18n.get(I18n.class);

    @Override
    public IsWidget createContent(LeaseYardiFinancialInfoDTO value) {
        FormFlexPanel contentPanel = new FormFlexPanel();
        int[] row = { -1 };
        contentPanel.setH1(++row[0], 0, 3, i18n.tr("Outstanding Charges"));
        createLineItems(row, contentPanel, value.charges());

        contentPanel.setH1(++row[0], 0, 3, i18n.tr("Unapplied Payments"));
        createLineItems(row, contentPanel, value.payments());

        return contentPanel;
    }

    private static <E extends InvoiceLineItem> void createLineItems(int[] row, FormFlexPanel panel, List<E> items) {
        // this code should be very defensive because you never know the quality of information that is coming from Yardi
        if (!items.isEmpty()) {
            int COL_DATE = 0;
            int COL_DESCRIPTION = 1;
            int COL_AMOUNT = 2;

            ++row[0];
            panel.setWidget(row[0], COL_DATE, new HTML(i18n.tr("Date")));
            panel.setWidget(row[0], COL_DESCRIPTION, new HTML(i18n.tr("Description")));
            panel.setWidget(row[0], COL_AMOUNT, new HTML(i18n.tr("Amount")));

            panel.getRowFormatter().setStyleName(row[0], CrmTheme.TransactionHistoryStyleName.TransactionsHistoryColumnTitle.name());
            panel.getCellFormatter().setStyleName(row[0], COL_AMOUNT, CrmTheme.TransactionHistoryStyleName.TransactionsHistoryMoneyColumnTitle.name());

            DateTimeFormat dateFormat = DateTimeFormat.getFormat(CDatePicker.defaultDateFormat);
            NumberFormat currencyFormat = TransactionHistoryViewer.NUMBER_FORMAT;

            BigDecimal totalAmount = new BigDecimal("0.00");

            for (E item : items) {
                LogicalDate date = item.postDate().getValue();
                HTML dateHtml = date != null ? new HTML(dateFormat.format(date)) : new HTML("&nbsp;");

                String description = !item.description().isNull() ? item.description().getValue() : "";
                HTML descriptionHtml = new HTML(htmlEscape(description));

                BigDecimal amount = item.amount().getValue();
                HTML amountHtml = amount != null ? new HTML(htmlEscape(currencyFormat.format(amount))) : new HTML("&nbsp;");

                ++row[0];
                panel.setWidget(row[0], COL_DATE, dateHtml);
                panel.setWidget(row[0], COL_DESCRIPTION, descriptionHtml);
                panel.setWidget(row[0], COL_AMOUNT, amountHtml);

                panel.getRowFormatter().setStyleName(
                        row[0],
                        row[0] % 2 == 0 ? CrmTheme.TransactionHistoryStyleName.TransactionRecordEven.name()
                                : CrmTheme.TransactionHistoryStyleName.TransactionRecordOdd.name());
                panel.getCellFormatter().setStyleName(row[0], COL_AMOUNT, CrmTheme.TransactionHistoryStyleName.TransactionRecordMoneyCell.name());

                if (amount != null) {
                    totalAmount = totalAmount.add(amount);
                }
            }

            HTML totalDescription = new HTML(htmlEscape(i18n.tr("Total")));
            HTML totalHtml = new HTML(htmlEscape(currencyFormat.format(totalAmount)));

            ++row[0];
            panel.setWidget(row[0], COL_DESCRIPTION, totalDescription);
            panel.setWidget(row[0], COL_AMOUNT, totalHtml);
            panel.getRowFormatter().getElement(row[0]).getStyle().setFontWeight(FontWeight.BOLD);
            panel.getCellFormatter().setStyleName(row[0], COL_AMOUNT, CrmTheme.TransactionHistoryStyleName.TransactionRecordMoneyCell.name());

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
