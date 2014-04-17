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

import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.common.client.theme.BillingTheme;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.dto.BillDTO;
import com.propertyvista.dto.InvoiceLineItemGroupDTO;

public class BillForm extends CEntityForm<BillDTO> {

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
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = -1;
        int col = oneColumn ? 0 : 1;
        int span = oneColumn ? 1 : 2;

        if (!justPreviewBill) {
            content.setH1(++row, 0, span, i18n.tr("Info"));
            content.setWidget(++row, 0, inject(proto().billingAccount().lease().unit(), new CEntityLabel<AptUnit>(), new FieldDecoratorBuilder().build()));
            content.setWidget(++row, 0, inject(proto().billingCycle().building(), new CEntityLabel<Building>(), new FieldDecoratorBuilder().build()));
            content.setWidget(++row, 0, inject(proto().billingCycle().billingType().billingPeriod(), new CEnumLabel(), new FieldDecoratorBuilder().build()));

            int row2 = oneColumn ? row : 0;
            content.setWidget(++row2, col, inject(proto().billingCycle().billingCycleStartDate(), new CDateLabel(), new FieldDecoratorBuilder().build()));
            content.setWidget(++row2, col, inject(proto().billingCycle().billingCycleEndDate(), new CDateLabel(), new FieldDecoratorBuilder().build()));
            content.setWidget(++row2, col, inject(proto().billingCycle().targetBillExecutionDate(), new CDateLabel(), new FieldDecoratorBuilder().build()));
            row = oneColumn ? row2 : row;

            content.setH1(++row, 0, span, i18n.tr("Status"));
            content.setWidget(++row, 0, inject(proto().billSequenceNumber(), new CNumberLabel(), new FieldDecoratorBuilder().build()));
            content.setWidget(++row, 0, inject(proto().dueDate(), new CDateLabel(), new FieldDecoratorBuilder().build()));
            content.setWidget(++row, 0, inject(proto().rejectReason(), new CEnumLabel(), new FieldDecoratorBuilder().build()));

            row2 = oneColumn ? row : row2 + 1;
            content.setWidget(++row2, col, inject(proto().billType(), new CEnumLabel(), new FieldDecoratorBuilder().build()));
            content.setWidget(++row2, col, inject(proto().billStatus(), new CEnumLabel(), new FieldDecoratorBuilder().build()));
            row = oneColumn ? row2 : row;
        }

        if (!justPreviewBill) {
            content.setH1(++row, 0, span, i18n.tr("Previous Bill"));
            content.setWidget(++row, 0,
                    inject(proto().balanceForwardAmount(), new CMoneyLabel(), new FieldDecoratorBuilder().componentAlignment(Alignment.right).build()));
            content.setWidget(++row, 0,
                    inject(proto().carryForwardCredit(), new CMoneyLabel(), new FieldDecoratorBuilder().componentAlignment(Alignment.right).build()));

            content.setWidget(++row, 0, inject(proto().depositRefundLineItems(), new LineItemCollapsibleViewer()));
            content.setWidget(++row, 0, inject(proto().immediateAccountAdjustmentLineItems(), new LineItemCollapsibleViewer()));
            content.setWidget(++row, 0, inject(proto().previousChargeRefundLineItems(), new LineItemCollapsibleViewer()));
            content.setWidget(++row, 0, inject(proto().nsfChargeLineItems(), new LineItemCollapsibleViewer()));
            content.setWidget(++row, 0, inject(proto().withdrawalLineItems(), new LineItemCollapsibleViewer()));
            content.setWidget(++row, 0, inject(proto().rejectedPaymentLineItems(), new LineItemCollapsibleViewer()));
            content.setWidget(++row, 0, inject(proto().paymentLineItems(), new LineItemCollapsibleViewer()));

            Widget lastBillTotal = inject(proto().pastDueAmount(), new CMoneyLabel(), new FieldDecoratorBuilder().componentAlignment(Alignment.right).build())
                    .asWidget();
            lastBillTotal.addStyleName(BillingTheme.StyleName.BillingBillTotal.name());
            content.setWidget(++row, 0, lastBillTotal);
        }

        content.setH1(++row, 0, span, i18n.tr("Current Bill"));
        if (justPreviewBill) {
            content.setWidget(++row, 0,
                    inject(proto().pastDueAmount(), new CMoneyLabel(), new FieldDecoratorBuilder().componentAlignment(Alignment.right).build()));
        }
        content.setWidget(++row, 0, inject(proto().serviceChargeLineItems(), new LineItemCollapsibleViewer()));
        content.setWidget(++row, 0, inject(proto().recurringFeatureChargeLineItems(), new LineItemCollapsibleViewer()));
        content.setWidget(++row, 0, inject(proto().onetimeFeatureChargeLineItems(), new LineItemCollapsibleViewer()));
        content.setWidget(++row, 0, inject(proto().pendingAccountAdjustmentLineItems(), new LineItemCollapsibleViewer()));
        content.setWidget(++row, 0,
                inject(proto().latePaymentFees(), new CMoneyLabel(), new FieldDecoratorBuilder().componentAlignment(Alignment.right).build()));
        content.setWidget(++row, 0, inject(proto().depositLineItems(), new LineItemCollapsibleViewer()));
        content.setWidget(++row, 0,
                inject(proto().productCreditAmount(), new CMoneyLabel(), new FieldDecoratorBuilder().componentAlignment(Alignment.right).build()));

        content.setWidget(++row, 0, inject(proto().currentAmount(), new CMoneyLabel(), new FieldDecoratorBuilder().componentAlignment(Alignment.right).build()));
        content.setWidget(++row, 0, inject(proto().taxes(), new CMoneyLabel(), new FieldDecoratorBuilder().componentAlignment(Alignment.right).build()));

        // Dues:
        Widget grandTotal = inject(proto().totalDueAmount(), new CMoneyLabel(), new FieldDecoratorBuilder().componentAlignment(Alignment.right).build())
                .asWidget();
        grandTotal.addStyleName(BillingTheme.StyleName.BillingBillTotal.name());
        content.setWidget(++row, 0, grandTotal);

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
        } else {
            get(proto().pastDueAmount()).setVisible(
                    !getValue().pastDueAmount().isNull() && getValue().pastDueAmount().getValue().compareTo(BigDecimal.ZERO) != 0);
        }

        hideLines(getValue().serviceChargeLineItems(), proto().serviceChargeLineItems());
        hideLines(getValue().recurringFeatureChargeLineItems(), proto().recurringFeatureChargeLineItems());
        hideLines(getValue().onetimeFeatureChargeLineItems(), proto().onetimeFeatureChargeLineItems());
        hideLines(getValue().pendingAccountAdjustmentLineItems(), proto().pendingAccountAdjustmentLineItems());
        hideLines(getValue().depositLineItems(), proto().depositLineItems());
    }

    private void hideLines(InvoiceLineItemGroupDTO value, InvoiceLineItemGroupDTO member) {
        get(member).setVisible(!value.lineItems().isEmpty());
    }
}