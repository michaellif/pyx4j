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
package com.propertyvista.biz.financial.billingext;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.TaxUtils;
import com.propertyvista.biz.financial.billing.BillDateUtils;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billing.DateRange;
import com.propertyvista.biz.financial.billing.internal.BillingManager;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Bill.BillType;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.portal.rpc.shared.BillingException;

class ExternalBillProducer {

    private final static Logger log = LoggerFactory.getLogger(ExternalBillProducer.class);

    private static final I18n i18n = I18n.get(ExternalBillProducer.class);

    private Bill currentBill;

    private final BillingCycle billingCycle;

    private final Lease lease;

    public ExternalBillProducer(BillingCycle billingCycle, Lease lease) {

        this.billingCycle = billingCycle;
        this.lease = lease;

    }

    public static Bill produceBill(Lease leaseId) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());
        lease.currentTerm().set(Persistence.service().retrieve(LeaseTerm.class, lease.currentTerm().getPrimaryKey().asCurrentKey()));
        if (lease.currentTerm().version().isNull()) {
            lease.currentTerm().set(Persistence.service().retrieve(LeaseTerm.class, lease.currentTerm().getPrimaryKey().asDraftKey()));
        }

        BillingCycle billingCycle = ServerSideFactory.create(BillingFacade.class).getNextBillBillingCycle(lease);
        return new ExternalBillProducer(billingCycle, lease).produceBill();
    }

    Bill produceBill() {
        BillingAccount billingAccount = lease.billingAccount();

        Persistence.service().retrieve(billingAccount.adjustments());

        Bill bill = EntityFactory.create(Bill.class);
        try {
            bill.billingAccount().set(lease.billingAccount());

            billingAccount.billCounter().setValue(billingAccount.billCounter().getValue() + 1);
            Persistence.service().persist(lease.billingAccount());

            bill.billSequenceNumber().setValue(billingAccount.billCounter().getValue());

            bill.billingCycle().set(billingCycle);
            BillingManager.instance().setBillStatus(bill, Bill.BillStatus.Running, true);

            bill.executionDate().setValue(new LogicalDate(SystemDateManager.getDate()));

            bill.billType().setValue(BillType.External);

            DateRange billingPeriodRange = BillDateUtils.calculateBillingPeriodRange(bill);
            bill.billingPeriodStartDate().setValue(billingPeriodRange.getFromDate());
            bill.billingPeriodEndDate().setValue(billingPeriodRange.getToDate());
            bill.dueDate().setValue(BillDateUtils.calculateBillDueDate(bill));

            currentBill = bill;

            prepareAccumulators();

            currentBill.balanceForwardAmount().setValue(new BigDecimal("0.00"));
            new ExternalChargeProcessor(this).execute();
            new ExternalPaymentProcessor(this).execute();

            calculateTotals();

            BillingManager.instance().setBillStatus(bill, Bill.BillStatus.Confirmed, true);

            Persistence.service().persist(bill.lineItems());
            Persistence.service().persist(bill);

        } catch (Throwable e) {
            log.error("Bill run error", e);
            BillingManager.instance().setBillStatus(bill, Bill.BillStatus.Failed, true);
            String billCreationError = i18n.tr("Bill run error");
            if (BillingException.class.isAssignableFrom(e.getClass())) {
                billCreationError = e.getMessage();
            }
            bill.billCreationError().setValue(billCreationError);
            bill.lineItems().clear();

            Persistence.service().persist(bill);
        }
        return bill;
    }

    private void prepareAccumulators() {
        //Set accumulating fields to 0 value
        currentBill.serviceCharge().setValue(new BigDecimal("0.00"));

        currentBill.depositRefundAmount().setValue(new BigDecimal("0.00"));
        currentBill.immediateAccountAdjustments().setValue(new BigDecimal("0.00"));
        currentBill.nsfCharges().setValue(new BigDecimal("0.00"));
        currentBill.withdrawalAmount().setValue(new BigDecimal("0.00"));
        currentBill.paymentRejectedAmount().setValue(new BigDecimal("0.00"));
        currentBill.paymentReceivedAmount().setValue(new BigDecimal("0.00"));

        currentBill.recurringFeatureCharges().setValue(new BigDecimal("0.00"));
        currentBill.oneTimeFeatureCharges().setValue(new BigDecimal("0.00"));
        currentBill.pendingAccountAdjustments().setValue(new BigDecimal("0.00"));
        currentBill.previousChargeRefunds().setValue(new BigDecimal("0.00"));
        currentBill.latePaymentFees().setValue(new BigDecimal("0.00"));
        currentBill.depositAmount().setValue(new BigDecimal("0.00"));
        currentBill.productCreditAmount().setValue(new BigDecimal("0.00"));
        currentBill.carryForwardCredit().setValue(new BigDecimal("0.00"));

        currentBill.taxes().setValue(new BigDecimal("0.00"));
    }

    private void calculateTotals() {

        // @formatter:off

        currentBill.pastDueAmount().setValue(
                currentBill.balanceForwardAmount().getValue().
                add(currentBill.paymentReceivedAmount().getValue()).
                add(currentBill.paymentRejectedAmount().getValue()).
                add(currentBill.immediateAccountAdjustments().getValue()).
                add(currentBill.nsfCharges().getValue()).
                add(currentBill.depositRefundAmount().getValue()).
                add(currentBill.withdrawalAmount().getValue()).
                add(currentBill.carryForwardCredit().getValue()));

        currentBill.currentAmount().setValue(
                currentBill.serviceCharge().getValue().
                add(currentBill.recurringFeatureCharges().getValue()).
                add(currentBill.oneTimeFeatureCharges().getValue()).
                add(currentBill.pendingAccountAdjustments().getValue()).
                add(currentBill.previousChargeRefunds().getValue()).
                add(currentBill.latePaymentFees().getValue()).
                add(currentBill.depositAmount().getValue()));

        BigDecimal taxCombinedAmount = TaxUtils.calculateCombinedTax(currentBill.lineItems());
        if (taxCombinedAmount.subtract(currentBill.taxes().getValue()).abs().compareTo(BigDecimal.ZERO) >= 0.01) {
            TaxUtils.pennyFix(taxCombinedAmount.subtract(currentBill.taxes().getValue()), currentBill.lineItems());
            currentBill.taxes().setValue(taxCombinedAmount);
        }

        currentBill.totalDueAmount().setValue(currentBill.pastDueAmount().getValue().add(currentBill.currentAmount().getValue().add(currentBill.taxes().getValue())));

        // @formatter:on
    }

    public Bill getCurrentBill() {
        return currentBill;
    }
}
