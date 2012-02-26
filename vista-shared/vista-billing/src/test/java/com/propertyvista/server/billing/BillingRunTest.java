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
 * Created on Feb 1, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.server.billing;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.dev.DataDump;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Feature.Type;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.AdjustmentType;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.ChargeType;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.TermType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;

public class BillingRunTest extends BillingTestBase {

    private String billingCycleId;

    private void createAgreement() {
        Lease lease = leaseDataModel.getLease();
        ProductItem serviceItem = leaseDataModel.getServiceItem();

        lease.serviceAgreement().serviceItem().item().set(serviceItem);

        Service service = serviceItem.product().cast();

        for (Feature feature : service.features()) {
            if (feature.items().size() == 0) {
                continue;
            }
            if (Type.addOn.equals(feature.type().getValue())) {
                BillableItem billableItem = EntityFactory.create(BillableItem.class);
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(leaseDateFrom);
                calendar.add(Calendar.MONTH, 4);
                calendar.add(Calendar.DATE, 5);
                billableItem.effectiveDate().setValue(new LogicalDate(calendar.getTime()));
                calendar.add(Calendar.DATE, 15);
                billableItem.expirationDate().setValue(new LogicalDate(calendar.getTime()));

                billableItem.item().set(feature.items().get(0));
                lease.serviceAgreement().featureItems().add(billableItem);

            } else if (feature.type().getValue().isInAgreement()) {
                BillableItem billableItem = EntityFactory.create(BillableItem.class);
                billableItem.item().set(feature.items().get(0));

                //One time adjustment(discount) on parking for second billing run
                if (Type.parking.equals(feature.type().getValue())) {
                    BillableItemAdjustment adjustment = EntityFactory.create(BillableItemAdjustment.class);
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(leaseDateFrom);
                    calendar.add(Calendar.MONTH, 1);
                    calendar.add(Calendar.DATE, 5);
                    adjustment.effectiveDate().setValue(new LogicalDate(calendar.getTime()));
                    adjustment.value().setValue(new BigDecimal("-10.00"));
                    adjustment.adjustmentType().setValue(AdjustmentType.monetary);
                    adjustment.chargeType().setValue(ChargeType.discount);
                    adjustment.termType().setValue(TermType.oneTime);
                    billableItem.adjustments().add(adjustment);
                }

                //Full term adjustment(discount) on parking
                if (Type.parking.equals(feature.type().getValue())) {
                    BillableItemAdjustment adjustment = EntityFactory.create(BillableItemAdjustment.class);
                    adjustment.effectiveDate().setValue(new LogicalDate(leaseDateFrom));
                    adjustment.value().setValue(new BigDecimal("-5.00"));
                    adjustment.adjustmentType().setValue(AdjustmentType.monetary);
                    adjustment.chargeType().setValue(ChargeType.discount);
                    adjustment.termType().setValue(TermType.inLease);
                    billableItem.adjustments().add(adjustment);
                }

                //Full term adjustment(discount) on locker
                if (Type.locker.equals(feature.type().getValue())) {
                    BillableItemAdjustment adjustment = EntityFactory.create(BillableItemAdjustment.class);
                    adjustment.effectiveDate().setValue(new LogicalDate(leaseDateFrom));
                    adjustment.value().setValue(new BigDecimal("-0.05"));
                    adjustment.adjustmentType().setValue(AdjustmentType.percentage);
                    adjustment.chargeType().setValue(ChargeType.discount);
                    adjustment.termType().setValue(TermType.inLease);
                    billableItem.adjustments().add(adjustment);
                }

                //Full term adjustment(discount) on locker (%)
                if (Type.pet.equals(feature.type().getValue())) {
                    BillableItemAdjustment adjustment = EntityFactory.create(BillableItemAdjustment.class);
                    adjustment.effectiveDate().setValue(new LogicalDate(leaseDateFrom));
                    adjustment.adjustmentType().setValue(AdjustmentType.free);
                    adjustment.chargeType().setValue(ChargeType.discount);
                    adjustment.termType().setValue(TermType.inLease);
                    billableItem.adjustments().add(adjustment);
                }

                lease.serviceAgreement().featureItems().add(billableItem);
            }
        }

        Persistence.service().persist(lease);

    }

