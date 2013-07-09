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

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.theme.BillingTheme;
import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
import com.propertyvista.dto.BillDTO;
import com.propertyvista.dto.InvoiceLineItemGroupDTO;

public class BillForm extends CEntityDecoratableForm<BillDTO> {

    private static final I18n i18n = I18n.get(BillForm.class);

    private final FormFlexPanel billPanel = new FormFlexPanel();

    private final boolean justPreviewBill;

    public BillForm() {
        this(false);
    }

    public BillForm(boolean justCurrentBill) {
        super(BillDTO.class);
        this.justPreviewBill = justCurrentBill;

        setEditable(false);
        setViewable(true);
    }

    @Override
    public IsWidget createContent() {

        // form top panel:
        FormFlexPanel infoPanel = new FormFlexPanel();
        int row = -1;

        if (!justPreviewBill) {
            infoPanel.setH1(++row, 0, 2, i18n.tr("Info"));
            infoPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingAccount().lease().unit())).build());
            infoPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingCycle().building())).build());
            infoPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingCycle().billingCycleStartDate())).build());
            infoPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingCycle().billingCycleEndDate())).build());
            infoPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingCycle().billingType().billingPeriod())).build());
            infoPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingCycle().targetBillExecutionDate())).build());

            infoPanel.setH1(++row, 0, 2, i18n.tr("Status"));
            infoPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billSequenceNumber())).build());
            infoPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billType())).build());
            infoPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().dueDate())).build());
            infoPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billStatus())).build());
            infoPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().rejectReason())).build());
        }

        // form bills panel:
        row = -1;
        if (!justPreviewBill) {
            billPanel.setH1(++row, 0, 2, i18n.tr("Last Bill"));
            billPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().balanceForwardAmount(), new CMoneyField())).build());
            billPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().carryForwardCredit(), new CMoneyField())).build());
            billPanel.setWidget(++row, 0, inject(proto().depositRefundLineItems(), new LineItemCollapsibleViewer()));
            billPanel.setWidget(++row, 0, inject(proto().immediateAccountAdjustmentLineItems(), new LineItemCollapsibleViewer()));
            billPanel.setWidget(++row, 0, inject(proto().previousChargeRefundLineItems(), new LineItemCollapsibleViewer()));
            billPanel.setWidget(++row, 0, inject(proto().nsfChargeLineItems(), new LineItemCollapsibleViewer()));
            billPanel.setWidget(++row, 0, inject(proto().withdrawalLineItems(), new LineItemCollapsibleViewer()));
            billPanel.setWidget(++row, 0, inject(proto().rejectedPaymentLineItems(), new LineItemCollapsibleViewer()));
            billPanel.setWidget(++row, 0, inject(proto().paymentLineItems(), new LineItemCollapsibleViewer()));

//            billPanel.setHR(++row, 0, 1);
            Widget lastBillTotal = new DecoratorBuilder(inject(proto().pastDueAmount(), new CMoneyField())).build();
            lastBillTotal.addStyleName(BillingTheme.StyleName.BillingBillTotal.name());
            billPanel.setWidget(++row, 0, lastBillTotal);
        }

        billPanel.setH1(++row, 0, 2, i18n.tr("Current Bill"));
        billPanel.setWidget(++row, 0, inject(proto().serviceChargeLineItems(), new LineItemCollapsibleViewer()));
        billPanel.setWidget(++row, 0, inject(proto().recurringFeatureChargeLineItems(), new LineItemCollapsibleViewer()));
        billPanel.setWidget(++row, 0, inject(proto().onetimeFeatureChargeLineItems(), new LineItemCollapsibleViewer()));
        billPanel.setWidget(++row, 0, inject(proto().pendingAccountAdjustmentLineItems(), new LineItemCollapsibleViewer()));
        billPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().latePaymentFees(), new CMoneyField())).build());
        billPanel.setWidget(++row, 0, inject(proto().depositLineItems(), new LineItemCollapsibleViewer()));
        billPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().productCreditAmount(), new CMoneyField())).build());

        billPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().currentAmount(), new CMoneyField())).build());
        billPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().taxes(), new CMoneyField())).build());

        // Dues:
//        billPanel.setHR(++row, 0, 1);
        Widget grandTotal = new DecoratorBuilder(inject(proto().totalDueAmount(), new CMoneyField())).build();
        grandTotal.addStyleName(BillingTheme.StyleName.BillingBillTotal.name());
        billPanel.setWidget(++row, 0, grandTotal);

        billPanel.getColumnFormatter().setWidth(0, VistaTheme.columnWidth);

        // form main panel:
        FormFlexPanel main = new FormFlexPanel();
        main.setWidget(0, 0, infoPanel);
        main.setWidget(1, 0, billPanel);

        return main;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

//        billPanel.setVisible(getValue().billStatus().getValue() != BillStatus.Failed);

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
        }

        hideLines(getValue().serviceChargeLineItems(), proto().serviceChargeLineItems());
        hideLines(getValue().recurringFeatureChargeLineItems(), proto().recurringFeatureChargeLineItems());
        hideLines(getValue().onetimeFeatureChargeLineItems(), proto().onetimeFeatureChargeLineItems());
        hideLines(getValue().pendingAccountAdjustmentLineItems(), proto().pendingAccountAdjustmentLineItems());
        hideLines(getValue().depositLineItems(), proto().depositLineItems());
    }

    protected class DecoratorBuilder extends WidgetDecorator.Builder { //builder specifically for this form (as it uses mixed formats)

        public DecoratorBuilder(CComponent<?> component) {
            super(component);
            componentAlignment(Alignment.right);
            labelAlignment(Alignment.left);
            useLabelSemicolon(false);
            componentWidth(30);
        }
    }

    private void hideLines(InvoiceLineItemGroupDTO line, InvoiceLineItemGroupDTO line2) {
        get(line2).setVisible(!line.lineItems().isEmpty());
    }
}