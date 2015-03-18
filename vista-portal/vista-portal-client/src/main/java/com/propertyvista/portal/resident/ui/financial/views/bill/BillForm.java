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
 */
package com.propertyvista.portal.resident.ui.financial.views.bill;

import java.math.BigDecimal;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.theme.BillingTheme;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.dto.BillDTO;
import com.propertyvista.dto.InvoiceLineItemGroupDTO;
import com.propertyvista.portal.shared.ui.util.CBuildingLabel;

public class BillForm extends CForm<BillDTO> {

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

        setViewable(true);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        if (showInfoSection) {
            formPanel.h1(i18n.tr("Info"));
            formPanel.append(Location.Left, proto().billingAccount().lease().unit(), new CEntityLabel<AptUnit>()).decorate();
            formPanel.append(Location.Left, proto().billingCycle().building(), new CBuildingLabel()).decorate();

            formPanel.append(Location.Left, proto().billingCycle().billingType().billingPeriod(), new CEnumLabel()).decorate();
            formPanel.append(Location.Left, proto().billingCycle().billingCycleStartDate(), new CDateLabel()).decorate();
            formPanel.append(Location.Left, proto().billingCycle().billingCycleEndDate(), new CDateLabel()).decorate();

            formPanel.append(Location.Left, proto().billingCycle().targetBillExecutionDate(), new CDateLabel()).decorate();

            formPanel.h1(i18n.tr("Status"));
            formPanel.append(Location.Left, proto().billSequenceNumber(), new CNumberLabel()).decorate();
            formPanel.append(Location.Left, proto().dueDate(), new CDateLabel()).decorate();
            formPanel.append(Location.Left, proto().rejectReason(), new CLabel<String>()).decorate();

            formPanel.append(Location.Left, proto().billType(), new CEnumLabel()).decorate();
            formPanel.append(Location.Left, proto().billStatus(), new CEnumLabel()).decorate();
        }

        if (showPreviousBill) {
            formPanel.h1(i18n.tr("Previous Bill"));
            formPanel.append(Location.Left, proto().balanceForwardAmount()).decorate();
            formPanel.append(Location.Left, proto().carryForwardCredit()).decorate();

            formPanel.append(Location.Left, proto().depositRefundLineItems(), new LineItemCollapsibleViewer());
            formPanel.append(Location.Left, proto().immediateAccountAdjustmentLineItems(), new LineItemCollapsibleViewer());
            formPanel.append(Location.Left, proto().previousChargeRefundLineItems(), new LineItemCollapsibleViewer());
            formPanel.append(Location.Left, proto().nsfChargeLineItems(), new LineItemCollapsibleViewer());
            formPanel.append(Location.Left, proto().withdrawalLineItems(), new LineItemCollapsibleViewer());
            formPanel.append(Location.Left, proto().rejectedPaymentLineItems(), new LineItemCollapsibleViewer());
            formPanel.append(Location.Left, proto().paymentLineItems(), new LineItemCollapsibleViewer());

            formPanel.append(Location.Left, proto().pastDueAmount()).decorate().componentWidth(100).customLabel(i18n.tr("Previous Bill Balance"));
            get(proto().pastDueAmount()).asWidget().addStyleName(BillingTheme.StyleName.BillingBillTotal.name());
        }

        formPanel.h1(i18n.tr("Current Bill"));
        if (!showPreviousBill) {
            formPanel.append(Location.Left, proto().pastDueAmount()).decorate();
        }
        formPanel.append(Location.Left, proto().serviceChargeLineItems(), new LineItemCollapsibleViewer());
        formPanel.append(Location.Left, proto().recurringFeatureChargeLineItems(), new LineItemCollapsibleViewer());
        formPanel.append(Location.Left, proto().onetimeFeatureChargeLineItems(), new LineItemCollapsibleViewer());
        formPanel.append(Location.Left, proto().pendingAccountAdjustmentLineItems(), new LineItemCollapsibleViewer());
        formPanel.append(Location.Left, proto().latePaymentFees()).decorate();
        formPanel.append(Location.Left, proto().depositLineItems(), new LineItemCollapsibleViewer());
        formPanel.append(Location.Left, proto().productCreditAmount()).decorate();

        formPanel.append(Location.Left, proto().currentAmount()).decorate();
        formPanel.append(Location.Left, proto().taxes()).decorate();

        // Dues:
        formPanel.append(Location.Left, proto().totalDueAmount()).decorate().componentWidth(100);
        get(proto().totalDueAmount()).asWidget().addStyleName(BillingTheme.StyleName.BillingBillTotal.name());

        return formPanel;
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