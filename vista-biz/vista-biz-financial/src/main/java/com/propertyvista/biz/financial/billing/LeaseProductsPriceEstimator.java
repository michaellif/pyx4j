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
package com.propertyvista.biz.financial.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.TaxUtils;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.InvoiceAdjustmentSubLineItem;
import com.propertyvista.domain.financial.billing.InvoiceConcessionSubLineItem;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.util.DomainUtil;

public class LeaseProductsPriceEstimator {

    private static final I18n i18n = I18n.get(LeaseProductsPriceEstimator.class);

    private final Lease lease;

    private final BillingCycle billingCycle;

    public LeaseProductsPriceEstimator(BillingCycle billingCycle, Lease lease) {
        this.billingCycle = billingCycle;
        this.lease = lease;
    }

    public List<InvoiceProductCharge> calculateCharges() {

        List<InvoiceProductCharge> charges = new ArrayList<InvoiceProductCharge>();

        // Add service to charges
        {
            BillableItem service = lease.currentTerm().version().leaseProducts().serviceItem();

            // Calculate overlapping between billing cycle and service
            DateRange overlap = BillDateUtils.getOverlappingRange(

            new DateRange(lease.currentTerm().termFrom().getValue(), lease.currentTerm().termTo().getValue()),

            new DateRange(service.effectiveDate().getValue(), service.expirationDate().getValue()),

            new DateRange(billingCycle.billingCycleStartDate().getValue(), billingCycle.billingCycleEndDate().getValue()));

            // If service fully covers billing cycle add service to charges, otherwise return empty list of charges
            if (overlap != null && overlap.getFromDate().equals(billingCycle.billingCycleStartDate().getValue())
                    && overlap.getToDate().equals(billingCycle.billingCycleEndDate().getValue())) {
                charges.add(createCharge(service));
            } else {
                return charges;
            }
        }

        // Add recurrent features to charges
        for (BillableItem feature : lease.currentTerm().version().leaseProducts().featureItems()) {

            Persistence.service().retrieve(feature.item().product());
            //Skip one time feature
            if (BillingUtils.isOneTimeFeature(feature.item().product())) {
                continue;
            }

            DateRange overlap = BillDateUtils.getOverlappingRange(

            new DateRange(lease.currentTerm().termFrom().getValue(), lease.currentTerm().termTo().getValue()),

            new DateRange(feature.effectiveDate().getValue(), feature.expirationDate().getValue()),

            new DateRange(billingCycle.billingCycleStartDate().getValue(), billingCycle.billingCycleEndDate().getValue()));

            // If feature fully covers billing cycle add feature to charges, otherwise continue
            if (overlap == null || !overlap.getFromDate().equals(billingCycle.billingCycleStartDate().getValue())
                    || !overlap.getToDate().equals(billingCycle.billingCycleEndDate().getValue())) {
                continue;
            }

            charges.add(createCharge(feature));
        }

        return charges;
    }

    private InvoiceProductCharge createCharge(BillableItem billableItem) {
        Persistence.service().retrieve(billableItem.item().product());

        InvoiceProductCharge charge = EntityFactory.create(InvoiceProductCharge.class);

        charge.arCode().set(billableItem.item().code());

        if (BillingUtils.isService(billableItem.item().product())) {
            charge.productType().setValue(InvoiceProductCharge.ProductType.service);
        } else if (BillingUtils.isRecurringFeature(billableItem.item().product())) {
            charge.productType().setValue(InvoiceProductCharge.ProductType.recurringFeature);
        }

        createChargeSubLineItem(charge, billableItem);
        createAdjustmentSubLineItems(charge, billableItem);
        createConcessionSubLineItems(charge, billableItem);

        charge.amount().setValue(charge.chargeSubLineItem().amount().getValue());

        for (InvoiceAdjustmentSubLineItem subLineItem : charge.adjustmentSubLineItems()) {
            charge.amount().setValue(charge.amount().getValue().add(subLineItem.amount().getValue()));
        }

        for (InvoiceConcessionSubLineItem subLineItem : charge.concessionSubLineItems()) {
            charge.amount().setValue(charge.amount().getValue().add(subLineItem.amount().getValue()));
        }

        TaxUtils.calculateProductChargeTaxes(charge, billingCycle.building());

        charge.description().setValue(charge.chargeSubLineItem().billableItem().item().description().getStringView());

        return charge;
    }

    private void createChargeSubLineItem(InvoiceProductCharge charge, BillableItem billableItem) {
        charge.chargeSubLineItem().billableItem().set(billableItem);
        charge.chargeSubLineItem().amount().setValue(charge.chargeSubLineItem().billableItem().agreedPrice().getValue());
        charge.chargeSubLineItem().description().setValue(billableItem.item().description().getStringView());
    }

    private void createAdjustmentSubLineItems(InvoiceProductCharge charge, BillableItem billableItem) {
        for (BillableItemAdjustment adjustment : billableItem.adjustments()) {

            DateRange overlap = BillDateUtils.getOverlappingRange(

            new DateRange(lease.currentTerm().termFrom().getValue(), lease.currentTerm().termTo().getValue()),

            new DateRange(adjustment.billableItem().effectiveDate().getValue(), adjustment.billableItem().expirationDate().getValue()),

            new DateRange(adjustment.effectiveDate().getValue(), adjustment.expirationDate().getValue()),

            new DateRange(billingCycle.billingCycleStartDate().getValue(), billingCycle.billingCycleEndDate().getValue()));

            if (overlap != null && overlap.getFromDate().equals(billingCycle.billingCycleStartDate().getValue())
                    && overlap.getToDate().equals(billingCycle.billingCycleEndDate().getValue())) {
                createAdjustmentSubLineItem(adjustment, charge);
            }
        }
    }

    private void createAdjustmentSubLineItem(BillableItemAdjustment billableItemAdjustment, InvoiceProductCharge charge) {

        InvoiceAdjustmentSubLineItem adjustment = EntityFactory.create(InvoiceAdjustmentSubLineItem.class);

        BigDecimal amount = null;

        if (BillableItemAdjustment.Type.percentage.equals(billableItemAdjustment.type().getValue())) {
            amount = DomainUtil.roundMoney(billableItemAdjustment.billableItem().agreedPrice().getValue().multiply(billableItemAdjustment.value().getValue()));
        } else if (BillableItemAdjustment.Type.monetary.equals(billableItemAdjustment.type().getValue())) {
            amount = billableItemAdjustment.value().getValue();
        }

        adjustment.amount().setValue(amount);
        adjustment.description().setValue(billableItemAdjustment.billableItem().item().code().getStringView() + " " + i18n.tr("Adjustment"));
        adjustment.billableItemAdjustment().set(billableItemAdjustment);

        charge.adjustmentSubLineItems().add(adjustment);

    }

    private void createConcessionSubLineItems(InvoiceProductCharge charge, BillableItem billableItem) {
        //TODO
    }
}