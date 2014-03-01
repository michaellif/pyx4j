/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 6, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing.internal;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.TaxUtils;
import com.propertyvista.biz.financial.billing.AbstractBillingProcessor;
import com.propertyvista.biz.financial.billing.BillDateUtils;
import com.propertyvista.biz.financial.billing.BillProducer;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.financial.billing.DateRange;
import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.shared.BillingException;

class InternalBillProducer implements BillProducer {

    private final static Logger log = LoggerFactory.getLogger(InternalBillProducer.class);

    private static final I18n i18n = I18n.get(InternalBillProducer.class);

    private Bill nextPeriodBill;

    private Bill currentPeriodBill;

    private Bill previousPeriodBill;

    private final BillingCycle billingCycle;

    private final boolean preview;

    private final Lease lease;

    InternalBillProducer(BillingCycle billingCycle, Lease lease, boolean preview) {

        this.billingCycle = billingCycle;
        this.preview = preview;
        this.lease = lease;

    }

    @Deprecated
    public static Bill produceProductBillPreview(BillingCycle billingCycle, Lease lease) {
        // make up required lease prototype
        Lease serviceLease = lease.duplicate();
        serviceLease.setPrimaryKey(new Key(0));
        serviceLease.status().setValue(Lease.Status.Application);
        // first bill start date should match lease start date
        serviceLease.currentTerm().termFrom().set(billingCycle.billingCycleStartDate());
        // billing account is used for previous bill lookup
        serviceLease.billingAccount().setPrimaryKey(new Key(0));
        serviceLease.billingAccount().carryforwardBalance().setValue(BigDecimal.ZERO);

        InternalBillProducer producer = new InternalBillProducer(billingCycle, serviceLease, true);
        Bill billPreview = producer.produceBill();

        return billPreview;
    }

    @Override
    public Bill produceBill() {

        BillingAccount billingAccount = lease.billingAccount();

        Persistence.service().retrieve(billingAccount.adjustments());

        Bill bill = EntityFactory.create(Bill.class);
        try {
            bill.billingAccount().set(lease.billingAccount());

            if (preview) {
                bill.billSequenceNumber().setValue(0);
            } else {
                billingAccount.billCounter().setValue(billingAccount.billCounter().getValue() + 1);
                Persistence.service().persist(lease.billingAccount());

                bill.billSequenceNumber().setValue(billingAccount.billCounter().getValue());
            }

            bill.billingCycle().set(billingCycle);
            BillingManager.instance().setBillStatus(bill, Bill.BillStatus.Running, true);

            currentPeriodBill = BillingManager.instance().getLatestConfirmedBill(lease);

            if (currentPeriodBill != null) {
                Persistence.service().retrieve(currentPeriodBill.lineItems());

                BillingCycle priorBillingCycle = ServerSideFactory.create(BillingCycleFacade.class).getPriorBillingCycle(currentPeriodBill.billingCycle());

                EntityQueryCriteria<Bill> criteria = EntityQueryCriteria.create(Bill.class);
                criteria.eq(criteria.proto().billingAccount(), lease.billingAccount());
                criteria.eq(criteria.proto().billingCycle(), priorBillingCycle);
                previousPeriodBill = Persistence.service().retrieve(criteria);
                if (previousPeriodBill != null) {
                    Persistence.service().retrieve(previousPeriodBill.lineItems());
                }

            }

            bill.executionDate().setValue(SystemDateManager.getLogicalDate());

            Bill.BillType billType = findBillType();
            bill.billType().setValue(billType);

            DateRange billingPeriodRange = BillDateUtils.calculateBillingPeriodRange(bill);
            bill.billingPeriodStartDate().setValue(billingPeriodRange.getFromDate());
            bill.billingPeriodEndDate().setValue(billingPeriodRange.getToDate());
            bill.dueDate().setValue(BillDateUtils.calculateBillDueDate(bill));

            nextPeriodBill = bill;

            BillingUtils.prepareAccumulators(nextPeriodBill);

            Bill lastBill = BillingManager.instance().getLatestConfirmedBill(lease);
            if (lastBill != null) {
                nextPeriodBill.balanceForwardAmount().setValue(lastBill.totalDueAmount().getValue());
            } else {
                nextPeriodBill.balanceForwardAmount().setValue(new BigDecimal("0.00"));
            }

            List<AbstractBillingProcessor<?>> processors = getExecutionPlan(nextPeriodBill.billType().getValue());
            for (AbstractBillingProcessor<?> processor : processors) {
                processor.execute();
            }

            calculateTotals();

            BillingManager.instance().setBillStatus(bill, Bill.BillStatus.Finished, true);

            if (!preview) {

                Persistence.service().persist(bill.lineItems());
                Persistence.service().persist(bill);

                LeaseBillingPolicy leaseBillingPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit().building(),
                        LeaseBillingPolicy.class);

                if (leaseBillingPolicy.confirmationMethod().getValue() == LeaseBillingPolicy.BillConfirmationMethod.automatic) {
                    bill = BillingManager.instance().confirmBill(bill);
                }
            }
        } catch (Throwable e) {
            log.error("Bill run error", e);
            BillingManager.instance().setBillStatus(bill, Bill.BillStatus.Failed, true);
            String billCreationError = i18n.tr("Bill run error");
            if (BillingException.class.isAssignableFrom(e.getClass())) {
                billCreationError = e.getMessage();
            }
            bill.billCreationError().setValue(billCreationError);
            bill.lineItems().clear();

            if (!preview) {
                Persistence.service().persist(bill);
            }
        }
        return bill;
    }

    protected List<AbstractBillingProcessor<?>> getExecutionPlan(Bill.BillType billType) {
        switch (billType) {
        case First:
            // @formatter:off
            return Arrays.asList(new AbstractBillingProcessor<?>[] {

                    new BillingProductChargeProcessor(this),
                    new BillingDepositProcessor(this),
                    new BillingLeaseAdjustmentProcessor(this),
                    new BillingPaymentProcessor(this)

            });
            // @formatter:on

        case ZeroCycle:
            // @formatter:off
            return Arrays.asList(new AbstractBillingProcessor<?>[] {
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
            return Arrays.asList(new AbstractBillingProcessor<?>[] {

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
            return Arrays.asList(new AbstractBillingProcessor<?>[] {
                    
                    new BillingProductChargeProcessor(this),
                    new BillingDepositProcessor(this),
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
        case ExistingLease: // zeroCycle bill should be issued; preview only
            if (!preview) {
                throw new BillingException(i18n.tr("Billing can only run in PREVIEW mode until Lease is Approved."));
            }
            return Bill.BillType.ZeroCycle;
        case NewLease:
        case Application: // preview only
            if (!preview) {
                throw new BillingException(i18n.tr("Billing can only run in PREVIEW mode until Lease is Approved."));
            }
            return Bill.BillType.First;
        case Approved: // first bill should be issued
            if (BillingManager.instance().getLatestConfirmedBill(lease) != null) {
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