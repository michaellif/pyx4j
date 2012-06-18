/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 25, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing;

import java.math.BigDecimal;
import java.util.List;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.financial.billing.InvoiceLatePaymentFee;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;

public class BillingLatePaymentFeeProcessor extends AbstractBillingProcessor {

    private static final I18n i18n = I18n.get(BillingLatePaymentFeeProcessor.class);

    BillingLatePaymentFeeProcessor(AbstractBillingManager billingManager) {
        super(billingManager);
    }

    @Override
    protected void execute() {
        createLatePaymentFeeItem();
    }

    /*
     * Late payments can only be identified on the 1st of the current month, which is the Due Date of the
     * CurrentPeriodBill and the corresponding due amount is the TotalDueAmount of the CurrentPeriodBill
     * (or BalanceForfardAmount of the NextPeriodBill)
     * That amount does not include any payments and immediate charges posted after the CurrentPeriodBill
     * run date. These charges have to be taken from the InterimLineItems.
     */
    private void createLatePaymentFeeItem() {
        if (getBillingManager().getCurrentPeriodBill() == null || getBillingManager().getCurrentPeriodBill().isNull()) {
            // too early for late payment calculations
            return;
        }
        // Start with the total due amount calculated on the run date
        Bill curBill = getBillingManager().getCurrentPeriodBill();
        BigDecimal overdueAmount = curBill.totalDueAmount().getValue();

        // add all posted interim items
        Persistence.service().retrieve(curBill.billingAccount());
        List<InvoiceLineItem> items = BillingUtils.getUnclaimedLineItems(getBillingManager().getNextPeriodBill().billingAccount());
        for (InvoiceLineItem item : items) {
            if (!item.postDate().isNull() && item.postDate().getValue().compareTo(curBill.dueDate().getValue()) <= 0) {
                overdueAmount = overdueAmount.add(BillingUtils.calculateTotal(item));
            }
        }

        if (overdueAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        // calculate late fees
        BigDecimal serviceCharge = getBillingManager().getCurrentPeriodBill().serviceCharge().getValue();
        LeaseBillingPolicy leaseBillingPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(
                getBillingManager().getNextPeriodBill().billingCycle().building(), LeaseBillingPolicy.class);
        BigDecimal latePaymentFee = LatePaymentUtils.calculateLatePaymentFee(overdueAmount, serviceCharge, leaseBillingPolicy);

        // create late fee line item
        InvoiceLatePaymentFee charge = EntityFactory.create(InvoiceLatePaymentFee.class);
        charge.billingAccount().set(getBillingManager().getNextPeriodBill().billingAccount());
        charge.dueDate().setValue(getBillingManager().getNextPeriodBill().dueDate().getValue());
        charge.amount().setValue(latePaymentFee);
        charge.taxTotal().setValue(BigDecimal.ZERO);
        charge.description().setValue(i18n.tr("Late payment fee"));
        charge.debitType().setValue(DebitType.latePayment);

        getBillingManager().getNextPeriodBill().lineItems().add(charge);

        getBillingManager().getNextPeriodBill().latePaymentFees()
                .setValue(getBillingManager().getNextPeriodBill().latePaymentFees().getValue().add(charge.amount().getValue()));

    }
}
