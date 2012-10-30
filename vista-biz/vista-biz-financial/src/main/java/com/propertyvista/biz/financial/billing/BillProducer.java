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
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.biz.financial.TaxUtils;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.shared.BillingException;

class BillProducer {

    private final static Logger log = LoggerFactory.getLogger(BillProducer.class);

    private static final I18n i18n = I18n.get(BillProducer.class);

    private Bill nextPeriodBill;

    private Bill currentPeriodBill;

    private Bill previousPeriodBill;

    private final BillingCycle billingCycle;

    private final boolean preview;

    private final Lease lease;

    BillProducer(BillingCycle billingCycle, Lease lease, boolean preview) {

        this.billingCycle = billingCycle;
        this.preview = preview;
        this.lease = lease;

    }

    Bill produceBill() {

        Persistence.service().retrieve(lease.billingAccount().adjustments());

        Bill bill = EntityFactory.create(Bill.class);
        try {
            bill.billStatus().setValue(Bill.BillStatus.Running);
            bill.billingAccount().set(lease.billingAccount());

            if (preview) {
                bill.billSequenceNumber().setValue(0);
            } else {
                lease.billingAccount().billCounter().setValue(lease.billingAccount().billCounter().getValue() + 1);
                Persistence.service().persist(lease.billingAccount());

                bill.billSequenceNumber().setValue(lease.billingAccount().billCounter().getValue());
                bill.latestBillInCycle().setValue(true);
            }

            bill.billingCycle().set(billingCycle);

            currentPeriodBill = BillingManager.getLatestConfirmedBill(lease);
            bill.previousCycleBill().set(currentPeriodBill);
            if (currentPeriodBill != null) {
                Persistence.service().retrieve(currentPeriodBill.lineItems());
                if (!currentPeriodBill.previousCycleBill().isNull()) {
                    previousPeriodBill = currentPeriodBill.previousCycleBill();
                    Persistence.service().retrieve(previousPeriodBill.lineItems());
                }
            }

            bill.executionDate().setValue(new LogicalDate(SysDateManager.getSysDate()));

            Bill.BillType billType = findBillType();
            bill.billType().setValue(billType);

            bill.billingPeriodStartDate().setValue(BillDateUtils.calculateBillingPeriodStartDate(bill));
            bill.billingPeriodEndDate().setValue(BillDateUtils.calculateBillingPeriodEndDate(bill));
            bill.dueDate().setValue(BillDateUtils.calculateBillDueDate(bill));

            nextPeriodBill = bill;

            prepareAccumulators();

            Bill lastBill = BillingManager.getLatestConfirmedBill(nextPeriodBill.billingAccount().lease());
            if (lastBill != null) {
                nextPeriodBill.balanceForwardAmount().setValue(lastBill.totalDueAmount().getValue());
            } else {
                nextPeriodBill.balanceForwardAmount().setValue(new BigDecimal("0.00"));
            }

            List<AbstractBillingProcessor> processors = getExecutionPlan(nextPeriodBill.billType().getValue());
            for (AbstractBillingProcessor processor : processors) {
                processor.execute();
            }

            calculateTotals();

            bill.billStatus().setValue(Bill.BillStatus.Finished);

            if (!preview) {
                BillingManager.updateBillingCycleStats(bill, true);
                Persistence.service().persist(bill.lineItems());
                Persistence.service().persist(bill);

                LeaseBillingPolicy leaseBillingPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit().building(),
                        LeaseBillingPolicy.class);

                if (leaseBillingPolicy.confirmationMethod().getValue() == LeaseBillingPolicy.BillConfirmationMethod.automatic) {
                    BillingManager.confirmBill(bill);
                }
            }

        } catch (Throwable e) {
            log.error("Bill run error", e);
            bill.billStatus().setValue(Bill.BillStatus.Failed);
            String billCreationError = i18n.tr("Bill run error");
            if (BillingException.class.isAssignableFrom(e.getClass())) {
                billCreationError = e.getMessage();
            }
            bill.billCreationError().setValue(billCreationError);
            bill.lineItems().clear();

            if (!preview) {
                BillingManager.updateBillingCycleStats(bill, true);
                Persistence.service().persist(bill);
            }
        }
        return bill;
    }

    private void prepareAccumulators() {
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
        nextPeriodBill.previousChargeRefunds().setValue(new BigDecimal("0.00"));
        nextPeriodBill.latePaymentFees().setValue(new BigDecimal("0.00"));
        nextPeriodBill.depositAmount().setValue(new BigDecimal("0.00"));
        nextPeriodBill.productCreditAmount().setValue(new BigDecimal("0.00"));
        nextPeriodBill.carryForwardCredit().setValue(new BigDecimal("0.00"));

        nextPeriodBill.taxes().setValue(new BigDecimal("0.00"));
    }

    protected List<AbstractBillingProcessor> getExecutionPlan(Bill.BillType billType) {
        switch (billType) {
        case First:
            // @formatter:off
            return Arrays.asList(new AbstractBillingProcessor[] {
                    
                    new BillingProductChargeProcessor(this),
                    new BillingDepositProcessor(this),
                    new BillingLeaseAdjustmentProcessor(this), 
                    new BillingPaymentProcessor(this)
                    
            });
            // @formatter:on

        case ZeroCycle:
            // @formatter:off
            return Arrays.asList(new AbstractBillingProcessor[] {
                    // create current bill charges/adjustments etc..
                    new BillingProductChargeProcessor(this),
                    // deposits for zero-cycle must be accounted for in the carry-forward balance
                    new BillingDepositProcessor(this),
                    // create initial debit/credit so initial debit/credit + charges = initial balance
                    new BillingCarryforwardProcessor(this)
                    
            });
            // @formatter:on

        case Regular:
            // @formatter:off
            return Arrays.asList(new AbstractBillingProcessor[] {
                    
                    new BillingProductChargeProcessor(this),
                    new BillingDepositProcessor(this),
                    new BillingLeaseAdjustmentProcessor(this), 
                    new BillingPaymentProcessor(this), 
                    /** Should run last **/
                    new BillingLatePaymentFeeProcessor(this) 
                    
            });
            // @formatter:on

        case Final:
            // @formatter:off
            return Arrays.asList(new AbstractBillingProcessor[] { 
                    new BillingProductChargeProcessor(this), 
                    new BillingLeaseAdjustmentProcessor(this),
                    new BillingPaymentProcessor(this),
                    /** Should run last **/
                    new BillingLatePaymentFeeProcessor(this) });
            // @formatter:on

        default:
            throw new Error("Can't find execution plan for billType " + billType);
        }
    }

    private void calculateTotals() {

        // @formatter:off
        
        nextPeriodBill.pastDueAmount().setValue(
                nextPeriodBill.balanceForwardAmount().getValue().
                add(nextPeriodBill.paymentReceivedAmount().getValue()).
                add(nextPeriodBill.paymentRejectedAmount().getValue()).
                add(nextPeriodBill.immediateAccountAdjustments().getValue()).
                add(nextPeriodBill.nsfCharges().getValue()).
                add(nextPeriodBill.depositRefundAmount().getValue()).
                add(nextPeriodBill.withdrawalAmount().getValue()).
                add(nextPeriodBill.carryForwardCredit().getValue()));

        nextPeriodBill.currentAmount().setValue(
                nextPeriodBill.serviceCharge().getValue().
                add(nextPeriodBill.recurringFeatureCharges().getValue()).
                add(nextPeriodBill.oneTimeFeatureCharges().getValue()).
                add(nextPeriodBill.pendingAccountAdjustments().getValue()).
                add(nextPeriodBill.previousChargeRefunds().getValue()).
                add(nextPeriodBill.latePaymentFees().getValue()).
                add(nextPeriodBill.depositAmount().getValue()));

        BigDecimal taxCombinedAmount = TaxUtils.calculateCombinedTax(nextPeriodBill.lineItems());
        if (taxCombinedAmount.subtract(nextPeriodBill.taxes().getValue()).abs().compareTo(BigDecimal.ZERO) >= 0.01) {
            TaxUtils.pennyFix(taxCombinedAmount.subtract(nextPeriodBill.taxes().getValue()), nextPeriodBill.lineItems());
            nextPeriodBill.taxes().setValue(taxCombinedAmount);            
        }

        nextPeriodBill.totalDueAmount().setValue(nextPeriodBill.pastDueAmount().getValue().add(nextPeriodBill.currentAmount().getValue().add(nextPeriodBill.taxes().getValue())));
        
        // @formatter:on
    }

    private Bill.BillType findBillType() {

        switch (lease.status().getValue()) {
        case ExistingLease: //zeroCycle bill should be issued; preview only
            if (!preview) {
                throw new BillingException(i18n.tr("Billing can only run in PREVIEW mode until Lease is Approved."));
            }
            return Bill.BillType.ZeroCycle;
        case Application: // preview only
            if (!preview) {
                throw new BillingException(i18n.tr("Billing can only run in PREVIEW mode until Lease is Approved."));
            }
            return Bill.BillType.First;
        case Approved: // first bill should be issued
            if (BillingManager.getLatestConfirmedBill(lease) != null) {
                return Bill.BillType.Regular;
            } else {
                if (lease.billingAccount().carryforwardBalance().isNull()) {
                    return Bill.BillType.First;
                } else {
                    return Bill.BillType.ZeroCycle;
                }
            }
        case Active:
            if (currentPeriodBill != null) {
                //check if previous confirmed Bill is the last cycle bill and only final bill should run after
                if (currentPeriodBill.billingPeriodEndDate().getValue().compareTo(lease.currentTerm().termTo().getValue()) == 0) {
                    return Bill.BillType.Final;
                } else {
                    return Bill.BillType.Regular;

                }
            }
        case Completed: // final bill should be issued
            return Bill.BillType.Final;
        default:
            throw new BillingException(i18n.tr("Billing can't run when lease is in status ''{0}''", lease.status().getValue()));
        }
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
