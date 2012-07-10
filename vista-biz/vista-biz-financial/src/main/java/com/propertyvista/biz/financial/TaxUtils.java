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
import java.util.List;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceChargeTax;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.financial.tax.Tax;
import com.propertyvista.domain.policy.policies.LeaseAdjustmentPolicy;
import com.propertyvista.domain.policy.policies.ProductTaxPolicy;
import com.propertyvista.domain.policy.policies.domain.LeaseAdjustmentPolicyItem;
import com.propertyvista.domain.policy.policies.domain.ProductTaxPolicyItem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;

public class TaxUtils {

    public static BigDecimal calculateCombinedTax(List<InvoiceDebit> debits, Building building) {
        //TODO
        // 1. calc tax on the combined totals of the given list of debits; note following:
        //    a) composite tax (T1 + T2 + ...) - tax of each type is calculated on the total amount of charges the tax
        //       is applicable for;
        //    b) compound tax (T1 * (T2 * (...))) - for now only 1 level possible (see Tax.compound()); compound tax
        //       applied after all non-compound taxes;
        // 2. compare to the sum of taxes for each debit; balance the diff by adjusting the biggest taxTotal (pennyFix)
        // 3. return tax amount
        return null;
    }

    public static void calculateProductChargeTaxes(InvoiceProductCharge charge, Building building) {
        if (!charge.amount().isNull()) {
            charge.taxes().addAll(TaxUtils.calculateTaxes(charge.amount().getValue(), charge.chargeSubLineItem().billableItem().item().type(), building));
        }
        charge.taxTotal().setValue(BigDecimal.ZERO);
        for (InvoiceChargeTax chargeTax : charge.taxes()) {
            charge.taxTotal().setValue(charge.taxTotal().getValue().add(chargeTax.amount().getValue()));
        }
    }

    public static void calculateAccountChargeTax(InvoiceAccountCharge charge, Building building) {
        if (!charge.amount().isNull()) {
            charge.taxes().addAll(TaxUtils.calculateTaxes(charge.amount().getValue(), charge.adjustment().reason(), building));
        }
        charge.taxTotal().setValue(BigDecimal.ZERO);
        for (InvoiceChargeTax chargeTax : charge.taxes()) {
            charge.taxTotal().setValue(charge.taxTotal().getValue().add(chargeTax.amount().getValue()));
        }
    }

    //TODO Calculate taxes for specific day
    public static List<InvoiceChargeTax> calculateTaxes(final BigDecimal baseAmount, ProductItemType productItemType, Building building) {
        List<Tax> taxes = retrieveTaxesForProductItemType(productItemType, building);
        return calculateTaxes(baseAmount, taxes);
    }

    public static List<InvoiceChargeTax> calculateTaxes(BigDecimal baseAmount, LeaseAdjustmentReason reason, Building building) {
        List<Tax> taxes = retrieveTaxesForAdjustmentReason(reason, building);
        return calculateTaxes(baseAmount, taxes);
    }

    static List<InvoiceChargeTax> calculateTaxes(final BigDecimal baseAmount, List<Tax> taxes) {

        List<InvoiceChargeTax> chargeTaxes = new ArrayList<InvoiceChargeTax>();
        if (taxes != null) {
            BigDecimal interimAmount = baseAmount;
            for (Tax tax : taxes) {
                if (!tax.compound().getValue()) {
                    InvoiceChargeTax chargeTax = EntityFactory.create(InvoiceChargeTax.class);
                    chargeTax.tax().set(tax);
                    chargeTax.amount().setValue(MoneyUtils.round(baseAmount.multiply(tax.rate().getValue())));
                    chargeTaxes.add(chargeTax);
                    interimAmount = interimAmount.add(chargeTax.amount().getValue());
                }
            }
            for (Tax tax : taxes) {
                if (tax.compound().getValue()) {
                    InvoiceChargeTax chargeTax = EntityFactory.create(InvoiceChargeTax.class);
                    chargeTax.tax().set(tax);
                    chargeTax.amount().setValue(MoneyUtils.round(interimAmount.multiply(tax.rate().getValue())));
                    chargeTaxes.add(chargeTax);
                    interimAmount = interimAmount.add(chargeTax.amount().getValue());
                }
            }
        }
        return chargeTaxes;
    }

    private static List<Tax> retrieveTaxesForProductItemType(ProductItemType productItemType, Building building) {

        ProductTaxPolicy productTaxPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(building, ProductTaxPolicy.class);

        ProductTaxPolicyItem productTaxPolicyItem = null;
        {
            EntityQueryCriteria<ProductTaxPolicyItem> criteria = new EntityQueryCriteria<ProductTaxPolicyItem>(ProductTaxPolicyItem.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().policy(), productTaxPolicy));
            criteria.add(PropertyCriterion.eq(criteria.proto().productItemType(), productItemType));
            productTaxPolicyItem = Persistence.service().retrieve(criteria);
        }

        return productTaxPolicyItem == null ? new ArrayList<Tax>() : productTaxPolicyItem.taxes();
    }

    private static List<Tax> retrieveTaxesForAdjustmentReason(LeaseAdjustmentReason reason, Building building) {
        LeaseAdjustmentPolicy leaseAdjustmentPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(building, LeaseAdjustmentPolicy.class);
        LeaseAdjustmentPolicyItem leaseAdjustmentPolicyItem = null;
        {
            EntityQueryCriteria<LeaseAdjustmentPolicyItem> criteria = new EntityQueryCriteria<LeaseAdjustmentPolicyItem>(LeaseAdjustmentPolicyItem.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().policy(), leaseAdjustmentPolicy));
            criteria.add(PropertyCriterion.eq(criteria.proto().leaseAdjustmentReason(), reason));
            leaseAdjustmentPolicyItem = Persistence.service().retrieve(criteria);
        }

        return leaseAdjustmentPolicyItem == null ? new ArrayList<Tax>() : leaseAdjustmentPolicyItem.taxes();
    }

}
