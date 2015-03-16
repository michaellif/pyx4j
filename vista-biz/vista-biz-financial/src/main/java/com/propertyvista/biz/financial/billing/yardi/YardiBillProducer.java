/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 16, 2015
 * @author vladlouk
 */
package com.propertyvista.biz.financial.billing.yardi;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.TaxUtils;
import com.propertyvista.biz.financial.billing.AbstractBillingProcessor;
import com.propertyvista.biz.financial.billing.BillDateUtils;
import com.propertyvista.biz.financial.billing.BillProducer;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.financial.billing.DateRange;
import com.propertyvista.biz.financial.billing.internal.BillingManager;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.shared.BillingException;

class YardiBillProducer implements BillProducer {

    private final static Logger log = LoggerFactory.getLogger(YardiBillProducer.class);

    private static final I18n i18n = I18n.get(YardiBillProducer.class);

    private final BillingCycle billingCycle;

    private final Lease lease;

    YardiBillProducer(BillingCycle billingCycle, Lease lease) {
        this.billingCycle = billingCycle;
        this.lease = lease;
    }

    @Override
    public Bill produceBill() {
        BillingAccount billingAccount = lease.billingAccount();
        Persistence.service().retrieve(billingAccount.adjustments());

        Bill previewBill = EntityFactory.create(Bill.class);
        try {
            previewBill.billingAccount().set(lease.billingAccount());
            previewBill.billSequenceNumber().setValue(0);
            previewBill.billingCycle().set(billingCycle);
            BillingManager.instance().setBillStatus(previewBill, Bill.BillStatus.Running, true);

            previewBill.executionDate().setValue(SystemDateManager.getLogicalDate());

            Bill.BillType billType = findBillType();
            previewBill.billType().setValue(billType);

            DateRange billingPeriodRange = BillDateUtils.calculateBillingPeriodRange(previewBill);
            previewBill.billingPeriodStartDate().setValue(billingPeriodRange.getFromDate());
            previewBill.billingPeriodEndDate().setValue(billingPeriodRange.getToDate());
            previewBill.dueDate().setValue(BillDateUtils.calculateBillDueDate(previewBill));

            BillingUtils.prepareAccumulators(previewBill);
            previewBill.balanceForwardAmount().setValue(new BigDecimal("0.00"));

            List<AbstractBillingProcessor<?>> processors = getExecutionPlan(previewBill.billType().getValue());
            for (AbstractBillingProcessor<?> processor : processors) {
                processor.execute();
            }

            calculateTotals(previewBill);

            previewBill.billStatus().setValue(Bill.BillStatus.Finished);
        } catch (Throwable e) {
            log.error("Yardi Bill preview run error", e);
            previewBill.billStatus().setValue(Bill.BillStatus.Failed);
            String billCreationError = i18n.tr("Bill run error");
            if (BillingException.class.isAssignableFrom(e.getClass())) {
                billCreationError = e.getMessage();
            }
            previewBill.billCreationError().setValue(billCreationError);
            previewBill.lineItems().clear();
        }
        return previewBill;
    }

    protected List<AbstractBillingProcessor<?>> getExecutionPlan(Bill.BillType billType) {
        switch (billType) {
        case First:
            // @formatter:off
            return Arrays.asList(new AbstractBillingProcessor<?>[] {

//                    new BillingProductChargeProcessor(this),
//                    new BillingDepositProcessor(this),
//                    new BillingLeaseAdjustmentProcessor(this),
//                    new BillingPaymentProcessor(this)

            });
            // @formatter:on

        default:
            throw new Error("Can't find execution plan for billType " + billType);
        }
    }

    private void calculateTotals(Bill bill) {
        // @formatter:off
        bill.pastDueAmount().setValue(
                bill.balanceForwardAmount().getValue().
                add(bill.paymentReceivedAmount().getValue()).
                add(bill.paymentRejectedAmount().getValue()).
                add(bill.immediateAccountAdjustments().getValue()).
                add(bill.nsfCharges().getValue()).
                add(bill.depositRefundAmount().getValue()).
                add(bill.withdrawalAmount().getValue()).
                add(bill.carryForwardCredit().getValue()));

        bill.currentAmount().setValue(
                bill.serviceCharge().getValue().
                add(bill.recurringFeatureCharges().getValue()).
                add(bill.oneTimeFeatureCharges().getValue()).
                add(bill.pendingAccountAdjustments().getValue()).
                add(bill.previousChargeRefunds().getValue()).
                add(bill.latePaymentFees().getValue()).
                add(bill.depositAmount().getValue()));

        BigDecimal taxCombinedAmount = TaxUtils.calculateCombinedTax(bill.lineItems());
        if (taxCombinedAmount.subtract(bill.taxes().getValue()).abs().compareTo(BigDecimal.ZERO) >= 0.01) {
            TaxUtils.pennyFix(taxCombinedAmount.subtract(bill.taxes().getValue()), bill.lineItems());
            bill.taxes().setValue(taxCombinedAmount);
        }

        bill.totalDueAmount().setValue(bill.pastDueAmount().getValue().add(bill.currentAmount().getValue().add(bill.taxes().getValue())));
        // @formatter:on
    }

    private Bill.BillType findBillType() {
        switch (lease.status().getValue()) {
        case Application:
        case Approved: // first bill should be issued
        case Active:
        case Completed: // final bill should be issued
            return Bill.BillType.First;
        default:
            throw new BillingException(i18n.tr("Billing can't run when lease is in status ''{0}''", lease.status().getValue()));
        }
    }
}