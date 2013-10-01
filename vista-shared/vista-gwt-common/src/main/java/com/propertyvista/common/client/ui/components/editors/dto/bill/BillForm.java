/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors.dto.bill;

import java.math.BigDecimal;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.theme.BillingTheme;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.dto.BillDTO;
import com.propertyvista.dto.InvoiceLineItemGroupDTO;

public class BillForm extends CEntityDecoratableForm<BillDTO> {

    private static final I18n i18n = I18n.get(BillForm.class);

    private final boolean justPreviewBill;

    private final boolean oneColumn;

    public BillForm() {
        this(false);
    }

    public BillForm(boolean justCurrentBill) {
        this(justCurrentBill, false);
    }

    public BillForm(boolean justCurrentBill, boolean oneColumn) {
        super(BillDTO.class);
        this.justPreviewBill = justCurrentBill;
        this.oneColumn = oneColumn;

        setEditable(false);
        setViewable(true);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = -1;
        int col = oneColumn ? 0 : 1;
        if (!justPreviewBill) {
            content.setH1(++row, 0, 2, i18n.tr("Info"));
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().billingAccount().lease().unit(), new CEntityLabel<AptUnit>())).build());
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().billingCycle().billingCycleStartDate())).build());
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().billingCycle().billingCycleEndDate())).build());

            int row2 = oneColumn ? row : 0;
            content.setWidget(++row2, col, new FormDecoratorBuilder(inject(proto().billingCycle().building(), new CEntityLabel<Building>())).build());
            content.setWidget(++row2, col, new FormDecoratorBuilder(inject(proto().billingCycle().billingType().billingPeriod())).build());
            content.setWidget(++row2, col, new FormDecoratorBuilder(inject(proto().billingCycle().targetBillExecutionDate())).build());
            row = oneColumn ? row2 : row;

            content.setH1(++row, 0, 2, i18n.tr("Status"));
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().billSequenceNumber())).build());
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().dueDate())).build());
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().rejectReason())).build());

            row2 = oneColumn ? row : row2 + 1;
            content.setWidget(++row2, col, new FormDecoratorBuilder(inject(proto().billType())).build());
            content.setWidget(++row2, col, new FormDecoratorBuilder(inject(proto().billStatus())).build());
            row = oneColumn ? row2 : row;
        }

        if (!justPreviewBill) {
            content.setH1(++row, 0, 2, i18n.tr("Last Bill"));
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().balanceForwardAmount(), new CMoneyField())).build());
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().carryForwardCredit(), new CMoneyField())).build());
            content.setWidget(++row, 0, inject(proto().depositRefundLineItems(), new LineItemCollapsibleViewer()));
            content.setWidget(++row, 0, inject(proto().immediateAccountAdjustmentLineItems(), new LineItemCollapsibleViewer()));
            content.setWidget(++row, 0, inject(proto().previousChargeRefundLineItems(), new LineItemCollapsibleViewer()));
            content.setWidget(++row, 0, inject(proto().nsfChargeLineItems(), new LineItemCollapsibleViewer()));
            content.setWidget(++row, 0, inject(proto().withdrawalLineItems(), new LineItemCollapsibleViewer()));
            content.setWidget(++row, 0, inject(proto().rejectedPaymentLineItems(), new LineItemCollapsibleViewer()));
            content.setWidget(++row, 0, inject(proto().paymentLineItems(), new LineItemCollapsibleViewer()));

            Widget lastBillTotal = new FormDecoratorBuilder(inject(proto().pastDueAmount(), new CMoneyField()), "20em", "8em", "8em").build();
            lastBillTotal.addStyleName(BillingTheme.StyleName.BillingBillTotal.name());
            content.setWidget(++row, 0, 2, lastBillTotal);
        }

        content.setH1(++row, 0, 2, i18n.tr("Current Bill"));
        content.setWidget(++row, 0, inject(proto().serviceChargeLineItems(), new LineItemCollapsibleViewer()));
        content.setWidget(++row, 0, inject(proto().recurringFeatureChargeLineItems(), new LineItemCollapsibleViewer()));
        content.setWidget(++row, 0, inject(proto().onetimeFeatureChargeLineItems(), new LineItemCollapsibleViewer()));
        content.setWidget(++row, 0, inject(proto().pendingAccountAdjustmentLineItems(), new LineItemCollapsibleViewer()));
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().latePaymentFees(), new CMoneyField())).build());
        content.setWidget(++row, 0, inject(proto().depositLineItems(), new LineItemCollapsibleViewer()));
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().productCreditAmount(), new CMoneyField())).build());

        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().currentAmount(), new CMoneyField()), "20em", "8em", "8em").build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().taxes(), new CMoneyField()), "20em", "8em", "8em").build());

        // Dues:
        Widget grandTotal = new FormDecoratorBuilder(inject(proto().totalDueAmount(), new CMoneyField()), "20em", "8em", "8em").build();
        grandTotal.addStyleName(BillingTheme.StyleName.BillingBillTotal.name());
        content.setWidget(++row, 0, 2, grandTotal);

        return content;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (!justPreviewBill) {
            get(proto().rejectReason()).setVisible(getValue().billStatus().getValue() == BillStatus.Rejected);
            get(proto().productCreditAmount()).setVisible(
                    !getValue().productCreditAmount().isNull() && getValue().productCreditAmount().getValue().compareTo(BigDecimal.ZERO) != 0);
            get(proto().latePaymentFees()).setVisible(
                    !getValue().latePaymentFees().isNull() && getValue().latePaymentFees().getValue().compareTo(BigDecimal.ZERO) != 0);
            get(proto().carryForwardCredit()).setVisible(
                    !getValue().carryForwardCredit().isNull() && getValue().carryForwardCredit().getValue().compareTo(BigDecimal.ZERO) != 0);

            hideLines(getValue().depositRefundLineItems(), proto().depositRefundLineItems());
            hideLines(getValue().immediateAccountAdjustmentLineItems(), proto().immediateAccountAdjustmentLineItems());
            hideLines(getValue().previousChargeRefundLineItems(), proto().previousChargeRefundLineItems());
            hideLines(getValue().nsfChargeLineItems(), proto().nsfChargeLineItems());
            hideLines(getValue().withdrawalLineItems(), proto().withdrawalLineItems());
            hideLines(getValue().rejectedPaymentLineItems(), proto().rejectedPaymentLineItems());
            hideLines(getValue().paymentLineItems(), proto().paymentLineItems());
        }

        hideLines(getValue().serviceChargeLineItems(), proto().serviceChargeLineItems());
        hideLines(getValue().recurringFeatureChargeLineItems(), proto().recurringFeatureChargeLineItems());
        hideLines(getValue().onetimeFeatureChargeLineItems(), proto().onetimeFeatureChargeLineItems());
        hideLines(getValue().pendingAccountAdjustmentLineItems(), proto().pendingAccountAdjustmentLineItems());
        hideLines(getValue().depositLineItems(), proto().depositLineItems());
    }

    private void hideLines(InvoiceLineItemGroupDTO line, InvoiceLineItemGroupDTO line2) {
        get(line2).setVisible(!line.lineItems().isEmpty());
    }
}