    public void testSequentialBillingRun() {
        preloadData();
        createAgreement();

        //==================== RUN 1 ======================//

        Bill bill = runBilling(1, true);

        assertEquals("Number of charges", 5, bill.charges().size());
        assertEquals("Number of charge adjustments", 4, bill.chargeAdjustments().size());
        assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

        assertEquals("Total adjustments", new BigDecimal("-26.19"), bill.totalAdjustments().getValue());
        assertEquals("One-time feature charges", new BigDecimal("40.00"), bill.oneTimeFeatureCharges().getValue());

        //==================== RUN 2 ======================//

        bill = runBilling(2, true);
        assertEquals("Number of charges", 5, bill.charges().size());
        assertEquals("Number of charge adjustments", 4, bill.chargeAdjustments().size());
        assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

        assertEquals("Total adjustments", new BigDecimal("-26.19"), bill.totalAdjustments().getValue());
        assertEquals("One-time feature charges", new BigDecimal("40.00"), bill.oneTimeFeatureCharges().getValue());

        if (false) {

            //==================== RUN 3 ======================//

            bill = runBilling(3, false);
            assertEquals("Number of charges", 4, bill.charges().size());
            assertEquals("Number of charge adjustments", 3, bill.chargeAdjustments().size());
            assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

            assertEquals("Total adjustments", new BigDecimal("-16.19"), bill.totalAdjustments().getValue());
            assertEquals("One-time feature charges", new BigDecimal("0.00"), bill.oneTimeFeatureCharges().getValue());

            bill = runBilling(4, true);
            assertEquals("Number of charges", 4, bill.charges().size());
            assertEquals("Number of charge adjustments", 3, bill.chargeAdjustments().size());
            assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

            assertEquals("Total adjustments", new BigDecimal("-16.19"), bill.totalAdjustments().getValue());
            assertEquals("One-time feature charges", new BigDecimal("0.00"), bill.oneTimeFeatureCharges().getValue());

            //==================== RUN 4 ======================//

            bill = runBilling(5, true);
            assertEquals("Number of charges", 5, bill.charges().size());
            assertEquals("Number of charge adjustments", 3, bill.chargeAdjustments().size());
            assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

            assertEquals("Total adjustments", new BigDecimal("-16.19"), bill.totalAdjustments().getValue());
            assertEquals("One-time feature charges", new BigDecimal("40.00"), bill.oneTimeFeatureCharges().getValue());

            //==================== RUN 5 ======================//

            bill = runBilling(6, false);
            assertEquals("Number of charges", 4, bill.charges().size());
            assertEquals("Number of charge adjustments", 3, bill.chargeAdjustments().size());
            assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

            bill = runBilling(7, false);
            assertEquals("Number of charges", 4, bill.charges().size());
            assertEquals("Number of charge adjustments", 3, bill.chargeAdjustments().size());
            assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

            assertEquals("Total adjustments", new BigDecimal("-16.19"), bill.totalAdjustments().getValue());
            assertEquals("One-time feature charges", new BigDecimal("0.00"), bill.oneTimeFeatureCharges().getValue());

            bill = runBilling(8, true);
            assertEquals("Number of charges", 4, bill.charges().size());
            assertEquals("Number of charge adjustments", 3, bill.chargeAdjustments().size());
            assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

            assertEquals("Total adjustments", new BigDecimal("-16.19"), bill.totalAdjustments().getValue());
            assertEquals("One-time feature charges", new BigDecimal("0.00"), bill.oneTimeFeatureCharges().getValue());

            //==================== RUN 6 ======================//

            bill = runBilling(9, true);
            assertEquals("Number of charges", 4, bill.charges().size());
            assertEquals("Number of charge adjustments", 3, bill.chargeAdjustments().size());
            assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

            assertEquals("Total adjustments", new BigDecimal("-16.19"), bill.totalAdjustments().getValue());
            assertEquals("One-time feature charges", new BigDecimal("0.00"), bill.oneTimeFeatureCharges().getValue());
        }
    }

    private Bill runBilling(int billNumber, boolean confirm) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseDataModel.getLease().getPrimaryKey());
        BillingFacade.runBilling(lease);

        Bill bill = BillingFacade.getLatestBill(lease.leaseFinancial().billingAccount());
        if (confirm) {
            BillingFacade.confirmBill(bill);
        } else {
            BillingFacade.rejectBill(bill);
        }

        Persistence.service().retrieve(bill.charges());
        Persistence.service().retrieve(bill.chargeAdjustments());
        Persistence.service().retrieve(bill.leaseAdjustments());

        DataDump.dump("bill", bill);
        DataDump.dump("lease", lease);

        assertEquals("Billing Period start date", bill.billingRun().billingPeriodStartDate().getValue(), bill.billingPeriodStartDate().getValue());
        assertEquals("Billing Period end date", bill.billingRun().billingPeriodEndDate().getValue(), bill.billingPeriodEndDate().getValue());

        int billingPeriodStartDay = bill.billingRun().billingCycle().billingPeriodStartDay().getValue();
        if (billingPeriodStartDay <= 28) {
            assertEquals("Billing Cycle Period Start Day", leaseDateFrom.getDate(), billingPeriodStartDay);
        } else {
            assertEquals("Billing Cycle Period Start Day", 1, billingPeriodStartDay);
        }
        assertEquals("Billing Cycle Payment Frequency", PaymentFrequency.Monthly, bill.billingRun().billingCycle().paymentFrequency().getValue());

        if (billingCycleId == null) {
            billingCycleId = bill.billingRun().billingCycle().id().getValue().toString();
        } else {
            assertEquals("Billing Cycle Id", billingCycleId, bill.billingRun().billingCycle().id().getValue().toString());
        }
        assertEquals("Bill Sequence Number", billNumber, (int) bill.billSequenceNumber().getValue());
        assertEquals("Bill Confirmation Status", confirm ? BillStatus.Confirmed : BillStatus.Rejected, bill.billStatus().getValue());

        assertEquals("ServiceCharge", new BigDecimal("930.30"), bill.serviceCharge().getValue());
        assertEquals("Recurring feature charges", new BigDecimal("78.38"), bill.recurringFeatureCharges().getValue());

        return bill;
    }
}
