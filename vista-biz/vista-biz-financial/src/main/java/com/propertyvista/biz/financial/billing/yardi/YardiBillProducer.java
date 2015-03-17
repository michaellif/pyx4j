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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IMoneyPercentAmount.ValueType;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.TaxUtils;
import com.propertyvista.biz.financial.billing.BillDateUtils;
import com.propertyvista.biz.financial.billing.BillProducer;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.financial.billing.DateRange;
import com.propertyvista.biz.financial.billing.ProrationUtils;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.InvoiceAdjustmentSubLineItem;
import com.propertyvista.domain.financial.billing.InvoiceConcessionSubLineItem;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.util.DomainUtil;
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
            previewBill.billStatus().setValue(Bill.BillStatus.Running);

            previewBill.executionDate().setValue(SystemDateManager.getLogicalDate());

            Bill.BillType billType = findBillType();
            previewBill.billType().setValue(billType);

            DateRange billingPeriodRange = BillDateUtils.calculateBillingPeriodRange(previewBill);
            previewBill.billingPeriodStartDate().setValue(billingPeriodRange.getFromDate());
            previewBill.billingPeriodEndDate().setValue(billingPeriodRange.getToDate());
            previewBill.dueDate().setValue(BillDateUtils.calculateBillDueDate(previewBill));

            BillingUtils.prepareAccumulators(previewBill);
            previewBill.balanceForwardAmount().setValue(new BigDecimal("0.00"));

            calculateProducts(previewBill);
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

    private void calculateProducts(Bill bill) {
        BillableItem service = lease.currentTerm().version().leaseProducts().serviceItem();
        createCharge(service, bill);

        for (BillableItem billableItem : lease.currentTerm().version().leaseProducts().featureItems()) {
            createCharge(billableItem, bill);
        }
    }

    private void createCharge(BillableItem billableItem, Bill bill) {
        Persistence.service().retrieve(billableItem.item().product());

        addCharge(createCharge(billableItem, bill, InvoiceProductCharge.Period.next), bill);
    }

    private void addCharge(InvoiceProductCharge charge, Bill bill) {
        if (charge == null) {
            return;
        }

        if (BillingUtils.isService(charge.chargeSubLineItem().billableItem().item().product())) { //Service
            bill.serviceCharge().setValue(bill.serviceCharge().getValue().add(charge.amount().getValue()));
        } else if (BillingUtils.isRecurringFeature(charge.chargeSubLineItem().billableItem().item().product())) { //Recurring Feature
            bill.recurringFeatureCharges().setValue(bill.recurringFeatureCharges().getValue().add(charge.amount().getValue()));
        } else {
            bill.oneTimeFeatureCharges().setValue(bill.oneTimeFeatureCharges().getValue().add(charge.amount().getValue()));
        }
        bill.taxes().setValue(bill.taxes().getValue().add(charge.taxTotal().getValue()));
        bill.lineItems().add(charge);
    }

    private InvoiceProductCharge createCharge(BillableItem billableItem, Bill bill, InvoiceProductCharge.Period period) {
        // Find if billable item period overlaps with the bill period.
        LogicalDate expirationDate = billableItem.expirationDate().getValue();
        // Keep in mind possible lease termination
        if (bill.billingAccount().lease().isValueDetached()) {
            Persistence.service().retrieve(bill.billingAccount());
            Persistence.service().retrieve(bill.billingAccount().lease());
        }
        if (expirationDate == null || expirationDate.after(bill.billingAccount().lease().leaseTo().getValue())) {
            expirationDate = bill.billingAccount().lease().leaseTo().getValue();
        }
        DateRange overlap = BillDateUtils.getOverlappingRange(new DateRange(bill.billingPeriodStartDate().getValue(), bill.billingPeriodEndDate().getValue()),
                new DateRange(billableItem.effectiveDate().getValue(), expirationDate));

        // If billable item is not in effect in this billing period do nothing
        if (overlap == null) {
            return null;
        }

        InvoiceProductCharge charge = EntityFactory.create(InvoiceProductCharge.class);

        charge.billingAccount().set(bill.billingAccount());
        charge.period().setValue(period);
        charge.fromDate().setValue(overlap.getFromDate());
        charge.toDate().setValue(overlap.getToDate());
        charge.dueDate().setValue(bill.dueDate().getValue());
        charge.arCode().set(billableItem.item().product().holder().code());

        if (BillingUtils.isService(billableItem.item().product())) {
            charge.productType().setValue(InvoiceProductCharge.ProductType.service);
        } else if (BillingUtils.isRecurringFeature(billableItem.item().product())) {
            charge.productType().setValue(InvoiceProductCharge.ProductType.recurringFeature);
        } else if (BillingUtils.isOneTimeFeature(billableItem.item().product())) {
            charge.productType().setValue(InvoiceProductCharge.ProductType.oneTimeFeature);
        } else {
            throw new Error("Unknown product type");
        }

        createChargeSubLineItem(charge, billableItem);
        createAdjustmentSubLineItems(charge, billableItem, bill);
        createConcessionSubLineItems(charge, billableItem);

        charge.amount().setValue(charge.chargeSubLineItem().amount().getValue());

        for (InvoiceAdjustmentSubLineItem subLineItem : charge.adjustmentSubLineItems()) {
            charge.amount().setValue(charge.amount().getValue().add(subLineItem.amount().getValue()));
        }

        for (InvoiceConcessionSubLineItem subLineItem : charge.concessionSubLineItems()) {
            charge.amount().setValue(charge.amount().getValue().add(subLineItem.amount().getValue()));
        }

        TaxUtils.calculateProductChargeTaxes(charge, bill.billingCycle().building());

        charge.description().setValue(charge.chargeSubLineItem().billableItem().item().name().getStringView());

        return charge;
    }

    private void createChargeSubLineItem(InvoiceProductCharge charge, BillableItem billableItem) {
        charge.chargeSubLineItem().billableItem().set(billableItem);

        if (BillingUtils.isOneTimeFeature(charge.chargeSubLineItem().billableItem().item().product())) {
            // do not prorate one-time charge
            charge.chargeSubLineItem().amount().setValue(charge.chargeSubLineItem().billableItem().agreedPrice().getValue(BigDecimal.ZERO));
        } else {
            charge.chargeSubLineItem().amount().setValue(prorate(charge));
        }

        charge.chargeSubLineItem().description().setValue(billableItem.item().description().getStringView());
    }

    private void createAdjustmentSubLineItems(InvoiceProductCharge charge, BillableItem billableItem, Bill bill) {
        for (BillableItemAdjustment adjustment : billableItem.adjustments()) {
            createAdjustmentSubLineItem(adjustment, charge, bill);
        }
    }

    private void createAdjustmentSubLineItem(BillableItemAdjustment billableItemAdjustment, InvoiceProductCharge charge, Bill bill) {
        if (Bill.BillType.Final.equals(bill.billType().getValue())) {
            throw new Error(i18n.tr("Final bill should not have adjustments"));
        }

        InvoiceAdjustmentSubLineItem adjustment = EntityFactory.create(InvoiceAdjustmentSubLineItem.class);

        BigDecimal amount = BigDecimal.ZERO;
        if (ValueType.Percentage.equals(billableItemAdjustment.type().getValue())) {
            amount = billableItemAdjustment.billableItem().agreedPrice().getValue(BigDecimal.ZERO)
                    .multiply(billableItemAdjustment.value().percent().getValue());
        } else if (ValueType.Monetary.equals(billableItemAdjustment.type().getValue())) {
            amount = billableItemAdjustment.value().amount().getValue(BigDecimal.ZERO);
        }

        DateRange overlap = BillDateUtils.getOverlappingRange(new DateRange(bill.billingPeriodStartDate().getValue(), bill.billingPeriodEndDate().getValue()),
                new DateRange(billableItemAdjustment.effectiveDate().getValue(), billableItemAdjustment.expirationDate().getValue()));

        if (overlap == null) {
            return;
        }

        overlap = BillDateUtils.getOverlappingRange(overlap, new DateRange(billableItemAdjustment.billableItem().effectiveDate().getValue(),
                billableItemAdjustment.billableItem().expirationDate().getValue()));

        if (overlap == null) {
            return;
        }

        BigDecimal proration = ProrationUtils.prorate(overlap.getFromDate(), overlap.getToDate(), bill.billingCycle());
        adjustment.amount().setValue(DomainUtil.roundMoney(amount.multiply(proration)));
        adjustment.description().setValue(billableItemAdjustment.billableItem().item().name().getStringView() + " " + i18n.tr("Adjustment"));
        adjustment.billableItemAdjustment().set(billableItemAdjustment);

        charge.adjustmentSubLineItems().add(adjustment);

    }

    private void createConcessionSubLineItems(InvoiceProductCharge charge, BillableItem billableItem) {
        //TODO
    }

    private BigDecimal prorate(InvoiceProductCharge charge) {
        BillingCycle cycle = billingCycle;
        BigDecimal proration = ProrationUtils.prorate(charge.fromDate().getValue(), charge.toDate().getValue(), cycle);
        return DomainUtil.roundMoney(charge.chargeSubLineItem().billableItem().agreedPrice().getValue(BigDecimal.ZERO).multiply(proration));
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