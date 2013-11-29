/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 4, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceChargeTax;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.financial.tax.Tax;
import com.propertyvista.domain.policy.policies.LeaseAdjustmentPolicy;
import com.propertyvista.domain.policy.policies.ProductTaxPolicy;
import com.propertyvista.domain.policy.policies.domain.LeaseAdjustmentPolicyItem;
import com.propertyvista.domain.policy.policies.domain.ProductTaxPolicyItem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.util.DomainUtil;

public class TaxUtils {

    public static BigDecimal calculateCombinedTax(IList<InvoiceLineItem> items) {
        BigDecimal taxCombinedAmount = BigDecimal.ZERO;
        ArrayList<Tax> taxes = new ArrayList<Tax>();

        for (InvoiceLineItem item : items) {
            if (item.isInstanceOf(InvoiceAccountCharge.class)) {
                InvoiceAccountCharge accountCharge = item.cast();
                if (LeaseAdjustment.Status.submited == accountCharge.adjustment().status().getValue()
                        && LeaseAdjustment.ExecutionType.immediate == accountCharge.adjustment().executionType().getValue()) {
                    continue;
                }
            }

            if (item.isInstanceOf(InvoiceDebit.class)) {
                InvoiceDebit debit = item.cast();
                for (InvoiceChargeTax tax : debit.taxes()) {
                    if (!taxes.contains(tax.tax())) {
                        taxes.add(tax.tax());
                    }
                }
            }
        }

        java.util.Collections.sort(taxes, new Comparator<Tax>() {
            @Override
            public int compare(Tax t1, Tax t2) {
                return (t1.compound().getValue() ^ t2.compound().getValue()) ? ((t1.compound().getValue() == true) ? 1 : -1) : 0;
            }
        });

        for (Tax tax : taxes) {
            BigDecimal amountPerTax = BigDecimal.ZERO;
            for (InvoiceLineItem item : items) {
                if (item.isInstanceOf(InvoiceAccountCharge.class)) {
                    InvoiceAccountCharge accountCharge = item.cast();
                    if (LeaseAdjustment.Status.submited == accountCharge.adjustment().status().getValue()
                            && LeaseAdjustment.ExecutionType.immediate == accountCharge.adjustment().executionType().getValue()) {
                        continue;
                    }
                }
                if (item.isInstanceOf(InvoiceDebit.class)) {
                    InvoiceDebit debit = item.cast();
                    for (InvoiceChargeTax invoiceTax : debit.taxes()) {
                        if (tax.equals(invoiceTax.tax())) {
                            amountPerTax = amountPerTax.add(debit.amount().getValue());
                        }
                    }
                }
            }
            if (tax.compound().isBooleanTrue()) {
                amountPerTax = amountPerTax.add(taxCombinedAmount);
                taxCombinedAmount = taxCombinedAmount.add(DomainUtil.roundMoney(amountPerTax.multiply(tax.rate().getValue())));
                break;
            } else {
                taxCombinedAmount = taxCombinedAmount.add(DomainUtil.roundMoney(amountPerTax.multiply(tax.rate().getValue())));
            }
        }

        return taxCombinedAmount;
    }

    public static void calculateProductChargeTaxes(InvoiceProductCharge charge, Building building) {
        if (!charge.amount().isNull()) {
            charge.taxes().addAll(
                    TaxUtils.calculateProductTaxes(charge.amount().getValue(), charge.chargeSubLineItem().billableItem().item().product().holder().code(),
                            building));
        }
        charge.taxTotal().setValue(BigDecimal.ZERO);
        for (InvoiceChargeTax chargeTax : charge.taxes()) {
            charge.taxTotal().setValue(charge.taxTotal().getValue().add(chargeTax.amount().getValue()));
        }
    }

    public static void calculateAccountChargeTax(InvoiceAccountCharge charge, Building building) {
        if (!charge.amount().isNull()) {
            charge.taxes().addAll(TaxUtils.calculateProductTaxes(charge.amount().getValue(), charge.adjustment().code(), building));
        }
        charge.taxTotal().setValue(BigDecimal.ZERO);
        for (InvoiceChargeTax chargeTax : charge.taxes()) {
            charge.taxTotal().setValue(charge.taxTotal().getValue().add(chargeTax.amount().getValue()));
        }
    }

    //TODO Calculate taxes for specific day
    public static List<InvoiceChargeTax> calculateProductTaxes(BigDecimal baseAmount, ARCode productCode, Building building) {
        List<Tax> taxes = retrieveTaxesForProductItemType(productCode, building);
        return calculateTaxes(baseAmount, taxes);
    }

