/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms;

import java.math.BigDecimal;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CViewer;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.common.client.theme.BillingTheme;
import com.propertyvista.portal.client.themes.TenantSureTheme;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.components.MoneyComboBox;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSurePaymentDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSurePaymentItemDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSurePaymentItemTaxDTO;

public class TenantSurePaymentViewer extends CViewer<TenantSurePaymentDTO> {

    private static final I18n i18n = I18n.get(TenantSurePaymentViewer.class);

    private final NumberFormat currencyFormat;

    private final DateTimeFormat dateFormat;

    public TenantSurePaymentViewer(NumberFormat currencyFormat, DateTimeFormat dateFormat) {
        this.currencyFormat = currencyFormat;
        this.dateFormat = dateFormat;
    }

    public TenantSurePaymentViewer() {
        this(MoneyComboBox.CANADIAN_CURRENCY_DETAILED_FORMAT, DateTimeFormat.getFormat(CDatePicker.defaultDateFormat));
    }

    @Override
    public IsWidget createContent(TenantSurePaymentDTO payment) {
        FlowPanel contentPanel = new FlowPanel();
        if (payment != null) {
            int row = -1;
            FormFlexPanel paymentBreakdownPanel = new FormFlexPanel();
            for (TenantSurePaymentItemDTO paymentItem : payment.paymentBreakdown()) {

                addDetailRecord(paymentBreakdownPanel, ++row, paymentItem.description().getValue(), paymentItem.amount().getValue());
                for (TenantSurePaymentItemTaxDTO tax : paymentItem.taxBreakdown()) {
                    addDetailRecord(paymentBreakdownPanel, ++row, tax.tax().getValue(), tax.amount().getValue());
                }
            }
            if (!payment.total().isNull()) {
                addTotalRecord(paymentBreakdownPanel, ++row, payment.total().getMeta().getCaption(), payment.total().getValue());
            }
            contentPanel.add(paymentBreakdownPanel);

            if (!payment.paymentDate().isNull()) {
                Label nextPaymentDateLabel = new Label();
                nextPaymentDateLabel.getElement().getStyle().setWidth(100, Unit.PCT);
                nextPaymentDateLabel.getElement().getStyle().setTextAlign(TextAlign.CENTER);
                nextPaymentDateLabel.setText(i18n.tr("Next Payment Date: {0}", dateFormat.format(payment.paymentDate().getValue())));
                contentPanel.add(nextPaymentDateLabel);
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
        table.getFlexCellFormatter().addStyleName(row, 2, TenantSureTheme.StyleName.TSPaymentAmount.name());
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
