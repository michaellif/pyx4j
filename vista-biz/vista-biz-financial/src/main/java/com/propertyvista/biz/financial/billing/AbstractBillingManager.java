/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 30, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.TaxUtils;
import com.propertyvista.domain.financial.billing.Bill;

abstract class AbstractBillingManager {

    private final static Logger log = LoggerFactory.getLogger(AbstractBillingManager.class);

    private static final I18n i18n = I18n.get(AbstractBillingManager.class);

    private final Bill nextPeriodBill;

    private Bill currentPeriodBill;

    private Bill previousPeriodBill;

    AbstractBillingManager(Bill bill, Bill.BillType billType) {

        bill.billType().setValue(billType);

        bill.billingPeriodStartDate().setValue(BillDateUtils.calculateBillingPeriodStartDate(bill));
        bill.billingPeriodEndDate().setValue(BillDateUtils.calculateBillingPeriodEndDate(bill));
        bill.dueDate().setValue(BillDateUtils.calculateBillDueDate(bill));

        this.nextPeriodBill = bill;
        if (!bill.previousCycleBill().isNull()) {
            this.currentPeriodBill = bill.previousCycleBill();
            Persistence.service().retrieve(currentPeriodBill.lineItems());
        }
        if (currentPeriodBill != null && !currentPeriodBill.previousCycleBill().isNull()) {
            this.previousPeriodBill = currentPeriodBill.previousCycleBill();
            Persistence.service().retrieve(previousPeriodBill.lineItems());
        }
    }

    void processBill() {

        //Set accumulating fields to 0 value
        nextPeriodBill.serviceCharge().setValue(new BigDecimal("0.00"));

        nextPeriodBill.depositRefundAmount().setValue(new BigDecimal("0.00"));
        nextPeriodBill.immediateAccountAdjustments().setValue(new BigDecimal("0.00"));
        nextPeriodBill.nsfCharges().setValue(new BigDecimal("0.00"));
        nextPeriodBill.withdrawalAmount().setValue(new BigDecimal("0.00"));
        nextPeriodBill.paymentRejectedAmount().setValue(new BigDecimal("0.00"));
        nextPeriodBill.paymentReceivedAmount().setValue(new BigDecimal("0.00"));

        nextPeriodBill.recurringFeatureCharges().setValue(new BigDecimal("0.00"));
        nextPeriodBill.oneTimeFeatureCharges().setValue(new BigDecimal("0.00"));
        nextPeriodBill.pendingAccountAdjustments().setValue(new BigDecimal("0.00"));
        nextPeriodBill.latePaymentFees().setValue(new BigDecimal("0.00"));
        nextPeriodBill.depositAmount().setValue(new BigDecimal("0.00"));
        nextPeriodBill.productCreditAmount().setValue(new BigDecimal("0.00"));
        nextPeriodBill.carryForwardCredit().setValue(new BigDecimal("0.00"));

        nextPeriodBill.taxes().setValue(new BigDecimal("0.00"));

        Bill lastBill = BillingLifecycleManager.getLatestConfirmedBill(nextPeriodBill.billingAccount().lease());
        if (lastBill != null) {
            nextPeriodBill.balanceForwardAmount().setValue(lastBill.totalDueAmount().getValue());
        } else {
            nextPeriodBill.balanceForwardAmount().setValue(new BigDecimal("0.00"));
        }

        List<AbstractBillingProcessor> processors = initProcessors();
        for (AbstractBillingProcessor processor : processors) {
            processor.execute();
        }

        calculateTotals();

    }

    abstract protected List<AbstractBillingProcessor> initProcessors();

    private void calculateTotals() {

        // @formatter:off
        
        nextPeriodBill.pastDueAmount().setValue(
                nextPeriodBill.balanceForwardAmount().getValue().
                add(nextPeriodBill.paymentReceivedAmount().getValue()).
                add(nextPeriodBill.paymentRejectedAmount().getValue()).
                add(nextPeriodBill.immediateAccountAdjustments().getValue()).
                add(nextPeriodBill.nsfCharges().getValue()).
                add(nextPeriodBill.depositRefundAmount().getValue()).
                add(nextPeriodBill.withdrawalAmount().getValue()));

        nextPeriodBill.currentAmount().setValue(
                nextPeriodBill.serviceCharge().getValue().
                add(nextPeriodBill.recurringFeatureCharges().getValue()).
                add(nextPeriodBill.oneTimeFeatureCharges().getValue()).
                add(nextPeriodBill.pendingAccountAdjustments().getValue()).
                add(nextPeriodBill.latePaymentFees().getValue()).
                add(nextPeriodBill.depositAmount().getValue()).
                add(nextPeriodBill.carryForwardCredit().getValue()));
        
        BigDecimal taxCombinedAmount = TaxUtils.calculateCombinedTax(nextPeriodBill.lineItems());
        if (taxCombinedAmount.subtract(nextPeriodBill.taxes().getValue()).abs().compareTo(BigDecimal.ZERO) >= 0.01) {
            TaxUtils.pennyFix(taxCombinedAmount.subtract(nextPeriodBill.taxes().getValue()), nextPeriodBill.lineItems());
            nextPeriodBill.taxes().setValue(taxCombinedAmount);            
        }

        nextPeriodBill.totalDueAmount().setValue(nextPeriodBill.pastDueAmount().getValue().add(nextPeriodBill.currentAmount().getValue().add(nextPeriodBill.taxes().getValue())));
        
        // @formatter:on
    }

    public Bill getNextPeriodBill() {
        return nextPeriodBill;
    }

    public Bill getCurrentPeriodBill() {
        return currentPeriodBill;
    }

    public Bill getPreviousPeriodBill() {
        return previousPeriodBill;
    }

}