    public static List<InvoiceChargeTax> calculateLeaseAdjustmentTaxes(BigDecimal baseAmount, ARCode adjustmentCode, Building building) {
        List<Tax> taxes = retrieveTaxesForAdjustmentReason(adjustmentCode, building);
        return calculateTaxes(baseAmount, taxes);
    }

    static List<InvoiceChargeTax> calculateTaxes(final BigDecimal baseAmount, List<Tax> taxes) {

        List<InvoiceChargeTax> chargeTaxes = new ArrayList<InvoiceChargeTax>();
        if (taxes != null) {
            BigDecimal interimAmount = baseAmount;
            for (Tax tax : taxes) {
                if (!tax.compound().isBooleanTrue()) {
                    InvoiceChargeTax chargeTax = EntityFactory.create(InvoiceChargeTax.class);
                    chargeTax.tax().set(tax);
                    chargeTax.amount().setValue(DomainUtil.roundMoney(baseAmount.multiply(tax.rate().getValue())));
                    chargeTaxes.add(chargeTax);
                    interimAmount = interimAmount.add(chargeTax.amount().getValue());
                }
            }
            for (Tax tax : taxes) {
                if (tax.compound().isBooleanTrue()) {
                    InvoiceChargeTax chargeTax = EntityFactory.create(InvoiceChargeTax.class);
                    chargeTax.tax().set(tax);
                    chargeTax.amount().setValue(DomainUtil.roundMoney(interimAmount.multiply(tax.rate().getValue())));
                    chargeTaxes.add(chargeTax);
                    interimAmount = interimAmount.add(chargeTax.amount().getValue());
                }
            }
        }
        return chargeTaxes;
    }

    private static List<Tax> retrieveTaxesForProductItemType(ARCode productCode, Building building) {

        ProductTaxPolicy productTaxPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(building, ProductTaxPolicy.class);

        ProductTaxPolicyItem productTaxPolicyItem = null;
        {
            EntityQueryCriteria<ProductTaxPolicyItem> criteria = new EntityQueryCriteria<ProductTaxPolicyItem>(ProductTaxPolicyItem.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().policy(), productTaxPolicy));
            criteria.add(PropertyCriterion.eq(criteria.proto().productCode(), productCode));
            productTaxPolicyItem = Persistence.service().retrieve(criteria);
        }

        return productTaxPolicyItem == null ? new ArrayList<Tax>() : productTaxPolicyItem.taxes();
    }

    private static List<Tax> retrieveTaxesForAdjustmentReason(ARCode adjustmentCode, Building building) {
        LeaseAdjustmentPolicy leaseAdjustmentPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(building, LeaseAdjustmentPolicy.class);
        LeaseAdjustmentPolicyItem leaseAdjustmentPolicyItem = null;
        {
            EntityQueryCriteria<LeaseAdjustmentPolicyItem> criteria = new EntityQueryCriteria<LeaseAdjustmentPolicyItem>(LeaseAdjustmentPolicyItem.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().policy(), leaseAdjustmentPolicy));
            criteria.add(PropertyCriterion.eq(criteria.proto().code(), adjustmentCode));
            leaseAdjustmentPolicyItem = Persistence.service().retrieve(criteria);
        }

        return leaseAdjustmentPolicyItem == null ? new ArrayList<Tax>() : leaseAdjustmentPolicyItem.taxes();
    }

    public static void pennyFix(BigDecimal difference, IList<InvoiceLineItem> items) {
        if (items != null && difference.abs().compareTo(BigDecimal.ZERO) >= 0.01) {
            InvoiceDebit fatTaxDebit = null;
            InvoiceChargeTax fatChargeTax = null;
            for (InvoiceLineItem item : items) {
                if (item.isInstanceOf(InvoiceDebit.class)) {
                    InvoiceDebit debit = item.cast();
                    for (InvoiceChargeTax tax : debit.taxes()) {
                        if (fatChargeTax == null) {
                            fatChargeTax = tax;
                            fatTaxDebit = debit;
                        } else if (fatChargeTax.amount().getValue().compareTo(tax.amount().getValue()) < 0) {
                            fatChargeTax = tax;
                            fatTaxDebit = debit;
                        }
                    }
                }
            }

            fatChargeTax.amount().setValue(fatChargeTax.amount().getValue().add(difference));
            fatTaxDebit.taxTotal().setValue(fatTaxDebit.taxTotal().getValue().add(difference));
        }
    }
}
