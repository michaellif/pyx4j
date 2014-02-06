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
package com.propertyvista.portal.resident.ui.financial.views.bill;

import java.math.BigDecimal;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.theme.BillingTheme;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.dto.BillDTO;
import com.propertyvista.dto.InvoiceLineItemGroupDTO;
import com.propertyvista.portal.shared.ui.util.CBuildingLabel;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class BillForm extends CEntityForm<BillDTO> {

    private static final I18n i18n = I18n.get(BillForm.class);

    private final boolean showInfoSection;

    private final boolean showPreviousBill;

    public BillForm() {
        this(true, true);
    }

    public BillForm(boolean showInfoSection, boolean showPreviousBill) {
        super(BillDTO.class);

        this.showInfoSection = showPreviousBill;
        this.showPreviousBill = showPreviousBill;

        setEditable(false);
        inheritEditable(false);

        setViewable(true);
        inheritViewable(false);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel content = new BasicFlexFormPanel();
        int row = -1;

        if (showInfoSection) {
            content.setH1(++row, 0, 1, i18n.tr("Info"));
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().billingAccount().lease().unit(), new CEntityLabel<AptUnit>())).build());
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().billingCycle().building(), new CBuildingLabel())).build());

            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().billingCycle().billingType().billingPeriod(), new CEnumLabel())).build());
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().billingCycle().billingCycleStartDate(), new CDateLabel())).build());
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().billingCycle().billingCycleEndDate(), new CDateLabel())).build());

            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().billingCycle().targetBillExecutionDate(), new CDateLabel())).build());

            content.setH1(++row, 0, 1, i18n.tr("Status"));
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().billSequenceNumber(), new CNumberLabel())).build());
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().dueDate(), new CDateLabel())).build());
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().rejectReason(), new CLabel<String>())).build());

            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().billType(), new CEnumLabel())).build());
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().billStatus(), new CEnumLabel())).build());
        }

        if (showPreviousBill) {
            content.setH1(++row, 0, 1, i18n.tr("Previous Bill"));
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().balanceForwardAmount(), new CMoneyLabel())).build());
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().carryForwardCredit(), new CMoneyLabel())).build());

            content.setWidget(++row, 0, inject(proto().depositRefundLineItems(), new LineItemCollapsibleViewer()));
            content.setWidget(++row, 0, inject(proto().immediateAccountAdjustmentLineItems(), new LineItemCollapsibleViewer()));
            content.setWidget(++row, 0, inject(proto().previousChargeRefundLineItems(), new LineItemCollapsibleViewer()));
            content.setWidget(++row, 0, inject(proto().nsfChargeLineItems(), new LineItemCollapsibleViewer()));
            content.setWidget(++row, 0, inject(proto().withdrawalLineItems(), new LineItemCollapsibleViewer()));
            content.setWidget(++row, 0, inject(proto().rejectedPaymentLineItems(), new LineItemCollapsibleViewer()));
            content.setWidget(++row, 0, inject(proto().paymentLineItems(), new LineItemCollapsibleViewer()));

            Widget pastDueAmount = new FormWidgetDecoratorBuilder(inject(proto().pastDueAmount(), new CMoneyLabel())).customLabel(
                    i18n.tr("Previous Bill Balance")).build();
            pastDueAmount.addStyleName(BillingTheme.StyleName.BillingBillTotal.name());
            content.setWidget(++row, 0, pastDueAmount);
        }

        content.setH1(++row, 0, 1, i18n.tr("Current Bill"));
        if (!showPreviousBill) {
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().pastDueAmount(), new CMoneyLabel())).build());
        }
        content.setWidget(++row, 0, inject(proto().serviceChargeLineItems(), new LineItemCollapsibleViewer()));
        content.setWidget(++row, 0, inject(proto().recurringFeatureChargeLineItems(), new LineItemCollapsibleViewer()));
        content.setWidget(++row, 0, inject(proto().onetimeFeatureChargeLineItems(), new LineItemCollapsibleViewer()));
        content.setWidget(++row, 0, inject(proto().pendingAccountAdjustmentLineItems(), new LineItemCollapsibleViewer()));
        content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().latePaymentFees(), new CMoneyLabel())).build());
        content.setWidget(++row, 0, inject(proto().depositLineItems(), new LineItemCollapsibleViewer()));
        content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().productCreditAmount(), new CMoneyLabel())).build());

        content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().currentAmount(), new CMoneyLabel())).build());
        content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().taxes(), new CMoneyLabel())).build());

        // Dues:
        Widget grandTotal = new FormWidgetDecoratorBuilder(inject(proto().totalDueAmount(), new CMoneyLabel())).build();
        grandTotal.addStyleName(BillingTheme.StyleName.BillingBillTotal.name());
        content.setWidget(++row, 0, grandTotal);

        return content;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (showPreviousBill) {
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