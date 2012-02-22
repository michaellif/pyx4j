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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
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
import com.propertyvista.server.billing.preload.LeaseDataModel;

public class BillingRunTest extends BillingTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createAgreement(leaseDataModel);
    }

    private void createAgreement(LeaseDataModel leaseDataModel) {
        Lease lease = leaseDataModel.getLease();
        ProductItem serviceItem = leaseDataModel.getServiceItem();

        lease.serviceAgreement().serviceItem().item().set(serviceItem);
        lease.serviceAgreement().serviceItem().billingPeriodNumber().setValue(1);

        Service service = serviceItem.product().cast();

        for (Feature feature : service.features()) {
            if (feature.items().size() == 0) {
                continue;
            }
            if (Type.addOn.equals(feature.type().getValue())) {
                BillableItem billableItem = EntityFactory.create(BillableItem.class);
                billableItem.billingPeriodNumber().setValue(4);
                billableItem.item().set(feature.items().get(0));
                lease.serviceAgreement().featureItems().add(billableItem);

            } else if (feature.type().getValue().isInAgreement()) {
                BillableItem billableItem = EntityFactory.create(BillableItem.class);
                billableItem.billingPeriodNumber().setValue(1);
                billableItem.item().set(feature.items().get(0));

                //One time adjustment(discount) on parking for second billing run
                if (Type.parking.equals(feature.type().getValue())) {
                    BillableItemAdjustment adjustment = EntityFactory.create(BillableItemAdjustment.class);
                    adjustment.billingPeriodNumber().setValue(2);
                    adjustment.value().setValue(new BigDecimal("-10.00"));
                    adjustment.adjustmentType().setValue(AdjustmentType.monetary);
                    adjustment.chargeType().setValue(ChargeType.discount);
                    adjustment.termType().setValue(TermType.oneTime);
                    billableItem.adjustments().add(adjustment);
                }

                //Full term adjustment(discount) on parking
                if (Type.parking.equals(feature.type().getValue())) {
                    BillableItemAdjustment adjustment = EntityFactory.create(BillableItemAdjustment.class);
                    adjustment.billingPeriodNumber().setValue(1);
                    adjustment.value().setValue(new BigDecimal("-5.00"));
                    adjustment.adjustmentType().setValue(AdjustmentType.monetary);
                    adjustment.chargeType().setValue(ChargeType.discount);
                    adjustment.termType().setValue(TermType.inLease);
                    billableItem.adjustments().add(adjustment);
                }

                //Full term adjustment(discount) on locker
                if (Type.locker.equals(feature.type().getValue())) {
                    BillableItemAdjustment adjustment = EntityFactory.create(BillableItemAdjustment.class);
                    adjustment.billingPeriodNumber().setValue(1);
                    adjustment.value().setValue(new BigDecimal("-0.05"));
                    adjustment.adjustmentType().setValue(AdjustmentType.percentage);
                    adjustment.chargeType().setValue(ChargeType.discount);
                    adjustment.termType().setValue(TermType.inLease);
                    billableItem.adjustments().add(adjustment);
                }

                //Full term adjustment(discount) on locker (%)
                if (Type.pet.equals(feature.type().getValue())) {
                    BillableItemAdjustment adjustment = EntityFactory.create(BillableItemAdjustment.class);
                    adjustment.billingPeriodNumber().setValue(1);
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

        //==================== RUN 1 ======================//

        Bill bill = runBilling(1, true);
        assertEquals("Number of charges", 4, bill.charges().size());
        assertEquals("Number of charge adjustments", 3, bill.chargeAdjustments().size());
        assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

        assertEquals("Billing period", 1, (int) bill.billingPeriodNumber().getValue());

        assertEquals("Total adjustments", new BigDecimal("-16.19"), bill.totalAdjustments().getValue());
        assertEquals("One-time feature charges", new BigDecimal("0.00"), bill.oneTimeFeatureCharges().getValue());

        //==================== RUN 2 ======================//

        bill = runBilling(2, true);
        assertEquals("Number of charges", 4, bill.charges().size());
        assertEquals("Number of charge adjustments", 4, bill.chargeAdjustments().size());
        assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

        assertEquals("Billing period", 2, (int) bill.billingPeriodNumber().getValue());

        assertEquals("Total adjustments", new BigDecimal("-26.19"), bill.totalAdjustments().getValue());
        assertEquals("One-time feature charges", new BigDecimal("0.00"), bill.oneTimeFeatureCharges().getValue());

        //==================== RUN 3 ======================//

        bill = runBilling(3, false);
        assertEquals("Number of charges", 4, bill.charges().size());
        assertEquals("Number of charge adjustments", 3, bill.chargeAdjustments().size());
        assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

        assertEquals("Billing period", 3, (int) bill.billingPeriodNumber().getValue());

        assertEquals("Total adjustments", new BigDecimal("-16.19"), bill.totalAdjustments().getValue());
        assertEquals("One-time feature charges", new BigDecimal("0.00"), bill.oneTimeFeatureCharges().getValue());

        bill = runBilling(4, true);
        assertEquals("Number of charges", 4, bill.charges().size());
        assertEquals("Number of charge adjustments", 3, bill.chargeAdjustments().size());
        assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

        assertEquals("Billing period", 3, (int) bill.billingPeriodNumber().getValue());

        assertEquals("Total adjustments", new BigDecimal("-16.19"), bill.totalAdjustments().getValue());
        assertEquals("One-time feature charges", new BigDecimal("0.00"), bill.oneTimeFeatureCharges().getValue());

        //==================== RUN 4 ======================//

        bill = runBilling(5, true);
        assertEquals("Number of charges", 5, bill.charges().size());
        assertEquals("Number of charge adjustments", 3, bill.chargeAdjustments().size());
        assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

        assertEquals("Billing period", 4, (int) bill.billingPeriodNumber().getValue());

        assertEquals("Total adjustments", new BigDecimal("-16.19"), bill.totalAdjustments().getValue());
        assertEquals("One-time feature charges", new BigDecimal("40.00"), bill.oneTimeFeatureCharges().getValue());

        //==================== RUN 5 ======================//

        bill = runBilling(6, false);
        assertEquals("Number of charges", 4, bill.charges().size());
        assertEquals("Number of charge adjustments", 3, bill.chargeAdjustments().size());
        assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

        assertEquals("Billing period", 5, (int) bill.billingPeriodNumber().getValue());

        bill = runBilling(7, false);
        assertEquals("Number of charges", 4, bill.charges().size());
        assertEquals("Number of charge adjustments", 3, bill.chargeAdjustments().size());
        assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

        assertEquals("Billing period", 5, (int) bill.billingPeriodNumber().getValue());

        assertEquals("Total adjustments", new BigDecimal("-16.19"), bill.totalAdjustments().getValue());
        assertEquals("One-time feature charges", new BigDecimal("0.00"), bill.oneTimeFeatureCharges().getValue());

        bill = runBilling(8, true);
        assertEquals("Number of charges", 4, bill.charges().size());
        assertEquals("Number of charge adjustments", 3, bill.chargeAdjustments().size());
        assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

        assertEquals("Billing period", 5, (int) bill.billingPeriodNumber().getValue());

        assertEquals("Total adjustments", new BigDecimal("-16.19"), bill.totalAdjustments().getValue());
        assertEquals("One-time feature charges", new BigDecimal("0.00"), bill.oneTimeFeatureCharges().getValue());

        //==================== RUN 6 ======================//

        bill = runBilling(9, true);
        assertEquals("Number of charges", 4, bill.charges().size());
        assertEquals("Number of charge adjustments", 3, bill.chargeAdjustments().size());
        assertEquals("Number of lease adjustments", 0, bill.leaseAdjustments().size());

        assertEquals("Billing period", 6, (int) bill.billingPeriodNumber().getValue());

        assertEquals("Total adjustments", new BigDecimal("-16.19"), bill.totalAdjustments().getValue());
        assertEquals("One-time feature charges", new BigDecimal("0.00"), bill.oneTimeFeatureCharges().getValue());

    }

    private Bill runBilling(int billNumber, boolean confirm) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        Lease lease = Persistence.service().query(criteria).get(0);
        BillingFacade.runBilling(lease);

        Bill bill = BillingFacade.getBill(lease.leaseFinancial().billingAccount(), lease.leaseFinancial().billingAccount().currentBillingRun());
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

        assertEquals("Bill Sequence Number", billNumber, (int) bill.billSequenceNumber().getValue());
        assertEquals("Bill Confirmation Status", confirm ? BillStatus.Confirmed : BillStatus.Rejected, bill.billStatus().getValue());

        assertEquals("ServiceCharge", new BigDecimal("930.30"), bill.serviceCharge().getValue());
        assertEquals("Recurring feature charges", new BigDecimal("78.38"), bill.recurringFeatureCharges().getValue());

        return bill;
    }
}
