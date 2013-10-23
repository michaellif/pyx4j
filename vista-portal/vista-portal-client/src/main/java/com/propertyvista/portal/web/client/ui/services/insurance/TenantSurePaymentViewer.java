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
package com.propertyvista.portal.web.client.ui.services.insurance;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CViewer;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.common.client.ui.components.tenantinsurance.MoneyComboBox;
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
        BasicFlexFormPanel contentPanel = new BasicFlexFormPanel();
        int outerRow = -1;

        if (payment != null && !payment.isNull()) {
            int innerRow = -1;
            BasicFlexFormPanel paymentBreakdownPanel = new BasicFlexFormPanel();
            for (TenantSurePaymentItemDTO paymentItem : payment.paymentBreakdown()) {

                new ViewerRecordWriter(currencyFormat).addDetailRecord(paymentBreakdownPanel, ++innerRow, paymentItem.description().getValue(), paymentItem
                        .amount().getValue());
                for (TenantSurePaymentItemTaxDTO tax : paymentItem.taxBreakdown()) {
                    new ViewerRecordWriter(currencyFormat).addDetailRecord(paymentBreakdownPanel, ++innerRow, tax.tax().getValue(), tax.amount().getValue());
                }
            }
            if (!payment.total().isNull()) {
                new ViewerRecordWriter(currencyFormat).addTotalRecord(paymentBreakdownPanel, ++innerRow, payment.total().getMeta().getCaption(), payment.total()
                        .getValue());
            }
            contentPanel.setWidget(++outerRow, 0, paymentBreakdownPanel);

            if (!payment.paymentDate().isNull()) {
                Label nextPaymentDateLabel = new Label();
                nextPaymentDateLabel.setText(i18n.tr("Payment Date: {0}", dateFormat.format(payment.paymentDate().getValue())));

                contentPanel.setWidget(++outerRow, 0, new HTML("&nbsp;"));
                contentPanel.setWidget(++outerRow, 0, nextPaymentDateLabel);
            }
        } else {
            Label noneLabel = new Label();
            noneLabel.getElement().getStyle().setWidth(100, Unit.PCT);
            noneLabel.getElement().getStyle().setTextAlign(TextAlign.CENTER);
            noneLabel.setText(i18n.tr("None"));
            contentPanel.setWidget(0, 0, noneLabel);

        }

        return contentPanel;
    }
}
