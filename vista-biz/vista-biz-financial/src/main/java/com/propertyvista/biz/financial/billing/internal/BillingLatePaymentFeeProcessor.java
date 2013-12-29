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
package com.propertyvista.biz.financial.billing.internal;

import java.math.BigDecimal;
import java.util.List;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.AbstractBillingProcessor;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.ARCode.Type;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceLatePaymentFee;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoicePayment;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;

public class BillingLatePaymentFeeProcessor extends AbstractBillingProcessor<InternalBillProducer> {

    private static final I18n i18n = I18n.get(BillingLatePaymentFeeProcessor.class);

    BillingLatePaymentFeeProcessor(InternalBillProducer billProducer) {
        super(billProducer);
    }

    @Override
    public void execute() {
        createLatePaymentFeeItem();
    }

    /*
     * Late payments are calculated for the CurrentPeriodBill and the corresponding due amount is the TotalDueAmount
     * of the CurrentPeriodBill (or BalanceForfardAmount of the NextPeriodBill)
     * That amount does not include any payments and immediate charges posted after the CurrentPeriodBill
     * run date. These charges and payments have to be taken from unclaimed InvoiceLineItems.
     */
    private void createLatePaymentFeeItem() {
        if (getBillProducer().getCurrentPeriodBill() == null || getBillProducer().getCurrentPeriodBill().isNull()) {
            // too early for late payment calculations
            return;
        }
        Bill nextBill = getBillProducer().getNextPeriodBill();
        // Start with the total due amount calculated on the run date
        Bill curBill = getBillProducer().getCurrentPeriodBill();
        BigDecimal overdueAmount = curBill.totalDueAmount().getValue();

        // Check for posted but unclaimed items
        Persistence.service().retrieve(curBill.billingAccount());
        List<InvoiceLineItem> items = BillingUtils.getUnclaimedLineItems(nextBill.billingAccount(), nextBill.billingCycle());
        for (InvoiceLineItem item : items) {
            if (item.postDate().isNull()) {
                continue;
            }
            if (!item.postDate().getValue().after(curBill.dueDate().getValue())) {
                // immediate charges posted after bill run but before due date are still due
                overdueAmount = overdueAmount.add(BillingUtils.calculateTotal(item));
            } else if (item instanceof InvoicePayment) {
                // account for PAD payments posted after due date
                // TODO - need to ensure that corresponding PAD execution has actually happened
                InvoicePayment payment = (InvoicePayment) item;
                if (!payment.paymentRecord().preauthorizedPayment().isNull()) {
                    overdueAmount = overdueAmount.add(BillingUtils.calculateTotal(item));
                }
            }
        }
        if (overdueAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        // calculate late fees
        BigDecimal serviceCharge = curBill.serviceCharge().getValue();
        LeaseBillingPolicy leaseBillingPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(nextBill.billingCycle().building(),
                LeaseBillingPolicy.class);
        BigDecimal latePaymentFee = LatePaymentUtils.calculateLatePaymentFee(overdueAmount, serviceCharge, leaseBillingPolicy);

        // create late fee line item
        InvoiceLatePaymentFee charge = EntityFactory.create(InvoiceLatePaymentFee.class);
        charge.billingAccount().set(nextBill.billingAccount());
        charge.dueDate().setValue(nextBill.dueDate().getValue());
        charge.amount().setValue(latePaymentFee);
        charge.taxTotal().setValue(BigDecimal.ZERO);
        charge.description().setValue(i18n.tr("Late payment fee"));
        charge.arCode().set(ServerSideFactory.create(ARFacade.class).getReservedARCode(Type.LatePayment));
        nextBill.lineItems().add(charge);

        nextBill.latePaymentFees().setValue(nextBill.latePaymentFees().getValue().add(charge.amount().getValue()));
    }
}
