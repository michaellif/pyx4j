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

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
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
        setViewable(true);
    }

    @Override
    public IsWidget createContent() {

        // form top panel:
        FormFlexPanel top = new FormFlexPanel();
        int row = -1;

        if (!justCurrentBill) {
            top.setH1(++row, 0, 2, i18n.tr("Info"));
            top.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingAccount().lease().unit())).build());
            top.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingCycle().building())).build());
            top.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingCycle().billingCycleStartDate())).build());
            top.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingCycle().billingCycleEndDate())).build());
            top.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingCycle().billingType().paymentFrequency())).build());
            top.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billingCycle().executionTargetDate())).build());

            top.setH1(++row, 0, 2, i18n.tr("Status"));
            top.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billSequenceNumber())).build());
            top.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billType())).build());
            top.setWidget(++row, 0, new DecoratorBuilder(inject(proto().billStatus())).build());
            top.setWidget(++row, 0, new DecoratorBuilder(inject(proto().rejectReason())).build());
        }

        // form bottom panel:
        FormFlexPanel bottom = new FormFlexPanel();
        row = -1;

        if (!justCurrentBill) {
            bottom.setH1(++row, 0, 2, i18n.tr("Last Bill"));
            bottom.setWidget(++row, 0, new DecoratorBuilder(inject(proto().balanceForwardAmount())).build());
            bottom.setWidget(++row, 0, inject(proto().depositRefundLineItems(), new LineItemCollapsableViewer()));
            bottom.setWidget(++row, 0, inject(proto().immediateAccountAdjustmentLineItems(), new LineItemCollapsableViewer()));
            bottom.setWidget(++row, 0, inject(proto().nsfChargeLineItems(), new LineItemCollapsableViewer()));
            bottom.setWidget(++row, 0, inject(proto().withdrawalLineItems(), new LineItemCollapsableViewer()));
            bottom.setWidget(++row, 0, inject(proto().rejectedPaymentLineItems(), new LineItemCollapsableViewer()));
            bottom.setWidget(++row, 0, inject(proto().paymentLineItems(), new LineItemCollapsableViewer()));

            bottom.setHR(++row, 0, 2);
            bottom.setWidget(++row, 0, new DecoratorBuilder(inject(proto().pastDueAmount())).build());
        }

        bottom.setH1(++row, 0, 2, i18n.tr("Current Bill"));
        bottom.setHeight("50px");
        bottom.setWidget(++row, 0, inject(proto().serviceChargeLineItems(), new LineItemCollapsableViewer()));
        bottom.setWidget(++row, 0, inject(proto().recurringFeatureChargeLineItems(), new LineItemCollapsableViewer()));
        bottom.setWidget(++row, 0, inject(proto().onetimeFeatureChargeLineItems(), new LineItemCollapsableViewer()));
        bottom.setWidget(++row, 0, inject(proto().pendingAccountAdjustmentLineItems(), new LineItemCollapsableViewer()));
        bottom.setWidget(++row, 0, new DecoratorBuilder(inject(proto().latePaymentFees())).build());
        bottom.setWidget(++row, 0, inject(proto().depositLineItems(), new LineItemCollapsableViewer()));
        bottom.setWidget(++row, 0, new DecoratorBuilder(inject(proto().productCreditAmount())).build());
        bottom.setWidget(++row, 0, new DecoratorBuilder(inject(proto().carryForwardCredit())).build());

        bottom.setWidget(++row, 0, new DecoratorBuilder(inject(proto().currentAmount())).build());
        bottom.setWidget(++row, 0, new DecoratorBuilder(inject(proto().taxes())).build());

        // Dues:
        bottom.setHR(++row, 0, 2);
        bottom.setWidget(++row, 0, new DecoratorBuilder(inject(proto().totalDueAmount())).build());

        top.getColumnFormatter().setWidth(1, "200px");
        bottom.getColumnFormatter().setWidth(0, "50%");
        bottom.getColumnFormatter().setWidth(1, "50%");

        // form main panel:
        FormFlexPanel main = new FormFlexPanel();
        main.setWidget(0, 0, top);
        main.setWidget(1, 0, bottom);

        return main;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        if (!justCurrentBill) {
            get(proto().rejectReason()).setVisible(getValue().billStatus().getValue() == BillStatus.Rejected);
            if (getValue().productCreditAmount().getValue().compareTo(BigDecimal.ZERO) == 0)
                get(proto().productCreditAmount()).setVisible(false);
            if (getValue().latePaymentFees().getValue().compareTo(BigDecimal.ZERO) == 0)
                get(proto().latePaymentFees()).setVisible(false);
            if (getValue().carryForwardCredit().getValue().compareTo(BigDecimal.ZERO) == 0)
                get(proto().carryForwardCredit()).setVisible(false);

            hideLines(getValue().depositRefundLineItems(), proto().depositRefundLineItems());
            hideLines(getValue().immediateAccountAdjustmentLineItems(), proto().immediateAccountAdjustmentLineItems());
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

    protected class DecoratorBuilder extends WidgetDecorator.Builder { //builder specifically for this form (as it uses mixed formats)

        public DecoratorBuilder(CComponent<?, ?> component) {
            super(component);
            readOnlyMode(!isEditable());
            componentAlignment(Alignment.right);
            labelAlignment(Alignment.left);
            useLabelSemicolon(false);
            componentWidth(30);
        }
    }

    private void hideLines(InvoiceLineItemGroupDTO line, InvoiceLineItemGroupDTO line2) {
        if (line.lineItems().size() == 0) {
            get(line2).setVisible(false);
        }
    }
}