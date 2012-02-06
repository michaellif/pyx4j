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
package com.propertyvista.server.billing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
import com.propertyvista.domain.financial.billing.BillCharge;
import com.propertyvista.domain.financial.billing.BillChargeAdjustment;
import com.propertyvista.domain.financial.billing.BillChargeTax;
import com.propertyvista.domain.financial.billing.BillLeaseAdjustment;
import com.propertyvista.domain.financial.billing.BillPayment;
import com.propertyvista.domain.financial.billing.BillingRun;
import com.propertyvista.domain.financial.billing.Payment;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Product;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

class Billing {

    private final static Logger log = LoggerFactory.getLogger(Billing.class);

    private final Bill bill;

    private Billing(Bill bill) {
        this.bill = bill;
    }

    static void createBill(BillingRun billingRun, BillingAccount billingAccount) {
        Bill bill = EntityFactory.create(Bill.class);
        bill.billStatus().setValue(BillStatus.Running);
        bill.billingAccount().set(billingAccount);
        bill.billSequenceNumber().setValue(bill.billingAccount().billCounter().getValue());

        bill.billingRun().set(billingRun);
        Persistence.service().persist(bill);
        try {
            new Billing(bill).run();
            bill.billStatus().setValue(BillStatus.Finished);
        } catch (Throwable e) {
            log.error("Bill run error", e);
            bill.billStatus().setValue(BillStatus.Erred);
        }
        Persistence.service().persist(bill);
    }

    private void run() {
        Persistence.service().retrieve(bill.billingAccount().leaseFinancial());
        Persistence.service().retrieve(bill.billingAccount().leaseFinancial().lease());
        Persistence.service().retrieve(bill.billingAccount().leaseFinancial().lease().serviceAgreement());

        //Set accumulating fields to 0 value
        bill.paymentReceivedAmount().setValue(new BigDecimal(0));
        bill.totalRecurringFeatureCharges().setValue(new BigDecimal(0));
        bill.totalOneTimeFeatureCharges().setValue(new BigDecimal(0));
        bill.totalRecurringFeatureCharges().setValue(new BigDecimal(0));
        bill.totalOneTimeFeatureCharges().setValue(new BigDecimal(0));
        bill.totalAdjustments().setValue(new BigDecimal(0));
        bill.totalImmediateAdjustments().setValue(new BigDecimal(0));
        bill.latePaymentCharges().setValue(new BigDecimal(0));
        bill.totalTaxes().setValue(new BigDecimal(0));

        getPreviousTotals();
        createPayments();
        createCharges();
        createLeaseAdjustments();
        createProductAdjustments();

    }

    private void getPreviousTotals() {
        Bill lastBill = BillingUtils.getLatestBill(bill.billingAccount());
        if (lastBill != null) {
            bill.previousBalanceAmount().setValue(lastBill.totalDueAmount().getValue());
        }
    }

    private void createPayments() {
        EntityQueryCriteria<Payment> criteria = EntityQueryCriteria.create(Payment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billPayment(), (Serializable) null));
        List<Payment> payments = Persistence.service().query(criteria);
        for (Payment item : payments) {
            createPayment(item);
        }
    }

    private BillPayment createPayment(Payment payment) {
        BillPayment billPayment = EntityFactory.create(BillPayment.class);
        billPayment.payment().set(payment);
        billPayment.bill().set(bill);
        bill.paymentReceivedAmount().setValue(bill.paymentReceivedAmount().getValue().add(billPayment.payment().amount().getValue()));
        return billPayment;
    }

    private void createCharges() {
        createCharge(bill.billingAccount().leaseFinancial().lease().serviceAgreement().serviceItem());
        for (BillableItem item : bill.billingAccount().leaseFinancial().lease().serviceAgreement().featureItems()) {
            createCharge(item);
        }
    }

    private BillCharge createCharge(BillableItem serviceItem) {
        BillCharge charge = EntityFactory.create(BillCharge.class);
        charge.bill().set(bill);
        charge.price().setValue(serviceItem.item().price().getValue());
        if (!charge.price().isNull()) {
            charge.taxes().addAll(TaxUtils.calculateTaxes(charge.price().getValue(), serviceItem.item().type().chargeCode().taxes()));
        }
        charge.taxTotal().setValue(new BigDecimal(0));
        for (BillChargeTax chargeTax : charge.taxes()) {
            charge.taxTotal().setValue(charge.taxTotal().getValue().add(chargeTax.amount().getValue()));
        }

        addCharge(charge);
        return charge;
    }

    private void addCharge(BillCharge charge) {
        if (isService(charge.billableItem().item().product())) { //Service
            bill.serviceCharge().setValue(bill.serviceCharge().getValue().add(charge.price().getValue()));
        } else if (isRecurringFeature(charge.billableItem().item().product())) { //Recurring Feature
            bill.totalRecurringFeatureCharges().setValue(bill.totalRecurringFeatureCharges().getValue().add(charge.price().getValue()));
        } else {
//            bill.totalOneTimeFeatureCharges().setValue(bill.totalOneTimeFeatureCharges().getValue() + charge.price().getValue());
        }
    }

    private void createLeaseAdjustments() {
        for (LeaseAdjustment item : bill.billingAccount().leaseFinancial().adjustmantItems()) {
            createeLeaseAdjustment(item);
        }
    }

    private BillLeaseAdjustment createeLeaseAdjustment(LeaseAdjustment item) {
        BillLeaseAdjustment adjustment = EntityFactory.create(BillLeaseAdjustment.class);
        adjustment.bill().set(bill);
        bill.totalAdjustments().setValue(bill.totalAdjustments().getValue().add(adjustment.price().getValue()));
        return adjustment;
    }

    private void createProductAdjustments() {
        createCharge(bill.billingAccount().leaseFinancial().lease().serviceAgreement().serviceItem());
        for (BillableItem item : bill.billingAccount().leaseFinancial().lease().serviceAgreement().featureItems()) {
            createProductAdjustments(item);
        }
    }

    private void createProductAdjustments(BillableItem item) {
        for (BillableItemAdjustment adjustment : item.adjustments()) {
            createProductAdjustment(adjustment);
        }
    }

    private BillChargeAdjustment createProductAdjustment(BillableItemAdjustment itemAdjustment) {
        BillChargeAdjustment adjustment = EntityFactory.create(BillChargeAdjustment.class);
        adjustment.bill().set(bill);
        bill.totalAdjustments().setValue(bill.totalAdjustments().getValue().add(adjustment.price().getValue()));
        return adjustment;
    }

    private boolean isService(Product product) {
        return product instanceof Service;
    }

    private boolean isFeature(Product product) {
        return product instanceof Feature;
    }

    private boolean isRecurringFeature(Product product) {
        return isFeature(product) && ((Feature) product).isRecurring().getValue();
    }

}
