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
package com.propertyvista.portal.web.client.ui.financial.views.bill;

import java.math.BigDecimal;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
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

    private final boolean justCurrentBill;

    public BillForm() {
        this(false);
    }

    public BillForm(boolean justCurrentBill) {
        super(BillDTO.class);

        this.justCurrentBill = justCurrentBill;

        setEditable(false);
        inheritEditable(false);

        setViewable(true);
        inheritViewable(false);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel flexPanel = new BasicFlexFormPanel();
        int row = -1;
        int col = 0;
        if (!justCurrentBill) {
            flexPanel.setH1(++row, 0, 2, i18n.tr("Info"));
            flexPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().billingAccount().lease().unit(), new CEntityLabel<AptUnit>())).build());
            flexPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().billingCycle().billingCycleStartDate(), new CDateLabel())).build());
            flexPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().billingCycle().billingCycleEndDate(), new CDateLabel())).build());

            flexPanel.setWidget(++row, col, new FormDecoratorBuilder(inject(proto().billingCycle().building(), new CEntityLabel<Building>())).build());
            flexPanel.setWidget(++row, col, new FormDecoratorBuilder(inject(proto().billingCycle().billingType().billingPeriod(), new CEnumLabel())).build());
            flexPanel.setWidget(++row, col, new FormDecoratorBuilder(inject(proto().billingCycle().targetBillExecutionDate(), new CDateLabel())).build());

            flexPanel.setH1(++row, 0, 2, i18n.tr("Status"));
            flexPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().billSequenceNumber(), new CNumberLabel())).build());
            flexPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().dueDate(), new CDateLabel())).build());
            flexPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().rejectReason(), new CLabel<String>())).build());

            flexPanel.setWidget(++row, col, new FormDecoratorBuilder(inject(proto().billType(), new CEnumLabel())).build());
            flexPanel.setWidget(++row, col, new FormDecoratorBuilder(inject(proto().billStatus(), new CEnumLabel())).build());
        }

        if (!justCurrentBill) {
            flexPanel.setH1(++row, 0, 2, i18n.tr("Last Bill"));
            flexPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().balanceForwardAmount(), new CMoneyLabel())).build());
            flexPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().carryForwardCredit(), new CMoneyLabel())).build());
            flexPanel.setWidget(++row, 0, inject(proto().depositRefundLineItems(), new LineItemCollapsibleViewer()));
            flexPanel.setWidget(++row, 0, inject(proto().immediateAccountAdjustmentLineItems(), new LineItemCollapsibleViewer()));
            flexPanel.setWidget(++row, 0, inject(proto().previousChargeRefundLineItems(), new LineItemCollapsibleViewer()));
            flexPanel.setWidget(++row, 0, inject(proto().nsfChargeLineItems(), new LineItemCollapsibleViewer()));
            flexPanel.setWidget(++row, 0, inject(proto().withdrawalLineItems(), new LineItemCollapsibleViewer()));
            flexPanel.setWidget(++row, 0, inject(proto().rejectedPaymentLineItems(), new LineItemCollapsibleViewer()));
            flexPanel.setWidget(++row, 0, inject(proto().paymentLineItems(), new LineItemCollapsibleViewer()));

            Widget lastBillTotal = new FormDecoratorBuilder(inject(proto().pastDueAmount(), new CMoneyLabel()), "20em", "8em", "8em").build();
            lastBillTotal.addStyleName(BillingTheme.StyleName.BillingBillTotal.name());
            flexPanel.setWidget(++row, 0, 2, lastBillTotal);
        }

        flexPanel.setH1(++row, 0, 2, i18n.tr("Current Bill"));
        flexPanel.setWidget(++row, 0, inject(proto().serviceChargeLineItems(), new LineItemCollapsibleViewer()));
        flexPanel.setWidget(++row, 0, inject(proto().recurringFeatureChargeLineItems(), new LineItemCollapsibleViewer()));
        flexPanel.setWidget(++row, 0, inject(proto().onetimeFeatureChargeLineItems(), new LineItemCollapsibleViewer()));
        flexPanel.setWidget(++row, 0, inject(proto().pendingAccountAdjustmentLineItems(), new LineItemCollapsibleViewer()));
        flexPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().latePaymentFees(), new CMoneyLabel())).build());
        flexPanel.setWidget(++row, 0, inject(proto().depositLineItems(), new LineItemCollapsibleViewer()));
        flexPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().productCreditAmount(), new CMoneyLabel())).build());

        flexPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().currentAmount(), new CMoneyLabel()), "20em", "8em", "8em").build());
        flexPanel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().taxes(), new CMoneyLabel()), "20em", "8em", "8em").build());

        // Dues:
        Widget grandTotal = new FormDecoratorBuilder(inject(proto().totalDueAmount(), new CMoneyLabel()), "20em", "8em", "8em").build();
        grandTotal.addStyleName(BillingTheme.StyleName.BillingBillTotal.name());
        flexPanel.setWidget(++row, 0, 2, grandTotal);

        return flexPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

//        billPanel.setVisible(getValue().billStatus().getValue() != BillStatus.Failed);

        if (!justCurrentBill) {
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